package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.effect.harmful.HorrifiedEffect;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.FireImp;
import org.confluence.mod.common.entity.monster.SimpleWormMonster;
import org.confluence.mod.common.entity.monster.TheHungry;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 血肉墙的服务端战斗主体。
///
/// 本体负责整面墙的推进、阶段和参战者管理；眼睛与嘴是可命中的临时部件，
/// 各自维护射击或吐出水蛭的节奏。墙面布局由一个持久化种子生成，因此仍保留
/// 墙面外观使用持久随机布局，保证区块重载后不会换成另一套眼、嘴和饿鬼位置。
public class WallOfFlesh extends BaseBoss implements IEntityAdditionalSpawnData {
    /// FTW 模式每 tick 尝试召唤火焰小鬼的概率分母。
    private static final int FTW_IMP_SPAWN_CHANCE = 60;
    /// 同一肉墙附近最多同时存在的火焰小鬼数量。
    private static final int FTW_IMP_LIMIT = 4;
    /// 统计已存在火焰小鬼的搜索半径，单位格。
    private static final double FTW_IMP_SEARCH_RANGE = 64.0;
    /// 每次召唤小鬼寻找可站立落点的最大尝试次数。
    private static final int FTW_IMP_SPAWN_ATTEMPTS = 16;

    private static final EntityDataAccessor<Boolean> DATA_PHASE_TWO = SynchedEntityData.defineId(WallOfFlesh.class, EntityDataSerializers.BOOLEAN);

    private static final String PHASE_TWO_TAG = "PhaseTwo";
    private static final String INITIAL_X_TAG = "InitialX";
    private static final String INITIAL_Y_TAG = "InitialY";
    private static final String INITIAL_Z_TAG = "InitialZ";
    private static final String LAYOUT_SEED_TAG = "LayoutSeed";
    private static final String HUNGRY_TIMER_TAG = "HungryTimer";
    private static final String HUNGRY_INITIALIZED_TAG = "HungryInitialized";

    /// 肉墙沿前进方向距初始位置的终点距离，单位格；到达后击杀仍受恐惧束缚的玩家并移除。
    private static final double FINISH_LINE_DISTANCE = 2000.0;
    /// 地形处理网格的水平采样数量。
    private static final int GRID_SIZE_X = 40;
    private final Map<UUID, Long> contactHits = new HashMap<>();
    /// 地形处理网格的竖直采样数量。
    private static final int GRID_SIZE_Y = 30;
    /// 相邻地形采样点之间的距离，单位格。
    private static final double GRID_SPACING = 15.0;
    /// 肉墙眼睛与嘴等部位的布局间距，单位格。
    private static final double PART_LAYOUT_SPACING = 20.0;
    /// 追击场地由网格数量与间距计算出的宽度，单位格。
    private static final double PURSUIT_WIDTH = GRID_SIZE_X * GRID_SPACING;
    /// 追击场地由网格数量与间距计算出的高度，单位格。
    private static final double PURSUIT_HEIGHT = GRID_SIZE_Y * GRID_SPACING;
    /// 追击区域允许的前后纵深，单位格。
    private static final double PURSUIT_DEPTH = 150.0;
    /// 下界肉墙特殊地形处理所用的参考高度，单位格；实际阈值取其三分之二。
    private static final double NETHER_GENERATION_HEIGHT = 128.0;
    /// 刷新肉墙周围区块保持加载状态的间隔，单位 tick。
    private static final int CHUNK_REFRESH_INTERVAL = 5;
    /// 区块票据在未刷新时保留的时间，单位 tick。
    private static final int CHUNK_RETENTION_TICKS = 30 * 20;

    /// 饿鬼重新生成的基础间隔，单位 tick。
    private static final int HUNGRY_RESPAWN_INTERVAL = 1200;
    /// 同一玩家同时分配到的嘴部攻击实体上限。
    private static final int MAX_ASSIGNED_MOUTHS = 2;
    /// 同一玩家同时分配到的眼部攻击实体上限。
    private static final int MAX_ASSIGNED_EYES = 4;

    /// 火焰小鬼随机落点的水平坐标取值宽度，单位格。
    private static final int FTW_IMP_HORIZONTAL_SPAWN_WIDTH = 25;
    /// 火焰小鬼随机落点的水平坐标中心偏移，单位格。
    private static final int FTW_IMP_HORIZONTAL_SPAWN_OFFSET = 12;
    /// 火焰小鬼随机落点的竖直坐标取值高度，单位格。
    private static final int FTW_IMP_VERTICAL_SPAWN_HEIGHT = 9;
    /// 火焰小鬼随机落点的竖直坐标中心偏移，单位格。
    private static final int FTW_IMP_VERTICAL_SPAWN_OFFSET = 4;

    /// 肉墙追击时每 tick 保留的旧移动速度比例。
    private static final double PURSUIT_MOMENTUM = 0.91D;
    /// 肉墙属性移动速度转为前进加速度的系数。
    private static final double PURSUIT_ACCELERATION_FACTOR = 0.125D;
    /// 空闲饿鬼锚点每次检查时重新生成饿鬼的概率。
    private static final float HUNGRY_RESPAWN_CHANCE = 0.4F;

