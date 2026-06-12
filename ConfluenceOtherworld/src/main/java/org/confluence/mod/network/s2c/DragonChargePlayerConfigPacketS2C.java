package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record DragonChargePlayerConfigPacketS2C(boolean enabled) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("dragon_charge_player_config");
    public static final PortStreamCodec<ByteBuf, DragonChargePlayerConfigPacketS2C> STREAM_CODEC = PortByteBufCodecs.BOOL
            .map(DragonChargePlayerConfigPacketS2C::new, DragonChargePlayerConfigPacketS2C::enabled);

    public DragonChargePlayerConfigPacketS2C() {
        this(CommonConfigs.isDragonChargePlayer());
    }

    @Override
    public void work(Player player) {
        CommonConfigs.handleDragonChargePlayer(enabled);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public static void sendToPlayer(ServerPlayer player) {
        if (LibEntityUtils.isSingleplayerOwner(player)) return;
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new DragonChargePlayerConfigPacketS2C());
    }

    public static void sendToAll() {
        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ClientboundCustomPayloadPacket payload = new ClientboundCustomPayloadPacket(new DragonChargePlayerConfigPacketS2C());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (LibEntityUtils.isSingleplayerOwner(player)) continue;
            player.connection.send(payload);
        }
    }
}
