package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.map.CreatureDefinition.ProjectileOverrides;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public class SlimeSpikeEntity extends AbstractHurtingProjectile {
    private static final int MAX_AGE = 30;
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(SlimeSpikeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_GRAVITY = SynchedEntityData.defineId(SlimeSpikeEntity.class, EntityDataSerializers.BOOLEAN);

    private float damage = 5.0f;

    public SlimeSpikeEntity(EntityType<? extends SlimeSpikeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_VARIANT, Variant.NORMAL.ordinal());
        entityData.define(DATA_HAS_GRAVITY, true);
    }

    public static SlimeSpikeEntity create(Level level, LivingEntity shooter, EntityType<? extends SlimeSpikeEntity> type, double dirX, double dirY, double dirZ, float velocity, float damage) {
        return create(level, shooter, type, dirX, dirY, dirZ, velocity, 0.0F, damage);
    }

    /// 创建史莱姆尖刺并显式指定散布值。普通工厂保留零散布，尖刺史莱姆自行传入散布，避免把所有调用方的弹道一并改变。
    public static SlimeSpikeEntity create(Level level, LivingEntity shooter, EntityType<? extends SlimeSpikeEntity> type, double dirX, double dirY, double dirZ, float velocity, float inaccuracy, float damage) {
        return create(level, shooter, type, dirX, dirY, dirZ, velocity, inaccuracy, damage, Variant.NORMAL, true);
    }

    public static SlimeSpikeEntity create(Level level, LivingEntity shooter, EntityType<? extends SlimeSpikeEntity> type, double dirX, double dirY, double dirZ, float velocity, float inaccuracy, float damage, Variant variant, boolean hasGravity) {
        SlimeSpikeEntity spike = new SlimeSpikeEntity(type, level);
        spike.setOwner(shooter);
        spike.setPos(shooter.getX(), shooter.getY() + shooter.getEyeHeight() * 0.5, shooter.getZ());
        spike.shoot(dirX, dirY, dirZ, velocity, inaccuracy);
        spike.damage = shooter instanceof Mob mob ? ProjectileOverrides.get(mob, type).damageOr(damage) : damage;
        spike.entityData.set(DATA_VARIANT, variant.ordinal());
        spike.entityData.set(DATA_HAS_GRAVITY, hasGravity);
        return spike;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide && result.getEntity() instanceof LivingEntity target
                && getOwner() instanceof Mob owner && owner.canAttack(target)
                && target.hurt(damageSources().mobProjectile(this, owner), damage)) {
            if (getVariant() == Variant.JUNGLE) {
                if (random.nextInt(8) < 5) {
                    int baseDuration = random.nextBoolean() ? 100 : 400;
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, scaledDuration(baseDuration)), owner);
                }
            } else if (getVariant() == Variant.ICE) {
                if (!TCUtils.hasType(target, TCItems.FROZEN$IMMUNE)) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, scaledDuration(400)), owner);
                    boolean expert = LibUtils.isAtLeastExpert(level(), blockPosition());
                    if (!expert) {
                        if (random.nextInt(20) == 0) {
                            target.addEffect(new MobEffectInstance(ModEffects.FROZEN.get(), 30), owner);
                        }
                    } else {
                        int roll = random.nextInt(400);
                        if (roll < 20) {
                            target.addEffect(new MobEffectInstance(ModEffects.FROZEN.get(), LibUtils.isMaster(level(), blockPosition()) ? 75 : 60), owner);
                        } else if (roll < 39) {
                            target.addEffect(new MobEffectInstance(ModEffects.FROZEN.get(), LibUtils.isMaster(level(), blockPosition()) ? 50 : 40), owner);
                        }
                    }
                }
            }
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
        super.tick();
        if (entityData.get(DATA_HAS_GRAVITY)) {
            setDeltaMovement(getDeltaMovement().add(0.0, -0.108, 0.0));
        }
        if (level().isClientSide) {
            level().addParticle(ParticleTypes.CRIT, getX(), getY(), getZ(), 0, 0, 0);
        }
        if (tickCount > MAX_AGE) {
            discard();
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public Variant getVariant() {
        return Variant.values()[entityData.get(DATA_VARIANT)];
    }

    private int scaledDuration(int classicTicks) {
        if (LibUtils.isMaster(level(), blockPosition())) return classicTicks * 5 / 2;
        return LibUtils.isAtLeastExpert(level(), blockPosition()) ? classicTicks * 2 : classicTicks;
    }

    public enum Variant {NORMAL, JUNGLE, ICE}
}
