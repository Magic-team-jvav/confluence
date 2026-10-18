package org.confluence.mod.common.data.saved;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.LevelChunk;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.common.block.functional.terraform.EnvironmentalMachineBlock;
import org.confluence.mod.common.block.functional.terraform.MachineConversionTable;
import org.confluence.mod.common.block.functional.terraform.Solution;
import org.confluence.mod.common.init.ModBiomes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// 机器的库存、已付费工作时间和扫描游标统一写入 level.dat，不与方块实体重复保存。
public enum EnvironmentalMachines implements IGlobalData {
    INSTANCE;
    public static final int RANGE_CHUNKS = 3;
    // 每个储液槽最多保存一组溶液。
    public static final int CAPACITY = 64;
    public static final int GLOBAL_SCAN_BUDGET = 4096;
    public static final int MACHINE_SCAN_BUDGET = 1024;
    public static final int GLOBAL_CHANGE_BUDGET = 128;
    public static final int MACHINE_CHANGE_BUDGET = 32;
    private final Map<Key, State> machines = new LinkedHashMap<>();
    private int nextMachine;

    private record Key(ResourceKey<Level> dimension, BlockPos pos) {}

    private record Work(int scanned, int changed) {}

    public static final class State {
        public Solution solution;
        public final int[] slots = new int[3];
        public int activeSlot = -1;
        public long cycle;

        public int stored() {return slots[0] + slots[1] + slots[2];}

        public long paidUntil = Long.MIN_VALUE;
        public long lastTime = Long.MIN_VALUE;
        public int cursor;
        public boolean world;
        public boolean enabled;

        // 预付一个溶液的工作时长；时间倒退只平移余量，避免重复收费或无限延长。
        public boolean power(long now) {
            // 停机期间保留已付工作时长，不消耗库存或推进改造。
            if (!enabled) {
                if (lastTime != Long.MIN_VALUE && paidUntil > lastTime)
                    paidUntil = now + paidUntil - lastTime;
                lastTime = now;
                return false;
            }
            if (lastTime != Long.MIN_VALUE && now < lastTime && paidUntil > lastTime)
                paidUntil = now + paidUntil - lastTime;
            lastTime = now;
            if (solution == null) return false;
            if (now < paidUntil) return true;
            if (stored() == 0) {
                solution = null;
                return false;
            }
            activeSlot = (activeSlot + 1) % 3;
            while (slots[activeSlot] == 0) activeSlot = (activeSlot + 1) % 3;
            slots[activeSlot]--;
            cycle++;
            paidUntil = now + (world ? 24000L : 8000L);
            return true;
        }
    }

    public State get(ServerLevel level, BlockPos pos, boolean world) {
        State state = machines.computeIfAbsent(new Key(level.dimension(), pos.immutable()), key -> new State());
        state.world = world;
        return state;
    }

    // 拆除时移除全局记录，由方块负责把剩余库存还给玩家。
    public State remove(ServerLevel level, BlockPos pos) {return machines.remove(new Key(level.dimension(), pos));}

    // 所有机器共享扫描预算，轮转调度；只操作已经加载的完整区块，不强制加载或生成邻区块。
    public void tick(ServerLevel level) {
        List<Map.Entry<Key, State>> entries = machines.entrySet().stream().filter(e -> e.getKey().dimension.equals(level.dimension())).toList();
        if (entries.isEmpty()) return;
        int budget = GLOBAL_SCAN_BUDGET;
        int changes = GLOBAL_CHANGE_BUDGET;
        int start = Math.floorMod(nextMachine, entries.size());
        int visited = 0;
        for (; visited < entries.size() && budget > 0 && changes > 0; visited++) {
            var entry = entries.get((start + visited) % entries.size());
            Key key = entry.getKey();
            State state = entry.getValue();
            if (!level.hasChunkAt(key.pos)) continue;
            if (!(level.getBlockState(key.pos).getBlock() instanceof EnvironmentalMachineBlock block)) {
                machines.remove(key);
                continue;
            }
            state.world = block.world;
            Solution previous = state.solution;
            int count = state.stored();
            boolean powered = state.power(level.getDayTime());
            if (previous != state.solution || count != state.stored()) sync(level, key.pos);
            if (powered) {
                Work work = scan(level, key.pos, state, Math.min(MACHINE_SCAN_BUDGET, budget), Math.min(MACHINE_CHANGE_BUDGET, changes));
                budget -= work.scanned;
                changes -= work.changed;
            }
        }
        nextMachine = start + visited;
    }

