package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.common.worldgen.structure.SimpleTemplatePiece;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.world.level.block.LeavesBlock.PERSISTENT;
import static org.confluence.lib.util.StructureUtils.getHeight;

public class LivingTreeStructure extends Structure {
    public static final MapCodec<LivingTreeStructure> CODEC = simpleCodec(LivingTreeStructure::new);

    protected LivingTreeStructure(StructureSettings settings) {
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
            BlockPos chestPos = centerPos;
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            centerPos = BaseStructures.livingTree(
                    centerPos,
                    blockMap,
                    random,
                    random.nextInt(55, 65),
                    4,
                    5.9D,
                    1.0D,
                    1,
                    20,
                    15,
                    -20,
                    2,
                    2,
                    1,
                    1,
                    5,
                    5,
                    10,
                    7,
                    -10,
                    2,
                    2,
                    1,
                    1,
                    5,
                    5,
                    true,
                    random.nextInt(65, 95),
                    4,
                    4.9D,
                    1.0D,
                    1,
                    true,
                    20,
                    10,
                    30,
                    15,
                    4,
                    2,
                    0.01F,
                    0.75F,
                    2,
                    1
            );

            Rotation rotation = Util.getRandom(Rotation.values(), random);

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.LIVING_LOG_BLOCKS.getWood().get().defaultBlockState(),
                    NatureBlocks.LIVING_LOG_BLOCKS.getLeaves().get().defaultBlockState().setValue(PERSISTENT, Boolean.TRUE)
            ), Map.of(
                    chestPos.offset(0, -3, 3), Confluence.asResource("surface_chests")
            ), builder);
            switch (rotation) {
                case CLOCKWISE_90 -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "living_room", centerPos.offset(5, 0, 1), true, true, Rotation.CLOCKWISE_90));
                case CLOCKWISE_180 -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "living_room", centerPos.offset(-1, 0, 5), true, true, Rotation.CLOCKWISE_180));
                case COUNTERCLOCKWISE_90 ->
                        builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "living_room", centerPos.offset(-5, 0, -1), true, true, Rotation.COUNTERCLOCKWISE_90));
                default -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "living_room", centerPos.offset(1, 0, -5), true, true, Rotation.NONE));
            }
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.LIVING_TREE.get();
    }
}
