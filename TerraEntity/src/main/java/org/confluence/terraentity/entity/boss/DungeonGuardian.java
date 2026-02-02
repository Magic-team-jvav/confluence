package org.confluence.terraentity.entity.boss;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.init.TETags;

public class DungeonGuardian extends Skeletron {
    int _attackDelay = 50;

    int attackDelay = _attackDelay;
    public DungeonGuardian(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1,new SpinGoal(){
            @Override
            public boolean canUse() {
                return getTarget() != null;
            }
            @Override
            public void tick() {
                Vec3 vec = getTarget().position().subtract(position());
                setDeltaMovement(vec.normalize().scale(0.8));
                lookAt(90);
            }

        });
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide && --attackDelay == 0){
            Player player = level().getNearestPlayer(this, 100);
            if(player == null || !player.isAlive()){
                this.discard();
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        attackDelay = _attackDelay;
        return entity.hurt(TETags.DamageTypes.of(level(), TETags.DamageTypes.PASS_ARMOR, this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
    }

    @Override
    public void firstSpawn() {

    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.IS_DROWNING)) {
            return false;
        }
        return super.hurt(pSource, pAmount); // confluence mixin here
    }

    @Override
    public boolean shouldShowMessage() {
        return false;
    }

    @Override
    public boolean shouldShowBossBar() {
        return false;
    }
}
