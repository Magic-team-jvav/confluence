package org.confluence.terraentity.entity.proj;

import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.registries.track.variant.BasisTrack;
import org.confluence.terraentity.utils.TEUtils;

public class SporeProjectile extends BaseProj<SporeProjectile> {
    private static final double Y_COMPONENT_SPEED = 0.1;
    private static final double Y_COMPONENT_DIFF_BEFORE_FALLING = 5.0;
    public Entity target;
    private boolean moveUpward = true; // 向上漂浮，在Y方向超过玩家头顶后开始向下坠落

    public SporeProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        this(pEntityType, pLevel, null);
    }
    public SporeProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel, Entity target) {
        super(pEntityType, pLevel, (MobEffectInstance) null);
        this.target = target;
        trackType = new BasisTrack(90, 0.15f);
        canPenetrateBlock = true;
        accelerationPower = 0;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 movement = getDeltaMovement();
        movement = movement.with(Direction.Axis.Y, moveUpward ? Y_COMPONENT_SPEED : -Y_COMPONENT_SPEED);

        if (getOwner() != null && target != null) {
            if (position().y() >= target.position().y() + Y_COMPONENT_DIFF_BEFORE_FALLING) moveUpward = false;

            Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5f, 0).subtract(position());
            dir = dir.with(Direction.Axis.Y, movement.get(Direction.Axis.Y)); // 防止y方向影响追踪效率

            double angle = TEUtils.angleBetween(movement, dir);
            movement = trackType.calDeltaMovement(movement, dir, angle);
        }
        else {
            moveUpward = false;
        }

        setDeltaMovement(movement);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public int getLifetime() {
        return 300;
    }
}
