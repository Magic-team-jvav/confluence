package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.DestroyerCameraShake;
import org.confluence.mod.client.particle.DestroyerDebrisParticle;
import org.confluence.mod.client.renderer.entity.DestroyerRenderer;
import org.confluence.mod.common.entity.ai.WormSteering;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyRules;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.entity.projectile.DestroyerLaserProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/// 毁灭者的战斗主体：负责 AI、生命及目标，身体轨迹与碰撞部件交给 DestroyerBody。
public class TheDestroyer extends BaseBoss {
    private static final EntityDataAccessor<Integer> BEAM_TICK = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> DEBRIS_STATE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<BlockPos> DEBRIS_ORIGIN = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> METAL = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SHELL_OPEN = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<CompoundTag> BODY_POSE = SynchedEntityData.defineId(TheDestroyer.class, EntityDataSerializers.COMPOUND_TAG);
    /// Wiki 标准长度：头部、80 节身体和尾部，共 82 节。
    public static final int SEGMENT_COUNT = 82;
    /// 身体中轴的总长度，单位格；按 Wiki 的 239 格长度配置。
    public static final double BODY_LENGTH = 239.0;
    /// 相邻链节点间距，单位格；尾部落在总长度的终点。
    public static final double SEGMENT_SPACING = BODY_LENGTH / (SEGMENT_COUNT - 1);
    /// 主动转向的最小半径，以体节间距为单位。
    private static final double TURN_RADIUS_SEGMENTS = 4.0;
    /// 主动转向每 tick 的最大角度，单位弧度；出土抛物线仍由重力决定。
    private static final double MAX_TURN_RADIANS = Math.toRadians(6.0);
    /// 新模型整体尺寸倍率；碰撞箱与静态模型使用同一倍率。
    public static final float MODEL_SCALE = 2.0F;
    /// 模型主体截面的宽高，单位格；侧翼不扩大实体碰撞。
    public static final float BODY_WIDTH = 28.0F / 16.0F * MODEL_SCALE;
    public static final float BODY_HEIGHT = 25.0F / 16.0F * MODEL_SCALE;
    /// 可受击碰撞箱相对身体尺寸的倍率，保留原有接触伤害范围。
    public static final float HITBOX_SCALE = 1.5F;
    /// 简单、普通、困难的剩余生命比例阈值；进入二阶段后不回退。
    private static final float EASY_THRESHOLD = 0.4F, NORMAL_THRESHOLD = 0.5F, HARD_THRESHOLD = 0.6F;
    /// 玩家低于当地地表多少格时进入地下模式。
    private static final double UNDERGROUND_DEPTH = 12.0;
    /// 玩家高于当地地表多少格时进入天空模式；退出阈值较低，避免模式抖动。
    private static final double SKY_ENTER_HEIGHT = 24.0, SKY_EXIT_HEIGHT = 16.0;
    /// 地形模式连续成立的确认时间，单位 tick。
    private static final int MODE_CONFIRM_TICKS = 30;
    /// 地下游走深度、环绕半径，单位格。
    private static final double BURROW_DEPTH = 10.0, BURROW_RADIUS = 20.0;
    /// 地面出土前的最短游走时间、出土点与玩家的水平距离，单位 tick/格。
    private static final int BURROW_TICKS = 60;
    private static final double BREACH_RANGE = 10.0;
    /// 地下及天空巡航速度，单位格/tick；转向通过加速度限制。
    private static final double BURROW_SPEED = 1.2, SKY_SPEED = 1.45, STEERING = 0.065;
    /// 出土水平、竖直初速度和重力，单位格/tick、格/tick²。
    private static final double BREACH_SPEED = 1.6, BREACH_RISE = 1.55, GRAVITY = 0.055;
    /// 上冲超过此时间仍未出土则重新寻找出土位置，单位 tick。
    private static final int BREACH_TIMEOUT = 60;
    /// 地下头部轴向滚动速度，单位度/tick。
    private static final float ROLL_SPEED = 14.0F;
    /// 天空偶发滚动的周期与持续时间，单位 tick。
    private static final int SKY_ROLL_INTERVAL = 180, SKY_ROLL_TICKS = 36;
    /// 每隔多少 tick 发起一轮激光，以及顺序发射的体节间隔。
    private static final int LASER_CYCLE = 180, LASER_STEP = 2;
    /// 激光初速和最大射程，单位格/tick、格。
    private static final float LASER_SPEED = 2.2F;
    public static final double LASER_RANGE = 96.0;
    /// 体节激光声音的最短间隔，单位 tick；齐射时共用一次声音。
    private static final int LASER_SOUND_INTERVAL = 4;
    /// 激光射击声音的音量与音调。
    private static final float LASER_SOUND_VOLUME = 0.7F, LASER_SOUND_PITCH = 1.0F;
    /// 探针检查间隔、同时存活上限；每个身体节最多放出一枚。
    private static final int PROBE_INTERVAL = 20, MAX_ACTIVE_PROBES = 16;
    /// 缺血比例映射的每次释放概率：满血 4%，濒死 40%。
    private static final float PROBE_BASE_CHANCE = 0.04F, PROBE_HEALTH_CHANCE = 0.36F;
    /// 头部入土声音与近距离震屏的间隔，单位 tick。
    private static final int DIG_INTERVAL = 10;
    /// 地下震屏为半秒/7 格，穿出或进入地面为一秒/14 格。
    public static final int DIG_SHAKE_TICKS = 10, CROSS_SHAKE_TICKS = 20;
    public static final double DIG_SHAKE_RADIUS = 7.0, CROSS_SHAKE_RADIUS = 14.0;
    public static final byte DIG_EVENT = 70, CROSS_EVENT = 71;
    private static final byte DEBRIS_EVENT = 73;
    /// 出土方块飞散的最短触发间隔，单位 tick，防止贴着地表反复穿越时堆积。
    private static final int DEBRIS_INTERVAL = 10;
    /// 地下移动时从头部中心向上检查的最大距离，单位格；只掀起此范围内表层的视觉方块。
    private static final int SURFACE_DEBRIS_DEPTH = 4;
    /// 地下掀起方块所需的最小头部位移平方，单位格²/tick²。
    private static final double SURFACE_DEBRIS_MIN_MOVEMENT_SQUARED = 0.01;
    /// 天空接触成功命中后的燃烧时间，单位秒。
    private static final int SKY_BURN_SECONDS = 5;
    /// 地下环绕玩家时的角速度，单位弧度/tick。
    private static final double BURROW_ANGULAR_SPEED = 0.035;
    /// 脱战时向地下撤离的速度，单位格/tick。
    private static final double RETREAT_SPEED = 0.6;
    /// 特殊攻击之间的巡航时间，单位 tick；直线冲刺与盘绕交替执行。
    private static final int SPECIAL_COOLDOWN = 200;
    /// 冲刺前减速瞄准及直线飞行时间，单位 tick。
    private static final int DASH_WINDUP = 24, DASH_DURATION = 28;
    /// 直线冲刺速度，单位格/tick；起步后不再追踪目标。
    private static final double DASH_SPEED = 3.2;
    /// 蓄势时每 tick 保留的速度比例。
    private static final double DASH_DRAG = 0.85;
    /// 包围攻击的总倒计时，包含追赶入场与收圈，单位 tick；240 tick 为 12 秒。
    private static final int COIL_DURATION = 240;
    /// 围圈初始和最终半径，单位格。
    private static final double COIL_START_RADIUS = 28.0, COIL_END_RADIUS = 10.0;
    /// 盘绕沿圆周前进的速度，单位格/tick。
    private static final double COIL_SPEED = 2.4;
    /// 到达围圈入口的判定距离，单位格。
    private static final double COIL_ARRIVAL_RANGE = 2.5;
    private final DestroyerBody body;
    private final BitSet releasedProbes = new BitSet(SEGMENT_COUNT);
    private final List<TheDestroyerProbe> probes = new ArrayList<>();
    private Mode candidateMode = Mode.GROUND;
    private int candidateTicks, groundTicks, launchTicks, laserTicks, laserCursor = -1, probeTicks;
    private boolean launching, airborne, wasSolid, terrainInitialized, alternateVolley;
    private Vec3 velocity = Vec3.ZERO;
    private float previousRoll, displayedRoll, launchRoll;
    private long rollUpdateTick;
    private int flightTicks, apexTicks;

