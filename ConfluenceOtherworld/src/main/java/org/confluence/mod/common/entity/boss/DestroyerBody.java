package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.joml.Quaternionf;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/// 毁灭者身体的唯一状态管理者：维护轨迹、碰撞部件和整链快照，客户端整链共用插值时刻。
public final class DestroyerBody {
    /// 相邻体节的最大滚转角差，单位度；达到此值时强制收拢至阈值以内。
    private static final float MAX_ROLL_GAP = 20.0F;
    /// 小角差跟随及越过最高点后归正时，每 tick 消除的剩余角差比例。
    private static final float ROLL_FOLLOW = 0.18F;
    /// 最多保存的头部轨迹采样数，防止长期战斗时历史无限增长。
    private static final int MAX_TRAIL_SAMPLES = 2048;
    /// 两次记录头部轨迹的最小位移，单位格。
    private static final double MIN_TRAIL_DISTANCE = 0.15;
    /// 快照固定长度：头部绝对位置、角度和年龄，各体节相对坐标、滚转及开合状态。
    private static final int SNAPSHOT_BYTES = 3 * Double.BYTES + 3 * Float.BYTES + Integer.BYTES
            + (TheDestroyer.SEGMENT_COUNT - 1) * (4 * Float.BYTES + 1);
    /// 客户端延后两 tick 播放整链快照，吸收到包时间的短暂波动。
    private static final double CLIENT_BUFFER_TICKS = 2.0;
    /// 最多保留的整链快照数，不随战斗时间增长。
    private static final int CLIENT_SNAPSHOT_LIMIT = 12;
    /// 每个客户端 tick 修正的播放时钟误差比例，避免收到包时重置插值进度。
    private static final double CLIENT_CLOCK_CORRECTION = 0.1;
    private final TheDestroyer head;
    private final DestroyerPart[] parts = new DestroyerPart[TheDestroyer.SEGMENT_COUNT - 1];
    private final ArrayDeque<Vec3> trail = new ArrayDeque<>();
    private final boolean[] exposed = new boolean[TheDestroyer.SEGMENT_COUNT];
    private final boolean[] ascending = new boolean[TheDestroyer.SEGMENT_COUNT];
    private final ArrayDeque<ClientPose> clientPoses = new ArrayDeque<>();
    private PathNode[] previousClientPoses, currentClientPoses;
    private double clientPoseTime, playbackTime, lastRenderTime = Double.NaN;
    private float renderFraction;

    private record ClientPose(int tick, PathNode[] nodes) {}

    DestroyerBody(TheDestroyer head) {
        this.head = head;
        for (int i = 0; i < parts.length; i++) parts[i] = new DestroyerPart(head, i + 1);
    }

    public DestroyerPart[] getParts() {return parts;}

    public Entity getSection(int index) {return index == 0 ? head : parts[index - 1];}

    public Vec3 getPosition(int index) {return getSection(index).position();}

    public float getRoll(int index) {return index == 0 ? head.getRoll(1.0F) : parts[index - 1].getRoll();}

    void assignIds(int headId) {
        for (int i = 0; i < parts.length; i++) parts[i].setId(headId + i + 1);
    }

    void remove() {
        for (DestroyerPart part : parts) part.setRemoved(Entity.RemovalReason.DISCARDED);
    }

    /// 初次生成、读档定位和生成包到达时，先提供连续身体；收到快照后不再重置。
    void initializePosition() {
        if (!trail.isEmpty() || hasClientSnapshot()) return;
        Vec3 forward = head.getLookAngle();
        for (DestroyerPart part : parts)
            part.updatePose(head.position().subtract(forward.scale(part.getIndex() * TheDestroyer.SEGMENT_SPACING)),
                    forward, 0, false);
    }

    void initializeTrail() {
        if (!trail.isEmpty()) return;
        Vec3 backward = head.getLookAngle().normalize().scale(-TheDestroyer.SEGMENT_SPACING);
        for (int i = 0; i < TheDestroyer.SEGMENT_COUNT; i++)
            trail.addLast(head.position().add(backward.scale(i)));
    }

    void update() {update(true);}

