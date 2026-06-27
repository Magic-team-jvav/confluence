package org.confluence.mod.common.entity.projectile.Flail;

import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModDamageTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.ParticleStorm;

/**
 * <h1>滴滴怪致残者投射物</h1>
 * 由 {@code DripplerCripplerItem.DripplerCripplerAttackStrategy} 在连枷 THROWN→RETRACT 时发射。
 * <p>
 * 受重力影响（较重），会在方块上弹射最多 2 次，或击中 2 个敌怪后消失。
 * 造成武器面板 50% 的伤害。
 */
public class DripplerCripplerProjectile extends BaseFlailProjectile {

    /** 剩余方块弹射次数 */
    private int bounceLeft = 3;
    /** 剩余可穿透实体数 */
    private int pierceLeft = 2;
    /** 弹射时的速度衰减系数 */
    private static final float BOUNCE_DAMPING = 0.6F;
    /** 额外重力加速度 */
    private static final double EXTRA_GRAVITY = 0.08;

    public DripplerCripplerProjectile(@NotNull EntityType<? extends BaseFlailProjectile> entityType,
                                      @NotNull Level level,
                                      @Nullable BaseFlailEntity parentFlail,
                                      @Nullable Player owner,
                                      @NotNull Vec3 velocity) {
        super(entityType, level, parentFlail, owner);
        setDeltaMovement(velocity);
        setNoGravity(false);
        faceVelocity();
    }

    /** 供 {@link EntityType.Builder} 反射使用的无参构造器 */
    public DripplerCripplerProjectile(@NotNull EntityType<? extends BaseFlailProjectile> entityType,
                                      @NotNull Level level) {
        super(entityType, level);
    }

    @Override
    protected void subTick() {
        // 施加额外重力
        if (!isNoGravity()) {
            setDeltaMovement(getDeltaMovement().add(0, -EXTRA_GRAVITY, 0));
        }
    }

    @Override
    public boolean canHitEntity(@NotNull Entity target) {
        Entity owner = getOwner();
        if (target == owner || target == getParentFlail()) return false;
        return super.canHitEntity(target);
    }

    @Override
    protected void onHitLiving(@NotNull LivingEntity target) {
        Player player = getOwnerAsPlayer();
        if (player == null) return;

        DamageSource source = ModDamageTypes.of(level(), ModDamageTypes.SWORD_PROJECTILE, this, player);
        if (target.hurt(source, baseDamage)) {
            VectorUtils.knockBackA2B(this, target, 0.15f, 0.08f);
        }

        // 穿透计数递减，耗尽后移除
        pierceLeft--;
        if (pierceLeft <= 0) {
            discard();
        }
    }

    @Override
    protected boolean onProjectileBlockHit(@NotNull BlockHitResult hitResult) {
        Vec3 motion = getDeltaMovement();
        if (motion.lengthSqr() < 1e-8) {
            return false;
        }
        // 根据击中面反射速度
        Direction face = hitResult.getDirection();
        Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());
        double verticalComponent = Math.abs(normal.y);
        // cos(45°) ≈ 0.707
        if (verticalComponent > 0.707) { 
            bounceLeft--;
        }
        if (bounceLeft <= 0) {
            return false; // 弹跳次数耗尽，移除
        }

        switch (hitResult.getDirection()) {
            case DOWN, UP -> setDeltaMovement(motion.x, -motion.y * BOUNCE_DAMPING, motion.z);
            case NORTH, SOUTH -> setDeltaMovement(motion.x, motion.y, -motion.z * BOUNCE_DAMPING);
            case EAST, WEST -> setDeltaMovement(-motion.x * BOUNCE_DAMPING, motion.y, motion.z);
        }
        // 更新朝向
        faceVelocity();
        return true; // 阻止默认移除，继续存活
    }

    //TODO 应用血粒子
}
