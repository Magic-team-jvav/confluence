package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.model.CrownOfKingSlimeModelEntity;
import org.confluence.mod.common.init.entity.MonsterEntities;

/**
 * 史莱姆王。
 *
 * <p>服务端以三个显式阶段推进战斗：常态跳跃、缩小传送、重新膨胀。常态阶段按固定节奏完成
 * 四次跳跃后传送；专家及以上且生命不足一半时，会连续完成两轮跳跃再传送。阶段、计时器和
 * 当前轮次会一起存档，避免区块卸载后出现尺寸、无敌状态和碰撞箱彼此不一致。</p>
 *
 * <p>逻辑尺寸与 1.21 侧一致，满血为 16、濒死最低为 6。尺寸同时决定碰撞箱和客户端模型
 * 缩放，不能只改变其中一侧。缩放阶段拒绝伤害和接触攻击；常态受伤时按生命阈值生成蓝史莱姆，
 * 并按难度额外生成尖刺史莱姆。</p>
 */
public class KingSlime extends BaseBoss {
    private static final String RUNTIME_TAG = "ConfluenceKingSlimeRuntime";
    private static final int RUNTIME_VERSION = 2;
    private static final int TRANSFORM_TICKS = 20;
    private static final int NORMAL_CYCLE_END = 85;
    private static final int MIN_SIZE = 6;
    private static final int MAX_HEALTH_SIZE_BONUS = 10;
    /**
     * 1.21 侧实体类型的基础尺寸为 0.6，每一级体型都在该尺寸上等比放大。
     * 碰撞箱必须与客户端使用的史莱姆模型同步缩放，否则会出现模型远大于实际判定箱的问题。
     */
    private static final float BASE_DIMENSION_PER_SIZE = 0.6F;

