package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.effect.CameraShake;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.init.entity.BossEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/// 女皇在半血时结束当前地面动作，切换到空中追击。
public class QueenSlime extends BaseBoss {
    // 重砸落地时由服务端广播的实体事件编号，客户端收到后触发震屏。
    private static final byte SLAM_IMPACT_EVENT = 70;
    // 震屏半径，单位为格，从女皇落地点算起，偏角随距离线性衰减。
    private static final double SLAM_SHAKE_RADIUS = 28.0;
    // 落地点处的最大相机偏角，单位为度。
    private static final float SLAM_SHAKE_ANGLE = 3.0F;
    // 重砸震屏的持续时间，单位为游戏刻（20 刻为 1 秒）。
    private static final int SLAM_SHAKE_TICKS = 20;

    // 转入飞行阶段的剩余生命比例。
    private static final float PHASE_HEALTH = 0.5F;
    // 飞行阶段触发下一次召唤所需的最大生命损失比例。
    private static final float AIR_SUMMON_LOSS = 0.015F;
    // 地面阶段触发下一次召唤所需的最大生命损失比例。
    private static final float GROUND_SUMMON_LOSS = 0.02F;
    // 每次受伤触发召唤时的数量上限，最少一只。
    private static final int MAX_SUMMON_COUNT = 2;
    // 首次进入战斗时额外等待的刻数。
    private static final int START_DELAY = 33;
    // 每刻向目标转身的最大角度。
    private static final float TURN_SPEED = 12.0F;
    // 目标超过此距离（格）时累计传送计时。
    private static final double TELEPORT_DISTANCE = 24.0;
    // 目标与女皇的高度差超过此值（格）时累计传送计时。
    private static final int TELEPORT_HEIGHT_GAP = 12;
    // 每次换位可尝试的落点数量。
    private static final int TELEPORT_ATTEMPTS = 12;
    // 围绕目标搜索传送落点的水平半径（格）。
    private static final int TELEPORT_RADIUS = 8;
    // 从目标上方多少格开始寻找落脚点。
    private static final int TELEPORT_SEARCH_HEIGHT = 6;
    // 飞行阶段选择下一次攻击前的等待刻数。
    private static final int AIR_ATTACK_DELAY = 40;
    // 准备连续跳跃时在地面等待的刻数。
    private static final int HOP_START_DELAY = 5;
    // 跳完一轮后原地休息的刻数。
    private static final int GROUND_ATTACK_DELAY = 40;
    // 飞到目标上方时允许下砸的最大水平距离（格）。
    private static final double DIVE_RANGE = 16.0;
    // 跳跃中朝目标修正的水平速度（格/刻）。
    private static final double HOP_AIR_SPEED = 0.56;
    // 每刻消除水平速度偏差的比例。
    private static final double HOP_STEERING = 0.12;
    // 两次普通跳跃之间的落地停顿（刻）。
    private static final int HOP_LANDING_DELAY = 4;
    // 一轮连续跳跃的次数，最后两跳分别为低跳和高跳。
    private static final int HOPS_PER_ROUND = 4;
    // 低跳的向上初速（格/刻）。
    private static final double LOW_HOP_LIFT = 1.125;
    // 高跳的向上初速（格/刻）。
    private static final double HIGH_HOP_LIFT = 2.438;
    // 普通跳跃的向上初速（格/刻）。
    private static final double NORMAL_HOP_LIFT = 1.5;
    // 低跳的水平初速（格/刻）。
    private static final double LOW_HOP_SPEED = 0.844;
    // 高跳的水平初速（格/刻）。
    private static final double HIGH_HOP_SPEED = 0.656;
    // 普通跳跃的水平初速（格/刻）。
    private static final double NORMAL_HOP_SPEED = 0.75;
    // 重砸起跳时瞄准目标上方的高度（格）。
    private static final int SLAM_HEIGHT = 12;
    // 重砸开始上升前的等待刻数。
    private static final int RISE_DELAY = 10;
    // 向目标上方移动的最大刻数。
    private static final int RISE_TIMEOUT = 30;
    // 重砸上升时的最大速度（格/刻）。
    private static final double RISE_SPEED = 1.5;
    // 一阶段重砸开始下落前的等待刻数。
    private static final int GROUND_DROP_DELAY = 10;
    // 重砸蓄势时每刻保留的速度比例。
    private static final double DROP_WINDUP_DRAG = 0.6;
    // 重砸持续未触地时恢复追击的超时刻数。
    private static final int DROP_TIMEOUT = 120;
    // 重砸的最大下落速度（格/刻）。
    private static final double DROP_SPEED = 2.625;
    // 重砸每刻增加的下落速度（格/刻²）。
    private static final double DROP_ACCELERATION = 0.35;
    // 凝胶齐射从蓄力到发射的刻数。
    private static final int VOLLEY_WINDUP = 20;
    // 成功换位后恢复行动的刻数。
    private static final int TELEPORT_RECOVERY = 10;
    // 落地冲击传入难度换算的基础伤害。
    private static final int IMPACT_DAMAGE = 42;
    // 视线畅通时悬停在目标上方的高度（格）。
    private static final double HOVER_HEIGHT = 15.625;
    // 遮挡时向上检查空间的最大格数。
    private static final int HOVER_SCAN_HEIGHT = 16;
    // 将目标位置差换算为期望飞行速度的比例。
    private static final double FLIGHT_APPROACH = 0.16;
    // 飞行追击速度上限（格/刻）。
    private static final double FLIGHT_SPEED = 2.25;
    // 每刻朝期望飞行速度靠拢的比例。
    private static final double FLIGHT_STEERING = 0.22;
    // 地面停顿和重砸下降时每刻保留的水平速度比例。
    private static final double HORIZONTAL_DRAG = 0.8;
    // 落地冲击波扩张的总刻数。
    private static final int IMPACT_TICKS = 3;
    // 冲击波开始扩张前的半径（格）。
    private static final double IMPACT_START_RADIUS = 2.5;
    // 冲击波在整个扩张期间增加的半径（格）。
    private static final double IMPACT_GROWTH = 12.5;
    // 每刻在冲击波边缘均匀布置的粒子点数量。
    private static final int IMPACT_RING_POINTS = 32;

