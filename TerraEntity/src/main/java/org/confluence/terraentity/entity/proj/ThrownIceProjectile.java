package org.confluence.terraentity.entity.proj;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ThrownIceProjectile extends BaseProj<ThrownIceProjectile> {

    public final Vector3f axis;
    public final float rotSpeed;
    public int lifetime;
    public ThrownIceProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, new MobEffectInstance(TEEffects.FROST_BURN, 100));
        axis = new Vector3f(
                this.level().random.nextFloat() - 0.5f,
                this.level().random.nextFloat() - 0.5f,
                this.level().random.nextFloat() - 0.5f);
        rotSpeed = this.level().random.nextFloat() * 0.3f;
        lifetime = 200;
    }

    @Override
    public void tick() {
        super.tick();
        this.applyGravity();
        double len = this.getDeltaMovement().length();
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(Math.min(len, 1)* 0.98F) );
    }

    @Override
    protected double getDefaultGravity() {
        return 0.108f;
    }

    @Override
    public int getLifetime() {
        return lifetime;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        Vec3 dir = TEUtils.sphere(this.random.nextFloat() * 0.3f + 0.3f, this.random.nextFloat() * 6.28f, this.random.nextFloat() * 0.3f);
//        Vec3 dir = TEUtils.sphere(0.5f, 0.3f, 3.14f);
        this.addDeltaMovement(dir.add(0,1,0));
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(!level().isClientSide()) {
            ((ServerLevel)level()).sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY(), this.getZ(),
                    50, 0.3, 0.3, 0.3, 0.2);
        }
    }
}
