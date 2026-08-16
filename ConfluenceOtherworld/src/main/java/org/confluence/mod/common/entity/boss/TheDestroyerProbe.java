package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MaintainRangedDistanceAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 毁灭者体节释放的远程附属探测器。
///
/// <p>探测器独立保持射击距离，但目标和生命周期归属于精确的毁灭者 UUID。主人暂时
/// 卸载时探测器停止攻击并等待恢复，不会转化成永久游荡的独立怪物。</p>
public final class TheDestroyerProbe
        extends BaseFlyingMonster {
    private static final String SHOT_TIMER_TAG = "ShotTimer";
    private static final int SHOT_INTERVAL = 60;

    private static final EntityDataAccessor<Optional<UUID>>
            OWNER_UUID = SynchedEntityData.defineId(
            TheDestroyerProbe.class,
            EntityDataSerializers.OPTIONAL_UUID);

    private final BossOwnerTracker<TheDestroyer> ownerTracker =
            new BossOwnerTracker<>(TheDestroyer.class);
    private int shotTimer = SHOT_INTERVAL;

    public TheDestroyerProbe(
            EntityType<? extends TheDestroyerProbe> type,
            Level level) {
        super(type, level);
        xpReward = 5;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setMaster(TheDestroyer master) {
        ownerTracker.bind(this, master);
        entityData.set(
                OWNER_UUID, Optional.of(master.getUUID()));
    }

    public @Nullable TheDestroyer getMaster() {
        return ownerTracker.resolve(this);
    }

    public @Nullable UUID getMasterUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(
                                new HasTargetCondition(
                                        TheDestroyerProbe.this),
                                new MaintainRangedDistanceAction(
                                        TheDestroyerProbe.this,
                                        10.0,
                                        16.0,
                                        0.7,
                                        40),
                                new WaitAction(20)),
                        new FlyWanderAction(
                                TheDestroyerProbe.this,
                                0.3,
                                8));
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }

        TheDestroyer master = getMaster();
        if (master == null || !master.isAlive()) {
            setTarget(null);
            return;
        }
        LivingEntity masterTarget = master.getTarget();
        if (masterTarget != null && masterTarget.isAlive()
                && getTarget() != masterTarget) {
            setTarget(masterTarget);
        }
        if (distanceToSqr(master) > 4096.0) {
            Vec3 towardMaster = master.position()
                    .add(0.0, 2.0, 0.0)
                    .subtract(position());
            setDeltaMovement(
                    getDeltaMovement().scale(0.4)
                            .add(towardMaster.normalize()
                                    .scale(0.45)));
        }
        if (getTarget() != null && --shotTimer <= 0) {
            shotTimer = SHOT_INTERVAL;
            TheDestroyer.fireLaser(
                    this,
                    getEyePosition(),
                    getTarget(),
                    (float) getAttributeValue(
                            net.minecraft.world.entity.ai.attributes
                                    .Attributes.ATTACK_DAMAGE));
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof TheDestroyer)
                && !(target instanceof TheDestroyerProbe)
                && super.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        TheDestroyer master = getMaster();
        if (master != null && source.getEntity() == master) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.VISUAL_NEURON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.VISUAL_NEURON_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
        tag.putInt(SHOT_TIMER_TAG, shotTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
        entityData.set(
                OWNER_UUID,
                Optional.ofNullable(
                        ownerTracker.getOwnerUUID()));
        shotTimer = tag.getInt(SHOT_TIMER_TAG);
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }
}
