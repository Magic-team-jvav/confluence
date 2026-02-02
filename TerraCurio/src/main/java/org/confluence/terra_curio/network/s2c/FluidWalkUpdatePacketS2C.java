package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.util.TCUtils;

public record FluidWalkUpdatePacketS2C() implements IPacketS2C {
    public static final Type<FluidWalkUpdatePacketS2C> TYPE = new Type<>(TerraCurio.asResource("fluid_walk_update"));
    private static final FluidWalkUpdatePacketS2C INSTANCE = new FluidWalkUpdatePacketS2C();
    public static final StreamCodec<ByteBuf, FluidWalkUpdatePacketS2C> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<FluidWalkUpdatePacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        TCUtils.updateWalkableFluidStates(player);
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        TCUtils.updateWalkableFluidStates(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, INSTANCE);
    }
}
