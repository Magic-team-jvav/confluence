package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summon.ClientSummonManager;
import org.confluence.mod.common.summon.SummonAnimation;
import org.confluence.mod.common.summon.SummonInstance;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonRenderPart;
import org.confluence.mod.common.summon.SummonVisualState;
import org.confluence.mod.common.summon.projectile.SummonProjectileInstance;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortVarInt;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// 同步一个玩家当前持有的召唤物姿态。
public record SummonSyncPacketS2C(UUID ownerId, List<Entry> entries) implements IPortPacket.S2C {
    private static final int MAX_ENTRIES = 512;
    private static final double TRACKING_RANGE = 160.0;
    public static final ResourceLocation ID = Confluence.asResource("summon_sync");
    public static final PortStreamCodec<ByteBuf, SummonSyncPacketS2C> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public SummonSyncPacketS2C decode(ByteBuf buffer) {
            UUID ownerId = new UUID(buffer.readLong(), buffer.readLong());
            int size = PortVarInt.read(buffer);
            if (size < 0 || size > MAX_ENTRIES) {
                throw new DecoderException("Summon entry count exceeds the protocol limit: " + size);
            }
            List<Entry> entries = new ArrayList<>(size);
            for (int index = 0; index < size; index++) {
                entries.add(Entry.decode(buffer));
            }
            return new SummonSyncPacketS2C(ownerId, entries);
        }

        @Override
        public void encode(ByteBuf buffer, SummonSyncPacketS2C packet) {
            if (packet.entries.size() > MAX_ENTRIES) {
                throw new EncoderException("Summon entry count exceeds the protocol limit: " + packet.entries.size());
            }
            buffer.writeLong(packet.ownerId.getMostSignificantBits());
            buffer.writeLong(packet.ownerId.getLeastSignificantBits());
            PortVarInt.write(buffer, packet.entries.size());
            packet.entries.forEach(entry -> entry.encode(buffer));
        }
    };

    public SummonSyncPacketS2C(ServerPlayer owner, List<SummonInstance> summons, List<SummonProjectileInstance> projectiles) {
        this(owner.getUUID(), createEntries(summons, projectiles));
    }

    private static List<Entry> createEntries(List<SummonInstance> summons, List<SummonProjectileInstance> projectiles) {
        List<SummonRenderPart> parts = new ArrayList<>(summons.size());
        for (SummonInstance summon : summons) summon.appendRenderParts(parts);
        List<Entry> entries = new ArrayList<>(parts.size() + projectiles.size());
        for (var part : parts) entries.add(Entry.from(part));
        for (SummonProjectileInstance projectile : projectiles)
            entries.add(Entry.from(projectile.renderPart()));
        return entries;
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void handle(IPortPacket.Context context) {
        Player player = context.player();
        if (player != null) context.enqueueWork(() -> work(player));
    }

    @Override
    public void work(Player player) {
        ClientSummonManager.accept(ownerId, entries);
    }

    public static void send(ServerPlayer owner, List<SummonInstance> summons, List<SummonProjectileInstance> projectiles) {
        SummonSyncPacketS2C packet = new SummonSyncPacketS2C(owner, summons, projectiles);
        Vec3 ownerPosition = owner.position();
        for (ServerPlayer viewer : owner.serverLevel().players()) {
            if (shouldSendTo(ownerPosition, packet.entries, viewer.position(), TRACKING_RANGE)) {
                Confluence.NETWORK_HANDLER.sendToPlayer(viewer, packet);
            }
        }
    }

    /// 判断观察者是否位于召唤者或任一召唤物可视部分的同步范围内。
    public static boolean shouldSendTo(Vec3 ownerPosition, List<Entry> entries, Vec3 viewerPosition, double radius) {
        double radiusSqr = radius * radius;
        if (ownerPosition.distanceToSqr(viewerPosition) <= radiusSqr) return true;
        for (Entry entry : entries) {
            if (entry.position.distanceToSqr(viewerPosition) <= radiusSqr) return true;
        }
        return false;
    }

    public record Entry(
            UUID id,
            ResourceLocation type,
            Vec3 position,
            float yaw,
            float pitch,
            float roll,
            boolean followingOwner,
            int order,
            SummonAnimation animation,
            int animationTicks,
            int animationDuration,
            float animationDegrees,
            float scale,
            float scaleY) {
        private static Entry from(org.confluence.mod.common.summon.SummonRenderPart part) {
            SummonPose pose = part.pose();
            SummonVisualState visual = part.visualState();
            return new Entry(part.id(), part.type(), pose.position(), pose.yaw(), pose.pitch(), pose.roll(),
                    visual.followingOwner(), part.order(), visual.animation(), visual.animationTicks(),
                    visual.animationDuration(), visual.animationDegrees(), visual.scale(), visual.scaleY());
        }

        private static Entry decode(ByteBuf buffer) {
            UUID id = new UUID(buffer.readLong(), buffer.readLong());
            ResourceLocation type = ResourceLocation.tryParse(PortByteBufCodecs.STRING_UTF8.decode(buffer));
            if (type == null) throw new DecoderException("Invalid summon type identifier");
            Vec3 position = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            float yaw = buffer.readFloat();
            float pitch = buffer.readFloat();
            float roll = buffer.readFloat();
            boolean followingOwner = buffer.readBoolean();
            int order = PortVarInt.read(buffer);
            int animationOrdinal = PortVarInt.read(buffer);
            SummonAnimation[] animations = SummonAnimation.values();
            if (animationOrdinal < 0 || animationOrdinal >= animations.length) {
                throw new DecoderException("Unknown summon animation state: " + animationOrdinal);
            }
            return new Entry(id, type, position, yaw, pitch, roll, followingOwner, order, animations[animationOrdinal], PortVarInt.read(buffer), PortVarInt.read(buffer), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }

        private void encode(ByteBuf buffer) {
            buffer.writeLong(id.getMostSignificantBits());
            buffer.writeLong(id.getLeastSignificantBits());
            PortByteBufCodecs.STRING_UTF8.encode(buffer, type.toString());
            buffer.writeDouble(position.x);
            buffer.writeDouble(position.y);
            buffer.writeDouble(position.z);
            buffer.writeFloat(yaw);
            buffer.writeFloat(pitch);
            buffer.writeFloat(roll);
            buffer.writeBoolean(followingOwner);
            PortVarInt.write(buffer, order);
            PortVarInt.write(buffer, animation.ordinal());
            PortVarInt.write(buffer, animationTicks);
            PortVarInt.write(buffer, animationDuration);
            buffer.writeFloat(animationDegrees);
            buffer.writeFloat(scale);
            buffer.writeFloat(scaleY);
        }
    }
}
