package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

/// 通用的「从天而降的追踪坠星」。
///
/// 星泣累积释放与星乐爆开的坠星共用它：从高处落下，并在水平方向上追踪最近的敌人。
/// 星泣的坠星会关闭追踪，并可用 `spawnDelay` 让多颗坠星按间隔依次落下。
public class HomingFallingStarProjectile extends AbstractManaProjectile {
    /// 星怒的坠星参数：目标上方 20 格生成、以 2.0 的初速俯冲（见 `AboveFallenGeneration`）。
    public static final double STAR_FURY_FALL_HEIGHT = 20.0;
    public static final double STAR_FURY_FALL_SPEED = 2.0;
    /// 落点预判的最大刻数：与星怒 `AboveFallenGeneration` 的 `predict` 同量级。
    private static final double FALL_LEAD_TICKS = 10.0;
    private static final double HOMING_SPEED = STAR_FURY_FALL_SPEED;
    private static final double HOMING_RANGE = 24.0;
    private static final int MAX_AGE = 200;
    /// 生成延迟同步给客户端：等待中的坠星要隐藏、也不能让客户端自己往下掉。
    private static final EntityDataAccessor<Integer> DATA_SPAWN_DELAY = SynchedEntityData.defineId(HomingFallingStarProjectile.class, EntityDataSerializers.INT);
    /// 发散阶段同步给客户端：星乐的坠星要先直线散开一段距离，再开始追踪。
    private static final EntityDataAccessor<Integer> DATA_DIVERGE_TICKS = SynchedEntityData.defineId(HomingFallingStarProjectile.class, EntityDataSerializers.INT);

    private boolean homing = true;
    private int spawnDelay;
    /// 星乐的发散阶段：>0 时保持出生速度直线飞行，不追踪也不吃重力。
    private int divergeTicks;
    /// 星泣的坠星：等待期间跟随这个目标，落下时再对准它（落下之后仍然不追踪）。
    private @Nullable LivingEntity fallTarget;
    private double fallOffsetX;
    private double fallOffsetZ;
    /// 是否已经按目标预判过一次弹道；预判只做一次，保证「不追踪」。
    private boolean aimed;
    /// 命中时是否清零目标的受击无敌帧：星泣每 0.5 秒一颗，每颗都必须结算伤害。
    private boolean ignoreInvulnerability;

    public HomingFallingStarProjectile(EntityType<HomingFallingStarProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public HomingFallingStarProjectile(LivingEntity living) {
        this(ModEntities.HOMING_FALLING_STAR.get(), living.level());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SPAWN_DELAY, 0);
        builder.define(DATA_DIVERGE_TICKS, 0);
    }

    /// 在目标上方生成一颗坠星：按星怒的坠星参数，从 `baseY + 20` 格高处、以 2.0 的初速垂直落下。
    ///
    /// `baseY` 传目标脚部高度即可；高处被方块占住时会向下找第一个空位，避免坠星一出场就埋在方块里。
    public static HomingFallingStarProjectile spawnAbove(Level level, @Nullable LivingEntity owner, double x, double baseY, double z) {
        HomingFallingStarProjectile star = new HomingFallingStarProjectile(ModEntities.HOMING_FALLING_STAR.get(), level);
        star.setOwner(owner);
        star.setPos(x, fallSpawnY(level, x, baseY, z), z);
        star.setDeltaMovement(0.0, -STAR_FURY_FALL_SPEED, 0.0);
        return star;
    }

    /// 星乐：从大坠星处沿 `direction` 弹出，先直线散开 `divergeTicks` 刻，再转入追踪。
    public static HomingFallingStarProjectile spawnBurst(Level level, @Nullable LivingEntity owner, Vec3 origin, Vec3 direction, double speed, int divergeTicks) {
        HomingFallingStarProjectile star = new HomingFallingStarProjectile(ModEntities.HOMING_FALLING_STAR.get(), level);
        star.setOwner(owner);
        star.setPos(origin.x, origin.y, origin.z);
        star.setDeltaMovement(direction.normalize().scale(speed));
        star.setDivergeTicks(divergeTicks);
        return star;
    }

