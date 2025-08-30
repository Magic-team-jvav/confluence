package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.mod.integration.terra_entity.trail.TerraSwordTrail;
import org.confluence.terraentity.api.entity.IOBBProjectile;
import org.confluence.terraentity.entity.ai.keyframe.Keyframe;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.confluence.terraentity.utils.OBB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.List;

import static org.confluence.terraentity.utils.TEUtils.rotToDir;

public class NightEdgeProjectile extends SwordProjectile implements IOBBProjectile {

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


        this.rotAnimation = Vec3KeyframeAnimation.builder()
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
        Level level = level();
        if (level.isClientSide) {
            this.trail.generateTrail(this, tickCount);
        }
        if (level.isClientSide() && tickCount <= 14 && level.random.nextBoolean()) {
            Vec3 pos = position().offsetRandom(level.random, 0.3f);
            Entity owner = getOwner();
            if (owner != null) {
                Vec3 facing = rotToDir(owner.getYHeadRot(), owner.getXRot()).scale(0.05);
                Vector4f curve = new Vector4f(0, 1f, 1f, 1);
                boolean dark = level.random.nextBoolean();
                CrossDustParticleOptions lightParticle = new CrossDustParticleOptions(false,
                    dark ? 0xff570EFD : 0xffE4E0FF, dark ? 0xff411EA3 : 0xffC55BFF, facing.toVector3f(), curve,
                    level.random.nextFloat() * 0.15f + 0.15f, 10, 60, curve,
                    true, true, true, false);
                level.addParticle(lightParticle, pos.x, pos.y, pos.z, 0, 0, 0);
            }

            Vector4f curve = new Vector4f(0, 0.7f, 0.9f, 1);
            CrossDustParticleOptions darkParticle = new CrossDustParticleOptions(level.random.nextBoolean(),
                0x66DD99FF, 0x7f714E82, Vec3.ZERO.offsetRandom(level.random, level.random.nextFloat() * 0.005f + 0.01f).toVector3f(),
                curve, level.random.nextFloat() * 0.6f + 0.4f, 30, level.random.nextInt(-40, 40),
                curve, true, true, false, false);
            level.addParticle(darkParticle, pos.x, pos.y, pos.z, 0, 0, 0);
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
        return 6f;
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
