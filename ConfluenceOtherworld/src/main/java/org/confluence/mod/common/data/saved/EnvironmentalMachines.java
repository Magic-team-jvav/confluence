package org.confluence.mod.common.data.saved;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.Heightmap;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.functional.terraform.EnvironmentalMachineBlock;
import org.confluence.mod.common.block.functional.terraform.Solution;
import org.confluence.mod.common.block.natural.BaseDroopingPlantsHeadBlock;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.*;

// 机器坐标、库存与已付费工作时间通过 IGlobalData 持久化；随机刻阻断蔓延，运行中的机器限量主动转换。
public enum EnvironmentalMachines implements IGlobalData {
    INSTANCE;
    public static final int SLOT_COUNT = 3;
    public static final int FUEL_PROGRESS_MAX = 100;
    public static final int CHUNK_WIDTH = 16;
    public static final int DAY_TICKS = 24000;
    public static final int CLENTAMINATOR_DAILY_COST = 3;
    public static final int TERRAFORMER_DAILY_COST = 1;
    private static final int STATUS_SYNC_INTERVAL = 20;
    private static final int ACTIVE_SAMPLES_PER_TICK = 24;
    private static final int TREE_BLOCK_LIMIT = 128;
    private static final int VINE_LENGTH_LIMIT = 25;
    public static final int MIN_RANGE_CHUNKS = 1;
    public static final int MAX_RANGE_CHUNKS = 20;
    public static final int DEFAULT_CLENTAMINATOR_RANGE_CHUNKS = 3;
    public static final int DEFAULT_TERRAFORMER_RANGE_CHUNKS = 5;
    // 每个储液槽最多保存一组溶液。
    public static final int CAPACITY = 64;
    private final Map<Key, State> machines = new LinkedHashMap<>();
    // 索引只保存机器引用，开关和库存实时读取；不遍历覆盖范围内的方块。
    private final Map<ResourceKey<Level>, Map<Long, List<Key>>> coverage = new HashMap<>();
    private final Map<ResourceKey<Level>, Set<Long>> biomeUpdates = new HashMap<>();

    private int indexedClentaminatorRange = -1;
    private int indexedTerraformerRange = -1;

    private record Key(ResourceKey<Level> dimension, BlockPos pos) {}

    public static final class State {
        // 当前已注入的溶液与各槽库存分离，停机后保留尚未使用的工作余量。
        public Solution solution;
        public final int[] slots = new int[SLOT_COUNT];
        public final Solution[] slotSolutions = new Solution[SLOT_COUNT];
        public int activeSlot = -1;
        public long paidUntil = Long.MIN_VALUE;
        public long lastTime = Long.MIN_VALUE;
        public boolean world;
        public boolean enabled;
        private long nextStatusSync;

        public int stored() {
            int count = 0;
            for (int amount : slots) count += amount;
            return count;
        }

        // 只有槽位里同时存在不同溶液才算混装；已经消耗的工作余量不是库存。
        public boolean hasMixedSolutions() {
            Solution selected = null;
            for (int slot = 0; slot < SLOT_COUNT; slot++) {
                if (slots[slot] <= 0 || slotSolutions[slot] == null) continue;
                if (selected != null && selected != slotSolutions[slot]) return true;
                selected = slotSolutions[slot];
            }
            return false;
        }

        // 玩家和漏斗共用库存变更逻辑，混装立即停机，但只有手动启动才引发事故。
        public void inventoryChanged(long now) {
            for (int slot = 0; slot < SLOT_COUNT; slot++)
                if (slots[slot] == 0) slotSolutions[slot] = null;
            long clock = enabled ? now : lastTime;
            if (solution != null && paidUntil > clock && stored() > 0) {
                boolean sameSolutionStored = false;
                for (int slot = 0; slot < SLOT_COUNT; slot++) {
                    if (slots[slot] > 0 && slotSolutions[slot] == solution) {
                        sameSolutionStored = true;
                        break;
                    }
                }
                if (!sameSolutionStored) {
                    enabled = false;
                    paidUntil = Long.MIN_VALUE;
                    activeSlot = -1;
                    solution = null;
                    lastTime = now;
                    clock = now;
                }
            }
            if (paidUntil <= clock) {
                solution = null;
                activeSlot = -1;
                for (int slot = 0; slot < SLOT_COUNT; slot++) {
                    if (slots[slot] > 0 && slotSolutions[slot] != null) {
                        solution = slotSolutions[slot];
                        break;
                    }
                }
            }
            if (hasMixedSolutions() || (stored() == 0 && paidUntil <= clock)) {
                enabled = false;
                lastTime = now;
            }
        }