    /// 星怒式落点高度：`baseY + 20` 格；若那里是实心方块，向下找第一个空位（最低保留 `baseY + 1`）。
    public static double fallSpawnY(Level level, double x, double baseY, double z) {
        double y = baseY + STAR_FURY_FALL_HEIGHT;
        double floor = baseY + 1.0;
        while (y > floor && !level.isEmptyBlock(BlockPos.containing(x, y, z))) {
            y -= 1.0;
        }
        return Math.max(floor, y);
    }

    /// 关闭 / 开启水平追踪。
    public void setHoming(boolean homing) {
        this.homing = homing;
    }

    public boolean isHoming() {
        return homing;
    }

    /// 延迟多少 tick 后才开始下落；>0 时停在上方等待（渲染也会暂时隐藏）。
    public void setSpawnDelay(int spawnDelay) {
        this.spawnDelay = Math.max(0, spawnDelay);
        entityData.set(DATA_SPAWN_DELAY, this.spawnDelay);
    }

    public int getSpawnDelay() {
        return entityData.get(DATA_SPAWN_DELAY);
    }

    /// 星乐的发散刻数：这段时间里坠星沿出生速度直线飞行，之后才进入追踪。
    public void setDivergeTicks(int divergeTicks) {
        this.divergeTicks = Math.max(0, divergeTicks);
        entityData.set(DATA_DIVERGE_TICKS, this.divergeTicks);
    }

    public int getDivergeTicks() {
        return entityData.get(DATA_DIVERGE_TICKS);
    }

    /// 星泣：让等待中的坠星一直悬浮在目标头顶（`offsetX` / `offsetZ` 是出生时摇出的水平偏移）。
    public void setFallTarget(@Nullable LivingEntity target, double offsetX, double offsetZ) {
        this.fallTarget = target;
        this.fallOffsetX = offsetX;
        this.fallOffsetZ = offsetZ;
    }

