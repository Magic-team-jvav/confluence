package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.ModEntities;
import org.joml.Vector3f;
import org.mesdag.particlestorm.data.MathHelper;

public class SmokeBombEntity extends BaseBombEntity {
    public SmokeBombEntity(EntityType<? extends BaseBombEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public SmokeBombEntity(LivingEntity pShooter) {
        super(ModEntities.SMOKE_BOMB_ENTITY.get(), pShooter);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            for (int i = 0; i < 60; i++) {
                Vector3f euler = MathHelper.getRandomEuler(random);
                Vector3f rotated = new Vector3f(0, 1, 0).rotateX(euler.x).rotateY(euler.y).rotateZ(euler.z).mul(0.2F);
                level().addParticle(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), rotated.x, rotated.y, rotated.z);
            }
        }
    }

    @Override
    protected void explodeFunction(ServerLevel level) {}

    @Override
    protected void createEmitter() {}
}
