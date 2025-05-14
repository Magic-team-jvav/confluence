package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.ModBlocks;
import org.joml.Vector3d;

import java.util.*;

import static org.confluence.lib.util.StructureUtils.*;
import static org.confluence.lib.util.VectorUtils.ellipsoidPos;
import static org.confluence.lib.util.VectorUtils.roundPos;

public class ShimmerLakeStructure extends Structure {
    public static final MapCodec<ShimmerLakeStructure> CODEC = simpleCodec(ShimmerLakeStructure::new);
    private static final ResourceLocation[] feature = new ResourceLocation[]{
            Confluence.asResource("amber_tree"),
            Confluence.asResource("diamond_tree"),
            Confluence.asResource("emerald_tree"),
            Confluence.asResource("ruby_tree"),
            Confluence.asResource("sapphire_tree"),
            Confluence.asResource("topaz_tree"),
            Confluence.asResource("tr_amethyst_tree")
    };

    protected ShimmerLakeStructure(StructureSettings settings) {
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
            BlockPos centerPos = startChunk.getMiddleBlockPosition(random.nextInt(-40, 10));
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Map<BlockPos, ResourceLocation> featureMap = new HashMap<>();
            List<Vector3d> vctPosList = new LinkedList<>();

            List<Vector3d> posOut = ellipsoidPos(36, 12, 36, centerPos, 0.03F, random);
            List<Vector3d> posIn = ellipsoidPos(24, 9, 24, centerPos, 0.03F, random);
            lineSet(posOut, 11.5, 11.5, 1, true, blockMap);
            lineSet(posOut, 8.5, 8.5, 0, 1, true, blockMap, centerPos.getY() - 2);
            lineSet(posIn, 6.5, 6.5, 0, 2, true, blockMap, centerPos.getY() - 2);
            roundPos(centerPos.offset(0, -1, 0), 34, random, vctPosList, 3, random.nextInt(14, 17), 0.0F);
            lineSetFeature(vctPosList, featureMap, feature, random);

            GridPiece.addPieces(blockMap, Lists.newArrayList(
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
}
