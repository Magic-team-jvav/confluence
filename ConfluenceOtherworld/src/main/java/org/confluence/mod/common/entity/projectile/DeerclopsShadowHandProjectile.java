package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 独眼巨鹿用于惩罚高处目标的暗影之手。
///
/// <p>方向在生成时锁定，前十 tick 负责预警，随后十四 tick 快速突进，
/// 最后减速消散。它不会在飞行途中重新索敌，因此玩家仍可通过移动躲避。</p>
public final class DeerclopsShadowHandProjectile extends StraightMonsterProjectile
        implements GeoEntity {
    private static final EntityDataAccessor<Vector3f> DATA_ATTACK_DIRECTION =
            SynchedEntityData.defineId(
                    DeerclopsShadowHandProjectile.class,
                    EntityDataSerializers.VECTOR3);
    private static final int LIFETIME = 40;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DeerclopsShadowHandProjectile(
            EntityType<? extends DeerclopsShadowHandProjectile> type,
            Level level) {
        super(type, level);
    }

    public void configure(Mob owner, Vec3 origin, Vec3 direction, float damage) {
        Vec3 attackDirection = direction.normalize();
        entityData.set(DATA_ATTACK_DIRECTION, attackDirection.toVector3f());
        super.configure(owner, origin, Vec3.ZERO, damage, LIFETIME);
        setYRot((float) (Math.toDegrees(Math.atan2(
                attackDirection.z, attackDirection.x)) - 90.0));
        setXRot((float) -Math.toDegrees(Math.asin(attackDirection.y)));
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_ATTACK_DIRECTION, new Vector3f());
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        Vec3 attackDirection = new Vec3(entityData.get(DATA_ATTACK_DIRECTION));
        if (attackDirection.lengthSqr() < 1.0E-4) {
            attackDirection = Vec3.directionFromRotation(getXRot(), getYRot());
        }
        if (tickCount > 10 && tickCount < 25) {
            return attackDirection;
        }
        return velocity.scale(0.6);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        /// 前十 tick 是纯预警阶段。此时弹幕尚未移动，方块射线可能因为出生点
        /// 紧贴墙面而立即报告命中；预警阶段不得因此少掉一只手。开始突进后
        /// 恢复通用方块碰撞，玩家仍可借助墙体阻挡攻击。
        if (tickCount > 10) {
            super.onHitBlock(result);
        }
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), 100));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
