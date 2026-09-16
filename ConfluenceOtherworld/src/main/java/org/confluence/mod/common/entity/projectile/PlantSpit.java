package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModParticleTypes;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public final class PlantSpit extends StraightMonsterProjectile {
    public enum Kind {CURSED_FLAME, SPORE}

    private final Kind kind;
    private ParticleEmitter emitter;

    public PlantSpit(EntityType<? extends PlantSpit> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || isRemoved()) return;
        if (kind == Kind.SPORE) {
            level().addParticle(ModParticleTypes.SPORE_CLOUD.get(), getX(), getY(), getZ(), 0.0, 0.0, 0.0);
        } else if (emitter == null) {
            emitter = new ParticleEmitter(level(), position(), Confluence.asResource("cursed_flames"));
            emitter.attachEntity(this);
            emitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (kind != Kind.SPORE || level().isClientSide || !(getOwner() instanceof Mob owner))
            return velocity;
        LivingEntity target = owner.getTarget();
        if (target == null || !target.isAlive() || !owner.canAttack(target)) return velocity;
        Vec3 desired = target.getEyePosition().subtract(position()).normalize().scale(0.15);
        if (LibUtils.isAtLeastExpert(level(), blockPosition()))
            desired = desired.add(Math.sin(tickCount * 0.4) * 0.05, Math.cos(tickCount * 0.3) * 0.05, 0.0);
        return velocity.lerp(desired, 0.08);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (kind == Kind.CURSED_FLAME && random.nextFloat() < 0.6875F) {
            int duration = LibUtils.isMaster(level(), blockPosition()) ? 100 + random.nextInt(51) : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 80 + random.nextInt(41) : 40 + random.nextInt(21);
            target.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO.get(), duration), owner);
        }
    }

    @Override
    public boolean isPickable() {
        return kind == Kind.SPORE && !isRemoved();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (kind != Kind.SPORE || amount <= 0.0F || isInvulnerableTo(source)) return false;
        if (!level().isClientSide) discard();
        return true;
    }
}