    private Vec3 initialPosition = Vec3.ZERO;
    private long layoutSeed;
    private int hungryTimer = HUNGRY_RESPAWN_INTERVAL;
    private boolean hungryInitialized;
    private boolean layoutGenerated;
    private boolean needsInitialPlacement;
    private final BossChunkTicket placementChunkTicket = new BossChunkTicket(getUUID());
    private final List<Vec3> eyeAnchors = new ArrayList<>();
    private final List<Vec3> mouthAnchors = new ArrayList<>();
    private final List<Vec3> hungryAnchors = new ArrayList<>();
    private WallOfFleshPart[] wallParts = new WallOfFleshPart[0];
    private static final EntityDataAccessor<CompoundTag> PART_TARGETS = SynchedEntityData.defineId(WallOfFlesh.class, EntityDataSerializers.COMPOUND_TAG);
    private final List<WallOfFleshEye> eyes = new ArrayList<>();
    private final List<WallOfFleshMouth> mouths = new ArrayList<>();
    private final Map<WallOfFleshEye, Player> eyeAssignments = new HashMap<>();
    private final Map<WallOfFleshMouth, Player> mouthAssignments = new HashMap<>();
    private Vec3 struckPartPosition;
    private WallOfFleshPart struckPart;
    private Vec3 rewardPosition;

    public boolean hurtFromPart(WallOfFleshPart part, DamageSource source, float amount) {
        WallOfFleshPart previous = struckPart;
        Vec3 previousPosition = struckPartPosition;
        struckPart = part;
        struckPartPosition = part.getBoundingBox().getCenter();
        try {
            return hurt(source, amount);
        } finally {
            struckPart = previous;
            struckPartPosition = previousPosition;
        }
    }

    @Override
    public Vec3 getRewardPosition(ServerPlayer player) {
        return rewardPosition == null ? position() : rewardPosition;
    }

