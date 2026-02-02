package org.confluence.terraentity.api.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.utils.OBB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 以玩家为中心的OBB弹幕碰撞器
 * @param <T>
 */
public interface IOBBProjectile extends ICollisionAttackEntity, IOriented {


    /**
     * 长度缩放
     */
    float lengthScale();

    @Override
    default Projectile collision$getSelf(){
        return (Projectile) this;
    }

    @Override
    default void doCollisionAttack(Predicate<Entity> filter, Consumer<Entity> attackCallback){
        if(!shouldDoCollision() || collision$getSelf().level().isClientSide) return;
        getCollisionProperties().reduceAttackInterval();
        if (canCollisionHurt() && !collision$getSelf().level().isClientSide && getCollisionProperties().canAttack()) {
            OBB obb = getOrientedBoundingBox();
            AABB border = collision$getSelf().getBoundingBox().inflate(lengthScale() * 2);
            var entities = collision$getSelf().level().getEntitiesOfClass(LivingEntity.class, border, EntitySelector.NO_SPECTATORS.and(e -> {
                if (e instanceof Player) return false;
                return filter.test(e) && obb.inflate(10).collide(e.getBoundingBox(), collision$getSelf().getDeltaMovement(), e.getDeltaMovement());
            }));
            for (LivingEntity living : entities) {
                attackCallback.accept(living);
                getCollisionProperties().rewind();
            }
        }
    }

    @Override
    default OBB getOrientedBoundingBox() {
        return buildOBB().updateVertex();
    }


    default OBB buildOBB() {
        Vec3 pos = collision$getSelf().position();
        return new OBB(pos, 0.75, 0.75, 1.5 * lengthScale(), collision$getSelf().getXRot(), collision$getSelf().getYRot()).offsetAlongAxisZ(0.75 * (lengthScale() - 1));
    }

    /**
     * 获取世界坐标
     */
    default Vec3 getHandPosition(float partialTick) {
        Entity owner = collision$getSelf().getOwner();
        double d0 = (double) (Mth.lerp(partialTick, owner.yRotO, owner.getYRot()) * (float) (Math.PI / 180.0)) + (Math.PI / 2);

        Vec3 vec31 = new Vec3(-0.3, owner.getEyeHeight(), owner.getBbWidth() * 0.1F);

        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        return collision$getSelf().getOwner().position().add(d1, vec31.y, d2);
    }


    /**
     * 获取本地坐标
     * @param time tickCount
     */
    Vec3 getModelPosition(int time);

    default void updateObb(){
        Projectile self = collision$getSelf();
        if (self.getOwner() != null) {
            self.xRotO = self.getXRot();
            self.yRotO = self.getYRot();
            self.setXRot(updateXRot(self.tickCount) + self.getOwner().getXRot() * 0.8f);
            self.setYRot(updateYRot(self.tickCount) + self.getOwner().getYRot());

            // 改变轴点
//            Vector3f offset = new Vector3f(0F, 0f, 0.3F);
//            float selfPitch = this.updateXRot(this.tickCount) * 0.017453292F;
//            float selfYaw = -this.updateYRot(this.tickCount) * 0.017453292F;
//            new Quaternionf().rotateY(selfYaw).rotateX(selfPitch).transform(offset);

            // 本地坐标
            Vector3f pos = getModelPosition(self.tickCount).toVector3f()
//                    .add(offset)
                    ;

            // 父级旋转
            float parentYaw = -self.getOwner().getYRot() * 0.017453292F;
            float parentPitch = self.getOwner().getXRot() * 0.017453292F;
            new Quaternionf().rotateY(parentYaw).rotateX(parentPitch).transform(pos);

            // 世界坐标
            Vec3 finalPos = new Vec3(pos.x, pos.y, pos.z).add(getHandPosition(0));
            self.setPos(finalPos);
        }
    }

    float updateXRot(int time);

    float updateYRot(int time);

    float updateZRot(float time);
}
