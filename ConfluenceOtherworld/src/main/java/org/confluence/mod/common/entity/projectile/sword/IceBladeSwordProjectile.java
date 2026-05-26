package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class IceBladeSwordProjectile extends ForwardSwordProjectile {
    private ParticleEmitter emitter;

    public IceBladeSwordProjectile(EntityType<? extends ForwardSwordProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && emitter == null) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("ball_of_frost_trail"));
            emitter.attachEntity(this);
            emitter.hideOutline = true;
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
    }
}
