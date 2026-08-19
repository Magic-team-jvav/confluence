package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.mesdag.portlib.wrapper.world.entity.projectile.PortProjectileDeflection;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 全部宝箱怪共用的伪装与跳跃战斗状态机。
///
/// <p>闭合、开箱、跳跃和重新闭合都是服务端权威状态，并通过实体数据同步给客户端。
/// 客户端只按同步姿态播放动画，不推测目标或落地时刻，因此多人环境下不会出现不同玩家
/// 看到不同开合状态的问题。全部宝箱怪在伪装时都不会因玩家靠近而主动索敌，只有玩家
/// 造成实际伤害后才会锁定该攻击者并开箱。木、金、冰和暗影宝箱怪使用普通跳跃节奏；
/// 四种困难宝箱怪复用同一状态机，但会按血量与攻击轮次提高连续跳跃强度。</p>
public class BaseMimic extends BaseMonster {
    private static final String IDLE_ANGLE_TAG = "MimicIdleAngle";
    private static final EntityDataAccessor<Byte> DATA_POSE = SynchedEntityData.defineId(BaseMimic.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_IDLE_ANGLE = SynchedEntityData.defineId(BaseMimic.class, EntityDataSerializers.INT);

    private static final RawAnimation CLOSED = RawAnimation.begin().thenLoop("Closed state");
    private static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("Open");
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlayAndHold("Jump");
    private static final RawAnimation CLOSE = RawAnimation.begin().thenPlayAndHold("Closed");

    private static final int OPENING_TICKS = 13;
    private static final int LOST_TARGET_TICKS = 20;
    private static final int CLOSING_TICKS = 13;
    private static final int DEFENDING_TICKS = 8;
    /// 困难宝箱怪闭合时保持弹幕原速反向飞行。PortLib 的通用反弹会将速度减半，
    /// 但泰拉的 50% 反射伤害应由伤害值承担，不能再叠加一次速度衰减。
    private static final PortProjectileDeflection MIMIC_REFLECTION = (projectile, entity, random) -> {
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0));
        projectile.setYRot(projectile.getYRot() + 180.0F);
        projectile.yRotO += 180.0F;
        projectile.hasImpulse = true;
    };

    private int poseTicks;
    private int targetMissingTicks;
    private int jumpCooldown;
    private int attackCycle;

    public BaseMimic(EntityType<? extends BaseMimic> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 80.0).add(Attributes.ATTACK_DAMAGE, 15.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_POSE, (byte) MimicPose.CLOSED.ordinal());
        entityData.define(DATA_IDLE_ANGLE, -1);
    }

    @Override
    public void onAddedToWorld() {
        if (!level().isClientSide && entityData.get(DATA_IDLE_ANGLE) < 0) {
            setIdleAngle(random.nextInt(4) * 90);
        }
        super.onAddedToWorld();
    }

    /// 宝箱怪的移动由自身状态机控制，行为树根只负责保持调度槽位。
    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    /// 伪装状态下不会因玩家靠近而主动索敌，只能由玩家的实际攻击唤醒。
    @Override
    protected boolean canTargetPlayer(LivingEntity target) {
        return false;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getMimicPose() == MimicPose.DEFENDING) {
            return false;
        }
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide && source.getEntity() instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            setTarget(player);
        }
        return damaged;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        LivingEntity target = getTarget();
        if (target != null && !(target instanceof Player)) {
            setTarget(null);
            target = null;
        }
        boolean validTarget = target != null
                && target.isAlive()
                && !target.isSpectator()
                && target.level() == level();
        if (validTarget) {
            targetMissingTicks = 0;
            tickCombat(target);
        } else {
            if (target != null) setTarget(null);
            tickDisengaged();
        }
    }

    private void tickCombat(LivingEntity target) {
        MimicPose pose = getMimicPose();
        if (pose == MimicPose.CLOSED || pose == MimicPose.CLOSING) {
            navigation.stop();
            setHorizontalMotion(0.0, 0.0);
            setMimicPose(MimicPose.OPENING);
            return;
        }

        poseTicks++;
        if (pose == MimicPose.OPENING) {
            navigation.stop();
            setHorizontalMotion(0.0, 0.0);
            lookAtTarget(target);
            if (poseTicks >= OPENING_TICKS) {
                setMimicPose(MimicPose.ACTIVE);
            }
            return;
        }

        if (pose == MimicPose.DEFENDING) {
            navigation.stop();
            setDeltaMovement(Vec3.ZERO);
            lookAtTarget(target);
            if (poseTicks >= DEFENDING_TICKS) {
                setMimicPose(MimicPose.ACTIVE);
            }
            return;
        }

        if (jumpCooldown > 0) jumpCooldown--;
        if (pose == MimicPose.ACTIVE && onGround() && jumpCooldown == 0) {
            beginJumpAttack(target);
            return;
        }

        if (pose == MimicPose.JUMPING) {
            lookAtTarget(target);
            if (poseTicks > 2 && onGround()) {
                damageNearbyTarget(target);
                jumpCooldown = nextJumpDelay();
                if (isHardmodeVariant() && attackCycle % 3 == 0) {
                    beginDefense();
                } else {
                    setMimicPose(MimicPose.ACTIVE);
                }
            }
        }
    }

    /// 宝箱怪没有普通怪物的主动玩家索敌目标。这里只保留受击反击目标，伪装唤醒与
    /// 后续开合仍由本类的服务端状态机处理，避免继承的最近玩家目标任务清除外部刚设置的攻击者。
    @Override
    protected void registerGoals() {
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    /// 困难模式宝箱怪完成三次跳跃后短暂闭合。闭合期间完全停止移动并免疫伤害；
    /// 专家难度下，箭和枪弹还会由投射物自身按宝箱怪所有权反射。
    protected void beginDefense() {
        navigation.stop();
        setDeltaMovement(Vec3.ZERO);
        setMimicPose(MimicPose.DEFENDING);
    }

    @Override
    public PortProjectileDeflection deflection(Projectile projectile) {
        if (getMimicPose() == MimicPose.DEFENDING && canReflectProjectiles() && (projectile instanceof AbstractArrow || projectile instanceof BaseBulletEntity)) {
            return MIMIC_REFLECTION;
        }
        return PortProjectileDeflection.NONE;
    }

    private void tickDisengaged() {
        MimicPose pose = getMimicPose();
        if (pose != MimicPose.CLOSED && pose != MimicPose.CLOSING) {
            targetMissingTicks++;
            /// 脱战不能依赖落地。宝箱怪可能在跳跃时越过坡面或悬崖；若把落地作为前置条件，
            /// 它会在持续下落期间永久停留于跳跃姿态，也与 1.21 的定时闭合行为不一致。
            if (targetMissingTicks >= LOST_TARGET_TICKS) {
                navigation.stop();
                setHorizontalMotion(0.0, 0.0);
                setMimicPose(MimicPose.CLOSING);
            }
            return;
        }

        navigation.stop();
        setHorizontalMotion(0.0, 0.0);
        if (pose == MimicPose.CLOSING) {
            poseTicks++;
            if (poseTicks >= CLOSING_TICKS) {
                snapToIdleAngle();
                setMimicPose(MimicPose.CLOSED);
            }
        } else {
            snapToIdleAngle();
        }
    }

    private void beginJumpAttack(LivingEntity target) {
        lookAtTarget(target);
        Vec3 offset = target.position().subtract(position());
        double horizontalLength = Math.hypot(offset.x, offset.z);
        double horizontalPower = jumpHorizontalPower();
        double motionX = horizontalLength > 1.0E-4
                ? offset.x / horizontalLength * horizontalPower
                : 0.0;
        double motionZ = horizontalLength > 1.0E-4
                ? offset.z / horizontalLength * horizontalPower
                : 0.0;
        double motionY = getJumpPower() + jumpVerticalBonus();
        setDeltaMovement(motionX, motionY, motionZ);
        hasImpulse = true;
        attackCycle++;
        setMimicPose(MimicPose.JUMPING);
    }

    /// 困难宝箱怪每三轮使用更强的扑击；半血后普通跳跃也会加快。
    private double jumpHorizontalPower() {
        if (!isHardmodeVariant()) return attackCycle % 3 == 2 ? 0.62 : 0.46;
        if (attackCycle % 3 == 2) return 0.95;
        return getHealth() <= getMaxHealth() * 0.5F ? 0.72 : 0.62;
    }

    private double jumpVerticalBonus() {
        if (isHardmodeVariant() && attackCycle % 3 == 2) return 0.32;
        return 0.12;
    }

    private int nextJumpDelay() {
        if (isHardmodeVariant() && getHealth() <= getMaxHealth() * 0.5F) return 8;
        return isHardmodeVariant() ? 12 : 15;
    }

    protected boolean isHardmodeVariant() {
        return true;
    }

    /// 困难宝箱怪仅在专家及以上难度反射可反射弹幕。
    protected boolean canReflectProjectiles() {
        return isHardmodeVariant() && LibUtils.isAtLeastExpert(level(), blockPosition());
    }

    private void damageNearbyTarget(LivingEntity target) {
        double reach = getBbWidth() * 0.5 + target.getBbWidth() * 0.5 + 0.75;
        if (distanceToSqr(target) <= reach * reach) {
            doHurtTarget(target);
        }
    }

    private void lookAtTarget(LivingEntity target) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        setYRot(yaw);
        setYBodyRot(yaw);
        setYHeadRot(yaw);
    }

    private void setHorizontalMotion(double x, double z) {
        Vec3 motion = getDeltaMovement();
        setDeltaMovement(x, motion.y, z);
    }

    private void snapToIdleAngle() {
        float angle = entityData.get(DATA_IDLE_ANGLE);
        if (angle < 0.0F) return;
        setYRot(angle);
        setYBodyRot(angle);
        setYHeadRot(angle);
    }

    private void setIdleAngle(int angle) {
        int cardinalAngle = Math.floorMod(angle, 360) / 90 * 90;
        entityData.set(DATA_IDLE_ANGLE, cardinalAngle);
        snapToIdleAngle();
    }

    private void setMimicPose(MimicPose pose) {
        if (getMimicPose() == pose) return;
        entityData.set(DATA_POSE, (byte) pose.ordinal());
        poseTicks = 0;
    }

    public MimicPose getMimicPose() {
        int id = Byte.toUnsignedInt(entityData.get(DATA_POSE));
        MimicPose[] values = MimicPose.values();
        return id < values.length ? values[id] : MimicPose.CLOSED;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(IDLE_ANGLE_TAG, entityData.get(DATA_IDLE_ANGLE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(IDLE_ANGLE_TAG, Tag.TAG_INT)) {
            setIdleAngle(tag.getInt(IDLE_ANGLE_TAG));
        }
        // 战斗目标不跨重载恢复；重新加载时必须先回到无攻击性的闭合伪装。
        entityData.set(DATA_POSE, (byte) MimicPose.CLOSED.ordinal());
        poseTicks = 0;
        targetMissingTicks = 0;
        jumpCooldown = 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 2, state -> state.setAndContinue(
                switch (getMimicPose()) {
                    case CLOSED -> CLOSED;
                    case OPENING, ACTIVE -> OPEN;
                    case JUMPING -> JUMP;
                    case DEFENDING, CLOSING -> CLOSE;
                })));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.METAL_HURT.get();
    }

    public enum MimicPose {
        CLOSED,
        OPENING,
        ACTIVE,
        JUMPING,
        DEFENDING,
        CLOSING
    }
}
