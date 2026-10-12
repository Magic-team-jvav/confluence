package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.block.natural.TaperedTwoPartBlock;
import org.confluence.mod.common.block.natural.TaperedTwoPartBlock.TaperedTwoPart;
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
import static org.confluence.lib.util.LibStructureUtils.putStalactite;
import static org.confluence.lib.util.LibStructureUtils.putStalagmite;

public class GraniteCaveStructure extends Structure {
    public static final Codec<GraniteCaveStructure> CODEC = simpleCodec(GraniteCaveStructure::new);
    /// 每个洞穴的目标摆放数量（按中心点数量摊成概率，不做提前截断，所以整洞均匀分布）
    private static final int MAX_CAVE_STALAGMITES = 64;
    /// 花岗岩石笋在材质表里的下标（0 空气、1 花岗岩；2~4 是朝上的石笋，5~7 是朝下挂的钟乳石）
    private static final int GRANITE_BASE = 2;
    private static final int GRANITE_TIP_MERGE = 3;
    private static final int GRANITE_TIP_SINGLE = 4;
    private static final int GRANITE_DOWN_BASE = 5;
    private static final int GRANITE_DOWN_TIP_MERGE = 6;
    private static final int GRANITE_DOWN_TIP_SINGLE = 7;

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
                ellipsoid(9.5F, 4.5F, 9.5F, checkPos, 1, true, blockMap);
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
                ellipsoid(random.nextInt(4, 9) + 0.5F, 0.1F, random.nextInt(4, 9) + 0.5F, checkPos, 1, true, blockMap);
            }

            // 花岗岩石笋 / 钟乳石：等所有空洞和地面补丁都铺完再放，避免被后面的空洞覆盖。
            // 概率按中心点数量缩放，让整条洞穴均匀分布；这里不能用「够数就 break」，
            // 因为 listPos2 是有序且局部聚集的，提前截断只会集中在前面的中心点，也就是洞穴的一角
            float chance = Math.min(0.25F, (float) MAX_CAVE_STALAGMITES / Math.max(1, listPos2.size()));
            for (Vector3f vector3d : listPos2) {
                checkPos = LibMathUtils.fromVector3f(vector3d);
                if (chance > random.nextFloat()) {
                    putStalagmite(blockMap, checkPos, 20, GRANITE_BASE, GRANITE_TIP_MERGE, GRANITE_TIP_SINGLE);
                }
                if (chance > random.nextFloat()) {
                    putStalactite(blockMap, checkPos, 20, GRANITE_DOWN_BASE, GRANITE_DOWN_TIP_MERGE, GRANITE_DOWN_TIP_SINGLE);
                }
            }

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.GRANITE.get().defaultBlockState(),
                    taperedBlock(Direction.UP, TaperedTwoPart.BASE),
                    taperedBlock(Direction.UP, TaperedTwoPart.TIP_MERGE),
                    taperedBlock(Direction.UP, TaperedTwoPart.TIP_SINGLE),
                    taperedBlock(Direction.DOWN, TaperedTwoPart.BASE),
                    taperedBlock(Direction.DOWN, TaperedTwoPart.TIP_MERGE),
                    taperedBlock(Direction.DOWN, TaperedTwoPart.TIP_SINGLE)
            ), builder);
        });
    }

    /// 花岗岩石笋方块状态：贴附着面那格 base、外侧那格 tip_merge、只有 1 格时 tip_single；direction 决定朝上长还是朝下挂
    private static BlockState taperedBlock(Direction direction, TaperedTwoPart part) {
        return NatureBlocks.GRANITE_TAPERED_BLOCK.get().defaultBlockState()
                .setValue(TaperedTwoPartBlock.TIP_DIRECTION, direction)
                .setValue(TaperedTwoPartBlock.PART, part);
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.GRANITE_CAVE.get();
    }
}
