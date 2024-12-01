package org.confluence.terraentity.entity.proj;


import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class BaseProj extends AbstractHurtingProjectile{
    private final long starttime = System.currentTimeMillis();
    public float damage;
    public int penetration = 1;
    protected MobEffectInstance effect;
    public ResourceLocation texture;

    public BaseProj(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel, MobEffectInstance pEffect) {
        super(pEntityType, pLevel);
        this.effect = pEffect;
    }


    public float getDamage() {return damage;}
    public void setDamage(int damage) {this.damage = damage;}
    public void setPenetrate(int penetration){this.penetration = penetration;}
    public void setTexture(ResourceLocation texture){this.texture = texture;}
    public ResourceLocation getTexture(){return texture;}
    public abstract int waveDur();

    public void doAABBHurt(){
        //包围盒检测造成伤害
        var entities=level().getEntities(this,this.getBoundingBox());
        if(!entities.isEmpty() && penetration > 0){
            for (var e:entities) {
                if(canHitEntity(e)) {
                    if(e instanceof LivingEntity living) {
                        living.hurt(this.damageSources().mobProjectile(this,living), getDamage());

                        //doKnockBack(living);
                        penetration--;
                        if(penetration <= 0) discard();
                    }
                    break;
                }
            }
        }
    }

    protected void doKnockBack(LivingEntity entity) {
        double d1 = Math.max(0.0, 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(((LivingEntity)getOwner()).getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) * 0.6 * d1);
        if (vec3.lengthSqr() > 0.0) {
            entity.push(vec3.x, 0.1, vec3.z);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }


    @Override
    public void tick() {
        if(!level().isClientSide){
            if(System.currentTimeMillis()-starttime > waveDur() * 50L) {
                discard();
                return;
            }
            doAABBHurt();
            if(penetration <= 0) return;
        }
        super.tick();
    }

    //弹幕设置
    @Override//取消射击惯性
    public void shootFromRotation(Entity pShooter, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * (float) (Math.PI / 180.0)) * Mth.cos(pX * (float) (Math.PI / 180.0));
        float f1 = -Mth.sin((pX + pZ) * (float) (Math.PI / 180.0));
        float f2 = Mth.cos(pY * (float) (Math.PI / 180.0)) * Mth.cos(pX * (float) (Math.PI / 180.0));
        this.shoot(f, f1, f2, pVelocity, pInaccuracy);
    }
    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
        if(!level().isClientSide()){
            if(getOwner()==null){
                discard();
                return;
            }
            this.damage = (int) ((LivingEntity)getOwner()).getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        }
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {

        if(!this.level().isClientSide()) {
            if(this.isRemoved()) return;
            Entity entity1 = pResult.getEntity();
            Entity entity = this.getOwner();
            if(effect!= null && entity1 != entity && entity1 instanceof LivingEntity living){
                living.addEffect(effect);
                living.hurt(this.damageSources().mobProjectile(this,living), getDamage());
            }
            this.discard();

        }
        super.onHitEntity(pResult);
    }


    @Nullable
    @Override//设置粒子效果
    protected ParticleOptions getTrailParticle() {
        return null;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity pTarget) {
        return pTarget != getOwner() &&
                super.canHitEntity(pTarget);
    }

    @Override//流体阻力
    protected float getLiquidInertia() {
        return 1;
    }

    @Override//火焰效果
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override//空气阻力
    protected float getInertia() {
        return 1;
    }
    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(!this.level().isClientSide()) discard();
    }


    public static class TextureLib{



    }

}
