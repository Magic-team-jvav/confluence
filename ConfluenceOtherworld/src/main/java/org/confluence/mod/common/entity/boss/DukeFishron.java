package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.fishing.FishingSession;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.util.OverworldUtils;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntFunction;

public class DukeFishron extends BaseBoss {
    // 断线后在水面蓄势，再从水下冲到水面上方。时间单位为刻，距离单位为格。
    private static final int SUMMON_SWIRL_TICKS = 32, SUMMON_BREACH_TICKS = 12;
    private static final double SUMMON_RISE_HEIGHT = 5.0;
    // 在浮漂上方寻找水面时最多检查的水深，避免水下洞穴沿水柱被当作海面。
    private static final int SUMMON_SURFACE_SCAN = 16;
    // 生命值比例，用于切换第二、第三阶段。
    private static final float STORM_PHASE_HEALTH = 0.50F, DARK_PHASE_HEALTH = 0.15F;
    // 普通冲刺和暗幕冲刺的速度（格/刻）及连续段数。
    private static final double DASH_SPEED = 2.15, DARK_DASH_SPEED = 3.35;
    private static final int FIRST_DASHES = 3, SECOND_DASHES = 5;
    // 各段冲刺的蓄势和持续时间（刻）。
    private static final int WINDUP = 12, DARK_WINDUP = 5, DASH_TIME = 13, DARK_DASH_TIME = 9;
    // 招式之间的间隔、喷吐片段的时间和翻转一圈所需的时间（刻）。
    private static final int RECOVERY = 22, SHOOT_TIME = 25, FLIP_TIME = 14;
    // 大龙卷的描边时间、存续时间及半径（格）。
    private static final int ORBIT_TIME = 24, STORM_TIME = 200;
    static final double STORM_RADIUS = 10;
    static final double STORM_HEIGHT = 20;
    // 岸边的采样点可能落在陆地，每次召唤只尝试有限个水面位置。
    private static final int STORM_WATER_ATTEMPTS = 12;
    private static final double STORM_PULL = 0.12;
    // 鲨鱼龙生成间隔（刻）和单个公爵的在场上限。
    private static final int SHARK_INTERVAL = 22, MAX_SHARKS = 12;
    // 扇面左右各展开 60 度，落地洪流则向四周发射。
    private static final float FLOOD_HALF_ANGLE = (float) Math.PI / 3;
    private static final int FLOOD_COUNT = 11, SLAM_COUNT = 28;
    private static final ResourceLocation SUMMON_WHIRLPOOL = Confluence.asResource("duke_fishron_summon_swirl_vortex_1");
    private static final ResourceLocation SLAM_WATER_COLUMN = Confluence.asResource("duke_fishron_slam_water_column_1");
    // 翻转结束后等嘴部张开，每两刻喷出一发，直到喷吐动作结束。
    private static final int BUBBLE_FIRST_SHOT = 3, BUBBLE_INTERVAL = 2;
    private static final float BUBBLE_YAW_SPREAD = 0.95F;
    private static final double BUBBLE_VERTICAL_SPREAD = 0.55;
    // 张嘴动画中，出弹点相对身体中心的前向和垂直偏移，单位为模型像素。
    private static final double MOUTH_FORWARD = 51.0, MOUTH_VERTICAL = -9.0;
    private static final double FLOOD_SPEED = 2.0, BUBBLE_SPEED = 1.55, TYPHOON_SPEED = 1.8;
    // 暗幕瞬移在玩家视线后方取落点，同时避开离公爵太近的位置。
    private static final double TELEPORT_CHANCE = 0.5;
    private static final double TELEPORT_MIN_RADIUS = 10, TELEPORT_MAX_RADIUS = 13;
    private static final float TELEPORT_REAR_HALF_ANGLE = 30 * Mth.DEG_TO_RAD;
    private static final double TELEPORT_MIN_DISPLACEMENT = 4;
    private static final double TELEPORT_MIN_HEIGHT = 0.5, TELEPORT_MAX_HEIGHT = 2.0;
    private static final int TELEPORT_ATTEMPTS = 12;
    private static final int DASH_LEAD_TICKS = 3;
    // 脱离地表海洋时缩短冲刺前摇，并提高冲刺速度。
    private static final double ENRAGED_DASH_MULTIPLIER = 1.4;
    private static final int ENRAGED_WINDUP = 7;
    // 冲刺前一刻的朝向与预判方向相差不超过 10 度才可起步。
    private static final double DASH_ALIGNMENT = Math.cos(Math.toRadians(10));
    private static final float TURN = 30;
    private static final EntityDataAccessor<Byte> ACTION = SynchedEntityData.defineId(DukeFishron.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ACTION_START = SynchedEntityData.defineId(DukeFishron.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DARK_SKY = SynchedEntityData.defineId(DukeFishron.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("standing");
    private static final RawAnimation SHOOT = RawAnimation.begin().thenPlayAndHold("shoot");
    private static final RawAnimation OPEN_MOUTH = RawAnimation.begin().thenLoop("open_mouth");

    public enum Temperament {
        // 离开地表海洋时启用狂暴属性。
        ENRAGED
    }

    private enum Action {
        // 减速收招，按阶段选择下一招或接入瞬移冲刺。
        RECOVER,
        // 在目标前方放置固定小龙卷风。
        TORNADO,
        // 绕目标划出风圈，完成一圈后进入 STORM。
        ORBIT,
        // 维持风眼吸引，在圈内海面放出鲨鱼龙；目标离圈即结束。
        STORM,
        // 面向目标蓄力，结束时锁定冲刺方向。
        ALIGN,
        // 沿锁定方向冲刺，剩余段数决定是否重新瞄准。
        DASH,
        // 向前方扇形分批喷射洪流。
        FLOOD,
        // 完成前空翻后进入 BUBBLES。
        FLIP,
        // 朝目标持续喷泡泡，直到喷射窗口结束。
        BUBBLES,
        // 向目标放出成对的利刃台风。
        TYPHOONS,
        // 升至目标上方，为下砸留出高度。
        RISE,
        // 向下俯冲并释放环形洪流与中央水柱。
        SLAM,
        // 召唤入场时在水面形成漩涡。
        SUMMON_SWIRL,
        // 从召唤位置冲出水面，随后开始战斗。
        SUMMON_BREACH,
        // 第三阶段瞬移后短暂停顿，再锁定冲刺方向。
        BLINK_WINDUP;

        private static final IntFunction<Action> BY_ID = ByIdMap.sparse(Enum::ordinal, values(), RECOVER);

        private static Action fromOrdinal(int ordinal) {
            return BY_ID.apply(ordinal);
        }
    }

    private static final Action[] FIRST_SEQUENCE = {Action.TORNADO, Action.ALIGN, Action.FLOOD, Action.FLIP, Action.TYPHOONS};
    private static final Action[] SECOND_SEQUENCE = {Action.ORBIT, Action.ALIGN, Action.RISE, Action.FLIP, Action.TYPHOONS};

    private final List<Sharkron> sharks = new ArrayList<>();
    private Action action = Action.RECOVER;
    private int phase = 1, ticks, nextAttack, dashes;
    private Vec3 dash = Vec3.ZERO;
    private Vec3 stormCenter;
    private double stormBottom;
    private double orbitStart;
    private boolean enraged;
    private double summonWaterY, summonStartY;
    private UUID summoner;

    public DukeFishron(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ACTION, (byte) Action.RECOVER.ordinal());
        builder.define(ACTION_START, 0);
        builder.define(DARK_SKY, false);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        tickEncounter();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    @Override
    protected void updateHealthPhase() {
        if (isSummoning()) return;
        float health = getHealth() / getMaxHealth();
        int nextPhase = health <= DARK_PHASE_HEALTH ? 3 : health <= STORM_PHASE_HEALTH ? 2 : 1;
        if (nextPhase > phase) {
            phase = nextPhase;
            nextAttack = 0;
            entityData.set(DARK_SKY, phase == 3);
            finishAttack();
        }
    }

    private void tickEncounter() {
        if (isSummoning()) {
            if (level() instanceof ServerLevel server) tickSummoning(server);
            return;
        }
        LivingEntity target = getTarget();
        if (!(target instanceof Player player) || !canAttack(player)) {
            setDeltaMovement(Vec3.ZERO);
            if (action != Action.RECOVER) finishAttack();
            return;
        }

        boolean outsideOcean = !isSurfaceOcean(player);
        if (outsideOcean != enraged) {
            enraged = outsideOcean;
            setSpecialState(Temperament.ENRAGED, enraged);
        }

        ticks++;
        Vec3 aim = player.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter());
        if (action != Action.DASH && action != Action.ALIGN && action != Action.BLINK_WINDUP && action != Action.ORBIT && action != Action.SLAM)
            faceCombatDirection(aim, TURN, TURN);
        switch (action) {
            case RECOVER -> {
                setDeltaMovement(getDeltaMovement().scale(0.65));
                if (ticks < (phase == 3 ? DARK_WINDUP : RECOVERY)) return;
                if (phase == 3) {
                    dashes = 1;
                    boolean blinked = random.nextDouble() < TELEPORT_CHANCE && tryBlinkForDash(player);
                    begin(blinked ? Action.BLINK_WINDUP : Action.ALIGN);
                    return;
                }
                Action[] sequence = phase == 1 ? FIRST_SEQUENCE : SECOND_SEQUENCE;
                Action next = sequence[nextAttack++ % sequence.length];
                if (next == Action.ALIGN) dashes = phase == 1 ? FIRST_DASHES : SECOND_DASHES;
                if (next == Action.ORBIT) {
                    stormCenter = player.position();
                    orbitStart = Math.atan2(getZ() - stormCenter.z, getX() - stormCenter.x);
                }
                begin(next);
            }
            case ALIGN, BLINK_WINDUP -> {
                setDeltaMovement(phase == 3 ? Vec3.ZERO : getDeltaMovement().scale(0.55));
                Vec3 direction = dashIntercept(player).subtract(getBoundingBox().getCenter()).normalize();
                boolean aligned = Vec3.directionFromRotation(getXRot(), yBodyRot).dot(direction) >= DASH_ALIGNMENT;
                faceCombatDirection(direction, TURN, TURN);
                if (!aligned || ticks < (phase == 3 ? DARK_WINDUP : enraged ? ENRAGED_WINDUP : WINDUP))
                    return;
                double speed = phase == 3 ? DARK_DASH_SPEED : DASH_SPEED;
                dash = direction.scale(enraged ? speed * ENRAGED_DASH_MULTIPLIER : speed);
                faceCombatDirection(dash, 180, 180);
                begin(Action.DASH);
                if (phase == 3) setDeltaMovement(dash);
            }
            case DASH -> {
                faceCombatDirection(dash, TURN, TURN);
                setDeltaMovement(dash);
                if (ticks >= (phase == 3 ? DARK_DASH_TIME : DASH_TIME) || horizontalCollision && ticks > 2) {
                    if (--dashes > 0) begin(Action.ALIGN);
                    else {
                        if (phase == 3) setDeltaMovement(Vec3.ZERO);
                        finishAttack();
                    }
                }
            }
            case TORNADO -> {
                hover(aim);
                if (ticks == 12) {
                    Vec3 center = player.position().add(aim.multiply(1, 0, 1).normalize().scale(5));
                    FishronProjectile.launch(this, FishronProjectile.Kind.TORNADO, center, Vec3.ZERO, false);
                }
                if (ticks >= SHOOT_TIME) finishAttack();
            }
            case ORBIT, STORM -> tickStorm(player);
            case FLOOD -> {
                hover(aim);
                if (ticks == 12 || ticks == 16 || ticks == 20) flood(aim.normalize(), false);
                if (ticks >= SHOOT_TIME) finishAttack();
            }
            case FLIP -> {
                setDeltaMovement(getDeltaMovement().scale(0.7));
                if (ticks >= FLIP_TIME) begin(Action.BUBBLES);
            }
            case BUBBLES -> {
                hover(aim);
                if (ticks >= BUBBLE_FIRST_SHOT && (ticks - BUBBLE_FIRST_SHOT) % BUBBLE_INTERVAL == 0) {
                    spitBubble(player.getBoundingBox().getCenter());
                }
                if (ticks >= SHOOT_TIME) finishAttack();
            }
            case TYPHOONS -> {
                hover(aim);
                if (ticks == 12) typhoons(aim.normalize());
                if (ticks >= SHOOT_TIME) finishAttack();
            }
            case RISE -> {
                Vec3 above = player.position().add(0, 12, 0).subtract(position());
                setDeltaMovement(above.scale(0.22).normalize().scale(Math.min(1.65, above.length() * 0.22)));
                if (above.lengthSqr() < 4 || ticks >= 36) begin(Action.SLAM);
            }
            case SLAM -> {
                Vec3 down = new Vec3(0, -2.8, 0);
                faceCombatDirection(down, TURN, TURN);
                setDeltaMovement(down);
                if (onGround() || verticalCollision && ticks > 1 || ticks >= 30 || getY() <= player.getY()) {
                    setDeltaMovement(Vec3.ZERO);
                    emitSlamWaterColumn();
                    flood(new Vec3(1, 0, 0), true);
                    finishAttack();
                }
            }
        }
    }

    private void tickStorm(Player player) {
        if (action == Action.ORBIT) {
            double angle = orbitStart + Math.PI * 2 * ticks / ORBIT_TIME;
            Vec3 point = stormCenter.add(Math.cos(angle) * STORM_RADIUS, 4, Math.sin(angle) * STORM_RADIUS);
            Vec3 course = point.subtract(position());
            setDeltaMovement(course.scale(0.7));
            faceCombatDirection(course, TURN, TURN);
            if (ticks >= ORBIT_TIME) {
                begin(Action.STORM);
                stormBottom = FishronProjectile.launch(this, FishronProjectile.Kind.STORM, stormCenter, Vec3.ZERO, false).getY();
            }
            return;
        }
        if (!insideStorm(player.position()) || ticks >= STORM_TIME) {
            finishAttack();
            return;
        }
        Vec3 toPlayer = player.position().subtract(position());
        hover(toPlayer);
        for (Player caught : level().getEntitiesOfClass(Player.class, getBoundingBox().move(stormCenter.subtract(position())).inflate(STORM_RADIUS, Math.max(STORM_HEIGHT / 2, stormCenter.y - stormBottom), STORM_RADIUS),
                p -> canAttack(p) && insideStorm(p.position()))) {
            Vec3 pull = stormCenter.subtract(caught.position()).multiply(1, 0, 1).normalize().scale(STORM_PULL);
            caught.push(pull.x, 0, pull.z);
            caught.hurtMarked = true;
        }
        if (ticks % SHARK_INTERVAL == 0) spawnStormShark();
        if (ticks == 1 || ticks == 100)
            typhoons(player.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter()).normalize());
        if (ticks == 47 || ticks == 147) flood(toPlayer.normalize(), false);
    }

    private void spawnStormShark() {
        for (int attempt = 0; attempt < STORM_WATER_ATTEMPTS; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = Math.sqrt(random.nextDouble()) * STORM_RADIUS;
            double x = stormCenter.x + Math.cos(angle) * radius;
            double z = stormCenter.z + Math.sin(angle) * radius;
            BlockPos column = BlockPos.containing(x, stormCenter.y, z);
            if (!level().hasChunkAt(column)) continue;
            BlockPos surface = level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, column).below();
            if (!level().getFluidState(surface).is(FluidTags.WATER) || !isSurfaceOcean(level(), surface))
                continue;
            BlockPos submerged = surface.below();
            if (!level().getFluidState(submerged).is(FluidTags.WATER)) continue;
            Vec3 from = new Vec3(x, submerged.getY(), z);
            if (!level().noCollision(BossEntities.SHARKRON.get().getDimensions().makeBoundingBox(from)))
                continue;
            spawnShark(from);
            return;
        }
    }

