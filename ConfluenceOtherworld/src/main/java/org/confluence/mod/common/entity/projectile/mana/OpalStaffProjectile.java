package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;

/// 欧泊法杖的洪流弹幕。
///
/// 出手时受重力下坠，砸到地面后贴着地面向前奔涌一段时间，然后折返奔向施法者；
/// 折返途中伤害翻倍。
///
/// 判定箱是「沿前进方向长 3、垂直方向宽 6、高 1」的**旋转**箱体：它跟着弹幕的水平朝向转，
/// 并且是真正的命中体积（不是只用来筛选候选实体的搜索框）。为了不干扰方块碰撞，
/// 实体自身的 `bb` 保持原始尺寸，宽箱只在 `doHitCheck` 里做一次范围命中判定。
public class OpalStaffProjectile extends AbstractManaProjectile {
    /// 洪流弹幕的粒子。
    public static final ResourceLocation TORRENT_PARTICLE = Confluence.asResource("opal_torrent");
    private static final double GRAVITY = 0.09;
    private static final double SURGE_SPEED = 0.55;
    /// 落地后贴地奔涌的时长，数值越大折返越晚。
    private static final int SURGE_TICKS = 45;
    private static final double RETURN_SPEED = 0.85;
    /// 延后的总存在时间。
    private static final int MAX_AGE = 320;
    /// 判定箱：沿前进方向的半长（总长 3）。
    public static final double HITBOX_HALF_LENGTH = 1.5;
    /// 判定箱：垂直前进方向的半宽（总宽 6）。
    public static final double HITBOX_HALF_WIDTH = 3.0;
    /// 判定箱：半高（总高 1）。
    public static final double HITBOX_HALF_HEIGHT = 0.5;
    /// 折返判定：水平距离小于该值即视为已回到施法者的 XZ 位置。
    private static final double RETURN_HORIZONTAL_SQR = 2.25;

    private Phase phase = Phase.FALLING;
    private int surgeTicks;
    private Vec3 surgeDirection = Vec3.ZERO;
    /// 当前水平朝向（单位向量），宽判定箱按它旋转。
    private Vec3 heading = new Vec3(0.0, 0.0, 1.0);

    public OpalStaffProjectile(EntityType<OpalStaffProjectile> entityType, Level level) {
        super(entityType, level);
        // 大范围洪流使用 ParticleStorm 自定义粒子，而不是通用水流粒子。
        withParticle(TORRENT_PARTICLE);
    }

    public OpalStaffProjectile(LivingEntity living) {
        this(ModEntities.OPAL_STAFF_PROJECTILE.get(), living.level());
    }

    public Phase getPhase() {
        return phase;
    }

    /// 当前判定箱的水平朝向，供渲染/调试使用。
    public Vec3 getHeading() {
        return heading;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Vec3 motion = getDeltaMovement();
        switch (phase) {
            case FALLING -> setDeltaMovement(motion.x, motion.y - GRAVITY, motion.z);
            case SURGE -> {
                surgeTicks++;
                setDeltaMovement(surgeDirection.scale(SURGE_SPEED));
                if (surgeTicks > SURGE_TICKS) {
                    this.phase = Phase.RETURNING;
                    // 洪流折返时是新一轮伤害，必须清掉去重记录，否则打过的怪在折返路上再也吃不到伤害。
                    clearPenetrateSet();
                }
            }
            case RETURNING -> {
                LivingEntity owner = getLivingOwner();
                if (owner == null) {
                    discard();
                    return;
                }
                // 只追 XZ，保持自身高度水平飞回。
                Vec3 direction = new Vec3(owner.getX() - getX(), 0.0, owner.getZ() - getZ());
                if (direction.horizontalDistanceSqr() < RETURN_HORIZONTAL_SQR) {
                    discard();
                    return;
                }
                setDeltaMovement(direction.normalize().scale(RETURN_SPEED));
            }
        }
        doSimpleMove();
        updateHeading();
        doAgeCheck(MAX_AGE);
    }

    /// 用水平运动方向刷新判定箱朝向，保证宽箱跟着发射/飞行方向旋转。
    private void updateHeading() {
        Vec3 motion = getDeltaMovement();
        Vec3 horizontal = new Vec3(motion.x, 0.0, motion.z);
        if (horizontal.lengthSqr() > 1.0E-6) {
            this.heading = horizontal.normalize();
        } else if (surgeDirection.lengthSqr() > 1.0E-6) {
            this.heading = surgeDirection;
        }
    }

