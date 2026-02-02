package org.confluence.terraentity.entity.proj;

import com.google.common.collect.Lists;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class LavaPillar extends BaseProj<LavaPillar> implements GeoEntity {

    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    int triggerTime = 50;
    int continueTime = 40;

    public LavaPillar(EntityType<? extends Projectile> pEntityType, Level pLevel, List<MobEffectInstance> pEffects) {
        super(pEntityType, pLevel, pEffects);
        this.penetration = 999;
    }

    public LavaPillar(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        this(pEntityType, pLevel, Lists.newArrayList());
    }

    @Override
    public int getLifetime() {
        return triggerTime + continueTime;
    }

    @Override
    public boolean isInWall() {
        return false;
    }

    // 取消渲染模型
    @Override
    public ResourceLocation getTexture(){
        return null;
    }


    @Override
    public void tick() {
        super.tick();
        if(this.level() instanceof ServerLevel serverLevel) {

            if (this.tickCount < triggerTime) {
                if(tickCount % 4 == 0) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.2f, this.getZ(), 4, 0.0D, 0.5D, 0.0D, 0);
                }
            } else {
                if(this.tickCount == triggerTime){
                    this.triggerAnim("Up", "up");
                }
                float f = (this.tickCount - triggerTime) * 1.0f / continueTime;
                f = f * ( 1f - f);
                if(tickCount % 8 == 0) {
                    serverLevel.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 1f, this.getZ(),
                            10, 0, f * 12, 0, 0);
                }

                if(this.tickCount % 10 == 0){
                    if(tickCount > triggerTime + continueTime * 0.5f){
                        this.doHurt(this.getBoundingBox().inflate(1).setMaxY(this.getY() + 3.0D));
                    }else {
                        this.doHurt(this.getBoundingBox().setMaxY(this.getY() + 3.0D));
                    }
                }
            }
        }
    }

    private void doHurt(AABB aabb){
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class,aabb , e->this.getOwner() == null ||
                this.getOwner() instanceof LivingEntity living && living.canAttack(e));
        for (LivingEntity livingentity : list) {
            livingentity.hurt(this.damageSources().source(DamageTypes.LAVA, this.getOwner()), this.damage);
        }
    }

    static RawAnimation up = RawAnimation.begin().thenPlay("up");
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "Up", state -> PlayState.CONTINUE)
                .triggerableAnim("up", up)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public boolean isTriggered(){
        return this.tickCount >= 50;
    }
}
