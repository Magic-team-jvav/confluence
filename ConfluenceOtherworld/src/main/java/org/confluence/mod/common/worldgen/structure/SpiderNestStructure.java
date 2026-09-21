package org.confluence.mod.common.worldgen.structure;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
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
            RandomSource random = context.random();
            /// 错开且相互重叠的洞室组成完整巢穴，扩大整体范围而非单个空腔。
            boolean alongX = random.nextBoolean();
            for (int i = -1; i <= 1; i++) {
                int sideways = random.nextInt(5) - 2;
                BlockPos room = center.offset(alongX ? i * 8 : sideways, random.nextInt(5) - 2, alongX ? sideways : i * 8);
                carveChamber(blocks, room, random);
            }
            var interior = new ArrayList<>(blocks.keySet());
            /// 从空腔向外铺两格实体洞壁，顶、底与转角都封闭，不依赖半径阈值留下薄壳。
            for (BlockPos pos : interior) {
                boolean boundary = false;
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = pos.relative(direction);
                    if (!blocks.containsKey(neighbor) || blocks.getInt(neighbor) == 1) {
                        boundary = true;
                        break;
                    }
                }
                if (!boundary) continue;
                for (BlockPos wall : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
                    blocks.putIfAbsent(wall.immutable(), 1);
                }
            }
            double phase = random.nextDouble() * Math.PI * 2;
            for (BlockPos pos : interior) {
                boolean nearWall = false;
                for (Direction direction : Direction.values()) {
                    if (blocks.getInt(pos.relative(direction, 2)) == 1) {
                        nearWall = true;
                        break;
                    }
                }
                double patch = Math.sin(pos.getX() * 0.45 + phase) + Math.cos(pos.getY() * 0.6 + pos.getZ() * 0.4);
                if (nearWall && patch > -0.3 && random.nextFloat() < 0.65F) blocks.put(pos, 2);
            }
            /// 洞壁使用蜘蛛巢作为迷你群系的计数方块，普通蛛网只负责洞内装饰。
            GridPiece.addPieces(blocks, new ArrayList<>(List.of(Blocks.AIR.defaultBlockState(), NatureBlocks.SPIDER_NEST.get().defaultBlockState(), Blocks.COBWEB.defaultBlockState())), builder);
        }));
    }

    /// 所有洞室先统一挖空，再生成外壳，防止相交处的洞壁切断内部空间。
    private static void carveChamber(Object2IntOpenHashMap<BlockPos> blocks, BlockPos center, RandomSource random) {
        double radiusX = 7 + random.nextDouble() * 2;
        double radiusY = 5 + random.nextDouble();
        double radiusZ = 7 + random.nextDouble() * 2;
        double phase = random.nextDouble() * Math.PI * 2;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-10, -7, -10), center.offset(10, 7, 10))) {
            double dx = pos.getX() - center.getX();
            double dy = pos.getY() - center.getY();
            double dz = pos.getZ() - center.getZ();
            double x = dx / radiusX;
            double y = dy / radiusY;
            double z = dz / radiusZ;
            double ripple = Math.sin(dx * 0.5 + phase) * Math.cos(dz * 0.4) + Math.sin(dy * 0.7 + phase);
            if (x * x + y * y + z * z + ripple * 0.1 < 1) blocks.put(pos.immutable(), 0);
        }
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.SPIDER_NEST.get();
    }
}
