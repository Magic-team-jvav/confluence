package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.confluence.lib.common.LibEffects;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.projectile.MartianElectricBolt;
import org.confluence.mod.common.gameevent.MartianEventHelper;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.UUID;

/// 固定炮塔装配两秒后开火，工程师死亡不会销毁已经部署的炮塔。
public final class TeslaTurret extends BaseWarriorMonster {
    private static final int BUILD_TICKS = 40;
    private static final int FIRE_INTERVAL = 60;
    private static final double SHOT_RANGE_SQUARED = 36.0D * 36.0D;
    private static final int HIT_DELAY = 30;
    private static final EntityDataAccessor<Integer> DATA_BUILD_TICKS = SynchedEntityData.defineId(TeslaTurret.class, EntityDataSerializers.INT);
    private static final RawAnimation EXPAND = RawAnimation.begin().thenPlayAndHold("Expand");
    private static final RawAnimation READY = RawAnimation.begin().thenLoop("Static Expand");
    private int buildTicks;
    private int fireCooldown;
    private @Nullable UUID engineerUUID;

    public TeslaTurret(EntityType<? extends TeslaTurret> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect != null
                && effect.getEffect() != MobEffects.POISON
                && !effect.is(ModEffects.ACID_VENOM)
                && !effect.is(ModEffects.BLEEDING)
                && !effect.is(ModEffects.BLOOD_BUTCHERED)
                && !effect.is(LibEffects.CONFUSED)
                && !effect.is(ModEffects.SHIMMER)
                && super.canBeAffected(effect);
    }

    public void bindEngineer(MartianEngineer engineer) {
        if (engineer != null) engineerUUID = engineer.getUUID();
    }

    public @Nullable UUID getEngineerUUID() {
        return engineerUUID;
    }

    public int getBuildTicks() {
        return level().isClientSide ? entityData.get(DATA_BUILD_TICKS) : buildTicks;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BUILD_TICKS, 0);
    }

    @Override
    protected BTNode createLandBehavior() {
        return new BTNode() {
            @Override
            public boolean canStart() {
                return !isPassenger();
            }

            @Override
            public BTStatus execute() {
                if (!canStart()) return BTStatus.FAILURE;
                tickTurret((ServerLevel) level());
                return BTStatus.RUNNING;
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "turret", 0, state -> state.setAndContinue(
                getBuildTicks() < BUILD_TICKS ? EXPAND : READY)));
    }

    /// 固定炮塔的常驻行为负责装配计时、锁定和开火。
    private void tickTurret(ServerLevel serverLevel) {
        setDeltaMovement(Vec3.ZERO);
        getNavigation().stop();
        if (fireCooldown > 0) fireCooldown--;
        LivingEntity target = getTarget();
        if (target != null) faceCombatDirection(target.getEyePosition().subtract(getEyePosition()), 18.0F, 18.0F);
        if (buildTicks < BUILD_TICKS) {
            entityData.set(DATA_BUILD_TICKS, ++buildTicks);
            return;
        }
        if (target != null && target.isAlive() && canAttack(target) && distanceToSqr(target) <= SHOT_RANGE_SQUARED
                && fireCooldown == 0 && hasLineOfSight(target)) {
            fire(serverLevel, target);
            fireCooldown = FIRE_INTERVAL;
        }
    }

    private void fire(ServerLevel level, LivingEntity target) {
        if (!ModEntities.MARTIAN_ELECTRIC_BOLT.isBound()) return;
        EntityType<MartianElectricBolt> type = ModEntities.MARTIAN_ELECTRIC_BOLT.get();
        MartianElectricBolt bolt = type.create(level);
        if (bolt == null) return;
        bolt.configureShot(this, target);
        MartianEventHelper.inheritEventMembership(this, bolt);
        if (!level.addFreshEntity(bolt)) bolt.discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getBuildTicks() < BUILD_TICKS) return false;
        boolean damaged = super.hurt(source, amount);
        if (damaged && isAlive()) fireCooldown = Math.min(Integer.MAX_VALUE - HIT_DELAY, fireCooldown) + HIT_DELAY;
        return damaged;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return !isNoAi() && !isPassenger();
    }

    @Override
    public void travel(Vec3 input) {
        setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public void push(double x, double y, double z) {}

    @Override
    public void push(Entity entity) {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BuildTicks", buildTicks);
        tag.putInt("FireCooldown", fireCooldown);
        if (engineerUUID != null) tag.putUUID("Engineer", engineerUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        buildTicks = tag.contains("BuildTicks") ? Mth.clamp(tag.getInt("BuildTicks"), 0, BUILD_TICKS) : 0;
        fireCooldown = tag.contains("FireCooldown") ? Math.max(0, tag.getInt("FireCooldown")) : 0;
        engineerUUID = tag.hasUUID("Engineer") ? tag.getUUID("Engineer") : null;
        entityData.set(DATA_BUILD_TICKS, buildTicks);
    }
}
