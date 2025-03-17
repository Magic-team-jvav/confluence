package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.ModBlocks;
import org.joml.Vector3d;

import java.util.*;

import static org.confluence.mod.util.StructureUtils.*;

public class ShimmerLakeStructure extends Structure {
    public static final MapCodec<ShimmerLakeStructure> CODEC = simpleCodec(ShimmerLakeStructure::new);
    public static final Map<Integer, ResourceLocation> feature = new HashMap<>(
            Map.of(
                    0, Confluence.asResource("amber_tree"),
                    1, Confluence.asResource("diamond_tree"),
                    2, Confluence.asResource("emerald_tree"),
                    3, Confluence.asResource("ruby_tree"),
                    4, Confluence.asResource("sapphire_tree"),
                    5, Confluence.asResource("topaz_tree"),
                    6, Confluence.asResource("tr_amethyst_tree")
            )
    );

    protected ShimmerLakeStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int lowestY = getLowestY(context, 16, 16);
        if (lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            ChunkPos startChunk = context.chunkPos();
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            centerPos = new BlockPos(centerPos.getX(), random.nextInt(-10, 20), centerPos.getZ());
            //todo ---------------------------------------------------↑↑↑↑↑↑↑↑↑↑ y轴绝对范围
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Map<BlockPos, ResourceLocation> featureMap = new HashMap<>();

            List<Vector3d> posOut = ellipsoidPos(36, 12, 36, centerPos, 0.03F, random);
            List<Vector3d> posIn = ellipsoidPos(24, 9, 24, centerPos, 0.03F, random);
            lineSet(posOut, 11.5, 11.5, 1, true, blockMap);
            lineSet(posOut, 8.5, 8.5, 0, 1, true, blockMap, centerPos.getY() - 2);
            lineSet(posIn, 6.5, 6.5, 0, 2, true, blockMap, centerPos.getY() - 2);
            treeSet(centerPos.offset(0, -1, 0), 34, random, featureMap);

            GridPiece.addPieces(blockMap, startChunk, lowestY, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    Blocks.STONE.defaultBlockState(),
                    ModBlocks.SHIMMER.get().defaultBlockState()
            ), featureMap, builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.SHIMMER_LAKE.get();
    }

    private static void treeSet(BlockPos centerPos, double radius, WorldgenRandom random, Map<BlockPos, ResourceLocation> featureMap) {
        int rotate = random.nextInt(14, 17);
        float rStep = Mth.PI * 2 / rotate;
        for (int i = 0; i < rotate; i++) {
            featureMap.put(centerPos.offset(((int) (Mth.cos(rStep * i) * radius) + random.nextInt(-3, 4)), 0, ((int) (Mth.sin(rStep * i) * radius) + random.nextInt(-3, 4))), feature.get(random.nextInt(7)));
        }
    }
}
