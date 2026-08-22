package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.effect.harmful.HorrifiedEffect;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.SimpleWormMonster;
import org.confluence.mod.common.entity.monster.TheHungry;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 血肉墙的服务端战斗主体。
///
/// <p>本体负责整面墙的推进、阶段和参战者管理；眼睛与嘴是可命中的临时部件，
/// 各自维护射击或吐出水蛭的节奏。墙面布局由一个持久化种子生成，因此仍保留
/// 1.21 的随机墙面外观，同时保证区块重载后不会换成另一套眼、嘴和饿鬼位置。</p>
public class WallOfFlesh extends BaseBoss {
    private static final EntityDataAccessor<Boolean> DATA_PHASE_TWO = SynchedEntityData.defineId(WallOfFlesh.class, EntityDataSerializers.BOOLEAN);

    private static final String PHASE_TWO_TAG = "PhaseTwo";
    private static final String INITIAL_X_TAG = "InitialX";
    private static final String INITIAL_Y_TAG = "InitialY";
    private static final String INITIAL_Z_TAG = "InitialZ";
    private static final String LAYOUT_SEED_TAG = "LayoutSeed";
    private static final String HUNGRY_TIMER_TAG = "HungryTimer";
    private static final String HUNGRY_INITIALIZED_TAG = "HungryInitialized";

    private static final double BASE_SPEED = 0.125;
    private static final double FINISH_LINE_DISTANCE = 2000.0;
    private static final double TARGET_RANGE = 120.0;
    private static final int GRID_SIZE_X = 40;
    private static final int GRID_SIZE_Y = 30;
    private static final double GRID_SPACING = 15.0;
    private static final double PURSUIT_WIDTH = GRID_SIZE_X * GRID_SPACING;
    private static final double PURSUIT_HEIGHT = GRID_SIZE_Y * GRID_SPACING;
    private static final double PURSUIT_DEPTH = 150.0;
    private static final int CHUNK_REFRESH_INTERVAL = 5;
    private static final int CHUNK_RETENTION_TICKS = 30 * 20;

    private static final int HUNGRY_RESPAWN_INTERVAL = 1200;
    private static final int MAX_ASSIGNED_MOUTHS = 2;
    private static final int MAX_ASSIGNED_EYES = 4;

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
    private final List<WallOfFleshEye> eyes = new ArrayList<>();
    private final List<WallOfFleshMouth> mouths = new ArrayList<>();
    private final Map<WallOfFleshEye, Player> eyeAssignments = new HashMap<>();
    private final Map<WallOfFleshMouth, Player> mouthAssignments = new HashMap<>();

    public WallOfFlesh(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
        xpReward = 3000;
    }

