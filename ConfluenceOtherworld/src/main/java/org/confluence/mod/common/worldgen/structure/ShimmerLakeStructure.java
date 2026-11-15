package org.confluence.mod.common.worldgen.structure;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalFourPartBlock;
import org.confluence.lib.common.block.StateProperties.HorizontalFourPart;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3f;

import java.util.*;

import static org.confluence.lib.util.LibGeometryUtils.ellipsoidPos;
import static org.confluence.lib.util.LibGeometryUtils.roundPos;
import static org.confluence.lib.util.LibStructureUtils.*;

public class ShimmerLakeStructure extends Structure {
    public static final MapCodec<ShimmerLakeStructure> CODEC = simpleCodec(ShimmerLakeStructure::new);
    /// 材质表下标：0 空气、1 石头、2 微光，3~6 是藤蔓（无浆果 / 带浆果 各一对，藤身 + 藤头），后面是 16 种朝向 / 部位的王莲
    private static final int VINE_PLANT = 3;
    private static final int VINE_HEAD = 4;
    private static final int VINE_PLANT_BERRIES = 5;
    private static final int VINE_HEAD_BERRIES = 6;
    private static final int LILY_START = 7;
    /// 微光浆果藤：在整个洞顶随机撒点（不用网格，免得看出棋盘感），每一列最多一条
    private static final int VINE_RADIUS = 30;
    private static final int VINE_TRIES = 420;
    /// 藤长范围：1~12，保底 1 格；越靠湖心期望越长，每条再叠加 ±2~3 格的随机涨落 / 缩短
    private static final int VINE_MIN_LENGTH = 1;
    private static final int VINE_MAX_LENGTH = 12;
    private static final int VINE_JITTER_MIN = 2;
    private static final int VINE_JITTER_MAX = 3;
    /// 结果（berries=true）的藤只占 1/50，其余都是光藤
    private static final int VINE_BERRY_CHANCE = 50;
    private static final int VINE_SCAN_HEIGHT = 24;
    private static final int VINE_SCAN_STEPS = 48;
    /// 微光王莲的目标片数
    private static final int MAX_LILIES = 12;
    /// 微光填在 centerPos.y - 2 及其以下，所以液面那一格（王莲要坐的那格）是 centerPos.y - 1
    private static final int LILY_Y_OFFSET = -1;
    private static final ResourceLocation[] feature = new ResourceLocation[]{
            Confluence.asResource("amber_tree"),
            Confluence.asResource("diamond_tree"),
            Confluence.asResource("jade_tree"),
            Confluence.asResource("ruby_tree"),
            Confluence.asResource("sapphire_tree"),
            Confluence.asResource("topaz_tree"),
            Confluence.asResource("amethyst_tree")
    };

    public ShimmerLakeStructure(StructureSettings settings) {
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
            BlockPos centerPos = startChunk.getMiddleBlockPosition(random.nextInt(-40, 10));
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Map<BlockPos, ResourceLocation> featureMap = new HashMap<>();
            List<Vector3f> vctPosList = new LinkedList<>();

            List<Vector3f> posOut = ellipsoidPos(36, 12, 36, centerPos, 0.03F, random);
            List<Vector3f> posIn = ellipsoidPos(24, 9, 24, centerPos, 0.03F, random);
            lineSet(posOut, 11.5F, 11.5F, 1, true, blockMap);
            lineSet(posOut, 8.5F, 8.5F, 0, 1, true, blockMap, centerPos.getY() - 2);
            lineSet(posIn, 6.5F, 6.5F, 0, 2, true, blockMap, centerPos.getY() - 2);
            roundPos(centerPos.offset(0, -1, 0), 34, random, vctPosList, 3, random.nextInt(14, 17), 0.0F);
            lineSetFeature(vctPosList, featureMap, feature, random);

            // 微光浆果：铺满整个洞顶。XZ 上直接随机撒点（随机偏移而不是网格），每一列最多挂一条
            Set<Long> vineColumns = new HashSet<>();
            for (int i = 0; i < VINE_TRIES; i++) {
                int dx = random.nextInt(VINE_RADIUS * 2 + 1) - VINE_RADIUS;
                int dz = random.nextInt(VINE_RADIUS * 2 + 1) - VINE_RADIUS;
                if (dx * dx + dz * dz > VINE_RADIUS * VINE_RADIUS) continue;
                int vineX = centerPos.getX() + dx;
                int vineZ = centerPos.getZ() + dz;
                if (!vineColumns.add(((long) vineX << 32) | (vineZ & 0xFFFFFFFFL))) continue; // 同一列只挂一条
                BlockPos ceilingAir = findTopAir(blockMap, vineX, vineZ, centerPos.getY() + VINE_SCAN_HEIGHT, VINE_SCAN_STEPS);
                if (ceilingAir != null) {
                    // 离湖心越近越长：把 1（正中）~0（边缘）作为长度期望的权重
                    double centerFactor = 1.0D - Math.sqrt((double) dx * dx + (double) dz * dz) / VINE_RADIUS;
                    putShimmerVine(blockMap, ceilingAir, random, centerFactor);
                }
            }

            // 微光王莲：铺在微光液面上，2x2 四格必须都站得住（本身是洞内空气、下面是微光）
            int lilies = 0;
            for (int i = 0; i < MAX_LILIES * 6 && lilies < MAX_LILIES; i++) {
                int dx = random.nextInt(29) - 14;
                int dz = random.nextInt(29) - 14;
                if (dx * dx + dz * dz > 196) continue; // 限制在液面范围内
                BlockPos lilyPos = new BlockPos(centerPos.getX() + dx, centerPos.getY() + LILY_Y_OFFSET, centerPos.getZ() + dz);
                if (putShimmerlily(blockMap, lilyPos, random)) lilies++;
            }

            List<BlockState> materials = new ArrayList<>();
            materials.add(Blocks.AIR.defaultBlockState());
            materials.add(Blocks.STONE.defaultBlockState());
            materials.add(ModBlocks.SHIMMER.get().defaultBlockState());
            materials.add(NatureBlocks.SHIMMER_DROOPING_VINE_PLANT.get().defaultBlockState().setValue(CaveVines.BERRIES, false));
            materials.add(NatureBlocks.SHIMMER_DROOPING_VINE.get().defaultBlockState().setValue(CaveVines.BERRIES, false));
            materials.add(NatureBlocks.SHIMMER_DROOPING_VINE_PLANT.get().defaultBlockState().setValue(CaveVines.BERRIES, true));
            materials.add(NatureBlocks.SHIMMER_DROOPING_VINE.get().defaultBlockState().setValue(CaveVines.BERRIES, true));
            // 王莲是多方块：4 个朝向 x 4 个部位，下标必须是 LILY_START + facing.get2DDataValue() * 4 + part.ordinal()
            BlockState[] lilyStates = new BlockState[16];
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                for (HorizontalFourPart part : HorizontalFourPart.values()) {
                    lilyStates[facing.get2DDataValue() * 4 + part.ordinal()] = NatureBlocks.BLINKING_ROYAL_SHIMMERLILY.get().defaultBlockState()
                            .setValue(HorizontalDirectionalWithHorizontalFourPartBlock.FACING, facing)
                            .setValue(HorizontalDirectionalWithHorizontalFourPartBlock.PART, part);
                }
            }
            materials.addAll(Arrays.asList(lilyStates));