    private void update(boolean effects) {
        initializeTrail();
        if (trail.getFirst().distanceToSqr(head.position()) >= MIN_TRAIL_DISTANCE * MIN_TRAIL_DISTANCE)
            trail.addFirst(head.position());
        while (trail.size() > MAX_TRAIL_SAMPLES) trail.removeLast();
        List<Vec3> path = new ArrayList<>(trail);
        int cursor = 0;
        double traversed = 0;
        for (DestroyerPart part : parts) {
            int index = part.getIndex();
            double distance = index * TheDestroyer.SEGMENT_SPACING;
            while (cursor + 1 < path.size() && traversed + path.get(cursor).distanceTo(path.get(cursor + 1)) < distance) {
                traversed += path.get(cursor).distanceTo(path.get(cursor + 1));
                cursor++;
            }
            Vec3 from = path.get(cursor), to = path.get(Math.min(cursor + 1, path.size() - 1));
            double length = from.distanceTo(to);
            Vec3 next = length < 0.0001 ? from : from.lerp(to, Mth.clamp((distance - traversed) / length, 0, 1));
            boolean outside = !head.isSolid(next);
            if (!exposed[index] && outside) ascending[index] = true;
            boolean ground = head.getMode() == TheDestroyer.Mode.GROUND;
            if (ground && outside && ascending[index] && next.y <= part.getY())
                ascending[index] = false;
            float roll = ground && outside && !ascending[index]
                    ? wrapRoll(part.getRoll() + Mth.wrapDegrees(-part.getRoll()) * ROLL_FOLLOW)
                    : followRoll(getRoll(index - 1), part.getRoll());
            boolean open = head.getMode() == TheDestroyer.Mode.SKY || ground && outside;
            Vec3 forward = getPosition(index - 1).subtract(next);
            part.updatePose(next, forward, roll, open);
            if (effects && open && !exposed[index] && head.level().hasChunkAt(BlockPos.containing(next))
                    && index % 2 != 0 && !part.isTail())
                head.playVentOpeningSound(next);
            exposed[index] = outside;
        }
        /// 留足身体末端后的一个插值节点，静止时不追加相同坐标。
        while (trail.size() > cursor + 3) trail.removeLast();
    }

    static float wrapRoll(float roll) {return Mth.wrapDegrees(roll);}

    static float followRoll(float previous, float current) {
        float gap = Mth.wrapDegrees(previous - current);
        float step = Math.abs(gap) >= MAX_ROLL_GAP
                ? gap - Math.copySign(MAX_ROLL_GAP - 0.01F, gap) : gap * ROLL_FOLLOW;
        return wrapRoll(current + step);
    }

    double distanceToSqr(Vec3 target, boolean horizontalOnly) {
        double nearest = Double.POSITIVE_INFINITY;
        for (int i = 0; i < TheDestroyer.SEGMENT_COUNT; i++) {
            Vec3 position = getPosition(i);
            double dx = position.x - target.x, dz = position.z - target.z;
            double dy = horizontalOnly ? 0 : position.y - target.y;
            nearest = Math.min(nearest, dx * dx + dy * dy + dz * dz);
        }
        return nearest;
    }

    /// 单份快照约 1.4 KiB，经头部同步数据发送；晚加入或重新追踪时仍能获得完整身体。
    CompoundTag createSnapshot() {
        ByteBuffer buffer = ByteBuffer.allocate(SNAPSHOT_BYTES);
        buffer.putDouble(head.getX()).putDouble(head.getY()).putDouble(head.getZ());
        buffer.putFloat(head.getYRot()).putFloat(head.getXRot()).putFloat(getRoll(0)).putInt(head.tickCount);
        for (DestroyerPart part : parts) {
            buffer.putFloat((float) (part.getX() - head.getX()));
            buffer.putFloat((float) (part.getY() - head.getY()));
            buffer.putFloat((float) (part.getZ() - head.getZ()));
            buffer.putFloat(part.getRoll()).put((byte) (part.isOpen() ? 1 : 0));
        }
        CompoundTag snapshot = new CompoundTag();
        snapshot.putByteArray("Pose", buffer.array());
        return snapshot;
    }