    boolean insideStorm(Vec3 point) {
        if (stormCenter == null || action != Action.STORM) return false;
        Vec3 offset = point.subtract(stormCenter);
        return offset.horizontalDistanceSqr() <= STORM_RADIUS * STORM_RADIUS && point.y >= stormBottom && offset.y <= STORM_HEIGHT / 2;
    }

    // 大龙卷外缘优先转回风眼，内部在绕行和切向弹射之间变化。
    Vec3 stormTyphoonDirection(Vec3 position, Vec3 towardPlayer, int age, int seed) {
        Vec3 radial = position.subtract(stormCenter).multiply(1, 0, 1);
        if (radial.horizontalDistanceSqr() > STORM_RADIUS * STORM_RADIUS * 0.64)
            return radial.scale(-1).normalize();
        Vec3 tangent = new Vec3(-radial.z, 0, radial.x).normalize().scale(seed % 2 == 0 ? 1 : -1);
        if (Math.floorMod(age / 12 + seed, 3) == 0)
            return towardPlayer.yRot((seed % 7 - 3) * 0.45F);
        return tangent.add(towardPlayer.scale(0.45)).normalize();
    }

    private static boolean isSurfaceOcean(Player player) {
        return isSurfaceOcean(player.level(), player.blockPosition());
    }

