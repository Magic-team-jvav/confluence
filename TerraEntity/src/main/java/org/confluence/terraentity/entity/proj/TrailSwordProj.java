package org.confluence.terraentity.entity.proj;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.entity.IOBBProjectile;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.confluence.terraentity.entity.util.trail.SwordTrail;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * OBB拖尾剑气
 */
public class TrailSwordProj<T extends TrailSwordProj<T>> extends BaseProj<T> implements IOBBProjectile {

    public SwordTrail trail;
    public Queue<Vec3> trailQueue;

    float speed = 4f;
    float lengthScale = 2f;

    Vec3KeyframeAnimation posAnimation;
    Vec3KeyframeAnimation rotAnimation;


    public TrailSwordProj(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        this(pEntityType, pLevel, List.of());
    }


    public TrailSwordProj(EntityType<? extends Projectile> pEntityType, Level pLevel, List<MobEffectInstance> pEffects) {
        super(pEntityType, pLevel, pEffects);
        this.penetration = 9999;
        this.trailQueue = new LinkedList<>();
        this.noPhysics = true;
        this.trail = new SwordTrail(1, 0.15f, 0xc1d236);

        this.posAnimation = Vec3KeyframeAnimation.builder()
                .addKeyframe(0, new Vec3(-0.8,0.3,1))
                .addKeyframe(10, new Vec3(1.5,-0.4,1))
                .build();
        this.rotAnimation = Vec3KeyframeAnimation.builder()
                .addKeyframe(0, new Vec3(0,70,120))
                .addKeyframe(10, new Vec3(0,-70,120))
                .build();


    }

    public TrailSwordProj setTrail(SwordTrail trail){
        this.trail = trail;
        return this;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide){
            this.trail.generateTrail(this, tickCount);
        }

        this.updateObb();
//        if (this.getOwner() != null) {
//            this.xRotO = this.getXRot();
//            this.yRotO = this.getYRot();
//            this.setXRot(this.updateXRot(this.tickCount) + this.getOwner().getXRot() * 0.8f);
//            this.setYRot(this.updateYRot(this.tickCount) + this.getOwner().getYRot());
//
//            // 改变轴点
////            Vector3f offset = new Vector3f(0F, 0f, 0.3F);
////            float selfPitch = this.updateXRot(this.tickCount) * 0.017453292F;
////            float selfYaw = -this.updateYRot(this.tickCount) * 0.017453292F;
////            new Quaternionf().rotateY(selfYaw).rotateX(selfPitch).transform(offset);
//
//            // 本地坐标
//            Vector3f pos = getModelPosition(this.tickCount).toVector3f()
////                    .add(offset)
//                    ;
//
//            // 父级旋转
//            float parentYaw = -getOwner().getYRot() * 0.017453292F;
//            float parentPitch = getOwner().getXRot() * 0.017453292F;
//            new Quaternionf().rotateY(parentYaw).rotateX(parentPitch).transform(pos);
//
//            // 世界坐标
//            Vec3 finalPos = new Vec3(pos.x, pos.y, pos.z).add(getHandPosition(0));
//            this.setPos(finalPos);
//        }

    }

    @Override
    public DamageSource getDamageSource(LivingEntity hurter){
        if(getOwner() != null && getOwner() instanceof Player player){
            return damageSources().playerAttack(player);
        }
        return super.getDamageSource(hurter);
    }

    @Override
    public float lengthScale() {
        return lengthScale;
    }

//    @Override
//    public void doCollisionAttack(Predicate<Entity> filter, Consumer<Entity> attackCallback){
//        if(!shouldDoCollision() || collision$getSelf().level().isClientSide) return;
//        getCollisionProperties().reduceAttackInterval();
//        if (canCollisionHurt() && !collision$getSelf().level().isClientSide && getCollisionProperties().canAttack()) {
//            OBB obb = getOrientedBoundingBox();
//            AABB border = getBoundingBox().inflate(lengthScale * 2);
//            var entities = level().getEntitiesOfClass(LivingEntity.class, border, EntitySelector.NO_SPECTATORS.and(e -> {
//                if (e instanceof Player) return false;
//                return filter.test(e) && obb.inflate(10).collide(e.getBoundingBox(), getDeltaMovement(), e.getDeltaMovement());
//            }));
//            for (LivingEntity living : entities) {
//                attackCallback.accept(living);
//                getCollisionProperties().rewind();
//            }
//        }
//    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
//        this.setPos(getHandPosition(0));
    }

    @Override
    public int getLifetime() {
        return (int) (40 / speed);
    }

//    @Override
//    public OBB getOrientedBoundingBox() {
//        Vec3 pos = position();
//        return new OBB(pos, 0.5, 0.5, 1.5 * lengthScale, this.getXRot(), getYRot()).offsetAlongAxisZ(0.75 * (lengthScale - 1)).updateVertex();
//    }

//    /**
//     * 获取世界坐标
//     */
//    public Vec3 getHandPosition(float partialTick) {
//        Entity owner = getOwner();
//        double d0 = (double) (Mth.lerp(partialTick, owner.yRotO, owner.getYRot()) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
//
//        Vec3 vec31 = new Vec3(-0.3, owner.getEyeHeight(), owner.getBbWidth() * 0.1F);
//
//        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
//        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
//        return getOwner().position().add(d1, vec31.y, d2);
//    }

    /**
     * 获取本地坐标
     * @param time tickCount
     */
    public Vec3 getModelPosition(int time) {
//        return Vec3.ZERO;
        return posAnimation.cal(time);
//        return new Vec3(updateX(time),updateY(time),updateZ(time));
    }

    public float updateX(int time){
        return time * speed * 0.06f - 0.8F;
    }

    public float updateY(int time){
        return time * speed * -0.02f + 0.3F;
    }

    public float updateZ(int time){
        return 1;
    }

    public float updateXRot(int time){
//        return 20;
        return (float) rotAnimation.cal(time).x();
//        return 0 * speed;
    }

    public float updateYRot(int time){
//        return 0;
        return (float) rotAnimation.cal(time).y();
//        return -time * 3 * speed + 70F;
    }

    public float updateZRot(float time){
        return (float) rotAnimation.cal(time ).z();
    }



}
