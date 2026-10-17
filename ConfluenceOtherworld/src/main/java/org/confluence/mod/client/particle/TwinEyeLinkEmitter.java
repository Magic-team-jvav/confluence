package org.confluence.mod.client.particle;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.mesdag.particlestorm.data.molang.compiler.value.Variable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

/// 一个发射器维护双眼端点，已有粒子通过参数化运动跟随连线伸缩。
public final class TwinEyeLinkEmitter extends ParticleEmitter {
    private final AbstractTwinEye owner;
    private AbstractTwinEye partner;
    private int searchTicks;
    private int nextParticle;
    private Vec3 direction = Vec3.ZERO;

    public TwinEyeLinkEmitter(AbstractTwinEye owner) {
        super(owner.level(), owner.getBoundingBox().getCenter(), Confluence.asResource("twins_link"));
        this.owner = owner;
        vars.setValue("variable.link_x", instance -> (float) direction.x);
        vars.setValue("variable.link_y", instance -> (float) direction.y);
        vars.setValue("variable.link_z", instance -> (float) direction.z);
        vars.setValue("variable.link_active", instance -> active && !isRemoved() ? 1 : 0);
        vars.setValue("variable.link_t", instance -> {
            float fraction = nextParticle++ / (float) Math.max(1, spawnRate - 1);
            instance.getVars().setValue("variable.link_t", new Variable("variable.link_t", fraction));
            return fraction;
        });
        hideOutline = true;
    }

    @Override
    public void tick() {
        if (!owner.isAlive() || owner.isRemoved()) {
            remove();
            return;
        }
        if (partner == null || partner.isRemoved()) {
            if (searchTicks-- <= 0) {
                searchTicks = 10;
                partner = owner.getPartner();
            }
        }
        active = partner != null && partner.isAlive() && !partner.isRemoved();
        direction = active ? partner.getBoundingBox().getCenter().subtract(owner.getBoundingBox().getCenter()) : Vec3.ZERO;
        if (!active) spawned = false;
        if (!spawned) nextParticle = 0;
        super.tick();
        setPos(owner.getBoundingBox().getCenter());
        var particles = MolangParticleEngine.INSTANCE.getParticlesForEmitter(this);
        if (active && particles != null) {
            // 复用同一批粒子，连接失效时由资源中的存活条件统一清理。
            for (var particle : particles) particle.self().setLifetime(particle.getAge() + 20);
        }
    }
}
