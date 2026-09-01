package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.DashAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 幻影龙——拜月教邪教徒召唤的飞龙仆从。
public class PhantasmDragon extends BaseFlyingMonster implements BossOwnedEntity {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(PhantasmDragon.class, EntityDataSerializers.OPTIONAL_UUID);
    private final BossOwnerTracker<LunaticCultist> ownerTracker = new BossOwnerTracker<>(LunaticCultist.class);

    public PhantasmDragon(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setMaster(LunaticCultist master) {
        ownerTracker.bind(this, master);
        entityData.set(OWNER_UUID, Optional.of(master.getUUID()));
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

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected int contactDetectionInterval() {
        return 1;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.3D;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(PhantasmDragon.this),
                                new DashAction(PhantasmDragon.this, 1.0, 25)),
                        SequenceNode.of(new WaitAction(15),
                                new FlyWanderAction(PhantasmDragon.this, 0.4, 12))
                );
            }
        };
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this, Player.class, 1, false, false, this::canTargetPlayer
        ));
    }

    @Override
    protected boolean canTargetPlayer(LivingEntity target) {
        return getMasterUUID() == null && isLegalIndependentTarget(target);
    }

    @Override
    public void tick() {
        LivingEntity inheritedTarget = null;
        boolean hasOwnerIdentity = !level().isClientSide && getMasterUUID() != null;
        if (hasOwnerIdentity) {
            ownerTracker.tickDependent(this, true, 100);
            inheritedTarget = getTarget();
            if (isRemoved()) return;
        } else if (!level().isClientSide && !isLegalIndependentTarget(getTarget())) {
            setTarget(null);
        }

        super.tick();
        if (!level().isClientSide && hasOwnerIdentity && getTarget() != inheritedTarget) {
            setTarget(inheritedTarget);
        }
    }

    private boolean isLegalIndependentTarget(@Nullable LivingEntity target) {
        return target instanceof Player player
                && player.level() == level()
                && player.isAlive()
                && !player.isCreative()
                && !player.isSpectator()
                && player.canBeSeenAsEnemy()
                && canAttack(player);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof LunaticCultist)
                && !(target instanceof PhantasmDragon)
                && super.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        LunaticCultist master = getMaster();
        if (master != null && source.getEntity() == master) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
        entityData.set(OWNER_UUID, Optional.ofNullable(ownerTracker.getOwnerUUID()));
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }
}
