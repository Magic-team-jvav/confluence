package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.RepeaterHud;

public final class RepeaterShootingPayloadS2C implements IPacketS2C {
    public static final RepeaterShootingPayloadS2C INSTANCE = new RepeaterShootingPayloadS2C();
    public static final CustomPacketPayload.Type<RepeaterShootingPayloadS2C> TYPE = new CustomPacketPayload.Type<>(Confluence.asResource("repeater_shooting_payload"));
    public static final StreamCodec<ByteBuf, RepeaterShootingPayloadS2C> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RepeaterShootingPayloadS2C() {}

    @Override
    public Type<RepeaterShootingPayloadS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        RepeaterHud.handle();
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, INSTANCE);
    }
}
