package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;

public record SecretFlagSyncPacketS2C(long flag) implements IPacketS2C {
    public static final Type<SecretFlagSyncPacketS2C> TYPE = Confluence.createType("secret_flag_sync");
    public static final StreamCodec<ByteBuf, SecretFlagSyncPacketS2C> STREAM_CODEC = ByteBufCodecs.VAR_LONG.map(SecretFlagSyncPacketS2C::new, SecretFlagSyncPacketS2C::flag);

    @Override
    public Type<SecretFlagSyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleSecretFlag(this);
    }

    public static void sendToAll(long flag) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new SecretFlagSyncPacketS2C(flag));
        }
    }

    public static void sendToClient(ServerPlayer player, long flag) {
        PacketDistributor.sendToPlayer(player, new SecretFlagSyncPacketS2C(flag));
    }
}