    private enum Action {IDLE, HOPS, RISE, DROP, VOLLEY, RECOVER, LANDING}

    private static final EntityDataAccessor<Byte> DATA_ACTION =
            SynchedEntityData.defineId(QueenSlime.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DATA_DIVING =
            SynchedEntityData.defineId(QueenSlime.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCENDING_FLIGHT =
            SynchedEntityData.defineId(QueenSlime.class, EntityDataSerializers.BOOLEAN);
    private Action action = Action.IDLE;
    private boolean groundHopsDue = true;
    // 不可达持续一秒才换位，过滤短暂遮挡；计数期间继续执行当前动作。
    private static final int TELEPORT_BLOCKED_TICKS = 20;
    // 成功传送后的冷却，防止连续换位。
    private static final int TELEPORT_COOLDOWN_TICKS = 100;
    private int teleportCooldown;
    private int ticks = -START_DELAY;
    private static final double JUMP_TAKEOFF_FRAME = 0.33333, JUMP_APEX_FRAME = 0.75;
    // 模型的 falling 收翼段为 1.25 秒，播完再进入实际下坠。
    private static final int WING_FOLD_TICKS = 25;
    private static final int LANDING_TICKS = 17;
    private static final EntityDataAccessor<Byte> HOP_STAGE = SynchedEntityData.defineId(QueenSlime.class, EntityDataSerializers.BYTE);
    private static final byte WINDUP = 1, ASCENT = 2, DESCENT = 3;
    private static final EntityDataAccessor<Float> ASCENT_SPEED = SynchedEntityData.defineId(QueenSlime.class, EntityDataSerializers.FLOAT);
    private static final byte LAND_EVENT = 71;
    private int hops;
    private Vec3 impactCenter;
    private int impactTicks;
    private final Set<UUID> impactHits = new HashSet<>();
    private int blockedFrames;
    private float lastSummonHealth = -1;
    private boolean flying;

    private void releaseSlimes() {
        if (lastSummonHealth < 0) lastSummonHealth = getMaxHealth();
        int threshold = (int) (getMaxHealth() * (flying ? AIR_SUMMON_LOSS : GROUND_SUMMON_LOSS));
        if (getHealth() + threshold >= lastSummonHealth) return;
        lastSummonHealth = getHealth();
        int count = 1 + random.nextInt(MAX_SUMMON_COUNT);
        for (int i = 0; i < count; i++) {
            var type = switch (random.nextInt(3)) {
                case 0 -> BossEntities.CRYSTAL_SLIME.get();
                case 1 -> BossEntities.BOUNCY_SLIME.get();
                default -> BossEntities.HEAVENLY_SLIME.get();
            };
            var minion = type.create(level());
            if (minion != null) {
                minion.attach(this);
                level().addFreshEntity(minion);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("LastSummonHealth", lastSummonHealth);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        lastSummonHealth = tag.contains("LastSummonHealth") ? tag.getFloat("LastSummonHealth") : getHealth();
        flying = isFlyingPhase();
    }

    public QueenSlime(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setMaxUpStep(1.0F);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.PINK;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ACTION, (byte) Action.IDLE.ordinal());
        entityData.define(DATA_DIVING, false);
        entityData.define(DATA_DESCENDING_FLIGHT, false);
        entityData.define(HOP_STAGE, (byte) 0);
        entityData.define(ASCENT_SPEED, 1.0F);
    }

    public boolean isFlyingPhase() {
        return getHealth() <= getMaxHealth() * PHASE_HEALTH;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        tickQueenMovement();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    private void tickQueenMovement() {
        tickImpact();
        if (flying != isFlyingPhase()) {
            flying = isFlyingPhase();
            groundHopsDue = true;
            lastSummonHealth = getHealth();
            setDeltaMovement(Vec3.ZERO);
            begin(Action.IDLE);
        }
        releaseSlimes();
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !canAttack(target)) {
            if (action != Action.IDLE) begin(Action.IDLE);
            setNoGravity(false);
            noPhysics = false;
            blockedFrames = 0;
            return;
        }

        Vec3 offset = target.position().subtract(position());
        Vec3 horizontal = new Vec3(offset.x, 0, offset.z);
        if (horizontal.lengthSqr() > 0.01) {
            horizontal = horizontal.normalize();
            if (action != Action.DROP) faceCombatDirection(horizontal, TURN_SPEED, 0.0F);
        }
        boolean airborne = flying && action != Action.LANDING;
        setNoGravity(airborne || action == Action.RISE || action == Action.DROP);
        noPhysics = action == Action.RISE;
        ticks++;

        if (teleportCooldown > 0) teleportCooldown--;
        if (!flying && (offset.lengthSqr() > TELEPORT_DISTANCE * TELEPORT_DISTANCE || !hasLineOfSight(target) || Math.abs(offset.y) > TELEPORT_HEIGHT_GAP))
            blockedFrames++;
        else blockedFrames = Math.max(0, blockedFrames - 1);
        if (!flying && teleportCooldown == 0 && blockedFrames >= TELEPORT_BLOCKED_TICKS && onGround()
                && (action == Action.IDLE || action == Action.HOPS)) {
            blockedFrames = 0;
            for (int i = 0; i < TELEPORT_ATTEMPTS; i++) {
                if (randomTeleport(target.getX() + random.nextInt(2 * TELEPORT_RADIUS + 1) - TELEPORT_RADIUS, target.getY() + TELEPORT_SEARCH_HEIGHT,
                        target.getZ() + random.nextInt(2 * TELEPORT_RADIUS + 1) - TELEPORT_RADIUS, true)) {
                    teleportCooldown = TELEPORT_COOLDOWN_TICKS;
                    groundHopsDue = true;
                    begin(Action.RECOVER);
                    return;
                }
            }
        }

        switch (action) {
            case IDLE -> {
                if (flying) {
                    hover(target);
                } else {
                    if (!onGround()) {
                        ticks--;
                        return;
                    }
                    Vec3 motion = getDeltaMovement();
                    setDeltaMovement(motion.x * HORIZONTAL_DRAG, motion.y, motion.z * HORIZONTAL_DRAG);
                }
                if (ticks >= (flying ? AIR_ATTACK_DELAY : groundHopsDue ? HOP_START_DELAY : GROUND_ATTACK_DELAY)) {
                    if (flying) {
                        boolean aboveTarget = offset.y < 0 && offset.horizontalDistanceSqr() < DIVE_RANGE * DIVE_RANGE;
                        begin(random.nextBoolean() && aboveTarget ? Action.DROP : Action.VOLLEY);
                    } else if (groundHopsDue) {
                        hops = 0;
                        begin(Action.HOPS);
                    } else {
                        begin(random.nextBoolean() ? Action.RISE : Action.VOLLEY);
                    }
                }
            }
            case HOPS -> {
                if (!onGround()) {
                    if (getDeltaMovement().y <= 0) entityData.set(HOP_STAGE, DESCENT);
                    Vec3 motion = getDeltaMovement();
                    Vec3 steering = horizontal.scale(HOP_AIR_SPEED).subtract(new Vec3(motion.x, 0, motion.z));
                    setDeltaMovement(motion.add(steering.scale(HOP_STEERING)));
                    return;
                }
                if (entityData.get(HOP_STAGE) == DESCENT || entityData.get(HOP_STAGE) == ASCENT) {
                    level().broadcastEntityEvent(this, LAND_EVENT);
                    entityData.set(HOP_STAGE, (byte) 0);
                    ticks = -HOP_LANDING_DELAY;
                }
                if (ticks < 0) return;
                if (hops >= HOPS_PER_ROUND) {
                    groundHopsDue = false;
                    begin(Action.IDLE);
                    return;
                }
                double lift = hops == HOPS_PER_ROUND - 2 ? LOW_HOP_LIFT : hops == HOPS_PER_ROUND - 1 ? HIGH_HOP_LIFT : NORMAL_HOP_LIFT;
                if (entityData.get(HOP_STAGE) != WINDUP) {
                    int ascentTicks = 0;
                    for (double vy = lift; vy > 0; vy = (vy - 0.08) * 0.98) ascentTicks++;
                    entityData.set(ASCENT_SPEED, (float) ((JUMP_APEX_FRAME - JUMP_TAKEOFF_FRAME) * 20 / ascentTicks));
                    entityData.set(HOP_STAGE, WINDUP);
                    ticks = 0;
                }
                if (ticks < Math.round(JUMP_TAKEOFF_FRAME * 20 / entityData.get(ASCENT_SPEED)))
                    return;
                double speed = hops == HOPS_PER_ROUND - 2 ? LOW_HOP_SPEED : hops == HOPS_PER_ROUND - 1 ? HIGH_HOP_SPEED : NORMAL_HOP_SPEED;
                setDeltaMovement(horizontal.scale(speed).add(0, lift, 0));
                entityData.set(HOP_STAGE, ASCENT);
                hops++;
            }
            case RISE -> {
                Vec3 destination = target.position().add(0, SLAM_HEIGHT, 0);
                Vec3 remaining = destination.subtract(position());
                if (ticks < RISE_DELAY) {
                    setDeltaMovement(Vec3.ZERO);
                } else if (remaining.lengthSqr() > 1 && ticks < RISE_TIMEOUT) {
                    setDeltaMovement(remaining.normalize().scale(Math.min(RISE_SPEED, remaining.length())));
                } else {
                    setDeltaMovement(Vec3.ZERO);
                    begin(Action.DROP);
                }
            }
            case DROP -> {
                int windup = flying ? WING_FOLD_TICKS : GROUND_DROP_DELAY;
                if (ticks <= windup) {
                    Vec3 motion = getDeltaMovement();
                    setDeltaMovement(motion.scale(DROP_WINDUP_DRAG));
                } else {
                    entityData.set(DATA_DIVING, true);
                    if (onGround()) {
                        setDeltaMovement(Vec3.ZERO);
                        level().broadcastEntityEvent(this, SLAM_IMPACT_EVENT);
                        impactCenter = position();
                        impactTicks = 0;
                        impactHits.clear();
                        tickImpact();
                        begin(Action.LANDING);
                    } else if (ticks > DROP_TIMEOUT) {
                        begin(Action.IDLE);
                    } else {
                        Vec3 motion = getDeltaMovement();
                        setDeltaMovement(motion.x * HORIZONTAL_DRAG, Math.max(-DROP_SPEED, motion.y - DROP_ACCELERATION), motion.z * HORIZONTAL_DRAG);
                    }
                }
            }
            case VOLLEY -> {
                if (flying) hover(target);
                else {
                    Vec3 motion = getDeltaMovement();
                    setDeltaMovement(motion.x * HORIZONTAL_DRAG, motion.y, motion.z * HORIZONTAL_DRAG);
                }
                if (ticks == VOLLEY_WINDUP) {
                    RoyalGel.volley(this, target);
                    if (!flying) groundHopsDue = true;
                    begin(Action.IDLE);
                }
            }
            case LANDING -> {
                if (ticks >= LANDING_TICKS) {
                    if (!flying) groundHopsDue = true;
                    begin(Action.IDLE);
                }
            }
            case RECOVER -> {
                if (ticks >= TELEPORT_RECOVERY) begin(Action.IDLE);
            }
        }
    }

    /// 泰拉弹幕 922 的九帧对应三个游戏刻。冲击以首次触地的位置为中心扩张，同一玩家只受击一次。
    private void tickImpact() {
        if (impactCenter == null || !(level() instanceof ServerLevel server)) return;
        double radius = IMPACT_START_RADIUS + IMPACT_GROWTH * ++impactTicks / IMPACT_TICKS;
        AABB area = new AABB(impactCenter, impactCenter).inflate(radius);
        for (Player player : server.getEntitiesOfClass(Player.class, area, this::canAttack)) {
            if (player.getBoundingBox().distanceToSqr(impactCenter) <= radius * radius && impactHits.add(player.getUUID()))
                player.hurt(damageSources().mobAttack(this), (float) CreatureDifficultyScaling.projectileDamage(this, IMPACT_DAMAGE, false));
        }
        for (int i = 0; i < IMPACT_RING_POINTS; i++) {
            double angle = i * Math.PI * 2 / IMPACT_RING_POINTS;
            server.sendParticles(ParticleTypes.POOF, impactCenter.x + radius * Math.cos(angle),
                    impactCenter.y + 0.15, impactCenter.z + radius * Math.sin(angle), 2, 0.15, 0.2, 0.15, 0.03);
        }
        if (impactTicks == IMPACT_TICKS) impactCenter = null;
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (level().isClientSide && event == LAND_EVENT) {
            landingAnimationTicks = LANDING_TICKS;
        } else if (level().isClientSide && event == SLAM_IMPACT_EVENT) {
            landingAnimationTicks = LANDING_TICKS;
            CameraShake.start(level(), position(), SLAM_SHAKE_RADIUS, SLAM_SHAKE_TICKS, SLAM_SHAKE_ANGLE);
        } else {
            super.handleEntityEvent(event);
        }
    }

    private void hover(LivingEntity target) {
        Vec3 destination = target.getBoundingBox().getCenter();
        if (hasLineOfSight(target)) {
            destination = destination.add(0, HOVER_HEIGHT, 0);
        } else {
            BlockPos base = BlockPos.containing(destination);
            for (int height = 0; height < HOVER_SCAN_HEIGHT; height++) {
                BlockPos pos = base.above(height);
                if (!level().getBlockState(pos).getCollisionShape(level(), pos).isEmpty()) {
                    destination = destination.add(0, Math.max(0, height - 1), 0);
                    break;
                }
            }
        }
        Vec3 offset = destination.subtract(getBoundingBox().getCenter());
        Vec3 motion = getDeltaMovement();
        Vec3 desired = offset.scale(FLIGHT_APPROACH);
        if (desired.lengthSqr() > FLIGHT_SPEED * FLIGHT_SPEED)
            desired = desired.normalize().scale(FLIGHT_SPEED);
        Vec3 flight = motion.lerp(desired, FLIGHT_STEERING);
        // 飞行追击中斜向下降才用俯冲姿态；重砸另走收翼、坠落、落地动画。
        entityData.set(DATA_DESCENDING_FLIGHT, flight.y < -0.15 && flight.horizontalDistanceSqr() > 0.09);
        setDeltaMovement(flight);
    }

    private void begin(Action next) {
        entityData.set(HOP_STAGE, (byte) 0);
        action = next;
        entityData.set(DATA_ACTION, (byte) next.ordinal());
        entityData.set(DATA_DIVING, false);
        ticks = 0;
    }

    private int landingAnimationTicks;

    @Override
    public void tick() {
        super.tick();
        if (landingAnimationTicks > 0) landingAnimationTicks--;
    }

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("flying1");
    private static final RawAnimation DIVE_FLIGHT = RawAnimation.begin().thenLoop("flying2");
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlayAndHold("jump");
    private static final RawAnimation FALL = RawAnimation.begin().thenLoop("fall");
    private static final RawAnimation REST = RawAnimation.begin().thenPlayAndHold("falldown");

    private static final RawAnimation CHARGE = RawAnimation.begin().thenPlayAndHold("charge");
    private static final RawAnimation FOLD_WINGS = RawAnimation.begin().thenPlayAndHold("falling");

    private RawAnimation bodyAnimation() {
        byte current = entityData.get(DATA_ACTION);
        byte hop = entityData.get(HOP_STAGE);
        if (hop == WINDUP || hop == ASCENT) return JUMP;
        if (current == Action.LANDING.ordinal() || landingAnimationTicks > 0) return REST;
        if (hop == DESCENT) return FALL;
        if (isFlyingPhase()) {
            if (current == Action.DROP.ordinal())
                return entityData.get(DATA_DIVING) ? FALL : FOLD_WINGS;
            return entityData.get(DATA_DESCENDING_FLIGHT) ? DIVE_FLIGHT : FLY;
        }
        if (current == Action.VOLLEY.ordinal() && onGround()) return CHARGE;
        if (current == Action.DROP.ordinal()) return FALL;
        if (current == Action.RISE.ordinal()) return onGround() ? IDLE_ANIMATION : JUMP;
        if (onGround()) return IDLE_ANIMATION;
        return getDeltaMovement().y > 0 ? JUMP : FALL;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 0, state -> {
            RawAnimation animation = bodyAnimation();
            // 起跳红帧需要从零点计时；其余动作在切换时短暂混合上一姿态。
            state.getController().transitionLength(animation == JUMP || animation == FOLD_WINGS ? 0 : 2);
            state.getController().setAnimationSpeed(animation == JUMP ? entityData.get(ASCENT_SPEED)
                    : animation == CHARGE ? 1.58333
                    : 1.0);
            return state.setAndContinue(animation);
        }));
    }
}