            GridPiece.addPieces(blockMap, materials, featureMap, builder);
        });
    }

    /// 从洞顶那一格往下铺一条微光浆果藤：一路 plant，最下面一节是 head（berries 全开）；被挡就截短
    private static void putShimmerVine(Object2IntMap<BlockPos> blockMap, BlockPos ceilingAir, WorldgenRandom random, double centerFactor) {
        int length = vineLength(random, centerFactor);
        BlockPos.MutableBlockPos pos = ceilingAir.mutable();
        int allowed = 0;
        for (int i = 0; i < length; i++) {
            if (!blockMap.containsKey(pos) || blockMap.getInt(pos) != 0) break;
            allowed++;
            pos.move(Direction.DOWN);
        }
        if (allowed <= 0) return;
        // 整条藤共用同一个结果状态：只有 1/50 的藤带浆果
        boolean berries = random.nextInt(VINE_BERRY_CHANCE) == 0;
        int plant = berries ? VINE_PLANT_BERRIES : VINE_PLANT;
        int head = berries ? VINE_HEAD_BERRIES : VINE_HEAD;
        pos.set(ceilingAir);
        for (int i = 0; i < allowed; i++) {
            blockMap.put(pos.immutable(), i == allowed - 1 ? head : plant);
            pos.move(Direction.DOWN);
        }
    }

    /// 藤长：期望值随「离湖心远近」线性变化（正中 12 格 → 边缘 1 格），每条再叠加 ±2~3 格的随机涨落 / 缩短，最后夹在 1~12
    private static int vineLength(WorldgenRandom random, double centerFactor) {
        double mean = VINE_MIN_LENGTH + Mth.clamp(centerFactor, 0.0D, 1.0D) * (VINE_MAX_LENGTH - VINE_MIN_LENGTH);
        int jitter = VINE_JITTER_MIN + random.nextInt(VINE_JITTER_MAX - VINE_JITTER_MIN + 1);
        if (random.nextBoolean()) jitter = -jitter;
        return Mth.clamp((int) Math.round(mean) + jitter, VINE_MIN_LENGTH, VINE_MAX_LENGTH);
    }

    /// 铺一片微光王莲：四格都写入材质表（朝向 + 部位），位置由 BASE 与朝向推出来
    private static boolean putShimmerlily(Object2IntMap<BlockPos> blockMap, BlockPos pos, WorldgenRandom random) {
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Map<HorizontalFourPart, BlockPos> relatives = HorizontalFourPart.getRelatives(HorizontalFourPart.BASE, facing, pos);
        if (!canPlaceLily(blockMap, pos)) return false;
        for (BlockPos relative : relatives.values()) {
            if (!canPlaceLily(blockMap, relative)) return false;
        }
        int start = LILY_START + facing.get2DDataValue() * 4;
        blockMap.put(pos, start + HorizontalFourPart.BASE.ordinal());
        for (Map.Entry<HorizontalFourPart, BlockPos> entry : relatives.entrySet()) {
            blockMap.put(entry.getValue(), start + entry.getKey().ordinal());
        }
        return true;
    }

    /// 王莲要坐在微光液面上：自身得是结构挖出来的空气，下面那格得是微光（材质 2）
    private static boolean canPlaceLily(Object2IntMap<BlockPos> blockMap, BlockPos pos) {
        return blockMap.containsKey(pos) && blockMap.getInt(pos) == 0 && blockMap.getInt(pos.below()) == 2;
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.SHIMMER_LAKE.get();
    }
}
