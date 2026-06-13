package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
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
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.OreBlocks;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.confluence.lib.util.LibGeometryUtils.frustumSetPos;
import static org.confluence.lib.util.LibStructureUtils.frustumSet;

public class IceThornStructure extends Structure {
    public static final Codec<IceThornStructure> CODEC = simpleCodec(IceThornStructure::new);
    public static final ResourceLocation[] feature = new ResourceLocation[]{
            Confluence.asResource("amber_tree"),
            Confluence.asResource("diamond_tree"),
            Confluence.asResource("jade_tree"),
            Confluence.asResource("ruby_tree"),
            Confluence.asResource("sapphire_tree"),
            Confluence.asResource("topaz_tree"),
            Confluence.asResource("amethyst_tree")
    };

    protected IceThornStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int lowestY = getLowestY(context, 16, 16);
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        if (x * x + z * z <= 400 * 400 || lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            int count = random.nextInt(4, 7);
            int step = 80 / count;
            int countThorn;
            Vector3f mainStart = LibMathUtils.toVector3f(centerPos);
            Vector3f mainEnd = LibMathUtils.toVector3f(centerPos).add(0, random.nextInt(120, 150), 0);
            Vector3f otherEnd;
            List<Vector3f> otherEnds = new ArrayList<>();
            List<Vector3f> thorns;
            float radius;

            for (int i = 1; i <= count; i++) {
                radius = random.nextInt(10, 16);
                otherEnd = new Vector3f(random.nextInt(-50, 51), step * i + random.nextInt(-3, 4), random.nextInt(-50, 51));
                Vector3f endPos = new Vector3f(otherEnd.x + mainStart.x, otherEnd.y + mainStart.y, otherEnd.z + mainStart.z);
                thorns = frustumSetPos(mainStart, endPos, radius, 0.6F, 0.05F, random);
                for (Vector3f thorn : thorns) {
                    countThorn = random.nextInt(5, 11);
                    for (int j = 0; j < countThorn; j++) {
                        blockMap.put(BlockPos.containing(thorn.x, thorn.y - j, thorn.z), 2);
                    }
                }
                frustumSet(mainStart, new Vector3f(endPos), radius, 0.6F, 0, blockMap);
                otherEnds.add(otherEnd);
            }
            frustumSet(mainStart, mainEnd, random.nextInt(10, 16), 0.6F, 0, blockMap);

            frustumSet(mainStart, mainEnd.set(mainEnd.x, mainStart.y + random.nextInt(10, 15), mainEnd.z), random.nextInt(2, 4), 0.6F, 1, blockMap);
            for (Vector3f end : otherEnds) {
                frustumSet(mainStart, new Vector3f(mainStart.x + (end.x * 0.1F), mainStart.y + (end.y * 0.1F), mainStart.z + (end.z * 0.1F)), random.nextInt(2, 4), 0.6F, 1, blockMap);
            }

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.PACKED_ICE.defaultBlockState(),
                    OreBlocks.WINTER_MARROW_BLOCK.get().defaultBlockState(),
                    Blocks.ICE.defaultBlockState()
            ), builder);
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.ICE_THORN.get();
    }
}