    /// 方块命中沿用原版射线（落地后切到奔涌），实体命中改用旋转宽箱。
    @Override
    protected void doHitCheck() {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitResult);
        }
        if (level().isClientSide) return;
        for (Entity entity : level().getEntities(this, searchBox(), this::canHitEntity)) {
            if (hitsOrientedBox(entity)) {
                onHitEntity(new EntityHitResult(entity));
            }
        }
    }

    /// 候选实体搜索范围（覆盖整个旋转宽箱）。
    private AABB searchBox() {
        double horizontal = Math.max(HITBOX_HALF_LENGTH, HITBOX_HALF_WIDTH) + 1.0;
        double centerY = getY() + getBbHeight() * 0.5;
        return new AABB(
                getX() - horizontal, centerY - HITBOX_HALF_HEIGHT - 1.0, getZ() - horizontal,
                getX() + horizontal, centerY + HITBOX_HALF_HEIGHT + 1.0, getZ() + horizontal
        );
    }

    /// 旋转箱（OBB）与实体 AABB 的相交判定。
    ///
    /// 只取箱体自己的三条轴做分离轴测试：箱体的 Y 轴与世界 Y 轴一致，另两轴只在水平面内旋转，
    /// 这三条轴已能覆盖实际会漏判的方向，代价远低于完整的 15 轴 SAT。
    private boolean hitsOrientedBox(Entity entity) {
        AABB box = entity.getBoundingBox();
        double centerY = getY() + getBbHeight() * 0.5;
        Vec3 delta = new Vec3(box.getCenter().x - getX(), box.getCenter().y - centerY, box.getCenter().z - getZ());

        Vec3 forward = heading;
        Vec3 right = new Vec3(-forward.z, 0.0, forward.x);
        double halfX = (box.maxX - box.minX) * 0.5;
        double halfY = (box.maxY - box.minY) * 0.5;
        double halfZ = (box.maxZ - box.minZ) * 0.5;

        // 沿前进方向：箱体半长 + 实体在该轴上的投影半径
        double alongRadius = Math.abs(forward.x) * halfX + Math.abs(forward.z) * halfZ;
        if (Math.abs(delta.x * forward.x + delta.z * forward.z) > HITBOX_HALF_LENGTH + alongRadius) return false;

        // 垂直前进方向：箱体半宽 + 实体投影半径
        double acrossRadius = Math.abs(right.x) * halfX + Math.abs(right.z) * halfZ;
        if (Math.abs(delta.x * right.x + delta.z * right.z) > HITBOX_HALF_WIDTH + acrossRadius) return false;

        // 垂直方向：箱体半高 + 实体半高
        return Math.abs(delta.y) <= HITBOX_HALF_HEIGHT + halfY;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (phase != Phase.FALLING) return;
        Vec3 motion = getDeltaMovement();
        Vec3 horizontal = new Vec3(motion.x, 0.0, motion.z);
        if (horizontal.lengthSqr() < 1.0E-4) {
            horizontal = new Vec3(0.0, 0.0, 1.0);
        }
        this.surgeDirection = horizontal.normalize();
        this.phase = Phase.SURGE;
        this.surgeTicks = 0;
        setDeltaMovement(surgeDirection.scale(SURGE_SPEED));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0.5, 0.2);
            doDiscardInMaxPenetrate(12);
        }
    }

    /// 折返途中伤害翻倍。
    @Override
    public float getCalculatedDamage() {
        float base = super.getCalculatedDamage();
        return phase == Phase.RETURNING ? base * 2.0F : base;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Phase", phase.name());
        compound.putInt("SurgeTicks", surgeTicks);
        compound.putDouble("SurgeX", surgeDirection.x);
        compound.putDouble("SurgeZ", surgeDirection.z);
        compound.putDouble("HeadingX", heading.x);
        compound.putDouble("HeadingZ", heading.z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.phase = Phase.valueOf(compound.getString("Phase"));
        this.surgeTicks = compound.getInt("SurgeTicks");
        this.surgeDirection = new Vec3(compound.getDouble("SurgeX"), 0.0, compound.getDouble("SurgeZ"));
        Vec3 savedHeading = new Vec3(compound.getDouble("HeadingX"), 0.0, compound.getDouble("HeadingZ"));
        if (savedHeading.lengthSqr() > 1.0E-6) {
            this.heading = savedHeading.normalize();
        }
    }

    public enum Phase {
        /// 出手下坠。
        FALLING,
        /// 贴地奔涌。
        SURGE,
        /// 折返施法者，伤害翻倍。
        RETURNING
    }
}
