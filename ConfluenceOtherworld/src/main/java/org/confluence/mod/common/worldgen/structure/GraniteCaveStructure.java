package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.confluence.lib.util.StructureUtils.ellipsoid;
import static org.confluence.lib.util.StructureUtils.getHeight;
import static org.confluence.lib.util.VectorUtils.ellipsoidPos;
import static org.confluence.lib.util.VectorUtils.frustumSetPos;

public class GraniteCaveStructure extends Structure {
    public static final MapCodec<GraniteCaveStructure> CODEC = simpleCodec(GraniteCaveStructure::new);

    public GraniteCaveStructure(StructureSettings settings) {
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
            BlockPos centerPos = startChunk.getMiddleBlockPosition(random.nextInt(-20, 21));
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Vector3d start = new Vector3d(centerPos.getX() + random.nextInt(-3, 4), centerPos.getY() + 20, centerPos.getZ() + random.nextInt(-3, 4));
            Vector3d end = new Vector3d(centerPos.getX() + random.nextInt(-3, 4), centerPos.getY() - 20, centerPos.getZ() + random.nextInt(-3, 4));
            BlockPos checkPos;
            int blockstate = 1;

            List<Vector3d> listPos = frustumSetPos(start, end, random.nextInt(53, 56) + 0.5, random.nextInt(53, 56) + 0.5, 0.0002F, random);
            List<Vector3d> listPos1 = new ArrayList<>();
            List<Vector3d> listPos2 = new ArrayList<>();
            List<Vector3d> listPos3 = new ArrayList<>();
            for (Vector3d vector3d : listPos) {
                listPos1.addAll(ellipsoidPos(15.5, 9.5, 15.5, VectorUtils.fromVector3d(vector3d), 0.002F, random));
            }

            for (Vector3d vector3d : listPos1) {
                checkPos = VectorUtils.fromVector3d(vector3d);
                listPos2.addAll(ellipsoidPos(15.5, 4.5, 15.5, checkPos, 0.002F, random));
            }
            for (Vector3d vector3d : listPos2) {
                checkPos = VectorUtils.fromVector3d(vector3d);
                ellipsoid(13.5, 4.5, 13.5, checkPos, blockstate, true, blockMap);
            }
            for (Vector3d vector3d : listPos2) {
                checkPos = VectorUtils.fromVector3d(vector3d);
                ellipsoid(random.nextInt(4, 12) + 0.5, 2.5, random.nextInt(4, 12) + 0.5, checkPos, 0, true, blockMap);
            }

            for (Vector3d vector3d : listPos2) {
                if (0.001F > random.nextFloat()) {
                    listPos3.addAll(ellipsoidPos(random.nextInt(10, 30) + 0.5, 1.5, random.nextInt(10, 30) + 0.5, VectorUtils.fromVector3d(vector3d), 0.05F, random));
                }
            }
            for (Vector3d vector3d : listPos3) {
                checkPos = VectorUtils.fromVector3d(vector3d);
                ellipsoid(random.nextInt(4, 12) + 0.5, 0.1, random.nextInt(4, 12) + 0.5, checkPos, blockstate, true, blockMap);
            }

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.GRANITE.get().defaultBlockState(),
                    Blocks.GRANITE.defaultBlockState()
            ), builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.GRANITE_CAVE.get();
    }
}