        // 启动前校验库存或已付费余量；混装不允许进入工作状态。
        public boolean setEnabled(boolean requested, long now) {
            if (requested && hasMixedSolutions()) return false;
            if (enabled == requested) return true;
            if (requested) {
                power(now);
                inventoryChanged(now);
                if (solution == null || (stored() == 0 && paidUntil <= now)) return false;
                enabled = true;
                power(now);
            } else {
                enabled = false;
                lastTime = now;
            }
            return true;
        }

        // 预付一个溶液的工作时长；时间倒退只平移余量，避免重复收费或无限延长。
        public boolean power(long now) {
            if (!enabled) {
                if (lastTime != Long.MIN_VALUE && paidUntil > lastTime)
                    paidUntil = now + paidUntil - lastTime;
                lastTime = now;
                return false;
            }
            if (lastTime != Long.MIN_VALUE && now < lastTime && paidUntil > lastTime)
                paidUntil = now + paidUntil - lastTime;
            if (hasMixedSolutions()) {
                enabled = false;
                lastTime = now;
                return false;
            }
            lastTime = now;
            if (solution != null && now < paidUntil) return true;
            if (stored() == 0) {
                solution = null;
                activeSlot = -1;
                enabled = false;
                return false;
            }
            activeSlot = (activeSlot + 1) % SLOT_COUNT;
            while (slots[activeSlot] == 0) activeSlot = (activeSlot + 1) % SLOT_COUNT;
            solution = slotSolutions[activeSlot];
            slots[activeSlot]--;
            if (slots[activeSlot] == 0) slotSolutions[activeSlot] = null;
            paidUntil = now + DAY_TICKS / (world ? TERRAFORMER_DAILY_COST : CLENTAMINATOR_DAILY_COST);
            return true;
        }
    }

    // 配置表示边长；偶数范围在负方向多覆盖一个区块，确保实际宽度严格等于配置。
    public static int rangeChunks(boolean world) {
        return world ? CommonConfigs.TERRAFORMER_RANGE_CHUNKS.get() : CommonConfigs.CLENTAMINATOR_RANGE_CHUNKS.get();
    }

    public State get(ServerLevel level, BlockPos pos, boolean world) {
        Key key = new Key(level.dimension(), pos.immutable());
        State state = machines.get(key);
        if (state == null) {
            state = new State();
            state.world = world;
            machines.put(key, state);
            coverage.remove(level.dimension());
        } else if (state.world != world) {
            state.world = world;
            coverage.remove(level.dimension());
        }
        return state;
    }

    // 拆除时同时失效覆盖索引，剩余库存仍由方块交还玩家。
    public State remove(ServerLevel level, BlockPos pos) {
        State state = machines.remove(new Key(level.dimension(), pos));
        if (state != null) coverage.remove(level.dimension());
        return state;
    }

    // 按维度与区块建立覆盖索引；这里只展开区块坐标，不读取或加载地形。
    private Map<Long, List<Key>> coverage(ResourceKey<Level> dimension) {
        int normalRange = rangeChunks(false);
        int worldRange = rangeChunks(true);
        if (indexedClentaminatorRange != normalRange || indexedTerraformerRange != worldRange) {
            coverage.clear();
            indexedClentaminatorRange = normalRange;
            indexedTerraformerRange = worldRange;
        }
        return coverage.computeIfAbsent(dimension, key -> {
            Map<Long, List<Key>> chunks = new HashMap<>();
            machines.forEach((machine, state) -> {
                if (!machine.dimension.equals(dimension)) return;
                int range = rangeChunks(state.world);
                int startX = (machine.pos.getX() >> 4) - range / 2;
                int startZ = (machine.pos.getZ() >> 4) - range / 2;
                for (int x = startX; x < startX + range; x++) {
                    for (int z = startZ; z < startZ + range; z++) {
                        chunks.computeIfAbsent(ChunkPos.asLong(x, z), ignored -> new ArrayList<>()).add(machine);
                    }
                }
            });
            return chunks;
        });
    }

