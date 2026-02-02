package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.handler.PlayerClimbHandler;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public record PlayerClimbPacketS2C(byte climberAmount) implements IPacketS2C {
    public static final Type<PlayerClimbPacketS2C> TYPE = new Type<>(TerraCurio.asResource("player_climb"));
    public static final StreamCodec<ByteBuf, PlayerClimbPacketS2C> STREAM_CODEC = ByteBufCodecs.BYTE.map(PlayerClimbPacketS2C::new, PlayerClimbPacketS2C::climberAmount);

    @Override
    public Type<PlayerClimbPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        PlayerClimbHandler.handlePacket(climberAmount);
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new PlayerClimbPacketS2C(TCUtils.getValue(serverPlayer, TCItems.WALL$CLIMB)));
    }
}
