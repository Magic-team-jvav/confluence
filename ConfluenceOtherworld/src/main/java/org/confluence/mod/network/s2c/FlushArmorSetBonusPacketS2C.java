package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.network.IPacket;

public record FlushArmorSetBonusPacketS2C(int playerId) implements IPacketS2C {
    public static final Type<FlushArmorSetBonusPacketS2C> TYPE = IPacket.createType("flush_armor_set_bonus");
    public static final StreamCodec<ByteBuf, FlushArmorSetBonusPacketS2C> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(FlushArmorSetBonusPacketS2C::new, FlushArmorSetBonusPacketS2C::playerId);

    @Override
    public Type<FlushArmorSetBonusPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleFlushArmorSetBonus(player, playerId);
    }

    public static void sendToPlayersTrackingTarget(ServerPlayer target) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new FlushArmorSetBonusPacketS2C(target.getId()));
    }

    public static void sendToClient(ServerPlayer sendTo, ServerPlayer target) {
        PacketDistributor.sendToPlayer(sendTo, new FlushArmorSetBonusPacketS2C(target.getId()));
    }
}
