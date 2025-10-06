package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.world.level.block.LeavesBlock.PERSISTENT;
import static org.confluence.lib.util.StructureUtils.getHeight;
import static org.confluence.lib.util.StructureUtils.rectangular;

public class LivingMahoganyTreeStructure extends Structure {
    public static final MapCodec<LivingMahoganyTreeStructure> CODEC = simpleCodec(LivingMahoganyTreeStructure::new);
    public static final ResourceKey<ConfiguredFeature<?, ?>> LIVING_IVY_CHESTS = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "living_ivy_chests");

    protected LivingMahoganyTreeStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        int lowestY = getHeight(x, z, context);
        if (x * x + z * z <= 400 * 400 || lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            BaseStructures.livingTree(
                    centerPos,
                    blockMap,
                    random,
                    random.nextInt(36, 42),
                    1,
                    2.5D,
                    0.8D,
                    1,
                    14,
                    2,
                    -12,
                    1,
                    1,
                    1,
                    1,
                    5,
                    1,
                    7,
                    5,
                    -10,
                    2,
                    1,
                    1,
                    1,
                    4,
                    2,
                    true,
                    random.nextInt(36, 40),
                    0,
                    0,
                    0,
                    0,
                    false,
                    9,
                    3,
                    13,
                    4,
                    2,
                    1,
                    0.20F,
                    0.75F,
                    2,
                    3
            );
            rectangular(centerPos.offset(1, 0, 1), centerPos.offset(-1, 1, -1), 0, blockMap, 0);

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.WOOD.get().defaultBlockState(),
                    NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LEAVES.get().defaultBlockState().setValue(PERSISTENT, true),
                    OreBlocks.SPORE_ROOT_BLOCK.get().defaultBlockState()
            ), Map.of(
                    centerPos, LIVING_IVY_CHESTS.location()
            ), builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.LIVING_MAHOGANY_TREE.get();
    }
}