    private static final EntityDataAccessor<Byte> DATA_PHASE =
            SynchedEntityData.defineId(KingSlime.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_PHASE_TICKS =
            SynchedEntityData.defineId(KingSlime.class, EntityDataSerializers.INT);

    private CombatPhase phase = CombatPhase.NORMAL;
    private int normalTicks;
    private int roundsRemaining = 1;
    private float oldSquish;
    private float squish;
    private float targetSquish;
    private boolean wasOnGround;

    private enum CombatPhase {
        NORMAL(0),
        SHRINKING(1),
        ENLARGING(2);

        private final byte id;

        CombatPhase(int id) {
            this.id = (byte) id;
        }

        private static CombatPhase byId(byte id) {
            return switch (id) {
                case 1 -> SHRINKING;
                case 2 -> ENLARGING;
                default -> NORMAL;
            };
        }
    }

    public KingSlime(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 800;
        setMaxUpStep(1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PHASE, CombatPhase.NORMAL.id);
        entityData.define(DATA_PHASE_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 728.0)
                .add(Attributes.ATTACK_DAMAGE, 16.5)
                .add(Attributes.ATTACK_KNOCKBACK, 2.2)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 100.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.BLUE;
    }

    /**
     * 史莱姆王的阶段本身就是一个长期运行的行为节点。目标选择仍由原版目标选择器负责，
     * 节点只消费当前目标并推进确定性的战斗节奏。
     */
    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        tickCombatState();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2,
                new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    void tickCombatState() {
        if (level().isClientSide || isNoAi() || isRemoved()) {
            return;
        }
        switch (phase) {
            case NORMAL -> tickNormalPhase();
            case SHRINKING -> tickShrinkingPhase();
            case ENLARGING -> tickEnlargingPhase();
        }
    }

    /**
     * 只在落地或液体中推进节拍，保证一次跳跃不会因滞空时间不同而被重复触发。
     * 20、40、60 为普通跳，80 为更高的收尾跳，85 后进入下一轮或开始传送。
     */
    private void tickNormalPhase() {
        if (!(onGround() || isInWater() || isInLava())) {
            return;
        }

        if (isInWater() || isInLava()) {
            normalTicks++;
            moveInLiquid();
            finishNormalCycle();
            return;
        }

        int acceleration = Math.max(0, (int) ((1.0F - getLogicalSize() / 127.0F) * 3.0F));
        normalTicks = advanceToJumpMarker(normalTicks, acceleration);
        if (normalTicks == 20 || normalTicks == 40 || normalTicks == 60 || normalTicks == 80) {
            jumpTowardTarget(normalTicks == 80);
        } else {
            setHorizontalMovement(Vec3.ZERO);
        }

        finishNormalCycle();
    }

    /**
     * 结算当前常态周期。陆地跳跃和液体漂浮只改变移动方式，使用同一套轮次与传送节拍。
     */
    private void finishNormalCycle() {
        if (normalTicks < NORMAL_CYCLE_END) {
            return;
        }
        if (--roundsRemaining > 0) {
            normalTicks = 0;
            return;
        }
        enterPhase(CombatPhase.SHRINKING);
    }

    private int advanceToJumpMarker(int current, int acceleration) {
        int next = current + 1;
        for (int marker : new int[]{20, 40, 60, 80}) {
            if (next > marker - acceleration && next <= marker) {
                return marker;
            }
        }
        return next + acceleration;
    }

    private void jumpTowardTarget(boolean finishingJump) {
        LivingEntity target = getTarget();
        Vec3 direction;
        if (target == null) {
            float angle = random.nextFloat() * Mth.TWO_PI;
            direction = new Vec3(Mth.cos(angle), 0.0, Mth.sin(angle));
        } else {
            Vec3 offset = target.position().subtract(position());
            direction = new Vec3(offset.x, 0.0, offset.z);
            if (direction.lengthSqr() > 1.0E-6) {
                direction = direction.normalize();
                float wantedYaw = (float) (Mth.atan2(-direction.x, direction.z) * Mth.RAD_TO_DEG);
                setYRot(Mth.rotLerp(0.35F, getYRot(), wantedYaw));
                yBodyRot = getYRot();
            }
        }

        double horizontalSpeed = LibUtils.switchByDifficulty(
                level(), blockPosition(), 1.1F, 1.35F, 1.55F, 1.8F);
        double baseVerticalSpeed = finishingJump
                ? LibUtils.switchByDifficulty(
                level(), blockPosition(), 2.0F, 2.25F, 2.5F, 2.75F)
                : LibUtils.switchByDifficulty(
                level(), blockPosition(), 1.5F, 1.75F, 2.0F, 2.25F);
        double verticalSpeed = baseVerticalSpeed * (getLogicalSize() + 127.0) / 256.0;
        setDeltaMovement(direction.x * horizontalSpeed, verticalSpeed,
                direction.z * horizontalSpeed);
        hasImpulse = true;
    }

    private void moveInLiquid() {
        LivingEntity target = getTarget();
        Vec3 direction = target == null
                ? Vec3.ZERO
                : new Vec3(target.getX() - getX(), 0.0, target.getZ() - getZ());
        if (direction.lengthSqr() > 1.0E-6) {
            direction = direction.normalize().scale(LibUtils.switchByDifficulty(
                    level(), blockPosition(), 0.1F, 0.15F, 0.2F, 0.25F));
        }
        setHorizontalMovement(direction);
        setDeltaMovement(getDeltaMovement().add(0.0, 0.05, 0.0));
    }

    private void tickShrinkingPhase() {
        int ticks = getPhaseTicks() + 1;
        setPhaseTicks(ticks);
        setHorizontalMovement(Vec3.ZERO);
        if (ticks < TRANSFORM_TICKS) {
            return;
        }

        teleportNearTarget();
        enterPhase(CombatPhase.ENLARGING);
    }

    private void tickEnlargingPhase() {
        int ticks = getPhaseTicks() + 1;
        setPhaseTicks(ticks);
        setHorizontalMovement(Vec3.ZERO);
        if (ticks >= TRANSFORM_TICKS) {
            enterPhase(CombatPhase.NORMAL);
        }
    }

    private void teleportNearTarget() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Player randomPlayer = serverLevel.getRandomPlayer();
        if (randomPlayer == null && getTarget() instanceof Player playerTarget) {
            randomPlayer = playerTarget;
        }
        if (randomPlayer == null) {
            return;
        }

        LivingEntity target = getTarget();
        Vec3 targetPosition = target == null
                ? randomPlayer.getOnPos().getCenter()
                : target.getOnPos().getCenter();
        serverLevel.addFreshEntity(new CrownOfKingSlimeModelEntity(
                serverLevel,
                position().add(0.0, getDimensions(getPose()).height, 0.0)));

        Vec3 destination;
        if (!isExpert()) {
            Vec3 direction = randomPlayer.getLookAngle().multiply(-1.0, 0.0, -1.0).normalize();
            destination = targetPosition.add(direction.scale(5.0));
        } else {
            float angle = random.nextFloat() * 3.14F;
            destination = targetPosition.add(
                    Mth.cos(angle) * 10.0,
                    0.0,
                    Mth.sin(angle) * 10.0);
        }
        teleportTo(destination.x, destination.y + 2.0, destination.z);
        setDeltaMovement(Vec3.ZERO);
    }

