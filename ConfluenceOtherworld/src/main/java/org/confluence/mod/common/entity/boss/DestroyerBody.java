package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.joml.Quaternionf;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/// 毁灭者身体的唯一状态管理者：维护轨迹、碰撞部件和整链快照，客户端整链共用插值时刻。
public final class DestroyerBody {
    // 相邻体节的最大滚转角差，单位度；达到此值时强制收拢至阈值以内。
    private static final float MAX_ROLL_GAP = 20.0F;
    // 小角差跟随及越过最高点后归正时，每 tick 消除的剩余角差比例。
    private static final float ROLL_FOLLOW = 0.18F;
    // 最多保存的头部轨迹采样数，防止长期战斗时历史无限增长。
    private static final int MAX_TRAIL_SAMPLES = 2048;
    // 两次记录头部轨迹的最小位移，单位格。
    private static final double MIN_TRAIL_DISTANCE = 0.15;
    // 活体朝向可由相邻体节的位置还原，快照只需额外传递滚转和开合状态。
    private static final int SNAPSHOT_BYTES = 3 * Double.BYTES + 3 * Float.BYTES + Integer.BYTES
            + (TheDestroyer.SEGMENT_COUNT - 1) * (4 * Float.BYTES + 1);
    // 尸体炸断后相邻体节可能消失，因此每节单独传递偏航和俯仰。
    private static final int CORPSE_SNAPSHOT_BYTES = SNAPSHOT_BYTES + (TheDestroyer.SEGMENT_COUNT - 1) * 2 * Float.BYTES;
    // 客户端延后两 tick 播放整链快照，吸收到包时间的短暂波动。
    private static final double CLIENT_BUFFER_TICKS = 2.0;
    // 最多保留的整链快照数，不随战斗时间增长。
    private static final int CLIENT_SNAPSHOT_LIMIT = 12;
    // 每个客户端 tick 修正的播放时钟误差比例，避免收到包时重置插值进度。
    private static final double CLIENT_CLOCK_CORRECTION = 0.1;
    // 触地后保留部分水平动量，让体节沿地面滑行后停下。
    private static final double DEATH_GROUND_DAMPING = 0.75;
    private final TheDestroyer head;
    private final DestroyerPart[] parts = new DestroyerPart[TheDestroyer.SEGMENT_COUNT - 1];
    private final ArrayDeque<Vec3> trail = new ArrayDeque<>();
    private final boolean[] exposed = new boolean[TheDestroyer.SEGMENT_COUNT];
    private final boolean[] ascending = new boolean[TheDestroyer.SEGMENT_COUNT];

    private Vec3[] deathVelocity;
    private boolean[] pinned;

