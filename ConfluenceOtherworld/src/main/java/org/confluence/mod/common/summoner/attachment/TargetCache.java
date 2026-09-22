package org.confluence.mod.common.summoner.attachment;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.minion.Minion;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.function.Predicate;

public class TargetCache {

    private final Int2BooleanOpenHashMap visibilityCache = new Int2BooleanOpenHashMap();
    private final Int2IntOpenHashMap hurterHistory = new Int2IntOpenHashMap();
    private final Int2BooleanOpenHashMap targetCache = new Int2BooleanOpenHashMap();
    private final Int2FloatOpenHashMap distanceCache = new Int2FloatOpenHashMap();
    private final Long2ObjectOpenHashMap<List<LivingEntity>> spatialGroups = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<LevelChunk> chunkCache = new Long2ObjectOpenHashMap<>();
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private ServerPlayer owner = null;
    private ServerLevel serverLevel;

    public void tick(ServerPlayer player) {
        this.owner = player;
        this.serverLevel = owner.serverLevel();
        hurterHistory.replaceAll((key, value) -> value - 1);
        hurterHistory.values().removeIf(value -> value <= 0);
        visibilityCache.clear();
        targetCache.clear();
        distanceCache.clear();
        spatialGroups.clear();
        chunkCache.clear();
    }

    public void record(LivingEntity target, int time) {
        hurterHistory.put(target.getUUID().hashCode(), time);
    }

    public boolean isTarget(@Nullable LivingEntity target) {
        if (owner != null && target != null && owner != target && target.isAlive()) {
            return targetCache.computeIfAbsent(target.getUUID().hashCode(), key -> {
                if (target instanceof Enemy) {
                    return true;
                }
                if (target instanceof Targeting targeting && targeting.getTarget() == owner) {
                    return true;
                }
                return hurterHistory.containsKey(target.getUUID().hashCode());
            });
        }
        return false;
    }