    /// 血肉墙的高度由竞技场和墙体布局决定，不能被重力逐 tick 下拉。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 3096.0)
                .add(Attributes.ATTACK_DAMAGE, 39.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.FOLLOW_RANGE, TARGET_RANGE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PHASE_TWO, false);
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
        setYRot(rotation);
        setYHeadRot(rotation);
        yBodyRot = rotation;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) {
            if (initialPosition == Vec3.ZERO) {
                /// Forge 1.20 在本回调执行时已把实体加入当前区段。此时跨区块移动会使
                /// 实体脱离 ticking 列表，因此初始后移必须等到首个正式 tick 再执行。
                needsInitialPlacement = true;
            } else {
                ensureWallLayout();
            }
        }
    }

    /// 在实体已经进入服务端 ticking 列表后完成与 1.21 相同的高度修正和后移。
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
                movingAlongX ? thickness : PURSUIT_WIDTH,
                PURSUIT_HEIGHT,
                movingAlongX ? PURSUIT_WIDTH : thickness);
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
        if (isRemoved()) {
            return;
        }

        lockCardinalRotation();
        if (level().isClientSide) {
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
        updateMovement();
        updateHungrySlots();
        checkFinishLine();
    }

    private void lockCardinalRotation() {
        float rotation = getDirection().toYRot();
        setYRot(rotation);
        setYHeadRot(rotation);
        yBodyRot = rotation;
    }

    private void updatePhase() {
        if (!isPhaseTwo() && getHealth() < getMaxHealth() * 0.5F) {
            entityData.set(DATA_PHASE_TWO, true);
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * 1.45);
        }
    }

    private void acquireFrontTarget() {
        LivingEntity current = getTarget();
        if (isValidFrontTarget(current)) {
            return;
        }
        setTarget(level().getEntitiesOfClass(Player.class, getPursuitBox(), this::isValidFrontTarget).stream().min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null));
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

    /// Wall-only, non-persistent region-ticket ownership. Vanilla/admin forced chunks are never
    /// read or written, and overlapping walls retain independent UUID-keyed leases.
    private static final class WallChunkRetention {
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
        if (tickCount % 20 != 0) {
            return;
        }
        AABB pursuitBox = getPursuitBox();
        for (Player player : level().players()) {
            if (player.isCreative() || player.isSpectator()) {
                continue;
            }
            if (player.getBoundingBox().intersects(pursuitBox)) {
                HorrifiedEffect.bind(player, this);
                registerCombatParticipant(player);
            }
            /// 离开区域后仍要续期，舌头才能持续把该参战者拉回；只给区域内玩家
            /// 续期会让其等待五秒后直接摆脱整场遭遇。
            if (HorrifiedEffect.isBoundTo(player, this)) {
                player.addEffect(new MobEffectInstance(ModEffects.HORRIFIED.get(), 100), this);
            }
        }
    }

    private void updateMovement() {
        /// 1.21 通过移动速度属性逐刻增加墙体推进量，半血时只把该属性提高 45%。
        /// 不能改成按失血比例直接写入 0.42 的固定速度，否则整场追逐节奏都会改变。
        addDeltaMovement(getForwardVector().scale(getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.125));
    }

    /// 根据持久化种子建立墙面布局，并补回不参与存档的眼睛与嘴部实体。
    private void ensureWallLayout() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!layoutGenerated) {
            if (layoutSeed == 0L) {
                do {
                    layoutSeed = random.nextLong();
                } while (layoutSeed == 0L);
            }
            generateWallLayout(RandomSource.create(layoutSeed));
            layoutGenerated = true;
        }

        ensurePartListSize(eyes, eyeAnchors.size());
        ensurePartListSize(mouths, mouthAnchors.size());
        for (int index = 0; index < eyeAnchors.size(); index++) {
            WallOfFleshEye eye = eyes.get(index);
            if (eye == null || !eye.isAlive()) {
                eyes.set(index, spawnEye(serverLevel, eyeAnchors.get(index)));
            }
        }
        for (int index = 0; index < mouthAnchors.size(); index++) {
            WallOfFleshMouth mouth = mouths.get(index);
            if (mouth == null || !mouth.isAlive()) {
                mouths.set(index, spawnMouth(serverLevel, mouthAnchors.get(index)));
            }
        }
    }

    private static <T> void ensurePartListSize(List<T> parts, int expectedSize) {
        while (parts.size() < expectedSize) {
            parts.add(null);
        }
    }

    private @Nullable WallOfFleshEye spawnEye(ServerLevel level, Vec3 anchor) {
        WallOfFleshEye eye = BossEntities.WALL_OF_FLESH_EYE.get().create(level);
        if (eye == null) {
            return null;
        }
        eye.setPos(position().add(rotateWallOffset(anchor)));
        eye.setMaster(this);
        if (!level.addFreshEntity(eye)) {
            eye.discard();
            return null;
        }
        return eye;
    }

    private @Nullable WallOfFleshMouth spawnMouth(ServerLevel level, Vec3 anchor) {
        WallOfFleshMouth mouth = BossEntities.WALL_OF_FLESH_MOUTH.get().create(level);
        if (mouth == null) {
            return null;
        }
        mouth.setPos(position().add(rotateWallOffset(anchor)));
        mouth.setMaster(this);
        if (!level.addFreshEntity(mouth)) {
            mouth.discard();
            return null;
        }
        return mouth;
    }

    private void updatePartPositions() {
        updatePartPositions(eyes, eyeAnchors);
        updatePartPositions(mouths, mouthAnchors);
    }

    private <T extends WallOfFleshPart> void updatePartPositions(List<T> parts, List<Vec3> anchors) {
        int count = Math.min(parts.size(), anchors.size());
        for (int index = 0; index < count; index++) {
            T part = parts.get(index);
            if (part != null && part.isAlive()) {
                part.setPos(position().add(rotateWallOffset(anchors.get(index))));
            }
        }
    }

    private void updatePartAssignments() {
        eyeAssignments.clear();
        mouthAssignments.clear();
        List<Player> players = level().getEntitiesOfClass(Player.class, getPursuitBox(), player -> isValidFrontTarget(player) && !player.isCreative() && !player.isSpectator());
        assignNearestParts(players, livingParts(eyes), MAX_ASSIGNED_EYES, eyeAssignments);
        assignNearestParts(players, livingParts(mouths), MAX_ASSIGNED_MOUTHS, mouthAssignments);
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

    private void generateWallLayout(RandomSource layoutRandom) {
        eyeAnchors.clear();
        mouthAnchors.clear();
        hungryAnchors.clear();
        generateLayoutRegion(layoutRandom, 0, 0, GRID_SIZE_X, GRID_SIZE_Y, 0, 6, 0.85);
        generateMouthsBetweenEyes(layoutRandom);

        // 极端随机结果仍必须提供三类核心战斗部件。
        if (eyeAnchors.isEmpty()) {
            eyeAnchors.add(new Vec3(-GRID_SPACING, 0.0, 0.0));
            eyeAnchors.add(new Vec3(GRID_SPACING, 0.0, 0.0));
        }
        if (mouthAnchors.isEmpty()) {
            mouthAnchors.add(Vec3.ZERO);
        }
        if (hungryAnchors.isEmpty()) {
            hungryAnchors.add(new Vec3(0.0, GRID_SPACING, 0.0));
        }
    }

    private void generateLayoutRegion(RandomSource layoutRandom, int x, int y, int width, int height, int depth, int maximumDepth, double subdivisionChance) {
        if (width <= 0 || height <= 0) {
            return;
        }

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        double offsetScale = 1.0
                - depth / (double) maximumDepth * 0.5;
        double maximumOffset = GRID_SPACING * 0.8 * offsetScale;
        long seedOffset = (long) x * 31L + (long) y * 17L
                + depth * 7L;
        double randomX = (layoutRandom.nextDouble() + seedOffset % 100L / 100.0) % 1.0;
        double randomY = (layoutRandom.nextDouble() + seedOffset % 83L / 100.0) % 1.0;
        Vec3 position = new Vec3((centerX - GRID_SIZE_X * 0.5) * GRID_SPACING + (randomX - 0.5) * maximumOffset, (centerY - GRID_SIZE_Y * 0.5) * GRID_SPACING + (randomY - 0.5) * maximumOffset, 0.0);

        boolean subdivide = depth < maximumDepth
                && width > 1
                && height > 1
                && layoutRandom.nextDouble() < subdivisionChance;
        if (depth < 3 && layoutRandom.nextDouble() < 1.0 - depth * 0.25) {
            subdivide = true;
        }
        if (subdivide) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            double nextChance = subdivisionChance
                    * (1.0 - depth / (double) maximumDepth * 0.02);
            generateLayoutRegion(layoutRandom, x, y, halfWidth, halfHeight, depth + 1, maximumDepth, nextChance);
            generateLayoutRegion(layoutRandom, x + halfWidth, y, width - halfWidth, halfHeight, depth + 1, maximumDepth, nextChance);
            generateLayoutRegion(layoutRandom, x, y + halfHeight, halfWidth, height - halfHeight, depth + 1, maximumDepth, nextChance);
            generateLayoutRegion(layoutRandom, x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight, depth + 1, maximumDepth, nextChance);
            return;
        }

        double conflictDistance = GRID_SPACING * 0.6
                * (1.0 - depth / (double) maximumDepth * 0.4);
        if (hasLayoutConflict(position, conflictDistance)) {
            return;
        }
        double depthFactor = 0.8
                + depth / (double) maximumDepth * 0.4;
        double roll = layoutRandom.nextDouble();
        double eyeChance = Math.min(0.45 * depthFactor, 1.0);
        double mouthChance = Math.min((0.45 + 0.4) * depthFactor, 1.0);
        double totalChance = Math.min((0.45 + 0.4 + 0.3) * depthFactor, 1.0);
        if (roll < eyeChance) {
            eyeAnchors.add(position);
        } else if (roll < mouthChance) {
            mouthAnchors.add(position);
        } else if (roll < totalChance) {
            hungryAnchors.add(position);
        }
    }

    private boolean hasLayoutConflict(Vec3 position, double distance) {
        double distanceSquared = distance * distance;
        return eyeAnchors.stream().anyMatch(anchor -> anchor.distanceToSqr(position) < distanceSquared)
                || mouthAnchors.stream().anyMatch(anchor -> anchor.distanceToSqr(position) < distanceSquared)
                || hungryAnchors.stream().anyMatch(anchor -> anchor.distanceToSqr(position) < distanceSquared);
    }

    /// 在同列眼睛的较大空档中补嘴，保持 1.21 的墙面攻击分布。
    private void generateMouthsBetweenEyes(RandomSource layoutRandom) {
        Map<Integer, List<Vec3>> eyesByColumn = new HashMap<>();
        for (Vec3 eye : List.copyOf(eyeAnchors)) {
            int column = (int) Math.round(eye.x / GRID_SPACING + GRID_SIZE_X * 0.5);
            if (column >= 0 && column < GRID_SIZE_X) {
                eyesByColumn.computeIfAbsent(column, ignored -> new ArrayList<>()).add(eye);
            }
        }
        for (Map.Entry<Integer, List<Vec3>> entry : eyesByColumn.entrySet()) {
            List<Vec3> columnEyes = entry.getValue();
            columnEyes.sort(Comparator.comparingDouble(Vec3::y));
            for (int index = 0; index + 1 < columnEyes.size(); index++) {
                Vec3 lower = columnEyes.get(index);
                Vec3 upper = columnEyes.get(index + 1);
                if (Math.abs(upper.y - lower.y) < GRID_SPACING * 1.5) {
                    continue;
                }
                Vec3 mouth = new Vec3((entry.getKey() - GRID_SIZE_X * 0.5) * GRID_SPACING, (lower.y + upper.y) * 0.5, lower.z);
                double conflictDistance = GRID_SPACING * 0.6;
                if (!hasLayoutConflict(mouth, conflictDistance) && layoutRandom.nextDouble() < 0.8) {
                    mouthAnchors.add(mouth);
                }
            }
        }
    }

    float getLaserDamage() {
        if (isMaster()) {
            return 15.0F;
        }
        if (isExpert()) {
            return 12.0F;
        }
        return level().getDifficulty().getId() <= 1
                ? 8.0F : 10.0F;
    }

    private void updateHungrySlots() {
        if (!hungryInitialized) {
            hungryInitialized = true;
            for (Vec3 anchor : hungryAnchors) {
                spawnHungry(anchor);
            }
            return;
        }
        if (--hungryTimer > 0) {
            return;
        }
        hungryTimer = HUNGRY_RESPAWN_INTERVAL;
        for (Vec3 anchor : hungryAnchors) {
            if (!hasLivingHungryAt(anchor) && random.nextFloat() < 0.4F) {
                spawnHungry(anchor);
            }
        }
    }

    private boolean hasLivingHungryAt(Vec3 anchor) {
        for (Entity entity : getSubEntities()) {
            if (entity instanceof TheHungry hungry
                    && hungry.isAlive()
                    && hungry.isOwnedBy(this)
                    && hungry.getLeashPos()
                    .distanceToSqr(rotateWallOffset(anchor)) < 0.01) {
                return true;
            }
        }
        return false;
    }

    private boolean spawnHungry(Vec3 localAnchor) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        TheHungry hungry = MonsterEntities.THE_HUNGRY.get().create(serverLevel);
        if (hungry == null) {
            return false;
        }
        Vec3 rotatedAnchor = rotateWallOffset(localAnchor);
        hungry.setPos(position().add(rotatedAnchor));
        hungry.setMaster(this, rotatedAnchor);
        if (getTarget() != null) {
            hungry.setTarget(getTarget());
        }
        return serverLevel.addFreshEntity(hungry);
    }

    private Vec3 rotateWallOffset(Vec3 offset) {
        Vec3 lateral = new Vec3(-getForwardVector().z, 0.0, getForwardVector().x);
        return lateral.scale(offset.x).add(0.0, offset.y, 0.0).add(getForwardVector().scale(offset.z));
    }

    /// 把部件的世界坐标还原为墙面局部坐标，供客户端按同一布局绘制模型。
    public Vec3 getLocalOffset(Entity part) {
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

    @Override
    public boolean isPushable() {
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
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(isPhaseTwo() ? BASE_SPEED * 1.45 : BASE_SPEED);
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
    }
}
