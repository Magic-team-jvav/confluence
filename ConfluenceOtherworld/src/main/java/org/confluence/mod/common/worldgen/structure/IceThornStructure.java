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
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.joml.Vector3d;

import java.util.*;

import static org.confluence.mod.util.StructureUtils.*;

public class IceThornStructure extends Structure {
    public static final MapCodec<IceThornStructure> CODEC = simpleCodec(IceThornStructure::new);
    public static final ResourceLocation[] feature = new ResourceLocation[]{
            Confluence.asResource("amber_tree"),
            Confluence.asResource("diamond_tree"),
            Confluence.asResource("emerald_tree"),
            Confluence.asResource("ruby_tree"),
            Confluence.asResource("sapphire_tree"),
            Confluence.asResource("topaz_tree"),
            Confluence.asResource("tr_amethyst_tree")
    };

    protected IceThornStructure(StructureSettings settings) {
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
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            int count = random.nextInt(4, 7);
            int step = 80 / count;
            int countThorn;
            Vector3d mainStart = new Vector3d(centerPos.getX(), centerPos.getY(), centerPos.getZ());
            Vector3d mainEnd = new Vector3d(centerPos.getX(), centerPos.getY() + random.nextInt(120, 150), centerPos.getZ());
            Vector3d otherEnd;
            List<Vector3d> otherEnds = new ArrayList<>();
            List<Vector3d> thorns;
            double radius;

            for (int i = 1; i <= count; i++) {
                radius = random.nextInt(10, 16);
                otherEnd = new Vector3d(random.nextInt(-50, 51), step * i + random.nextInt(-3, 4), random.nextInt(-50, 51));
                thorns = frustumSetPos(mainStart, new Vector3d(otherEnd.x + mainStart.x, otherEnd.y + mainStart.y, otherEnd.z + mainStart.z), radius, 0.6, 0.05F, random);
                for (Vector3d thorn : thorns) {
                    countThorn = random.nextInt(5, 11);
                    for (int j = 0; j < countThorn; j++) {
                        blockMap.put(new BlockPos((int) thorn.x, (int) thorn.y - j, (int) thorn.z), 2);
                    }
                }
                frustumSet(mainStart, new Vector3d(otherEnd.x + mainStart.x, otherEnd.y + mainStart.y, otherEnd.z + mainStart.z), radius, 0.6, 0, blockMap);
                otherEnds.add(otherEnd);
            }
            frustumSet(mainStart, mainEnd, random.nextInt(10, 16), 0.6, 0, blockMap);

            frustumSet(mainStart, mainEnd.set(mainEnd.x, mainStart.y + random.nextInt(10, 15), mainEnd.z), random.nextInt(2, 4), 0.6, 1, blockMap);
            for (Vector3d end : otherEnds) {
                frustumSet(mainStart, new Vector3d(mainStart.x + (end.x * 0.1), mainStart.y + (end.y * 0.1), mainStart.z + (end.z * 0.1)), random.nextInt(2, 4), 0.6, 1, blockMap);
            }

            GridPiece.addPieces(blockMap, startChunk, lowestY, Lists.newArrayList(
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
