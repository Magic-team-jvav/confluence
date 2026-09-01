package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 世纪之花触手——从方块表面伸出攻击玩家，短时间后消失。
public class PlanteraTentacle extends BaseBossPart<Plantera> implements GeoEntity {
    private static final String SLOT_TAG = "Slot";
    private static final float DAMAGE = 15.6F;
    private static final float MAX_HEALTH = 260.0F;
    private static final float ARMOR = 20.0F;
    private static final double DISTANCE_FROM_ANCHOR = 6.0;
    private static final double RADIAL_STEP = 0.15;
    private static final double TARGET_ATTRACTION_RADIUS = 24.0;
    private static final double TARGET_ATTRACTION = 0.25;
    private static final double TENTACLE_REPULSION = 2.5;
    private static final int CONTACT_COOLDOWN = 20;

    private static final EntityDataAccessor<Integer> SLOT = SynchedEntityData.defineId(PlanteraTentacle.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int contactCooldown;

    public PlanteraTentacle(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    /// 将新建触手绑定到指定槽位。槽位必须由世纪之花分配，外部调用者不应自行复用。
    public void setMaster(Plantera master, int slot) {
        entityData.set(SLOT, Mth.clamp(slot, 0, Plantera.TENTACLE_COUNT - 1));
        bindTo(master);
        master.bindTentacle(this);
    }

    public int getSlot() {
        return entityData.get(SLOT);
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(SLOT, 0);
    }

    @Override
    protected void tickPart(Plantera master) {
        if (level().isClientSide) {
            return;
        }

        Vec3 anchor = master.getTentacleAnchor(getSlot());
        Vec3 offset = getEyePosition().subtract(anchor);
        double distance = offset.length();
        double desiredDistance = DISTANCE_FROM_ANCHOR * master.getScale();
        if (distance < 1.0E-6) {
            offset = master.getTentacleBaseDirection(getSlot()).scale(desiredDistance);
        } else {
            double radialStep = RADIAL_STEP * master.getScale();
            double radialChange = Mth.clamp(desiredDistance - distance, -radialStep, radialStep);
            offset = offset.scale(Math.min(distance + radialChange, desiredDistance) / distance);
        }

        LivingEntity target = master.getTarget();
        if (target != null) {
            Vec3 targetOffset = target.getEyePosition().subtract(anchor);
            double attraction = Math.max(TARGET_ATTRACTION_RADIUS - targetOffset.length(), 0.0) * TARGET_ATTRACTION;
            offset = rotateToward(offset, targetOffset, attraction);
        }

        // 所有存活触手都通过小角度旋转彼此推开。
        for (PlanteraTentacle other : master.getTentacles()) {
            if (other == null || other == this || other.isRemoved()) {
                continue;
            }
            Vec3 otherOffset = other.getEyePosition().subtract(anchor);
            double separation = other.getEyePosition().distanceTo(getEyePosition());
            offset = rotateToward(offset, otherOffset, -TENTACLE_REPULSION / Math.max(separation, 1.0));
        }

        Vec3 desiredEyePosition = anchor.add(offset);
        Vec3 movement = master.getTentacleAnchorVelocity(getSlot()).add(desiredEyePosition.subtract(getEyePosition()));
        setDeltaMovement(movement);
        move(MoverType.SELF, movement);

        Vec3 lookDirection = getEyePosition().subtract(anchor);
        if (lookDirection.lengthSqr() > 1.0E-6) {
            setYRot((float) (Mth.atan2(lookDirection.z, lookDirection.x) * Mth.RAD_TO_DEG) - 90.0F);
            setXRot((float) (-Mth.atan2(lookDirection.y, lookDirection.horizontalDistance()) * Mth.RAD_TO_DEG));
        }

        if (contactCooldown > 0) {
            contactCooldown--;
            return;
        }
        for (Entity entity : SweptContactAttack.findTargets(this, 0.25D,
                SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity living && living != master && master.canAttack(living))) {
            if (entity.hurt(damageSources().mobAttack(master), DAMAGE)) {
                contactCooldown = CONTACT_COOLDOWN;
                break;
            }
        }
    }

    /// 绕当前偏移与目标偏移的叉积旋转指定角度；使用 Rodrigues 公式避免把
    /// 四元数依赖扩散到公共实体逻辑。
    private static Vec3 rotateToward(Vec3 offset, Vec3 targetOffset, double degrees) {
        if (Math.abs(degrees) < 1.0E-9 || offset.lengthSqr() < 1.0E-9 || targetOffset.lengthSqr() < 1.0E-9) {
            return offset;
        }
        Vec3 axis = offset.cross(targetOffset);
        if (axis.lengthSqr() < 1.0E-10) {
            return offset;
        }
        axis = axis.normalize();
        double radians = Math.toRadians(degrees);
        double cosine = Math.cos(radians);
        double sine = Math.sin(radians);
        return offset.scale(cosine).add(axis.cross(offset).scale(sine)).add(axis.scale(axis.dot(offset) * (1.0 - cosine)));
    }

    @Override
    protected float getMaxPartHealth() {
        return MAX_HEALTH;
    }

    @Override
    protected Class<Plantera> getOwnerType() {
        return Plantera.class;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void onPartDestroyed(Plantera owner) {
        owner.onTentacleDestroyed(getSlot(), this);
    }

    @Override
    protected void readPartSaveData(CompoundTag tag) {
        entityData.set(SLOT, Mth.clamp(tag.getInt(SLOT_TAG), 0, Plantera.TENTACLE_COUNT - 1));
    }

    @Override
    protected void addPartSaveData(CompoundTag tag) {
        tag.putInt(SLOT_TAG, getSlot());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Plantera owner = getOwner();
        if (owner == null || !owner.isAlive() || isRemoved() || isInvulnerableTo(source))
            return false;
        float appliedDamage = source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount : CombatRules.getDamageAfterAbsorb(amount, ARMOR, 0.0F);
        if (appliedDamage <= 0.0F) return false;
        float remaining = Math.max(0.0F, getPartHealth() - appliedDamage);
        setPartHealth(remaining);
        indicateHurt();
        if (remaining <= 0.0F) {
            onPartDestroyed(owner);
            discard();
        }
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
