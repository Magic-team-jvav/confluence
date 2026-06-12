package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record SecretFlagSyncPacketS2C(long flag) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("secret_flag_sync");
    public static final PortStreamCodec<ByteBuf, SecretFlagSyncPacketS2C> STREAM_CODEC = PortByteBufCodecs.VAR_LONG.map(SecretFlagSyncPacketS2C::new, SecretFlagSyncPacketS2C::flag);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleSecretFlag(this);
    }

    public static void sendToAll(long flag) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new SecretFlagSyncPacketS2C(flag));
        }
    }

    public static void sendToClient(ServerPlayer player, long flag) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new SecretFlagSyncPacketS2C(flag));
    }
}
