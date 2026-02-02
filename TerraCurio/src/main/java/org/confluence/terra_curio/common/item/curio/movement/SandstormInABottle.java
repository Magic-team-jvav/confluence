package org.confluence.terra_curio.common.item.curio.movement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.terra_curio.client.handler.GravitationHandler;
import org.confluence.terra_curio.client.handler.PlayerJumpHandler;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class SandstormInABottle extends BaseCurioItem {
    public SandstormInABottle(Builder builder) {
        super(builder);
    }

    @Override
    protected void particleTick(LivingEntity living, ParticleEmitter emitter, ResourceLocation particle) {
        super.particleTick(living, emitter, particle);
        emitter.active = PlayerJumpHandler.isOnSandstormJump;

        if (emitter.active) {
            if (GravitationHandler.isShouldRot(living)) {
                emitter.offsetPos = new Vec3(0, living.getBbHeight(), 0);
            } else {
                emitter.offsetPos = Vec3.ZERO;
            }
        }
    }
}
