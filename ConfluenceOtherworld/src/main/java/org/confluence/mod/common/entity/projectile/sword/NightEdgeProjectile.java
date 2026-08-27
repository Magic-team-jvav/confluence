package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.terraentity.api.entity.IOBBProjectile;
import org.confluence.terraentity.entity.ai.keyframe.Keyframe;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.confluence.terraentity.utils.OBB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NightEdgeProjectile extends SwordProjectile implements IOBBProjectile {
    private final Vec3KeyframeAnimation posAnimation;
    private final Vec3KeyframeAnimation rotAnimation;

    public NightEdgeProjectile(EntityType<? extends SwordProjectile> type, Level level) {
        super(type, level);

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
                new Keyframe(0, -2, 1, 1),
                new Keyframe(3, 1, 1, 1),
                new Keyframe(6, 1, -1, 1),
                new Keyframe(9, -2, -1, 1),
                new Keyframe(12, -2)
        ));

        this.rotAnimation = Vec3KeyframeAnimation.builder()
                .addKeyframe(new Keyframe(0, 0, 0, 1, 0, 1), new Vec3(0, 135, 120))
                .addKeyframe(new Keyframe(3, 0, 0, 1, 0, 1), new Vec3(0, 45, 120))
                .addKeyframe(new Keyframe(6, 0, 0, 1, 0, 1), new Vec3(0, -45, 120))
                .addKeyframe(new Keyframe(9, 0, 0, 1, 0, 1), new Vec3(0, -135, 120))
                .addKeyframe(new Keyframe(12, 0, 0, 1, 0, 1), new Vec3(0, -225, 120))
                .build();

        this.setExistTime(11);
    }

    @Override
    public DamageSource damageSource() {
        return ModDamageTypes.of(level(), DamageTypes.MOB_ATTACK, this, getOwner());
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
        this.setDeltaMovement(Vec3.ZERO);
        this.updateObb();
    }

    // 调整攻击范围
    @Override
    public float lengthScale() {
        return 6f;
    }

    public OBB buildOBB() {
        return IOBBProjectile.super.buildOBB().inflate(0.5);
    }

    /// 获取本地坐标
    ///
    /// @param time tickCount
    public Vec3 getModelPosition(int time) {
        return posAnimation.cal(time);
    }

    public float updateXRot(int time) {
        return (float) rotAnimation.cal(time).x();
    }

    public float updateYRot(int time) {
        return (float) rotAnimation.cal(time).y();
    }

    public float updateZRot(float time) {
        return (float) rotAnimation.cal(time).z();
    }
}
