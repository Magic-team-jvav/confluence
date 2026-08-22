package org.confluence.mod.common.entity.boss;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.DestroyerLaserProjectile;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.ModEntities;

/// 毁灭者头部，负责体节链、激光和探测器的统一生命周期。
///
/// <p>奇数编号体节是探测器舱。每个舱室最多释放一次探测器，释放记录由头部持久化，
/// 所以体节因区块卸载而重建时不会重置。未释放的探测器舱会按照 1.21 侧的顺序射击
/// 与高空齐射节奏发射真实激光弹幕。</p>
public class TheDestroyer extends BaseWormBoss {
    private static final String RELEASED_PROBE_SEGMENTS_TAG = "ReleasedProbeSegments";
    private static final String PHASE_TAG = "Phase";
    private static final String PHASE_TIMER_TAG = "PhaseTimer";
    private static final String LASER_SEQUENCE_INDEX_TAG = "LaserSequenceIndex";
    private static final String VOLLEY_COOLDOWN_TAG = "VolleyCooldown";
    private static final String CAVE_ATTACK_STATE_TAG = "CaveAttackState";
    private static final String GROUND_ATTACK_STATE_TAG = "GroundAttackState";
    private static final String SKY_ATTACK_STATE_TAG = "SkyAttackState";

    public static final int SEGMENT_COUNT = 80;
    public static final float SEGMENT_SPACING = 3.2F;
    private static final int DEEP_Y = 60;
    private static final int SKY_Y = 100;
    private static final double BASE_MOVE_SPEED = 1.0;
    private static final float BASE_TURN_SPEED = 9.0F;
    private static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_BODY_ROLL = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.FLOAT);

    private final long[] releasedProbeSegments = new long[2];
    private int phaseTimer;
    private int laserSequenceIndex = -1;
    private int volleyCooldown;
    private int caveAttackState;
    private int groundAttackState;
    private int skyAttackState;
    private float previousBodyRoll;
    private boolean performingBarrelRoll;

    public enum Phase {
        UNDERGROUND,
        GROUND,
        SKY
    }

    public TheDestroyer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        xpReward = 2000;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PHASE, Phase.GROUND.ordinal());
        entityData.define(DATA_BODY_ROLL, 0.0F);
    }

    @Override
    protected int getSegmentCount() {
        return SEGMENT_COUNT;
    }

    @Override
    protected float getSegmentSpacing() {
        return SEGMENT_SPACING;
    }

    /// 在头部附近盘曲生成完整机械体节链。
    ///
    /// <p>1.21 会以直线生成八十节身体，但在 Minecraft 中这会跨越十六个区块，
    /// 超出 Boss 当前强加载区域后导致尾部创建失败。这里保留相同数量和间距，
    /// 只把初始形状改成盘曲布局；进入战斗后仍由统一跟随物理自然展开。</p>
    @Override
    protected Vec3 getInitialSegmentPosition(int index, Vec3 previousPosition) {
        Vec3 direction = getLookAngle().multiply(1.0, 0.0, 1.0);
        if (direction.lengthSqr() <= 1.0E-7) {
            direction = new Vec3(0.0, 0.0, 1.0);
        }
        return previousPosition.add(direction.normalize().scale(-SEGMENT_SPACING).yRot(index * 0.16F));
    }

    /// 毁灭者和世界吞噬怪一样不能依赖地面路径。
    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createWormBossAttributes()
                .add(Attributes.MAX_HEALTH, 23333.0)
                .add(Attributes.ATTACK_DAMAGE, 35.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 96.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        previousBodyRoll = getBodyRoll();
        super.tick();
        if (isRemoved() || level().isClientSide) {
            return;
        }
        if (getTarget() == null && tickCount % 30 == 0) {
            Player replacement = findCombatPlayer();
            if (replacement != null) {
                setTarget(replacement);
            }
        }
        LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            updatePhase(target);
            tickPhaseMovement(target);
            updateSegmentRolls();
            for (BossWormPart segment : getSegments()) {
                segment.updateDestroyerPresentation(this);
            }
            tickLaserControl(target);
        }
    }

    private void updatePhase(LivingEntity target) {
        Phase expected = target.getY() < DEEP_Y
                ? Phase.UNDERGROUND
                : target.getY() > SKY_Y
                ? Phase.SKY
                : Phase.GROUND;
        if (getPhase() != expected) {
            entityData.set(DATA_PHASE, expected.ordinal());
            phaseTimer = 0;
            caveAttackState = 0;
            groundAttackState = 0;
            skyAttackState = 0;
            performingBarrelRoll = false;
        }
    }

    /// 按目标高度执行地下钻击、地表跃出和高空俯冲。
    ///
    /// <p>1.20 仅调整为连续三维转向；阶段边界、主要目标点与速度倍率保持 1.21
    /// 语义。速度交给原版实体同步消费，不通过逐刻传送移动。</p>
    private void tickPhaseMovement(LivingEntity target) {
        phaseTimer++;
        switch (getPhase()) {
            case UNDERGROUND -> tickUndergroundMovement(target);
            case GROUND -> tickGroundMovement(target);
            case SKY -> tickSkyMovement(target);
        }
    }

    private void tickUndergroundMovement(LivingEntity target) {
        double speed = BASE_MOVE_SPEED;
        float turn = BASE_TURN_SPEED;
        float rollSpeed = 15.0F;
        switch (caveAttackState) {
            case 0 -> {
                if (phaseTimer > 100) {
                    caveAttackState = 1;
                    phaseTimer = 0;
                }
            }
            case 1 -> {
                double progress = phaseTimer / 20.0;
                rollSpeed = (float) (15.0 * (1.0 + 1.5 * progress));
                speed = BASE_MOVE_SPEED * (1.0 - 0.4 * progress);
                if (phaseTimer > 20) {
                    caveAttackState = 2;
                    phaseTimer = 0;
                    Vec3 direction = target.getEyePosition().subtract(getEyePosition());
                    if (direction.lengthSqr() > 1.0E-7) {
                        setDeltaMovement(direction.normalize().scale(BASE_MOVE_SPEED * 1.5));
                    }
                }
            }
            case 2 -> {
                rollSpeed = 37.5F;
                speed = BASE_MOVE_SPEED * 1.5;
                turn = BASE_TURN_SPEED * 0.05F;
                if (phaseTimer > 30) {
                    caveAttackState = 3;
                    phaseTimer = 0;
                }
            }
            case 3 -> {
                double progress = phaseTimer / 20.0;
                rollSpeed = (float) (15.0 * (2.5 - 1.5 * progress));
                speed = BASE_MOVE_SPEED * (1.5 - 0.5 * progress);
                turn = (float) (BASE_TURN_SPEED * (0.05 + 0.95 * progress));
                if (phaseTimer > 20) {
                    caveAttackState = 0;
                    phaseTimer = 0;
                }
            }
            default -> caveAttackState = 0;
        }
        Vec3 targetVelocity = target.getEyePosition().subtract(getEyePosition());
        if (targetVelocity.lengthSqr() > 1.0E-7) {
            targetVelocity = targetVelocity.normalize().scale(speed);
            double maximumTurn = turn * Mth.DEG_TO_RAD;
            Vec3 velocity = LibMathUtils.interpolateBasis(getDeltaMovement(), targetVelocity, angle -> Math.min(angle, maximumTurn), difference -> difference * 0.25);
            faceVelocity(velocity);
            setDeltaMovement(velocity);
        }
        setBodyRoll((getBodyRoll() + rollSpeed) % 360.0F);
    }

    private void tickGroundMovement(LivingEntity target) {
        switch (groundAttackState) {
            case 0 -> {
                Vec3 targetPosition = target.getEyePosition();
                double surfaceY = level().getHeight(Heightmap.Types.WORLD_SURFACE, Mth.floor(targetPosition.x), Mth.floor(targetPosition.z));
                targetPosition = new Vec3(targetPosition.x, surfaceY - 20.0, targetPosition.z);
                moveDirectlyToward(targetPosition, BASE_MOVE_SPEED * 1.35);
                setBodyRoll(getBodyRoll() + 20.0F);
                if (distanceToSqr(targetPosition) < 16.0) {
                    groundAttackState = 1;
                    phaseTimer = 0;
                }
            }
            case 1 -> {
                Vec3 targetPosition = target.getEyePosition();
                moveDirectlyToward(targetPosition, BASE_MOVE_SPEED * 1.5);
                setBodyRoll(getBodyRoll() + 12.5F);
                if (targetPosition.distanceToSqr(getEyePosition()) < 36.0) {
                    groundAttackState = 2;
                    phaseTimer = 0;
                }
            }
            case 2 -> {
                // 1.21 此状态会额外推进一次计时，保持同样的下坠起点与恢复节奏。
                phaseTimer++;
                if (phaseTimer > 45) {
                    setDeltaMovement(getDeltaMovement().subtract(0.0, 0.05, 0.0));
                    setBodyRoll(getBodyRoll() + 5.0F);
                } else {
                    smoothResetRoll();
                }
                if (phaseTimer > 80) {
                    groundAttackState = 0;
                    phaseTimer = 0;
                }
            }
            default -> groundAttackState = 0;
        }
    }

    private void tickSkyMovement(LivingEntity target) {
        if (!performingBarrelRoll && random.nextInt(300) == 0) {
            performingBarrelRoll = true;
        }
        if (performingBarrelRoll) {
            float roll = getBodyRoll() + 25.0F;
            setBodyRoll(roll);
            if (roll > 720.0F) {
                setBodyRoll(0.0F);
                performingBarrelRoll = false;
            }
        } else {
            smoothResetRoll();
        }
        switch (skyAttackState) {
            case 0 -> {
                Vec3 hoverTarget = target.position().add(0.0, 25.0, 0.0);
                moveDirectlyToward(hoverTarget, BASE_MOVE_SPEED * 1.5);
                if (distanceToSqr(hoverTarget) < 256.0 || phaseTimer > 60) {
                    skyAttackState = 1;
                    phaseTimer = 0;
                    volleyCooldown = 20;
                }
            }
            case 1 -> {
                float angle = tickCount * 6.0F * Mth.DEG_TO_RAD;
                Vec3 attackPosition = target.position().add(Mth.sin(angle) * 15.0, 10.0, Mth.cos(angle) * 15.0);
                moveDirectlyToward(attackPosition, BASE_MOVE_SPEED * 2.25);
                if (phaseTimer > 60) {
                    skyAttackState = 2;
                    phaseTimer = 0;
                }
            }
            case 2 -> {
                moveDirectlyToward(target.getEyePosition(), BASE_MOVE_SPEED * 1.6);
                if (phaseTimer > 40) {
                    skyAttackState = 0;
                    phaseTimer = 0;
                }
            }
            default -> skyAttackState = 0;
        }
    }

    /// 地表与高空阶段在 1.21 中会立即朝向目标点，并沿新的正前方移动。
    private void moveDirectlyToward(Vec3 destination, double speed) {
        Vec3 direction = destination.subtract(getEyePosition());
        if (direction.lengthSqr() <= 1.0E-7) return;
        direction = direction.normalize();
        faceVelocity(direction);
        setDeltaMovement(direction.scale(speed));
    }

    private void faceVelocity(Vec3 velocity) {
        if (velocity.lengthSqr() <= 1.0E-7) return;
        lookAt(EntityAnchorArgument.Anchor.EYES, getEyePosition().add(velocity));
    }

    private void smoothResetRoll() {
        float roll = Mth.wrapDegrees(getBodyRoll());
        setBodyRoll(Math.abs(roll) > 2.0F ? roll * 0.8F : 0.0F);
    }

    /// 按照头部到尾部的顺序平滑传递滚转角，形成 1.21 的螺旋效果。
    private void updateSegmentRolls() {
        float previousRoll = getBodyRoll();
        for (BossWormPart segment : getSegments()) {
            float currentRoll = segment.getSegmentRoll();
            float difference = Mth.degreesDifference(currentRoll, previousRoll);
            if (Math.abs(difference) >= 10.0F) {
                currentRoll += difference > 0.0F
                        ? difference - 9.0F
                        : difference + 9.0F;
            } else {
                currentRoll += difference * 0.15F;
            }
            currentRoll = Mth.wrapDegrees(currentRoll);
            segment.setSegmentRoll(currentRoll);
            previousRoll = currentRoll;
        }
    }

    public float getBodyRoll() {
        return entityData.get(DATA_BODY_ROLL);
    }

    public float getPreviousBodyRoll() {
        return previousBodyRoll;
    }

    public void setBodyRoll(float roll) {
        entityData.set(DATA_BODY_ROLL, roll);
    }

    public Phase getPhase() {
        return Phase.values()[Mth.clamp(entityData.get(DATA_PHASE), 0, Phase.values().length - 1)];
    }

    @Override
    protected boolean hurtSegment(BossWormPart segment, DamageSource source, float amount) {
        boolean hurt = super.hurtSegment(segment, source, amount);
        if (!hurt || level().isClientSide) {
            return hurt;
        }
        if (getPhase() != Phase.UNDERGROUND && segment.isDestroyerProbeSegment() && !hasReleasedProbe(segment.getSegmentIndex()) && random.nextFloat() < 0.2F) {
            releaseProbe(segment);
        }
        return true;
    }

    /// 推进体节激光控制器。地表阶段偶尔从头到尾依次检查体节；高空阶段额外执行整链
    /// 齐射。只有侧翼已经打开、尚未释放探测器且不在实心方块中的探测器舱能够开火。
    private void tickLaserControl(LivingEntity target) {
        if (getPhase() == Phase.UNDERGROUND) {
            return;
        }
        if (getPhase() == Phase.SKY && volleyCooldown-- <= 0) {
            for (BossWormPart segment : getSegments()) {
                shootFromSegment(segment, target);
            }
            volleyCooldown = 80 + random.nextInt(40);
            return;
        }
        if (laserSequenceIndex >= 0) {
            if (laserSequenceIndex < getSegments().size()) {
                shootFromSegment(getSegments().get(laserSequenceIndex), target);
                laserSequenceIndex++;
            } else {
                laserSequenceIndex = -1;
            }
        } else if (random.nextInt(150) == 0) {
            laserSequenceIndex = 0;
        }
    }

    private boolean shootFromSegment(BossWormPart segment, LivingEntity target) {
        if (!segment.isAlive() || !segment.isDestroyerProbeSegment() || hasReleasedProbe(segment.getSegmentIndex()) || !segment.areDestroyerFlapsOpen() || level().getBlockState(segment.blockPosition()).isSolid()) {
            return false;
        }
        Vec3 origin = segment.position().add(0.0, segment.getBbHeight() * 0.5, 0.0);
        return fireLaser(this, origin, target, getLaserDamage());
    }

    /// 供回归测试和显式攻击事件使用：从首个满足条件的探测器舱发射一枚激光。
    int shootFromBody() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            return 0;
        }
        for (BossWormPart segment : getSegments()) {
            if (shootFromSegment(segment, target)) {
                return 1;
            }
        }
        return 0;
    }

    static boolean fireLaser(Monster owner, Vec3 origin, LivingEntity target, float damage) {
        if (!(owner.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        DestroyerLaserProjectile laser = ModEntities.DESTROYER_LASER.get().create(serverLevel);
        if (laser == null) {
            return false;
        }
        laser.configure(owner, origin, target, damage);
        return serverLevel.addFreshEntity(laser);
    }

    private float getLaserDamage() {
        return isMaster() ? 22.0F
                : isExpert() ? 18.0F : 14.0F;
    }

    /// 测试和显式战斗事件使用的批量入口。它按体节顺序释放尚未使用的探测器舱，
    /// 不会绕过每个舱室仅能释放一次的约束。
    void spawnProbes() {
        int requested = isMaster() ? 6 : isExpert() ? 4 : 2;
        for (BossWormPart segment : getSegments()) {
            if (requested <= 0) {
                break;
            }
            if (segment.isDestroyerProbeSegment() && !hasReleasedProbe(segment.getSegmentIndex()) && releaseProbe(segment)) {
                requested--;
            }
        }
    }

    boolean releaseProbe(BossWormPart segment) {
        int index = segment.getSegmentIndex();
        if (!(level() instanceof ServerLevel serverLevel) || !segment.isDestroyerProbeSegment() || hasReleasedProbe(index)) {
            return false;
        }
        markProbeReleased(index);
        TheDestroyerProbe probe = BossEntities.THE_DESTROYER_PROBE.get().create(serverLevel);
        if (probe == null) {
            return false;
        }
        probe.setPos(segment.position().add(0.0, 1.5, 0.0));
        probe.setMaster(this);
        if (getTarget() != null) {
            probe.setTarget(getTarget());
        }
        if (!serverLevel.addFreshEntity(probe)) {
            removeSubEntity(probe);
            return false;
        }
        return true;
    }

    boolean hasReleasedProbe(int segmentIndex) {
        if (segmentIndex < 1 || segmentIndex > SEGMENT_COUNT) {
            return false;
        }
        int bitIndex = segmentIndex - 1;
        return (releasedProbeSegments[bitIndex >>> 6] & 1L << (bitIndex & 63)) != 0L;
    }

    private void markProbeReleased(int segmentIndex) {
        int bitIndex = segmentIndex - 1;
        releasedProbeSegments[bitIndex >>> 6] |= 1L << (bitIndex & 63);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof TheDestroyer)
                && !(target instanceof TheDestroyerProbe)
                && super.canAttack(target);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLongArray(RELEASED_PROBE_SEGMENTS_TAG, releasedProbeSegments);
        tag.putInt(PHASE_TAG, getPhase().ordinal());
        tag.putInt(PHASE_TIMER_TAG, phaseTimer);
        tag.putInt(LASER_SEQUENCE_INDEX_TAG, laserSequenceIndex);
        tag.putInt(VOLLEY_COOLDOWN_TAG, volleyCooldown);
        tag.putInt(CAVE_ATTACK_STATE_TAG, caveAttackState);
        tag.putInt(GROUND_ATTACK_STATE_TAG, groundAttackState);
        tag.putInt(SKY_ATTACK_STATE_TAG, skyAttackState);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        long[] savedSegments = tag.getLongArray(RELEASED_PROBE_SEGMENTS_TAG);
        for (int index = 0; index < releasedProbeSegments.length; index++) {
            releasedProbeSegments[index] =
                    index < savedSegments.length
                            ? savedSegments[index] : 0L;
        }
        entityData.set(DATA_PHASE, Mth.clamp(tag.getInt(PHASE_TAG), 0, Phase.values().length - 1));
        phaseTimer = Mth.clamp(tag.getInt(PHASE_TIMER_TAG), 0, 10000);
        laserSequenceIndex = Mth.clamp(tag.getInt(LASER_SEQUENCE_INDEX_TAG), -1, SEGMENT_COUNT);
        volleyCooldown = Mth.clamp(tag.getInt(VOLLEY_COOLDOWN_TAG), 0, 120);
        caveAttackState = Mth.clamp(tag.getInt(CAVE_ATTACK_STATE_TAG), 0, 3);
        groundAttackState = Mth.clamp(tag.getInt(GROUND_ATTACK_STATE_TAG), 0, 2);
        skyAttackState = Mth.clamp(tag.getInt(SKY_ATTACK_STATE_TAG), 0, 2);
    }
}
