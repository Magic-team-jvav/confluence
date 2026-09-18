package org.confluence.mod.client.summoner;

import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import net.minecraft.client.renderer.LevelRenderer;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 动态光照调度器(1.21.1 版,26.2 新架构移植:空间查找 + 跨帧跟踪)。
 * <p>
 * 双路径:
 * - 方块路径:{@code LevelRendererMixin} 注入 getLightColor,返回提升后的 packed light
 * - 实体路径:{@code EntityRendererMixin} 注入 getPackedLightCoords,{@link #getDynamicLight(Vec3, int)}
 * </p>
 * <p>
 * 光源分组:光源按 16×16×16 三维区块分组(排序数组 + {@code SectionPos.asLong} key→起始索引)。
 * 查询时按查询点在区块内的位置裁剪邻居区块(MAX_RADIUS 7.75 < 16,边界距离决定溢出方向),
 * 平均只需查 1-8 个分组而非固定 27 个。
 * </p>
 * <p>
 * 跨帧跟踪:写入光源时计算身份哈希(id = 位置×亮度混合),update 用 {@code Long2ObjectOpenHashMap}
 * O(1) 匹配上一帧缓存:未命中(新增/大幅移动)→ 刷新;命中但位置或亮度不同(移动)→ 刷新;
 * 命中且相同(静止)→ 跳过;旧缓存未被命中的(移除)→ 刷新。静止帧短路复用快照,零分配零排序。
 * </p>
 * <p>
 * 与 26.2 差异:1.21.1 无 smooth 8-bit 格式,但采用 LDL 1.21.1 同款位运算,
 * 把 dynamic light ×16 写入 block 光通道低 20 位(1.21.1 实际值 0-240 占用低 12 位),借助 16×16 lightmap 的
 * 双线性过滤实现 1/16 级平滑过渡。
 * 布局:LevelRenderer.getLightColor 与 EntityRenderer.getPackedLightCoords
 * 均为 LightTexture.pack 布局 sky&lt;&lt;20 | block&lt;&lt;4。
 * </p>
 */
public final class DynamicLightDispatcher {

    /** 影响半径:限制区块重编译范围(7.75 < 16,见 SECTION_BITS) */
    private static final double MAX_RADIUS = 7.75;
    private static final double MAX_RADIUS_SQUARED = MAX_RADIUS * MAX_RADIUS;
    /** 衰减斜率:每格衰减量(15 级 / 7.75 格) */
    private static final double FALLOFF = 15.0 / MAX_RADIUS;
    /** 光源分组区块边长 16(2^4),MAX_RADIUS < 16 保证影响不跨 2 个区块 */
    private static final int SECTION_BITS = 4;

    /** 当前帧光源累积(渲染线程,addLightSources 追加,update 消费后 clear 复用) */
    private static final ArrayList<LightSource> LightSources = new ArrayList<>();
    /** 编译线程快照:volatile 引用替换,对象不可变,零拷贝 */
    private static volatile Snapshot SnapshotLightSources = Snapshot.EMPTY;
    /** 跨帧光源缓存:身份哈希 → 上一帧光源(命中判定 + 位置比较决定区块刷新) */
    private static Long2ObjectOpenHashMap<LightSource> LastFrameLightSources = new Long2ObjectOpenHashMap<>();

    /** 点光源(写入时计算身份哈希与分组区块 key) */
    private record LightSource(long id, long sectionKey, double x, double y, double z, int luminance) {}

    /** 空间查找快照:按 sectionKey 排序的光源 + sectionKey→排序数组起始索引 */
    private record Snapshot(LightSource[] sources, Long2IntOpenHashMap startIndex) {
        static final Snapshot EMPTY = new Snapshot(new LightSource[0], emptyIndex());

        private static Long2IntOpenHashMap emptyIndex() {
            Long2IntOpenHashMap map = new Long2IntOpenHashMap(1);
            map.defaultReturnValue(-1);
            return map;
        }
    }

    private DynamicLightDispatcher() {}

    /** 放置沿 PathNode 路径分布的多个点光源(局部 AABB → 世界旋转) */
    public static void addLightSources(PathNode pathNode, AABB aabb, int light) {
        Vec3 pos = pathNode.pos();
        double minX = aabb.minX, maxX = aabb.maxX;
        double minY = aabb.minY, maxY = aabb.maxY;
        double minZ = aabb.minZ, maxZ = aabb.maxZ;

        // Z轴方向最长,沿Z轴等间隔放置点光源(间隔≤0.5格)
        double zLen = maxZ - minZ;
        int count = Math.max(1, Mth.ceil(zLen / 0.5));
        double step = zLen / count;

        // 局部→世界旋转:先绕Y旋转yaw,再绕X旋转pitch,再绕Z旋转roll
        float yawRad = (float) Math.toRadians(-pathNode.yaw());
        float pitchRad = (float) Math.toRadians(pathNode.pitch());
        double cosY = Math.cos(yawRad), sinY = Math.sin(yawRad);
        double cosP = Math.cos(pitchRad), sinP = Math.sin(pitchRad);

        for (int i = 0; i <= count; i++) {
            double lz = minZ + step * i;

            double lx = (minX + maxX) * 0.5;
            double ly = (minY + maxY) * 0.5;

            double rry = ly * cosP - lz * sinP;
            double rrz = ly * sinP + lz * cosP;

            double wwx = lx * cosY + rrz * sinY;
            double wwz = -lx * sinY + rrz * cosY;

            addLightSources(pos.add(wwx, rry, wwz), light);
        }
    }

    /** 写入点光源:O(1) 追加,写入时计算身份哈希(跨帧跟踪用)与分组区块 key */
    public static void addLightSources(Vec3 pos, int light) {
        light = Mth.clamp(light, 0, 15);
        if (light <= 0) {
            return;
        }
        int x = Mth.floor(pos.x), y = Mth.floor(pos.y), z = Mth.floor(pos.z);
        LightSources.add(new LightSource(
                lightId(pos.x, pos.y, pos.z, light),
                SectionPos.asLong(x >> SECTION_BITS, y >> SECTION_BITS, z >> SECTION_BITS),
                pos.x, pos.y, pos.z, light));
    }

    /**
     * 帧末调度(renderLevel TAIL):构建空间查找快照并跨帧对比光源,重编译位置变化/新增/移除的光源影响区块。
     * <p>
     * 区块编译每帧触发,光源位置/亮度变化的 section 立即重建以获得最佳跟手效果;
     * 静止光源(身份命中且位置亮度相同)零刷新,全静止帧短路复用快照。
     * </p>
     */
    public static void update(LevelRenderer levelRenderer) {
        Snapshot old = SnapshotLightSources;

        if (LightSources.isEmpty()) {
            // 无光源:恢复全部旧影响区块(移除光源后光照回归 vanilla),清空缓存与快照
            if (old.sources.length != 0) {
                LongOpenHashSet sections = new LongOpenHashSet(old.sources.length * 8);
                for (LightSource s : old.sources) {
                    gatherClosestChunks(s.x, s.y, s.z, sections);
                }
                for (long sec : sections) {
                    levelRenderer.setSectionDirty(SectionPos.x(sec), SectionPos.y(sec), SectionPos.z(sec));
                }
                SnapshotLightSources = Snapshot.EMPTY;
                LastFrameLightSources.clear();
            }
            return;
        }

        // 静止帧短路:光源与上帧完全一致(id 全部命中且亮度相同、数量相同)
        // → 快照排序数组、跨帧缓存、区块均无变化,直接复用,零分配零排序
        boolean allStatic = LightSources.size() == LastFrameLightSources.size();
        if (allStatic) {
            for (LightSource s : LightSources) {
                LightSource matched = LastFrameLightSources.get(s.id());
                if (matched == null || matched.luminance != s.luminance) {
                    allStatic = false;
                    break;
                }
            }
        }
        if (allStatic) {
            LightSources.clear();
            return;
        }

        LightSource[] sources = LightSources.toArray(new LightSource[0]);
        LightSources.clear();

        // 空间查找:按 sectionKey 排序 + 起始索引(同 key 连续,一次哈希定位区间)
        Arrays.sort(sources, Comparator.comparingLong(LightSource::sectionKey));
        Long2IntOpenHashMap startIndex = new Long2IntOpenHashMap(sources.length * 2 + 1);
        startIndex.defaultReturnValue(-1);
        long prevKey = sources[0].sectionKey();
        startIndex.put(prevKey, 0);
        for (int i = 1; i < sources.length; i++) {
            long key = sources[i].sectionKey();
            if (key != prevKey) {
                startIndex.put(key, i);
                prevKey = key;
            }
        }

        // 跨帧光源对比:未命中 = 新增/大幅移动;命中但位置或亮度不同 = 移动;命中且相同 = 静止
        Long2ObjectOpenHashMap<LightSource> newCache = new Long2ObjectOpenHashMap<>(sources.length * 2);
        LongOpenHashSet dirty = new LongOpenHashSet();
        for (LightSource s : sources) {
            LightSource matched = LastFrameLightSources.get(s.id());
            if (matched == null || matched.x != s.x || matched.y != s.y || matched.z != s.z || matched.luminance != s.luminance) {
                gatherClosestChunks(s.x, s.y, s.z, dirty);
            }
            newCache.put(s.id(), s);
        }
        // 旧缓存未被命中的光源 = 移除 → 刷新其影响区块
        for (Long2ObjectMap.Entry<LightSource> entry : LastFrameLightSources.long2ObjectEntrySet()) {
            if (!newCache.containsKey(entry.getLongKey())) {
                LightSource removed = entry.getValue();
                gatherClosestChunks(removed.x, removed.y, removed.z, dirty);
            }
        }
        for (long sec : dirty) {
            levelRenderer.setSectionDirty(SectionPos.x(sec), SectionPos.y(sec), SectionPos.z(sec));
        }

        // 快照与跨帧缓存引用替换(编译线程读旧快照,无竞态)
        SnapshotLightSources = new Snapshot(sources, startIndex);
        LastFrameLightSources = newCache;
    }

    // ==================== 方块路径(编译期,BlockPos 级,GPU 顶点插值平滑) ====================

    /**
     * 方块路径:由 {@code LevelRendererMixin} 在 getLightColor 调用,返回提升后的 packed light。
     */
    public static int getDynamicLight(BlockAndTintGetter level, BlockState state, BlockPos blockPos, int originalLight) {
        if (state.isSolidRender(level, blockPos)) {
            return originalLight;
        }
        double dynamicLight = getDynamicLightLevel(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        if (dynamicLight > 0) {
            int blockLevel = LightTexture.block(originalLight);
            if (dynamicLight > blockLevel) {
                return withDynamicLight(originalLight, dynamicLight);
            }
        }
        return originalLight;
    }

    // ==================== 实体路径(渲染期,查询眼睛所在方块贡献) ====================

    /**
     * 实体路径:由 {@code EntityRendererMixin} 在 getPackedLightCoords 调用,
     * 用实体眼睛的精确连续坐标查询动态光照——贡献随实体移动连续变化,无方块级跳变。
     *
     * @param eyePos       实体眼睛的连续世界坐标
     * @param originalLight vanilla 原始 packed light
     * @return 提升后的 packed light(仅 block 位可能提升,sky 位保持不变)
     */
    public static int getDynamicLight(Vec3 eyePos, int originalLight) {
        double dynamicLight = getDynamicLightLevel(eyePos.x, eyePos.y, eyePos.z);
        if (dynamicLight > 0) {
            int blockLevel = LightTexture.block(originalLight);
            if (dynamicLight > blockLevel) {
                return withDynamicLight(originalLight, dynamicLight);
            }
        }
        return originalLight;
    }

    /**
     * 查询 (x, y, z) 连续坐标处的动态光照(方块路径传方块中心,实体路径传 eyePos 精确位置)。
     * <p>
     * 区块裁剪用 floor 后的块内偏移:偏移 ≤6 查西/下/北邻居,≥9 查东/上/南邻居,[7,8] 只查自身
     * (MAX_RADIUS 7.75 < 16,边界距离决定溢出方向),最多 9 个分组,平均 1-8 个。
     * 距离计算用连续坐标,贡献随查询点移动连续变化。
     * </p>
     */
    private static double getDynamicLightLevel(double x, double y, double z) {
        Snapshot snap = SnapshotLightSources;
        if (snap.sources.length == 0) {
            return 0;
        }
        double result = 0;
        int fx = Mth.floor(x), fy = Mth.floor(y), fz = Mth.floor(z);
        int cx = fx >> SECTION_BITS, cy = fy >> SECTION_BITS, cz = fz >> SECTION_BITS;
        int ox = fx & 15, oy = fy & 15, oz = fz & 15;
        int x0 = ox <= 6 ? cx - 1 : cx;
        int x1 = ox >= 9 ? cx + 1 : cx;
        int y0 = oy <= 6 ? cy - 1 : cy;
        int y1 = oy >= 9 ? cy + 1 : cy;
        int z0 = oz <= 6 ? cz - 1 : cz;
        int z1 = oz >= 9 ? cz + 1 : cz;
        for (int bx = x0; bx <= x1; bx++) {
            for (int by = y0; by <= y1; by++) {
                for (int bz = z0; bz <= z1; bz++) {
                    int start = snap.startIndex.get(SectionPos.asLong(bx, by, bz));
                    if (start < 0) {
                        continue;
                    }
                    LightSource[] sources = snap.sources;
                    long key = sources[start].sectionKey();
                    for (int i = start; i < sources.length && sources[i].sectionKey() == key; i++) {
                        LightSource s = sources[i];
                        double ddx = x - s.x;
                        double ddy = y - s.y;
                        double ddz = z - s.z;
                        double distSq = ddx * ddx + ddy * ddy + ddz * ddz;
                        if (distSq <= MAX_RADIUS_SQUARED) {
                            double light = s.luminance - Math.sqrt(distSq) * FALLOFF;
                            if (light > result) {
                                result = light;
                            }
                        }
                    }
                }
            }
        }
        return Mth.clamp(result, 0.0, 15.0);
    }

    /**
     * 将动态光照写入 block 光通道(UV2 低 20 位,对齐 LDL 的 lightmap 坐标法)。
     * <p>
     * shader 侧 lightmap 采样为 {@code uv / 256.0} 映射到 16×16 lightmap 纹素:
     * block 光值(0-15)在 UV2 中按 {@code value << 4} 落在低 12 位,小数(1/16 级)进入
     * 更低 4 位后,texture() 双线性过滤会在相邻纹素间插值 → 平滑光照过渡。
     * 因此这里直接写入 {@code (int)(dynamicLight * 16.0)}(0-240,保留 1/16 小数精度),
     * 取代旧的 Math.round 整数取整(16 级跳变)。
     * 掩码保留 sky 位(bit20+),清除 block 光通道低 20 位;与 LDL 1.21.1 完全一致。
     * </p>
     */
    private static int withDynamicLight(int originalLight, double dynamicLight) {
        int luminance = (int) (dynamicLight * 16.0);
        return (originalLight & 0xfff00000) | (luminance & 0x000fffff);
    }

    /** 光源影响区块:所在区块 + 沿位置偏移方向的最多 7 个邻居(半径 7.75 < 区块边长 16) */
    private static void gatherClosestChunks(double x, double y, double z, LongOpenHashSet out) {
        int cx = SectionPos.blockToSectionCoord(x);
        int cy = SectionPos.blockToSectionCoord(y);
        int cz = SectionPos.blockToSectionCoord(z);
        out.add(SectionPos.asLong(cx, cy, cz));
        int sx = (Mth.floor(x) & 15) >= 8 ? 1 : -1;
        int sy = (Mth.floor(y) & 15) >= 8 ? 1 : -1;
        int sz = (Mth.floor(z) & 15) >= 8 ? 1 : -1;
        for (int i = 0; i < 7; i++) {
            if (i % 4 == 0) {
                cx += sx;
            } else if (i % 4 == 1) {
                cz += sz;
            } else if (i % 4 == 2) {
                cx -= sx;
            } else {
                cz -= sz;
                cy += sy;
            }
            out.add(SectionPos.asLong(cx, cy, cz));
        }
    }

    /** 光源身份哈希:位置(double 位模式)× 亮度混合(64-bit,碰撞概率可忽略;碰撞时走"位置不同→刷新"分支) */
    private static long lightId(double x, double y, double z, int luminance) {
        long h = Double.doubleToRawLongBits(x) * 0x9E3779B97F4A7C15L ^ Double.doubleToRawLongBits(y) * 0xBF58476D1CE4E5B9L ^ Double.doubleToRawLongBits(z) * 0x94D049BB133111EBL ^ (long) luminance * 0x27D4EB2F165667C5L;
        h ^= h >>> 32;
        return h;
    }
}