    private static boolean isSurfaceOcean(Level level, BlockPos position) {
        return level.dimension() == OverworldUtils.dimension() && OverworldUtils.inSurface(position.getY())
                && OverworldUtils.isOcean(level.getBiome(position));
    }

    public static boolean tryFishingSpawn(ServerPlayer player, FishingHook hook, FishingSession session) {
        ServerLevel level = player.serverLevel();
        BlockPos water = hook.blockPosition();
        if (!level.getFluidState(water).is(FluidTags.WATER) && level.getBlockState(water).isAir())
            water = water.below();
        if (level.getDifficulty() == Difficulty.PEACEFUL || !level.getFluidState(water).is(FluidTags.WATER)
                || !OverworldUtils.isOcean(level.getBiome(water))) return false;
        if (!level.getEntitiesOfClass(DukeFishron.class, hook.getBoundingBox().inflate(96), DukeFishron::isAlive).isEmpty())
            return false;
        DukeFishron duke = BossEntities.DUKE_FISHRON.get().create(level);
        if (duke == null) return false;
        if (!duke.prepareSummoning(level, new Vec3(hook.getX(), water.getY(), hook.getZ()), player) || !level.addFreshEntity(duke))
            return false;
        level.playSound(null, hook.blockPosition(), ModSoundEvents.DECOUPLING.get(), SoundSource.NEUTRAL);
        session.consumeSummoningBait(player);
        return true;
    }

