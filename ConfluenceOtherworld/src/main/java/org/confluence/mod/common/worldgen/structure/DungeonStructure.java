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
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.confluence.lib.util.StructureUtils.lineSet;

public class DungeonStructure extends Structure {
    public static final MapCodec<DungeonStructure> CODEC = simpleCodec(DungeonStructure::new);

    protected DungeonStructure(StructureSettings settings) {
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
            Rotation rotation = Util.getRandom(Rotation.values(), random);
            List<Vector3d> firstChannel = new ArrayList<>();
            Vector3d vct = new Vector3d(centerPos.getX(), centerPos.getY() - 4, centerPos.getZ());
            firstChannel.add(vct);
            vct = new Vector3d(centerPos.getX() + random.nextInt(-80, 81), random.nextInt(0, 20), centerPos.getZ() + random.nextInt(-80, 81));
            firstChannel.add(vct);
            VectorUtils.lightningPathList(firstChannel, 2, 8, random);
            lineSet(firstChannel, 5.5, 5.5, 1, true, blockMap);
            lineSet(firstChannel, 2.5, 2.5, 0, true, blockMap);

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    DecorativeBlocks.BLUE_BRICKS.get().defaultBlockState()
            ), builder);
            switch (rotation) {
                case CLOCKWISE_90 -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos.offset(15, -3, -23), true, true, Rotation.CLOCKWISE_90));
                case CLOCKWISE_180 -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos.offset(23, -3, 15), true, true, Rotation.CLOCKWISE_180));
                case COUNTERCLOCKWISE_90 -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos.offset(-15, -3, 23), true, true, Rotation.COUNTERCLOCKWISE_90));
                default -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos.offset(-23, -3, -15), true, true, Rotation.NONE));
            }
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.DUNGEON.get();
    }
}
