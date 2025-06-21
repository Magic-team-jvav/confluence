package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.common.component.AccessoriesComponent;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;

import java.util.function.Predicate;

public record VisibilityPacketS2C(byte mask) implements CustomPacketPayload {
    public static final byte ECHO = 0b0000010; // 回声
    public static final byte THE_CONSTANT_POST_EFFECT = 0b0000100; // 永恒领域后处理效果
    public static final byte SIGNAL = 0b0001000; // 信号线
    public static final Type<VisibilityPacketS2C> TYPE = new Type<>(Confluence.asResource("visibility"));
    public static final StreamCodec<ByteBuf, VisibilityPacketS2C> STREAM_CODEC = ByteBufCodecs.BYTE.map(VisibilityPacketS2C::new, VisibilityPacketS2C::mask);

    public VisibilityPacketS2C(byte checkMask, boolean visible) {
        this((byte) (checkMask | (visible ? 1 : 0)));
    }

    @Override
    public Type<VisibilityPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                ClientPacketHandler.handleVisibility(mask, (mask & 1) != 0);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendEcho(ServerPlayer player) {
        boolean visible = TCUtils.hasAccessoriesType(player, AccessoryItems.SPECTRE$GOGGLES) &&
                CuriosUtils.hasCurio(player, (Predicate<ItemStack>) itemStack -> {
                    AccessoriesComponent component = TCUtils.getAccessoriesComponent(itemStack);
                    if (component == null) return false;
                    return component.contains(AccessoryItems.SPECTRE$GOGGLES) && itemStack.getItem() instanceof IFunctionCouldEnable func && func.isEnabled(itemStack, null);
                });
        LibUtils.getOrCreatePersistedData(player).putBoolean("confluence:has_echo_visibility", visible);
        PacketDistributor.sendToPlayer(player, new VisibilityPacketS2C(ECHO, visible));
    }

    public static void sendTheConstantPostEffect(ServerPlayer serverPlayer) {
        boolean secretSeed = ModSecretSeeds.THE_CONSTANT.match(serverPlayer.server);
        boolean accessory = CuriosUtils.hasCurio(serverPlayer, AccessoryItems.RADIO_THING.get());
        PacketDistributor.sendToPlayer(serverPlayer, new VisibilityPacketS2C(THE_CONSTANT_POST_EFFECT, secretSeed ^ accessory));
    }

    public static void sendSignal(ServerPlayer serverPlayer, boolean visible) {
        PacketDistributor.sendToPlayer(serverPlayer, new VisibilityPacketS2C(SIGNAL, visible));
    }
}
