package org.confluence.mod.common.entity.mount;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 临时玩家坐骑的最小公共基类。
///
/// 公共层只处理所有坐骑都必需的所有者、单控制乘客、输入状态、朝向、
/// 伤害转交和离场清理。速度、跳跃、飞行、能量、攻击和动画全部由具体实体实现。
public abstract class AbstractMountEntity extends Entity implements OwnableEntity {
    private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(AbstractMountEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> JUMP_INPUT = SynchedEntityData.defineId(AbstractMountEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean localJumpInput;
    private boolean slotBound;
    private boolean exitSoundPlayed;

    protected AbstractMountEntity(EntityType<?> type, Level level) {
        super(type, level);
        setMaxUpStep(1.0F);
    }

    /// 创建实体后由坐骑物品或槽位管理器调用一次。
    public final void initialize(Player owner, boolean slotBound) {
        setOwnerUUID(owner.getUUID());
        this.slotBound = slotBound;
        setPos(owner.getX(), owner.getY(), owner.getZ());
        setRot(owner.getYRot(), owner.getXRot() * 0.5F);
    }

    public final boolean isSlotBound() {
        return slotBound;
    }

    public final boolean mountPlayer(Player player) {
        if (level().isClientSide || getOwnerUUID() == null
                || !getOwnerUUID().equals(player.getUUID())) {
            return false;
        }
        boolean mounted = player.startRiding(this, true);
        if (mounted) {
            playEnterSound();
        }
        return mounted;
    }

    @Override
    protected final void defineSynchedData() {
        entityData.define(OWNER, Optional.empty());
        entityData.define(JUMP_INPUT, false);
        defineMountSynchedData();
    }

    /// 具体坐骑可声明自己的动画或能量同步字段。
    protected void defineMountSynchedData() {
    }

    @Override
    public final @Nullable UUID getOwnerUUID() {
        return entityData.get(OWNER).orElse(null);
    }

    public final void setOwnerUUID(@Nullable UUID owner) {
        entityData.set(OWNER, Optional.ofNullable(owner));
    }

    @Override
    public final @Nullable LivingEntity getOwner() {
        UUID owner = getOwnerUUID();
        if (owner == null) {
            return null;
        }
        Entity passenger = getFirstPassenger();
        if (passenger instanceof LivingEntity living
                && owner.equals(living.getUUID())) {
            return living;
        }
        return level().getPlayerByUUID(owner);
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        Entity passenger = getFirstPassenger();
        return passenger instanceof Player player
                && player.getUUID().equals(getOwnerUUID())
                ? player
                : null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().isEmpty()
                && passenger instanceof Player
                && passenger.getUUID().equals(getOwnerUUID());
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity passenger = getControllingPassenger();
        if (!(passenger instanceof Player player)) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
        if (!level().isClientSide
                && (getOwner() == null || !player.isAlive())) {
            discard();
            return;
        }
        setRot(player.getYRot(), player.getXRot() * 0.5F);
        tickRidden(player);
    }

    /// 每 tick 的具体移动与能力逻辑。客户端可做同公式预测。
    protected abstract void tickRidden(Player player);

    /// 服务端只接受当前控制乘客的跳跃键状态。
    public final void setControllerJumpInput(
            Player player,
            boolean jumping
    ) {
        if (!level().isClientSide
                && player == getControllingPassenger()
                && entityData.get(JUMP_INPUT) != jumping) {
            entityData.set(JUMP_INPUT, jumping);
            onJumpInputChanged(player, jumping);
        }
    }

    /// 本地预测只记录按键，不修改任何服务端能力数值。
    public final void setLocalJumpInput(Player player, boolean jumping) {
        if (level().isClientSide
                && player == getControllingPassenger()
                && localJumpInput != jumping) {
            localJumpInput = jumping;
            onJumpInputChanged(player, jumping);
        }
    }

    protected final boolean isJumpInputDown() {
        return level().isClientSide
                ? localJumpInput
                : entityData.get(JUMP_INPUT);
    }

    protected void onJumpInputChanged(Player player, boolean jumping) {
    }

    /// 把玩家局部横向输入转换为世界方向，并以固定加速度逼近目标速度。
    protected final Vec3 accelerateHorizontal(
            Player player,
            double localStrafe,
            double localForward,
            double maximumSpeed,
            double acceleration
    ) {
        Vec3 current = getDeltaMovement();
        // 坐骑方向键表达的是当前移动意图，不是带惯性的油门。松开方向键时
        // 必须在同一 tick 清除水平速度，否则高速度坐骑会继续滑行数 tick，
        // 客户端看起来就像按键被粘住。这里只保留垂直分量，跳跃、飞行与
        // 自然下落仍由具体坐骑按自己的规则继续计算。
        if (Math.abs(localStrafe) < 1.0E-6
                && Math.abs(localForward) < 1.0E-6) {
            return new Vec3(0.0, current.y, 0.0);
        }

        double yaw = Math.toRadians(player.getYRot());
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double targetX =
                (localStrafe * cos - localForward * sin) * maximumSpeed;
        double targetZ =
                (localForward * cos + localStrafe * sin) * maximumSpeed;
        double length = Math.sqrt(targetX * targetX + targetZ * targetZ);
        if (length > maximumSpeed && length > 0.0) {
            targetX = targetX / length * maximumSpeed;
            targetZ = targetZ / length * maximumSpeed;
        }

        double dx = targetX - current.x;
        double dz = targetZ - current.z;
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance > acceleration && distance > 0.0) {
            dx = dx / distance * acceleration;
            dz = dz / distance * acceleration;
        }
        return new Vec3(current.x + dx, current.y, current.z + dz);
    }

    protected final void moveWithVelocity(Vec3 velocity) {
        setDeltaMovement(velocity);
        move(MoverType.SELF, velocity);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof LivingEntity living) {
            living.yBodyRot = getYRot();
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return getBbHeight() * 0.75 + 0.2;
    }

    @Override
    public void removePassenger(Entity passenger) {
        boolean controller = passenger == getControllingPassenger();
        super.removePassenger(passenger);
        if (!level().isClientSide && controller && !exitSoundPlayed) {
            exitSoundPlayed = true;
            playExitSound();
        }
    }

    protected void playEnterSound() {
        playSound(SoundEvents.AMBIENT_UNDERWATER_ENTER, 0.5F, 3.0F);
    }

    protected void playExitSound() {
        playSound(SoundEvents.AMBIENT_UNDERWATER_EXIT, 0.5F, 3.0F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && getOwner() instanceof Player owner) {
            owner.hurt(source, amount);
        }
        return source.is(DamageTypes.GENERIC_KILL)
                && super.hurt(source, amount);
    }

    @Override
    public final boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected final void readAdditionalSaveData(CompoundTag tag) {
        discard();
    }

    @Override
    protected final void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