    // 与肉后转换一样按区块推进；进一步拆成固定数量的方块，防止整区块转换卡住主线程。
    private Work scan(ServerLevel level, BlockPos origin, State state, int budget, int changeBudget) {
        int height = level.getHeight();
        int chunkVolume = height * 256;
        int total = RANGE_CHUNKS * RANGE_CHUNKS * chunkVolume;
        int scanned = 0;
        int changed = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        while (scanned < budget && changed < changeBudget) {
            state.cursor = Math.floorMod(state.cursor, total);
            int chunkIndex = state.cursor / chunkVolume;
            int cx = (origin.getX() >> 4) + chunkIndex % RANGE_CHUNKS - RANGE_CHUNKS / 2;
            int cz = (origin.getZ() >> 4) + chunkIndex / RANGE_CHUNKS - RANGE_CHUNKS / 2;
            LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
            if (chunk == null) {
                state.cursor = (chunkIndex + 1) * chunkVolume % total;
                scanned += Math.min(256, budget - scanned);
                continue;
            }
            int local = state.cursor % chunkVolume;
            pos.set((cx << 4) + (local & 15), level.getMinBuildHeight() + (local >> 8), (cz << 4) + ((local >> 4) & 15));
            var source = chunk.getBlockState(pos);
            if (!source.isAir() && !source.hasBlockEntity()) {
                var target = MachineConversionTable.INSTANCE.convert(state.solution, source);
                if (target != source && level.setBlock(pos, target, 2)) changed++;
            }
            state.cursor++;
            scanned++;
            if (state.cursor % chunkVolume == 0) convertBiomes(level, chunk, state.solution);
        }
        return new Work(scanned, changed);
    }

    // 完成一个区块后刷新群系容器并单独发送群系包，不重发整张区块和其中的方块实体。
    private void convertBiomes(ServerLevel level, LevelChunk chunk, Solution solution) {
        var registry = level.registryAccess().registryOrThrow(Registries.BIOME);
        chunk.fillBiomesFromNoise((x, y, z, sampler) -> {
            Holder<Biome> source = chunk.getNoiseBiome(x, y, z);
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
            return registry.getHolderOrThrow(target);
        }, level.getChunkSource().randomState().sampler());
        chunk.setUnsaved(true);
        var packet = ClientboundChunksBiomesPacket.forChunks(List.of(chunk));
        for (var player : level.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false))
            player.connection.send(packet);
    }

    public static void sync(ServerLevel level, BlockPos pos) {
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
            data.putLong("Cycle", state.cycle);
            data.putIntArray("Slots", state.slots);
            data.putInt("ActiveSlot", state.activeSlot);
            data.putLong("PaidUntil", state.paidUntil);
            data.putLong("LastTime", state.lastTime);
            data.putInt("Cursor", state.cursor);
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
            for (int i = 0; i < Math.min(3, slots.length); i++)
                state.slots[i] = Math.max(0, Math.min(CAPACITY, slots[i]));
            state.cycle = data.getLong("Cycle");
            state.activeSlot = Math.max(-1, Math.min(2, data.getInt("ActiveSlot")));
            state.paidUntil = data.getLong("PaidUntil");
            state.lastTime = data.getLong("LastTime");
            state.cursor = Math.max(0, data.getInt("Cursor"));
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
        nextMachine = 0;
    }
}
