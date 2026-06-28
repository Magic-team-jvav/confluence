package org.confluence.mod.common.entity.projectile.spear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModParticleTypes;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;

/**
 * <h1>北极矛子弹射物</h1>
 * 由 {@link NorthPoleProjectile} 间歇生成，初始速度为零，受重力下落，使用孢子云粒子渲染。
 */
public class NorthPoleSubProjectile extends SpearProjectile {
    public NorthPoleSubProjectile(EntityType<? extends NorthPoleSubProjectile> entityType, Level level) {
        super(entityType, level);
        this.collisionProperties = new CollisionProperties(1, 1, 0.65F);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) {
            EmitterCreationPacketS2C.sendToAll(
                    Confluence.asResource("spore_cloud"),
                    position().toVector3f(),
                    MolangExp.EMPTY,
                    this);
        }
    }

    @Override
    protected void updateMotion() {
        // 仅受重力影响，不做额外运动计算
    }

    @Override
    protected Vec3 initVelocity(LivingEntity owner, Vec3 direction, float speed) {
        return Vec3.ZERO;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticleTypes.SNOW.get(),
                    getX(), getY(), getZ(), 40, 0.2, 0.2, 0.2, 0.01);
        }
    }
}
