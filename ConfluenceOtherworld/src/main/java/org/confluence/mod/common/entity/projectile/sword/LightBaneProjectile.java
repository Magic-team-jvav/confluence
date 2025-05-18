package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModParticleTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LightBaneProjectile extends SwordProjectile<LightBaneProjectile> {

    List<Entity> hits = new ArrayList<>();

    public LightBaneProjectile(EntityType<LightBaneProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        hitCount = 99999;

    }

    @Override
    public void tick() {
        super.tick();
        if(direction!=null) {
            float f = 10;
            float control = Math.min(Math.abs(tickCount - f), f) * (tickCount < f? -0.02f : 0.02f);
            this.setDeltaMovement(direction.normalize().scale(control));
            lookAt(EntityAnchorArgument.Anchor.EYES , getEyePosition().subtract(direction));
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return  !hits.contains(target) && super.canHitEntity(target);
    }

    @Override
    protected boolean doHurt(Entity target) {
        if(super.doHurt(target)){
            hits.add(target);
            ((ServerLevel) level()).sendParticles(ModParticleTypes.LIGHT_BANE.get(),getX(),getY(),getZ(),1,0,0,0,0);
            return true;
        }
        return false;
    }

    @Override
    public DamageSource damageSource(){
        return super.damageSource();
    }

    @Nullable
    protected ParticleOptions getTrailParticle() {
        return random.nextBoolean() ? ModParticleTypes.LIGHT_BANE_FADE.get() : ModParticleTypes.LIGHT_BANE_DUST.get();
    }
}