    private final ArrayDeque<ClientPose> clientPoses = new ArrayDeque<>();
    private PathNode[] previousClientPoses, currentClientPoses;
    private double clientPoseTime, playbackTime, lastRenderTime = Double.NaN;
    private final ByteBuffer snapshotBuffer = ByteBuffer.allocate(CORPSE_SNAPSHOT_BYTES);
    private CompoundTag lastSnapshot;
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
            if (effects && ground && open && !exposed[index] && head.level().hasChunkAt(BlockPos.containing(next))
                    && index % 2 != 0 && !part.isTail())
                head.playVentOpeningSound(next);
            exposed[index] = outside;
        }
        // 留足身体末端后的一个插值节点，静止时不追加相同坐标。
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

    // 入土体节成为锚点，空中体节保留各自最后一次位移并受相邻体节牵引。
    void tickDeathChain(int count) {
        if (deathVelocity == null) {
            boolean uninitialized = trail.isEmpty();
            if (uninitialized) {
                initializePosition();
                initializeTrail();
            }
            deathVelocity = new Vec3[parts.length + 1];
            pinned = new boolean[deathVelocity.length];
            deathVelocity[0] = head.getDeltaMovement();
            for (DestroyerPart part : parts)
                deathVelocity[part.getIndex()] = uninitialized ? Vec3.ZERO
                        : part.position().subtract(part.getContactSweepStart());
            for (int i = 0; i < pinned.length; i++)
                pinned[i] = head.level().getBlockCollisions(head, deathBox(i)).iterator().hasNext();
        }
        Vec3[] next = new Vec3[count];
        for (int i = 0; i < count; i++)
            next[i] = pinned[i] || head.isSectionExploded(i) ? getPosition(i) : getPosition(i).add(deathVelocity[i].scale(0.98)).add(0, -0.08, 0);
        for (int pass = 0; pass < 5; pass++) {
            for (int i = 1; i < count; i++) {
                if (head.isSectionExploded(i) || head.isSectionExploded(i - 1)) continue;
                Vec3 delta = next[i].subtract(next[i - 1]);
                double length = delta.length();
                if (length < 1.0E-6) continue;
                Vec3 correction = delta.scale((length - TheDestroyer.SEGMENT_SPACING) / length);
                if (!pinned[i])
                    next[i] = next[i].subtract(correction.scale(pinned[i - 1] ? 1 : 0.5));
                if (!pinned[i - 1])
                    next[i - 1] = next[i - 1].add(correction.scale(pinned[i] ? 1 : 0.5));
            }
        }
        for (int i = 0; i < count; i++) {
            if (head.isSectionExploded(i)) continue;
            if (pinned[i]) {
                deathVelocity[i] = Vec3.ZERO;
                continue;
            }
            Vec3 old = getPosition(i);
            Vec3 motion = next[i].subtract(old);
            Vec3 allowed = Entity.collideBoundingBox(head, motion, deathBox(i), head.level(), List.of());
            next[i] = old.add(allowed);
            // 碰撞只消除受阻方向的速度，死亡起始时已经埋入方块的体节才固定。
            boolean onGround = motion.y < 0.0 && allowed.y > motion.y;
            double damping = onGround ? DEATH_GROUND_DAMPING : 1.0;
            deathVelocity[i] = new Vec3(
                    motion.x == allowed.x ? allowed.x * damping : 0.0,
                    motion.y == allowed.y ? allowed.y : 0.0,
                    motion.z == allowed.z ? allowed.z * damping : 0.0);
        }

        // 所有体节完成碰撞后，再按实际落点计算朝向。
        for (int i = 0; i < count; i++) {
            if (head.isSectionExploded(i)) continue;
            Vec3 forward = getSection(i).getLookAngle();
            if (i > 0 && !head.isSectionExploded(i - 1)) forward = next[i - 1].subtract(next[i]);
            else if (i + 1 < count && !head.isSectionExploded(i + 1))
                forward = next[i].subtract(next[i + 1]);
            if (i == 0) {
                head.setPos(next[i]);
                head.faceCombatDirection(forward, 180, 180);
            } else parts[i - 1].updatePose(next[i], forward, getRoll(i), false);
        }
    }

    private AABB deathBox(int index) {
        Vec3 pos = getPosition(index);
        double half = TheDestroyer.BODY_WIDTH / 2.0;
        return new AABB(pos.x - half, pos.y, pos.z - half, pos.x + half, pos.y + TheDestroyer.BODY_HEIGHT, pos.z + half);
    }

    // 通过头部同步完整姿态，让中途加入的客户端也能重建整条身体。
    CompoundTag createSnapshot() {
        ByteBuffer buffer = snapshotBuffer.clear();
        boolean corpse = !head.isAlive();
        buffer.putDouble(head.getX()).putDouble(head.getY()).putDouble(head.getZ());
        buffer.putFloat(head.getYRot()).putFloat(head.getXRot()).putFloat(getRoll(0)).putInt(head.tickCount);
        for (DestroyerPart part : parts) {
            buffer.putFloat((float) (part.getX() - head.getX()));
            buffer.putFloat((float) (part.getY() - head.getY()));
            buffer.putFloat((float) (part.getZ() - head.getZ()));
            if (corpse) buffer.putFloat(part.getYRot()).putFloat(part.getXRot());
            buffer.putFloat(part.getRoll()).put((byte) (part.isOpen() ? 1 : 0));
        }
        byte[] bytes = buffer.array();
        if (lastSnapshot != null) {
            byte[] old = lastSnapshot.getByteArray("Pose");
            int clock = 3 * Double.BYTES + 3 * Float.BYTES;
            if (old.length == buffer.position() && java.util.Arrays.equals(bytes, 0, clock, old, 0, clock)
                    && java.util.Arrays.equals(bytes, clock + 4, buffer.position(), old, clock + 4, old.length))
                return lastSnapshot;
        }
        CompoundTag snapshot = new CompoundTag();
        snapshot.putByteArray("Pose", java.util.Arrays.copyOf(bytes, buffer.position()));
        lastSnapshot = snapshot;
        return snapshot;
    }

    void readSnapshot(CompoundTag snapshot) {
        byte[] bytes = snapshot.getByteArray("Pose");
        boolean corpse = bytes.length == CORPSE_SNAPSHOT_BYTES;
        if (!corpse && bytes.length != SNAPSHOT_BYTES) return;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Vec3 origin = new Vec3(buffer.getDouble(), buffer.getDouble(), buffer.getDouble());
        float yaw = buffer.getFloat(), pitch = buffer.getFloat(), roll = buffer.getFloat();
        int snapshotTick = buffer.getInt();
        if (!clientPoses.isEmpty() && snapshotTick <= clientPoses.getLast().tick()) return;

        PathNode[] nodes = new PathNode[TheDestroyer.SEGMENT_COUNT];
        nodes[0] = new PathNode(origin, yaw - 180, -pitch, roll);
        head.setPos(origin);
        head.setYRot(yaw);
        head.setXRot(pitch);
        for (DestroyerPart part : parts) {
            Vec3 position = origin.add(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
            Vec3 forward = getPosition(part.getIndex() - 1).subtract(position);
            if (corpse) {
                float partYaw = buffer.getFloat(), partPitch = buffer.getFloat();
                forward = Vec3.directionFromRotation(partPitch, partYaw);
            }
            roll = buffer.getFloat();
            part.updatePose(position, forward, roll, buffer.get() != 0);
            nodes[part.getIndex()] = new PathNode(position, part.getYRot() - 180, -part.getXRot(), roll);
        }
        if (!clientPoses.isEmpty() && snapshotTick - clientPoses.getLast().tick() > CLIENT_BUFFER_TICKS + 1) {
            ClientPose last = clientPoses.getLast();
            clientPoses.clear();
            clientPoses.addLast(new ClientPose(snapshotTick - 1, last.nodes()));
            playbackTime = snapshotTick - 1;
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
        if (deathVelocity == null) return;
        ListTag corpse = new ListTag();
        for (int i = 0; i < deathVelocity.length; i++) {
            Entity section = getSection(i);
            CompoundTag node = new CompoundTag();
            node.putDouble("X", section.getX());
            node.putDouble("Y", section.getY());
            node.putDouble("Z", section.getZ());
            node.putFloat("Yaw", section.getYRot());
            node.putFloat("Pitch", section.getXRot());
            node.putFloat("Roll", getRoll(i));
            node.putDouble("VX", deathVelocity[i].x);
            node.putDouble("VY", deathVelocity[i].y);
            node.putDouble("VZ", deathVelocity[i].z);
            node.putBoolean("Pinned", pinned[i]);
            corpse.add(node);
        }
        tag.put("DestroyerCorpse", corpse);
    }

    void load(CompoundTag tag) {
        trail.clear();
        ListTag history = tag.getList("DestroyerTrail", 10);
        for (int i = 0; i < Math.min(history.size(), MAX_TRAIL_SAMPLES); i++) {
            CompoundTag point = history.getCompound(i);
            trail.addLast(new Vec3(point.getDouble("X"), point.getDouble("Y"), point.getDouble("Z")));
        }
        // 召唤指令可能在读完 NBT 后才设置坐标，等实体定位后再初始化空轨迹。
        if (!trail.isEmpty()) update(false);
        ListTag corpse = tag.getList("DestroyerCorpse", 10);
        if (corpse.size() != parts.length + 1 || head.isAlive()) return;
        deathVelocity = new Vec3[corpse.size()];
        pinned = new boolean[corpse.size()];
        for (int i = 0; i < corpse.size(); i++) {
            CompoundTag node = corpse.getCompound(i);
            Vec3 pos = new Vec3(node.getDouble("X"), node.getDouble("Y"), node.getDouble("Z"));
            float yaw = node.getFloat("Yaw"), pitch = node.getFloat("Pitch");
            if (i == 0) head.setPos(pos);
            else
                parts[i - 1].updatePose(pos, Vec3.directionFromRotation(pitch, yaw), node.getFloat("Roll"), false);
            getSection(i).setYRot(yaw);
            getSection(i).setXRot(pitch);
            deathVelocity[i] = new Vec3(node.getDouble("VX"), node.getDouble("VY"), node.getDouble("VZ"));
            pinned[i] = node.getBoolean("Pinned");
        }
    }
}