    private boolean prepareSummoning(ServerLevel level, Vec3 bobber, ServerPlayer player) {
        BlockPos water = BlockPos.containing(bobber);
        for (int i = 0; i < SUMMON_SURFACE_SCAN && level.getFluidState(water.above()).is(FluidTags.WATER); i++) {
            water = water.above();
        }
        if (level.getFluidState(water.above()).is(FluidTags.WATER)) return false;
        summonWaterY = water.getY() + level.getFluidState(water).getHeight(level, water);
        summonStartY = summonWaterY - getBbHeight() - 0.25;
        setPos(bobber.x, summonStartY, bobber.z);
        if (!level.noCollision(this)) {
            // 浅水用更靠近水面的起点，仍要确认整段上升路线没有方块。
            summonStartY = summonWaterY - 0.25;
            setPos(bobber.x, summonStartY, bobber.z);
        }
        if (!level.noCollision(this, getBoundingBox().expandTowards(0, summonWaterY + SUMMON_RISE_HEIGHT - summonStartY, 0)))
            return false;
        summoner = player.getUUID();
        faceSummoner(player, 180.0F);
        yRotO = yBodyRotO = yHeadRotO = getYRot();
        xRotO = getXRot();
        begin(Action.SUMMON_SWIRL);
        return true;
    }

