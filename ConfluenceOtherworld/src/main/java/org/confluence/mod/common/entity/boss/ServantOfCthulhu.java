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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.confluence.mod.common.entity.monster.CreatureAttributeBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 克苏鲁之眼生成的短命仆从。
///
/// 仆从拥有独立实体类型，以便追踪所有权并在区块恢复后重新绑定。具有主人身份时只继承
/// 主人的权威目标；完全没有主人身份的独立实体才会自行寻找玩家。
public class ServantOfCthulhu extends BaseFlyingMonster implements BossOwnedEntity {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(ServantOfCthulhu.class, EntityDataSerializers.OPTIONAL_UUID);
    private final BossOwnerTracker<EyeOfCthulhu> ownerTracker = new BossOwnerTracker<>(EyeOfCthulhu.class);

    public ServantOfCthulhu(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setMaster(EyeOfCthulhu master) {
        ownerTracker.bind(this, master);
        entityData.set(OWNER_UUID, Optional.of(master.getUUID()));
        BossMinionCoordinator.faceTargetImmediately(this, getTarget());
    }

    public @Nullable EyeOfCthulhu getMaster() {
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
        return CreatureAttributeBuilder.creature(10.0, 1.0, 3.0, 30.0, 0.5, 0.3).flying();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(Integer.MAX_VALUE);
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
        if (level().isClientSide) return;
        if (hasOwnerIdentity && getTarget() != inheritedTarget) {
            setTarget(inheritedTarget);
        }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            setDeltaMovement(getDeltaMovement().scale(0.9D));
            return;
        }

        boolean coordinatedDive = masterForCoordination() != null
                && BossMinionCoordinator.isAttackWindow(this, 52, 25);
        Vec3 destination = coordinatedDive
                ? BossMinionCoordinator.predict(target, 5.0D, 4.0D)
                : BossMinionCoordinator.orbitPoint(this, target, 6.0D, 1.8D, 0.035D, 8);
        Vec3 desired = destination.subtract(getEyePosition());
        Vec3 direction = turnDirectionToward(getDeltaMovement(), desired, 10.0F);
        double distance = Math.sqrt(desired.lengthSqr());
        double speed = Mth.clamp(0.28D + distance * 0.018D, 0.28D, 0.62D);
        Vec3 velocity = getDeltaMovement().scale(0.72D).add(direction.scale(speed * 0.28D));
        if (velocity.lengthSqr() > speed * speed) velocity = velocity.normalize().scale(speed);
        setDeltaMovement(velocity);
        faceCombatMovement(30.0F, 30.0F);
        hasImpulse = true;
    }

    private @Nullable EyeOfCthulhu masterForCoordination() {
        return getMasterUUID() == null ? null : getMaster();
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
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected int contactDetectionInterval() {
        return 1;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.2D;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof EyeOfCthulhu)
                && !(target instanceof ServantOfCthulhu)
                && super.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        EyeOfCthulhu master = getMaster();
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