    /// 公共钱币只结算一次，落在击杀来源附近的墙体部件处，不使用高空的本体中心。
    public Vec3 getCoinDropPosition(DamageSource source) {
        if (rewardPosition != null) return rewardPosition;
        Entity anchor = source.getEntity();
        if (anchor == null) anchor = getTarget();
        if (anchor == null) {
            List<ServerPlayer> participants = getOnlineCombatParticipants();
            if (!participants.isEmpty()) anchor = participants.get(0);
        }
        if (anchor == null) return position();
        Vec3 nearest = null;
        double distance = Double.MAX_VALUE;
        for (WallOfFleshPart part : wallParts) {
            double candidateDistance = part.distanceToSqr(anchor);
            if (candidateDistance < distance) {
                distance = candidateDistance;
                nearest = part.getBoundingBox().getCenter();
            }
        }
        return nearest == null ? anchor.position() : nearest.add(getForwardVector().scale(2));
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && rewardPosition == null) {
            Vec3 anchor = struckPartPosition == null ? getCoinDropPosition(source)
                    : struckPartPosition.add(getForwardVector().scale(2));
            prepareRewardSite(BlockPos.containing(anchor));
        }
        super.die(source);
    }

    /// 普通战利品也落到同一奖励点，不通过移动本体来重定位掉落。
    @Override
    public ItemEntity spawnAtLocation(ItemStack stack, float offset) {
        ItemEntity item = super.spawnAtLocation(stack, offset);
        if (item != null && rewardPosition != null) item.setPos(rewardPosition);
        return item;
    }

    /// 只在已有空腔内建框；不挖掘地形，也不覆盖容器和玩家建筑。
    private void prepareRewardSite(BlockPos anchor) {
        for (int radius = 0; radius <= 8; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        if (Math.abs(dx) != radius && Math.abs(dy) != radius && Math.abs(dz) != radius)
                            continue;
                        BlockPos center = anchor.offset(dx, dy, dz);
                        if (canBuildRewardSite(center, true)) {
                            buildRewardSite(center, true);
                            return;
                        }
                    }
                }
            }
        }
        /// 大空腔不存在时只找三格宽的承托平台，保留周围原有方块。
        for (int dy = 0; dy <= 8; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos center = anchor.offset(dx, dy, dz);
                    if (canBuildRewardSite(center, false)) {
                        buildRewardSite(center, false);
                        return;
                    }
                }
            }
        }
        /// 连小平台都无法安全放置时不强拆地形，保留战场落点。
        rewardPosition = Vec3.atBottomCenterOf(anchor);
    }

    private boolean canBuildRewardSite(BlockPos center, boolean frame) {
        int radius = frame ? 2 : 1;
        int top = frame ? 3 : 1;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -1, -radius), center.offset(radius, top, radius))) {
            if (level().isOutsideBuildHeight(pos) || !level().hasChunkAt(pos)) return false;
            BlockState state = level().getBlockState(pos);
            if (state.is(Blocks.BEDROCK) || state.hasBlockEntity() || state.getDestroySpeed(level(), pos) < 0
                    || state.is(BlockTags.FEATURES_CANNOT_REPLACE)) return false;
            boolean floor = pos.getY() == center.getY() - 1;
            if (floor && !frame && state.isFaceSturdy(level(), pos, Direction.UP)) continue;
            if (floor && state.getBlock() instanceof LiquidBlock) continue;
            if (!state.isAir()) return false;
        }
        return level().getEntities(this, new AABB(center.offset(-radius, -1, -radius), center.offset(radius + 1, top + 1, radius + 1)),
                entity -> entity instanceof LivingEntity && entity.isAlive()).isEmpty();
    }

    private void buildRewardSite(BlockPos center, boolean frame) {
        /// 与 1.21 一致：整座奖励保护框等概率使用魔矿砖或猩红矿砖。
        BlockState frameState = (random.nextBoolean() ? DecorativeBlocks.DEMONITE_ORE_BRICKS
                : DecorativeBlocks.CRIMTANE_ORE_BRICKS).FULL.get().defaultBlockState();
        int radius = frame ? 2 : 1;
        int top = frame ? 3 : -1;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -1, -radius), center.offset(radius, top, radius))) {
            boolean shell = pos.getY() == center.getY() - 1 || pos.getY() == center.getY() + top
                    || Math.abs(pos.getX() - center.getX()) == radius || Math.abs(pos.getZ() - center.getZ()) == radius;
            BlockState state = level().getBlockState(pos);
            if (shell && (state.isAir() || state.getBlock() instanceof LiquidBlock))
                level().setBlock(pos, frameState, 3);
        }
        rewardPosition = Vec3.atBottomCenterOf(center).add(0, 0.5, 0);
    }

    public WallOfFlesh(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 3000;
        if (!level.isClientSide) {
            layoutSeed = random.nextLong();
            ensureWallLayout();
        }
    }

    /// 血肉墙的高度由竞技场和墙体布局决定，不能被重力逐 tick 下拉。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    /// 客户端可能只加载墙面边缘；管理原点区块未加载时仍需推进本体插值。
    @Override
    public boolean isAlwaysTicking() {
        return level().isClientSide;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PHASE_TWO, false);
        entityData.define(PART_TARGETS, new CompoundTag());
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                /// 血肉墙使用自身的固定推进时序；行为树只保留空闲节点，避免通用追击
                /// 行为在每个 tick 覆盖墙体的轴向速度。
                return new WaitAction(100);
            }
        };
    }

    public boolean isPhaseTwo() {
        return entityData.get(DATA_PHASE_TWO);
    }

    /// 返回当前固定前进方向。血肉墙只允许沿水平四个主方向移动。
    public Vec3 getForwardVector() {
        Direction direction = getDirection();
        return new Vec3(direction.getStepX(), 0.0, direction.getStepZ());
    }

    public void setForward(Direction direction) {
        if (direction.getAxis().isVertical()) {
            throw new IllegalArgumentException("Wall of Flesh requires a horizontal direction: " + direction);
        }
        float rotation = direction.toYRot();
        LibEntityUtils.setEntityYaw(this, rotation);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) {
            if (initialPosition == Vec3.ZERO) {
                /// 本回调执行时实体已加入当前区段。此时跨区块移动会使
                /// 实体脱离 ticking 列表，因此初始后移必须等到首个正式 tick 再执行。
                needsInitialPlacement = true;
            } else {
                ensureWallLayout();
            }
        }
    }

    /// 在实体进入服务端 ticking 列表后完成高度修正和后移。
    /// 移动完成后立即把区域票据迁移到落点；不能在服务器 tick 内同步等待区块生成，
    /// 否则巨型墙面的加载会阻塞整条服务器线程。
    private boolean applyInitialPlacement() {
        if (!needsInitialPlacement) {
            return true;
        }
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        Vec3 summonPosition = new Vec3(getX(), level().getMinBuildHeight() + GRID_SIZE_Y * GRID_SPACING * 0.5, getZ())
                .add(getForwardVector().scale(-50.0));
        BlockPos summonBlockPos = BlockPos.containing(summonPosition);
        placementChunkTicket.refresh(serverLevel, new ChunkPos(summonBlockPos), BossChunkTicket.REGION_DISTANCE);
        if (!serverLevel.isPositionEntityTicking(summonBlockPos)) return false;
        moveTo(summonPosition.x, summonPosition.y, summonPosition.z, getYRot(), getXRot());
        initialPosition = summonPosition;
        needsInitialPlacement = false;
        placementChunkTicket.release();
        refreshWallChunkTickets(serverLevel);
        return true;
    }

    private void refreshWallChunkTickets(ServerLevel serverLevel) {
        WallChunkRetention.refresh(serverLevel, getUUID(), getWallBounds(), serverLevel.getGameTime());
    }

    /// Real wall plane, excluding the much deeper pursuit/drag volume.
    AABB getWallBounds() {
        boolean movingAlongX = getDirection().getAxis() == Direction.Axis.X;
        double thickness = Math.max(1.0D, getBbWidth());
        return AABB.ofSize(position(),
                movingAlongX ? thickness : PURSUIT_WIDTH * getScale(),
                PURSUIT_HEIGHT * getScale(),
                movingAlongX ? PURSUIT_WIDTH * getScale() : thickness);
    }

    public boolean isWithinTrackingRange(Player player, int range) {
        if (player.level() != level()) return false;
        AABB bounds = getWallBounds();
        double x = player.getX() - Mth.clamp(player.getX(), bounds.minX, bounds.maxX);
        double z = player.getZ() - Mth.clamp(player.getZ(), bounds.minZ, bounds.maxZ);
        return x * x + z * z <= (double) range * range;
    }

    /// Removal is an explicit teardown; abandoned refreshes otherwise expire after 30 seconds.
    @Override
    public void remove(RemovalReason reason) {
        placementChunkTicket.release();
        if (level() instanceof ServerLevel serverLevel) {
            WallChunkRetention.release(serverLevel, getUUID());
        }
        super.remove(reason);
    }

    @Override
    public void tick() {
        boolean placementReady = level().isClientSide
                || applyInitialPlacement();
        super.tick();
        if (!isAlive()) {
            return;
        }

        lockCardinalRotation();
        if (level().isClientSide) {
            updatePartPositions();
            return;
        }
        if (!placementReady) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level();
        WallChunkRetention.expire(serverLevel, serverLevel.getGameTime());
        if (tickCount % CHUNK_REFRESH_INTERVAL == 0) {
            refreshWallChunkTickets(serverLevel);
        }
        updatePhase();
        acquireFrontTarget();
        updateHorrifiedPlayers();
        ensureWallLayout();
        updatePartPositions();
        if (tickCount % 10 == 0) {
            updatePartAssignments();
        }
        for (WallOfFleshPart part : wallParts) part.tickPart();
        updateMovement();
        updateHungrySlots();
        tickFireImps();
        checkFinishLine();
    }

    private void lockCardinalRotation() {
        float rotation = getDirection().toYRot();
        LibEntityUtils.setEntityYaw(this, rotation);
    }

    private void updatePhase() {
        if (!isPhaseTwo() && getHealth() < getMaxHealth() * 0.5F) {
            entityData.set(DATA_PHASE_TWO, true);
        }
        setSpecialState(CombatState.WOUNDED, isPhaseTwo());
    }

    private void acquireFrontTarget() {
        LivingEntity current = getTarget();
        if (current instanceof Player player ? isValidCurrentCombatPlayer(player) : isValidFrontTarget(current)) {
            return;
        }
        setTarget(findCombatPlayer());
    }

    @Override
    protected boolean maintainsEncounterChunkTicket() {
        // 血肉墙按长条战斗区域维护多组专用票据，不能与本体中心票据互相覆盖。
        return false;
    }

    @Override
    protected Vec3 getDisengageMovement() {
        /// 撤离期间基类会屏蔽普通速度写入，包括 travel 的阻力更新，因此在此保留水平阻力。
        return getDeltaMovement().scale(PURSUIT_MOMENTUM).add(getForwardVector().scale(getAttributeValue(Attributes.MOVEMENT_SPEED) * PURSUIT_ACCELERATION_FACTOR));
    }

    @Override
    protected boolean isValidCurrentCombatPlayer(Player player) {
        return super.isValidCurrentCombatPlayer(player)
                && (isValidFrontTarget(player) || HorrifiedEffect.isBoundTo(player, this));
    }

    /// 巨型墙面的战斗距离从实际墙体边界计算，不能用远离玩家的管理原点触发脱战。
    @Override
    protected double combatAnchorDistanceSqr(LivingEntity target) {
        AABB bounds = getWallBounds();
        double x = target.getX() - Mth.clamp(target.getX(), bounds.minX, bounds.maxX);
        double y = target.getY() - Mth.clamp(target.getY(), bounds.minY, bounds.maxY);
        double z = target.getZ() - Mth.clamp(target.getZ(), bounds.minZ, bounds.maxZ);
        return x * x + y * y + z * z;
    }

    /// 墙后的参战者仍属于当前战斗，必须由狂卷之舌拉回墙前，而不是被当作无目标脱战。
    public boolean isBehindWall(LivingEntity living) {
        return living.position().subtract(position()).dot(getForwardVector()) < 0.0;
    }

    boolean isValidFrontTarget(@Nullable LivingEntity target) {
        if (target == null || !target.isAlive() || !canAttack(target)) {
            return false;
        }
        Vec3 horizontal = target.position().subtract(position()).multiply(1.0, 0.0, 1.0);
        return target.getBoundingBox().intersects(getPursuitBox())
                && (horizontal.lengthSqr() < 1.0E-6 || horizontal.normalize().dot(getForwardVector()) >= 0.0);
    }

    /// 返回血肉墙前方的追逐区域。区域会随四向朝向旋转，但不会随玩家视角改变。
    public AABB getPursuitBox() {
        Vec3 center = position().add(getForwardVector().scale(PURSUIT_DEPTH * 0.5));
        boolean movingAlongX = getDirection().getAxis() == Direction.Axis.X;
        double xSize = movingAlongX ? PURSUIT_DEPTH : PURSUIT_WIDTH;
        double zSize = movingAlongX ? PURSUIT_WIDTH : PURSUIT_DEPTH;
        return AABB.ofSize(center, xSize, PURSUIT_HEIGHT, zSize);
    }

    public @Nullable WallOfFleshMouth findTongueMouth(LivingEntity living) {
        WallOfFleshMouth nearest = null;
        WallOfFleshMouth nearestClear = null;
        double nearestDistance = Double.MAX_VALUE;
        double nearestClearDistance = Double.MAX_VALUE;
        for (Entity entity : wallParts) {
            if (!(entity instanceof WallOfFleshMouth mouth) || !mouth.isAlive() || mouth.getY() <= level().getMinBuildHeight())
                continue;
            if (level().dimension() == OverworldUtils.underworld() && mouth.getY() >= NETHER_GENERATION_HEIGHT * 2.0 / 3.0)
                continue;
            double distance = mouth.distanceToSqr(living);
            if (distance < nearestDistance) {
                nearest = mouth;
                nearestDistance = distance;
            }
            if (!mouth.isInWall() && distance < nearestClearDistance) {
                nearestClear = mouth;
                nearestClearDistance = distance;
            }
        }
        return nearestClear == null ? nearest : nearestClear;
    }

    /// 返回包含完整墙面及其前方战斗带的客户端剔除范围。
    @Override
    public AABB getBoundingBoxForCulling() {
        return getPursuitBox();
    }

    /// 巨型墙面不能使用普通八格实体的默认距离剔除。
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    private void updateHorrifiedPlayers() {
        AABB pursuitBox = getPursuitBox();
        for (Player player : level().players()) {
            if ((player.isCreative() && !isDeveloperTarget(player)) || player.isSpectator()) {
                continue;
            }
            /// 首次进入战斗带立即绑定，避免玩家在效果刷新前穿到背面后被当作无目标。
            if (player.getBoundingBox().intersects(pursuitBox) && !HorrifiedEffect.isBoundTo(player, this)) {
                HorrifiedEffect.bind(player, this);
                registerCombatParticipant(player);
            }
            if (tickCount % 20 == 0) {
                if (HorrifiedEffect.isBoundTo(player, this)) {
                    player.addEffect(new MobEffectInstance(ModEffects.HORRIFIED.get(), 100), this);
                }
            }
            if (HorrifiedEffect.isBoundTo(player, this) && isBehindWall(player)
                    && !player.hasEffect(ModEffects.THE_TONGUE.get())) {
                player.addEffect(new MobEffectInstance(ModEffects.THE_TONGUE.get(), 60), this);
            }
        }
    }

    private void updateMovement() {
        /// 半血后的属性增幅自然提高加速度与巡航速度，不直接跳到固定速度。
        double health = getHealth() / getMaxHealth();
        double acceleration = 1.0 + (health < 0.5 ? (0.5 - health) * 2.0 : 0.0);
        if (isExpert()) acceleration += Math.pow(1.0 - health, 3.0);
        if (isFtw()) acceleration = acceleration * 1.1 + 0.2;
        setDeltaMovement(getDeltaMovement().add(getForwardVector().scale(getAttributeValue(Attributes.MOVEMENT_SPEED) * PURSUIT_ACCELERATION_FACTOR * acceleration)));
    }

    /// 根据持久化种子建立墙面布局，并补回不参与存档的眼睛与嘴部实体。
    private void ensureWallLayout() {
        if (layoutGenerated) return;
        generateWallLayout(RandomSource.create(layoutSeed));
        buildParts();
        /// 服务端在加入世界前分配连续 ID；客户端使用生成包携带的本体 ID。
        setId(ENTITY_COUNTER.getAndAdd(wallParts.length + 1) + 1);
        layoutGenerated = true;
    }

    private void buildParts() {
        eyes.clear();
        mouths.clear();
        wallParts = new WallOfFleshPart[eyeAnchors.size() + mouthAnchors.size()];
        int index = 0;
        for (Vec3 ignored : eyeAnchors) {
            WallOfFleshEye eye = new WallOfFleshEye(this, index);
            eyes.add(eye);
            wallParts[index++] = eye;
        }
        for (Vec3 ignored : mouthAnchors) {
            WallOfFleshMouth mouth = new WallOfFleshMouth(this, index);
            mouths.add(mouth);
            wallParts[index++] = mouth;
        }
        updatePartPositions();
    }

    @Override
    public boolean isMultipartEntity() {return true;}

    @Override
    public WallOfFleshPart[] getParts() {return wallParts;}

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int index = 0; index < wallParts.length; index++)
            wallParts[index].setId(id + index + 1);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /// Forge 在普通附加数据回调之前就注册部件，因此必须在客户端工厂中先建立布局。
    public static WallOfFlesh createClient(PlayMessages.SpawnEntity packet, Level level) {
        WallOfFlesh wall = new WallOfFlesh(BossEntities.WALL_OF_FLESH.get(), level);
        wall.readSpawnData(packet.getAdditionalData());
        return wall;
    }

    /// 初次追踪携带实际布局，晚加入玩家不依赖先前广播，也不独立生成眼嘴。
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeLong(layoutSeed);
        writeAnchors(buffer, eyeAnchors);
        writeAnchors(buffer, mouthAnchors);
    }

    private static void writeAnchors(FriendlyByteBuf buffer, List<Vec3> anchors) {
        buffer.writeVarInt(anchors.size());
        for (Vec3 anchor : anchors)
            buffer.writeDouble(anchor.x).writeDouble(anchor.y).writeDouble(anchor.z);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        if (layoutGenerated) return;
        layoutSeed = buffer.readLong();
        readAnchors(buffer, eyeAnchors);
        readAnchors(buffer, mouthAnchors);
        buildParts();
        setId(getId());
        layoutGenerated = true;
    }

    private static void readAnchors(FriendlyByteBuf buffer, List<Vec3> anchors) {
        anchors.clear();
        int count = buffer.readVarInt();
        for (int index = 0; index < count; index++)
            anchors.add(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
    }

    public long getLayoutSeed() {return layoutSeed;}

    public @Nullable LivingEntity getPartTarget(int index) {
        Entity target = level().getEntity(entityData.get(PART_TARGETS).getInt(Integer.toString(index)));
        return target instanceof LivingEntity living && living.isAlive() ? living : null;
    }

    private void updatePartPositions() {
        for (WallOfFleshPart part : wallParts) {
            Vec3 offset = getLocalOffset(part);
            part.updatePosition(position().add(rotateWallOffset(offset)));
        }
    }

    private void updatePartAssignments() {
        eyeAssignments.clear();
        mouthAssignments.clear();
        List<Player> players = level().getEntitiesOfClass(Player.class, getPursuitBox(), player -> isValidFrontTarget(player) && (!player.isCreative() || isDeveloperTarget(player)) && !player.isSpectator());
        assignNearestParts(players, livingParts(eyes), MAX_ASSIGNED_EYES, eyeAssignments);
        assignNearestParts(players, livingParts(mouths), MAX_ASSIGNED_MOUTHS, mouthAssignments);
        CompoundTag targets = new CompoundTag();
        for (WallOfFleshPart part : wallParts) {
            LivingEntity target = getAssignedTarget(part);
            if (target != null)
                targets.putInt(Integer.toString(part.getPartIndex()), target.getId());
        }
        entityData.set(PART_TARGETS, targets);
    }

    private static <T extends WallOfFleshPart> List<T> livingParts(List<T> parts) {
        return parts.stream().filter(part -> part != null && part.isAlive()).toList();
    }

    private static <T extends WallOfFleshPart> void assignNearestParts(List<Player> players, List<T> parts, int maximumParts, Map<T, Player> assignments) {
        if (players.isEmpty() || parts.isEmpty()) {
            return;
        }
        List<T> selected = new ArrayList<>(parts);
        selected.sort(Comparator.comparingDouble(part -> players.stream().mapToDouble(part::distanceToSqr).min().orElse(Double.MAX_VALUE)));
        if (selected.size() > maximumParts) {
            selected = selected.subList(0, maximumParts);
        }
        for (T part : selected) {
            players.stream().min(Comparator.comparingDouble(part::distanceToSqr)).ifPresent(player -> assignments.put(part, player));
        }
    }

    @Nullable
    LivingEntity getAssignedTarget(WallOfFleshPart part) {
        if (part instanceof WallOfFleshEye eye) {
            return eyeAssignments.get(eye);
        }
        if (part instanceof WallOfFleshMouth mouth) {
            return mouthAssignments.get(mouth);
        }
        return null;
    }

    /// 每个采样区固定一个眼睛或嘴，不跳过区域，也不在生成后补点。
    /// 采样区间距由 PART_LAYOUT_SPACING 控制，位置在中心附近 ±25% 范围内随机偏移。
    private void generateWallLayout(RandomSource layoutRandom) {
        eyeAnchors.clear();
        mouthAnchors.clear();
        hungryAnchors.clear();
        int columns = Mth.ceil(PURSUIT_WIDTH / PART_LAYOUT_SPACING);
        int rows = Mth.ceil(PURSUIT_HEIGHT / PART_LAYOUT_SPACING);
        double cellWidth = PURSUIT_WIDTH / columns;
        double cellHeight = PURSUIT_HEIGHT / rows;
        for (int y = 0; y < rows; y++) {
            boolean eye = false;
            for (int x = 0; x < columns; x++) {
                Vec3 anchor = new Vec3(
                        (x + 0.5 + (layoutRandom.nextDouble() - 0.5) * 0.5) * cellWidth - PURSUIT_WIDTH * 0.5,
                        (y + 0.5 + (layoutRandom.nextDouble() - 0.5) * 0.5) * cellHeight - PURSUIT_HEIGHT * 0.5, 0);
                /// 每对位置随机决定眼嘴顺序，避免局部只出现一种部件。
                if ((x & 1) == 0) eye = layoutRandom.nextBoolean();
                if (eye) eyeAnchors.add(anchor);
                else mouthAnchors.add(anchor);
                eye = !eye;

                /// 饿鬼独立于眼嘴覆盖，每隔两行、两列放置一个，不替代墙面部件。
                if ((x & 1) == 0 && (y & 1) == 0) {
                    hungryAnchors.add(anchor.add(0, cellHeight * 0.25, 0));
                }
            }
        }
    }


    /// FTW 以独立随机节拍补充火焰小鬼；落点只从已加载且可站立的位置中选择。
    private void tickFireImps() {
        if (!isFtw() || getTarget() == null || random.nextInt(FTW_IMP_SPAWN_CHANCE) != 0 || !(level() instanceof ServerLevel serverLevel))
            return;
        if (level().getEntitiesOfClass(FireImp.class, getBoundingBox().inflate(FTW_IMP_SEARCH_RANGE), LivingEntity::isAlive).size() >= FTW_IMP_LIMIT)
            return;
        for (int attempt = 0; attempt < FTW_IMP_SPAWN_ATTEMPTS; attempt++) {
            BlockPos pos = getTarget().blockPosition().offset(random.nextInt(FTW_IMP_HORIZONTAL_SPAWN_WIDTH) - FTW_IMP_HORIZONTAL_SPAWN_OFFSET, random.nextInt(FTW_IMP_VERTICAL_SPAWN_HEIGHT) - FTW_IMP_VERTICAL_SPAWN_OFFSET, random.nextInt(FTW_IMP_HORIZONTAL_SPAWN_WIDTH) - FTW_IMP_HORIZONTAL_SPAWN_OFFSET);
            if (!level().hasChunkAt(pos) || !level().hasChunkAt(pos.below())
                    || !level().getBlockState(pos.below()).isFaceSturdy(level(), pos.below(), Direction.UP))
                continue;
            FireImp imp = MonsterEntities.FIRE_IMP.get().create(serverLevel);
            if (imp == null) return;
            imp.setPos(Vec3.atBottomCenterOf(pos));
            if (!level().noCollision(imp, imp.getBoundingBox())) continue;
            imp.setWallOwner(this);
            if (!serverLevel.addFreshEntity(imp)) imp.discard();
            return;
        }
    }

    private void updateHungrySlots() {
        if (!hungryInitialized) {
            hungryInitialized = true;
            for (Vec3 anchor : hungryAnchors) {
                spawnHungry(anchor, false);
            }
            return;
        }
        if (--hungryTimer > 0) {
            return;
        }
        if (!isExpert()) return;
        hungryTimer = HUNGRY_RESPAWN_INTERVAL;
        for (Vec3 anchor : hungryAnchors) {
            if (!hasLivingHungryAt(anchor) && random.nextFloat() < HUNGRY_RESPAWN_CHANCE) {
                spawnHungry(anchor, true);
            }
        }
    }

    private boolean hasLivingHungryAt(Vec3 anchor) {
        for (Entity entity : getSubEntities()) {
            if (entity instanceof TheHungry hungry
                    && hungry.isAlive()
                    && !hungry.isFree()
                    && hungry.isOwnedBy(this)
                    && hungry.getLeashPos()
                    .distanceToSqr(rotateWallOffset(anchor).scale(getScale())) < 0.01) {
                return true;
            }
        }
        return false;
    }

    private boolean spawnHungry(Vec3 localAnchor, boolean respawn) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        TheHungry hungry = MonsterEntities.THE_HUNGRY.get().create(serverLevel);
        if (hungry == null) {
            return false;
        }
        Vec3 rotatedAnchor = rotateWallOffset(localAnchor).scale(getScale());
        hungry.setPos(position().add(rotatedAnchor));
        hungry.setMaster(this, rotatedAnchor);
        hungry.setSuppressLoot(respawn);
        if (getTarget() != null) {
            hungry.setTarget(getTarget());
        }
        return serverLevel.addFreshEntity(hungry);
    }

    private Vec3 rotateWallOffset(Vec3 offset) {
        Vec3 lateral = new Vec3(-getForwardVector().z, 0.0, getForwardVector().x);
        return lateral.scale(offset.x).add(0.0, offset.y, 0.0).add(getForwardVector().scale(offset.z));
    }

    /// 把部件的世界坐标转换为墙面局部坐标，供客户端按同一布局绘制模型。
    public Vec3 getLocalOffset(Entity part) {
        if (part instanceof WallOfFleshPart wallPart) {
            int index = wallPart.getPartIndex();
            return (index < eyeAnchors.size() ? eyeAnchors.get(index) : mouthAnchors.get(index - eyeAnchors.size())).scale(getScale());
        }
        Vec3 delta = part.position().subtract(position());
        Vec3 forward = getForwardVector();
        Vec3 lateral = new Vec3(-forward.z, 0.0, forward.x);
        return new Vec3(delta.dot(lateral), delta.y, delta.dot(forward));
    }

    private void checkFinishLine() {
        Vec3 travelled = position().subtract(initialPosition);
        if (travelled.dot(getForwardVector()) < FINISH_LINE_DISTANCE && level().getWorldBorder().isWithinBounds(blockPosition())) {
            return;
        }
        for (Player player : level().players()) {
            if (HorrifiedEffect.isBoundTo(player, this)) {
                player.kill();
            }
        }
        discard();
    }

    /// 本体只是整场战斗的管理原点，接触判定由实际可见的眼睛和嘴承担。
    @Override
    protected boolean hasEntityContactAttack() {
        return false;
    }

    /// 所有眼睛和嘴共享受害者冷却，不改动玩家自身的受伤无敌帧。
    public boolean hurtOnContact(LivingEntity target) {
        long now = level().getGameTime();
        contactHits.values().removeIf(expiry -> expiry <= now);
        if (contactHits.containsKey(target.getUUID())) return false;
        if (!doContactHurtTarget(target)) return false;
        contactHits.put(target.getUUID(), now + 10);
        return true;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target == this || target instanceof TheHungry || target instanceof SimpleWormMonster && target.getType() == MonsterEntities.LEECH.get()) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypeTags.IS_DROWNING)
                || super.isInvulnerableTo(source);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    /// 墙体背景不直接承受点击和弹幕命中，伤害由眼睛与嘴部转发。
    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(PHASE_TWO_TAG, isPhaseTwo());
        tag.putDouble(INITIAL_X_TAG, initialPosition.x);
        tag.putDouble(INITIAL_Y_TAG, initialPosition.y);
        tag.putDouble(INITIAL_Z_TAG, initialPosition.z);
        tag.putLong(LAYOUT_SEED_TAG, layoutSeed);
        tag.putInt(HUNGRY_TIMER_TAG, hungryTimer);
        tag.putBoolean(HUNGRY_INITIALIZED_TAG, hungryInitialized);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(DATA_PHASE_TWO, tag.getBoolean(PHASE_TWO_TAG));
        updatePhase();
        initialPosition = new Vec3(tag.getDouble(INITIAL_X_TAG), tag.getDouble(INITIAL_Y_TAG), tag.getDouble(INITIAL_Z_TAG));
        layoutSeed = tag.getLong(LAYOUT_SEED_TAG);
        hungryTimer = tag.getInt(HUNGRY_TIMER_TAG);
        hungryInitialized = tag.getBoolean(HUNGRY_INITIALIZED_TAG);
        layoutGenerated = false;
        eyeAnchors.clear();
        mouthAnchors.clear();
        hungryAnchors.clear();
        eyes.clear();
        mouths.clear();
        eyeAssignments.clear();
        mouthAssignments.clear();
        ensureWallLayout();
    }

    /// 嘴部沿用本体属性，眼部使用注册表中的独立防御。
    public enum BodySection {EYE}

    @Override
    public double getAttributeValue(Attribute attribute) {
        double value = super.getAttributeValue(attribute);
        if (!(struckPart instanceof WallOfFleshEye)) return value;
        double base = getAttributeBaseValue(attribute);
        double part = stateAttributeValue(BodySection.EYE, attribute, base);
        if (attribute == Attributes.ARMOR) return Math.max(0, value + part - base);
        if (attribute == Attributes.ARMOR_TOUGHNESS) {
            var rule = CreatureDifficultyScaling.difficulty(this);
            return Math.max(0, Math.min(8, value + rule.toughness(part) - rule.toughness(base)));
        }
        return value;
    }

    public enum CombatState {WOUNDED}

    /// Wall-only, non-persistent region-ticket ownership. Vanilla/admin forced chunks are never
    /// read or written, and overlapping walls retain independent UUID-keyed leases.
    private static final class WallChunkRetention {
        /// 肉墙区块票据覆盖的区块距离。
        private static final int TICKET_DISTANCE = 2;
        private static final TicketType<UUID> TYPE = TicketType.create("confluence:wall_of_flesh", UUID::compareTo, CHUNK_RETENTION_TICKS);
        private static final Map<ServerLevel, Map<UUID, OwnerLease>> LEVELS = new WeakHashMap<>();

        private WallChunkRetention() {}

        private static void refresh(ServerLevel level, UUID owner, AABB bounds, long now) {
            expire(level, now);
            Map<UUID, OwnerLease> owners = LEVELS.computeIfAbsent(level, ignored -> new HashMap<>());
            OwnerLease lease = owners.computeIfAbsent(owner, ignored -> new OwnerLease());
            int minX = Math.floorDiv((int) Math.floor(bounds.minX), 16);
            int maxX = Math.floorDiv((int) Math.floor(bounds.maxX - 1.0E-7D), 16);
            int minZ = Math.floorDiv((int) Math.floor(bounds.minZ), 16);
            int maxZ = Math.floorDiv((int) Math.floor(bounds.maxZ - 1.0E-7D), 16);
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    ChunkPos chunk = new ChunkPos(x, z);
                    level.getChunkSource().addRegionTicket(TYPE, chunk, TICKET_DISTANCE, owner, true);
                    lease.expirations.put(chunk, now + CHUNK_RETENTION_TICKS);
                }
            }
        }

        private static void expire(ServerLevel level, long now) {
            Map<UUID, OwnerLease> owners = LEVELS.get(level);
            if (owners == null) return;
            var ownerIterator = owners.entrySet().iterator();
            while (ownerIterator.hasNext()) {
                var ownerEntry = ownerIterator.next();
                UUID owner = ownerEntry.getKey();
                var chunkIterator = ownerEntry.getValue().expirations.entrySet().iterator();
                while (chunkIterator.hasNext()) {
                    var chunkEntry = chunkIterator.next();
                    if (chunkEntry.getValue() < now) {
                        level.getChunkSource().removeRegionTicket(TYPE, chunkEntry.getKey(), TICKET_DISTANCE, owner, true);
                        chunkIterator.remove();
                    }
                }
                if (ownerEntry.getValue().expirations.isEmpty()) {
                    ownerIterator.remove();
                }
            }
            if (owners.isEmpty()) LEVELS.remove(level);
        }

        private static void release(ServerLevel level, UUID owner) {
            Map<UUID, OwnerLease> owners = LEVELS.get(level);
            if (owners == null) return;
            OwnerLease lease = owners.remove(owner);
            if (lease != null) {
                for (ChunkPos chunk : lease.expirations.keySet()) {
                    level.getChunkSource().removeRegionTicket(TYPE, chunk, TICKET_DISTANCE, owner, true);
                }
            }
            if (owners.isEmpty()) LEVELS.remove(level);
        }

        private static final class OwnerLease {
            final Map<ChunkPos, Long> expirations = new HashMap<>();
        }
    }
}
