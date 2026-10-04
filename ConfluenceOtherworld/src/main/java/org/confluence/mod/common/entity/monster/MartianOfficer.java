package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/** 由可再生护盾保护的战士 */
public final class MartianOfficer extends BaseWarriorMonster {
    public static final float MAX_SHIELD = 200.0F;
    public static final int RECOVERY_TICKS = 60;
    private static final EntityDataAccessor<Float> SHIELD = SynchedEntityData.defineId(MartianOfficer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> IMPACT_SEQUENCE = SynchedEntityData.defineId(MartianOfficer.class, EntityDataSerializers.INT);
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

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHIELD, MAX_SHIELD);
        builder.define(IMPACT_SEQUENCE, 0);
    }

    public float getShieldHealth() { return entityData.get(SHIELD); }
    public boolean hasShield() { return getShieldHealth() > 0.0F; }
    public float shieldFormation(float partialTick) { return Math.min(1, (visualShieldTicks + partialTick) / 12.0F); }
    public float shieldDissolve(float partialTick) { return Math.min(1, (visualBrokenTicks + partialTick) / 8.0F); }
    public float shieldImpactAge(float partialTick) { return visualImpactTicks + partialTick; }

    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (!level().isClientSide) return;
        if (key.equals(IMPACT_SEQUENCE)) visualImpactTicks = 0;
        if (key.equals(SHIELD)) {
            float health = getShieldHealth();
            if (health <= 0 && previousShieldHealth > 0) visualBrokenTicks = 0;
            if (health > 0 && previousShieldHealth <= 0) visualShieldTicks = 0;
            previousShieldHealth = health;
        }
    }

    private void showShieldImpact() {
        entityData.set(IMPACT_SEQUENCE, entityData.get(IMPACT_SEQUENCE) + 1);
    }

    @Override public void tick() {
        super.tick();
        if (level().isClientSide) {
            visualShieldTicks = Math.min(20, visualShieldTicks + 1);
            visualBrokenTicks = Math.min(20, visualBrokenTicks + 1);
            visualImpactTicks = Math.min(20, visualImpactTicks + 1);
            return;
        }
        // TODO 火星事件本体（MartianEventHelper）尚未移植，事件结束后本实体不会自动消失，待事件批次补回
        if (level().isClientSide || !isAlive()) return;
        if (shieldHitTicks > 0) shieldHitTicks--;
        if (!hasShield() && ++recoveryTicks >= RECOVERY_TICKS) {
            entityData.set(SHIELD, MAX_SHIELD);
            recoveryTicks = 0;
            shieldHitTicks = 0;
        }
        if (hasShield()) clearFire();
        tickOfficerContact();
    }

    @Override protected MeleeAttackGoal createMeleeGoal(double speed) {
        return new MeleeAttackGoal(this, speed, true) {
            @Override protected void checkAndPerformAttack(LivingEntity target) {
                // 仅用于寻路：伤害完全由下方的物理接触判定造成。
            }
        };
    }

    @Override public boolean doHurtTarget(Entity target) {
        return isPerformingContactAttack() && super.doHurtTarget(target);
    }

    private void tickOfficerContact() {
        if (contactCooldown > 0) contactCooldown--;
        Vec3 previous = new Vec3(xo, yo, zo);
        if (previous.distanceToSqr(position()) > 256) previous = position();
        final Vec3 start = previous;
        var targets = hasShield()
                ? level().getEntities(this, OfficerShieldGeometry.bounds(start, getScale())
                        .minmax(OfficerShieldGeometry.bounds(position(), getScale())), entity -> canContactAttack(entity)
                        && OfficerShieldGeometry.intersects(start, position(), getScale(), entity.getBoundingBox()))
                : SweptContactAttack.findTargets(this, previous, 0, maximumContactSweepDistance(), this::canContactAttack);
        boolean hit = false;
        for (Entity target : targets) {
            if (contactCooldown == 0) hit |= doContactHurtTarget(target);
        }
        if (hit) contactCooldown = contactAttackInterval();
    }

    @Override public boolean hurt(DamageSource source, float amount) {
        if (!hasShield() || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return super.hurt(source, amount);
        if (level().isClientSide || !isAlive() || isInvulnerableTo(source) || !(amount > 0.0F)) return false;
        if (source.is(DamageTypeTags.IS_FIRE)) return false;
        // 护盾防御 20 -> 4，采用泰拉瑞亚的半数防御固定减伤。
        float damage = source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount : Math.max(0.2F, amount - 2.0F);
        if (shieldHitTicks > 10) {
            if (damage <= lastShieldDamage) return false;
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
        if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) setLastHurtByMob(attacker);
        level().broadcastEntityEvent(this, (byte) 2);
        return true;
    }

    @Override public void knockback(double strength, double x, double z) {
        if (!hasShield()) super.knockback(strength, x, z);
    }
    @Override public boolean isPushable() { return !hasShield() && super.isPushable(); }
    @Override public boolean canBeAffected(MobEffectInstance effect) {
        return !hasShield() && super.canBeAffected(effect);
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "officer", 4, state -> state.setAndContinue(
                state.isMoving() ? (isSprinting() ? DASH : WALK) : STAND)));
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Shield", getShieldHealth());
        tag.putInt("ShieldRecoveryTicks", recoveryTicks);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        float shield = tag.contains("Shield") ? tag.getFloat("Shield") : MAX_SHIELD;
        entityData.set(SHIELD, Float.isFinite(shield) ? Math.max(0, Math.min(MAX_SHIELD, shield)) : MAX_SHIELD);
        recoveryTicks = hasShield() ? 0 : Math.max(0, Math.min(RECOVERY_TICKS - 1, tag.getInt("ShieldRecoveryTicks")));
        shieldHitTicks = 0;
    }
}