    /// TODO 新资源仅提供部分开合动画，待补齐头甲与侧翼的 opening/open/closing/closed 动画。
    /// TODO 补齐白漆贴图；METAL 已独立同步且持久化，后续按此状态选择双形态材质。
    private enum SpecialAttack {
        /// 未执行特殊攻击，按当前移动模式行动。
        NONE,
        /// 冲刺前蓄力并锁定本次方向。
        DASH_WINDUP,
        /// 沿锁定方向直线冲刺。
        DASH,
        /// 接近玩家，为环绕收紧建立位置。
        COIL_APPROACH,
        /// 倒计时内追踪玩家位置并环绕收紧。
        COIL
    }

    private SpecialAttack specialAttack = SpecialAttack.NONE;
    /// 有效受击时通知客户端生成机械火花的实体事件编号。
    private static final byte HURT_SPARK_EVENT = 72;
    /// 同一部位两次火花之间的最短间隔，单位 tick。
    private static final int HURT_SPARK_INTERVAL = 3;
    private int specialTicks, specialCooldown = SPECIAL_COOLDOWN, lastSparkTick = -HURT_SPARK_INTERVAL;
    private boolean nextCoil;
    private Vec3 dashDirection = Vec3.ZERO;
    private int coilTicksRemaining;
    private int lastDebrisTick = -DEBRIS_INTERVAL;
    private DestroyerPart attackingPart;
    private int lastLaserSoundTick = -LASER_SOUND_INTERVAL;
    private double coilAngle;
    private int beamCooldown = 200, beamCount;
    private Vec3 beamAim = Vec3.ZERO;