    // 只查询目标区块的候选机器；范围重叠时选择距离最近的有效机器，同距按坐标排序。
    public Solution solutionAt(ServerLevel level, BlockPos pos) {
        List<Key> candidates = coverage(level.dimension()).get(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
        if (candidates == null) return null;
        Key selected = null;
        Solution solution = null;
        double distance = Double.POSITIVE_INFINITY;
        for (Key key : candidates) {
            State state = machines.get(key);
            if (state == null || !state.enabled) continue;
            if (level.hasChunkAt(key.pos) && !(level.getBlockState(key.pos).getBlock() instanceof EnvironmentalMachineBlock)) {
                // tick() 会清理失效机器；查询期间不修改正在遍历的机器集合。
                continue;
            }
            if (!power(level, key, state)) continue;
            double candidate = key.pos.distSqr(pos);
            if (candidate < distance || (candidate == distance && (selected == null || key.pos.asLong() < selected.pos.asLong()))) {
                selected = key;
                solution = state.solution;
                distance = candidate;
            }
        }
        return solution;
    }

    // 返回是否受机器维持；即使无需替换方块，也要阻止外来感染覆盖已匹配的环境。
    public boolean maintain(ServerLevel level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos) || !level.isLoaded(pos)) return false;
        Solution solution = solutionAt(level, pos);
        if (solution == null) return false;
        convertAt(level, pos, solution);
        return true;
    }

    private void convertAt(ServerLevel level, BlockPos pos, Solution solution) {
        BlockState source = level.getBlockState(pos);
        if (!source.isAir() && !source.hasBlockEntity()) {
            var target = solution.convert(level, pos, source);
            if (target != source) {
                if (source.getBlock() instanceof BaseDroopingPlantsHeadBlock) {
                    convertVineColumn(level, pos, solution);
                } else {
                    if (source.is(BlockTags.LOGS) || source.is(BlockTags.LEAVES)
                            || level.getBlockState(pos.above()).is(BlockTags.LOGS))
                        convertTree(level, pos, solution);
                    level.setBlock(pos, target, 2);
                }
            }
            convertBiome(level, pos, solution);
        }
    }

    // 与自然蔓延共用树木连通规则，且只改造已加载、受同种溶液覆盖的树体。
    // 沿用自然蔓延的连通树思路：普通树按六面相邻，棕榈树还检查对角邻接。
    // 只读取已加载位置，限制单次转换量，避免大树或贴合树冠拖慢世界刻。
    private void convertTree(ServerLevel level, BlockPos origin, Solution solution) {
        ArrayDeque<BlockPos> pending = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        pending.add(origin);
        pending.add(origin.above());
        while (!pending.isEmpty() && visited.size() < TREE_BLOCK_LIMIT) {
            BlockPos pos = pending.removeFirst();
            if (!visited.add(pos) || level.isOutsideBuildHeight(pos) || !level.isLoaded(pos)
                    || solutionAt(level, pos) != solution) continue;
            BlockState source = level.getBlockState(pos);
            if (!source.is(BlockTags.LOGS) && !source.is(BlockTags.LEAVES)) continue;
            boolean palm = source.is(NatureBlocks.PALM_LOG_BLOCKS.LOG.get())
                    || source.is(NatureBlocks.PALM_LOG_BLOCKS.WOOD.get())
                    || source.is(NatureBlocks.PALM_LOG_BLOCKS.STRIPPED_LOG.get())
                    || source.is(NatureBlocks.PALM_LOG_BLOCKS.STRIPPED_WOOD.get())
                    || source.is(NatureBlocks.PALM_LOG_BLOCKS.LEAVES.get());
            BlockState target = solution.convert(level, pos, source);
            if (target != source) level.setBlock(pos, target, 2);
            if (source.is(BlockTags.LEAVES)) convertVineColumn(level, pos.below(), solution);
            if (palm) {
                for (BlockPos neighbor : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)))
                    if (!neighbor.equals(pos)) pending.add(neighbor.immutable());
            } else {
                for (Direction direction : Direction.values()) pending.add(pos.relative(direction));
            }
        }
    }

    // 随机刻仍阻止自然蔓延；机器另作限量采样，使普通刻速下也能持续净化。
    // 命中树冠或任一段垂藤时转换整串，保留年龄、水浸与头/身/尾属性。
    private void convertVineColumn(ServerLevel level, BlockPos hit, Solution solution) {
        BlockPos top = hit;
        for (int i = 0; i < VINE_LENGTH_LIMIT && level.isLoaded(top.above())
                && level.getBlockState(top.above()).getBlock() instanceof BaseDroopingPlantsHeadBlock; i++)
            top = top.above();
        for (int i = 0; i < VINE_LENGTH_LIMIT && level.isLoaded(top) && !level.isOutsideBuildHeight(top); i++, top = top.below()) {
            BlockState source = level.getBlockState(top);
            if (!(source.getBlock() instanceof BaseDroopingPlantsHeadBlock) || solutionAt(level, top) != solution)
                break;
            BlockState target = solution.convert(level, top, source);
            if (target != source) level.setBlock(top, target, 2);
        }
    }

    private void activeConvert(ServerLevel level, Key key, State state) {
        int range = rangeChunks(state.world);
        int startX = (key.pos.getX() >> 4) - range / 2;
        int startZ = (key.pos.getZ() >> 4) - range / 2;
        for (int i = 0; i < ACTIVE_SAMPLES_PER_TICK; i++) {
            int chunkX = startX + level.random.nextInt(range);
            int chunkZ = startZ + level.random.nextInt(range);
            LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
            if (chunk == null) continue;
            int localX = level.random.nextInt(CHUNK_WIDTH);
            int localZ = level.random.nextInt(CHUNK_WIDTH);
            int surface = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, localX, localZ) - 1;
            int y = i < ACTIVE_SAMPLES_PER_TICK * 2 / 3
                    ? surface - level.random.nextInt(4)
                    : Mth.nextInt(level.random, level.getMinBuildHeight(), Math.max(level.getMinBuildHeight(), surface));
            BlockPos sample = new BlockPos(chunkX * CHUNK_WIDTH + localX, y, chunkZ * CHUNK_WIDTH + localZ);
            if (solutionAt(level, sample) == state.solution)
                convertAt(level, sample, state.solution);
        }
    }

    // 计费独立于随机刻；查询和世界刻共用入口，避免在到期边界继续免费维持。
    private boolean power(ServerLevel level, Key key, State state) {
        Solution previous = state.solution;
        int count = state.stored();
        boolean wasEnabled = state.enabled;
        boolean powered = state.power(level.getDayTime());
        if (previous != state.solution || count != state.stored() || wasEnabled != state.enabled)
            sync(level, key.pos);
        return powered;
    }

    // 世界刻计费、同步状态并限量转换已加载地形；不强制加载覆盖范围内的区块。
    public void tick(ServerLevel level) {
        Iterator<Map.Entry<Key, State>> iterator = machines.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            Key key = entry.getKey();
            if (!key.dimension.equals(level.dimension())) continue;
            State state = entry.getValue();
            if (level.hasChunkAt(key.pos)) {
                if (!(level.getBlockState(key.pos).getBlock() instanceof EnvironmentalMachineBlock block)) {
                    iterator.remove();
                    coverage.remove(level.dimension());
                    continue;
                }
                if (state.world != block.world) {
                    state.world = block.world;
                    coverage.remove(level.dimension());
                }
            }
            if (power(level, key, state)) activeConvert(level, key, state);
            if (level.hasChunkAt(key.pos) && level.getGameTime() >= state.nextStatusSync) {
                state.nextStatusSync = level.getGameTime() + STATUS_SYNC_INTERVAL;
                sync(level, key.pos);
            }
        }
        // 同一区块在一秒内的多次群系修改合并发送，避免每次随机刻都发包。
        if (level.getGameTime() % STATUS_SYNC_INTERVAL == 0) {
            Set<Long> updates = biomeUpdates.remove(level.dimension());
            if (updates != null) {
                for (long packed : updates) {
                    ChunkPos pos = new ChunkPos(packed);
                    LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);
                    if (chunk == null) continue;
                    var packet = ClientboundChunksBiomesPacket.forChunks(List.of(chunk));
                    for (var player : level.getChunkSource().chunkMap.getPlayers(pos, false))
                        player.connection.send(packet);
                }
            }
        }
    }

    // 随实际维持位置修改一个群系采样格，不再整区块反复填充群系。
    private void convertBiome(ServerLevel level, BlockPos pos, Solution solution) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk == null) return;
        int x = (pos.getX() >> 2) & 3;
        int y = (pos.getY() >> 2) & 3;
        int z = (pos.getZ() >> 2) & 3;
        var biomes = chunk.getSection(chunk.getSectionIndex(pos.getY())).getBiomes();
        if (!(biomes instanceof PalettedContainer<Holder<Biome>> mutable)) return;
        Holder<Biome> source = biomes.get(x, y, z);
        String id = source.unwrapKey().map(k -> k.location().getPath()).orElse("");
        boolean desert = id.contains("desert") || id.contains("badlands");
        boolean snow = id.contains("snow") || id.contains("tundra") || id.contains("frozen") || id.contains("ice");
        ResourceKey<Biome> target = switch (solution) {
            case PURPLE ->
                    desert ? ModBiomes.THE_CORRUPTION_DESERT : snow ? ModBiomes.THE_CORRUPTION_TUNDRA : ModBiomes.THE_CORRUPTION;
            case RED ->
                    desert ? ModBiomes.THE_CRIMSON_DESERT : snow ? ModBiomes.THE_CRIMSON_TUNDRA : ModBiomes.THE_CRIMSON;
            case BLUE ->
                    desert ? ModBiomes.THE_HALLOW_DESERT : snow ? ModBiomes.THE_HALLOW_TUNDRA : ModBiomes.THE_HALLOW;
            case DARK_BLUE -> ModBiomes.GLOWING_MUSHROOM;
            case YELLOW -> Biomes.DESERT;
            case WHITE -> Biomes.SNOWY_PLAINS;
            case BROWN -> Biomes.FOREST;
            case GREEN ->
                    desert ? Biomes.DESERT : snow ? Biomes.SNOWY_PLAINS : id.contains("mushroom") || id.contains("jungle") ? Biomes.JUNGLE : Biomes.FOREST;
        };
        Holder<Biome> replacement = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(target);
        if (replacement.equals(source)) return;
        mutable.set(x, y, z, replacement);
        chunk.setUnsaved(true);
        biomeUpdates.computeIfAbsent(level.dimension(), ignored -> new HashSet<>()).add(chunk.getPos().toLong());
    }

    public static void sync(ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) return;
        for (BlockPos part : List.of(pos, pos.above())) {
            if (level.getBlockEntity(part) instanceof EnvironmentalMachineBlock.BEntity entity) {
                entity.refresh();
                level.sendBlockUpdated(part, entity.getBlockState(), entity.getBlockState(), 2);
            }
        }
    }

    @Override
    public void encode(CompoundTag tag) {
        ListTag list = new ListTag();
        machines.forEach((key, state) -> {
            CompoundTag data = new CompoundTag();
            data.putString("Dimension", key.dimension.location().toString());
            data.putLong("Position", key.pos.asLong());
            if (state.solution != null) data.putString("Solution", state.solution.id());
            data.putIntArray("Slots", state.slots);
            ListTag solutions = new ListTag();
            for (Solution solution : state.slotSolutions)
                solutions.add(StringTag.valueOf(solution == null ? "" : solution.id()));
            data.put("SlotSolutions", solutions);
            data.putInt("ActiveSlot", state.activeSlot);
            data.putLong("PaidUntil", state.paidUntil);
            data.putLong("LastTime", state.lastTime);
            data.putBoolean("World", state.world);
            data.putBoolean("Enabled", state.enabled);
            list.add(data);
        });
        tag.put("Machines", list);
    }

    // 单例在每次读取前清空，避免切换存档时串入上一个世界的机器。
    @Override
    public void decode(CompoundTag tag) {
        clear();
        for (Tag element : tag.getList("Machines", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) element;
            ResourceLocation dimension = ResourceLocation.tryParse(data.getString("Dimension"));
            if (dimension == null) continue;
            State state = new State();
            try {
                if (data.contains("Solution"))
                    state.solution = Solution.valueOf(data.getString("Solution").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {continue;}
            int[] slots = data.getIntArray("Slots");
            ListTag solutions = data.getList("SlotSolutions", Tag.TAG_STRING);
            for (int slot = 0; slot < Math.min(SLOT_COUNT, slots.length); slot++) {
                state.slots[slot] = Mth.clamp(slots[slot], 0, CAPACITY);
                // 旧存档只有一个溶液类型，按原有库存逐槽迁移，不丢失物品。
                if (!data.contains("SlotSolutions")) state.slotSolutions[slot] = state.solution;
                else if (slot < solutions.size()) {
                    try {
                        state.slotSolutions[slot] = Solution.valueOf(solutions.getString(slot).toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (state.slotSolutions[slot] == null) state.slots[slot] = 0;
            }
            state.activeSlot = Mth.clamp(data.getInt("ActiveSlot"), -1, SLOT_COUNT - 1);
            state.paidUntil = data.getLong("PaidUntil");
            state.lastTime = data.getLong("LastTime");
            state.world = data.getBoolean("World");
            state.enabled = data.getBoolean("Enabled");
            machines.put(new Key(ResourceKey.create(Registries.DIMENSION, dimension), BlockPos.of(data.getLong("Position"))), state);
        }
    }

    @Override
    public String serializeKey() {return "confluence:environmental_machines";}

    @Override
    public void clear() {
        machines.clear();
        coverage.clear();
        biomeUpdates.clear();
        indexedClentaminatorRange = -1;
        indexedTerraformerRange = -1;
    }
}
