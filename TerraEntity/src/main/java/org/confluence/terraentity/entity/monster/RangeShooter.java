package org.confluence.terraentity.entity.monster;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.monster.prefab.AttributeBuilder;
import org.confluence.terraentity.entity.proj.BaseProj;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;

import java.util.function.Supplier;

/**
 * 远程法师
 */
public class RangeShooter extends AbstractMonster {

    int _phase = 200;
    int phase = _phase;
    int _delay = 8;
    int delay = -1;
    int lastPhase = _phase;
    Supplier<? extends EntityType<? extends BaseProj<?>>> projType;

    public RangeShooter(EntityType<? extends Monster> type, Level level, Supplier<? extends EntityType<? extends BaseProj<?>>> projType, AttributeBuilder builder) {
        super(type, level, builder);
        this.projType = projType;
    }
    public RangeShooter(EntityType<? extends Monster> type, Level level,int attackDelay,  Supplier<? extends EntityType<? extends BaseProj<?>>> projType, AttributeBuilder builder) {
        this(type, level, projType, builder);
        _delay = attackDelay;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        super.registerGoals();
    }

    public void tick() {
        super.tick();
        var att = this.getAttribute(Attributes.FOLLOW_RANGE);
        if(getTarget() != null){
            if(!att.hasModifier(TerraEntity.space("battle"))){
                att.addTransientModifier(new AttributeModifier(TerraEntity.space("battle"),1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
            LivingEntity target = getTarget();
            lookAt(target, 10, 70);
            this.moveControl.strafe(0.01f, 0.01f);
            if(phase == 180 || phase == 130 || phase == 80){
                lastPhase = phase;
                delay = _delay;
                this.swing(InteractionHand.MAIN_HAND, true);
            }
            if(--delay == 0){
                BaseProj proj = projType.get().create(level());
                proj.setOwner(this);
                proj.setPos(this.getEyePosition());
                proj.setDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                proj.shoot((float)(target.getX() - this.getX()), (float)(target.getY() - target.getBbHeight() * 0.3f - this.getY()), (float)(target.getZ() - this.getZ()), 0.3f, 0.8f);
                level().addFreshEntity(proj);
            }

            if(--phase<= 0){
                phase = _phase;
                Vec3 pos;
                for(int i = 0; i < 4; i++) {
                    pos = LandRandomPos.getPosTowards(this, 20, 5, target.position());
                    if(pos!= null){
                        this.moveTo(pos);
                        break;
                    }
                }
            }
        }else{
            if(att.hasModifier(TerraEntity.space("battle"))){
                att.removeModifier(TerraEntity.space("battle"));
            }
            phase = _phase;
        }

    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (getType() == TEMonsterEntities.FIRE_IMP.get() && pSource.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        if(super.hurt(pSource, pAmount)){
            this.phase = this.lastPhase-1;
            return true;
        }
        return false;
    }

    public int getCurrentSwingDuration() {
        return 20;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk/Idle/Attack", 5, state ->{
            if(this.swingTime > 0){
                return state.setAndContinue(DefaultAnimations.ATTACK_CAST);
            }
            if(state.isMoving()){
                return state.setAndContinue(DefaultAnimations.WALK);
            }
            return state.setAndContinue(DefaultAnimations.IDLE);
        }
        ));
    }

}
