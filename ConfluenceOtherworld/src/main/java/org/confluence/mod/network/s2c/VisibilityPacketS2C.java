package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.item.IFunctionCouldEnable;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.common.util.PortTriState;

import java.util.function.Predicate;

public record VisibilityPacketS2C(byte mask) implements IPortPacket.S2C {
    public static final byte ECHO = 0b0000010; // 回声
    public static final byte THE_CONSTANT_POST_EFFECT = 0b0000100; // 永恒领域后处理效果
    public static final byte SIGNAL = 0b0001000; // 信号线
    public static final byte SUNGLASSES = 0b0010000; // 墨镜太阳
    public static final ResourceLocation ID = Confluence.asResource("visibility");
    public static final PortStreamCodec<ByteBuf, VisibilityPacketS2C> STREAM_CODEC = PortByteBufCodecs.BYTE
            .map(VisibilityPacketS2C::new, VisibilityPacketS2C::mask);

    public VisibilityPacketS2C(byte checkMask, boolean visible) {
        this((byte) (checkMask | (visible ? 1 : 0)));
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleVisibility(mask, (mask & 1) != 0);
    }

    public static void sendEcho(ServerPlayer player) {
        boolean visible = TCUtils.hasType(player, AccessoryItems.SPECTRE$GOGGLES) &&
                CuriosUtils.hasCurio(player, (Predicate<ItemStack>) itemStack -> {
                    if (itemStack.getItem() instanceof IFunctionCouldEnable func && func.isEnabled(itemStack)) {
                        PrimitiveValueComponent component = TCUtils.getAccessoriesComponent(itemStack);
                        if (component != null) {
                            return component.contains(AccessoryItems.SPECTRE$GOGGLES);
                        }
                    }
                    return false;
                });
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
        if (data.getBoolean("confluence:has_echo_visibility") != visible) {
            data.putBoolean("confluence:has_echo_visibility", visible);
            Confluence.NETWORK_HANDLER.sendToPlayer(player, new VisibilityPacketS2C(ECHO, visible));
        }
    }

    public static void sendTheConstantPostEffect(ServerPlayer player) {
        boolean secretSeed = ModSecretSeeds.THE_CONSTANT.match(player.server);
        boolean accessory = CuriosUtils.hasCurio(player, AccessoryItems.RADIO_THING.get());
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new VisibilityPacketS2C(THE_CONSTANT_POST_EFFECT, secretSeed ^ accessory));
    }

    public static void sendSignal(ServerPlayer player, boolean visible) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new VisibilityPacketS2C(SIGNAL, visible));
    }

    public static void sendSunglasses(ServerPlayer player, PortTriState normal, PortTriState extra) {
        boolean visible = (normal.isDefault() && player.getItemBySlot(EquipmentSlot.HEAD).is(VanityArmorItems.SUNGLASSES.get())) || normal.isTrue();
        if (!visible) {
            visible = (extra.isDefault() && ExtraInventory.of(player).getVanityArmor(ExtraInventory.VANITY_HEAD_INDEX, false).is(VanityArmorItems.SUNGLASSES.get())) || extra.isTrue();
        }
        if (visible &&
                player.getY() > OverworldUtils.getSurfaceY() &&
                !ModSecretSeeds.DONT_DIG_UP.match(player.server) &&
                !player.level().isRaining() &&
                LibDateUtils.isWithinDayTime(
                        LibDateUtils.getDayTime(5, 30),
                        LibDateUtils.getDayTime(18, 30),
                        player.level()
                )
        ) {
            AchievementUtils.awardAchievement(player, "on_fleek");
        }
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new VisibilityPacketS2C(SUNGLASSES, visible));
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }
}