    private void faceSummoner(Player player, float turnSpeed) {
        faceCombatDirection(player.position().subtract(position()).multiply(1, 0, 1), turnSpeed, 0);
        setXRot(0);
    }

    private void tickSummoning(ServerLevel level) {
        Player player = summoner == null ? null : level.getPlayerByUUID(summoner);
        if (player != null) faceSummoner(player, TURN);
        setDeltaMovement(Vec3.ZERO);
        ticks++;
        if (action == Action.SUMMON_SWIRL) {
            if (ticks == 1) emitSummoningWhirlpool();
            if (ticks >= SUMMON_SWIRL_TICKS) begin(Action.SUMMON_BREACH);
            return;
        }

        double previousY = getY();
        double progress = Math.min(1.0, ticks / (double) SUMMON_BREACH_TICKS);
        double eased = Math.sin(progress * Math.PI * 0.5);
        setPos(getX(), summonStartY + (summonWaterY + SUMMON_RISE_HEIGHT - summonStartY) * eased, getZ());
        if (previousY < summonWaterY && getY() >= summonWaterY) {
            level.playSound(null, BlockPos.containing(getX(), summonWaterY, getZ()), SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL, 1.7F, 0.8F);
            level.sendParticles(ParticleTypes.SPLASH, getX(), summonWaterY + 0.15, getZ(), 64, 1.8, 0.3, 1.8, 0.24);
        }
        if (ticks >= SUMMON_BREACH_TICKS) {
            finishAttack();
            if (player != null && isValidCurrentCombatPlayer(player)) setTarget(player);
            summoner = null;
        }
    }

