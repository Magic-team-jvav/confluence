package org.confluence.mod.network.s2c;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.client.handler.StarPhaseHandler;
import org.confluence.mod.common.data.saved.StarPhase;
import org.confluence.mod.network.IPacket;

import static org.confluence.mod.common.data.saved.ConfluenceData.STAR_PHASES_SIZE;

public record StarPhasesPacketS2C(Either<Int2ObjectMap<StarPhase>, Int2ObjectMap.Entry<StarPhase>> starPhases) implements IPacketS2C {
    public static final Type<StarPhasesPacketS2C> TYPE = IPacket.createType("star_phases");
    public static final StreamCodec<ByteBuf, StarPhasesPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        public StarPhasesPacketS2C decode(ByteBuf buffer) {
            boolean isLeft = buffer.readBoolean();
            if (isLeft) {
                int length = buffer.readInt();
                Int2ObjectMap<StarPhase> map = new Int2ObjectArrayMap<>();
                for (int i = 0; i < length; i++) {
                    map.put(i, new StarPhase(buffer));
                }
                return new StarPhasesPacketS2C(Either.left(map));
            }
            return new StarPhasesPacketS2C(Either.right(new AbstractInt2ObjectMap.BasicEntry<>(buffer.readInt(), new StarPhase(buffer))));
        }

        @Override
        public void encode(ByteBuf buffer, StarPhasesPacketS2C value) {
            value.starPhases.ifLeft(map -> {
                buffer.writeBoolean(true);
                buffer.writeInt(STAR_PHASES_SIZE);
                for (int i = 0; i < STAR_PHASES_SIZE; i++) {
                    map.getOrDefault(i, StarPhase.DEFAULT).writeTo(buffer);
                }
            }).ifRight(entry -> {
                buffer.writeBoolean(false);
                buffer.writeInt(entry.getIntKey());
                entry.getValue().writeTo(buffer);
            });
        }
    };

    @Override
    public Type<StarPhasesPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        StarPhaseHandler.handleStarPhases(starPhases);
    }

    public static void sendToAll(int index, int timeOffset, float radius, float angle) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new StarPhasesPacketS2C(Either.right(new AbstractInt2ObjectMap.BasicEntry<>(index, new StarPhase(timeOffset, radius, angle)))));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, Int2ObjectMap<StarPhase> starPhases) {
        PacketDistributor.sendToPlayer(serverPlayer, new StarPhasesPacketS2C(Either.left(starPhases)));
    }
}
