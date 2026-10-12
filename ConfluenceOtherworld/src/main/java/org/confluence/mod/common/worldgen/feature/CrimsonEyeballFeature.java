package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/// 猩红眼球：一系列大小不一的眼球地物，由眼球块（眼白）与瞳孔块组成。
///
/// - {@code embedded = true}：嵌在地里的 3x3x2 眼球。最上层是外露的「眼白 + 瞳孔」面（瞳孔一定连续不断开），
///   第二层整层眼白，用来埋进地下，所以看上去像一颗从土里露出来的眼珠；
/// - {@code embedded = false}：立在地表的实心立方体眼球，尺寸 2x2 ~ 5x5（越小越常见），瞳孔面朝随机的一个水平方向，
///   其余五面与内部都填眼白块。瞳孔大小跟随眼球尺寸：2x2 以单块为主，3x3 为单块 / 双块 / 2x2，
///   4x4 保底 2x2，5x5 保底 3x3。眼球底部会长出分叉的猩红石「血管」，贴着地面爬开并向外铺成底座，
///   而且整体比地面高出一格；4x4 / 5x5 的巨型眼球还有较低概率内部含水、在非正面破一个洞让水流出来。
public class CrimsonEyeballFeature extends Feature<CrimsonEyeballFeature.Config> {
    /// 站立眼球的尺寸权重（下标 = 尺寸 - 2）：2x2 40%、3x3 30%、4x4 20%、5x5 10%
    private static final int[] SIZE_WEIGHTS = {4, 3, 2, 1};
    /// 嵌地眼球的 XZ 面边长与层数
    private static final int EMBEDDED_SIZE = 3;
    private static final int EMBEDDED_LAYERS = 2;
    /// 内部含水 / 破洞的巨型眼球尺寸门槛
    private static final int GIANT_SIZE = 4;
    /// 底座向外铺开的最大格数
    private static final int MAX_WRAP_DEPTH = 3;
    /// 2x2 四角图案，同时也是 4x4 眼球的保底瞳孔
    private static final int[][] PUPIL_QUAD_NW = {{0, 0}, {1, 0}, {0, 1}, {1, 1}};
    private static final int[][] PUPIL_QUAD_NE = {{1, 0}, {2, 0}, {1, 1}, {2, 1}};
    private static final int[][] PUPIL_QUAD_SW = {{0, 1}, {1, 1}, {0, 2}, {1, 2}};
    private static final int[][] PUPIL_QUAD_SE = {{1, 1}, {2, 1}, {1, 2}, {2, 2}};
    /// 2x2 眼球：倾向单块瞳孔（少数双块），形状都在 2x2 面内，绝不会超出范围
    private static final int[][][] PUPIL_SHAPES_SMALL = {
            {{0, 0}}, {{0, 0}}, {{0, 0}},
            {{0, 0}, {1, 0}}, {{0, 0}, {0, 1}}
    };
    /// 3x3 眼球：单块居中、双块横竖各向、2x2 四角，共 11 种连续图案
    private static final int[][][] PUPIL_SHAPES_MEDIUM = {
            {{1, 1}},
            {{0, 1}, {1, 1}}, {{1, 1}, {2, 1}},
            {{1, 0}, {1, 1}}, {{1, 1}, {1, 2}}, {{0, 1}, {0, 2}}, {{2, 1}, {2, 2}},
            PUPIL_QUAD_NW, PUPIL_QUAD_NE, PUPIL_QUAD_SW, PUPIL_QUAD_SE
    };
    /// 4x4 眼球：保底 2x2 瞳孔
    private static final int[][][] PUPIL_SHAPES_LARGE = {
            PUPIL_QUAD_NW, PUPIL_QUAD_NE, PUPIL_QUAD_SW, PUPIL_QUAD_SE
    };
    /// 5x5 眼球：保底 3x3 瞳孔（整块，靠偏移在面内随机位置）
    private static final int[][][] PUPIL_SHAPES_GIANT = {
            {
                    {0, 0}, {1, 0}, {2, 0},
                    {0, 1}, {1, 1}, {2, 1},
                    {0, 2}, {1, 2}, {2, 2}
            }
    };

