package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.MartianReinforcement;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.init.entity.ModEntities;

import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/// 由可再生护盾保护的战士
public final class MartianOfficer extends MartianHumanoidMonster {
    /// 护盾没有专家生命加成：1000 × 26%；防御 20 分别换算为护甲 9、韧性 4。
    public static final float MAX_SHIELD = 260.0F;
    public static final float SHIELD_ARMOR = 9.0F;
    public static final float SHIELD_TOUGHNESS = 4.0F;
    public static final int RECOVERY_TICKS = 60;
    private static final EntityDataAccessor<Float> SHIELD = SynchedEntityData.defineId(MartianOfficer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> IMPACT_SEQUENCE = SynchedEntityData.defineId(MartianOfficer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CALLING = SynchedEntityData.defineId(MartianOfficer.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation CALL_FOR_ASSISTANCE = RawAnimation.begin().thenLoop("Call for Assistance");
    private int assistanceCooldown;
    private int callingTicks;
    private int populationCheckCooldown;
    private boolean prefersAssistance;
    private int visualShieldTicks = 20;
    private int visualBrokenTicks = 20;
    private int visualImpactTicks = 20;
    private float previousShieldHealth = MAX_SHIELD;
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation DASH = RawAnimation.begin().thenLoop("Dash");
    private int recoveryTicks;
    private int shieldHitTicks;
    private float lastShieldDamage;
    private int contactCooldown;

    public MartianOfficer(EntityType<? extends MartianOfficer> type, Level level) {
        super(type, level, 0.05D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHIELD, MAX_SHIELD);
        builder.define(IMPACT_SEQUENCE, 0);
        builder.define(CALLING, false);
    }

    public boolean isCallingForAssistance() {
        return entityData.get(CALLING);
    }

    @Override
    protected BTNode createLandBehavior() {
        return new ConditionalSwitchNode(() -> prefersAssistance || isCallingForAssistance(), new BTNode() {
            @Override
            public BTStatus execute() {
                return BTStatus.RUNNING;
            }

            @Override
            public void stop() {
                getNavigation().stop();
            }
        }, super.createLandBehavior());
    }

    private void tickAssistance() {
        if (assistanceCooldown > 0) {
            assistanceCooldown--;
        }
        LivingEntity target = getTarget();
        if (!(level() instanceof ServerLevel server) || !(target instanceof Player player)
                || !player.isAlive() || !canAttack(player) || isNoAi() || isPassenger()) {
            prefersAssistance = false;
            callingTicks = 0;
            entityData.set(CALLING, false);
            return;
        }
        if (callingTicks > 0) {
            callingTicks--;
            stopForAssistance();
            getLookControl().setLookAt(player, 30, 30);
            entityData.set(CALLING, callingTicks > 0);
            return;
        }
        if (populationCheckCooldown-- <= 0) {
            prefersAssistance = MartianReinforcement.hasCapacity(server);
            populationCheckCooldown = 20;
        }
        if (!prefersAssistance) {
            return;
        }
        double distance = position().subtract(player.position()).horizontalDistance();
        if (distance < 16) {
            if (tickCount % 10 == 0 || getNavigation().isDone()) {
                Vec3 away = position().subtract(player.position()).multiply(1, 0, 1);
                if (away.lengthSqr() < 0.01) {
                    away = new Vec3(1, 0, 0);
                }
                Vec3 destination = position().add(away.normalize().scale(8));
                if (!getNavigation().moveTo(destination.x, getY(), destination.z, 1.2)) {
                    /// 无法部署增援时继续近战追击，避免原地持续等待。
                    prefersAssistance = false;
                    populationCheckCooldown = 40;
                }
            }
            return;
        }
        if (!hasLineOfSight(player)) {
            prefersAssistance = false;
            populationCheckCooldown = 20;
            return;
        }
        stopForAssistance();
        getLookControl().setLookAt(player, 30, 30);
        if (assistanceCooldown > 0) {
            return;
        }
        /// 立即复查名额，其他军官可能已经在本刻预留最后一个位置。
        if (!MartianReinforcement.hasCapacity(server)) {
            prefersAssistance = false;
            return;
        }
        MartianReinforcement drop = ModEntities.MARTIAN_REINFORCEMENT.get().create(server);
        if (drop != null) {
            drop.initialize(this, player.position());
            if (server.addFreshEntity(drop)) {
                callingTicks = MartianReinforcement.WARNING_TICKS;
                entityData.set(CALLING, true);
                assistanceCooldown = 100;
            }
        }
    }

    private void stopForAssistance() {
        getNavigation().stop();
        getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0);
        setSpeed(0);
        setSprinting(false);
        setDeltaMovement(0, getDeltaMovement().y, 0);
    }

    public float getShieldHealth() {
        return entityData.get(SHIELD);
    }

    public boolean hasShield() {
        return getShieldHealth() > 0.0F;
    }

    public float shieldFormation(float partialTick) {
        return Math.min(1, (visualShieldTicks + partialTick) / 12.0F);
    }

    public float shieldDissolve(float partialTick) {
        return Math.min(1, (visualBrokenTicks + partialTick) / 8.0F);
    }

    public float shieldImpactAge(float partialTick) {
        return visualImpactTicks + partialTick;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (!level().isClientSide) {
            return;
        }
        if (key.equals(IMPACT_SEQUENCE)) {
            visualImpactTicks = 0;
        }
        if (key.equals(SHIELD)) {
            float health = getShieldHealth();
            if (health <= 0 && previousShieldHealth > 0) {
                visualBrokenTicks = 0;
            }
            if (health > 0 && previousShieldHealth <= 0) {
                visualShieldTicks = 0;
            }
            previousShieldHealth = health;
        }
    }

    private void showShieldImpact() {
        entityData.set(IMPACT_SEQUENCE, entityData.get(IMPACT_SEQUENCE) + 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            visualShieldTicks = Math.min(20, visualShieldTicks + 1);
            visualBrokenTicks = Math.min(20, visualBrokenTicks + 1);
            visualImpactTicks = Math.min(20, visualImpactTicks + 1);
            return;
        }
        if (!isAlive()) {
            return;
        }
        if (shieldHitTicks > 0) {
            shieldHitTicks--;
        }
        if (!hasShield() && ++recoveryTicks >= RECOVERY_TICKS) {
            entityData.set(SHIELD, MAX_SHIELD);
            recoveryTicks = 0;
            shieldHitTicks = 0;
        }
        if (hasShield()) {
            clearFire();
        }
        tickAssistance();
        tickOfficerContact();
    }

    @Override
    protected MeleeAttackGoal createMeleeGoal(double speed) {
        return new MeleeAttackGoal(this, speed, true) {
            @Override
            protected void checkAndPerformAttack(LivingEntity target) {
                /// 仅用于寻路：伤害完全由下方的物理接触判定造成。
            }
        };
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return isPerformingContactAttack() && super.doHurtTarget(target);
    }

    private void tickOfficerContact() {
        if (isNoAi() || isPassenger()) return;
        if (getVehicle() instanceof MartianReinforcement) {
            return;
        }
        if (contactCooldown > 0) {
            contactCooldown--;
        }
        if (contactCooldown > 0) {
            return;
        }
        Vec3 previous = new Vec3(xo, yo, zo);
        if (previous.distanceToSqr(position()) > 256) {
            previous = position();
        }
        final Vec3 start = previous;
        var targets = hasShield()
                ? level().getEntities(this, OfficerShieldGeometry.bounds(start, getScale())
                .minmax(OfficerShieldGeometry.bounds(position(), getScale())), entity -> canContactAttack(entity)
                && OfficerShieldGeometry.intersects(start, position(), getScale(), entity.getBoundingBox()))
                : SweptContactAttack.findTargets(this, previous, 0, maximumContactSweepDistance(), this::canContactAttack);
        boolean hit = false;
        for (Entity target : targets) {
            hit |= doContactHurtTarget(target);
        }
        if (hit) {
            contactCooldown = contactAttackInterval();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!Float.isFinite(amount) || amount <= 0.0F || EnemyDamageRules.blocks(this, source)) {
            return false;
        }
        if (!hasShield() || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(source, amount);
        }
        if (level().isClientSide || !isAlive() || isInvulnerableTo(source)) {
            return false;
        }
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        float damage = source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount
                : CombatRules.getDamageAfterAbsorb(this, amount, source,
                LibAttributes.applyArmorPenetration(this, source, SHIELD_ARMOR), SHIELD_TOUGHNESS);
        if (shieldHitTicks > 10) {
            if (damage <= lastShieldDamage) {
                return false;
            }
            float previous = lastShieldDamage;
            lastShieldDamage = damage;
            damage -= previous;
        } else {
            lastShieldDamage = damage;
            shieldHitTicks = 20;
        }
        entityData.set(SHIELD, Math.max(0.0F, getShieldHealth() - damage));
        showShieldImpact();
        recoveryTicks = 0;
        if (source.getEntity() instanceof LivingEntity attacker) {
            setLastHurtByMob(attacker);
        }
        level().broadcastEntityEvent(this, (byte) 2);
        return true;
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasShield()) {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public boolean isPushable() {
        return !hasShield() && super.isPushable();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return !hasShield() && super.canBeAffected(effect);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<MartianOfficer> controller = new AnimationController<>(this, "officer", 4, state -> {
            if (isCallingForAssistance()) {
                return state.setAndContinue(CALL_FOR_ASSISTANCE);
            }
            RawAnimation movement = isSprinting() ? DASH : WALK;
            return state.setAndContinue(state.isMoving() ? movement : STAND);
        });
        controllers.add(controller);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Shield", getShieldHealth());
        tag.putInt("ShieldRecoveryTicks", recoveryTicks);
        tag.putInt("AssistanceCooldown", assistanceCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        float shield = tag.contains("Shield") ? tag.getFloat("Shield") : MAX_SHIELD;
        entityData.set(SHIELD, Float.isFinite(shield) ? Math.max(0, Math.min(MAX_SHIELD, shield)) : MAX_SHIELD);
        recoveryTicks = hasShield() ? 0 : Math.max(0, Math.min(RECOVERY_TICKS - 1, tag.getInt("ShieldRecoveryTicks")));
        shieldHitTicks = 0;
        assistanceCooldown = Math.max(0, Math.min(100, tag.getInt("AssistanceCooldown")));
        callingTicks = 0;
        prefersAssistance = false;
        populationCheckCooldown = 0;
        entityData.set(CALLING, false);
    }
}
