package org.confluence.terra_curio.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.util.TCUtils;

public record PlayerSprintPacketC2S() implements IPacketC2S {
    public static final PlayerSprintPacketC2S INSTANCE = new PlayerSprintPacketC2S();
    public static final Type<PlayerSprintPacketC2S> TYPE = new Type<>(TerraCurio.asResource("player_sprint"));
    public static final StreamCodec<ByteBuf, PlayerSprintPacketC2S> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<PlayerSprintPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        TCUtils.applyCthulhuSprinting(true, player);
    }
}
