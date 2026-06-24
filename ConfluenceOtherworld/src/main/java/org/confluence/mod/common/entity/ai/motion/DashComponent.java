package org.confluence.mod.common.entity.ai.motion;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class DashComponent {
    Vec3 direction;
    public Vec3 targetPos;

    public Entity owner;

    public DashComponent(Entity owner) {
        this.owner = owner;
        this.direction = Vec3.ZERO;
        this.targetPos = owner.position();
    }


    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }

    public void setTargetPos(Vec3 targetPos) {
        this.targetPos = targetPos;
    }

    public Vec3 getDirection(){
        return direction;
    }

    public Vec3 getTargetPos(){
        return targetPos;
    }

    /**
     * 悬挂在目标实体目标位置
     * @param target 目标实体
     * @param distance xz距离
     * @param height 高度
     * @param speed 速度
     */
    public void hangOn(LivingEntity target, float distance, float height, float speed){
        if(target!=null){
            setNearestTargetPos(target, distance, height);
            direction = targetPos.subtract(owner.position());
            owner.addDeltaMovement(direction.scale(speed * 0.01f));
            if(owner.distanceToSqr(target)<2){
                owner.setDeltaMovement(owner.getDeltaMovement().scale(0.95f));
            }
        }
    }

    /**
     * 悬挂在目标实体目标头顶
     * @param target 目标实体
     * @param height 高度
     * @param speed 速度
     */
    public void hangAbove(LivingEntity target, float height, float speed){
        if(target!=null){
            targetPos = target.position().add(0, height, 0);
            direction = targetPos.subtract(owner.position());
            owner.addDeltaMovement(direction.scale(speed * 0.01f));
            if(owner.distanceToSqr(target)<2){
                owner.setDeltaMovement(owner.getDeltaMovement().scale(0.95f));
            }
        }
    }

    public void accelerate(float speed) {
        owner.addDeltaMovement(direction.normalize().scale(speed));
    }
    public void uniformMove(float speed) {
        owner.setDeltaMovement(direction.normalize().scale(speed));
    }

    /**
     * 预判冲刺方向
     * @param target 目标实体
     * @return 冲刺方向
     */
    public void setPredictDirection(Entity target){
        direction = target.position().add(0, 1, 0).add(target.getKnownMovement().scale(10)).subtract(owner.position());
    }



    /**
     * 获取目标相对直线位置
     * @param target   目标实体
     * @param distance xz距离
     * @param height   高度
     */
    public Vec3 setNearestTargetPos(Entity target, float distance, float height){
        return targetPos = owner.position().subtract(target.position()).multiply(1, 0, 1).normalize().scale(distance).add(0, height, 0).add(target.position());
    }

    public void lookAtDirection(){
        owner.lookAt(EntityAnchorArgument.Anchor.EYES, direction.add(owner.position()));
    }
}
