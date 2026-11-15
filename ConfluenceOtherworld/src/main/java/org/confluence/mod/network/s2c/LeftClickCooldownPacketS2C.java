package org.confluence.mod.network.s2c;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.LeftClickCooldowns;

public record LeftClickCooldownPacketS2C(Item item, int ticks) implements IPacketS2C {
    public static final Type<LeftClickCooldownPacketS2C> TYPE = Confluence.createType("left_click_cooldown");
    public static final StreamCodec<RegistryFriendlyByteBuf, LeftClickCooldownPacketS2C> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(Registries.ITEM), LeftClickCooldownPacketS2C::item,
                    ByteBufCodecs.VAR_INT, LeftClickCooldownPacketS2C::ticks,
                    LeftClickCooldownPacketS2C::new
            );

    @Override
    public Type<LeftClickCooldownPacketS2C> type() {
        return TYPE;
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
        PacketDistributor.sendToPlayer(player, new LeftClickCooldownPacketS2C(item, ticks));
    }
}
