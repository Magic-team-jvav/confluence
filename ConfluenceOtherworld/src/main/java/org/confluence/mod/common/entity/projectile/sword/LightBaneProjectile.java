package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModParticleTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LightBaneProjectile extends SwordProjectile {
    private final Set<UUID> hitTargets = new HashSet<>();

    public LightBaneProjectile(EntityType<LightBaneProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        remainingHits = 99999;
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved() || direction.lengthSqr() <= 1.0E-8) return;
        float midpoint = 10.0F;
        float speed = Math.min(Math.abs(tickCount - midpoint), midpoint) * (tickCount < midpoint ? -0.02F : 0.02F);
        setDeltaMovement(direction.normalize().scale(speed));
        lookAt(EntityAnchorArgument.Anchor.EYES, getEyePosition().subtract(direction));
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity identity = ProjectileHitRules.dedupeIdentity(target);
        return !hitTargets.contains(identity.getUUID()) && super.canHitEntity(target);
    }

    @Override
    protected boolean hurtTarget(Entity target) {
        Entity identity = ProjectileHitRules.dedupeIdentity(target);
        if (!super.hurtTarget(target)) return false;
        hitTargets.add(identity.getUUID());
        ((ServerLevel) level()).sendParticles(ModParticleTypes.LIGHT_BANE.get(), getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        return true;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return random.nextBoolean() ? ModParticleTypes.LIGHT_BANE_FADE.get() : ModParticleTypes.LIGHT_BANE_DUST.get();
    }
}
