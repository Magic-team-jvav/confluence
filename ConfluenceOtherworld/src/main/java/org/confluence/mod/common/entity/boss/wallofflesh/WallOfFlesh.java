package org.confluence.mod.common.entity.boss.wallofflesh;

import it.unimi.dsi.fastutil.objects.ObjectIntMutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionDefaults;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.LibUtils;
import org.confluence.terraentity.api.entity.IExtendedTracking;
import org.confluence.terraentity.effect.harmful.HorrifiedEffect;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.entity.monster.TheHungry;
import org.confluence.terraentity.entity.monster.prefab.AbstractPrefab;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.integration.ModChecker;
import org.confluence.terraentity.network.s2c.SyncWallOfFleshPositionsPacket;
import org.confluence.terraentity.utils.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static org.confluence.terraentity.init.TEEntityDataSerializers.TUPLET_VEC3_INT_LIST_SERIALIZER;
import static org.confluence.terraentity.init.TEEntityDataSerializers.TUPLE_INT_VEC3_LIST_SERIALIZER;

public class WallOfFlesh extends AbstractTerraBossBase implements Boss, IExtendedTracking {
    public boolean genSegments = true;
    boolean shouldMove = true;
    final float baseMoveSpeed = 0.125f;
    Vec3 InitPos = Vec3.ZERO;

    public AABB insideCollisionBox;
    public AABB outsideCollisionBox;

    private final int gridSizeX = 40;
    private final int gridSizeY = 30;
    public float gridSpacing = 15.0f;

    private static final int summonCDAll = 1200; //饿鬼召唤cd
    private int summonCD = summonCDAll;

    private float targetHeight;
    private static final double FINISH_LINE_DISTANCE = 2000;
    private static final int MAX_ASSIGNED_MOUTHS = 2;
    private static final int MAX_ASSIGNED_EYES = 4;

    public List<WallOfFleshPart> subEntities = new CopyOnWriteArrayList<>();
    List<ObjectIntPair<Vec3>> eyePositions = new ArrayList<>();
    List<ObjectIntPair<Vec3>> mouthPositions = new ArrayList<>();
    protected List<TheHungry> theHungries = new ArrayList<>();

    public final List<Tuple<Integer, Vec3>> localOffsets = new CopyOnWriteArrayList<>(); // 存储子实体相对坐标
    public final List<Tuple<Vec3, Integer>> theHungryList = new CopyOnWriteArrayList<>();
    private final Map<WallOfFleshMouth, Player> mouseToPlayerMap = new HashMap<>();
    private final Map<WallOfFleshEye, Player> eyeToPlayerMap = new HashMap<>();

    private static final EntityDataAccessor<List<Tuple<Integer, Vec3>>> DATA_LOCAL_OFFSETS =
            SynchedEntityData.defineId(WallOfFlesh.class, TUPLE_INT_VEC3_LIST_SERIALIZER.get());
    private static final EntityDataAccessor<List<Tuple<Vec3, Integer>>> DATA_HUNGRY_OFFSETS =
            SynchedEntityData.defineId(WallOfFlesh.class, TUPLET_VEC3_INT_LIST_SERIALIZER.get());