    void readSnapshot(CompoundTag snapshot) {
        byte[] bytes = snapshot.getByteArray("Pose");
        if (bytes.length != SNAPSHOT_BYTES) return;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Vec3 origin = new Vec3(buffer.getDouble(), buffer.getDouble(), buffer.getDouble());
        float yaw = buffer.getFloat(), pitch = buffer.getFloat(), roll = buffer.getFloat();
        int snapshotTick = buffer.getInt();
        if (!clientPoses.isEmpty() && snapshotTick <= clientPoses.getLast().tick()) return;
        head.tickCount = snapshotTick;
        PathNode[] nodes = new PathNode[TheDestroyer.SEGMENT_COUNT];
        nodes[0] = new PathNode(origin, yaw - 180, -pitch, roll);
        head.setPos(origin);
        head.setYRot(yaw);
        head.setXRot(pitch);
        for (DestroyerPart part : parts) {
            Vec3 position = origin.add(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
            roll = buffer.getFloat();
            part.updatePose(position, getPosition(part.getIndex() - 1).subtract(position), roll, buffer.get() != 0);
            nodes[part.getIndex()] = new PathNode(position, part.getYRot() - 180, -part.getXRot(), roll);
        }
        if (clientPoses.isEmpty()) {
            playbackTime = snapshotTick - CLIENT_BUFFER_TICKS;
            previousClientPoses = currentClientPoses = nodes;
        }
        clientPoses.addLast(new ClientPose(snapshotTick, nodes));
        while (clientPoses.size() > CLIENT_SNAPSHOT_LIMIT) clientPoses.removeFirst();
        clientPoseTime = head.level().getGameTime();
    }

    boolean hasClientSnapshot() {return !clientPoses.isEmpty();}

    private float interpolation(float partialTick) {
        double now = head.level().getGameTime() + partialTick;
        if (now == lastRenderTime) return renderFraction;
        double elapsed = Double.isNaN(lastRenderTime) ? 0 : Math.max(0, now - lastRenderTime);
        lastRenderTime = now;
        ClientPose newest = clientPoses.getLast();
        double target = newest.tick() - CLIENT_BUFFER_TICKS + Math.max(0, now - clientPoseTime);
        double correction = (target - playbackTime) * CLIENT_CLOCK_CORRECTION;
        playbackTime += elapsed * Mth.clamp(1.0 + correction, 0.5, 1.5);
        playbackTime = Mth.clamp(playbackTime, clientPoses.getFirst().tick(), newest.tick());
        ClientPose previous = clientPoses.getFirst();
        ClientPose next = previous;
        for (ClientPose pose : clientPoses) {
            next = pose;
            if (pose.tick() >= playbackTime) break;
            previous = pose;
        }
        previousClientPoses = previous.nodes();
        currentClientPoses = next.nodes();
        renderFraction = next.tick() == previous.tick() ? 1.0F
                : (float) Mth.clamp((playbackTime - previous.tick()) / (next.tick() - previous.tick()), 0.0, 1.0);
        return renderFraction;
    }

    public Vec3 getRenderPosition(int index, float partialTick) {
        if (!hasClientSnapshot()) return getPosition(index);
        float fraction = interpolation(partialTick);
        return previousClientPoses[index].pos().lerp(currentClientPoses[index].pos(), fraction);
    }

    public Quaternionf getRenderRotation(int index, float partialTick) {
        if (!hasClientSnapshot()) {
            Entity section = getSection(index);
            return new PathNode(section.position(), section.getYRot() - 180, -section.getXRot(), getRoll(index)).toQuaternion();
        }
        float fraction = interpolation(partialTick);
        return previousClientPoses[index].toQuaternion().slerp(currentClientPoses[index].toQuaternion(), fraction);
    }

    void save(CompoundTag tag) {
        ListTag history = new ListTag();
        for (Vec3 point : trail) {
            CompoundTag entry = new CompoundTag();
            entry.putDouble("X", point.x);
            entry.putDouble("Y", point.y);
            entry.putDouble("Z", point.z);
            history.add(entry);
        }
        tag.put("DestroyerTrail", history);
    }

    void load(CompoundTag tag) {
        trail.clear();
        ListTag history = tag.getList("DestroyerTrail", 10);
        for (int i = 0; i < Math.min(history.size(), MAX_TRAIL_SAMPLES); i++) {
            CompoundTag point = history.getCompound(i);
            trail.addLast(new Vec3(point.getDouble("X"), point.getDouble("Y"), point.getDouble("Z")));
        }
        update(false);
    }
}
