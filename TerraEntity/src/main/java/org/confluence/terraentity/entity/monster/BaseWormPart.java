package org.confluence.terraentity.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.api.entity.ICollisionAttackEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 蠕虫体节
 */
public class BaseWormPart extends PartEntity<BaseWorm> implements GeoEntity, ICollisionAttackEntity {

    private final EntityDimensions size;
    CollisionProperties collisionProperties = new CollisionProperties(10,20,0);

    public boolean isTail = false;
    public int index;

    public int deathTime;
    public int hurtTime;

    public BaseWormPart(BaseWorm parent, int index) {
        super(parent);
        this.size = this.getParent().getDimensions(Pose.STANDING);

        this.setBoundingBox(parent.getBoundingBox());
        this.index = index;
    }


    @Override
    public void tick() {
        if(this.getParent()== null || this.getParent().isRemoved()){
            discard();
            return;
        }
        if(this.isRemoved()){
            return;
        }

        this.deathTime = this.getParent().deathTime;
        this.hurtTime = Math.max(0, this.hurtTime - 1);

    }

    public void tickPart(Entity leader, double segInternal) {

        double xxo = this.getX();
        double yyo = this.getY();
        double zzo = this.getZ();
        float xRotOO = this.getXRot();
        float yRotOO = this.getYRot();

        this.tick();

        double followX = leader.getX();
        double followY = leader.getY();
        double followZ = leader.getZ();

        // 方向

        Vec3 diff = new Vec3(this.getX() - followX, this.getY() - followY, this.getZ() - followZ);
        diff = diff.normalize().scale(segInternal);

        // 弹簧恢复力
//            if(!this.isAlive()) {
//                float angle = (((leader.getYRot() + 180) * Mth.PI) / 180.0F);
//                double straightenForce = 0.05D + (1.0D / (i + 1)) * 0.5D;
//                if (this.isDeadOrDying()) straightenForce = 0.0D; //Dead snakes don't move
//                double idealX = -Mth.sin(angle) * straightenForce;
//                double idealZ = Mth.cos(angle) * straightenForce;
//                double groundY = this.isInWall() ? followY + 2.0F : followY;
//                double idealY = (groundY - followY) * straightenForce;
//                diff = diff.add(idealX, idealY, idealZ).normalize();
//            }
        if(!this.isAlive()){
            float dy = (float) (this.getY() - followY);
            if(dy < 0)
                diff = diff.add(new Vec3(0,dy * this.deathTime / 100,0 ));
        }

        double f = 1.0D;

        double destX = followX + f * diff.x();
        double destY = followY + f * diff.y();
        double destZ = followZ + f * diff.z();

        double distance = Mth.sqrt((float) (diff.x() * diff.x() + diff.z() * diff.z()));
        float yaw = (float) (Math.atan2(diff.z(), diff.x()) * 180.0D / Math.PI) + 90.0F;
        float pitch = -(float) (Math.atan2(diff.y(), distance) * 180.0D / Math.PI);

        this.setYRot(yaw);
        this.setXRot(pitch);


        this.setDeltaMovement(destX - this.getX(), destY - this.getY(), destZ - this.getZ());
        this.moveTo(destX, destY, destZ, yaw, pitch);

        this.xo = xxo;
        this.yo = yyo;
        this.zo = zzo;
        this.xRotO = xRotOO;
        this.yRotO = yRotOO;
        this.yRotO = wrapYRotation(this.yRotO, yaw);
//        this.xRotO = wrapYRotation(this.xRotO, pitch);

        this.doCollisionAttack(e->e instanceof LivingEntity living && this.getParent().canAttack(living),
                e->this.getParent().doHurtTarget(e)
        );
    }

    protected float wrapYRotation(float current, float target){
        while (target - current > 180.0F){
            current += 360.0F;
        }
        while (target - current < -180.0F){
            current -= 360.0F;
        }
        return current;
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
//        this.getParent().setHealth(this.getParent().getHealth() - amount);
//        SoundEvent hurtSound = this.getParent().getHurtSound(source);
//        if(hurtSound!=null) {
//            playSound(hurtSound);
//        }
//        if(this.getParent().getHealth() <= 0) {
//            return false;
//        }
        if (getParent().hurt(source, amount)) {
            this.hurtTime = 10;
            return true;
        }
        return false;
    }

    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    public boolean is(Entity entity) {
        return this == entity || this.getParent() == entity;
    }


    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    public boolean shouldBeSaved() {
        return false;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public CollisionProperties getCollisionProperties() {
        return collisionProperties;
    }

    @Override
    public boolean shouldDoCollision() {
        return getParent().shouldDoCollision();
    }

    @Override
    public boolean isSprinting() {
        return getParent().isSprinting();
    }
}
