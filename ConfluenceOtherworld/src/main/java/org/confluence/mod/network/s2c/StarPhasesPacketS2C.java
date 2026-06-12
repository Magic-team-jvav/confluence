package org.confluence.mod.network.s2c;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.StarPhaseHandler;
import org.confluence.mod.common.data.saved.StarPhase;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import static org.confluence.mod.common.data.saved.ConfluenceData.STAR_PHASES_SIZE;

public record StarPhasesPacketS2C(
        Either<Int2ObjectMap<StarPhase>, Int2ObjectMap.Entry<StarPhase>> starPhases) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("star_phases");
    public static final PortStreamCodec<ByteBuf, StarPhasesPacketS2C> STREAM_CODEC = new StreamCodec<>() {
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
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        StarPhaseHandler.handleStarPhases(starPhases);
    }

    public static void sendToAll(int index, int timeOffset, float radius, float angle) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new StarPhasesPacketS2C(Either.right(new AbstractInt2ObjectMap.BasicEntry<>(index, new StarPhase(timeOffset, radius, angle)))));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, Int2ObjectMap<StarPhase> starPhases) {
        Confluence.NETWORK_HANDLER.sendToPlayer(serverPlayer, new StarPhasesPacketS2C(Either.left(starPhases)));
    }
}