    /// 命中时是否清零目标的受击无敌帧。
    public void setIgnoreInvulnerability(boolean ignoreInvulnerability) {
        this.ignoreInvulnerability = ignoreInvulnerability;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        /// 延迟只由服务端推进，客户端读同步值（否则等待中的星会在客户端自己往下掉）。
        int delay = level().isClientSide ? getSpawnDelay() : spawnDelay;
        if (delay > 0) {
            if (!level().isClientSide) {
                /// 等待期间跟着目标走：星泣的多颗坠星是隔 0.5 秒依次落下的，
                /// 这段时间目标会走位、被箭击退，出生点不刷新的话后几颗就砸在空地上了。
                keepAboveTarget();
                --spawnDelay;
                entityData.set(DATA_SPAWN_DELAY, spawnDelay);
                if (spawnDelay <= 0) {
                    aimAtTarget();
                }
            }
            return;
        }
        /// 星乐的发散阶段：保持出生方向直线飞出，不追踪、也不施加额外重力；结束后才转入追踪。
        int diverging = level().isClientSide ? getDivergeTicks() : divergeTicks;
        if (diverging > 0) {
            if (!level().isClientSide) {
                --divergeTicks;
                entityData.set(DATA_DIVERGE_TICKS, divergeTicks);
            }
            doSimpleMove();
            doAgeCheck(MAX_AGE);
            return;
        }
        /// 第一颗（延迟 0）也要在真正开始下落前对准一次。
        aimAtTarget();
        if (homing) {
            LivingEntity target = findNearest();
            if (target != null) {
                Vec3 direction = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(position());
                if (direction.lengthSqr() > 1.0E-4) {
                    Vec3 desired = direction.normalize().scale(HOMING_SPEED);
                    setDeltaMovement(getDeltaMovement().scale(0.35).add(desired.scale(0.65)));
                }
            } else {
                setDeltaMovement(getDeltaMovement().add(0.0, -0.04, 0.0));
            }
        } else {
            /// 不追踪时只保持下落，不再横向修正弹道。
            setDeltaMovement(getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        doSimpleMove();
        doAgeCheck(MAX_AGE);
    }

    /// 等待期间把落点刷新到目标头顶。
    private void keepAboveTarget() {
        if (fallTarget == null || !fallTarget.isAlive()) return;
        double x = fallTarget.getX() + fallOffsetX;
        double z = fallTarget.getZ() + fallOffsetZ;
        setPos(x, fallSpawnY(level(), x, fallTarget.getY(), z), z);
    }

    /// 落下瞬间按目标当前位置与速度预判一次落点，只给一次初速，之后不再修正（保持「不追踪」）。
    private void aimAtTarget() {
        if (aimed || fallTarget == null || !fallTarget.isAlive()) return;
        aimed = true;
        Vec3 center = fallTarget.getBoundingBox().getCenter();
        double distance = position().distanceTo(center);
        double lead = Math.min(FALL_LEAD_TICKS, distance / STAR_FURY_FALL_SPEED);
        Vec3 aim = center.add(fallTarget.getDeltaMovement().scale(lead));
        Vec3 direction = aim.subtract(position());
        setDeltaMovement(direction.lengthSqr() < 1.0E-4
                ? new Vec3(0.0, -STAR_FURY_FALL_SPEED, 0.0)
                : direction.normalize().scale(STAR_FURY_FALL_SPEED));
    }

    private @Nullable LivingEntity findNearest() {
        LivingEntity nearest = null;
        double best = HOMING_RANGE * HOMING_RANGE;
        for (LivingEntity candidate : level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(HOMING_RANGE))) {
            if (candidate == getOwner() || !candidate.isAlive() || candidate.isSpectator()) continue;
            if (!LibEntityUtils.canHit(getOwner(), candidate)) continue;
            double distance = candidate.distanceToSqr(this);
            if (distance < best) {
                best = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    @Override
    protected void doHitCheck() {
        /// 等待与发散阶段都不做碰撞判定：星乐一次爆开有十几颗，在室内很容易刚出手就撞在墙顶；
        /// 等它们真正开始追踪后再正常结算。
        if (getSpawnDelay() > 0 || getDivergeTicks() > 0) return;
        super.doHitCheck();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!level().isClientSide) discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (!doPenetrateCheck(entity)) return;
        /// 星泣的坠星是「每 0.5 秒一颗」的独立结算；箭矢命中留下的无敌帧会把第一颗直接吞掉
        /// （`LivingEntity.hurt` 在 `invulnerableTime > 10` 且伤害不高于 `lastHurt` 时返回 false），
        /// 因此这一族坠星命中前先清零无敌帧，与极昼、暮晓等范围伤害的处理保持一致。
        if (ignoreInvulnerability && entity instanceof LivingEntity living) {
            living.invulnerableTime = 0;
        }
        doHurtAndKnockback(entity, 0.3, 0.2);
        doDiscardInMaxPenetrate(1);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Homing", homing);
        compound.putInt("SpawnDelay", spawnDelay);
        compound.putInt("DivergeTicks", divergeTicks);
        compound.putBoolean("IgnoreInvulnerability", ignoreInvulnerability);
        compound.putDouble("FallOffsetX", fallOffsetX);
        compound.putDouble("FallOffsetZ", fallOffsetZ);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.homing = !compound.contains("Homing") || compound.getBoolean("Homing");
        this.spawnDelay = Math.max(0, compound.getInt("SpawnDelay"));
        entityData.set(DATA_SPAWN_DELAY, spawnDelay);
        this.divergeTicks = Math.max(0, compound.getInt("DivergeTicks"));
        entityData.set(DATA_DIVERGE_TICKS, divergeTicks);
        this.ignoreInvulnerability = compound.getBoolean("IgnoreInvulnerability");
        this.fallOffsetX = compound.getDouble("FallOffsetX");
        this.fallOffsetZ = compound.getDouble("FallOffsetZ");
    }
}