    private void enterPhase(CombatPhase newPhase) {
        phase = newPhase;
        entityData.set(DATA_PHASE, newPhase.id);
        setPhaseTicks(0);
        if (newPhase == CombatPhase.NORMAL) {
            normalTicks = 0;
            roundsRemaining = isExpert() && getHealth() / getMaxHealth() < 0.5F
                    ? 2 : 1;
        }
        refreshDimensions();
    }

    private void setPhaseTicks(int ticks) {
        entityData.set(DATA_PHASE_TICKS, ticks);
        refreshDimensions();
    }

    private int getPhaseTicks() {
        return entityData.get(DATA_PHASE_TICKS);
    }

    private int getLogicalSize() {
        int maximum = Math.round(getHealth() / getMaxHealth() * MAX_HEALTH_SIZE_BONUS)
                + MIN_SIZE;
        float factor = switch (phase) {
            case NORMAL -> 1.0F;
            case SHRINKING -> 1.0F - (float) getPhaseTicks() / TRANSFORM_TICKS;
            case ENLARGING -> (float) getPhaseTicks() / TRANSFORM_TICKS;
        };
        return Mth.clamp(Math.round(maximum * factor), 1, maximum);
    }

    /**
     * 客户端渲染器使用连续值插值，避免同步的整数碰撞尺寸造成逐格缩放。
     */
    public float getVisualSize(float partialTick) {
        float maximum = getHealth() / getMaxHealth() * MAX_HEALTH_SIZE_BONUS + MIN_SIZE;
        float ticks = getPhaseTicks() + partialTick;
        return switch (phase) {
            case NORMAL -> maximum;
            case SHRINKING -> Math.max(0.0F,
                    maximum * (1.0F - ticks / TRANSFORM_TICKS));
            case ENLARGING -> Math.min(maximum,
                    maximum * ticks / TRANSFORM_TICKS);
        };
    }

    /**
     * 上一游戏刻的挤压值，供客户端在两刻之间平滑插值。
     */
    public float getOldSquish() {
        return oldSquish;
    }

    /**
     * 当前游戏刻的挤压值。
     */
    public float getSquish() {
        return squish;
    }

    private void setHorizontalMovement(Vec3 movement) {
        Vec3 current = getDeltaMovement();
        setDeltaMovement(movement.x, current.y, movement.z);
    }

