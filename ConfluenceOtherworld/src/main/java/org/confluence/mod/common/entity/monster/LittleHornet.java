package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.boss.BossOwnedEntity;
import org.confluence.mod.common.entity.boss.BossOwnerTracker;
import org.confluence.mod.common.entity.boss.QueenBee;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Optional;
import java.util.UUID;

/// 蜂王召唤的近战幼蜂。
///
/// 幼蜂只响应受击或蜂王每 32 tick 下发的目标，使用飞行导航
/// 追近后近战；离蜂王超过 30 格且蜂王所在位置可容纳实体时，回到蜂王上方。所有者追踪器
/// 只负责跨存档恢复归属，不额外增加持续追踪、强制返航或独立索敌。
public final class LittleHornet extends Hornet implements BossOwnedEntity {
    private static final RawAnimation WING = RawAnimation.begin().thenLoop("wing");
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(LittleHornet.class, EntityDataSerializers.OPTIONAL_UUID);

    private final BossOwnerTracker<QueenBee> ownerTracker = new BossOwnerTracker<>(QueenBee.class);

    public LittleHornet(EntityType<? extends LittleHornet> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setMaster(QueenBee master) {
        ownerTracker.bind(this, master);
        entityData.set(OWNER_UUID, Optional.of(master.getUUID()));
        BossMinionCoordinator.faceTargetImmediately(this, getTarget());
    }

    public @Nullable QueenBee getMaster() {
        return ownerTracker.resolve(this);
    }

    @Override
    public @Nullable QueenBee getBossOwner() {
        return getMaster();
    }

    public @Nullable UUID getMasterUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    /// 只保留受击反击目标。蜂王每 32 tick 下发的目标不能被
    /// 普通黄蜂的最近玩家目标任务覆盖或清除。
    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    /// 幼蜂没有普通怪物的自主玩家索敌，只接受受击目标和蜂王下发的目标。
    @Override
    protected boolean canTargetPlayer(LivingEntity target) {
        return false;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return false;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(new LittleHornetMeleeNode(), new HornetWanderNode());
            }
        };
    }

    @Override
    public void tick() {
        LivingEntity inheritedTarget = null;
        QueenBee master = null;
        if (!level().isClientSide && getMasterUUID() != null) {
            master = ownerTracker.tickDependent(this, true, 100);
            inheritedTarget = getTarget();
            if (isRemoved()) return;
        }
        super.tick();
        if (level().isClientSide || !isAlive()) {
            return;
        }
        if (master != null && getTarget() != inheritedTarget) {
            setTarget(inheritedTarget);
        }
        if (master == null) {
            return;
        }
        if ((tickCount & 31) == 0) {
            if (distanceTo(master) > 60.0 && level().getBlockState(master.blockPosition()).isAir()) {
                setPos(master.getX(), master.getY() + 0.5, master.getZ());
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        QueenBee master = getMaster();
        if (master != null && source.getEntity() == master) {
            return false;
        }
        return super.hurt(source, amount);
    }

    /// 幼蜂不继承普通黄蜂的远程施法动画，只持续播放振翅动画。
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Wing", 0, state -> state.setAndContinue(WING)));
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

    /// 近战追击允许在短暂失去视线后继续跟随目标。
    /// 节点只保留追路和攻击冷却，不加入冲锋蓄力或锁定方向。
    private final class LittleHornetMeleeNode extends BTNode {
        // 导航速度倍率与近战命中冷却（tick）；编队等待时使用 75% 速度。
        private static final double MOVE_SPEED = 2.0;
        private static final int ATTACK_INTERVAL = 20;
        private int repathDelay;
        private int attackCooldown;

        @Override
        public void start() {
            repathDelay = 0;
            attackCooldown = 0;
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive()) {
                return BTStatus.FAILURE;
            }

            boolean attackWindow = BossMinionCoordinator.isAttackWindow(LittleHornet.this, 64, 28);
            getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--repathDelay <= 0) {
                QueenBee master = getMaster();
                if (master != null && !attackWindow) {
                    var staging = BossMinionCoordinator.orbitPoint(
                            LittleHornet.this, target, 5.0D, 1.5D, 0.03D, 8);
                    getNavigation().moveTo(staging.x, staging.y, staging.z, MOVE_SPEED * 0.75D);
                } else {
                    var intercept = BossMinionCoordinator.predict(target, 4.0D, 3.0D);
                    getNavigation().moveTo(intercept.x, intercept.y, intercept.z, MOVE_SPEED);
                }
                repathDelay = 4 + getRandom().nextInt(7);
            }
            if (attackCooldown > 0) {
                attackCooldown--;
            }

            double reach = getBbWidth() * 2.0F;
            double attackReachSqr = reach * reach + target.getBbWidth();
            if (attackWindow && distanceToSqr(target) <= attackReachSqr && attackCooldown <= 0) {
                attackCooldown = ATTACK_INTERVAL;
                swing(InteractionHand.MAIN_HAND);
                doHurtTarget(target);
            }
            return BTStatus.RUNNING;
        }

        @Override
        public void stop() {
            getNavigation().stop();
        }
    }
}
