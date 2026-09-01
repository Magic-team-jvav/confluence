package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossOwnedEntity;
import org.confluence.mod.common.entity.boss.BossOwnerTracker;
import org.confluence.mod.common.entity.boss.BrainOfCthulhu;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 克苏鲁之脑第一阶段召唤的视神经元。
///
/// 视神经元不会自行搜索玩家。克苏鲁之脑为每只神经元分配一个待命位置，并在攻击间隔到达时命令一只
/// 已就绪的神经元出击。出击中的神经元持续修正速度；一旦冲过目标、目标失效或受到伤害，便切换为返回状态。
/// 返回到待命位置附近后才会重新接受下一次出击命令。
///
/// 所有权使用 Boss UUID 持久化。区块卸载只会清除当前实例缓存，不会猜测附近的同类 Boss；
/// Boss 重新加载后，神经元会通过精确 UUID 恢复双向关系，从而避免多人同时挑战时串场。
public class VisualNeuron extends BaseFlyingMonster implements BossOwnedEntity {
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
        return 0.2;
    }

    /// 视神经元在当前版本属性注册中使用的基础最大生命。
    public static final double BASE_MAX_HEALTH = 44.0;
    private static final String STATE_TAG = "CombatState";
    private static final String ATTACK_TICKS_TAG = "AttackTicks";
    private static final String HOME_X_TAG = "HomeX";
    private static final String HOME_Y_TAG = "HomeY";
    private static final String HOME_Z_TAG = "HomeZ";
    private static final String HOME_OFFSET_X_TAG = "HomeOffsetX";
    private static final String HOME_OFFSET_Y_TAG = "HomeOffsetY";
    private static final String HOME_OFFSET_Z_TAG = "HomeOffsetZ";
    // 攻击与归位加速度、速度上限的单位均为方块/tick；归位用十 tick 渐进恢复。
    private static final double ATTACK_ACCELERATION = 0.16;
    private static final double RETURN_ACCELERATION = 0.5 / 10.0;
    private static final double MAX_ATTACK_SPEED = 1.05;
    private static final double MAX_RETURN_SPEED = 0.5;
    // 回到距编队点 2 方块内视为就位；偏航超过 45° 时先转向再发起下一轮攻击。
    private static final double READY_DISTANCE_SQR = 4.0;
    private static final double TURN_BACK_ANGLE = Math.PI / 4.0;

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(VisualNeuron.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> COMBAT_STATE = SynchedEntityData.defineId(VisualNeuron.class, EntityDataSerializers.INT);

    private final BossOwnerTracker<BrainOfCthulhu> ownerTracker = new BossOwnerTracker<>(BrainOfCthulhu.class);
    private @Nullable Vec3 homePosition;
    private @Nullable Vec3 homeOffset;
    private boolean ready = true;
    private int attackTicks;
    private boolean reportedDeath;

    public VisualNeuron(EntityType<? extends VisualNeuron> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
        entityData.define(COMBAT_STATE, CombatState.RETURNING.id);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CreatureAttributeBuilder.creature(BASE_MAX_HEALTH, 10.0, 9.0, 0.0, 0.0, 0.1);
    }

    /// 建立神经元与权威 Boss 的精确所有权。
    public void setOwner(BrainOfCthulhu owner) {
        ownerTracker.bind(this, owner);
        entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
        BossMinionCoordinator.faceTargetImmediately(this, getTarget());
        // Boss 随从不能套用普通怪物按玩家距离随机消失的规则，否则第一阶段会无故丢失编队成员。
        setPersistenceRequired();
    }

    public @Nullable BrainOfCthulhu getOwner() {
        return ownerTracker.resolve(this);
    }

    @Override
    public @Nullable BaseBoss getBossOwner() {
        return getOwner();
    }

    public @Nullable UUID getOwnerUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    public boolean isOwnedBy(BrainOfCthulhu owner) {
        return owner.getUUID().equals(getOwnerUUID());
    }

    /// 更新随 Boss 移动的待命位置。该位置由 Boss 统一计算，神经元只负责返回。
    public void setHomePosition(Vec3 homePosition) {
        this.homePosition = homePosition;
    }

    public @Nullable Vec3 getHomePosition() {
        return homePosition;
    }

    /// 保存相对本体的初始编队偏移，供本体计算随机编队摆动。
    public void setHomeOffset(Vec3 homeOffset) {
        this.homeOffset = homeOffset;
    }

    public @Nullable Vec3 getHomeOffset() {
        return homeOffset;
    }

    /// 命令当前已就绪的神经元攻击指定目标。
    ///
    /// @return 是否成功接受了这次命令
    public boolean attack(LivingEntity target) {
        if (!isReady() || !target.isAlive()) return false;
        setTarget(target);
        ready = false;
        attackTicks = 0;
        setCombatState(CombatState.ATTACKING);
        Vec3 launchDirection = target.getEyePosition().subtract(getEyePosition());
        if (launchDirection.lengthSqr() > 1.0E-6) {
            setDeltaMovement(launchDirection.normalize().scale(0.65D));
        }
        return true;
    }

    public boolean isReady() {
        return ready && getCombatState() == CombatState.RETURNING;
    }

    public boolean isAttacking() {
        return getCombatState() == CombatState.ATTACKING;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                // 专用状态机由实体 tick 推进，通用 Goal 调度不得重复修改速度。
                return new WaitAction(Integer.MAX_VALUE);
            }
        };
    }

    @Override
    public void tick() {
        if (!level().isClientSide && isAlive() && !isRemoved()) {
            BrainOfCthulhu owner = ownerTracker.tickDependent(this, false, 100);
            if (owner == null) {
                if (isRemoved()) reportDeath();
                if (getOwnerUUID() == null) discard();
                return;
            }
        }
        super.tick();
        if (!level().isClientSide && isAlive() && !isRemoved()) {
            tickCombatState();
        }
    }

    /// 神经元的状态更新只在服务端执行，客户端仅接收位置、朝向和状态同步。
    private void tickCombatState() {
        BrainOfCthulhu owner = ownerTracker.resolve(this);
        if (owner == null) {
            if (isRemoved()) reportDeath();
            if (getOwnerUUID() == null) discard();
            return;
        }

        if (getCombatState() == CombatState.ATTACKING) {
            tickAttack();
        } else {
            tickReturn(owner);
        }
        hasImpulse = true;
    }

    private void tickAttack() {
        attackTicks++;
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            beginReturn();
            return;
        }

        Vec3 toTarget = target.getEyePosition().subtract(position());
        if (toTarget.lengthSqr() < 1.0E-6) {
            return;
        }

        Vec3 desiredDirection = toTarget.normalize();
        Vec3 velocity = getDeltaMovement().scale(0.97D).add(desiredDirection.scale(ATTACK_ACCELERATION));
        if (velocity.lengthSqr() > MAX_ATTACK_SPEED * MAX_ATTACK_SPEED) {
            velocity = velocity.normalize().scale(MAX_ATTACK_SPEED);
        }
        setDeltaMovement(velocity);
        faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);

        // 出击后至少完成一段清晰的冲锋；越过目标或持续过久才返航，不能刚离开编队
        // 就因网络位置/目标轻微侧移立即撤回，看起来像只会黏着本体。
        if ((attackTicks >= 8 && angleBetween(velocity, toTarget) > TURN_BACK_ANGLE)
                || attackTicks >= 80) {
            beginReturn();
        }
    }

    private void tickReturn(BrainOfCthulhu owner) {
        if (homePosition == null) {
            homePosition = owner.position();
        }

        Vec3 toHome = homePosition.subtract(position());
        if (toHome.lengthSqr() <= READY_DISTANCE_SQR) {
            ready = true;
        }
        if (toHome.lengthSqr() > 1.0E-6) {
            Vec3 velocity = getDeltaMovement().scale(0.9D).add(toHome.normalize().scale(RETURN_ACCELERATION));
            if (velocity.lengthSqr() > MAX_RETURN_SPEED * MAX_RETURN_SPEED) {
                velocity = velocity.normalize().scale(MAX_RETURN_SPEED);
            }
            setDeltaMovement(velocity);
        }
        LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
        } else {
            faceCombatPosition(position().scale(2.0).subtract(owner.position()), 30.0F, 30.0F);
        }
    }

    private void beginReturn() {
        setCombatState(CombatState.RETURNING);
        attackTicks = 0;
    }

    private CombatState getCombatState() {
        return CombatState.byId(entityData.get(COMBAT_STATE));
    }

    private void setCombatState(CombatState state) {
        entityData.set(COMBAT_STATE, state.id);
    }

    private static double angleBetween(Vec3 first, Vec3 second) {
        if (first.lengthSqr() < 1.0E-6 || second.lengthSqr() < 1.0E-6) return 0.0;
        return Math.acos(Mth.clamp(first.normalize().dot(second.normalize()), -1.0, 1.0));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        BrainOfCthulhu owner = getOwner();
        if (owner != null && source.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
            owner.registerCombatParticipant(player);
        }
        if (isAttacking()) {
            beginReturn();
            if (owner != null) {
                Vec3 toOwner = owner.position().subtract(position());
                if (toOwner.lengthSqr() > 1.0E-6) {
                    setDeltaMovement(toOwner.normalize());
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
        tag.putInt(STATE_TAG, getCombatState().id);
        tag.putInt(ATTACK_TICKS_TAG, attackTicks);
        if (homePosition != null) {
            tag.putDouble(HOME_X_TAG, homePosition.x);
            tag.putDouble(HOME_Y_TAG, homePosition.y);
            tag.putDouble(HOME_Z_TAG, homePosition.z);
        }
        if (homeOffset != null) {
            tag.putDouble(HOME_OFFSET_X_TAG, homeOffset.x);
            tag.putDouble(HOME_OFFSET_Y_TAG, homeOffset.y);
            tag.putDouble(HOME_OFFSET_Z_TAG, homeOffset.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
        entityData.set(OWNER_UUID, Optional.ofNullable(ownerTracker.getOwnerUUID()));
        setCombatState(CombatState.byId(tag.getInt(STATE_TAG)));
        attackTicks = Math.max(0, tag.getInt(ATTACK_TICKS_TAG));
        if (tag.contains(HOME_X_TAG) && tag.contains(HOME_Y_TAG) && tag.contains(HOME_Z_TAG)) {
            homePosition = new Vec3(tag.getDouble(HOME_X_TAG), tag.getDouble(HOME_Y_TAG), tag.getDouble(HOME_Z_TAG));
        } else {
            homePosition = null;
        }
        if (tag.contains(HOME_OFFSET_X_TAG) && tag.contains(HOME_OFFSET_Y_TAG) && tag.contains(HOME_OFFSET_Z_TAG)) {
            homeOffset = new Vec3(tag.getDouble(HOME_OFFSET_X_TAG), tag.getDouble(HOME_OFFSET_Y_TAG), tag.getDouble(HOME_OFFSET_Z_TAG));
        } else {
            homeOffset = null;
        }
        ready = getCombatState() == CombatState.RETURNING;
        reportedDeath = false;
    }

    @Override
    public void die(DamageSource source) {
        reportDeath();
        super.die(source);
    }

    private void reportDeath() {
        if (level().isClientSide || reportedDeath) return;
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID == null) return;
        reportedDeath = true;
        BrainOfCthulhu owner = getOwner();
        if (owner != null) {
            owner.onNeuronDefeated(this);
        } else if (level() instanceof ServerLevel serverLevel) {
            BrainOfCthulhu.recordDetachedNeuronDeath(serverLevel, ownerUUID, getUUID());
        }
    }

    public void cancelPendingSpawn() {
        reportedDeath = true;
        ownerTracker.clear(this);
        entityData.set(OWNER_UUID, Optional.empty());
        discard();
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) reportDeath();
        ownerTracker.unbind(this);
        super.remove(reason);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.VISUAL_NEURON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.VISUAL_NEURON_DEATH.get();
    }

    private enum CombatState {
        ATTACKING(0),
        RETURNING(1);

        private final int id;

        CombatState(int id) {
            this.id = id;
        }

        private static CombatState byId(int id) {
            return id == ATTACKING.id ? ATTACKING : RETURNING;
        }
    }
}
