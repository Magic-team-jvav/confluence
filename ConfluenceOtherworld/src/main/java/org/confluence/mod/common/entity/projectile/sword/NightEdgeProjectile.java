package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.integration.terra_entity.trail.TerraSwordTrail;
import org.confluence.terraentity.entity.ai.IOBBProjectile;
import org.confluence.terraentity.entity.ai.keyframe.Keyframe;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.confluence.terraentity.entity.util.trail.PositionPoseProperties;
import org.confluence.terraentity.utils.OBB;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NightEdgeProjectile extends SwordProjectile<NightEdgeProjectile> implements IOBBProjectile<NightEdgeProjectile> {

    Vec3KeyframeAnimation posAnimation;
    Vec3KeyframeAnimation rotAnimation;
    public TerraSwordTrail trail;


    public NightEdgeProjectile(EntityType<? extends SwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);

        this.canPenalize = true;
        this.hitCount = 9999;

        this.posAnimation = new Vec3KeyframeAnimation(List.of(
                new Keyframe(0, -1.2, -0.5, 1),
                new Keyframe(3, -1.2, 0.5, 1),
                new Keyframe(6, 1.2, 0.5, 1),
                new Keyframe(9, 1.2, -0.5, 1),
                new Keyframe(12, -1.2)
        ), List.of(
                new Keyframe(0, 0.3),
                new Keyframe(3, -0.4),
                new Keyframe(6, -0.8),
                new Keyframe(9, -0.4),
                new Keyframe(12, 0.3)
        ), List.of(
                new Keyframe(0, -2, 1,1),
                new Keyframe(3, 1, 1,1),
                new Keyframe(6, 1, -1, 1),
                new Keyframe(9, -2, -1, 1),
                new Keyframe(12, -2)
        ));


        this.rotAnimation = Vec3KeyframeAnimation.Builder()
                .addKeyframe(new Keyframe(0, 0, 0,1,0,1), new Vec3(0,135,120))
                .addKeyframe(new Keyframe(3, 0, 0,1,0,1), new Vec3(0,45,120))
                .addKeyframe(new Keyframe(6, 0, 0,1,0,1), new Vec3(0,-45,120))
                .addKeyframe(new Keyframe(9, 0, 0,1,0,1), new Vec3(0,-135,120))
                .addKeyframe(new Keyframe(12, 0, 0,1,0,1), new Vec3(0,-225,120))
                .build();

        this.trail = new TerraSwordTrail(3F, 0.3f, 0x121212);
        this.setExistTime(11);

    }


    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(3); // 让第一人称可以看到刀光
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    public boolean isControlledByLocalInstance() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide){
            this.trail.generateTrail(this, tickCount);
        }
        this.setDeltaMovement(Vec3.ZERO);
        this.updateObb();


    }
    public void onAddedToLevel(){
        super.onAddedToLevel();

    }


    // 调整攻击范围
    @Override
    public float lengthScale() {
        return 2f;
    }

    public OBB buildOBB() {
        return IOBBProjectile.super.buildOBB().inflate(0.5);
    }
    /**
     * 获取本地坐标
     * @param time tickCount
     */
    public Vec3 getModelPosition(int time) {
        return posAnimation.cal(time);
    }

    public Vec3 getModelPosition(float time) {
        return posAnimation.cal(time);
    }

    public float updateXRot(int time){
        return (float) rotAnimation.cal(time).x();
    }

    public float updateYRot(int time){
        return (float) rotAnimation.cal(time).y();
    }

    public float updateZRot(float time){
        return (float) rotAnimation.cal(time ).z();
    }
}
