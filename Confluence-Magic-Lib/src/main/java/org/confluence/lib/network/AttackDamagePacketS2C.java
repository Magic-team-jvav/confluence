package org.confluence.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.client.DPSMeter;

public record AttackDamagePacketS2C(float amount) implements IPacketS2C {
    public static final Type<AttackDamagePacketS2C> TYPE = new Type<>(ConfluenceMagicLib.asResource("attack_damage"));
    public static final StreamCodec<ByteBuf, AttackDamagePacketS2C> STREAM_CODEC = ByteBufCodecs.FLOAT.map(AttackDamagePacketS2C::new, AttackDamagePacketS2C::amount);

    @Override
    public Type<AttackDamagePacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        DPSMeter.addDPS(amount, player.level().getGameTime());
    }

    public static void sendToClient(ServerPlayer player, float amount) {
        PacketDistributor.sendToPlayer(player, new AttackDamagePacketS2C(amount));
    }
}
