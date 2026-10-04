package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.confluence.mod.common.entity.ai.bt.leaf.WormMovementAction;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.OverworldUtils;

/// 飞龙使用与其他分段敌怪一致的 Worm AI；区别仅在于可在空气中持续转向。
public class Wyvern extends BaseWormMonster {
    public Wyvern(EntityType<? extends BaseWormMonster> type, Level level, EntityType<BaseWormPart> segmentType) {
        super(type, level, segmentType);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }


    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return target.getY() >= OverworldUtils.getSurfaceY() && super.canAttack(target);
    }

    @Override
    public boolean isInsideActivityRegion(BlockPos pos) {
        return pos.getY() >= OverworldUtils.getSurfaceY();
    }

    @Override
    protected int getSegmentCount() {
        return 12;
    }

    @Override
    protected float segmentSpacing() {
        return 1.0F;
    }

    @Override
    protected WormMovementAction.Profile movementProfile() {
        return WormMovementAction.Profile.flying();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.WYVERN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.WYVERN_DEATH.get();
    }
}