    public WallOfFlesh(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseMoveSpeed);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(999);
        genGridWall();
        explosionResistance = switch (this.level().getDifficulty()) {
            case EASY -> 0.25f;
            case NORMAL -> 0.15f;
            case HARD -> 0.05f;
            default -> 1.0f;
        };
    }

    public WallOfFlesh(Level level, Direction direction) {
        this(TEBossEntities.WALL_OF_FLESH.get(), level);
        this.setForward(direction);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_LOCAL_OFFSETS, new CopyOnWriteArrayList<>());
        builder.define(DATA_HUNGRY_OFFSETS, new CopyOnWriteArrayList<>());
    }

    public List<Tuple<Integer, Vec3>> getLocalOffsets() {
        return this.entityData.get(DATA_LOCAL_OFFSETS);
    }

    public void setLocalOffsets(int entityId, Vec3 offset) {
        this.localOffsets.add(new Tuple<>(entityId, offset));
        this.entityData.set(DATA_LOCAL_OFFSETS, this.localOffsets);
    }

    public List<Tuple<Vec3, Integer>> getHungryOffsets() {
        return this.entityData.get(DATA_HUNGRY_OFFSETS);
    }

    public void setHungryOffsets(Vec3 offset, int entityId) {
        if (!this.level().isClientSide) {
            boolean exists = false;
            for (Tuple<Vec3, Integer> tuple : this.theHungryList) {
                if (tuple.getB().equals(entityId)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                this.theHungryList.add(new Tuple<>(offset, entityId));
                this.entityData.set(DATA_HUNGRY_OFFSETS, this.theHungryList);
            }
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void registerGoals() {}

    public void addChild(Entity child, Vec3 localOffset) {
        if (child instanceof WallOfFleshPart part) {
            subEntities.add(part);
            this.setLocalOffsets(subEntities.size() - 1, localOffset);
            // todo 出事了再考虑这个
//            if (level() instanceof ServerLevel level) {
//                level.dragonParts.put(part.getId(), part);
//            }
        } else if (child instanceof TheHungry hungry && !this.level().isClientSide) {
            this.setHungryOffsets(localOffset, hungry.getId());
            Vec3 rotatedOffset = rotateLocalOffset(localOffset);
            hungry.setInitPos(this.position().add(rotatedOffset).toVector3f());
            theHungries.add(hungry);
        }
        // 应用旋转到子实体位置
        Vec3 rotatedOffset = rotateLocalOffset(localOffset);
        child.setPos(this.position().add(rotatedOffset));
    }

    private void genGridWall() {
        List<Vec3> hungryPositions = new ArrayList<>();
        if (!level().isClientSide) {// 只在服务端生成眼睛和嘴巴，并通过数据同步到客户端
            float zOffset = 0.0F;

            final int MAX_DEPTH = 6;
            final double EYE_CHANCE = 0.45;
            final double MOUTH_CHANCE = 0.4;
            final double HUNGRY_CHANCE = 0.3;
            final double SUBDIVISION_CHANCE = 0.85; // 细分概率

            // 使用四叉树生成眼睛、嘴巴和饿鬼位置
            generateAllEntitiesQuadTree(0, 0, gridSizeX, gridSizeY, 0, MAX_DEPTH,
                    EYE_CHANCE, MOUTH_CHANCE, HUNGRY_CHANCE, SUBDIVISION_CHANCE,
                    eyePositions, mouthPositions, hungryPositions, zOffset);

            // 额外在眼睛之间生成嘴巴
            generateMouthsBetweenEyes(eyePositions, mouthPositions, hungryPositions);

            int index = 0;
            // 生成眼睛
            for (int i = 0; i < eyePositions.size(); i++) {
                ObjectIntPair<Vec3> pair = eyePositions.get(i);
                Vec3 pos = pair.key();
                WallOfFleshEye eye = new WallOfFleshEye(this, "WallOfFleshEye" + (i + 1), 4.0f, 4.0f);
                addChild(eye, pos);
                pair.right(index++);
            }

            // 生成嘴巴
            for (int i = 0; i < mouthPositions.size(); i++) {
                ObjectIntPair<Vec3> pair = mouthPositions.get(i);
                Vec3 pos = pair.key();
                WallOfFleshMouth mouth = new WallOfFleshMouth(this, "WallOfFleshMouth" + i, 3.0f, 4.0f);
                addChild(mouth, pos);
                pair.right(index++);
            }

            // 生成饿鬼（只在服务端）
            for (Vec3 pos : hungryPositions) {
                TheHungry hungry = new TheHungry(TEMonsterEntities.THE_HUNGRY.get(), level(),
                        new AbstractPrefab().getPrefab()) {
                    @Override
                    protected boolean shouldDropLoot() {
                        return false;
                    }
                };

                addChild(hungry, pos);
                hungry.minion_setOwner(this);
            }
        }
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.size() + 1) + 1);
    }

    //四叉树
    private void generateAllEntitiesQuadTree(int x, int y, int width, int height, int depth, int maxDepth,
                                             double eyeChance, double mouthChance, double hungryChance, double subdivisionChance,
                                             List<ObjectIntPair<Vec3>> eyePositions, List<ObjectIntPair<Vec3>> mouthPositions, List<Vec3> hungryPositions, float zOffset) {

        if (width <= 0 || height <= 0) {
            return;
        }

        final double localGridSpacing = gridSpacing;

        final int centerX = x + width / 2;
        final int centerY = y + height / 2;
        final double gridSizeXHalf = (double) gridSizeX / 2.0;
        final double gridSizeYHalf = (double) gridSizeY / 2.0;

        double offsetScale = 1.0 - (depth / (double) maxDepth) * 0.5;
        double maxOffset = localGridSpacing * 0.8 * offsetScale;

        long seedOffset = (long) x * 31 + (long) y * 17 + depth * 7L;
        double randOffsetX = (random.nextDouble() + (seedOffset % 100) / 100.0) % 1.0;
        double randOffsetY = (random.nextDouble() + (seedOffset % 83) / 100.0) % 1.0;
        double offsetX = (randOffsetX - 0.5) * maxOffset;
        double offsetY = (randOffsetY - 0.5) * maxOffset;

        double worldPosX = (centerX - gridSizeXHalf) * localGridSpacing + offsetX;
        double worldPosY = (centerY - gridSizeYHalf) * localGridSpacing + offsetY;
        Vec3 worldPos = new Vec3(worldPosX, worldPosY, zOffset);

        boolean shouldSubdivide = depth < maxDepth && width > 1 && height > 1;
        if (shouldSubdivide) {
            shouldSubdivide = random.nextDouble() < subdivisionChance;
        }
        if (depth < 3) {
            double randSubdivide = random.nextDouble();
            if (randSubdivide < 1.0 - (depth * 0.25)) {
                shouldSubdivide = true;
            }
        }

        if (shouldSubdivide) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;

            double decayFactor = 1 - (depth / (double) maxDepth) * 0.02;
            final double newSubdivisionChance = subdivisionChance * decayFactor;

            generateAllEntitiesQuadTree(x, y, halfWidth, halfHeight, depth + 1, maxDepth,
                    eyeChance, mouthChance, hungryChance, newSubdivisionChance,
                    eyePositions, mouthPositions, hungryPositions, zOffset);

            generateAllEntitiesQuadTree(x + halfWidth, y, width - halfWidth, halfHeight, depth + 1, maxDepth,
                    eyeChance, mouthChance, hungryChance, newSubdivisionChance,
                    eyePositions, mouthPositions, hungryPositions, zOffset);

            generateAllEntitiesQuadTree(x, y + halfHeight, halfWidth, height - halfHeight, depth + 1, maxDepth,
                    eyeChance, mouthChance, hungryChance, newSubdivisionChance,
                    eyePositions, mouthPositions, hungryPositions, zOffset);

            generateAllEntitiesQuadTree(x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight, depth + 1, maxDepth,
                    eyeChance, mouthChance, hungryChance, newSubdivisionChance,
                    eyePositions, mouthPositions, hungryPositions, zOffset);
        } else {
            double rand = random.nextDouble();

            double conflictDistanceScale = 1.0 - (depth / (double) maxDepth) * 0.4;
            double conflictDistance = localGridSpacing * 0.6 * conflictDistanceScale;
            final double conflictDistanceSqr = conflictDistance * conflictDistance;

            boolean hasConflict = false;

            final boolean eyesEmpty = eyePositions.isEmpty();
            final boolean mouthsEmpty = mouthPositions.isEmpty();
            final boolean hungryEmpty = hungryPositions.isEmpty();

            if (!eyesEmpty && !hasConflict) {
                for (ObjectIntPair<Vec3> existingPos : eyePositions) {
                    if (existingPos.key().distanceToSqr(worldPos) < conflictDistanceSqr) {
                        hasConflict = true;
                        break;
                    }
                }
            }

            if (!mouthsEmpty && !hasConflict) {
                for (ObjectIntPair<Vec3> existingPos : mouthPositions) {
                    if (existingPos.key().distanceToSqr(worldPos) < conflictDistanceSqr) {
                        hasConflict = true;
                        break;
                    }
                }
            }

            if (!hungryEmpty && !hasConflict) {
                for (Vec3 existingPos : hungryPositions) {
                    if (existingPos.distanceToSqr(worldPos) < conflictDistanceSqr) {
                        hasConflict = true;
                        break;
                    }
                }
            }

            if (!hasConflict) {
                double depthFactor = 0.8 + (depth / (double) maxDepth) * 0.4; // 深度越大，概率越高（弥补小区域面积小）
                double adjustedEyeChance = eyeChance * depthFactor;
                double adjustedMouthChance = mouthChance * depthFactor;
                double adjustedHungryChance = hungryChance * depthFactor;

                double eyeMouthChance = adjustedEyeChance + adjustedMouthChance;
                double totalChance = eyeMouthChance + adjustedHungryChance;

                double finalEyeChance = Math.min(adjustedEyeChance, 1.0);
                double finalEyeMouthChance = Math.min(eyeMouthChance, 1.0);
                double finalTotalChance = Math.min(totalChance, 1.0);

                if (rand < finalEyeChance) {
                    eyePositions.add(new ObjectIntMutablePair<>(worldPos, -1));
                } else if (rand < finalEyeMouthChance) {
                    mouthPositions.add(new ObjectIntMutablePair<>(worldPos, -1));
                } else if (rand < finalTotalChance) {
                    hungryPositions.add(worldPos);
                }
            }
        }
    }

    /**
     * 在上下相邻且空间足够的眼睛中间生成嘴巴
     */
    private void generateMouthsBetweenEyes(List<ObjectIntPair<Vec3>> eyePositions, List<ObjectIntPair<Vec3>> mouthPositions, List<Vec3> hungryPositions) {
        Map<Integer, List<Vec3>> eyesByGridX = new HashMap<>();

        for (ObjectIntPair<Vec3> eyePos : eyePositions) {
            int gridX = (int) Math.round(eyePos.key().x / gridSpacing + gridSizeX / 2.0);

            if (gridX >= 0 && gridX < gridSizeX) {
                eyesByGridX.computeIfAbsent(gridX, k -> new ArrayList<>()).add(eyePos.key());
            }
        }

        eyesByGridX.forEach((gridX, eyesInColumn) -> {
            eyesInColumn.sort(Comparator.comparingDouble(a -> a.y));

            // 检查连续3个眼睛，将中间的眼睛替换为嘴巴
            for (int i = 0; i < eyesInColumn.size() - 2; i++) {
                Vec3 eye1 = eyesInColumn.get(i);
                Vec3 eye2 = eyesInColumn.get(i + 1);
                Vec3 eye3 = eyesInColumn.get(i + 2);

                double distance1 = Math.abs(eye2.y - eye1.y);
                double distance2 = Math.abs(eye3.y - eye2.y);

                // 将Y轴距离判断范围 扩大到 gridSpacing * 3.75
                if (Math.abs(eye2.x - eye1.x) < gridSpacing * 0.1 &&
                        Math.abs(eye2.z - eye1.z) < gridSpacing * 0.1 &&
                        distance1 <= gridSpacing * 3.75 &&
                        distance2 <= gridSpacing * 3.75) {

                    eyePositions.removeIf(existingEye ->
                            existingEye.key().distanceToSqr(eye2) < gridSpacing * gridSpacing * 0.1);

                    boolean hasExistingMouth = false;
                    for (ObjectIntPair<Vec3> existingMouth : mouthPositions) {
                        if (existingMouth.key().distanceToSqr(eye2) < gridSpacing * gridSpacing * 0.5) {
                            hasExistingMouth = true;
                            break;
                        }
                    }

                    if (!hasExistingMouth) {
                        mouthPositions.add(new ObjectIntMutablePair<>(eye2, -1));
                    }
                }
            }

            for (int i = 0; i < eyesInColumn.size() - 1; i++) {
                Vec3 eye1 = eyesInColumn.get(i);
                Vec3 eye2 = eyesInColumn.get(i + 1);

                double distance = Math.abs(eye2.y - eye1.y);
                if (distance >= gridSpacing * 1.5) {
                    double midY = (eye1.y + eye2.y) / 2.0;

                    Vec3 mouthPos = new Vec3(
                            (gridX - gridSizeX / 2.0) * gridSpacing,
                            midY,
                            eye1.z
                    );

                    double conflictDistance = gridSpacing * 0.6;

                    boolean hasExistingMouth = false;
                    for (ObjectIntPair<Vec3> existingMouth : mouthPositions) {
                        if (existingMouth.key().distanceToSqr(mouthPos) < conflictDistance * conflictDistance) {
                            hasExistingMouth = true;
                            break;
                        }
                    }

                    if (hasExistingMouth) {
                        continue;
                    }

                    eyePositions.removeIf(existingEye ->
                            existingEye.key().distanceToSqr(mouthPos) < conflictDistance * conflictDistance);

                    hungryPositions.removeIf(existingHungry ->
                            existingHungry.distanceToSqr(mouthPos) < conflictDistance * conflictDistance);

                    boolean hasConflict = false;
                    for (ObjectIntPair<Vec3> existingMouth : mouthPositions) {
                        if (existingMouth.key().distanceToSqr(mouthPos) < gridSpacing * gridSpacing * 0.5) {
                            hasConflict = true;
                            break;
                        }
                    }

                    if (!hasConflict && random.nextDouble() < 0.8) {
                        mouthPositions.add(new ObjectIntMutablePair<>(mouthPos, -1));
                    }
                }
            }
        });
    }

    protected void dropAllDeathLoot(ServerLevel level, DamageSource damageSource) {
        // 使用实体实际位置计算中心方块位置，确保从真正的中心点蔓延
        BlockPos centerPos = BlockPos.containing(this.position()).below(1);

        Block targetBlock = Blocks.OBSIDIAN;
        if (ModChecker.confluence.isLoaded()) {
            // 猩红和魔矿砖对半概率
            String targetResource = this.random.nextBoolean() ?
                    "confluence:demonite_ore_bricks" :
                    "confluence:crimtane_ore_bricks";
            targetBlock = BuiltInRegistries.BLOCK.getOptional(
                    ResourceLocation.parse(targetResource)
            ).orElse(Blocks.OBSIDIAN);
        }
        Block block = targetBlock;

        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos framePos = centerPos.offset(x, y, z);
                    boolean isFrame = Math.abs(x) == 4 || Math.abs(y) == 4 || Math.abs(z) == 4;
                    BlockState blockState = level.getBlockState(framePos);

                    float hardness = blockState.getDestroySpeed(level, framePos);

                    if (isFrame) {
                        if (((hardness != -1.0f && hardness < 5.0f)) || blockState.canBeReplaced()) {
                            level.setBlockAndUpdate(framePos, block.defaultBlockState());
                        }
                    } else if (!blockState.isAir() && hardness != -1.0f && hardness < 5.0f) {
                        level.setBlockAndUpdate(framePos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }

        super.dropAllDeathLoot(level, damageSource);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public boolean isMovingAlongX() {
        Direction dir = this.getDirection();
        return dir == Direction.EAST || dir == Direction.WEST;
    }

    @Override
    public @NotNull Vec3 getForward() {
        float yaw = this.getYRot();
        float normalizedYaw = Math.round(yaw / 90.0f) * 90.0f;
        float yawRad = (float) Math.toRadians(normalizedYaw);
        return new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
    }

    public void setForward(Direction direction) {
        switch (direction) {
            case NORTH -> this.setYRot(0.0F);   // 北: 0°
            case EAST -> this.setYRot(90.0F);   // 东: 90°
            case SOUTH -> this.setYRot(180.0F); // 南: 180°
            case WEST -> this.setYRot(270.0F);  // 西: 270°
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    /**
     * 根据实体方向旋转相对位置向量。此方法调用{@link #rotateLocalOffset(Vec3, boolean)}，并将{@code isAbs}参数设为{@code false}。
     * 旋转逻辑针对每个具体方向（北、东、南、西）进行精确计算。
     *
     * @param localOffset 原始的相对位置向量
     * @return 旋转后新的相对位置向量
     * @see #rotateLocalOffset(Vec3, boolean)
     */
    public Vec3 rotateLocalOffset(Vec3 localOffset) {
        return this.rotateLocalOffset(localOffset, false);
    }

    /**
     * 根据实体方向旋转相对位置向量。该方法提供两种旋转模式：
     * <ul>
     *   <li>当 {@code isAbs} 为 {@code false} 时（默认），进行精确的方向转换，考虑每个方向（北、东、南、西）的特定旋转。</li>
     *   <li>当 {@code isAbs} 为 {@code true} 时，进行绝对对称处理，将方向简化为南北轴向或东西轴向的转换。</li>
     * </ul>
     *
     * @param localOffset 原始的相对位置向量
     * @param isAbs       是否启用绝对对称处理模式：
     *                    {@code true} 表示启用，简化方向处理；
     *                    {@code false} 表示禁用，进行精确方向转换
     * @return 旋转后新的相对位置向量
     */
    public Vec3 rotateLocalOffset(Vec3 localOffset, boolean isAbs) {
        Direction direction = this.getDirection();
        if (isAbs) {
            return switch (direction) {
                case NORTH, SOUTH -> // 北方向,南方向
                        new Vec3(localOffset.x, localOffset.y, -localOffset.z);
                case EAST, WEST -> // 东方向,西方向
                        new Vec3(localOffset.z, localOffset.y, localOffset.x);
                default -> localOffset;
            };
        } else {
            return switch (direction) {
                case NORTH -> // 北方向：Z轴负向
                        new Vec3(localOffset.x, localOffset.y, -localOffset.z);
                case EAST -> // 东方向：X轴正向
                        new Vec3(localOffset.z, localOffset.y, localOffset.x);
                case SOUTH -> // 南方向：Z轴正向
                        new Vec3(-localOffset.x, localOffset.y, localOffset.z);
                case WEST -> // 西方向：X轴负向
                        new Vec3(-localOffset.z, localOffset.y, -localOffset.x);
                default -> localOffset;
            };
        }
    }

    private void updateChildPosition(Entity child) {
        List<Tuple<Integer, Vec3>> syncedOffsets = getLocalOffsets();
        List<Tuple<Vec3, Integer>> hungrySyncedOffsets = getHungryOffsets();

        if (child instanceof TheHungry hungry) {
            Vec3 localOffset = Vec3.ZERO;
            for (Tuple<Vec3, Integer> tuple : hungrySyncedOffsets) {
                if (tuple.getB().equals(hungry.getId())) {
                    localOffset = tuple.getA();
                    break;
                }
            }

            Vec3 childPos = this.position().add(rotateLocalOffset(localOffset));
            Vec3 vec3 = hungry.position().subtract(hungry.getInitPos());
            Vec3 summonPos = childPos.add(vec3);
            hungry.setPos(summonPos);
            hungry.setInitPos(childPos.toVector3f());
            // 更新饿鬼的旋转
            hungry.setYRot(this.getYRot());
        } else if (child instanceof WallOfFleshPart part) {
            int childIndex = subEntities.indexOf(child);
            Vec3 localOffset = (childIndex >= 0 && childIndex < syncedOffsets.size()) ? syncedOffsets.get(childIndex).getB() : Vec3.ZERO;

            Vec3 childPos = this.position().add(rotateLocalOffset(localOffset));
            part.moveTo(childPos);
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        this.noPhysics = true;
        this.noCulling = true;
        this.setNoGravity(true);
        double summonDir = 50;
        this.targetHeight = this.level().getMinBuildHeight() + (gridSizeY * gridSpacing) / 2;
        Vec3 summonPos = new Vec3(
                this.position().x,
                this.targetHeight,
                this.position().z
        ).add(getForward().scale(-summonDir));

        //确保刚生成时不会出现插值抖动和瞬移拖影
        this.setPos(summonPos);
        this.setPosRaw(summonPos.x, summonPos.y, summonPos.z);
        double dx = this.getX();
        double dy = this.getY();
        double dz = this.getZ();
        this.xo = dx;
        this.yo = dy;
        this.zo = dz;
        this.xOld = dx;
        this.yOld = dy;
        this.zOld = dz;

        this.reapplyPosition();
        this.InitPos = summonPos;

        if (level() instanceof ServerLevel level) {
            for (TheHungry theHungry : theHungries) {
                TEUtils.internalSpawnEntity(theHungry, level);
            }

            for (int i = 0; i < subEntities.size(); i++) {
                WallOfFleshPart part = subEntities.get(i);
                if (i < eyePositions.size()) {
                    eyePositions.get(i).right(part.getId());
                } else {
                    mouthPositions.get(i - eyePositions.size()).right(part.getId());
                }
            }
            AdapterUtils.sendToAllPlayers(
                    new SyncWallOfFleshPositionsPacket(
                            this.getId(), eyePositions, mouthPositions
                    )
            );
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        for (Entity child : subEntities) {
            updateChildPosition(child);
        }
        for (Tuple<Vec3, Integer> tuple : theHungryList) {
            Entity child = level().getEntity(tuple.getB());
            if (child != null) {
                updateChildPosition(child);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && !this.isDeadOrDying()) {
            if (this.tickCount % 5 == 0) {
                handleChunkLoading();
            }

            if (--summonCD <= 0) {
                summonCD = summonCDAll;
                List<Integer> deadHungryIndices = new ArrayList<>();
                for (int i = 0; i < theHungryList.size(); i++) {
                    Tuple<Vec3, Integer> tuple = theHungryList.get(i);
                    Integer hungryId = tuple.getB();
                    Entity hungry = level().getEntity(hungryId);
                    if (hungry == null || !hungry.isAlive()) {
                        deadHungryIndices.add(i);
                    }
                }

                for (int index : deadHungryIndices) {
                    // 40%概率重新生成饿鬼
                    if (random.nextFloat() < 0.4f) {
                        Tuple<Vec3, Integer> tuple = theHungryList.get(index);
                        Vec3 theHungryPos = tuple.getA();
                        TheHungry newHungry = TEUtils.spawnEntity(() -> new TheHungry(TEMonsterEntities.THE_HUNGRY.get(), level(), new AbstractPrefab().getPrefab()) {
                            @Override
                            protected boolean shouldDropLoot() {
                                return false;
                            }
                        }, (ServerLevel) level(), theHungryPos);

                        if (newHungry != null) {
                            this.theHungryList.set(index, new Tuple<>(theHungryPos, newHungry.getId()));
                            newHungry.minion_setOwner(this);
                            newHungry.setInitPos(this.position().add(theHungryPos).toVector3f());
                            newHungry.setPos(this.position().add(theHungryPos));
                            level().playSound(null, newHungry.blockPosition(), TESounds.WALL_OF_FLESH_SUMMON.get(), SoundSource.HOSTILE, 1, 1);
                        }
                    }
                }
            }

            if (this.tickCount % 5 == 0 && this.getInsideBox() != null && this.getOutsideBox() != null) {
                List<Player> nearbyPlayers = level().getEntitiesOfClass(Player.class,
                        this.getOutsideBox());


                DeferredHolder<MobEffect, HorrifiedEffect> horrifiedHolder = TEEffects.HORRIFIED;
                MobEffectInstance horrifiedEffect = new MobEffectInstance(horrifiedHolder, 200, 3, false, true);

                nearbyPlayers.stream()
                        .filter(LivingEntity::canBeSeenByAnyone)
                        .filter(e -> !(e instanceof Player p && (p.isCreative() || p.isSpectator())))
                        .forEach(player -> {
                            horrifiedHolder.get().setWallOfFlesh(this);
                            player.addEffect(horrifiedEffect);
                        });
            }

            if (this.tickCount % 10 == 0) {
                updatePlayerMouseAssignments();
            }

            for (LivingEntity nearbyLiving : this.getInsideBoxPlayers()) {
                for (WallOfFleshPart part : this.subEntities) {
                    double distanceSqr = part.position().distanceToSqr(nearbyLiving.position());
                    if (distanceSqr <= 120 * 120) {
                        Vec3 localOffset = part.position();
                        part.tickPart(localOffset.x, localOffset.y, localOffset.z);
                    }
                }
            }
            if (shouldMove && this.isAlive()) {
                Vec3 forward = this.getForward();
                float yaw = (float) Math.toDegrees(
                        Math.atan2(-forward.x, forward.z)
                );

                float alignedYaw = Math.round(yaw / 90.0f) * 90.0f;

                this.setYRot(Mth.wrapDegrees(alignedYaw));
                Vec3 moveDirection = this.getForward();
                Vec3 currentOffset = this.position().subtract(InitPos);
                double progress = currentOffset.dot(moveDirection);

                // 四方向终点判断
                if ((moveDirection.x > 0 && progress >= FINISH_LINE_DISTANCE) || // 东方向
                        (moveDirection.x < 0 && progress <= -FINISH_LINE_DISTANCE) || // 西方向
                        (moveDirection.z > 0 && progress >= FINISH_LINE_DISTANCE) || // 南方向
                        (moveDirection.z < 0 && progress <= -FINISH_LINE_DISTANCE) || // 北方向
                        !this.level().getWorldBorder().isWithinBounds(this.position())) {
                    //如果血肉墙到达了地图的另一边，它会消失，且所有受到惊恐减益影响的玩家会死亡
                    getNearbyPlayers().stream().filter(entity -> entity.hasEffect(TEEffects.HORRIFIED)).forEach(LivingEntity::kill);
                    this.clearChildrenAndHungry();
                    this.bossEvent.getPlayers().forEach(p -> p.sendSystemMessage(this.getDisplayName().copy().append(Component.translatable("message.confluence.boss_discard", getDisplayName()))));
                    this.discard();
                    return;
                }
                this.addDeltaMovement(getForward().scale(this.getMoveSpeed() * 0.125F));
                double yDiff = this.targetHeight == 0 ? 0 : targetHeight - this.getY();
                if (Math.abs(yDiff) > 0.1) {
                    float directionSign = (yDiff > 0) ? 1.0f : -1.0f;
                    this.setDeltaMovement(this.getDeltaMovement().add(0, directionSign, 0).scale(this.getMoveSpeed() * 0.1F));
                }
            }
        }

        if (genSegments && this.dirty) {
            genSegments = false;
            if (!level().isClientSide) {
                CameraShakeManager.addCameraShake(new CameraShakeData(300, this.position(), 180));
            }
        }
    }

    public AABB getInsideBox() {
        Direction dir = this.getDirection();
        boolean isReverse = dir == Direction.WEST || dir == Direction.SOUTH;
        int completion = isReverse ? 0 : 5;
        Vec3 vec3 = new Vec3(gridSizeX * gridSpacing / 2, gridSizeY * gridSpacing / 2, completion);
        Vec3 rotVec = this.rotateLocalOffset(vec3, true);

        if (isMovingAlongX()) {
            insideCollisionBox = new AABB(
                    this.position().subtract(rotVec.x, rotVec.y, rotVec.z + gridSpacing / 2),
                    this.position().add(rotVec.x + 150 * this.getForward().x, rotVec.y, rotVec.z + 150 * this.getForward().z - gridSpacing / 2));
        } else {
            insideCollisionBox = new AABB(
                    this.position().subtract(rotVec.x + gridSpacing / 2, rotVec.y, rotVec.z),
                    this.position().add(rotVec.x, rotVec.y, rotVec.z + 120 * this.getForward().z - gridSpacing / 2));
        }
        return this.insideCollisionBox;
    }

    public AABB getOutsideBox() {
        Direction dir = this.getDirection();
        boolean isReverse = dir == Direction.WEST || dir == Direction.SOUTH;
        int completion = isReverse ? -200 : 200;
        Vec3 vec3 = new Vec3(gridSizeX * gridSpacing / 2 + 150, gridSizeY * gridSpacing / 2 + 150, completion);
        Vec3 rotVec = this.rotateLocalOffset(vec3, true);

        if (isMovingAlongX()) {
            outsideCollisionBox = new AABB(
                    this.position().subtract(rotVec.x, rotVec.y, rotVec.z + gridSpacing / 2),
                    this.position().add(rotVec.x, rotVec.y, rotVec.z - gridSpacing / 2));
        } else {
            outsideCollisionBox = new AABB(
                    this.position().subtract(rotVec.x - 30 + gridSpacing / 2, rotVec.y, rotVec.z),
                    this.position().add(rotVec.x - 30 - gridSpacing / 2, rotVec.y, rotVec.z));
        }
        return this.outsideCollisionBox;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return this.getOutsideBox();
    }

    @Override
    public void onRemovedFromLevel() {
        subEntities.forEach(Entity::onRemovedFromLevel);
        clearChildrenAndHungry();
        this.bossEvent.removeAllPlayers();
        super.onRemovedFromLevel();
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return LibUtils.getOwner(target) != this && super.canAttack(target);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_DROWNING)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    public boolean hurt(WallOfFleshPart wallOfFleshPart, @NotNull DamageSource source, float damage) {
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && wallOfFleshPart instanceof WallOfFleshMouth) {
            this.hurtArmor(source, damage);
            damage = CombatRules.getDamageAfterAbsorb(this, damage, source, 12, (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }
        return this.hurt(source, damage);
    }

    public boolean hurt(DamageSource source, float amount) {

        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide) {
            Vec3 centerPos = this.position();
            Entity sourceEntity = damageSource.getEntity();
            int maxY = DimensionDefaults.NETHER_GENERATION_HEIGHT * 3 / 4; // 留出框架的空间
            Vec3 addVec = new Vec3(0, 10, 0);
            if (sourceEntity instanceof Player player) {
                centerPos = player.position().add(addVec);
                if (this.level().dimension() == Level.NETHER) {   // 如果在地狱维度，确保中心位置在 NETHER_GENERATION_HEIGHT 以下
                    if (centerPos.y() > maxY) {
                        centerPos = new Vec3(centerPos.x(), maxY, centerPos.z());
                    }
                }
            } else if (sourceEntity != null) {
                // 如果伤害来源不是玩家但能追溯到其所有者
                if (sourceEntity instanceof OwnableEntity ownable) {
                    Entity owner = ownable.getOwner();
                    if (owner instanceof Player) {
                        centerPos = owner.position().add(addVec);
                    }
                }
                if (this.level().dimension() == Level.NETHER) {   // 如果在地狱维度，确保中心位置在 NETHER_GENERATION_HEIGHT 以下
                    if (centerPos.y() > maxY) {
                        centerPos = new Vec3(centerPos.x(), maxY, centerPos.z());
                    }
                }
            } else {
                List<Player> intersectingPlayers = this.level().players().stream()
                        .filter(player -> this.getOutsideBox().intersects(player.getBoundingBox()))
                        .collect(Collectors.toList());

                if (!intersectingPlayers.isEmpty()) {
                    RandomSource random = this.level().random;
                    Player selectedPlayer = intersectingPlayers.get(random.nextInt(intersectingPlayers.size()));
                    centerPos = selectedPlayer.position().add(addVec);

                    if (this.level().dimension() == Level.NETHER) {   // 如果在地狱维度，确保中心位置在 NETHER_GENERATION_HEIGHT 以下
                        if (centerPos.y() > maxY) {
                            centerPos = new Vec3(centerPos.x(), maxY, centerPos.z());
                        }
                    }
                } else if (this.level().dimension() == Level.NETHER && centerPos.y() > maxY) {   // 如果在地狱维度，确保中心位置在 NETHER_GENERATION_HEIGHT 以下
                    centerPos = new Vec3(centerPos.x(), maxY, centerPos.z());
                }
            }

            this.setPos(new Vec3(centerPos.x(), centerPos.y(), centerPos.z()));
            clearChildrenAndHungry();
        }
        super.die(damageSource);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide) {
            AdapterUtils.sendToAllPlayers(new SyncWallOfFleshPositionsPacket(getId(), List.of(), List.of()));
        }
        super.remove(reason);
    }

    private void clearChildrenAndHungry() {
        subEntities.forEach(Entity::discard);
        localOffsets.clear();
        theHungryList.stream().map(tuple -> level().getEntity(tuple.getB())).filter(Objects::nonNull).forEach(Entity::discard);
        theHungryList.clear();
    }

    @Override
    public void changeState() {
        if (this.getStage() == 1 && this.getHealth() / getMaxHealth() < 0.5) {
            this.setStage(2);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getMoveSpeed() * 1.45F);
            for (WallOfFleshPart part : this.getParts()) {
                part.onParentChangeState(this.getStage());
            }
        }
        this.syncStatus(this.getStage());
    }

    @Override
    protected void initStage(int stage) {
        if (stage == 2) {
            for (WallOfFleshPart part : this.getParts()) {
                part.onParentChangeState(stage);
            }
        }
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        double size = this.getOutsideBox().getSize() * 1.5F;
        return distanceToSqr(entity) < size * size;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public WallOfFleshPart @NotNull [] getParts() {
        return this.subEntities.toArray(new WallOfFleshPart[0]);
    }

    @Override
    public void addSkills() {
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    protected LivingEntity findTarget() {
        List<Player> nearbyPlayers = getNearbyPlayers();

        if (nearbyPlayers.isEmpty()) {
            return null;
        }

        Player closestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        for (Player player : nearbyPlayers) {
            double distance = this.distanceToSqr(player);
            if (distance < minDistance) {
                minDistance = distance;
                closestPlayer = player;
            }
        }

        return closestPlayer;
    }

    protected List<Player> getNearbyPlayers() {
        return this.getNearbyPlayers(0);
    }

    protected List<Player> getInsideBoxPlayers() {
        return this.getInsideBoxPlayers(0);
    }

    @Override
    protected List<Player> getNearbyPlayers(double range) {
        List<Player> players = new ArrayList<>();
        isCreativePlayer = false;
        List<Player> nearbyTargets = level().getEntitiesOfClass(Player.class,
                this.getOutsideBox().inflate(range));
        for (Player player : nearbyTargets) {
            players.add(player);
            if (!isCreativePlayer && !player.canBeSeenAsEnemy()) {
                isCreativePlayer = true;
            }
            this.noActionTime = 0;
        }
        return players;
    }

    protected List<Player> getInsideBoxPlayers(double range) {
        List<Player> players = new ArrayList<>();
        isCreativePlayer = false;
        List<Player> nearbyTargets = level().getEntitiesOfClass(Player.class,
                this.getInsideBox().inflate(range));
        for (Player player : nearbyTargets) {
            players.add(player);
            if (!isCreativePlayer && !player.canBeSeenAsEnemy()) {
                isCreativePlayer = true;
            }
            this.noActionTime = 0;
        }
        return players;
    }

    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        return false;
    }

    protected boolean canRide(Entity entity) {
        return false;
    }

    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    public int getGridSizeX() {
        return gridSizeX;
    }

    public int getGridSizeY() {
        return gridSizeY;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.subEntities.size(); ++i) {
            this.subEntities.get(i).setId(id + i + 1);
        }
    }

    /**
     * 更新玩家与嘴/眼的分配关系，确保每个玩家都分配到最近的部位，
     * 并限制最多参与分配的嘴为2个、眼睛为4个
     */
    private void updatePlayerMouseAssignments() {
        mouseToPlayerMap.clear();
        eyeToPlayerMap.clear();

        // 收集所有存活的玩家和部位
        List<Player> alivePlayers = new ArrayList<>();
        List<WallOfFleshMouth> aliveMice = new ArrayList<>();
        List<WallOfFleshEye> aliveEyes = new ArrayList<>();

        for (LivingEntity living : this.getInsideBoxPlayers()) {
            if (living instanceof Player player && player.isAlive()) {
                alivePlayers.add(player);
            }
        }

        for (WallOfFleshPart part : this.subEntities) {
            if (part instanceof WallOfFleshMouth mouse && mouse.isAlive()) {
                aliveMice.add(mouse);
            } else if (part instanceof WallOfFleshEye eye && eye.isAlive()) {
                aliveEyes.add(eye);
            }
        }

        if (alivePlayers.isEmpty() || (aliveMice.isEmpty() && aliveEyes.isEmpty())) {
            return;
        }

        assignNearestParts(alivePlayers, aliveMice, MAX_ASSIGNED_MOUTHS, mouseToPlayerMap);
        assignNearestParts(alivePlayers, aliveEyes, MAX_ASSIGNED_EYES, eyeToPlayerMap);
    }

    private <T extends WallOfFleshPart> void assignNearestParts(List<Player> players,
                                                                List<T> parts,
                                                                int maxParts,
                                                                Map<T, Player> assignment) {
        if (parts.isEmpty() || players.isEmpty()) {
            return;
        }

        List<T> availableParts = new ArrayList<>(parts);
        if (maxParts > 0 && availableParts.size() > maxParts) {
            availableParts.sort(Comparator.comparingDouble(part -> {
                double minDistance = Double.MAX_VALUE;
                for (Player player : players) {
                    double distanceSqr = part.position().distanceToSqr(player.position());
                    if (distanceSqr < minDistance) {
                        minDistance = distanceSqr;
                    }
                }
                return minDistance;
            }));
            availableParts = new ArrayList<>(availableParts.subList(0, maxParts));
        }

        Map<T, Player> partToNearestPlayer = new HashMap<>();

        for (T part : availableParts) {
            Player nearestPlayer = findNearestPlayer(part, players);
            if (nearestPlayer != null) {
                partToNearestPlayer.put(part, nearestPlayer);
            }
        }

        assignment.putAll(partToNearestPlayer);
    }

    private <T extends WallOfFleshPart> Player findNearestPlayer(T part, List<Player> players) {
        Player nearestPlayer = null;
        double nearestDistanceSqr = Double.MAX_VALUE;
        for (Player player : players) {
            double distanceSqr = part.position().distanceToSqr(player.position());
            if (distanceSqr < nearestDistanceSqr) {
                nearestDistanceSqr = distanceSqr;
                nearestPlayer = player;
            }
        }
        return nearestPlayer;
    }

    /**
     * 检查指定的嘴是否是分配给指定玩家的嘴
     */
    public boolean isNearestMouthForPlayer(WallOfFleshMouth mouse, Player player) {
        if (!player.isAlive() || !mouse.isAlive()) {
            return false;
        }

        // 检查这个嘴是否分配给了这个玩家
        Player assignedPlayer = mouseToPlayerMap.get(mouse);
        return assignedPlayer != null && assignedPlayer == player;
    }

    /**
     * 检查指定的眼睛是否是分配给指定玩家的眼
     */
    public boolean isNearestEyeForPlayer(WallOfFleshEye eye, Player player) {
        if (!player.isAlive() || !eye.isAlive()) {
            return false;
        }

        Player assignedPlayer = eyeToPlayerMap.get(eye);
        return assignedPlayer != null && assignedPlayer == player;
    }

    /**
     * 区块加载
     */
    private void handleChunkLoading() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int gridSizeX = this.getGridSizeX();
        float gridSpacing = this.gridSpacing;

        int minX = (int) Math.floor((this.getX() - gridSizeX * gridSpacing / 2) / 16.0);
        int maxX = (int) Math.ceil((this.getX() + gridSizeX * gridSpacing / 2) / 16.0);
        int minZ = (int) Math.floor((this.getZ() - gridSizeX * gridSpacing / 2) / 16.0);
        int maxZ = (int) Math.ceil((this.getZ() + gridSizeX * gridSpacing / 2) / 16.0);

        for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
                net.minecraft.world.level.ChunkPos chunkPos = new net.minecraft.world.level.ChunkPos(chunkX, chunkZ);
                WorldChunksManager.forceLoadChunk(serverLevel, chunkPos);
            }
        }

        if (this.tickCount % 180 == 0) {
            WorldChunksManager.freeChunks(serverLevel);
        }
    }
}
