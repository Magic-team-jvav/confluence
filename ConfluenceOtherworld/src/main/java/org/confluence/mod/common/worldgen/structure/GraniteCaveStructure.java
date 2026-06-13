package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
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
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.confluence.lib.util.LibGeometryUtils.ellipsoidPos;
import static org.confluence.lib.util.LibGeometryUtils.frustumSetPos;
import static org.confluence.lib.util.LibStructureUtils.ellipsoid;
import static org.confluence.lib.util.LibStructureUtils.getHeight;

public class GraniteCaveStructure extends Structure {
    public static final Codec<GraniteCaveStructure> CODEC = simpleCodec(GraniteCaveStructure::new);

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
            Vector3f start = new Vector3f(centerPos.getX() + random.nextInt(-3, 4), centerPos.getY() + 20, centerPos.getZ() + random.nextInt(-3, 4));
            Vector3f end = new Vector3f(centerPos.getX() + random.nextInt(-3, 4), centerPos.getY() - 20, centerPos.getZ() + random.nextInt(-3, 4));
            BlockPos checkPos;
            int blockstate = 1;

            List<Vector3f> listPos = frustumSetPos(start, end, random.nextInt(38, 41) + 0.5F, random.nextInt(38, 41) + 0.5F, 0.0002F, random);
            List<Vector3f> listPos1 = new ArrayList<>();
            List<Vector3f> listPos2 = new ArrayList<>();
            List<Vector3f> listPos3 = new ArrayList<>();
            for (Vector3f vector3d : listPos) {
                listPos1.addAll(ellipsoidPos(13.5F, 9.5F, 13.5F, LibMathUtils.fromVector3f(vector3d), 0.002F, random));
            }

            for (Vector3f vector3d : listPos1) {
                checkPos = LibMathUtils.fromVector3f(vector3d);
                listPos2.addAll(ellipsoidPos(11.5F, 6.5F, 11.5F, checkPos, 0.002F, random));
            }
            for (Vector3f vector3d : listPos2) {
                checkPos = LibMathUtils.fromVector3f(vector3d);
                ellipsoid(9.5F, 4.5F, 9.5F, checkPos, blockstate, true, blockMap);
            }
            for (Vector3f vector3d : listPos2) {
                checkPos = LibMathUtils.fromVector3f(vector3d);
                ellipsoid(random.nextInt(4, 9) + 0.5F, 2.5F, random.nextInt(4, 9) + 0.5F, checkPos, 0, true, blockMap);
            }

            for (Vector3f vector3d : listPos2) {
                if (0.001F > random.nextFloat()) {
                    listPos3.addAll(ellipsoidPos(random.nextInt(8, 13) + 0.5F, 1.5F, random.nextInt(8, 13) + 0.5F, LibMathUtils.fromVector3f(vector3d), 0.05F, random));
                }
            }
            for (Vector3f vector3d : listPos3) {
                checkPos = LibMathUtils.fromVector3f(vector3d);
                ellipsoid(random.nextInt(4, 9) + 0.5F, 0.1F, random.nextInt(4, 9) + 0.5F, checkPos, blockstate, true, blockMap);
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
