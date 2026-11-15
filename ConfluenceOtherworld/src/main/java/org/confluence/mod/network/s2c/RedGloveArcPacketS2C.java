package org.confluence.mod.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RedGloveVfx;
import org.confluence.mod.common.item.mana.RedGlovePattern;
import org.joml.Vector3f;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.ArrayList;
import java.util.List;

/// Authoritative clipped control polylines, sent in bounded batches without dropping targets.
public record RedGloveArcPacketS2C(
        int casterId,
        long tick,
        long seed,
        Vector3f origin,
        List<RedGlovePattern.Path> paths
) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("red_glove_arc");
    public static final int BATCH_PATHS = 64;
    public static final PortStreamCodec<FriendlyByteBuf, RedGloveArcPacketS2C> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public RedGloveArcPacketS2C decode(FriendlyByteBuf buffer) {
            int casterId = buffer.readVarInt();
            long tick = buffer.readVarLong(), seed = buffer.readVarLong();
            Vec3 origin = new Vec3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            if (!finite(origin)) throw new IllegalArgumentException("Invalid arc origin");
            int count = buffer.readByte();
            if (count > BATCH_PATHS) throw new IllegalArgumentException("Too many arc paths");
            List<RedGlovePattern.Path> paths = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int depth = buffer.readByte(), parent = buffer.readVarInt(), root = buffer.readVarInt();
                if (parent < -1 || root < -1 || root > 95)
                    throw new IllegalArgumentException("Invalid arc topology");
                int points = buffer.readByte();
                if (points < 2 || points > 96)
                    throw new IllegalArgumentException("Invalid arc points");
                List<Vec3> line = new ArrayList<>();
                for (int j = 0; j < points; j++) {
                    Vec3 point = j == points - 1 ? new Vec3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat())
                            : origin.add(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
                    Vec3 relative = point.subtract(origin);
                    if (!finite(relative) || relative.lengthSqr() > 40 * 40)
                        throw new IllegalArgumentException("Invalid arc point");
                    line.add(point);
                }
                paths.add(new RedGlovePattern.Path(depth, parent, root, line));
            }
            return new RedGloveArcPacketS2C(casterId, tick, seed, origin.toVector3f(), List.copyOf(paths));
        }

        @Override
        public void encode(FriendlyByteBuf buffer, RedGloveArcPacketS2C packet) {
            buffer.writeVarInt(packet.casterId);
            buffer.writeVarLong(packet.tick);
            buffer.writeVarLong(packet.seed);
            buffer.writeFloat(packet.origin.x);
            buffer.writeFloat(packet.origin.y);
            buffer.writeFloat(packet.origin.z);
            buffer.writeByte(packet.paths.size());
            for (var path : packet.paths) {
                buffer.writeByte(path.depth());
                buffer.writeVarInt(path.parent());
                buffer.writeVarInt(path.rootIndex());
                buffer.writeByte(path.points().size());
                for (int i = 0; i < path.points().size(); i++) {
                    Vec3 point = path.points().get(i);
                    if (i == path.points().size() - 1) {
                        buffer.writeFloat((float) point.x);
                        buffer.writeFloat((float) point.y);
                        buffer.writeFloat((float) point.z);
                        continue;
                    }
                    Vec3 relative = point.subtract(new Vec3(packet.origin));
                    buffer.writeFloat((float) relative.x);
                    buffer.writeFloat((float) relative.y);
                    buffer.writeFloat((float) relative.z);
                }
            }
        }
    };

    private static boolean finite(Vec3 v) {return Double.isFinite(v.x) && Double.isFinite(v.y) && Double.isFinite(v.z);}

    @Override
    public ResourceLocation identifier() {return ID;}

    @Override
    public void work(Player player) {
        RedGloveVfx.receive(player, this);
    }

    public static void send(ServerPlayer owner, Vec3 origin, long seed, List<RedGlovePattern.Path> paths) {
        for (int start = 0; start < paths.size(); start += BATCH_PATHS) {
            var packet = new RedGloveArcPacketS2C(owner.getId(), owner.level().getGameTime(), seed,
                    origin.toVector3f(), List.copyOf(paths.subList(start, Math.min(paths.size(), start + BATCH_PATHS))));
            // 96 around the source also reaches observers beside any endpoint of the 32-block cone.
            PortPacketDistributor.sendToPlayersNear(owner.level().dimension(), null, origin.x, origin.y, origin.z, 96, packet);
        }
    }
}
