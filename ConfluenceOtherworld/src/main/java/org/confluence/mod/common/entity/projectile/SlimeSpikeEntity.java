package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class SlimeSpikeEntity extends AbstractHurtingProjectile {
    private static final String RUNTIME_KEY = "ConfluenceSlimeSpikeRuntime";
    private static final int RUNTIME_VERSION = 1;
    private static final int MAX_AGE = 80;

    private float damage = 5.0f;
    private boolean invalidRuntimeState;

    public SlimeSpikeEntity(EntityType<? extends SlimeSpikeEntity> type, Level level) {
        super(type, level);
    }

    public static SlimeSpikeEntity create(Level level, LivingEntity shooter,
                                          EntityType<? extends SlimeSpikeEntity> type,
                                          double dirX, double dirY, double dirZ,
                                          float velocity, float damage) {
        return create(level, shooter, type, dirX, dirY, dirZ, velocity, 0.0F, damage);
    }

    /// 创建史莱姆尖刺并显式指定散布值。普通工厂保留零散布，尖刺史莱姆则使用
    /// 1.21 的 {@code 1.0F} 散布，避免把所有调用方的弹道一并改变。
    public static SlimeSpikeEntity create(Level level, LivingEntity shooter,
                                          EntityType<? extends SlimeSpikeEntity> type,
                                          double dirX, double dirY, double dirZ,
                                          float velocity, float inaccuracy, float damage) {
        SlimeSpikeEntity spike = new SlimeSpikeEntity(type, level);
        spike.setOwner(shooter);
        spike.setPos(shooter.getX(), shooter.getY() + shooter.getEyeHeight() * 0.5, shooter.getZ());
        spike.shoot(dirX, dirY, dirZ, velocity, inaccuracy);
        spike.damage = damage;
        return spike;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            target.hurt(damageSources().mobProjectile(this,
                    getOwner() instanceof LivingEntity o ? o : null), damage);
        }
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }

    @Override
    public void tick() {
        if (!level().isClientSide && invalidRuntimeState) {
            discard();
            return;
        }
        super.tick();
        if (level().isClientSide) {
            level().addParticle(ParticleTypes.CRIT, getX(), getY(), getZ(), 0, 0, 0);
        }
        if (tickCount > MAX_AGE) {
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putFloat("Damage", damage);
        runtime.putInt("Age", tickCount);
        compound.put(RUNTIME_KEY, runtime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.invalidRuntimeState = true;
        if (!compound.contains(RUNTIME_KEY, Tag.TAG_COMPOUND)) return;
        CompoundTag runtime = compound.getCompound(RUNTIME_KEY);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("Damage", Tag.TAG_FLOAT)
                || !runtime.contains("Age", Tag.TAG_INT)) {
            return;
        }
        float savedDamage = runtime.getFloat("Damage");
        int savedAge = runtime.getInt("Age");
        if (!Float.isFinite(savedDamage) || savedDamage < 0.0F
                || savedAge < 0 || savedAge > MAX_AGE) {
            return;
        }
        this.damage = savedDamage;
        this.tickCount = savedAge;
        this.invalidRuntimeState = false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