    private static long cellKey(int x, int y, int z) {
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) y & 0x3FFFFFFL) << 12 | ((long) z & 0x3FFFFFFL);
    }

    public List<LivingEntity> getEntitiesInRadius(Vec3 pos, double radius, @Nullable Predicate<LivingEntity> filter) {
        List<LivingEntity> result = new ArrayList<>();
        if (radius <= 0 || serverLevel == null ) {
            return result;
        }
        double radiusSq = radius * radius;
        int minCellX = Mth.floor(pos.x - radius) >> 4;
        int maxCellX = Mth.floor(pos.x + radius) >> 4;
        int minCellY = Mth.floor(pos.y - radius) >> 4;
        int maxCellY = Mth.floor(pos.y + radius) >> 4;
        int minCellZ = Mth.floor(pos.z - radius) >> 4;
        int maxCellZ = Mth.floor(pos.z + radius) >> 4;
        AABB searchBox = null;
        for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                for (int cellZ = minCellZ; cellZ <= maxCellZ; cellZ++) {
                    if (!spatialGroups.containsKey(cellKey(cellX, cellY, cellZ))) {
                        AABB cellBox = cellBox(cellX, cellY, cellZ);
                        searchBox = searchBox == null ? cellBox : searchBox.minmax(cellBox);
                    }
                }
            }
        }
        if (searchBox != null) {
            for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, searchBox)) {
                if (owner != entity && entity.isAlive()) {
                    int entityCellX = Mth.floor(entity.getX()) >> 4;
                    int entityCellY = Mth.floor(entity.getY()) >> 4;
                    int entityCellZ = Mth.floor(entity.getZ()) >> 4;
                    if (entityCellX >= minCellX && entityCellX <= maxCellX && entityCellY >= minCellY && entityCellY <= maxCellY && entityCellZ >= minCellZ && entityCellZ <= maxCellZ) {
                        List<LivingEntity> cell = spatialGroups.computeIfAbsent(cellKey(entityCellX, entityCellY, entityCellZ), key -> new ArrayList<>());
                        if (cell.stream().noneMatch(existing -> existing.getUUID().equals(entity.getUUID()))) {
                            cell.add(entity);
                        }
                    }
                }
            }
        }
        for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                for (int cellZ = minCellZ; cellZ <= maxCellZ; cellZ++) {
                    List<LivingEntity> cell = spatialGroups.get(cellKey(cellX, cellY, cellZ));
                    if (cell == null) {
                        continue;
                    }
                    for (LivingEntity entity : cell) {
                        if (entity.isAlive() && entity.getBoundingBox().getCenter().distanceToSqr(pos) <= radiusSq) {
                            if (filter == null || filter.test(entity)) {
                                result.add(entity);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static AABB cellBox(int cellX, int cellY, int cellZ) {
        return new AABB(cellX << 4, cellY << 4, cellZ << 4, (cellX + 1) << 4, (cellY + 1) << 4, (cellZ + 1) << 4);
    }

    // ==================== Chunk 缓存（每tick预加载） ====================

    public LivingEntity getNewTarget(Minion minion, List<LivingEntity> targets, float ownerWarningDistance, boolean selfCenter) {
        Player owner = minion.getOwner();
        LivingEntity currentTarget = minion.getTarget();
        LivingEntity newTarget = null;
        double bestScore = Double.MAX_VALUE;
        for (LivingEntity entity : targets) {
            if (owner.getData(SummonerAttachmentTypes.SUMMON_MARK_DATA).isSummonMarkTarget(entity)) {
                return entity;
            }
            double score = selfCenter ? getDistance(minion, entity) : getDistance(owner, entity);
            if (ownerWarningDistance > 0 && getDistance(owner, entity) < ownerWarningDistance) {
                score -= 10000.0;
            }
            if (entity == currentTarget) {
                score -= 1000.0;
            }
            score += ((entity.getId() * 31 + minion.hashCode() * 17) % 5) * 40;
            if (score < bestScore) {
                bestScore = score;
                newTarget = entity;
            }
        }
        return newTarget;
    }

    public float getDistance(Minion minion, LivingEntity living) {
        int key = minion.getUuid().hashCode() + living.getUUID().hashCode();
        return distanceCache.computeIfAbsent(key, (IntToDoubleFunction) (k -> (float) minion.getPos().distanceTo(living.getBoundingBox().getCenter())));
    }

    public float getDistance(LivingEntity living1, LivingEntity living2) {
        int key = living1.getUUID().hashCode() + living2.getUUID().hashCode();
        return distanceCache.computeIfAbsent(key, (IntToDoubleFunction)(k -> (float) living1.getEyePosition().distanceTo(living2.getBoundingBox().getCenter())));
    }

    //此缓存不能被共享，极易卡顿，不建议使用
    @Deprecated
    public boolean isVisibility(AttachmentEntity attachmentEntity, LivingEntity living) {
        Integer key = attachmentEntity.getUuid().hashCode() + living.getUUID().hashCode();
        return visibilityCache.computeIfAbsent(key, k -> hasLineOfSight(attachmentEntity.getPos(), living.getBoundingBox().getCenter()));
    }

    public boolean isVisibility(@Nullable LivingEntity living1, @Nullable LivingEntity living2) {
        if (living1 != null && living2 != null) {
            Integer key = living1.getUUID().hashCode() + living2.getUUID().hashCode();
            return visibilityCache.computeIfAbsent(key, k -> hasLineOfSight(living1.getEyePosition(), living2.getEyePosition()));
        }
        return false;
    }

    private boolean hasLineOfSight(Vec3 from, Vec3 to) {
        if (serverLevel == null) {
            return false;
        }
        if (from.distanceToSqr(to) > 128.0 * 128.0) {
            return false;
        }

        double startX = Mth.lerp(-1.0E-7, from.x, to.x);
        double startY = Mth.lerp(-1.0E-7, from.y, to.y);
        double startZ = Mth.lerp(-1.0E-7, from.z, to.z);
        double endX = Mth.lerp(-1.0E-7, to.x, from.x);
        double endY = Mth.lerp(-1.0E-7, to.y, from.y);
        double endZ = Mth.lerp(-1.0E-7, to.z, from.z);

        int curX = Mth.floor(endX);
        int curY = Mth.floor(endY);
        int curZ = Mth.floor(endZ);

        double dx = startX - endX;
        double dy = startY - endY;
        double dz = startZ - endZ;

        int stepX = Mth.sign(dx);
        int stepY = Mth.sign(dy);
        int stepZ = Mth.sign(dz);

        double tDeltaX = stepX == 0 ? Double.MAX_VALUE : (double) stepX / dx;
        double tDeltaY = stepY == 0 ? Double.MAX_VALUE : (double) stepY / dy;
        double tDeltaZ = stepZ == 0 ? Double.MAX_VALUE : (double) stepZ / dz;

        double tMaxX = tDeltaX * (stepX > 0 ? 1.0 - Mth.frac(endX) : Mth.frac(endX));
        double tMaxY = tDeltaY * (stepY > 0 ? 1.0 - Mth.frac(endY) : Mth.frac(endY));
        double tMaxZ = tDeltaZ * (stepZ > 0 ? 1.0 - Mth.frac(endZ) : Mth.frac(endZ));

        int minBuildHeight = serverLevel.getMinBuildHeight();
        int maxBuildHeight = serverLevel.getMaxBuildHeight();

        // 局部缓存：同一条射线内复用 chunk/section 引用
        long lastChunkKey = Long.MIN_VALUE;
        LevelChunk chunk = null;
        int lastSectionY = Integer.MIN_VALUE;
        LevelChunkSection section = null;

        for (int steps = 0; steps < 128; steps++) {
            if (tMaxX > 1.0 && tMaxY > 1.0 && tMaxZ > 1.0) {
                return true;
            }

            // 步进
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    curX += stepX;
                    tMaxX += tDeltaX;
                } else {
                    curZ += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    curY += stepY;
                    tMaxY += tDeltaY;
                } else {
                    curZ += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }

            // 高度裁剪
            if (curY < minBuildHeight || curY >= maxBuildHeight) {
                continue;
            }

            // 从缓存取 chunk，未命中则加载并写入缓存
            int chunkX = SectionPos.blockToSectionCoord(curX);
            int chunkZ = SectionPos.blockToSectionCoord(curZ);
            long chunkKey = ChunkPos.asLong(chunkX, chunkZ);
            if (chunkKey != lastChunkKey) {
                chunk = chunkCache.get(chunkKey);
                if (chunk == null) {
                    chunk = serverLevel.getChunkSource().getChunkNow(chunkX, chunkZ);
                    if (chunk != null) {
                        chunkCache.put(chunkKey, chunk);
                    }
                }
                lastChunkKey = chunkKey;
                lastSectionY = Integer.MIN_VALUE;
            }

            if (chunk == null) {
                return false;
            }

            // 缓存 Section
            int sectionY = chunk.getSectionIndex(curY);
            if (sectionY != lastSectionY) {
                section = chunk.getSection(sectionY);
                lastSectionY = sectionY;
            }

            if (section == null || section.hasOnlyAir()) {
                continue;
            }

            BlockState blockState = section.getBlockState(curX & 15, curY & 15, curZ & 15);

            if (!blockState.canOcclude()) {
                continue;
            }

            if (blockState.isCollisionShapeFullBlock(serverLevel, mutablePos.set(curX, curY, curZ))) {
                return false;
            }

            VoxelShape voxelShape = blockState.getCollisionShape(serverLevel, mutablePos);
            if (voxelShape.isEmpty()) {
                continue;
            }

            BlockHitResult hitResult = voxelShape.clip(from, to, mutablePos);
            if (hitResult != null) {
                return false;
            }
        }

        return true;
    }
}
