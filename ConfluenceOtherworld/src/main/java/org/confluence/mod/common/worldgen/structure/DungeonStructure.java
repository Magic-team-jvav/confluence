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

public class DungeonStructure extends Structure {
    public static final MapCodec<DungeonStructure> CODEC = simpleCodec(DungeonStructure::new);
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

            GridPiece.addPieces(blockMap, startChunk, lowestY, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    Blocks.STONE.defaultBlockState(),
                    ModBlocks.SHIMMER.get().defaultBlockState()
            ), builder);
            builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos, true, true, Rotation.NONE));
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.DUNGEON.get();
    }
}
