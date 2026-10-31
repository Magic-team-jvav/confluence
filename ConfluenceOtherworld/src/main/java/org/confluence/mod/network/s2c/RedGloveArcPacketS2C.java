package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.RedGloveVisualEvent;
import org.confluence.mod.common.item.mana.RedGlovePattern;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.network.IPacketS2C;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/** Authoritative clipped control polylines, sent in bounded batches without dropping targets. */
public record RedGloveArcPacketS2C(ResourceLocation dimension, int casterId, long tick, long seed, Vec3 origin,
                                  List<RedGlovePattern.Path> paths) implements IPacketS2C {
    public static final Type<RedGloveArcPacketS2C> TYPE = Confluence.createType("red_glove_arc");
    public static final int BATCH_PATHS = 64;
    public static final StreamCodec<RegistryFriendlyByteBuf, RedGloveArcPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override public RedGloveArcPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            int bytes = buffer.readUnsignedShort();
            if (bytes < 1 || bytes > 256) throw new IllegalArgumentException("Invalid dimension ID");
            byte[] utf = new byte[bytes]; buffer.readBytes(utf);
            ResourceLocation dimension = ResourceLocation.parse(new String(utf, java.nio.charset.StandardCharsets.UTF_8));
            int casterId = buffer.readInt();
            long tick = buffer.readLong(), seed = buffer.readLong();
            Vec3 origin = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            if (!finite(origin)) throw new IllegalArgumentException("Invalid arc origin");
            int count = buffer.readUnsignedByte();
            if (count > BATCH_PATHS) throw new IllegalArgumentException("Too many arc paths");
            List<RedGlovePattern.Path> paths = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int depth = buffer.readUnsignedByte(), points = buffer.readUnsignedByte();
                if (points < 2 || points > 96) throw new IllegalArgumentException("Invalid arc points");
                List<Vec3> line = new ArrayList<>();
                for (int j = 0; j < points; j++) {
                    Vec3 point = j == points - 1 ? new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
                            : origin.add(new Vec3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));
                    Vec3 relative = point.subtract(origin);
                    if (!finite(relative) || relative.lengthSqr() > 40 * 40) throw new IllegalArgumentException("Invalid arc point");
                    line.add(point);
                }
                paths.add(new RedGlovePattern.Path(depth, -1, -1, line));
            }
            return new RedGloveArcPacketS2C(dimension, casterId, tick, seed, origin, List.copyOf(paths));
        }
        @Override public void encode(RegistryFriendlyByteBuf buffer, RedGloveArcPacketS2C packet) {
            byte[] dimension = packet.dimension.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if (dimension.length > 256 || packet.paths.size() > BATCH_PATHS) throw new IllegalArgumentException("Arc batch too large");
            buffer.writeShort(dimension.length); buffer.writeBytes(dimension);
            buffer.writeInt(packet.casterId);
            buffer.writeLong(packet.tick); buffer.writeLong(packet.seed);
            buffer.writeDouble(packet.origin.x); buffer.writeDouble(packet.origin.y); buffer.writeDouble(packet.origin.z);
            buffer.writeByte(packet.paths.size());
            for (var path : packet.paths) {
                buffer.writeByte(path.depth()); buffer.writeByte(path.points().size());
                for (int i = 0; i < path.points().size(); i++) {
                    Vec3 point = path.points().get(i);
                    if (i == path.points().size() - 1) {
                        buffer.writeDouble(point.x); buffer.writeDouble(point.y); buffer.writeDouble(point.z); continue;
                    }
                    Vec3 relative = point.subtract(packet.origin);
                    buffer.writeFloat((float) relative.x); buffer.writeFloat((float) relative.y); buffer.writeFloat((float) relative.z);
                }
            }
        }
    };
    private static boolean finite(Vec3 v) { return Double.isFinite(v.x) && Double.isFinite(v.y) && Double.isFinite(v.z); }
    @Override public Type<RedGloveArcPacketS2C> type() { return TYPE; }
    @Override public void work(Player player) { NeoForge.EVENT_BUS.post(new RedGloveVisualEvent(this)); }
    public static void send(ServerPlayer owner, Vec3 origin, long seed, List<RedGlovePattern.Path> paths) {
        for (int start = 0; start < paths.size(); start += BATCH_PATHS) {
            var packet = new RedGloveArcPacketS2C(owner.level().dimension().location(), owner.getId(), owner.level().getGameTime(), seed,
                    origin, List.copyOf(paths.subList(start, Math.min(paths.size(), start + BATCH_PATHS))));
            // 96 around the source also reaches observers beside any endpoint of the 32-block cone.
            PacketDistributor.sendToPlayersNear(owner.serverLevel(), null, origin.x, origin.y, origin.z, 96, packet);
        }
    }
}
