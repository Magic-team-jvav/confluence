package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;

/**
 * 类似鸟妖这样的蝙蝠ai，但是可以发射弹幕
 */
public class Harpy extends AbstractMonster {

    protected int _shootTick = 20;
    protected int shootTick;

    protected int _shootCooldown = 150;
    protected int shootCooldown;

    protected int _shootCount = 3;
    protected int shootCount;


    public Harpy(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder);

        this.init();
        this.shootCount = _shootCount;
        this.shootCooldown = _shootCooldown;
        this.shootTick = _shootTick;
    }

    protected void init(){

    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        return spawnReason == MobSpawnType.NATURAL; // 无视光照
    }

    @Override
    public void tick(){
        super.tick();
        if(getTarget() != null) {
            this.lookControl.setLookAt(getTarget(),5, 80);
            this.lookAt(getTarget(), 5, 80);
            this.setDeltaMovement(getDeltaMovement().scale(0.95f));
            if (--shootCooldown < 0) {

                this.shootTick(getTarget(), _shootTick - shootTick);
                if (--shootTick < 0) {
                    shootTick = _shootTick;

                    this.shoot(getTarget());
                    if (--shootCount <= 0) {
                        shootCount = _shootCount;

                        shootCooldown = _shootCooldown;
                    }
                }

            }
        }

    }

    public void move(@NotNull MoverType pType, @NotNull Vec3 motion) {
        if (dead) {
            super.move(pType, motion);
            return;
        }

        Vec3 collide = ((EntityAccessor) this).callCollide(motion);
        if (collide.x != motion.x) {
            motion = new Vec3(-motion.x*0.8F, motion.y, motion.z).add(0,0.2f,0);
        }
        if (collide.z != motion.z) {
            motion = new Vec3(motion.x, motion.y, -motion.z*0.8F).add(0,0.2f,0);

        }

        setDeltaMovement(motion);
        super.move(pType, motion);
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

    protected void shoot(LivingEntity living){
        this.swing(InteractionHand.MAIN_HAND);
        LineProj proj = TEProjectileEntities.HARPY_FEATURE_PROJ.get().create(level());
        if(proj != null) {
            proj.setPos(this.getEyePosition());
            proj.setOwner(this);
            proj.shoot(living.getX() - this.getX(), living.getY() - this.getY(), living.getZ() - this.getZ(), 0.5f, 2f);
            proj.setDamage((float) this.getAttributeValue(LibAttributes.getAttackDamage()));
            level().addFreshEntity(proj);
        }
    }

    protected void shootTick(LivingEntity living, int tick){

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new DashGoal(this,
                0.95f, 0.5f, 30, 0.02f,
                10,90f,30f){
            @Override
            public void dashBackTick(){
                if(getTarget()!=null){
                    lookAt(getTarget(), 5, 80);
                }
            }
            @Override
            public boolean canUse() {
                return super.canUse() && shootCooldown > 0 || getState() != States.idle;
            }

        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericFlyController(this));
    }

}
