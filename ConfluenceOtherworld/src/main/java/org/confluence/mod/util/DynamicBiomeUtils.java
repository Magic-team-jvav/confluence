package org.confluence.mod.util;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.ReturnException;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.mixed.IPalettedContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class DynamicBiomeUtils {
    public static final int BIOME_THRESHOLD = 256;
    public static final Map<Predicate<BlockState>, BiConsumer<BlockCounts, Integer>> COUNTER = new ImmutableMap.Builder<Predicate<BlockState>, BiConsumer<BlockCounts, Integer>>()
        .put(block -> block.is(ModTags.Blocks.CRIMSON), (counter, count) -> counter.crimson.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.CRIMSON_DESERT), (counter, count) -> counter.crimsonSand.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.CRIMSON_TUNDRA), (counter, count) -> counter.crimsonIce.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.CORRUPTION), (counter, count) -> counter.corrupt.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.CORRUPTED_DESERT), (counter, count) -> counter.corruptSand.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.CORRUPTED_TUNDRA), (counter, count) -> counter.corruptIce.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.HALLOW), (counter, count) -> counter.hallow.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.HALLOW_DESERT), (counter, count) -> counter.hallowSand.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.HALLOW_TUNDRA), (counter, count) -> counter.hallowIce.addAndGet(count))
        .put(block -> block.is(Blocks.SUNFLOWER), (counter, count) -> counter.sunflower.addAndGet(count))
        .put(block -> block.is(ModTags.Blocks.TOMBSTONE), (counter, count) -> counter.tomb.addAndGet(count))
        .build();

    /**
     * 动态群系的优先级，数字小的优先级高
     */
    public static final Object2IntMap<ResourceKey<Biome>> PRIORITY = Util.make(new Object2IntOpenHashMap<>() {{
        this.defRetValue = Integer.MAX_VALUE;
    }}, map -> {
        map.put(ModBiomes.THE_HALLOW_TUNDRA, 0);
        map.put(ModBiomes.THE_CORRUPTION_TUNDRA, 100);
        map.put(ModBiomes.THE_CRIMSON_TUNDRA, 200);
        map.put(ModBiomes.THE_HALLOW_DESERT, 300);
        map.put(ModBiomes.THE_CORRUPTION_DESERT, 400);
        map.put(ModBiomes.THE_CRIMSON_DESERT, 500);
        map.put(ModBiomes.THE_HALLOW, 600);
        map.put(ModBiomes.THE_CORRUPTION, 700);
        map.put(ModBiomes.THE_CRIMSON, 800);
        map.put(ModBiomes.GLOWING_MUSHROOM, 900);
    });

    /**
     * 判断这个区块的纯净形态应该是什么样的
     * <p>
     * 如果原群系包含邪恶：如果纯邪恶则返回平原，否则从纯净中挑一个
     * <p>
     * 如果原群系没有邪恶则返回原群系
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static PalettedContainer<Holder<Biome>> judgeBackupBiome(LevelChunkSection section) {
        PalettedContainer<Holder<Biome>> biomes = (PalettedContainer<Holder<Biome>>) section.getBiomes();
        AtomicReference<Holder<Biome>> pure = new AtomicReference<>();
        AtomicBoolean hasEvil = new AtomicBoolean(false);
        try {
            biomes.getAll(biome -> {
                if (biome.is(ModTags.Biomes.SPREADABLE)) {
                    hasEvil.set(true);
                } else {
                    pure.set(biome);
                }
                if (pure.get() != null && hasEvil.get()) {
                    throw new ReturnException();
                }
            });
        } catch (ReturnException ignore) {
        }
        if (hasEvil.get()) {
            if (pure.get() != null) {
                return ((IPalettedContainer<Holder<Biome>>) biomes).confluence$recreateSingle(pure.get());
            }
            return ((IPalettedContainer<Holder<Biome>>) biomes).confluence$recreateSingle(((IChunkSection) section).confluence$getBiomeByKey(Biomes.PLAINS));
        }
        return biomes;
    }

    /**
     * @return 平衡的结果，纯净返回null
     */
    public static Holder<Biome> judgeSection(LevelChunkSection section) {
        IChunkSection biomeSource = (IChunkSection) section;
        BlockCounts counts = biomeSource.confluence$getBlockCounts();
        int sunflower = counts.sunflower.get() * 64;
        int crimson = Math.max(0, counts.crimson.get() - sunflower);
        int corrupt = Math.max(0, counts.corrupt.get() - sunflower);
        int hallow = counts.hallow.get();

        // (假设)同时存在400个猩红块和400个腐化块的时候，只要400个神圣块就能完全抵消，邪恶不会相加
        int evil = Math.max(crimson, corrupt);
        crimson -= hallow;
        corrupt -= hallow;
        hallow -= evil;

        if (corrupt >= BIOME_THRESHOLD && corrupt >= crimson) {
            if (counts.corruptSand.get() >= BIOME_THRESHOLD || section.getBiomes().maybeHas(biomeHolder -> biomeHolder.is(Biomes.DESERT))) {
                return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_CORRUPTION_DESERT);
            } else if (counts.corruptIce.get() >= BIOME_THRESHOLD || section.getBiomes().maybeHas(biomeHolder -> biomeHolder.is(BiomeTags.SPAWNS_SNOW_FOXES))) {
                return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_CORRUPTION_TUNDRA);
            }
            return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_CORRUPTION);
        } else if (crimson >= BIOME_THRESHOLD) {
            if (counts.crimsonSand.get() >= BIOME_THRESHOLD || section.getBiomes().maybeHas(biomeHolder -> biomeHolder.is(Biomes.DESERT))) {
                return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_CRIMSON_DESERT);
            } else if (counts.crimsonIce.get() >= BIOME_THRESHOLD || section.getBiomes().maybeHas(biomeHolder -> biomeHolder.is(BiomeTags.SPAWNS_SNOW_FOXES))) {
                return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_CRIMSON_TUNDRA);
            }
            return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_CRIMSON);
        } else if (hallow >= BIOME_THRESHOLD) {
            if (counts.hallowSand.get() >= BIOME_THRESHOLD || section.getBiomes().maybeHas(biomeHolder -> biomeHolder.is(Biomes.DESERT))) {
                return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_HALLOW_DESERT);
            } else if (counts.hallowIce.get() >= BIOME_THRESHOLD || section.getBiomes().maybeHas(biomeHolder -> biomeHolder.is(BiomeTags.SPAWNS_SNOW_FOXES))) {
                return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_HALLOW_TUNDRA);
            }
            return biomeSource.confluence$getBiomeByKey(ModBiomes.THE_HALLOW);
        } else {
            return null;
        }
    }

    public static LevelChunkSection getSection(Level level, BlockPos pos) {
        return level.getChunk(pos).getSection(level.getSectionIndex(pos.getY()));
    }

    public static IChunkSection getISection(Level level, BlockPos pos) {
        return (IChunkSection) getSection(level, pos);
    }

    /**
     * 为这个区块应用动态群系，从底部判断到顶部，应用动态群系的规则
     */
    @SuppressWarnings("unchecked")
    public static void applyDynamicBiome(ChunkAccess chunk) {
        LibUtils.devRun(() -> Confluence.LOGGER.debug("protoToLevel {}", chunk));
        Holder<Biome> belowBiome = null;
        for (LevelChunkSection section : chunk.getSections()) {
            section.recalcBlockCounts();
            Holder<Biome> currentBiome = judgeSection(section);
            if (currentBiome != null) {
                ((IChunkSection) section).confluence$setBiomes(((IPalettedContainer<Holder<Biome>>) section.getBiomes()).confluence$recreateSingle(currentBiome));
            } else if (belowBiome != null) {
                ((IChunkSection) section).confluence$setBiomes(((IPalettedContainer<Holder<Biome>>) section.getBiomes()).confluence$recreateSingle(belowBiome));
            }
            belowBiome = currentBiome;
        }
    }
}
