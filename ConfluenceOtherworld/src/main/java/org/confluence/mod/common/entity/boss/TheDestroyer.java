package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ModClientConfigs;
import org.confluence.mod.client.effect.CameraShake;
import org.confluence.mod.client.particle.DestroyerDebrisParticle;
import org.confluence.mod.client.renderer.entity.DestroyerRenderer;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.ai.WormSteering;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyRules;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.entity.projectile.DestroyerLaserProjectile;
import org.confluence.mod.common.entity.projectile.bomb.BaseDynamiteEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.network.s2c.TerraStyleExplosionPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.BitSet;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.function.IntFunction;

/// 毁灭者的战斗主体：负责 AI、生命及目标，身体轨迹与碰撞部件交给 DestroyerBody。
public class TheDestroyer extends MechanicalBoss {
    private static final EntityDataAccessor<Integer> SIDE_BEAM_SEED = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BEAM_TICK = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> DEBRIS_STATE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<BlockPos> DEBRIS_ORIGIN = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> METAL = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SHELL_OPEN = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> EXPLODED_SECTIONS = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DEATH_SHAKE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<CompoundTag> BODY_POSE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<CompoundTag> PROBE_STATE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.COMPOUND_TAG);

    // 头部、80 节身体和尾部共 82 节。
    public static final int SEGMENT_COUNT = 82;
    // 身体中轴的总长度，单位格；按 Wiki 的 239 格长度配置。
    public static final double BODY_LENGTH = 239.0;
    // 相邻链节点间距，单位格；尾部落在总长度的终点。
    public static final double SEGMENT_SPACING = BODY_LENGTH / (SEGMENT_COUNT - 1);
    // 主动转向的最小半径，以体节间距为单位。
    private static final double TURN_RADIUS_SEGMENTS = 4.0;
    // 主动转向每 tick 的最大角度，单位度。
    private static final float MAX_TURN_DEGREES = 6.0F;
    private static final double MAX_TURN_RADIANS = Math.toRadians(MAX_TURN_DEGREES);
    // 冲刺前原地对准目标时，每刻允许的最大转角。
    private static final float DASH_AIM_TURN_DEGREES = 8.0F;
    // 新模型整体尺寸倍率；碰撞箱与静态模型使用同一倍率。
    public static final float MODEL_SCALE = 2.0F;
    // 模型主体截面的宽高，单位格；侧翼不扩大实体碰撞。
    public static final float BODY_WIDTH = 28.0F / 16.0F * MODEL_SCALE;
    public static final float BODY_HEIGHT = 25.0F / 16.0F * MODEL_SCALE;
    // 可受击碰撞箱相对身体尺寸的倍率，保留原有接触伤害范围。
    public static final float HITBOX_SCALE = 1.5F;
    private static final int BEAM_BASE_COOLDOWN = 100, BEAM_UNHURT_TICKS = 400;
    private static final double BEAM_ADVANCE_SPEED = 0.6;
    private static final Map<Player, Long> PLAYER_DAMAGE_COUNTS = new WeakHashMap<>();
    // 玩家低于当地地表多少格时进入地下模式。
    private static final double UNDERGROUND_DEPTH = 12.0;
    // 玩家高于当地地表多少格时进入天空模式；退出阈值较低，避免模式抖动。
    private static final double SKY_ENTER_HEIGHT = 24.0, SKY_EXIT_HEIGHT = 16.0;
    // 地形模式连续成立的确认时间，单位 tick。
    private static final int MODE_CONFIRM_TICKS = 30;
    // 地下游走深度、环绕半径，单位格。
    private static final double BURROW_DEPTH = 10.0, BURROW_RADIUS = 20.0;
    // 地面出土前的最短游走时间、出土点与玩家的水平距离，单位 tick/格。
    private static final int BURROW_TICKS = 60;
    private static final double BREACH_RANGE = 10.0;
    // 地下与天空巡航速度，单位格/tick。STEERING 限制每刻的速度大小变化。
    private static final double BURROW_SPEED = 1.2, SKY_SPEED = 1.45, STEERING = 0.065;
    // 出土水平、竖直初速度和重力，单位格/tick、格/tick²。
    private static final double BREACH_SPEED = 1.6, BREACH_RISE = 1.55, GRAVITY = 0.055;
    // 上冲超过此时间仍未出土则重新寻找出土位置，单位 tick。
    private static final int BREACH_TIMEOUT = 60;
    // 地下头部轴向滚动速度，单位度/tick。
    private static final float ROLL_SPEED = 14.0F;
    // 天空偶发滚动的周期与持续时间，单位 tick。
    private static final int SKY_ROLL_INTERVAL = 180, SKY_ROLL_TICKS = 36;
    // 每隔多少 tick 发起一轮激光，以及顺序发射的体节间隔。
    private static final int LASER_CYCLE = 180, LASER_STEP = 2;
    // 激光初速和最大射程，单位格/tick、格。
    private static final float LASER_SPEED = 2.2F;
    public static final double LASER_RANGE = 192.0;
    // 喷气音效短暂重叠，避免较短的音频播放结束后出现空档。
    private static final int JET_SOUND_INTERVAL = 90;
    // 定期检查玩家是否进入声源范围，避免每 tick 遍历全部体节。
    private static final int JET_LISTENER_CHECK_INTERVAL = 5;
    private static final int VENT_SOUND_INTERVAL = 12;
    // 排气声仅发送给附近的玩家，避免远处体节播放影响附近体节。
    private static final double EXHAUST_SOUND_RADIUS = 24.0;
    private static final float EXHAUST_SOUND_VOLUME = 1.5F;
    // 小激光音效的播放间隔，单位 tick，避免齐射时每节各播一次。
    private static final int LASER_SOUND_INTERVAL = 4;
    // 激光射击声音的音量与音调。
    private static final float LASER_SOUND_VOLUME = 0.7F, LASER_SOUND_PITCH = 1.0F;
    // 头部入土声音与近距离震屏的间隔，单位 tick。
    private static final int DIG_INTERVAL = 10;
    // 地下震屏为半秒/14 格，穿出或进入地面为一秒/28 格。
    private static final float SHAKE_ANGLE = 2.4F;
    public static final int DIG_SHAKE_TICKS = 10, CROSS_SHAKE_TICKS = 20;
    public static final double DIG_SHAKE_RADIUS = 14.0, CROSS_SHAKE_RADIUS = 28.0;
    public static final byte DIG_EVENT = 70, CROSS_EVENT = 71;
    private static final byte DEBRIS_EVENT = 73;
    private static final byte PHASE_SHELL_EVENT = 74;
    // 相邻体节爆甲之间的间隔，单位 tick。
    private static final int SHELL_BURST_INTERVAL = 1;
    // 出土方块飞散的最短触发间隔，单位 tick，防止贴着地表反复穿越时堆积。
    private static final int DEBRIS_INTERVAL = 10;
    // 地下移动时从头部中心向上检查的最大距离，单位格；只掀起此范围内表层的视觉方块。
    private static final int SURFACE_DEBRIS_DEPTH = 4;
    // 地下掀起方块所需的最小头部位移平方，单位格²/tick²。
    private static final double SURFACE_DEBRIS_MIN_MOVEMENT_SQUARED = 0.01;
    // 天空接触成功命中后的燃烧时间，单位秒。
    private static final int SKY_BURN_SECONDS = 5;
    // 地下环绕玩家时的角速度，单位弧度/tick。
    private static final double BURROW_ANGULAR_SPEED = 0.035;
    // 脱战时向地下撤离的速度，单位格/tick。
    private static final double RETREAT_SPEED = 0.6;
    // 特殊攻击之间的巡航时间，单位 tick；直线冲刺与盘绕交替执行。
    private static final int SPECIAL_COOLDOWN = 200;
    // 冲刺前减速瞄准及直线飞行时间，单位 tick。
    private static final int DASH_WINDUP = 24, DASH_DURATION = 28;
    // 直线冲刺速度，单位格/tick；起步后不再追踪目标。
    private static final double DASH_SPEED = 3.2;
    // 包围攻击的总倒计时，包含追赶入场与收圈，单位 tick；240 tick 为 12 秒。
    private static final int COIL_DURATION = 240;
    // 围圈初始和最终半径，单位格。
    private static final double COIL_START_RADIUS = 28.0, COIL_END_RADIUS = SEGMENT_SPACING * TURN_RADIUS_SEGMENTS;
    // 盘绕沿圆周前进的速度，单位格/tick。
    private static final double COIL_SPEED = 2.4;
    // 到达围圈入口的判定距离，单位格。
    private static final double COIL_ARRIVAL_RANGE = 2.5;

    // 有效受击时通知客户端生成机械火花的实体事件编号。
    private static final byte HURT_SPARK_EVENT = 72;
    // 同一部位两次火花之间的最短间隔，单位 tick。
    private static final int HURT_SPARK_INTERVAL = 3;

    // 死亡后开始颤抖前的自由下坠时间，单位为游戏刻。
    private static final int DEATH_FALL_TICKS = 30;
    // 随着已爆体节增加，逐步缩短间隔，单位为游戏刻。
    private static final int DEATH_BURST_START_INTERVAL = 6;
    // 用三分之一游戏刻计时，收尾最多每刻引爆三节。
    private static final int DEATH_BURST_STEPS_PER_TICK = 3;
    private static final int DEATH_BURST_END_INTERVAL = 1;
    // 最后一成体节按最快节奏爆开。
    private static final float DEATH_BURST_FULL_SPEED_PROGRESS = 0.9F;
    // 第一次爆炸前的颤抖时间，单位为游戏刻。
    private static final int DEATH_SHAKE_TICKS = 20;

    private enum SpecialAttack {
        // 未执行特殊攻击，按当前移动模式行动。
        NONE,
        // 冲刺前蓄力并锁定本次方向。
        DASH_WINDUP,
        // 沿锁定方向直线冲刺。
        DASH,
        // 接近玩家，为环绕收紧建立位置。
        COIL_APPROACH,
        // 倒计时内追踪玩家位置并环绕收紧。
        COIL
    }

    public enum Mode {
        // 地下游走和绕轴滚动，以冲撞为主。
        UNDERGROUND,
        // 在地面下接近玩家，出土后按抛物线运动。
        GROUND,
        // 空中追击并使用体节激光攻击。
        SKY;

        private static final IntFunction<Mode> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);

        private static Mode fromOrdinal(int ordinal) {
            return BY_ID.apply(ordinal);
        }
    }

    /// 部位基础值统一在 BossEntities 的状态参数中声明，共用头部的生命。
    public enum BodySection {
        /// 身体体节，使用身体部位参数。
        BODY,
        /// 尾部体节，使用尾部参数。
        TAIL
    }

    private final DestroyerBody body;
    private final ProbeRelease probes = new ProbeRelease();
    private DestroyerPart attackingPart;

    private Mode candidateMode = Mode.GROUND;
    private int candidateTicks, groundTicks, launchTicks, flightTicks, apexTicks;
    private boolean launching, airborne, wasSolid, terrainInitialized;
    private Vec3 velocity = Vec3.ZERO;
    private float previousRoll, displayedRoll, launchRoll;
    private long rollUpdateTick;

    private SpecialAttack specialAttack = SpecialAttack.NONE;
    private int specialTicks, specialCooldown = SPECIAL_COOLDOWN;
    private Vec3 dashDirection = Vec3.ZERO;
    private boolean nextCoil;
    private int coilTicksRemaining;
    private double coilAngle;

    private int laserTicks, laserCursor = -1;
    private boolean alternateVolley;
    private int beamCooldown = BEAM_BASE_COOLDOWN, beamCount, beamUnhurtTicks;
    private LivingEntity beamTimerTarget;
    private long beamTargetDamageCount;

    private int lastSparkTick = -HURT_SPARK_INTERVAL;
    private int lastDebrisTick = -DEBRIS_INTERVAL;
    private int lastLaserSoundTick = -LASER_SOUND_INTERVAL;
    private final Map<ServerPlayer, Integer> lastJetSoundTicks = new WeakHashMap<>();
    private final Map<ServerPlayer, Integer> lastVentSoundTicks = new WeakHashMap<>();

    private int clientShellCursor = -1;
    private boolean clientSimultaneousBurst;
    private int[] deathOrder;
    private int[] deathRank;

    public int getBeamTick() {return isAlive() && !isParalyzed() ? entityData.get(BEAM_TICK) : -1;}

    public int getSideBeamSeed() {return entityData.get(SIDE_BEAM_SEED);}

    /// 按体节同步已脱离的探测器，供客户端单独隐藏对应模型组。
    public boolean isProbeReleased(int index) {
        long[] bits = entityData.get(PROBE_STATE).getLongArray("Released");
        int word = index >>> 6;
        return word < bits.length && (bits[word] & (1L << (index & 63))) != 0;
    }


    public Vec3 beamOrigin() {return getBoundingBox().getCenter().add(getLookAngle().scale(10));}

    public TheDestroyer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        // 头部俯仰由三维运动方向决定，禁止默认视线控制器每 tick 将其归零。
        lookControl = new LookControl(this) {
            @Override
            protected boolean resetXRotOnTick() {
                return false;
            }
        };
        noPhysics = true;
        setNoGravity(true);
        body = new DestroyerBody(this);
        setId(ENTITY_COUNTER.getAndAdd(SEGMENT_COUNT) + 1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BEAM_TICK, -1);
        builder.define(SIDE_BEAM_SEED, 0);
        builder.define(MODE, Mode.GROUND.ordinal());
        builder.define(DEBRIS_STATE, Blocks.AIR.defaultBlockState());
        builder.define(DEBRIS_ORIGIN, BlockPos.ZERO);
        builder.define(METAL, false);
        builder.define(ROLL, 0.0F);
        builder.define(SHELL_OPEN, false);
        builder.define(EXPLODED_SECTIONS, 0);
        builder.define(DEATH_SHAKE, 0);
        builder.define(BODY_POSE, new CompoundTag());
        builder.define(PROBE_STATE, new CompoundTag());
    }

    public DestroyerBody getBody() {return body;}

    @Override
    public boolean isMultipartEntity() {return true;}

    @Override
    public DestroyerPart[] getParts() {return body.getParts();}

    @Override
    public void setId(int id) {
        super.setId(id);
        if (body != null) body.assignIds(id);
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (body != null) body.initializePosition();
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        if (body != null) {
            for (DestroyerPart part : body.getParts()) part.refreshDimensions();
        }
    }

    @Override
    protected AABB makeBoundingBox() {
        return super.makeBoundingBox().move(0, (getBbHeight() / HITBOX_SCALE - getBbHeight()) / 2.0, 0);
    }

    @Override
    public void onRemovedFromLevel() {
        if (isRemoved()) body.remove();
        super.onRemovedFromLevel();
    }

    public boolean isWithinTrackingRange(ServerPlayer player, double range) {
        return body.distanceToSqr(player.position(), true) <= range * range;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            DestroyerRenderer.tickBeamEffects(this);
            if (!isAlive()) clientShellCursor = -1;
            if (clientShellCursor >= 0 && isMetal() && tickCount % SHELL_BURST_INTERVAL == 0) {
                if (clientSimultaneousBurst && clientShellCursor == 0 && !isSilent()) {
                    level().playLocalSound(getX(), getY(), getZ(), ModSoundEvents.DESTROYER_SHELL_BURST.get(), getSoundSource(), 2.0F, 1.0F, false);
                }
                int end = clientSimultaneousBurst ? SEGMENT_COUNT : clientShellCursor + 1;
                while (clientShellCursor < end)
                    DestroyerRenderer.spawnPhaseShell(this, clientShellCursor++);
                if (clientShellCursor >= SEGMENT_COUNT) clientShellCursor = -1;
            }
        }
        if (!level().isClientSide && !isRemoved()) entityData.set(BODY_POSE, body.createSnapshot());
    }

    @Override
    public Vec3 getLightProbePosition(float partialTick) {
        return body.getRenderPosition(0, partialTick).add(0, BODY_HEIGHT / 2.0, 0);
    }

    /// 全链共用快照插值，避免原版再单独插值头部造成头身位置不一致。
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
        if (!body.hasClientSnapshot()) super.lerpTo(x, y, z, yaw, pitch, steps);
    }

    @Override
    public double getAttributeValue(Holder<Attribute> attribute) {
        double value = super.getAttributeValue(attribute);
        if (attackingPart != null && attribute == Attributes.ATTACK_DAMAGE) {
            double headBase = getAttributeBaseValue(attribute);
            double partBase = stateAttributeValue(sectionOf(attackingPart), attribute, headBase);
            return headBase > 0 ? value * partBase / headBase : partBase * CreatureDifficultyScaling.attackMultiplier(this);
        }
        if ((attribute == Attributes.ARMOR || attribute == Attributes.ARMOR_TOUGHNESS)
                && getDamageFeedbackTarget() instanceof DestroyerPart part) {
            double headBase = getAttributeBaseValue(attribute);
            if (attribute == Attributes.ARMOR)
                return Math.max(0, stateAttributeValue(sectionOf(part), attribute, headBase) + value - headBase);
            if (attribute == Attributes.ARMOR_TOUGHNESS) {
                var rule = CreatureDifficultyScaling.difficulty(this);
                double partBase = stateAttributeValue(sectionOf(part), attribute, headBase);
                return Math.clamp(rule.toughness(partBase) + value - rule.toughness(headBase), 0, CreatureDifficultyRules.MAX_TOUGHNESS);
            }
        }
        return value;
    }

    private static BodySection sectionOf(DestroyerPart part) {
        return part.isTail() ? BodySection.TAIL : BodySection.BODY;
    }

    private boolean hurtByContact(Entity section, Player player) {
        DestroyerPart previous = attackingPart;
        attackingPart = section instanceof DestroyerPart part ? part : null;
        try {
            return doContactHurtTarget(player);
        } finally {
            attackingPart = previous;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide && getDamageFeedbackTarget() == this
                && tickCount - lastSparkTick >= HURT_SPARK_INTERVAL) {
            lastSparkTick = tickCount;
            playSound(ModSoundEvents.MECHANICAL_BOSS_HURT.get(), 1.0F, 1.0F);
            level().broadcastEntityEvent(this, HURT_SPARK_EVENT);
        }
        return damaged;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        // 受击声由实际命中部位在服务端播放，避免客户端收到本体伤害包后再从头部播放。
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.METAL_DEATH.get();
    }

    public Mode getMode() {return Mode.fromOrdinal(entityData.get(MODE));}

    public boolean isMetal() {return entityData.get(METAL);}

    /// 爆甲从头向尾推进，尚未爆开的体节继续显示一阶段模型。
    public boolean isPhaseOneSection(int index) {
        return !isMetal() || clientShellCursor >= 0 && index >= clientShellCursor;
    }

    public boolean isShellOpen() {return entityData.get(SHELL_OPEN);}

    public float getRoll(float partialTick) {
        float progress = Mth.clamp(level().getGameTime() - rollUpdateTick + partialTick, 0, 1);
        return previousRoll + Mth.wrapDegrees(displayedRoll - previousRoll) * progress;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (BODY_POSE.equals(key) && level().isClientSide && body != null) {
            body.readSnapshot(entityData.get(BODY_POSE));
        } else if (ROLL.equals(key)) {
            previousRoll = displayedRoll;
            displayedRoll = entityData.get(ROLL);
            rollUpdateTick = level().getGameTime();
        }
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        tickDestroyer();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    /// 位移由服务端状态机处理，关闭原版陆行摩擦和重力对抛物线的二次作用。
    @Override
    public void travel(Vec3 input) {
        if (isParalyzed()) return;
        if (!level().isClientSide && isAlive() && isDisengageRetreating()) {
            moveAlongTrajectory(getDeltaMovement());
            body.update();
        }
    }

    @Override
    protected double combatAnchorDistanceSqr(LivingEntity target) {
        return body.distanceToSqr(target.position(), false);
    }

    @Override
    protected Vec3 getDisengageMovement() {return new Vec3(0, -RETREAT_SPEED, 0);}

    @Override
    protected boolean hasEntityContactAttack() {return false;}

    private void tickDestroyer() {
        if (isParalyzed()) return;
        if (!isAlive() || isDisengageRetreating()) return;
        LivingEntity target = getTarget();
        updateStage();
        probes.tick();
        body.initializeTrail();
        if (target == null || !target.isAlive() || target.isRemoved() || target.level() != level()) {
            finishSpecialAttack();
            if (getBeamTick() >= 0) beamCooldown = BEAM_BASE_COOLDOWN;
            entityData.set(BEAM_TICK, -1);
            beamTimerTarget = null;
            beamUnhurtTicks = 0;
            velocity = Vec3.ZERO;
            setDeltaMovement(Vec3.ZERO);
            body.update();
            return;
        }
        tickBeam(target);
        if (!isAlive()) return;
        if (getBeamTick() < 0 && specialAttack == SpecialAttack.NONE) updateMode(target);
        boolean solidBefore = isSolid(position());
        Vec3 previousVelocity = velocity;
        if (getBeamTick() == 100) finishSpecialAttack();
        boolean specialMovement = (getBeamTick() < 100 || getBeamTick() >= 460) && tickSpecialAttack(target);
        if (!specialMovement) switch (getMode()) {
            case UNDERGROUND -> {
                velocity = steer(velocity, target.getBoundingBox().getCenter().subtract(position()), BURROW_SPEED);
                entityData.set(ROLL, DestroyerBody.wrapRoll(entityData.get(ROLL) + ROLL_SPEED));
            }
            case SKY -> {
                velocity = steer(velocity, target.getBoundingBox().getCenter().subtract(position()), SKY_SPEED);
                float roll = entityData.get(ROLL);
                int beamTick = getBeamTick();
                if (beamTick < 100) {
                    entityData.set(ROLL, tickCount % SKY_ROLL_INTERVAL < SKY_ROLL_TICKS
                            ? DestroyerBody.wrapRoll(roll + ROLL_SPEED) : DestroyerBody.followRoll(0, roll));
                } else if (beamTick < 220) {
                    // 蓄力时收正滚转，发射后保持姿态不再旋转。
                    entityData.set(ROLL, DestroyerBody.followRoll(0, roll));
                }
            }
            case GROUND -> tickGround(target, solidBefore);
        }
        if (getBeamTick() >= 100 && getBeamTick() < 220) {
            float slowdown = Mth.clamp((getBeamTick() - 100) / 120.0F, 0, 1);
            velocity = velocity.scale(1.0F - slowdown);
        } else if (getBeamTick() >= 220 && getBeamTick() < 460) {
            Vec3 direction = target.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter());
            Vec3 beamStart = beamOrigin();
            Vec3 beamEnd = beamStart.add(getLookAngle().scale(LASER_RANGE));
            beamEnd = level().clip(new ClipContext(beamStart, beamEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation();
            AABB targetBox = target.getBoundingBox().inflate(1.0);
            boolean aimingAtTarget = targetBox.contains(beamStart) || targetBox.clip(beamStart, beamEnd).isPresent();
            velocity = aimingAtTarget ? Vec3.ZERO : direction.lengthSqr() < 1.0E-7 ? getLookAngle().scale(BEAM_ADVANCE_SPEED) : direction.normalize().scale(BEAM_ADVANCE_SPEED);
        }
        Vec3 previous = position();
        moveAlongTrajectory(previousVelocity, velocity);
        setDeltaMovement(velocity);
        int beamTick = getBeamTick();
        if (beamTick < 460) {
            boolean chargingBeam = beamTick >= 100 && beamTick < 220;
            boolean aimingDash = beamTick < 100 && specialAttack == SpecialAttack.DASH_WINDUP;
            Vec3 desiredFacing = chargingBeam || aimingDash ? target.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter()) : velocity;
            turnHeadTowards(desiredFacing, aimingDash ? DASH_AIM_TURN_DEGREES : MAX_TURN_DEGREES);
        }
        boolean solidNow = isSolid(position());
        if (!specialMovement && getMode() == Mode.GROUND) {
            if (launching && !solidNow) {
                launching = false;
                airborne = true;
                flightTicks = 0;
                apexTicks = Math.max(1, (int) Math.ceil(Math.max(0, velocity.y) / GRAVITY));
                launchRoll = entityData.get(ROLL);
            } else if (airborne && solidNow) {
                airborne = false;
                groundTicks = 0;
            }
        }
        entityData.set(SHELL_OPEN, getMode() == Mode.SKY || getMode() == Mode.GROUND && airborne || specialMovement);
        tickHeadTerrain(previous, solidNow);
        body.update();
        if (getMode() == Mode.SKY && velocity.lengthSqr() > 0.0064 && tickCount % JET_LISTENER_CHECK_INTERVAL == 0) {
            playJetExhaustSounds();
        }
        attackContact(previous);
        // 接触伤害可能触发反伤，结算后重新确认能否发射本刻的齐射。
        if (isAlive()) tickLasers(target);
    }

    private void updateStage() {
        float threshold = phaseTwoThreshold();
        if (!isMetal() && getHealth() <= getMaxHealth() * threshold) {
            level().broadcastEntityEvent(this, PHASE_SHELL_EVENT);
            entityData.set(METAL, true);
            broadcastPhaseTransition();
        }
    }

    private float phaseTwoThreshold() {
        return LibUtils.switchByDifficulty(level(), 0.5F, 0.5F, 0.5F, 2.0F / 3.0F);
    }

    /// 特殊攻击只接管自己的运动阶段，出土抛物线结束前不会插入新攻击。
    private boolean tickSpecialAttack(LivingEntity target) {
        Vec3 coilCenter = target.position();
        if (specialAttack == SpecialAttack.NONE) {
            if (getBeamTick() >= 0 || airborne || launching || --specialCooldown > 0) return false;
            specialTicks = 0;
            if (nextCoil && getMode() != Mode.UNDERGROUND) {
                coilTicksRemaining = COIL_DURATION;
                coilAngle = Math.atan2(getZ() - coilCenter.z, getX() - coilCenter.x);
                specialAttack = SpecialAttack.COIL_APPROACH;
            } else {
                specialAttack = SpecialAttack.DASH_WINDUP;
                playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.6F);
            }
            nextCoil = !nextCoil;
        }
        specialTicks++;
        switch (specialAttack) {
            case DASH_WINDUP -> {
                if (specialTicks >= DASH_WINDUP) {
                    dashDirection = getLookAngle();
                    velocity = dashDirection.scale(DASH_SPEED);
                    specialAttack = SpecialAttack.DASH;
                    specialTicks = 0;
                }
            }
            case DASH -> {
                velocity = dashDirection.scale(DASH_SPEED);
                if (specialTicks >= DASH_DURATION) finishSpecialAttack();
            }
            case COIL_APPROACH -> {
                Vec3 entry = coilCenter.add(Math.cos(coilAngle) * COIL_START_RADIUS, 0,
                        Math.sin(coilAngle) * COIL_START_RADIUS);
                Vec3 offset = entry.subtract(position());
                velocity = offset.normalize().scale(Math.min(COIL_SPEED, offset.length()));
                if (offset.lengthSqr() <= COIL_ARRIVAL_RANGE * COIL_ARRIVAL_RANGE) {
                    specialAttack = SpecialAttack.COIL;
                    specialTicks = 0;
                }
            }
            case COIL -> {
                // 圆心每 tick 读取玩家当前位置；倒计时持续消耗，追赶过程不会重置。
                double formationTicks = Math.PI * 2 * COIL_START_RADIUS / COIL_SPEED;
                double progress = Mth.clamp((COIL_DURATION - coilTicksRemaining - formationTicks) / (COIL_DURATION - formationTicks), 0, 1);
                double radius = Mth.lerp(progress, COIL_START_RADIUS, COIL_END_RADIUS);
                // 收圈时降低速度，使目标角速度不超过头部允许的转向速度。
                double coilSpeed = Math.min(COIL_SPEED, radius * MAX_TURN_RADIANS * 0.9);
                double step = coilSpeed / radius;
                coilAngle += step;
                Vec3 destination = coilCenter.add(Math.cos(coilAngle) * radius, 0, Math.sin(coilAngle) * radius);
                velocity = destination.subtract(position());
                if (velocity.lengthSqr() > coilSpeed * coilSpeed)
                    velocity = velocity.normalize().scale(coilSpeed);
            }
            default -> {}
        }
        if ((specialAttack == SpecialAttack.COIL_APPROACH || specialAttack == SpecialAttack.COIL)
                && --coilTicksRemaining <= 0) finishSpecialAttack();
        entityData.set(ROLL, DestroyerBody.followRoll(0, entityData.get(ROLL)));
        return true;
    }

    private void finishSpecialAttack() {
        specialAttack = SpecialAttack.NONE;
        specialCooldown = SPECIAL_COOLDOWN;
        specialTicks = 0;
        coilTicksRemaining = 0;
    }

    private void updateMode(LivingEntity target) {
        BlockPos pos = target.blockPosition();
        if (!level().hasChunkAt(pos)) return;
        double surface = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        double height = target.getY() - surface;
        Mode desired = height < -UNDERGROUND_DEPTH ? Mode.UNDERGROUND
                : height > (getMode() == Mode.SKY ? SKY_EXIT_HEIGHT : SKY_ENTER_HEIGHT) ? Mode.SKY : Mode.GROUND;
        if (desired != candidateMode) {
            candidateMode = desired;
            candidateTicks = 0;
        }
        if (++candidateTicks < MODE_CONFIRM_TICKS || desired == getMode() || airborne || launching)
            return;
        entityData.set(MODE, desired.ordinal());
        groundTicks = 0;
        laserCursor = -1;
        laserTicks = 0;
    }

    private void tickGround(LivingEntity target, boolean solid) {
        if (airborne) {
            velocity = velocity.add(0, -GRAVITY, 0);
            flightTicks++;
            float progress = Math.min(1.0F, flightTicks / (float) apexTicks);
            // 离地仍沿原方向滚动，速度逐渐下降；最高点后保持归正。
            float spin = ROLL_SPEED * apexTicks * (progress - progress * progress * 0.5F);
            float end = launchRoll + ROLL_SPEED * apexTicks * 0.5F;
            float correction = -Mth.wrapDegrees(end) * progress * progress;
            entityData.set(ROLL, progress >= 1 ? 0 : DestroyerBody.wrapRoll(launchRoll + spin + correction));
            return;
        }
        entityData.set(ROLL, DestroyerBody.wrapRoll(entityData.get(ROLL) + ROLL_SPEED));
        if (launching) {
            if (++launchTicks > BREACH_TIMEOUT) {
                launching = false;
                groundTicks = 0;
            }
            return;
        }
        groundTicks++;
        BlockPos playerPos = target.blockPosition();
        double ground = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, playerPos.getX(), playerPos.getZ());
        double angle = tickCount * BURROW_ANGULAR_SPEED;
        double radius = groundTicks < BURROW_TICKS ? BURROW_RADIUS : 0;
        Vec3 destination = new Vec3(target.getX() + Math.cos(angle) * radius, Math.max(level().getMinBuildHeight() + 1, ground - BURROW_DEPTH),
                target.getZ() + Math.sin(angle) * radius);
        velocity = steer(velocity, destination.subtract(position()), BURROW_SPEED);
        Vec3 horizontal = target.position().subtract(position()).multiply(1, 0, 1);
        if (solid && groundTicks >= BURROW_TICKS && horizontal.lengthSqr() <= BREACH_RANGE * BREACH_RANGE) {
            Vec3 direction = horizontal.lengthSqr() < 0.001 ? new Vec3(1, 0, 0) : horizontal.normalize();
            velocity = direction.scale(BREACH_SPEED).add(0, BREACH_RISE, 0);
            launching = true;
            launchTicks = 0;
        }
    }

    private void turnHeadTowards(Vec3 desired, float maxDegrees) {
        if (desired.lengthSqr() < 1.0E-7) return;
        Vec3 facing = WormSteering.turn(getLookAngle(), desired, Math.toRadians(maxDegrees));
        // 接近竖直时，微小的水平分量变化也会翻转方位角；限制模型自身的偏航与俯仰。
        faceCombatDirection(facing, maxDegrees, maxDegrees);
    }

    private void moveAlongTrajectory(Vec3 desired) {
        moveAlongTrajectory(velocity, desired);
    }

    // 按最小转弯半径和最大转角限制实际位移。
    private void moveAlongTrajectory(Vec3 previous, Vec3 desired) {
        Vec3 heading = previous.lengthSqr() > 1.0E-7 ? previous : getLookAngle();
        velocity = WormSteering.limit(heading, desired, SEGMENT_SPACING * TURN_RADIUS_SEGMENTS, MAX_TURN_RADIANS);
        setPos(position().add(velocity));
    }

    // 计算巡航期望速度，转向由位移阶段统一限制。
    static Vec3 steer(Vec3 current, Vec3 offset, double speed) {
        if (offset.lengthSqr() < 0.001) return current;
        double currentSpeed = current.length();
        double nextSpeed = currentSpeed + Mth.clamp(speed - currentSpeed, -STEERING, STEERING);
        return offset.normalize().scale(nextSpeed);
    }

    // 为每名玩家选择最近的张开体节，长身体不会只从头部发声。
    private void playJetExhaustSounds() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        double radiusSqr = EXHAUST_SOUND_RADIUS * EXHAUST_SOUND_RADIUS;
        for (ServerPlayer player : serverLevel.players()) {
            Integer lastTick = lastJetSoundTicks.get(player);
            if (lastTick != null && tickCount - lastTick < JET_SOUND_INTERVAL) continue;
            Vec3 closest = null;
            double closestDistance = radiusSqr;
            for (DestroyerPart part : body.getParts()) {
                if (!part.isOpen() || part.isRemoved()) continue;
                double distance = part.distanceToSqr(player);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closest = part.position();
                }
            }
            if (closest != null) {
                lastJetSoundTicks.put(player, tickCount);
                player.connection.send(new ClientboundSoundPacket(ModSoundEvents.DESTROYER_JET_EXHAUST, SoundSource.HOSTILE,
                        closest.x, closest.y, closest.z, EXHAUST_SOUND_VOLUME, 1.0F, random.nextLong()));
            }
        }
    }

    // 散热声按玩家限频，而不是让远处体节占用整条身体的播放机会。
    void playVentOpeningSound(Vec3 position) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        double radiusSqr = EXHAUST_SOUND_RADIUS * EXHAUST_SOUND_RADIUS;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(position) > radiusSqr) continue;
            Integer previousTick = lastVentSoundTicks.get(player);
            if (previousTick != null && tickCount - previousTick < VENT_SOUND_INTERVAL) continue;
            lastVentSoundTicks.put(player, tickCount);
            player.connection.send(new ClientboundSoundPacket(ModSoundEvents.DESTROYER_VENT_EXHAUST, SoundSource.HOSTILE,
                    position.x, position.y, position.z, EXHAUST_SOUND_VOLUME, 1.0F, random.nextLong()));
        }
    }

    private boolean hasBeamUsesRemaining() {
        int limit = LibUtils.switchByDifficulty(level(), 1, 2, 3, -1);
        return limit < 0 || beamCount < limit;
    }

    private boolean canStartBeam(LivingEntity target) {
        double distance = getBoundingBox().getCenter().distanceToSqr(target.getBoundingBox().getCenter());
        return isAlive() && target.isAlive() && target.level() == level() && isMetal() && hasBeamUsesRemaining()
                && specialAttack == SpecialAttack.NONE && getMode() == Mode.SKY && isBeamAboveGround() && getBeamTick() < 0 && beamCooldown <= 0 && distance >= 12 * 12 && distance <= 20 * 20;
    }

    /// 激光必须从地表上方的空气中发射，不加载未就绪的区块。
    private boolean isBeamAboveGround() {
        BlockPos pos = blockPosition();
        return level().hasChunkAt(pos) && !isSolid(position()) && getY() >= level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
    }

    private void startBeam() {
        entityData.set(SIDE_BEAM_SEED, random.nextInt());
        entityData.set(BEAM_TICK, 0);

    }

    /// 由实际伤害事件记录，不依赖伤害来源或受击动画。
    public static void recordPlayerDamage(Player player) {
        PLAYER_DAMAGE_COUNTS.merge(player, 1L, Long::sum);
    }

    private void tickBeam(LivingEntity target) {
        int tick = getBeamTick();
        if (tick < 0) {
            if (beamTimerTarget != target) {
                beamTimerTarget = target;
                beamUnhurtTicks = 0;
                beamTargetDamageCount = target instanceof Player player ? PLAYER_DAMAGE_COUNTS.getOrDefault(player, 0L) : 0;
            }
            long damageCount = target instanceof Player player ? PLAYER_DAMAGE_COUNTS.getOrDefault(player, 0L) : 0;
            if (damageCount != beamTargetDamageCount) {
                beamTargetDamageCount = damageCount;
                beamUnhurtTicks = 0;
            }
            if (beamCooldown > 0) {
                beamCooldown--;
                beamUnhurtTicks = 0;
                return;
            }
            float threshold = LibUtils.switchByDifficulty(level(), 0.25F, 1.0F / 3.0F, 0.5F, 2.0F / 3.0F);
            if (target instanceof Player && getHealth() < getMaxHealth() * threshold) {
                beamUnhurtTicks = Math.min(BEAM_UNHURT_TICKS, beamUnhurtTicks + 1);
            } else beamUnhurtTicks = 0;
            if (canStartBeam(target) && (beamUnhurtTicks >= BEAM_UNHURT_TICKS || random.nextInt(200) == 0)) {
                beamUnhurtTicks = 0;
                startBeam();
            }
            return;
        }
        if (tick >= 460 || tick >= 219 && tick < 420 && (getMode() != Mode.SKY || !isBeamAboveGround())) {
            entityData.set(BEAM_TICK, -1);
            beamCooldown = BEAM_BASE_COOLDOWN;
            beamUnhurtTicks = 0;
            return;
        }
        entityData.set(BEAM_TICK, tick + 1);
        if (tick == 219 && beamCount < Integer.MAX_VALUE) beamCount++;
        if (tick == 99) {
            level().playSound(null, getX(), getY(), getZ(), ModSoundEvents.DESTROYER_TERRIFYING_LASER.get(), SoundSource.HOSTILE, 4.0F, 1.0F);
        }
        if (tick >= 100 && tick < 420) {
            for (int ordinal = 0; ordinal < DestroyerSideBeams.COUNT; ordinal++) {
                int sideStartTick = DestroyerSideBeams.startTick(this, ordinal);
                if (tick < sideStartTick || tick >= sideStartTick + 200 || (tick - sideStartTick) % 10 != 0)
                    continue;
                Vec3 sideStart = DestroyerSideBeams.origin(this, ordinal, 1.0F);
                if (isSolid(sideStart)) continue;
                Vec3 sideEnd = sideStart.add(DestroyerSideBeams.direction(this, ordinal, 1.0F).scale(LASER_RANGE));
                sideEnd = level().clip(new ClipContext(sideStart, sideEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation();
                for (Player player : level().getEntitiesOfClass(Player.class, new AABB(sideStart, sideEnd).inflate(0.75))) {
                    if (canAttack(player) && (player.getBoundingBox().inflate(0.5).contains(sideStart)
                            || player.getBoundingBox().inflate(0.5).clip(sideStart, sideEnd).isPresent()))
                        player.hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
                }
            }
        }
        if (tick < 220 || tick >= 420) return;
        Vec3 start = beamOrigin(), end = start.add(getLookAngle().scale(LASER_RANGE));
        BlockHitResult hit = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        end = hit.getLocation();
        if (tick % 10 == 0) {
            for (Player player : level().getEntitiesOfClass(Player.class, new AABB(start, end).inflate(1.5))) {
                if (canAttack(player) && (player.getBoundingBox().inflate(1.0).contains(start)
                        || player.getBoundingBox().inflate(1.0).clip(start, end).isPresent()))
                    player.hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hit.getBlockPos();
                if (level().hasChunkAt(pos)) {
                    BlockState state = level().getBlockState(pos);
                    ((ServerLevel) level()).sendParticles(ParticleTypes.LARGE_SMOKE, end.x, end.y, end.z, 12, 1, 0.4, 1, 0.05);
                    emitDebris(pos, state);
                }
            }
        }
    }

    private void tickLasers(LivingEntity target) {
        if (getMode() == Mode.UNDERGROUND) {
            laserCursor = -1;
            laserTicks = 0;
            return;
        }
        if (laserCursor >= 0) {
            if (tickCount % LASER_STEP == 0) {
                fireSegment(laserCursor++, target);
                if (laserCursor >= SEGMENT_COUNT - 1) laserCursor = -1;
            }
        } else if (++laserTicks >= LASER_CYCLE) {
            laserTicks = 0;
            alternateVolley = !alternateVolley;
            if (getMode() == Mode.SKY && alternateVolley) {
                for (int i = 1; i < SEGMENT_COUNT - 1; i++) fireSegment(i, target);
            } else laserCursor = 1;
        }
    }

    private void fireSegment(int index, LivingEntity target) {
        Vec3 pos = body.getPosition(index);
        if (isSolid(pos) || pos.distanceToSqr(target.position()) > LASER_RANGE * LASER_RANGE)
            return;
        DestroyerLaserProjectile.fire(this, pos, target.getBoundingBox().getCenter(), LASER_SPEED);
        if (tickCount - lastLaserSoundTick >= LASER_SOUND_INTERVAL) {
            lastLaserSoundTick = tickCount;
            level().playSound(null, pos.x, pos.y, pos.z, ModSoundEvents.MECHANICAL_BOSS_SMALL_LASER.get(), SoundSource.HOSTILE, LASER_SOUND_VOLUME, LASER_SOUND_PITCH);
        }
    }

    public boolean isSolid(Vec3 point) {
        BlockPos pos = BlockPos.containing(point);
        if (pos.getY() < level().getMinBuildHeight() || !level().hasChunkAt(pos)) return true;
        return !level().getBlockState(pos).getCollisionShape(level(), pos).isEmpty();
    }

    private void tickHeadTerrain(Vec3 previous, boolean solid) {
        if (!terrainInitialized) {
            wasSolid = solid;
            terrainInitialized = true;
        }
        boolean crossing = solid != wasSolid;
        if (crossing || solid && tickCount % DIG_INTERVAL == 0) {
            BlockPos materialPos = BlockPos.containing(solid ? position() : previous);
            if (level().hasChunkAt(materialPos)) {
                BlockState state = level().getBlockState(materialPos);
                level().playSound(null, blockPosition(), state.getSoundType().getBreakSound(), SoundSource.HOSTILE, 0.25F, 0.8F);
                if (crossing && !solid && !state.isAir()) emitDebris(materialPos, state);
                if (crossing)
                    ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), getX(), getY() + BODY_HEIGHT / 2, getZ(), 32, 2, 1.5, 2, 0.15);
            }
            level().broadcastEntityEvent(this, crossing ? CROSS_EVENT : DIG_EVENT);
        }
        if (solid && previous.distanceToSqr(position()) >= SURFACE_DEBRIS_MIN_MOVEMENT_SQUARED
                && tickCount - lastDebrisTick >= DEBRIS_INTERVAL) {
            BlockPos surface = blockPosition();
            for (int depth = 0; depth <= Mth.ceil(BODY_HEIGHT / 2.0) + SURFACE_DEBRIS_DEPTH; depth++, surface = surface.above()) {
                if (!level().hasChunkAt(surface) || !level().hasChunkAt(surface.above())) break;
                BlockState state = level().getBlockState(surface);
                if (!state.getCollisionShape(level(), surface).isEmpty() && level().getBlockState(surface.above()).isAir()) {
                    emitDebris(surface, state);
                    break;
                }
            }
        }
        wasSolid = solid;
    }

    /// 仅发送表层方块的视觉副本，不破坏方块，也不请求加载区块。
    private void emitDebris(BlockPos position, BlockState state) {
        if (tickCount - lastDebrisTick < DEBRIS_INTERVAL) return;
        lastDebrisTick = tickCount;
        entityData.set(DEBRIS_STATE, state);
        entityData.set(DEBRIS_ORIGIN, position);
        // 先同步来源状态和位置，再发事件，避免客户端沿用上一次的材质。
        ((ServerLevel) level()).getChunkSource().broadcastAndSend(this,
                new ClientboundSetEntityDataPacket(getId(), entityData.getNonDefaultValues()));
        level().broadcastEntityEvent(this, DEBRIS_EVENT);
    }

    /// 全链只遍历现场玩家；连续碰撞与原版受伤无敌帧共同避免高速穿过漏判及体节叠伤。
    private void attackContact(Vec3 previousHead) {
        for (Player player : level().players()) {
            if (!canAttack(player)) continue;
            for (int i = 0; i < SEGMENT_COUNT; i++) {
                Entity section = body.getSection(i);
                if (section == null || section.isRemoved()) continue;
                Vec3 before = i == 0 ? previousHead : ((DestroyerPart) section).getContactSweepStart();
                double halfWidth = section.getBbWidth() / HITBOX_SCALE / 2.0;
                double halfHeight = section.getBbHeight() / HITBOX_SCALE / 2.0;
                AABB target = player.getBoundingBox().inflate(halfWidth, halfHeight, halfWidth);
                Vec3 center = section.position().add(0, halfHeight, 0);
                Vec3 start = before.add(0, halfHeight, 0);
                if (target.contains(center) || start.distanceToSqr(center) <= 256 && target.clip(start, center).isPresent()) {
                    if (hurtByContact(section, player) && getMode() == Mode.SKY)
                        player.igniteForSeconds(SKY_BURN_SECONDS);
                    break;
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (level().isClientSide && event == PHASE_SHELL_EVENT) {
            clientShellCursor = 0;
            clientSimultaneousBurst = ModClientConfigs.destroyerShellBurstMode == ModClientConfigs.ShellBurstMode.SIMULTANEOUS;
        } else if (level().isClientSide && event < 0 && -event < SEGMENT_COUNT) {
            body.getParts()[-event - 1].emitHurtSparks();
        } else if (level().isClientSide && event == DEBRIS_EVENT) {
            DestroyerDebrisParticle.spawn(entityData.get(DEBRIS_ORIGIN), entityData.get(DEBRIS_STATE));
        } else if (level().isClientSide && event == HURT_SPARK_EVENT) {
            MolangParticleEngine.INSTANCE.addEmitter(new ParticleEmitter(level(), getBoundingBox().getCenter(),
                    Confluence.asResource("mechanical_boss_hurt_spark"),
                    new MolangExp("variable.radius", Math.max(getBbWidth(), getBbHeight()) / 2.0F)));
        } else if (level().isClientSide && (event == DIG_EVENT || event == CROSS_EVENT)) {
            CameraShake.start(level(), getBoundingBox().getCenter(),
                    event == DIG_EVENT ? DIG_SHAKE_RADIUS : CROSS_SHAKE_RADIUS,
                    event == DIG_EVENT ? DIG_SHAKE_TICKS : CROSS_SHAKE_TICKS, SHAKE_ANGLE);
        } else super.handleEntityEvent(event);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DestroyerBeamCount", beamCount);
        tag.putInt("DestroyerBeamCooldown", beamCooldown);
        tag.putBoolean("DestroyerMetal", isMetal());
        tag.putInt("DestroyerMode", getMode().ordinal());
        probes.save(tag);
        tag.putBoolean("DestroyerAirborne", airborne);
        tag.putBoolean("DestroyerLaunching", launching);
        tag.putInt("DestroyerGroundTicks", groundTicks);
        tag.putInt("DestroyerFlightTicks", flightTicks);
        tag.putInt("DestroyerApexTicks", apexTicks);
        tag.putFloat("DestroyerLaunchRoll", launchRoll);
        tag.putFloat("DestroyerRoll", entityData.get(ROLL));
        tag.putDouble("DestroyerVX", velocity.x);
        tag.putDouble("DestroyerVY", velocity.y);
        tag.putDouble("DestroyerVZ", velocity.z);
        tag.putInt("ExplodedSections", entityData.get(EXPLODED_SECTIONS));
        body.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        beamCount = tag.getInt("DestroyerBeamCount");
        beamCooldown = tag.contains("DestroyerBeamCooldown") ? tag.getInt("DestroyerBeamCooldown") : BEAM_BASE_COOLDOWN;
        entityData.set(METAL, tag.getBoolean("DestroyerMetal"));
        entityData.set(MODE, Mode.fromOrdinal(tag.getInt("DestroyerMode")).ordinal());
        entityData.set(ROLL, tag.getFloat("DestroyerRoll"));
        probes.load(tag);
        airborne = tag.getBoolean("DestroyerAirborne");
        launching = tag.getBoolean("DestroyerLaunching");
        groundTicks = tag.getInt("DestroyerGroundTicks");
        flightTicks = tag.getInt("DestroyerFlightTicks");
        apexTicks = Math.max(1, tag.getInt("DestroyerApexTicks"));
        launchRoll = tag.getFloat("DestroyerLaunchRoll");
        velocity = new Vec3(tag.getDouble("DestroyerVX"), tag.getDouble("DestroyerVY"), tag.getDouble("DestroyerVZ"));
        entityData.set(EXPLODED_SECTIONS, Mth.clamp(tag.getInt("ExplodedSections"), 0, SEGMENT_COUNT));
        body.load(tag);
        entityData.set(BODY_POSE, body.createSnapshot());
    }

    private void prepareDeathOrder() {
        if (deathOrder != null) return;
        deathOrder = new int[SEGMENT_COUNT];
        deathRank = new int[SEGMENT_COUNT];
        // 同一实体在两端还原同一排列，只需同步已经爆开的节数。
        Random shuffle = new Random(getUUID().getMostSignificantBits() ^ getUUID().getLeastSignificantBits());
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            int slot = shuffle.nextInt(i + 1);
            deathOrder[i] = deathOrder[slot];
            deathOrder[slot] = i;
        }
        for (int i = 0; i < SEGMENT_COUNT; i++) deathRank[deathOrder[i]] = i;
    }

    public boolean isSectionExploded(int index) {
        int exploded = entityData.get(EXPLODED_SECTIONS);
        if (exploded == 0) return false;
        prepareDeathOrder();
        return deathRank[index] < exploded;
    }

    @Override
    protected void tickDeath() {
        ++deathTime;
        if (!(level() instanceof ServerLevel server)) return;
        body.tickDeathChain(SEGMENT_COUNT);
        if (deathTime <= DEATH_FALL_TICKS) return;
        int shakeTick = deathTime - DEATH_FALL_TICKS;
        entityData.set(DEATH_SHAKE, Math.min(shakeTick, DEATH_SHAKE_TICKS));
        if (shakeTick <= DEATH_SHAKE_TICKS) return;
        int exploded = entityData.get(EXPLODED_SECTIONS);
        if (exploded >= SEGMENT_COUNT) {
            remove(RemovalReason.KILLED);
            return;
        }

        int elapsed = (shakeTick - DEATH_SHAKE_TICKS - 1) * DEATH_BURST_STEPS_PER_TICK;
        int scheduled = 0;
        for (int i = 0; i < exploded; i++) scheduled += deathBurstInterval(i);
        if (elapsed < scheduled) return;

        prepareDeathOrder();
        // 同一游戏刻内依次处理已到时的体节，限制读档后的追赶速度。
        int index = 0;
        for (int count = 0; count < DEATH_BURST_STEPS_PER_TICK && exploded < SEGMENT_COUNT && elapsed >= scheduled; count++) {
            index = deathOrder[exploded];
            server.broadcastEntityEvent(this, index == 0 ? HURT_SPARK_EVENT : (byte) -index);
            scheduled += deathBurstInterval(exploded);
            exploded++;
        }
        entityData.set(EXPLODED_SECTIONS, exploded);
        if (exploded == SEGMENT_COUNT) {
            Vec3 center = body.getSection(index).getBoundingBox().getCenter();
            TerraStyleExplosionPacketS2C.send2All(server, center.x, center.y, center.z, BaseDynamiteEntity.BLAST_POWER);
            remove(RemovalReason.KILLED);
        }
    }

    @Override
    public Vec3 getRewardPosition(ServerPlayer player) {
        Entity hit = getDamageFeedbackTarget();
        if (hit != this) return hit.getBoundingBox().getCenter();

        // 持续伤害等来源没有命中体节，按参战者所在的一段虫身结算。
        Entity nearest = this;
        for (int i = 1; i < SEGMENT_COUNT; i++) {
            Entity section = body.getSection(i);
            if (section.distanceToSqr(player) < nearest.distanceToSqr(player)) nearest = section;
        }
        return nearest.getBoundingBox().getCenter();
    }

    private static int deathBurstInterval(int exploded) {
        float progress = Math.min(1.0F, exploded / ((SEGMENT_COUNT - 2) * DEATH_BURST_FULL_SPEED_PROGRESS));
        return Math.round(Mth.lerp(progress, DEATH_BURST_START_INTERVAL * DEATH_BURST_STEPS_PER_TICK, DEATH_BURST_END_INTERVAL));
    }

    public float getDeathShakeStrength(int sectionIndex) {
        int shakeTick = entityData.get(DEATH_SHAKE);
        int exploded = entityData.get(EXPLODED_SECTIONS);
        if (shakeTick == 0 || exploded >= SEGMENT_COUNT) return 0.0F;
        prepareDeathOrder();
        return Math.abs(deathOrder[exploded] - sectionIndex) <= 1
                ? shakeTick / (float) DEATH_SHAKE_TICKS : 0.0F;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide && !isAlive()) {
            entityData.set(BEAM_TICK, -1);
            entityData.set(SHELL_OPEN, false);
            finishSpecialAttack();
            laserCursor = -1;
            beamTimerTarget = null;
        }
        if (level() instanceof ServerLevel serverLevel && !isAlive() && shouldShowMessage()) {
            if (serverLevel.getDifficulty() != Difficulty.PEACEFUL && tryBeginDeathRewardSettlement()) {
                KillBoard.INSTANCE.defeat(getType());
                for (var player : getOnlineCombatParticipants()) {
                    TreasureBagItem.createItemEntity(this, player);
                    AchievementUtils.mechanicalBossDefeated(player, this);
                }
            }
        }
    }

    // 下一次释放优先从最近受击的体节向两侧查找。
    void recordProbeHit(DestroyerPart part) {
        probes.lastHitProbeIndex = part.getIndex();
    }

    // 已释放的体节写入存档，客户端据此隐藏体节上的探测器模型。
    private final class ProbeRelease {
        private final BitSet releasedProbes = new BitSet(SEGMENT_COUNT);
        private int nextProbeTick;
        private int lastHitProbeIndex = 1;

        private void tick() {
            // 血量决定累计可释放数量，进二阶段后剩余体节继续按间隔补发。
            int count = SEGMENT_COUNT - 2;
            float progress = Mth.clamp((1.0F - getHealth() / getMaxHealth()) / (1.0F - phaseTwoThreshold()), 0.0F, 1.0F);
            int desired = Math.min(count, (int) Math.floor(progress * count + 1.0E-4F));
            if (isMetal()) desired = count;
            if (desired <= releasedProbes.cardinality() || tickCount < nextProbeTick) return;
            int active = 0;
            for (Entity dependent : getSubEntities()) {
                if (dependent instanceof TheDestroyerProbe probe && probe.isAlive()) active++;
            }
            // 存量越高，补充越慢。每次只生成一只，避免集中补发。
            nextProbeTick = tickCount + 2 + active;
            DestroyerPart[] parts = body.getParts();
            for (int distance = 0; distance <= count; distance++) {
                int left = lastHitProbeIndex - distance, right = lastHitProbeIndex + distance;
                if ((left >= 1 && left <= count && releaseProbe(parts[left - 1]))
                        || (distance > 0 && right >= 1 && right <= count && releaseProbe(parts[right - 1]))) {
                    syncReleasedProbes();
                    return;
                }
            }
        }

        private boolean releaseProbe(DestroyerPart part) {
            int index = part.getIndex();
            if (!isAlive() || index <= 0 || part.isTail() || releasedProbes.get(index))
                return false;
            TheDestroyerProbe probe = new TheDestroyerProbe(BossEntities.THE_DESTROYER_PROBE.get(), level());
            probe.moveTo(part.getX(), part.getY(), part.getZ(), probe.getYRot(), probe.getXRot());
            if (!level().addFreshEntity(probe)) return false;
            probe.attach(TheDestroyer.this, part.position());
            releasedProbes.set(index);
            return true;
        }

        private void syncReleasedProbes() {
            CompoundTag tag = new CompoundTag();
            tag.putLongArray("Released", releasedProbes.toLongArray());
            entityData.set(PROBE_STATE, tag);
        }

        private void save(CompoundTag tag) {
            tag.putLongArray("DestroyerProbes", releasedProbes.toLongArray());
        }

        private void load(CompoundTag tag) {
            releasedProbes.clear();
            releasedProbes.or(BitSet.valueOf(tag.getLongArray("DestroyerProbes")));
            syncReleasedProbes();
        }
    }

}
