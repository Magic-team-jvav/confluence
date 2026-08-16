package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 敌对水生生物共用的水下导航、移动与目标选择基类。
///
/// <p>具体生物只补充属性和攻击方式，避免各自重复处理离水、游动与路径导航边界。</p>
public abstract class BaseAquaticMonster extends BaseMonster {
    protected BaseAquaticMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected boolean canTargetPlayer(LivingEntity target) {
        return target.isInWater();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return target.isInWater() && super.canAttack(target);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isNoAi() && !isInWaterRainOrBubble() && onGround()) {
            setDeltaMovement(getDeltaMovement().add(
                    (random.nextFloat() * 2.0F - 1.0F) * 0.2F,
                    0.5,
                    (random.nextFloat() * 2.0F - 1.0F) * 0.2F));
            setYRot(random.nextFloat() * 360.0F);
            setOnGround(false);
            hasImpulse = true;
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isEffectiveAi() && isInWater()) {
            moveRelative(getSpeed(), travelVector);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9));
            if (getTarget() == null) {
                setDeltaMovement(getDeltaMovement().add(0.0, -0.005, 0.0));
            }
            return;
        }
        super.travel(travelVector);
    }
}
