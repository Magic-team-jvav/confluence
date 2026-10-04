package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;

public record DragonChargePlayerConfigPacketS2C(boolean enabled) implements IPacketS2C {
    public static final Type<DragonChargePlayerConfigPacketS2C> TYPE = Confluence.createType("dragon_charge_player_config");
    public static final StreamCodec<ByteBuf, DragonChargePlayerConfigPacketS2C> STREAM_CODEC = ByteBufCodecs.BOOL
            .map(DragonChargePlayerConfigPacketS2C::new, DragonChargePlayerConfigPacketS2C::enabled);

    public DragonChargePlayerConfigPacketS2C() {
        this(CommonConfigs.isDragonChargePlayer());
    }

    @Override
    public void work(Player player) {
        CommonConfigs.handleDragonChargePlayer(enabled);
    }

    @Override
    public Type<DragonChargePlayerConfigPacketS2C> type() {
        return TYPE;
    }

    public static void sendToPlayer(ServerPlayer player) {
        if (LibEntityUtils.isSingleplayerOwner(player)) return;
        PacketDistributor.sendToPlayer(player, new DragonChargePlayerConfigPacketS2C());
    }

    public static void sendToAll() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ClientboundCustomPayloadPacket payload = new ClientboundCustomPayloadPacket(new DragonChargePlayerConfigPacketS2C());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (LibEntityUtils.isSingleplayerOwner(player)) continue;
            player.connection.send(payload);
        }
    }
}
