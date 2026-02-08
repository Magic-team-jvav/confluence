package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terra_curio.common.entity.BeeProjectile;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.registries.track.variant.BasisTrack;
import org.confluence.terraentity.registries.track.variant.SimpleTrack;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BeeGunBullet extends BeeProjectile {
    private ITrackType trackType = new BasisTrack(90, 0.3);

    public BeeGunBullet(Level level, @Nullable LivingEntity owner, boolean isGiant) {
        super(ModEntities.BEE_GUN_BULLET.get(), level, owner, isGiant);
    }

    public BeeGunBullet(EntityType<? extends BeeProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void trackTarget() {
        LivingEntity target = TEUtils.getAABBAngleTarget(position(), position().add(getDeltaMovement().normalize()), level(), getOwner(), 10, 180, this::canHitEntity);
        if (target == null) {
            setDeltaMovement(getDeltaMovement().normalize().scale(isGiant() ? 0.5 : 0.25));
        } else {
            Vec3 motion = getDeltaMovement();
            Vec3 dir = target.position().add(0, target.getEyeHeight() * 0.5, 0).subtract(position());
            double angle = TEUtils.angleBetween(motion, dir);
            if (angle < 90 && !(trackType instanceof SimpleTrack)) {
                this.trackType = new SimpleTrack(90, 0.5, isGiant() ? 0.5 : 0.25, Optional.of(0.5), 0.5);
            }
            setDeltaMovement(trackType.calDeltaMovement(getDeltaMovement(), dir, angle));
        }
    }

    @Override
    protected DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }
}
