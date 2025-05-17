package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.integration.terra_entity.trail.TerraSwordTrail;
import org.confluence.terraentity.entity.ai.IOBBProjectile;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;

import java.util.LinkedList;
import java.util.Queue;

public class NightEdgeProjectile extends SwordProjectile<NightEdgeProjectile> implements IOBBProjectile<NightEdgeProjectile> {

    Vec3KeyframeAnimation posAnimation;
    Vec3KeyframeAnimation rotAnimation;
    public TerraSwordTrail trail;
    public Queue<Vec3> trailQueue;


    public NightEdgeProjectile(EntityType<? extends SwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);

        this.canPenalize = true;
        this.hitCount = 9999;
        this.posAnimation = Vec3KeyframeAnimation.Builder()
                .addKeyframe(0, new Vec3(-0.8,0.3,1))
                .addKeyframe(5, new Vec3(1.5,-0.4,1))
                .addKeyframe(10, new Vec3(1.5,-0.8,-2))
                .addKeyframe(15, new Vec3(-0.8,-0.4,-2))
                .addKeyframe(20, new Vec3(-0.8,0.3,1))
                .build();


        this.rotAnimation = Vec3KeyframeAnimation.Builder()
                .addKeyframe(0, new Vec3(0,70,120))
                .addKeyframe(5, new Vec3(0,-70,120))
                .addKeyframe(10, new Vec3(0,-140,120))
                .addKeyframe(15, new Vec3(0,-280,120))
                .addKeyframe(20, new Vec3(0,-360,120))
                .build();

        this.trail = new TerraSwordTrail(1, 0.15f, 0x121212);
        this.trailQueue = new LinkedList<>();

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

    @Override
    public float lengthScale() {
        return 2f;
    }

    /**
     * 获取本地坐标
     * @param time tickCount
     */
    public Vec3 getModelPosition(int time) {
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
