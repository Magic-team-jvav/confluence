package org.confluence.mod.common.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class Snail extends SimpleCritter {
    private static final RawAnimation CRAWL = RawAnimation.begin().thenLoop("move.walk");
    private static final String ATTACHMENT_TAG = "SnailAttachment";
    private static final String CRAWL_DIRECTION_TAG = "SnailCrawlDirection";
    private static final EntityDataAccessor<Byte> DATA_ATTACHMENT = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_CRAWL_DIRECTION = SynchedEntityData.defineId(Snail.class, EntityDataSerializers.BYTE);
    private static final double CONTACT = 0.015;
    private final Profile profile;
    private Corner corner;
    private int turnCooldown;

    public Snail(EntityType<? extends Snail> type, Level level) {
        this(type, level, Profile.NORMAL);
    }

    public Snail(EntityType<? extends Snail> type, Level level, Profile profile) {
        super(type, level);
        this.profile = profile;
        setMaxUpStep(0.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Crawl", 0,
                state -> isNoGravity() && getDeltaMovement().lengthSqr() > 1.0E-8
                        ? state.setAndContinue(CRAWL) : PlayState.STOP));
    }

    @Override
    public boolean fireImmune() {
        return profile.fireImmune;
    }

    @Override
    public boolean isFullBright() {
        return profile.fullBright;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ATTACHMENT, (byte) Direction.UP.get3DDataValue());
        entityData.define(DATA_CRAWL_DIRECTION, (byte) Direction.NORTH.get3DDataValue());
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    public Direction getAttachmentFace() {
        return Direction.from3DDataValue(Byte.toUnsignedInt(entityData.get(DATA_ATTACHMENT)));
    }

    public Direction getCrawlDirection() {
        return Direction.from3DDataValue(Byte.toUnsignedInt(entityData.get(DATA_CRAWL_DIRECTION)));
    }

    private void setAttachmentFace(Direction face) {
        entityData.set(DATA_ATTACHMENT, (byte) face.get3DDataValue());
    }

    private void setCrawlDirection(Direction direction) {
        entityData.set(DATA_CRAWL_DIRECTION, (byte) direction.get3DDataValue());
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        navigation.stop();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    @Override
    public void travel(Vec3 input) {
        if (!isEffectiveAi()) {
            super.travel(input);
            return;
        }
        double speed = Math.max(0.005, getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.18);
        if (corner != null && followCorner(speed)) return;

        Direction face = getAttachmentFace();
        AABB support = findSupport(face, getBoundingBox());
        if (support == null) {
            face = recoverSupport();
            if (face == null) {
                detach();
                setAttachmentFace(Direction.UP);
                super.travel(input);
                return;
            }
            setAttachmentFace(face);
            support = findSupport(face, getBoundingBox());
        }

        // 已经触水时允许沿岸壁向上脱离，不能先解除贴壁再被重力拉回液面。
        if (liquidOverlap(getBoundingBox()) > 0 && face.getAxis().isVertical()) {
            Direction wall = findDryEscapeWall(speed);
            if (wall == null) {
                detach();
                super.travel(input);
                return;
            }
            face = wall;
            setAttachmentFace(face);
            setCrawlDirection(Direction.UP);
            support = findSupport(face, getBoundingBox());
        }
        // 避水/拐角受阻分支同样必须保持贴壁，不能只在实际移动时关闭重力。
        setNoGravity(true);
        fallDistance = 0;

        Direction crawl = getCrawlDirection();
        if (crawl.getAxis() == face.getAxis()) {
            crawl = face.getAxis().isVertical() ? Direction.NORTH : Direction.UP;
            setCrawlDirection(crawl);
        }
        if (face == Direction.UP && --turnCooldown <= 0) {
            setCrawlDirection(Direction.Plane.HORIZONTAL.getRandomDirection(random));
            crawl = getCrawlDirection();
            turnCooldown = 100 + random.nextInt(160);
        }
        Vec3 step = vector(crawl).scale(speed);
        AABB next = getBoundingBox().move(step);
        if (!canCrawlWithoutEnteringLiquid(getBoundingBox(), step)) {
            turnAwayFromLiquid(face, crawl, speed);
            return;
        }

        if (!level().noCollision(this, next)) {
            crawlMove(step);
            Direction obstacle = crawl.getOpposite();
            if (findSupport(obstacle, getBoundingBox()) != null) {
                setAttachmentFace(obstacle);
                setCrawlDirection(face);
            } else {
                setCrawlDirection(crawl.getOpposite());
            }
        } else if (findSupport(face, next) == null) {
            Corner candidate = makeCorner(support, face, crawl);
            Vec3 toEdge = candidate.edge.subtract(getBoundingBox().getCenter());
            AABB edgeBox = getBoundingBox().move(toEdge);
            if (!canCrawlWithoutEnteringLiquid(getBoundingBox(), toEdge)
                    || !canCrawlWithoutEnteringLiquid(edgeBox, candidate.exit.subtract(candidate.edge))) {
                turnAwayFromLiquid(face, crawl, speed);
                return;
            }
            corner = candidate;
            if (!followCorner(speed)) {
                setCrawlDirection(crawl.getOpposite());
                setDeltaMovement(Vec3.ZERO);
            }
        } else {
            crawlMove(step);
        }
        updateRotation();
    }

    /// 按真实液面高度计算相交体积；containsAnyLiquid 会把液体所在整格都视为液体。
    private double liquidOverlap(AABB box) {
        double volume = 0;
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(box.minX, box.minY, box.minZ),
                BlockPos.containing(box.maxX, box.maxY, box.maxZ))) {
            var fluid = level().getFluidState(pos);
            if (fluid.isEmpty()) continue;
            double height = Math.min(box.maxY, pos.getY() + fluid.getHeight(level(), pos)) - Math.max(box.minY, pos.getY());
            if (height <= 0) continue;
            double width = Math.min(box.maxX, pos.getX() + 1) - Math.max(box.minX, pos.getX());
            double depth = Math.min(box.maxZ, pos.getZ() + 1) - Math.max(box.minZ, pos.getZ());
            if (width > 0 && depth > 0) volume += width * height * depth;
        }
        return volume;
    }

    private boolean canCrawlWithoutEnteringLiquid(AABB box, Vec3 step) {
        double current = liquidOverlap(box);
        if (current > 0) {
            // 被推入液面后可以向外爬，但不能继续深入或在液面内横着游。
            return liquidOverlap(box.move(step)) < current - 1.0E-10;
        }
        return liquidOverlap(box.expandTowards(step)) <= 1.0E-10;
    }

    private Direction findDryEscapeWall(double speed) {
        Vec3 up = vector(Direction.UP).scale(speed);
        AABB box = getBoundingBox();
        if (!level().noCollision(this, box.move(up)) || !canCrawlWithoutEnteringLiquid(box, up))
            return null;
        for (Direction wall : Direction.Plane.HORIZONTAL) {
            if (findSupport(wall, box) != null && findSupport(wall, box.move(up)) != null)
                return wall;
        }
        return null;
    }

    private void turnAwayFromLiquid(Direction face, Direction crawl, double speed) {
        Direction[] alternatives = {crawl.getOpposite(), Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN};
        AABB box = getBoundingBox();
        for (Direction direction : alternatives) {
            if (direction == crawl || direction.getAxis() == face.getAxis()) continue;
            Vec3 step = vector(direction).scale(speed);
            AABB next = box.move(step);
            if (level().noCollision(this, next) && findSupport(face, next) != null && canCrawlWithoutEnteringLiquid(box, step)) {
                setCrawlDirection(direction);
                break;
            }
        }
        // 两侧都不可走时保持朝向，不要每 tick 翻转 180 度。
        turnCooldown = 100;
        setNoGravity(true);
        setDeltaMovement(Vec3.ZERO);
        updateRotation();
    }

    private Direction recoverSupport() {
        if (findSupport(Direction.UP, getBoundingBox()) != null) return Direction.UP;
        for (Direction direction : Direction.values()) {
            if (findSupport(direction, getBoundingBox()) != null) return direction;
        }
        return null;
    }

    private AABB contactProbe(Direction face, AABB box) {
        double inset = 0.001;
        return switch (face) {
            case UP ->
                    new AABB(box.minX + inset, box.minY - CONTACT, box.minZ + inset, box.maxX - inset, box.minY + inset, box.maxZ - inset);
            case DOWN ->
                    new AABB(box.minX + inset, box.maxY - inset, box.minZ + inset, box.maxX - inset, box.maxY + CONTACT, box.maxZ - inset);
            case EAST ->
                    new AABB(box.minX - CONTACT, box.minY + inset, box.minZ + inset, box.minX + inset, box.maxY - inset, box.maxZ - inset);
            case WEST ->
                    new AABB(box.maxX - inset, box.minY + inset, box.minZ + inset, box.maxX + CONTACT, box.maxY - inset, box.maxZ - inset);
            case SOUTH ->
                    new AABB(box.minX + inset, box.minY + inset, box.minZ - CONTACT, box.maxX - inset, box.maxY - inset, box.minZ + inset);
            case NORTH ->
                    new AABB(box.minX + inset, box.minY + inset, box.maxZ - inset, box.maxX - inset, box.maxY - inset, box.maxZ + CONTACT);
        };
    }

    private AABB findSupport(Direction face, AABB box) {
        AABB probe = contactProbe(face, box);
        AABB best = null;
        double furthest = -Double.MAX_VALUE;
        Direction crawl = getCrawlDirection();
        for (var shape : level().getBlockCollisions(this, probe)) {
            for (AABB part : shape.toAabbs()) {
                if (!part.intersects(probe)) continue;
                double edge = extreme(part, crawl);
                if (edge > furthest) {
                    best = part;
                    furthest = edge;
                }
            }
        }
        return best;
    }

    private Corner makeCorner(AABB support, Direction face, Direction crawl) {
        AABB box = getBoundingBox();
        Vec3 center = box.getCenter();
        Vec3 edge = withProjection(center, crawl, extreme(support, crawl) + halfExtent(box, crawl) + 0.002);
        edge = withProjection(edge, face, extreme(support, face) + halfExtent(box, face) + 0.002);
        Vec3 exit = withProjection(edge, face, extreme(support, face) - halfExtent(box, face) - 0.002);
        return new Corner(support, edge, exit, face, crawl);
    }

    private boolean followCorner(double speed) {
        Corner path = corner;
        if (path == null) return false;
        boolean anchorExists = false;
        for (var shape : level().getBlockCollisions(this, path.support.deflate(0.001))) {
            if (shape.toAabbs().stream().anyMatch(path.support::equals)) {
                anchorExists = true;
                break;
            }
        }
        if (!anchorExists || ++path.age > 600) {
            detach();
            return false;
        }
        Vec3 target = path.rounded ? path.exit : path.edge;
        Vec3 delta = target.subtract(getBoundingBox().getCenter());
        Vec3 step = delta.length() <= speed ? delta : delta.normalize().scale(speed);
        if (!level().noCollision(this, getBoundingBox().move(step)) || !canCrawlWithoutEnteringLiquid(getBoundingBox(), step)) {
            corner = null;
            turnAwayFromLiquid(getAttachmentFace(), getCrawlDirection(), speed);
            return true;
        }
        crawlMove(step);
        if (getBoundingBox().getCenter().distanceToSqr(target) < 1.0E-8) {
            if (!path.rounded) {
                path.rounded = true;
                setAttachmentFace(path.crawl);
                setCrawlDirection(path.face.getOpposite());
            } else {
                corner = null;
                turnCooldown = 100;
            }
        }
        updateRotation();
        return true;
    }

    private void crawlMove(Vec3 movement) {
        setNoGravity(true);
        fallDistance = 0;
        Vec3 before = position();
        move(MoverType.SELF, movement);
        setDeltaMovement(position().subtract(before));
        hasImpulse = true;
    }

    private void detach() {
        boolean wasAttached = isNoGravity() || corner != null;
        corner = null;
        setNoGravity(false);
        if (wasAttached) setDeltaMovement(Vec3.ZERO);
    }

    private void updateRotation() {
        Direction crawl = getCrawlDirection();
        Direction face = getAttachmentFace();
        Direction horizontal = crawl.getAxis().isHorizontal() ? crawl : face;
        float yaw = switch (horizontal) {
            case SOUTH -> 0;
            case WEST -> 90;
            case NORTH -> 180;
            case EAST -> -90;
            default -> getYRot();
        };
        setYRot(yaw);
        setYBodyRot(yaw);
        setYHeadRot(yaw);
        setXRot(crawl == Direction.UP ? -90 : crawl == Direction.DOWN ? 90 : 0);
    }

    private static Vec3 vector(Direction direction) {
        return Vec3.atLowerCornerOf(direction.getNormal());
    }

    private static double extreme(AABB box, Direction direction) {
        return direction.getAxisDirection() == Direction.AxisDirection.POSITIVE
                ? box.max(direction.getAxis()) : -box.min(direction.getAxis());
    }

    private static double halfExtent(AABB box, Direction direction) {
        return (box.max(direction.getAxis()) - box.min(direction.getAxis())) * 0.5;
    }

    private static Vec3 withProjection(Vec3 position, Direction axis, double projection) {
        Vec3 direction = vector(axis);
        return position.add(direction.scale(projection - position.dot(direction)));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte(ATTACHMENT_TAG, entityData.get(DATA_ATTACHMENT));
        tag.putByte(CRAWL_DIRECTION_TAG, entityData.get(DATA_CRAWL_DIRECTION));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(ATTACHMENT_TAG))
            entityData.set(DATA_ATTACHMENT, tag.getByte(ATTACHMENT_TAG));
        if (tag.contains(CRAWL_DIRECTION_TAG))
            entityData.set(DATA_CRAWL_DIRECTION, tag.getByte(CRAWL_DIRECTION_TAG));
        detach();
        turnCooldown = 100;
    }

    // 外棱过渡绑定真实碰撞形状：先越过棱边，再沿相邻面下降/上升；支撑消失立即结束。
    private static final class Corner {
        private final AABB support;
        private final Vec3 edge;
        private final Vec3 exit;
        private final Direction face;
        private final Direction crawl;
        private boolean rounded;
        private int age;

        private Corner(AABB support, Vec3 edge, Vec3 exit, Direction face, Direction crawl) {
            this.support = support;
            this.edge = edge;
            this.exit = exit;
            this.face = face;
            this.crawl = crawl;
        }
    }

    public enum Profile {
        NORMAL(false, false), GLOWING(false, true), MAGMA(true, true);
        private final boolean fireImmune;
        private final boolean fullBright;

        Profile(boolean fireImmune, boolean fullBright) {
            this.fireImmune = fireImmune;
            this.fullBright = fullBright;
        }
    }
}
