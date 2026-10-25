package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 世纪之花触手——从方块表面伸出攻击玩家，短时间后消失。
public class PlanteraTentacle extends BaseLivingBossPart<Plantera> implements GeoEntity {
    private static final String SLOT_TAG = "Slot";
    /// 触手默认环绕锚点的距离，单位格。
    private static final double DISTANCE_FROM_ANCHOR = 6.0;

    /// FTW 模式触手径向移动和目标吸引的倍率。
    private static final double FTW_TENTACLE_SPEED_MULTIPLIER = 9.0;
    /// 触手每 tick 向目标环绕半径收缩或扩张的最大距离，单位格；随 Boss 缩放。
    private static final double RADIAL_STEP = 0.15;
    /// 玩家可吸引触手偏离环绕轨道的距离，单位格。
    private static final double TARGET_ATTRACTION_RADIUS = 24.0;
    /// 触手朝目标偏移时使用的吸引强度。
    private static final double TARGET_ATTRACTION = 0.25;
    /// 触手彼此靠近时的分离强度。
    private static final double TENTACLE_REPULSION = 2.5;
    /// 同一触手连续造成接触伤害的最短间隔，单位 tick。
    private static final int CONTACT_COOLDOWN = 20;

    private static final EntityDataAccessor<Integer> SLOT = SynchedEntityData.defineId(PlanteraTentacle.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int contactCooldown;

    public PlanteraTentacle(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    /// 将新建触手绑定到指定槽位。槽位必须由世纪之花分配，外部调用者不应自行复用。
    public void setMaster(Plantera master, int slot) {
        if (!bindTo(master)) {
            return;
        }
        entityData.set(SLOT, Mth.clamp(slot, 0, Plantera.TENTACLE_COUNT - 1));
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
            double radialStep = RADIAL_STEP * master.getScale() * (master.isFtw() ? FTW_TENTACLE_SPEED_MULTIPLIER : 1.0);
            double radialChange = Mth.clamp(desiredDistance - distance, -radialStep, radialStep);
            offset = offset.scale(Math.min(distance + radialChange, desiredDistance) / distance);
        }

        LivingEntity target = master.getTarget();
        if (target != null) {
            Vec3 targetOffset = target.getEyePosition().subtract(anchor);
            double attraction = Math.max(TARGET_ATTRACTION_RADIUS - targetOffset.length(), 0.0) * TARGET_ATTRACTION
                    * (master.isFtw() ? FTW_TENTACLE_SPEED_MULTIPLIER : 1.0);
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
            LibEntityUtils.updateEntityRotation(this, lookDirection);
        }

        if (contactCooldown > 0) contactCooldown--;
        if (master.getTarget() == null || contactCooldown > 0) return;
        for (Entity entity : SweptContactAttack.findTargets(this, 0.25D,
                SweptContactAttack.DEFAULT_MAX_SWEEP_DISTANCE,
                candidate -> candidate instanceof LivingEntity living && living != master && master.canAttack(living))) {
            if (entity.hurt(damageSources().mobAttack(master), (float) getAttributeValue(Attributes.ATTACK_DAMAGE))) {
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
        if (!super.hurt(source, amount)) {
            return false;
        }
        indicateHurt();
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
