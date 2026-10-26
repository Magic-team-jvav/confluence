package org.confluence.mod.network.s2c;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.LeftClickCooldowns;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record LeftClickCooldownPacketS2C(Item item, int ticks) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("left_click_cooldown");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, LeftClickCooldownPacketS2C> STREAM_CODEC =
            PortStreamCodec.composite(
                    PortByteBufCodecs.registry(Registries.ITEM), LeftClickCooldownPacketS2C::item,
                    PortByteBufCodecs.VAR_INT, LeftClickCooldownPacketS2C::ticks,
                    LeftClickCooldownPacketS2C::new
            );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        LeftClickCooldowns cooldowns = LeftClickCooldowns.of(player);
        if (ticks > 0) {
            cooldowns.addCooldown(item, ticks);
        } else {
            cooldowns.removeCooldown(item);
        }
    }

    public static void send(ServerPlayer player, Item item, int ticks) {
        PortPacketDistributor.sendToPlayer(player, new LeftClickCooldownPacketS2C(item, ticks));
    }
}
