package org.confluence.lib.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StateProperties {
    public static final BooleanProperty SIGNAL = BooleanProperty.create("signal"); // 电网信号
    public static final BooleanProperty VISIBLE = BooleanProperty.create("visible"); // 可视
    public static final BooleanProperty REVERSE = BooleanProperty.create("reverse"); // 反转
    public static final BooleanProperty DRIVE = BooleanProperty.create("drive"); // 驱动
    public static final BooleanProperty IS_SUPPORTING = BooleanProperty.create("is_supporting"); // 支撑
    public static final BooleanProperty UNLOCKED = BooleanProperty.create("unlocked"); // 解锁
    public static final EnumProperty<HorizontalTwoPart> HORIZONTAL_TWO_PART = EnumProperty.create("horizontal_two_part", HorizontalTwoPart.class);
    public static final EnumProperty<VerticalTwoPart> VERTICAL_TWO_PART = EnumProperty.create("vertical_two_part", VerticalTwoPart.class);
    public static final EnumProperty<ForwardTwoPart> FORWARD_TWO_PART = EnumProperty.create("forward_two_part", ForwardTwoPart.class);
    public static final EnumProperty<HorizontalFourPart> HORIZONTAL_FOUR_PART = EnumProperty.create("horizontal_four_part", HorizontalFourPart.class);
    public static final EnumProperty<VerticalFourPart> VERTICAL_FOUR_PART = EnumProperty.create("vertical_four_part", VerticalFourPart.class);
    public static final EnumProperty<HorizontalNinePart> HORIZONTAL_NINE_PART = EnumProperty.create("horizontal_nine_part", HorizontalNinePart.class);
    public static final EnumProperty<HorizontalTenPart> HORIZONTAL_TEN_PART = EnumProperty.create("horizontal_ten_part", HorizontalTenPart.class);

    public enum HorizontalTwoPart implements StringRepresentable {
        BASE("base"),
        RIGHT("right");

        private final String name;

        HorizontalTwoPart(String pName) {
            this.name = pName;
        }

        /**
         * 获取与该方块相连的多方块的相对方向
         * <p>
         * 注：是以玩家视角看向的相对方向
         *
         * @param blockState 该方块的方块状态
         * @return 相对方向
         */
        public static Direction getConnectedDirection(BlockState blockState) {
            Direction facing = blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? blockState.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
            return switch (blockState.getValue(HORIZONTAL_TWO_PART)) {
                case BASE -> facing.getCounterClockWise(); // 获取其相对右边
                case RIGHT -> facing.getClockWise(); // 获取其相对左边
            };
        }

        public static HorizontalTwoPart getAnotherPart(HorizontalTwoPart exclude) {
            return exclude.isRight() ? BASE : RIGHT;
        }

        public String toString() {
            return name;
        }

        public @NotNull String getSerializedName() {
            return name;
        }

        public boolean isBase() {
            return this == BASE;
        }

        public boolean isRight() {
            return this == RIGHT;
        }
    }

    public enum VerticalTwoPart implements StringRepresentable {
        BASE("base"),
        UP("up");

        private final String name;

        VerticalTwoPart(String name) {
            this.name = name;
        }

        /**
         * 获取与该方块相连的多方块的相对方向
         * <p>
         * 注：是以玩家视角看向的相对方向
         *
         * @param blockState 该方块的方块状态
         * @return 相对方向
         */
        public static Direction getConnectedDirection(BlockState blockState) {
            return blockState.getValue(VERTICAL_TWO_PART).isBase() ? Direction.UP : Direction.DOWN;
        }

        public static VerticalTwoPart getAnotherPart(VerticalTwoPart exclude) {
            return exclude.isUpper() ? BASE : UP;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        public boolean isBase() {
            return this == BASE;
        }

        public boolean isUpper() {
            return this == UP;
        }
    }

    public enum ForwardTwoPart implements StringRepresentable {
        BASE("base"),
        FORWARD("forward");

        private final String name;

        ForwardTwoPart(String pName) {
            this.name = pName;
        }

        /**
         * 获取与该方块相连的多方块的相对方向
         * <p>
         * 注：是以玩家视角看向的相对方向
         *
         * @param blockState 该方块的方块状态
         * @return 相对方向
         */
        public static Direction getConnectedDirection(BlockState blockState) {
            if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                return blockState.getValue(FORWARD_TWO_PART).isBase() ? facing.getOpposite() : facing;
            }
            return Direction.NORTH;
        }

        public static ForwardTwoPart getAnotherPart(ForwardTwoPart exclude) {
            return exclude.isFront() ? BASE : FORWARD;
        }

        public String toString() {
            return name;
        }

        public @NotNull String getSerializedName() {
            return name;
        }

        public boolean isBase() {
            return this == BASE;
        }

        public boolean isFront() {
            return this == FORWARD;
        }
    }

    public enum HorizontalFourPart implements StringRepresentable {
        BASE("base"),
        RIGHT("right"),
        FRONT("front"),
        CORNER("corner");

        private final String name;

        HorizontalFourPart(String name) {
            this.name = name;
        }

        /**
         * 获取与该方块相连的多方块的相对方向
         * <p>
         * 注：是以玩家视角看向的相对方向
         *
         * @param blockState 该方块的方块状态
         * @return 相对方向
         */
        public static Direction getConnectedDirection(BlockState blockState) {
            Direction facing = blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? blockState.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
            return switch (blockState.getValue(HORIZONTAL_FOUR_PART)) {
                case BASE, FRONT -> facing.getCounterClockWise(); // 获取其相对右边
                case RIGHT, CORNER -> facing.getClockWise(); // 获取其相对左边
            };
        }

        public static Map<HorizontalFourPart, BlockPos> getRelatives(HorizontalFourPart part, Direction facing, BlockPos pos) {
            Direction left = facing.getClockWise(); // '↓' -> '←'
            Direction front = left.getClockWise(); // '←' -> '↑'
            Direction right = front.getClockWise(); // '↑' -> '→'

            BlockPos leftPos = pos.relative(left);
            BlockPos frontPos = pos.relative(front);
            BlockPos rightPos = pos.relative(right);
            BlockPos backPos = pos.relative(facing);
            if (part == BASE) {
                return Map.of(
                        RIGHT, rightPos,
                        FRONT, frontPos,
                        CORNER, frontPos.relative(right)
                );
            } else if (part == RIGHT) {
                return Map.of(
                        BASE, leftPos,
                        FRONT, leftPos.relative(front),
                        CORNER, frontPos
                );
            } else if (part == FRONT) {
                return Map.of(
                        BASE, backPos,
                        RIGHT, backPos.relative(right),
                        CORNER, rightPos
                );
            } else {
                return Map.of(
                        BASE, leftPos.relative(facing),
                        RIGHT, backPos,
                        FRONT, leftPos
                );
            }
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    public enum VerticalFourPart implements StringRepresentable {
        BASE("base"),
        RIGHT("right"),
        UP("up"),
        RIGHT_UP("right_up");

        private final String name;

        VerticalFourPart(String pName) {
            this.name = pName;
        }

        public boolean isBase() {
            return this == BASE;
        }

        /**
         * 获取与该方块相连的多方块的相对方向
         * <p>
         * 注：是以玩家视角看向的相对方向
         *
         * @param blockState 该方块的方块状态
         * @return 相对方向
         */
        public static Direction getConnectedDirection(BlockState blockState) {
            Direction facing = blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? blockState.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
            return switch (blockState.getValue(VERTICAL_FOUR_PART)) {
                case BASE, UP -> facing.getCounterClockWise(); // 获取其相对右边
                case RIGHT, RIGHT_UP -> facing.getClockWise(); // 获取其相对左边
            };
        }

        public static Map<VerticalFourPart, BlockPos> getRelatives(VerticalFourPart part, Direction facing, BlockPos pos) {
            if (part == BASE) {
                BlockPos right = pos.relative(facing.getCounterClockWise());
                return Map.of(
                        UP, pos.above(),
                        RIGHT, right,
                        RIGHT_UP, right.above()
                );
            } else if (part == RIGHT) {
                BlockPos left = pos.relative(facing.getClockWise());
                return Map.of(
                        RIGHT_UP, pos.above(),
                        BASE, left,
                        UP, left.above()
                );
            } else if (part == UP) {
                BlockPos right = pos.relative(facing.getCounterClockWise());
                return Map.of(
                        BASE, pos.below(),
                        RIGHT_UP, right,
                        RIGHT, right.below()
                );
            } else {
                BlockPos left = pos.relative(facing.getClockWise());
                return Map.of(
                        RIGHT, pos.below(),
                        UP, left,
                        BASE, left.below()
                );
            }
        }

        public String toString() {
            return name;
        }

        public @NotNull String getSerializedName() {
            return name;
        }

        public boolean isUpper() {
            return this == UP || this == RIGHT_UP;
        }
    }

    public enum HorizontalNinePart implements StringRepresentable {
        CENTER("center"),
        FRONT("front"),
        LEFT("left"),
        RIGHT("right"),
        BACK("back"),
        LEFT_FRONT("left_front"),
        RIGHT_FRONT("right_front"),
        LEFT_BACK("left_back"),
        RIGHT_BACK("right_back");
        private final String name;
        HorizontalNinePart(String name) {
            this.name = name;
        }
        @Contract(pure = true)
        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        /**
         * 从某个方块获取中心方块的相对位置，或从中心获取某个方块的相对位置
         * </P>
         * 前提是必须要存在该属性
         *
         * @param now 正在处理的位置，获取中心时填入本方块位置，获取对应其它方块时填入中心方块位置
         * @param reverseRelative 是否反转延伸。当该值为false时返回中心，为true时返回对应块位置
         * @return 请求的方块位置
         */
        public BlockPos toCenter(BlockPos now, Direction facing, boolean reverseRelative) {
            if (reverseRelative) facing = facing.getOpposite();
            return switch (this){
                case FRONT ->  now.relative(facing.getOpposite());
                case LEFT_FRONT -> now.relative(facing.getOpposite()).relative(facing.getClockWise());
                case RIGHT_FRONT -> now.relative(facing.getOpposite()).relative(facing.getCounterClockWise());
                case LEFT -> now.relative(facing.getClockWise());
                case RIGHT -> now.relative(facing.getCounterClockWise());
                case LEFT_BACK -> now.relative(facing).relative(facing.getClockWise());
                case RIGHT_BACK -> now.relative(facing).relative(facing.getCounterClockWise());
                case BACK -> now.relative(facing);
                default -> now;
            };
        }

        /**
         * 获取除了ign之外的所有方块的相对位属性和世界坐标
         */
        public static Map<HorizontalNinePart, BlockPos> getAllExcept(Direction facing, BlockPos center, @Nullable StateProperties.HorizontalNinePart ign) {
            Map<HorizontalNinePart, BlockPos> allEx = new HashMap<>();
            for (HorizontalNinePart part : HorizontalNinePart.values()) {
                if (part == ign) continue;
                allEx.put(part, part.toCenter(center, facing, true));
            }
            return allEx;
        }

        /**
         * 通过世界位置推断当前方块的相对位属性
         */
        public static @Nullable StateProperties.HorizontalNinePart infer(BlockPos center, BlockPos current, Direction facing) {
            for (Map.Entry<HorizontalNinePart, BlockPos> entry : getAllExcept(facing, center ,null).entrySet()) {
                if (entry.getValue().equals(current)) return entry.getKey();
            }
            return null;
        }
    }

    public enum HorizontalTenPart implements StringRepresentable {
        UP("up"),
        CENTER("center"),
        FRONT("front"),
        LEFT("left"),
        RIGHT("right"),
        BACK("back"),
        LEFT_FRONT("left_front"),
        RIGHT_FRONT("right_front"),
        LEFT_BACK("left_back"),
        RIGHT_BACK("right_back");
        private final String name;
        HorizontalTenPart(String name) {
            this.name = name;
        }
        @Contract(pure = true)
        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
        public BlockPos toBase(BlockPos now, Direction facing, boolean reverseRelative) {
            if (reverseRelative) facing = facing.getOpposite();
            int verticalReverse = reverseRelative? -1 : 1;
            return switch (this){
                case UP -> now;
                case CENTER -> now.above(verticalReverse);
                case FRONT ->  now.relative(facing.getOpposite()).above(verticalReverse);
                case LEFT_FRONT -> now.relative(facing.getOpposite()).relative(facing.getClockWise()).above(verticalReverse);
                case RIGHT_FRONT -> now.relative(facing.getOpposite()).relative(facing.getCounterClockWise()).above(verticalReverse);
                case LEFT -> now.relative(facing.getClockWise()).above(verticalReverse);
                case RIGHT -> now.relative(facing.getCounterClockWise()).above(verticalReverse);
                case LEFT_BACK -> now.relative(facing).relative(facing.getClockWise()).above(verticalReverse);
                case RIGHT_BACK -> now.relative(facing).relative(facing.getCounterClockWise()).above(verticalReverse);
                case BACK -> now.relative(facing).above(verticalReverse);
            };
        }

        public static @NotNull Map<HorizontalTenPart, BlockPos> getAllExcept(Direction facing, BlockPos center, @Nullable StateProperties.HorizontalTenPart ign) {
            Map<HorizontalTenPart, BlockPos> allEx = new HashMap<>();
            for (HorizontalTenPart part : HorizontalTenPart.values()) {
                if (part == ign) continue;
                allEx.put(part, part.toBase(center, facing, true));
            }
            return allEx;
        }

        public static @Nullable StateProperties.HorizontalTenPart infer(BlockPos center, BlockPos current, Direction facing) {
            for (Map.Entry<HorizontalTenPart, BlockPos> entry : getAllExcept(facing, center ,null).entrySet()) {
                if (entry.getValue().equals(current)) return entry.getKey();
            }
            return null;
        }
    }
}
