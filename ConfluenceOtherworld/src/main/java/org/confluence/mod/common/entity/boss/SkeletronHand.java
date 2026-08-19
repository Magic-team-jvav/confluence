package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 骷髅王之手。绕头部轨道运行，周期性挥击玩家。
public class SkeletronHand extends BaseBossPart<Skeletron> implements GeoEntity {
    private static final int CLASSIC_SLAP_INTERVAL = 45;
    private static final int EXPERT_SLAP_INTERVAL = 30;
    private static final int RANDOM_INTERVAL = 6;
    private static final double CLASSIC_SLAP_SPEED = 1.0;
    private static final double EXPERT_SLAP_SPEED = 1.2;
    private static final double PREPARE_DISTANCE = 6.0;
    private static final double PASS_DISTANCE = 4.0;
    private static final double ARRIVAL_DISTANCE_SQUARED = 1.5;
    private static final float DAMAGE = 10.0F;
    private static final float MAX_PART_HEALTH = 405.0F;
    private static final float PART_ARMOR = 4.0F;
    private static final String HAND_INDEX_TAG = "HandIndex";
    private static final EntityDataAccessor<Integer> HAND_INDEX = SynchedEntityData.defineId(SkeletronHand.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int slapInterval;
    private double slapSpeed;
    private int slapTick;
    /// -1 表示待机，0 表示向后蓄势，1 表示穿过目标。
    private int slapPhase = -1;
    private Vec3 phaseTarget = Vec3.ZERO;

    public SkeletronHand(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(Skeletron master, int index) {
        if (index < 0 || index > 1) throw new IllegalArgumentException("Hand index must be 0 or 1");
        bindTo(master);
        this.entityData.set(HAND_INDEX, index);
        this.slapInterval = (master.isExpert()
                ? EXPERT_SLAP_INTERVAL : CLASSIC_SLAP_INTERVAL)
                + master.getRandom().nextInt(RANDOM_INTERVAL);
        this.slapSpeed = master.isExpert()
                ? EXPERT_SLAP_SPEED : CLASSIC_SLAP_SPEED;
        // 1.21 侧首次生成后立即进入一次挥击，而不是先等待完整冷却。
        this.slapTick = slapInterval;
    }

    public int getHandIndex() {
        return entityData.get(HAND_INDEX);
    }

    @Override
    protected void tickPart(Skeletron master) {
        if (level().isClientSide) return;

        if (slapPhase >= 0) {
            tickSlap(master);
        } else if (!master.isSpinning() && slapTick >= slapInterval) {
            beginSlap(master);
            tickSlap(master);
        } else {
            tickStandby(master);
        }
    }

    private void tickStandby(Skeletron master) {
        slapTick++;
        float yaw = master.yBodyRot * Mth.DEG_TO_RAD;
        double side = getHandIndex() == 0 ? 1.0 : -1.0;
        double vertical = master.isSpinning() ? 4.0 : -3.5;
        Vec3 target = master.position().add(Mth.cos(yaw) * 5.0 * side, vertical, Mth.sin(yaw) * 5.0 * side);
        moveToward(target, master.isExpert() ? 1.0 : 0.7);
    }

    private void beginSlap(Skeletron master) {
        LivingEntity target = master.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        Vec3 away = position().subtract(target.position());
        if (away.lengthSqr() <= 1.0E-7) {
            away = new Vec3(0.0, 0.0, 1.0);
        }
        phaseTarget = position().add(away.normalize().scale(PREPARE_DISTANCE));
        slapPhase = 0;
    }

    private void tickSlap(Skeletron master) {
        LivingEntity target = master.getTarget();
        if (target == null || !target.isAlive() || master.isSpinning()) {
            finishSlap();
            return;
        }
        if (position().distanceToSqr(phaseTarget) <= ARRIVAL_DISTANCE_SQUARED) {
            if (slapPhase == 0) {
                Vec3 through = target.position().subtract(position());
                if (through.lengthSqr() <= 1.0E-7) {
                    through = new Vec3(0.0, 0.0, 1.0);
                }
                phaseTarget = target.position().add(through.normalize().scale(PASS_DISTANCE));
                slapPhase = 1;
            } else {
                finishSlap();
                return;
            }
        }
        moveToward(phaseTarget, slapSpeed);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.5))) {
            if (entity != master && master.canAttack(entity)) {
                entity.hurt(damageSources().mobAttack(master), DAMAGE);
            }
        }
    }

    private void finishSlap() {
        slapPhase = -1;
        slapTick = 0;
        phaseTarget = Vec3.ZERO;
        setDeltaMovement(Vec3.ZERO);
    }

    private void moveToward(Vec3 target, double maximumSpeed) {
        Vec3 difference = target.subtract(position());
        if (difference.lengthSqr() <= 1.0E-7) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        Vec3 velocity = difference.normalize().scale(maximumSpeed);
        setDeltaMovement(velocity);
        setPos(getX() + velocity.x, getY() + velocity.y, getZ() + velocity.z);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Skeletron owner = getOwner();
        if (owner == null || !owner.isAlive() || isRemoved() || isInvulnerableTo(source))
            return false;
        float appliedDamage = source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount : CombatRules.getDamageAfterAbsorb(amount, PART_ARMOR, 0.0F);
        if (appliedDamage <= 0.0F) return false;
        float remaining = Math.max(0.0F, getPartHealth() - appliedDamage);
        setPartHealth(remaining);
        onPartHealthChanged(owner, remaining);
        if (remaining <= 0.0F) {
            onPartDestroyed(owner);
            discard();
        }
        return true;
    }

    @Override
    protected float getMaxPartHealth() {
        return MAX_PART_HEALTH;
    }

    @Override
    protected Class<Skeletron> getOwnerType() {
        return Skeletron.class;
    }

    @Override
    protected void onPartDestroyed(Skeletron owner) {
        owner.onHandDestroyed(getHandIndex(), this);
    }

    @Override
    protected void onPartHealthChanged(Skeletron owner, float remainingHealth) {
        owner.onHandHealthChanged(getHandIndex(), remainingHealth);
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(HAND_INDEX, 0);
    }

    @Override
    protected void readPartSaveData(CompoundTag tag) {
        entityData.set(HAND_INDEX, tag.getInt(HAND_INDEX_TAG));
        slapPhase = -1;
        slapTick = slapInterval;
        phaseTarget = Vec3.ZERO;
    }

    @Override
    protected void addPartSaveData(CompoundTag tag) {
        tag.putInt(HAND_INDEX_TAG, getHandIndex());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