    @Override
    public void tick() {
        oldSquish = squish;
        boolean groundedBeforeTick = onGround();
        super.tick();
        if (onGround() && !groundedBeforeTick) {
            targetSquish = -0.5F;
        } else if (!onGround() && wasOnGround) {
            targetSquish = 1.0F;
        }
        squish += (targetSquish - squish) * 0.5F;
        targetSquish *= 0.6F;
        wasOnGround = onGround();
        resetFallDistance();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (phase != CombatPhase.NORMAL || source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
            return false;
        }

        int slimesBefore = getSlimesLeft();
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide) {
            for (int i = getSlimesLeft(); i < slimesBefore; i++) {
                spawnSplitSlimes();
            }
        }
        return hurt;
    }

    private int getSlimesLeft() {
        return (int) (getHealth() / getMaxHealth() * getTotalSplits());
    }

    private int getTotalSplits() {
        return LibUtils.switchByDifficulty(
                level(), blockPosition(), 30, 50, 75, 100);
    }

    private void spawnSplitSlimes() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        spawnSlime(serverLevel, MonsterEntities.BLUE_SLIME.get());
        float spikedChance = LibUtils.switchByDifficulty(
                level(), blockPosition(), 0.0F, 0.5F, 0.75F, 1.0F);
        if (random.nextFloat() < spikedChance) {
            spawnSlime(serverLevel, MonsterEntities.SPIKED_SLIME.get());
        }
    }

    private void spawnSlime(ServerLevel serverLevel, EntityType<?> slimeType) {
        Entity entity = slimeType.create(serverLevel);
        if (!(entity instanceof Mob slime)) {
            return;
        }
        slime.setPos(getX() + (random.nextFloat() - 0.5F) * 2.0F,
                getY() + 0.5, getZ() + (random.nextFloat() - 0.5F) * 2.0F);
        slime.setTarget(getTarget());
        serverLevel.addFreshEntity(slime);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float dimension = BASE_DIMENSION_PER_SIZE * getLogicalSize();
        return EntityDimensions.scalable(dimension, dimension);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_PHASE.equals(key)) {
            phase = CombatPhase.byId(entityData.get(DATA_PHASE));
            refreshDimensions();
        } else if (DATA_PHASE_TICKS.equals(key)) {
            refreshDimensions();
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && !(target instanceof Slime);
    }

    @Override
    public void playerTouch(Player player) {
        if (phase == CombatPhase.NORMAL) {
            super.playerTouch(player);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putString("Phase", phase.name());
        runtime.putInt("PhaseTicks", getPhaseTicks());
        runtime.putInt("NormalTicks", normalTicks);
        runtime.putInt("RoundsRemaining", roundsRemaining);
        tag.put(RUNTIME_TAG, runtime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        resetRuntime();
        if (!tag.contains(RUNTIME_TAG, CompoundTag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag runtime = tag.getCompound(RUNTIME_TAG);
        if (!isValidRuntime(runtime)) {
            return;
        }
        CombatPhase restoredPhase = CombatPhase.valueOf(runtime.getString("Phase"));
        phase = restoredPhase;
        entityData.set(DATA_PHASE, restoredPhase.id);
        entityData.set(DATA_PHASE_TICKS, runtime.getInt("PhaseTicks"));
        normalTicks = runtime.getInt("NormalTicks");
        roundsRemaining = runtime.getInt("RoundsRemaining");
        refreshDimensions();
    }

    private boolean isValidRuntime(CompoundTag runtime) {
        if (runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("Phase", CompoundTag.TAG_STRING)
                || !runtime.contains("PhaseTicks", CompoundTag.TAG_INT)
                || !runtime.contains("NormalTicks", CompoundTag.TAG_INT)
                || !runtime.contains("RoundsRemaining", CompoundTag.TAG_INT)) {
            return false;
        }

        CombatPhase restoredPhase;
        try {
            restoredPhase = CombatPhase.valueOf(runtime.getString("Phase"));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
        int phaseTicks = runtime.getInt("PhaseTicks");
        int restoredNormalTicks = runtime.getInt("NormalTicks");
        int restoredRounds = runtime.getInt("RoundsRemaining");
        return phaseTicks >= 0
                && phaseTicks < (restoredPhase == CombatPhase.NORMAL ? 1 : TRANSFORM_TICKS)
                && restoredNormalTicks >= 0
                && restoredNormalTicks < NORMAL_CYCLE_END
                && restoredRounds >= 1
                && restoredRounds <= 2;
    }

    private void resetRuntime() {
        phase = CombatPhase.NORMAL;
        entityData.set(DATA_PHASE, CombatPhase.NORMAL.id);
        entityData.set(DATA_PHASE_TICKS, 0);
        normalTicks = 0;
        roundsRemaining = 1;
        refreshDimensions();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean shouldDiscardWhenNoTarget() {
        return true;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide) {
            for (int i = 0; i < 4; i++) {
                spawnSplitSlimes();
            }
        }
    }

    String getCombatPhaseName() {
        return phase.name();
    }

    int getNormalTicks() {
        return normalTicks;
    }

    int getRoundsRemaining() {
        return roundsRemaining;
    }

    int getCurrentLogicalSize() {
        return getLogicalSize();
    }
}