    public CrimsonEyeballFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        return context.config().embedded() ? placeEmbedded(context) : placeStanding(context);
    }

    /// 第一种：嵌在地里的眼球。origin 是地表上方一格，外露面替换掉地表方块，第二层埋在地表下一格。
    private boolean placeEmbedded(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (!isFlatEnough(level, origin, EMBEDDED_SIZE, 1)) return false;

        BlockState eyeball = config.eyeball().getState(random, origin);
        BlockState pupil = config.pupil().getState(random, origin);
        int[][] shape = pickPupil(EMBEDDED_SIZE, random);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < EMBEDDED_SIZE; dx++) {
            for (int dz = 0; dz < EMBEDDED_SIZE; dz++) {
                boolean isPupil = contains(shape, dx, dz);
                for (int dy = 0; dy < EMBEDDED_LAYERS; dy++) {
                    pos.set(origin.getX() + dx, origin.getY() - 1 - dy, origin.getZ() + dz);
                    level.setBlock(pos, isPupil && dy == 0 ? pupil : eyeball, 3);
                }
            }
        }
        return true;
    }

    /// 第二种：立在地表的实心立方体眼球，正面（随机水平朝向）带瞳孔，底部带猩红石包裹。
    private boolean placeStanding(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos base = context.origin();
        int size = randomSize(random);
        if (!isFlatEnough(level, base, size, size <= 3 ? 1 : 2)) return false;
        if (!hasRoom(level, base, size)) return false;
        if (!level.getFluidState(base.below()).isEmpty()) return false;

        BlockState eyeball = config.eyeball().getState(random, base);
        BlockState pupil = config.pupil().getState(random, base);
        BlockState vein = config.vein().getState(random, base);
        // 实心立方体，其余部分全部由眼球块填充
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                for (int dz = 0; dz < size; dz++) {
                    level.setBlock(base.offset(dx, dy, dz), eyeball, 3);
                }
            }
        }
        // 正面瞳孔：朝向随机不一致，瞳孔大小跟随眼球尺寸
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        placePupil(level, base, size, facing, pickPupil(size, random), random, pupil);

        // 附带包裹：分叉的猩红石血管贴着地面爬开，底座向外铺开，两者都比地面高出一格
        if (random.nextFloat() < config.veinChance()) {
            placeVeins(level, base, size, random, vein);
            wrapBase(level, base, size, random, vein);
        }
        // 巨型眼球内部含水 + 非正面破损（权重更低）
        if (size >= GIANT_SIZE && random.nextFloat() < config.waterChance()) {
            BlockState fluid = config.fluid().getState(random, base);
            placeWaterAndBreach(level, base, size, facing, random, fluid, vein);
        }
        return true;
    }

    /// 按尺寸权重随机一个边长（2x2 ~ 5x5）
    private static int randomSize(RandomSource random) {
        int total = 0;
        for (int weight : SIZE_WEIGHTS) total += weight;
        int roll = random.nextInt(total);
        for (int i = 0; i < SIZE_WEIGHTS.length; i++) {
            roll -= SIZE_WEIGHTS[i];
            if (roll < 0) return i + 2;
        }
        return SIZE_WEIGHTS.length + 1;
    }

    /// 挑一种瞳孔图案：2x2 以单块为主，3x3 单块 / 双块 / 2x2，4x4 保底 2x2，5x5 保底 3x3
    private static int[][] pickPupil(int size, RandomSource random) {
        int[][][] table = switch (size) {
            case 2 -> PUPIL_SHAPES_SMALL;
            case 3 -> PUPIL_SHAPES_MEDIUM;
            case 4 -> PUPIL_SHAPES_LARGE;
            default -> PUPIL_SHAPES_GIANT;
        };
        return table[random.nextInt(table.length)];
    }

    /// 检查地表是否够平：footprint 内每列的地面高度与 origin 相差不超过 tolerance，避免斜面 / 悬崖上出现悬空眼球
    private static boolean isFlatEnough(WorldGenLevel level, BlockPos origin, int size, int tolerance) {
        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                int height = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX() + dx, origin.getZ() + dz);
                if (Math.abs(height - origin.getY()) > tolerance) return false;
            }
        }
        return true;
    }

    /// 检查立方体所需空间：可替换 / 空气的方块要占大多数，否则说明埋在土里或撞进山体
    private static boolean hasRoom(WorldGenLevel level, BlockPos base, int size) {
        int blocked = 0;
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 1; dy < size; dy++) {
                for (int dz = 0; dz < size; dz++) {
                    BlockState state = level.getBlockState(base.offset(dx, dy, dz));
                    if (!state.isAir() && !state.canBeReplaced()) blocked++;
                }
            }
        }
        return blocked <= size * size * (size - 1) / 5;
    }

    /// 把瞳孔形状摆到正面：可偏移范围按所选形状的外接范围算，所以无论哪种尺寸都不会超出面外
    private static void placePupil(WorldGenLevel level, BlockPos base, int size, Direction facing, int[][] shape,
                                   RandomSource random, BlockState pupil) {
        int maxU = 0, maxV = 0;
        for (int[] cell : shape) {
            maxU = Math.max(maxU, cell[0]);
            maxV = Math.max(maxV, cell[1]);
        }
        int offsetU = random.nextInt(size - maxU);
        int offsetV = random.nextInt(size - maxV);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int[] cell : shape) {
            level.setBlock(facePos(pos, base, size, facing, cell[0] + offsetU, cell[1] + offsetV), pupil, 3);
        }
    }

    /// 面内坐标 (u 水平, v 垂直) → 立方体某个面的方块位置
    private static BlockPos facePos(BlockPos.MutableBlockPos pos, BlockPos base, int size, Direction face, int u, int v) {
        return switch (face) {
            case NORTH -> pos.set(base.getX() + u, base.getY() + v, base.getZ());
            case SOUTH -> pos.set(base.getX() + u, base.getY() + v, base.getZ() + size - 1);
            case WEST -> pos.set(base.getX(), base.getY() + v, base.getZ() + u);
            case EAST -> pos.set(base.getX() + size - 1, base.getY() + v, base.getZ() + u);
            default -> pos.set(base.getX() + u, base.getY() + v, base.getZ());
        };
    }

    /// 血管：从眼球底部向外爬出几条带分叉的猩红石枝，落在该列地面之上（比地面高出一格）
    private static void placeVeins(WorldGenLevel level, BlockPos base, int size, RandomSource random, BlockState vein) {
        int centerX = base.getX() + size / 2;
        int centerZ = base.getZ() + size / 2;
        int arms = 2 + random.nextInt(3);
        for (int i = 0; i < arms; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            int x = centerX + (int) Math.round(Math.cos(angle) * (size * 0.5 + 0.5));
            int z = centerZ + (int) Math.round(Math.sin(angle) * (size * 0.5 + 0.5));
            int length = 2 + random.nextInt(3 + size);
            drawVein(level, base.getY(), x, z, angle, length, random, vein);
            if (random.nextBoolean()) { // 分叉
                double branchAngle = angle + (random.nextBoolean() ? 0.7 : -0.7);
                drawVein(level, base.getY(), x, z, branchAngle, Math.max(2, length / 2), random, vein);
            }
        }
    }

    private static void drawVein(WorldGenLevel level, int baseY, int startX, int startZ, double angle, int length,
                                 RandomSource random, BlockState vein) {
        double x = startX;
        double z = startZ;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int step = 0; step < length; step++) {
            int y = aboveGroundAt(level, (int) Math.floor(x), (int) Math.floor(z), baseY);
            if (y != Integer.MIN_VALUE) {
                pos.set((int) Math.floor(x), y, (int) Math.floor(z));
                level.setBlock(pos, vein, 3);
            }
            angle += (random.nextDouble() - 0.5) * 0.8;
            x += Math.cos(angle);
            z += Math.sin(angle);
        }
    }

    /// 在 baseY 附近向下找该列最上方的实心方块，返回它上面那一格（即比地面高出一格）
    private static int aboveGroundAt(WorldGenLevel level, int x, int z, int baseY) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = baseY + 2; y >= baseY - 4; y--) {
            pos.set(x, y, z);
            if (level.getBlockState(pos).blocksMotion()) return y + 1;
        }
        return Integer.MIN_VALUE;
    }

    /// 底座包裹：在眼球底部、贴着地面的那一层向外铺猩红石（还是比地面高出一格），每条边铺开的宽度随机
    private static void wrapBase(WorldGenLevel level, BlockPos base, int size, RandomSource random, BlockState vein) {
        int maxDepth = size <= 2 ? 2 : MAX_WRAP_DEPTH;
        int north = 1 + random.nextInt(maxDepth);
        int south = 1 + random.nextInt(maxDepth);
        int west = 1 + random.nextInt(maxDepth);
        int east = 1 + random.nextInt(maxDepth);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -west; dx < size + east; dx++) {
            for (int dz = -north; dz < size + south; dz++) {
                if (dx >= 0 && dx < size && dz >= 0 && dz < size) continue; // 眼球本体不覆盖
                if (random.nextFloat() > 0.7F) continue; // 留一些缺口，看起来更像爬出来的血管
                pos.set(base.getX() + dx, base.getY(), base.getZ() + dz);
                if (!level.getBlockState(pos.below()).blocksMotion()) continue; // 悬空的地方不铺
                level.setBlock(pos, vein, 3);
            }
        }
    }

    /// 巨型眼球内部灌一点水，并在非正面（不是瞳孔面）破一个洞，让水能流出来
    private static void placeWaterAndBreach(WorldGenLevel level, BlockPos base, int size, Direction facing,
                                            RandomSource random, BlockState fluid, BlockState vein) {
        int waterTop = 1 + random.nextInt(size - 2);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = 1; dx < size - 1; dx++) {
            for (int dz = 1; dz < size - 1; dz++) {
                for (int dy = 0; dy < waterTop; dy++) {
                    pos.set(base.getX() + dx, base.getY() + dy, base.getZ() + dz);
                    level.setBlock(pos, fluid, 3);
                }
            }
        }
        Direction[] sides = new Direction[3];
        int index = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction != facing) sides[index++] = direction;
        }
        Direction breachFace = sides[random.nextInt(sides.length)];
        // 水面高于底部时，洞开在底座之上，水才流得出去
        int minV = waterTop > 1 ? 1 : 0;
        int holes = 1 + random.nextInt(2);
        for (int i = 0; i < holes; i++) {
            int u = 1 + random.nextInt(size - 2);
            int v = minV + random.nextInt(waterTop - minV);
            BlockPos hole = facePos(pos, base, size, breachFace, u, v);
            BlockPos outward = hole.relative(breachFace);
            if (level.getBlockState(outward).is(vein.getBlock())) {
                level.setBlock(outward, Blocks.AIR.defaultBlockState(), 3); // 洞口外面也空出来，底座才不会把水堵在眼球里
            }
            level.setBlock(hole, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static boolean contains(int[][] shape, int x, int z) {
        for (int[] cell : shape) {
            if (cell[0] == x && cell[1] == z) return true;
        }
        return false;
    }

    public record Config(BlockStateProvider eyeball,
                         BlockStateProvider pupil,
                         BlockStateProvider vein,
                         BlockStateProvider fluid,
                         boolean embedded,
                         float veinChance,
                         float waterChance
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("eyeball").forGetter(Config::eyeball),
                BlockStateProvider.CODEC.fieldOf("pupil").forGetter(Config::pupil),
                BlockStateProvider.CODEC.fieldOf("vein").forGetter(Config::vein),
                BlockStateProvider.CODEC.optionalFieldOf("fluid", BlockStateProvider.simple(Blocks.WATER)).forGetter(Config::fluid),
                Codec.BOOL.optionalFieldOf("embedded", false).forGetter(Config::embedded),
                Codec.FLOAT.optionalFieldOf("vein_chance", 0.7F).forGetter(Config::veinChance),
                Codec.FLOAT.optionalFieldOf("water_chance", 0.25F).forGetter(Config::waterChance)
        ).apply(instance, Config::new));
    }
}
