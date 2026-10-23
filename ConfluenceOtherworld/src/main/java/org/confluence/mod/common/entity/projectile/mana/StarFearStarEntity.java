package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;

/// 星惧鞭子命中敌怪后冒出的坠星。
///
/// 它会快速逃离施法者；只要施法者成功碰到它，就会获得「摘星」增益。
public class StarFearStarEntity extends AbstractManaProjectile {
    /// 逃离速度。
    private static final double FLEE_SPEED = 0.95;
    /// 施法者触碰判定距离。
    private static final double CATCH_DISTANCE = 1.6;
    /// 未追上时的存在时间（tick）。
    private static final int MAX_AGE = 120;
    /// 摘星增益时长（tick）。
    public static final int BUFF_DURATION = 400;

    public StarFearStarEntity(EntityType<StarFearStarEntity> entityType, Level level) {
        super(entityType, level);
    }

    public StarFearStarEntity(LivingEntity living) {
        this(ModEntities.STAR_FEAR_STAR.get(), living.level());
    }

    /// 不参与常规碰撞，只做「被施法者追上」的判定。
    @Override
    protected void doHitCheck() {}

    @Override
    public void baseTick() {
        super.baseTick();
        LivingEntity owner = getLivingOwner();
        if (owner == null) {
            if (!level().isClientSide) discard();
            return;
        }
        if (!level().isClientSide && distanceToSqr(owner) <= CATCH_DISTANCE * CATCH_DISTANCE) {
            owner.addEffect(new MobEffectInstance(ModEffects.STAR_HARVEST, BUFF_DURATION));
            discard();
            return;
        }
        // 沿远离施法者的水平方向快速逃离，并保留一点上下浮动。
        Vec3 away = new Vec3(getX() - owner.getX(), 0.0, getZ() - owner.getZ());
        if (away.lengthSqr() < 1.0E-4) {
            away = new Vec3(random.nextDouble() - 0.5, 0.0, random.nextDouble() - 0.5);
        }
        Vec3 flee = away.normalize().scale(FLEE_SPEED);
        setDeltaMovement(flee.x, Math.sin(tickCount * 0.6) * 0.12, flee.z);
        doSimpleMove();
        doAgeCheck(MAX_AGE);
    }
}