    public int getBeamTick() {return entityData.get(BEAM_TICK);}

    public Vec3 beamOrigin() {return getBoundingBox().getCenter().add(getLookAngle().scale(10));}


    public enum Mode {
        /// 地下游走和绕轴滚动，以冲撞为主。
        UNDERGROUND,
        /// 在地面下接近玩家，出土后按抛物线运动。
        GROUND,
        /// 空中追击并使用体节激光攻击。
        SKY
    }

    /// 部位基础值统一在 BossEntities 的状态参数中声明，共用头部的生命。
    public enum BodySection {
        /// 身体体节，使用身体部位参数。
        BODY,
        /// 尾部体节，使用尾部参数。
        TAIL
    }

    public TheDestroyer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        /// 头部俯仰由三维运动方向决定，禁止默认视线控制器每 tick 将其归零。
        lookControl = new LookControl(this) {
            @Override
            protected boolean resetXRotOnTick() {
                return false;
            }
        };
        noPhysics = true;
        setNoGravity(true);
        body = new DestroyerBody(this);
        /// 加入世界前为头部及部件保留连续 ID，客户端沿用头部生成包中的 ID。
        setId(ENTITY_COUNTER.getAndAdd(SEGMENT_COUNT) + 1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BEAM_TICK, -1);
        entityData.define(MODE, Mode.GROUND.ordinal());
        entityData.define(DEBRIS_STATE, Blocks.AIR.defaultBlockState());
        entityData.define(DEBRIS_ORIGIN, BlockPos.ZERO);
        entityData.define(METAL, false);
        entityData.define(ROLL, 0.0F);
        entityData.define(SHELL_OPEN, false);
        entityData.define(BODY_POSE, new CompoundTag());
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

    /// 受击箱扩大后仍以原身体中心为中心，接触伤害继续使用原身体尺寸。
    @Override
    protected AABB makeBoundingBox() {
        return super.makeBoundingBox().move(0, (getBbHeight() / HITBOX_SCALE - getBbHeight()) / 2.0, 0);
    }

    @Override
    public void onRemovedFromWorld() {
        if (isRemoved()) body.remove();
        super.onRemovedFromWorld();
    }

