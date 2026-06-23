package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.IDiscardWhenRespawnEntity;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terraentity.entity.ai.goal.MutableRangeNearestAttackableTargetGoal;
import org.jetbrains.annotations.NotNull;

public class BoneSerpent<S extends BaseWormPart> extends BaseWorm<S> implements IDiscardWhenRespawnEntity {

    public BoneSerpent(EntityType<? extends BoneSerpent> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);
        this.segInternal = 2.5f;
    }
    @Override
    public int getSegmentCount() {
        return 18;
    }
    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(1, new Wyvern.WyvernAttackGoal<>(this, 16));
        this.goalSelector.addGoal(3, new Wyvern.WyvernRandomStrollGoal<>(this, 20, 0.015f, 0.2f, 6, 150,10,0.9f));

//        this.targetSelector.addGoal(1,new AccelerateOnSeeingGoal(this,0.25f));
        this.targetSelector.addGoal(2, new MutableRangeNearestAttackableTargetGoal<>(this, Player.class,false));
    }



    @Override
    public S createPart(int index) {
        return (S) createSimplePart(this, index);
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public double wrapWanderHeight(Vec3 pos){
        double y = Math.max(pos.y, 25);
        return y + 7; // 至少离地面10格高
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSoundEvents.TR_SKELETON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BONE_SERPENT_DEATH.get();
    }
}
