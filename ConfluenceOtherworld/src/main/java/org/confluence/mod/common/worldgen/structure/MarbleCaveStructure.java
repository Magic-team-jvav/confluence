package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.joml.Vector3f;

import java.util.*;

import static org.confluence.lib.util.LibGeometryUtils.ellipsoidPos;
import static org.confluence.lib.util.LibGeometryUtils.frustumSetPos;
import static org.confluence.lib.util.LibStructureUtils.ellipsoid;
import static org.confluence.lib.util.LibStructureUtils.getHeight;

public class MarbleCaveStructure extends Structure {
    public static final Codec<MarbleCaveStructure> CODEC = simpleCodec(MarbleCaveStructure::new);
    public static final ResourceKey<ConfiguredFeature<?, ?>> MARBLE_CAVE_POT = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "marble_cave_pot");

    public MarbleCaveStructure(StructureSettings settings) {
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
            Map<BlockPos, ResourceLocation> featureMap = new HashMap<>();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(random.nextInt(-20, 21));
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Vector3f start = new Vector3f(centerPos.getX() + random.nextInt(-3, 4), centerPos.getY() + 10, centerPos.getZ() + random.nextInt(-3, 4));
            Vector3f end = new Vector3f(centerPos.getX() + random.nextInt(-3, 4), centerPos.getY() - 10, centerPos.getZ() + random.nextInt(-3, 4));
            BlockPos checkPos;

            List<Vector3f> listPos = frustumSetPos(start, end, random.nextInt(38, 41) + 0.5F, random.nextInt(38, 41) + 0.5F, 0.0002F, random);
            List<Vector3f> listPos1 = new ArrayList<>();
            List<Vector3f> listPos2 = new ArrayList<>();
            for (Vector3f vector3d : listPos) {
                listPos1.addAll(ellipsoidPos(13.5F, 13.5F, 13.5F, LibMathUtils.fromVector3f(vector3d), 0.002F, random));
            }

            for (Vector3f vector3d : listPos1) {
                checkPos = LibMathUtils.fromVector3f(vector3d);
                listPos2.addAll(ellipsoidPos(11.5F, 11.5F, 11.5F, checkPos, 0.002F, random));
            }
            for (Vector3f vector3d : listPos2) {
                checkPos = LibMathUtils.fromVector3f(vector3d);
                ellipsoid(9.5F, 9.5F, 9.5F, checkPos, 1, true, blockMap);
            }
            for (Vector3f vector3d : listPos2) {
                checkPos = LibMathUtils.fromVector3f(vector3d);
                ellipsoid(random.nextInt(4, 9) + 0.5F, random.nextInt(4, 9) + 0.5F, random.nextInt(4, 9) + 0.5F, checkPos, 0, true, blockMap);
                if (0.1F > random.nextFloat()) featureMap.put(checkPos, MARBLE_CAVE_POT.location());
            }

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    Blocks.CALCITE.defaultBlockState()
            ), featureMap, builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.MARBLE_CAVE.get();
    }
}
