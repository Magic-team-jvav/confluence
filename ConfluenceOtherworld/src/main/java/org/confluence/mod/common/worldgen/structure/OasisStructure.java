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
import org.confluence.lib.util.LibVectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.confluence.lib.util.LibGeometryUtils.ballPos;
import static org.confluence.lib.util.LibGeometryUtils.frustumSetPos;
import static org.confluence.lib.util.LibStructureUtils.getHeight;
import static org.confluence.lib.util.LibStructureUtils.lineSet;

public class OasisStructure extends Structure {
    public static final MapCodec<OasisStructure> CODEC = simpleCodec(OasisStructure::new);

    protected OasisStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        int averageY = 0;
        for (int x1 = -1; x1 < 2; x1++) {
            for (int z1 = -1; z1 < 2; z1++) {
                averageY += getHeight(x + 16 * x1, z + 16 * z1, context);
            }
        }
        averageY /= 9;
        int lowestY = averageY;
        if (x * x + z * z <= 400 * 400 || lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Vector3d start = new Vector3d(centerPos.getX() + random.nextInt(-2, 3), centerPos.getY() -20, centerPos.getZ() + random.nextInt(-2, 3));
            Vector3d end = new Vector3d(centerPos.getX(), centerPos.getY() + 1, centerPos.getZ());
            List<Vector3d> listPos = frustumSetPos(start, end, 60.5, 40.5, 0.2F, random);
            Map<BlockPos, ResourceLocation> feature = new HashMap<>();
            BlockPos checkPos;
            for (Vector3d vector3d : listPos) {
                checkPos = LibVectorUtils.fromVector3d(vector3d).offset(0, 4, 0);
                if ((checkPos.getY() > centerPos.getY()) && (0.05F > random.nextFloat()) && (new Vector3d(centerPos.getX(), 0, centerPos.getZ()).distance(new Vector3d(checkPos.getX(), 0, checkPos.getZ())) > 32)) feature.put(checkPos, Confluence.asResource("palm_tree"));
            }

            lineSet(listPos, 3.5, 3.5, 2, true, blockMap);
            List<Vector3d> listVct = ballPos(18.5, centerPos.offset(0, 18, 0), 0.02F, random);
            for (Vector3d vector3d : listVct) {
                ball(15.5, LibVectorUtils.fromVector3d(vector3d), 0, 3, true, blockMap, centerPos.getY() - 1);
            }

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    Blocks.SAND.defaultBlockState(),
                    NatureBlocks.MOISTENED_SAND_BLOCK.get().defaultBlockState(),
                    Blocks.WATER.defaultBlockState()
            ), feature, builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.OASIS.get();
    }
}
