package org.confluence.mod.common.entity.monster;

import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.EnemyWalkNodeEvaluator;
import org.confluence.mod.common.entity.projectile.SpiderWebSpit;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;

public class ClimbingSpider extends BaseWarriorMonster {
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(ClimbingSpider.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Direction> ATTACHMENT_FACE = SynchedEntityData.defineId(ClimbingSpider.class, EntityDataSerializers.DIRECTION);

    private final Kind kind;
    private int spitTicks;

    public ClimbingSpider(EntityType<? extends ClimbingSpider> type, Level level, Kind kind) {
        // 三种蜘蛛共用 SpiderSetModel 的程序步态，不请求资源中不存在的 walk/idle 动画。
        super(type, level, 0.0, LandAnimationProfile.NONE, LandSoundProfile.ROUTINE, 1.0, true);
        this.kind = kind;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CLIMBING, false);
        entityData.define(ATTACHMENT_FACE, Direction.UP);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level) {
            @Override
            protected PathFinder createPathFinder(int maxVisitedNodes) {
                nodeEvaluator = new EnemyWalkNodeEvaluator();
                return new PathFinder(nodeEvaluator, maxVisitedNodes);
            }
        };
    }

    @Override
    public boolean onClimbable() {
        return entityData.get(CLIMBING);
    }

    /// 支撑面的外法线；UP 表示正常地面姿态。由服务端同步，不依赖客户端碰撞标志。
    public Direction getAttachmentFace() {
        return entityData.get(ATTACHMENT_FACE);
    }

    private Direction findAttachmentFace() {
        if (!onClimbable()) return Direction.UP;
        Direction previous = getAttachmentFace();
        if (previous.getAxis().isHorizontal() && touchesWall(previous.getOpposite()))
            return previous;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (touchesWall(direction)) return direction.getOpposite();
        }
        return Direction.UP;
    }

    private boolean touchesWall(Direction direction) {
        AABB box = getBoundingBox();
        double reach = 0.125;
        // 只检测碰撞箱侧面，避免脚下地板被误判为墙；兼容非完整方块的碰撞形状。
        AABB probe = switch (direction) {
            case NORTH ->
                    new AABB(box.minX + 0.01, box.minY + 0.01, box.minZ - reach, box.maxX - 0.01, box.maxY - 0.01, box.minZ);
            case SOUTH ->
                    new AABB(box.minX + 0.01, box.minY + 0.01, box.maxZ, box.maxX - 0.01, box.maxY - 0.01, box.maxZ + reach);
            case WEST ->
                    new AABB(box.minX - reach, box.minY + 0.01, box.minZ + 0.01, box.minX, box.maxY - 0.01, box.maxZ - 0.01);
            case EAST ->
                    new AABB(box.maxX, box.minY + 0.01, box.minZ + 0.01, box.maxX + reach, box.maxY - 0.01, box.maxZ - 0.01);
            default -> throw new IllegalArgumentException("Expected horizontal wall direction");
        };
        return level().getBlockCollisions(this, probe).iterator().hasNext();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        entityData.set(CLIMBING, horizontalCollision);
        entityData.set(ATTACHMENT_FACE, findAttachmentFace());
        setSpecialState(CombatState.CLIMBING, kind == Kind.BLACK_RECLUSE && onClimbable());
        LivingEntity target = getTarget();
        if (kind == Kind.WALL || isNoAi() || !isAlive() || target == null || !target.isAlive() || !canAttack(target) || hasEffect(LibEffects.CONFUSED.get())
                || !LibUtils.isAtLeastExpert(level(), blockPosition()) || !getSensing().hasLineOfSight(target))
            return;
        if (++spitTicks < stateParameters(CombatState.SPITTING).attackInterval()) return;
        spitTicks = 0;
        SpiderWebSpit spit = ModEntities.SPIDER_WEB_SPIT.get().create(level());
        if (spit == null) return;
        Vec3 offset = target.getEyePosition().subtract(getEyePosition());
        Vec3 aim = offset.add(0.0, offset.horizontalDistance() * 0.12, 0.0).normalize().scale(0.8);
        spit.configure(this, getEyePosition(), aim, LibUtils.isMaster(level(), blockPosition()) ? 54.0F : 36.0F, 80);
        if (level().addFreshEntity(spit)) playSound(SoundEvents.SPIDER_AMBIENT, 0.8F, 1.4F);
        else spit.discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide) spitTicks -= 7 + random.nextInt(14);
        return damaged;
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 multiplier) {
        if (!state.is(Blocks.COBWEB)) super.makeStuckInBlock(state, multiplier);
    }

    public enum CombatState {CLIMBING, SPITTING}

    public enum Kind {
        WALL, BLACK_RECLUSE, JUNGLE
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.BLACK_RECLUSE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BLACK_RECLUSE_DEATH.get();
    }

}
