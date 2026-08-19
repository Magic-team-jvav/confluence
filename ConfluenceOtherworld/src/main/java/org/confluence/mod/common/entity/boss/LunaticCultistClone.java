package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.CircleAroundTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 拜月教邪教徒在仪式攻击中生成的可受击幻影。
///
/// <p>幻影参与目标判断与受击反馈，但其生命周期和战斗归属始终由主体 Boss 管理。</p>
public final class LunaticCultistClone extends BaseFlyingMonster {
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

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
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
    }

    public @Nullable LunaticCultist getMaster() {
        return ownerTracker.resolve(this);
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
                return SequenceNode.of(new HasTargetCondition(LunaticCultistClone.this), new CircleAroundTargetAction(LunaticCultistClone.this, 0.45, 8.0), new WaitAction(24));
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        LunaticCultist master = getMaster();
        if (master != null && master.isAlive()) {
            LivingEntity masterTarget = master.getTarget();
            if (masterTarget != null && masterTarget.isAlive() && getTarget() != masterTarget) {
                setTarget(masterTarget);
            }
        }
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
        if (master != null && source.getEntity() instanceof Player) {
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
}
