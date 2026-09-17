package org.confluence.mod.common.worldgen.structure;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.util.OverworldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SpiderNestStructure extends Structure {
    public static final Codec<SpiderNestStructure> CODEC = simpleCodec(SpiderNestStructure::new);

    public SpiderNestStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int minimum = context.heightAccessor().getMinBuildHeight() + 16;
        int maximum = Math.min(OverworldUtils.getUndergroundY() - 8,
                context.chunkGenerator().getFirstFreeHeight(context.chunkPos().getMiddleBlockX(), context.chunkPos().getMiddleBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()) - 20);
        if (maximum <= minimum) return Optional.empty();
        BlockPos center = context.chunkPos().getMiddleBlockPosition(context.random().nextInt(minimum, maximum + 1));
        return Optional.of(new GenerationStub(center, builder -> {
            var blocks = new Object2IntOpenHashMap<BlockPos>();
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-12, -7, -12), center.offset(12, 7, 12))) {
                double x = (pos.getX() - center.getX()) / 12.0;
                double y = (pos.getY() - center.getY()) / 7.0;
                double z = (pos.getZ() - center.getZ()) / 12.0;
                double radius = x * x + y * y + z * z;
                if (radius > 1.0) continue;
                int block = radius > 0.8 ? 1 : radius > 0.45 && context.random().nextFloat() < 0.3F ? 2 : 0;
                blocks.put(pos.immutable(), block);
            }
            GridPiece.addPieces(blocks, new ArrayList<>(List.of(Blocks.AIR.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.COBWEB.defaultBlockState())), builder);
        }));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.SPIDER_NEST.get();
    }
}