    private void emitSummoningWhirlpool() {
        EmitterCreationPacketS2C packet = new EmitterCreationPacketS2C(
                SUMMON_WHIRLPOOL, new Vec3(getX(), summonWaterY + 0.08, getZ()).toVector3f(), MolangExp.EMPTY, -1);
        PacketDistributor.sendToPlayersTrackingEntity(this, packet);
    }

    private boolean isSummoning() {
        return action == Action.SUMMON_SWIRL || action == Action.SUMMON_BREACH;
    }

    public boolean isSummonSubmerged() {
        return entityData.get(ACTION) == Action.SUMMON_SWIRL.ordinal();
    }

    @Override
    protected boolean shouldMaintainCombatTarget() {return !isSummoning();}

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return (isSummoning() && !source.is(DamageTypes.GENERIC_KILL) && !source.is(DamageTypes.FELL_OUT_OF_WORLD))
                || super.isInvulnerableTo(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!isSummoning()) return;
        tag.putBoolean("SummonBreaching", action == Action.SUMMON_BREACH);
        tag.putDouble("SummonWaterY", summonWaterY);
        tag.putDouble("SummonStartY", summonStartY);
        tag.putInt("SummonTicks", ticks);
        if (summoner != null) tag.putUUID("Summoner", summoner);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains("SummonWaterY")) return;
        summonWaterY = tag.getDouble("SummonWaterY");
        summonStartY = tag.getDouble("SummonStartY");
        summoner = tag.hasUUID("Summoner") ? tag.getUUID("Summoner") : null;
        begin(tag.getBoolean("SummonBreaching") ? Action.SUMMON_BREACH : Action.SUMMON_SWIRL);
        ticks = tag.getInt("SummonTicks");
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (level() instanceof ServerLevel && !isAlive() && shouldShowMessage()
                && level().getDifficulty() != Difficulty.PEACEFUL && tryBeginDeathRewardSettlement()) {
            KillBoard.INSTANCE.defeat(getType());
        }
    }

    public boolean hasDarkSky() {
        return entityData.get(DARK_SKY);
    }

    boolean allowsProjectiles() {return phase < 3;}

    public boolean isEnraged() {return enraged;}

    @Override
    protected boolean hasEntityContactAttack() {return !isSummoning();}

    boolean trackShark(Sharkron shark) {
        sharks.removeIf(Entity::isRemoved);
        if (sharks.contains(shark)) return true;
        if (sharks.size() >= MAX_SHARKS) return false;
        return sharks.add(shark);
    }

    void spawnShark(Vec3 from) {
        sharks.removeIf(Entity::isRemoved);
        LivingEntity target = getTarget();
        if (sharks.size() >= MAX_SHARKS || target == null) return;
        Vec3 velocity = target.getBoundingBox().getCenter().subtract(from).normalize().scale(1.15);
        Sharkron shark = new Sharkron(BossEntities.SHARKRON.get(), level());
        shark.launch(this, from, velocity);
        if (level().addFreshEntity(shark)) trackShark(shark);
    }

    private void emitSlamWaterColumn() {
        EmitterCreationPacketS2C packet = new EmitterCreationPacketS2C(
                SLAM_WATER_COLUMN, position().toVector3f(), MolangExp.EMPTY, -1);
        PacketDistributor.sendToPlayersTrackingEntity(this, packet);
    }

    private void flood(Vec3 forward, boolean radial) {
        int count = radial ? SLAM_COUNT : FLOOD_COUNT;
        for (int i = 0; i < count; i++) {
            float angle = radial ? (float) (Math.PI * 2 * i / count) : FLOOD_HALF_ANGLE * (2.0F * i / (count - 1) - 1);
            shot(FishronProjectile.Kind.FLOOD, forward.yRot(angle).scale(FLOOD_SPEED), radial, radial ? position().add(0, 0.8, 0) : muzzle(forward));
        }
    }

    private void spitBubble(Vec3 target) {
        Vec3 from = bubbleMouth();
        Vec3 forward = target.subtract(from).normalize();
        Vec3 spread = forward.yRot((random.nextFloat() - 0.5F) * BUBBLE_YAW_SPREAD).add(0, (random.nextDouble() - 0.5) * BUBBLE_VERTICAL_SPREAD, 0);
        shot(FishronProjectile.Kind.BUBBLE, spread.normalize().scale(BUBBLE_SPEED), false, from);
    }

    private Vec3 bubbleMouth() {
        float pitch = getXRot();
        float yaw = getYRot();
        double scale = getScale() / 16.0;
        return getBoundingBox().getCenter()
                .add(Vec3.directionFromRotation(pitch, yaw).scale(MOUTH_FORWARD * scale))
                .add(Vec3.directionFromRotation(pitch - 90.0F, yaw).scale(MOUTH_VERTICAL * scale));
    }

    private void typhoons(Vec3 forward) {
        for (int side : new int[]{-1, 1}) {
            shot(FishronProjectile.Kind.TYPHOON, forward.yRot(side * 0.34F).scale(TYPHOON_SPEED), false, muzzle(forward));
        }
    }

    private void shot(FishronProjectile.Kind kind, Vec3 motion, boolean extended, Vec3 from) {
        FishronProjectile.launch(this, kind, from, motion, extended);
    }

    private Vec3 muzzle(Vec3 forward) {
        return getBoundingBox().getCenter().add(forward.scale(getBbWidth() * 0.55));
    }

    private void hover(Vec3 aim) {
        setDeltaMovement(aim.normalize().scale(Math.max(-0.55, Math.min(0.55, (aim.length() - 14) * 0.10))));
    }

    private Vec3 dashIntercept(Player player) {
        return player.getBoundingBox().getCenter().add(player.getDeltaMovement().scale(DASH_LEAD_TICKS));
    }

    private boolean tryBlinkForDash(Player player) {
        if (!(level() instanceof ServerLevel server)) return false;
        Vec3 intercept = dashIntercept(player);
        Vec3 behindPlayer = Vec3.directionFromRotation(0.0F, player.getYRot()).scale(-1);
        double halfHeight = getBbHeight() * 0.5;

        for (int attempt = 0; attempt < TELEPORT_ATTEMPTS; attempt++) {
            float angle = (random.nextFloat() * 2 - 1) * TELEPORT_REAR_HALF_ANGLE;
            double radius = Mth.lerp(random.nextDouble(), TELEPORT_MIN_RADIUS, TELEPORT_MAX_RADIUS);
            Vec3 rearOffset = behindPlayer.yRot(angle).scale(radius);
            double y = intercept.y + Mth.lerp(random.nextDouble(), TELEPORT_MIN_HEIGHT, TELEPORT_MAX_HEIGHT) - halfHeight;
            Vec3 destination = new Vec3(player.getX() + rearOffset.x, y, player.getZ() + rearOffset.z);
            if (distanceToSqr(destination) < TELEPORT_MIN_DISPLACEMENT * TELEPORT_MIN_DISPLACEMENT)
                continue;
            AABB bounds = getBoundingBox().move(destination.subtract(position()));
            if (bounds.minY < server.getMinBuildHeight() || bounds.maxY >= server.getMaxBuildHeight()
                    || !server.getWorldBorder().isWithinBounds(bounds)
                    || !server.hasChunksAt(BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ),
                    BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ))
                    || !server.noCollision(this, bounds)) continue;

            Vec3 center = bounds.getCenter();
            if (server.clip(new ClipContext(center, intercept, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this))
                    .getType() != HitResult.Type.MISS) continue;

            Vec3 departure = getBoundingBox().getCenter();
            setDeltaMovement(Vec3.ZERO);
            faceCombatDirection(intercept.subtract(center), 180, 180);
            teleportTo(destination.x, destination.y, destination.z);
            drawBlink(server, departure);
            drawBlink(server, center);
            return true;
        }
        return false;
    }

    private static void drawBlink(ServerLevel level, Vec3 center) {
        level.sendParticles(ParticleTypes.SQUID_INK, center.x, center.y, center.z, 12, 0.65, 0.40, 0.65, 0.025);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        // 实体追踪先下发位移，再同步招式。落点直接采用最新位置，避免把闪现插值成飞行。
        if (key == ACTION && level().isClientSide && entityData.get(ACTION) == Action.BLINK_WINDUP.ordinal() && lerpSteps > 0) {
            float yaw = (float) lerpYRot;
            moveTo(lerpX, lerpY, lerpZ, yaw, (float) lerpXRot);
            lerpSteps = 0;
            lerpHeadSteps = 0;
            setYHeadRot(yaw);
            setYBodyRot(yaw);
            yHeadRotO = yaw;
            yBodyRotO = yaw;
        }
    }

    private void finishAttack() {
        stormCenter = null;
        begin(Action.RECOVER);
    }

    private void begin(Action next) {
        action = next;
        ticks = 0;
        entityData.set(ACTION, (byte) next.ordinal());
        entityData.set(ACTION_START, (int) level().getGameTime());
    }

    public float flipDegrees(float partialTick) {
        if (entityData.get(ACTION) != Action.FLIP.ordinal()) return 0;
        return Math.min(1, Math.max(0, ((int) level().getGameTime() - entityData.get(ACTION_START) + partialTick) / FLIP_TIME)) * 360;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 3, state -> state.setAndContinue(switch (Action.fromOrdinal(entityData.get(ACTION))) {
            case TORNADO, FLOOD, TYPHOONS -> SHOOT;
            case BUBBLES -> OPEN_MOUTH;
            case RECOVER, ALIGN, BLINK_WINDUP, SUMMON_SWIRL, SUMMON_BREACH -> IDLE;
            default -> SWIM;
        })));
    }

    @Override
    protected SoundEvent getAmbientSound() {return ModSoundEvents.DUKE_FISHRON_FREE.get();}

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {return ModSoundEvents.DUKE_FISHRON_HURT.get();}

    @Override
    protected SoundEvent getDeathSound() {return ModSoundEvents.DUKE_FISHRON_DEATH.get();}
}
