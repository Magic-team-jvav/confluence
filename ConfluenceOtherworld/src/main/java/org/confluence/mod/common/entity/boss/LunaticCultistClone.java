package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.CircleAroundTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 拜月教邪教徒在仪式攻击中生成的可受击幻影。
///
/// 幻影参与目标判断与受击反馈，但其生命周期和战斗归属始终由主体 Boss 管理。
public final class LunaticCultistClone extends BaseFlyingMonster implements BossOwnedEntity {
    private static final String AGE_TAG = "IllusionAge";
    private static final int LIFETIME = 240;
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(LunaticCultistClone.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CLONE_INDEX = SynchedEntityData.defineId(LunaticCultistClone.class, EntityDataSerializers.INT);

    private final BossOwnerTracker<LunaticCultist> ownerTracker = new BossOwnerTracker<>(LunaticCultist.class);
    private int illusionAge;

    public LunaticCultistClone(EntityType<? extends LunaticCultistClone> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
        entityData.define(CLONE_INDEX, 0);
    }

    public void setMaster(LunaticCultist master, int cloneIndex) {
        ownerTracker.bind(this, master);
        entityData.set(OWNER_UUID, Optional.of(master.getUUID()));
        entityData.set(CLONE_INDEX, Mth.clamp(cloneIndex, 0, LunaticCultist.CLONE_COUNT - 1));
        setTarget(master.getTarget());
        BossMinionCoordinator.faceTargetImmediately(this, getTarget());
    }

    public @Nullable LunaticCultist getMaster() {
        return ownerTracker.resolve(this);
    }

    @Override
    public @Nullable BaseBoss getBossOwner() {
        return getMaster();
    }

    public @Nullable UUID getMasterUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    public int getCloneIndex() {
        return entityData.get(CLONE_INDEX);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new ConditionalSwitchNode(() -> getMaster() != null && getMaster().isPerformingRitual(),
                        new WaitAction(20),
                        SequenceNode.of(new HasTargetCondition(LunaticCultistClone.this), new CircleAroundTargetAction(LunaticCultistClone.this, 0.45, 8.0), new WaitAction(24)));
            }
        };
    }

    @Override
    public void tick() {
        LivingEntity inheritedTarget = null;
        boolean owned = !level().isClientSide && ownerTracker.getOwnerUUID() != null;
        if (owned) {
            ownerTracker.tickDependent(this, true, 100);
            inheritedTarget = getTarget();
            if (isRemoved()) return;
        }
        super.tick();
        if (level().isClientSide) return;
        if (owned && getTarget() != inheritedTarget) {
            setTarget(inheritedTarget);
        }
        if (getMaster() != null && getMaster().isPerformingRitual())
            setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        if (++illusionAge >= LIFETIME) discard();
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof LunaticCultist)
                && !(target instanceof LunaticCultistClone)
                && !(target instanceof PhantasmDragon)
                && super.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return true;
        LunaticCultist master = getMaster();
        if (master != null && !master.isPerformingRitual() && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;
        if (master != null && source.getEntity() instanceof LivingEntity attacker && master.canAttack(attacker)) {
            master.onEncounterHurt(source);
            master.onCloneHit(this);
        }
        discard();
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
        tag.putInt(AGE_TAG, illusionAge);
        tag.putInt("CloneIndex", getCloneIndex());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
        entityData.set(OWNER_UUID, Optional.ofNullable(ownerTracker.getOwnerUUID()));
        entityData.set(CLONE_INDEX, Mth.clamp(tag.getInt("CloneIndex"), 0, LunaticCultist.CLONE_COUNT - 1));
        illusionAge = Mth.clamp(tag.getInt(AGE_TAG), 0, LIFETIME - 1);
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.LUNATIC_CULTIST_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.LUNATIC_CULTIST_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.LUNATIC_CULTIST_DEATH.get();
    }

}