    /// 身体任意位置进入玩家视距即可追踪头部，配对与解除追踪仍由原版负责。
    public boolean isWithinTrackingRange(ServerPlayer player, double range) {
        return body.distanceToSqr(player.position(), true) <= range * range;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) DestroyerRenderer.tickBeamEffects(this);
        if (!level().isClientSide && !isRemoved()) entityData.set(BODY_POSE, body.createSnapshot());
    }

    @Override
    public Vec3 getLightProbePosition(float partialTick) {
        return body.getRenderPosition(0, partialTick).add(0, BODY_HEIGHT / 2.0, 0);
    }

    /// 全链共用快照插值，避免原版再单独插值头部造成头身位置不一致。
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps, boolean teleport) {
        if (!body.hasClientSnapshot()) super.lerpTo(x, y, z, yaw, pitch, steps, teleport);
    }

    @Override
    public double getAttributeValue(Attribute attribute) {
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
                return Math.max(0, Math.min(CreatureDifficultyRules.MAX_TOUGHNESS,
                        rule.toughness(partBase) + value - rule.toughness(headBase)));
            }
        }
        return value;
    }

    private static BodySection sectionOf(DestroyerPart part) {
        return part.isTail() ? BodySection.TAIL : BodySection.BODY;
    }

    /// 临时标记实际接触部位，继续走原有接触伤害、附魔和穿甲流程，不改写头部属性表。
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
            playSound(ModSoundEvents.METAL_HURT.get(), 1.0F, 1.0F);
            level().broadcastEntityEvent(this, HURT_SPARK_EVENT);
        }
        return damaged;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        /// 受击声由实际命中部位在服务端播放，避免客户端收到本体伤害包后再从头部播放。
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.METAL_DEATH.get();
    }

    public Mode getMode() {return Mode.values()[entityData.get(MODE)];}

    public boolean isMetal() {return entityData.get(METAL);}

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
        if (!level().isClientSide && isDisengageRetreating()) {
            setPos(position().add(getDeltaMovement()));
            body.update();
        }
    }

    /// 索敌与脱战都按整条身体计算距离，共用头部的唯一战斗目标。
    @Override
    protected double combatAnchorDistanceSqr(LivingEntity target) {
        return body.distanceToSqr(target.position(), false);
    }

    @Override
    protected Vec3 getDisengageMovement() {return new Vec3(0, -RETREAT_SPEED, 0);}

    @Override
    protected boolean hasEntityContactAttack() {return false;}

    private void tickDestroyer() {
        if (!isAlive() || isDisengageRetreating()) return;
        LivingEntity target = getTarget();
        updateStage();
        body.initializeTrail();
        if (target == null || !target.isAlive() || target.isRemoved() || target.level() != level()) {
            finishSpecialAttack();
            entityData.set(BEAM_TICK, -1);
            velocity = Vec3.ZERO;
            setDeltaMovement(Vec3.ZERO);
            body.update();
            return;
        }
        tickBeam(target);
        if (getBeamTick() < 0) updateMode(target);
        boolean solidBefore = isSolid(position());
        Vec3 previousVelocity = velocity;
        boolean ballistic = getMode() == Mode.GROUND && airborne;
        if (getBeamTick() == 100) finishSpecialAttack();
        boolean specialMovement = getBeamTick() >= 100 && getBeamTick() < 460 ? false : tickSpecialAttack(target);
        if (!specialMovement) switch (getMode()) {
            case UNDERGROUND -> {
                velocity = steer(velocity, target.getBoundingBox().getCenter().subtract(position()), BURROW_SPEED);
                entityData.set(ROLL, DestroyerBody.wrapRoll(entityData.get(ROLL) + ROLL_SPEED));
            }
            case SKY -> {
                velocity = steer(velocity, target.getBoundingBox().getCenter().subtract(position()), SKY_SPEED);
                float roll = entityData.get(ROLL);
                entityData.set(ROLL, tickCount % SKY_ROLL_INTERVAL < SKY_ROLL_TICKS
                        ? DestroyerBody.wrapRoll(roll + ROLL_SPEED) : DestroyerBody.followRoll(0, roll));
            }
            case GROUND -> tickGround(target, solidBefore);
        }
        if (getBeamTick() >= 100 && getBeamTick() < 220) {
            float slowdown = Mth.clamp((getBeamTick() - 100) / 120.0F, 0, 1);
            velocity = velocity.scale(1.0F - slowdown);
        } else if (getBeamTick() >= 220 && getBeamTick() < 460) {
            velocity = Vec3.ZERO;
        }
        if (!ballistic)
            velocity = WormSteering.limit(previousVelocity.lengthSqr() > 1.0E-7 ? previousVelocity : getLookAngle(),
                    velocity, SEGMENT_SPACING * TURN_RADIUS_SEGMENTS, MAX_TURN_RADIANS);
        Vec3 previous = position();
        setPos(previous.add(velocity));
        setDeltaMovement(velocity);
        if (getBeamTick() >= 100 && getBeamTick() < 420) {
            Vec3 desired = target.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter()).normalize();
            beamAim = WormSteering.turn(getLookAngle(), desired, Math.toRadians(2));
            faceCombatDirection(beamAim, 2, 2);
        } else if (getBeamTick() < 100)
            faceCombatDirection(specialAttack == SpecialAttack.DASH_WINDUP
                ? target.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter()) : velocity, 180, 180);
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
        attackContact(previous);
        tickLasers(target);
        tickProbes();
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
                velocity = velocity.scale(DASH_DRAG);
                if (specialTicks >= DASH_WINDUP) {
                    Vec3 direction = target.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter());
                    dashDirection = direction.lengthSqr() < 1.0E-7 ? getLookAngle() : direction.normalize();
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
                /// 圆心每 tick 读取玩家当前位置；倒计时持续消耗，追赶过程不会重置。
                double formationTicks = Math.PI * 2 * COIL_START_RADIUS / COIL_SPEED;
                double progress = Mth.clamp((COIL_DURATION - coilTicksRemaining - formationTicks) / (COIL_DURATION - formationTicks), 0, 1);
                double radius = Mth.lerp(progress, COIL_START_RADIUS, COIL_END_RADIUS);
                double step = COIL_SPEED / radius;
                coilAngle += step;
                Vec3 destination = coilCenter.add(Math.cos(coilAngle) * radius, 0, Math.sin(coilAngle) * radius);
                velocity = destination.subtract(position());
                if (velocity.lengthSqr() > COIL_SPEED * COIL_SPEED)
                    velocity = velocity.normalize().scale(COIL_SPEED);
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

    private void updateStage() {
        float threshold = switch (level().getDifficulty()) {
            case PEACEFUL, EASY -> EASY_THRESHOLD;
            case NORMAL -> NORMAL_THRESHOLD;
            case HARD -> HARD_THRESHOLD;
        };
        if (!isMetal() && getHealth() <= getMaxHealth() * threshold) {
            entityData.set(METAL, true);
            broadcastPhaseTransition();
        }
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
            /// 离地仍沿原方向滚动，速度逐渐下降；最高点后保持归正。
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

    /// 速度大小逐步变化，方向按转弯半径旋转，避免反向目标将速度抵消后直接掉头。
    static Vec3 steer(Vec3 current, Vec3 offset, double speed) {
        if (offset.lengthSqr() < 0.001) return current;
        double currentSpeed = current.length();
        double nextSpeed = currentSpeed + Mth.clamp(speed - currentSpeed, -STEERING, STEERING);
        return WormSteering.limit(current, offset.normalize().scale(nextSpeed),
                SEGMENT_SPACING * TURN_RADIUS_SEGMENTS, MAX_TURN_RADIANS);
    }

    void playVentOpeningSound(Vec3 position) {
        level().playSound(null, BlockPos.containing(position), SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 0.15F, 1.4F);
    }

    public boolean isSolid(Vec3 point) {
        BlockPos pos = BlockPos.containing(point);
        if (pos.getY() < level().getMinBuildHeight() || !level().hasChunkAt(pos)) return true;
        return !level().getBlockState(pos).getCollisionShape(level(), pos).isEmpty();
    }

    private boolean canStartBeam(LivingEntity target) {
        double distance = getBoundingBox().getCenter().distanceToSqr(target.getBoundingBox().getCenter());
        return isAlive() && target.isAlive() && target.level() == level() && isMetal() && beamCount < 2
                && specialAttack == SpecialAttack.NONE && getMode() == Mode.SKY && isBeamAboveGround() && getBeamTick() < 0 && beamCooldown <= 0 && distance >= 12 * 12 && distance <= 20 * 20;
    }

    /// 激光必须从地表上方的空气中发射，不加载未就绪的区块。
    private boolean isBeamAboveGround() {
        BlockPos pos = blockPosition();
        return level().hasChunkAt(pos) && !isSolid(position())
                && getY() >= level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
    }

    private void startBeam() {
        entityData.set(BEAM_TICK, 0);
        beamAim = getLookAngle();
        beamCount++;
    }

    private void tickBeam(LivingEntity target) {
        int tick = getBeamTick();
        if (tick < 0) {
            if (beamCount < 2 && beamCooldown > 0) beamCooldown--;
            if (canStartBeam(target) && random.nextInt(200) == 0) startBeam();
            return;
        }
        if (tick >= 460) {
            entityData.set(BEAM_TICK, -1);
            beamCooldown = 300;
            return;
        }
        if (tick >= 219 && tick < 420 && (getMode() != Mode.SKY || !isBeamAboveGround())) {
            entityData.set(BEAM_TICK, -1);
            beamCooldown = 300;
            return;
        }
        entityData.set(BEAM_TICK, tick + 1);
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
            level().playSound(null, pos.x, pos.y, pos.z, ModSoundEvents.ITEM_DEATH_LASER.get(),
                    SoundSource.HOSTILE, LASER_SOUND_VOLUME, LASER_SOUND_PITCH);
        }
    }

    private void tickProbes() {
        if (getMode() == Mode.UNDERGROUND || getMode() == Mode.GROUND && !airborne) return;
        if (++probeTicks < PROBE_INTERVAL) return;
        probeTicks = 0;
        probes.removeIf(p -> !p.isAlive() || p.isRemoved());
        if (probes.size() >= MAX_ACTIVE_PROBES) return;
        float missing = 1 - getHealth() / getMaxHealth();
        if (random.nextFloat() >= PROBE_BASE_CHANCE + missing * PROBE_HEALTH_CHANCE) return;
        int start = 1 + random.nextInt(SEGMENT_COUNT - 2);
        for (int n = 0; n < SEGMENT_COUNT - 2; n++) {
            int i = 1 + (start - 1 + n) % (SEGMENT_COUNT - 2);
            if (releasedProbes.get(i) || isSolid(body.getPosition(i))) continue;
            TheDestroyerProbe probe = new TheDestroyerProbe(BossEntities.THE_DESTROYER_PROBE.get(), level());
            probe.attach(this, body.getPosition(i));
            if (level().addFreshEntity(probe)) {
                probes.add(probe);
                addSubEntity(probe);
                releasedProbes.set(i);
            }
            return;
        }
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
        /// 先同步来源状态和位置，再发事件，避免客户端沿用上一次的材质。
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
                        player.setSecondsOnFire(SKY_BURN_SECONDS);
                    break;
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (level().isClientSide && event < 0 && -event < SEGMENT_COUNT) {
            body.getParts()[-event - 1].emitHurtSparks();
        } else if (level().isClientSide && event == DEBRIS_EVENT) {
            DestroyerDebrisParticle.spawn(entityData.get(DEBRIS_ORIGIN), entityData.get(DEBRIS_STATE));
        } else if (level().isClientSide && event == HURT_SPARK_EVENT) {
            MolangParticleEngine.INSTANCE.addEmitter(new ParticleEmitter(level(), getBoundingBox().getCenter(),
                    Confluence.asResource("mechanical_boss_hurt_spark"),
                    new MolangExp("variable.radius", Math.max(getBbWidth(), getBbHeight()) / 2.0F)));
        } else if (level().isClientSide && (event == DIG_EVENT || event == CROSS_EVENT)) {
            DestroyerCameraShake.start(this,
                    event == DIG_EVENT ? DIG_SHAKE_RADIUS : CROSS_SHAKE_RADIUS,
                    event == DIG_EVENT ? DIG_SHAKE_TICKS : CROSS_SHAKE_TICKS);
        } else super.handleEntityEvent(event);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DestroyerBeamCount", beamCount);
        tag.putInt("DestroyerBeamCooldown", beamCooldown);
        tag.putBoolean("DestroyerMetal", isMetal());
        tag.putInt("DestroyerMode", getMode().ordinal());
        tag.putLongArray("DestroyerProbes", releasedProbes.toLongArray());
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
        body.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        beamCount = tag.getInt("DestroyerBeamCount");
        beamCooldown = tag.contains("DestroyerBeamCooldown") ? tag.getInt("DestroyerBeamCooldown") : 200;
        entityData.set(METAL, tag.getBoolean("DestroyerMetal"));
        entityData.set(MODE, Mth.clamp(tag.getInt("DestroyerMode"), 0, Mode.values().length - 1));
        entityData.set(ROLL, tag.getFloat("DestroyerRoll"));
        releasedProbes.clear();
        releasedProbes.or(BitSet.valueOf(tag.getLongArray("DestroyerProbes")));
        airborne = tag.getBoolean("DestroyerAirborne");
        launching = tag.getBoolean("DestroyerLaunching");
        groundTicks = tag.getInt("DestroyerGroundTicks");
        flightTicks = tag.getInt("DestroyerFlightTicks");
        apexTicks = Math.max(1, tag.getInt("DestroyerApexTicks"));
        launchRoll = tag.getFloat("DestroyerLaunchRoll");
        velocity = new Vec3(tag.getDouble("DestroyerVX"), tag.getDouble("DestroyerVY"), tag.getDouble("DestroyerVZ"));
        body.load(tag);
        entityData.set(BODY_POSE, body.createSnapshot());
    }
}
