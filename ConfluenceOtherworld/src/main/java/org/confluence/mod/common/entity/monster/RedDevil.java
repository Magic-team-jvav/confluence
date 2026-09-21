package org.confluence.mod.common.entity.monster;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.entity.projectile.UnholyTridentProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;

public final class RedDevil extends Demon {
    private static final double HOVER_CLEARANCE = 4.0;
    private static final double MIN_CLEARANCE = 3.5;

    public RedDevil(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 reboundVelocity(Vec3 requested, Vec3 allowed) {
        Vec3 movement = super.reboundVelocity(requested, allowed);
        if (level().isClientSide || !isAlive() || isApproachingVolleyTarget()) return movement;

        // 按整个身体下方以及下一步经过的区域找地面，不能只查中心点。
        // 巡航和投掷阶段维持悬浮；冲撞接近阶段不施加最低高度，允许接触地面目标。
        AABB body = getBoundingBox();
        AABB footprint = body.expandTowards(movement.x, 0, movement.z);
        AABB below = new AABB(footprint.minX, body.minY - HOVER_CLEARANCE - 0.5, footprint.minZ,
                footprint.maxX, body.minY, footprint.maxZ);
        double groundY = Double.NEGATIVE_INFINITY;
        for (var shape : level().getBlockCollisions(this, below)) {
            if (!shape.isEmpty()) groundY = Math.max(groundY, shape.max(Direction.Axis.Y));
        }
        if (!Double.isFinite(groundY)) return movement;

        double clearance = body.minY - groundY;
        double lift = Mth.clamp((HOVER_CLEARANCE - clearance) * 0.16, -0.12, 0.25);
        double vertical = Math.max(movement.y, lift);
        if (clearance >= MIN_CLEARANCE) vertical = Math.max(vertical, MIN_CLEARANCE - clearance);
        // 低矮洞穴不能为满足悬浮高度强行钻进顶棚，由原有水平反弹行为寻找出口。
        if (vertical > 0 && !level().noCollision(this, body.expandTowards(0, vertical, 0)))
            vertical = 0;
        return new Vec3(movement.x, vertical, movement.z);
    }

    @Override
    protected int[] volleyTicks() {
        return new int[]{175, 183, 191, 199, 207};
    }

    @Override
    protected Projectile createVolleyProjectile(LivingEntity target) {
        UnholyTridentProjectile projectile = new UnholyTridentProjectile(ModEntities.UNHOLY_TRIDENT.get(), level());
        projectile.configure(this, target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.2F, 0.6F, 0.0F, 160);
        playSound(SoundEvents.TRIDENT_THROW, 1.0F, 0.8F);
        return projectile;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.DEMON_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.DEMON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.DEMON_DEATH.get();
    }

}
