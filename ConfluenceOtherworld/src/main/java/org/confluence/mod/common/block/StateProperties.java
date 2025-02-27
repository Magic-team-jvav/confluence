package org.confluence.mod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StateProperties {
    public static final BooleanProperty SIGNAL = BooleanProperty.create("signal"); // 电网信号
    public static final BooleanProperty VISIBLE = BooleanProperty.create("visible"); // 可视
    public static final BooleanProperty REVERSE = BooleanProperty.create("reverse"); // 反转
    public static final BooleanProperty DRIVE = BooleanProperty.create("drive"); // 驱动
    public static final BooleanProperty IS_SUPPORTING = BooleanProperty.create("is_supporting"); // 支撑
    public static final EnumProperty<HorizontalTwoPart> HORIZONTAL_TWO_PART = EnumProperty.create("horizontal_two_part", HorizontalTwoPart.class);
    public static final EnumProperty<VerticalTwoPart> VERTICAL_TWO_PART = EnumProperty.create("vertical_two_part", VerticalTwoPart.class);
    public static final EnumProperty<HorizontalFourPart> HORIZONTAL_FOUR_PART = EnumProperty.create("horizontal_four_part", HorizontalFourPart.class);
    public static final EnumProperty<VerticalFourPart> VERTICAL_FOUR_PART = EnumProperty.create("vertical_four_part", VerticalFourPart.class);

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
            Direction facing = blockState.hasProperty(HorizontalDirectionalBlock.FACING) ? blockState.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
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
            Direction facing = blockState.hasProperty(HorizontalDirectionalBlock.FACING) ? blockState.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
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

        /**
         * 获取与该方块相连的多方块的相对方向
         * <p>
         * 注：是以玩家视角看向的相对方向
         *
         * @param blockState 该方块的方块状态
         * @return 相对方向
         */
        public static Direction getConnectedDirection(BlockState blockState) {
            Direction facing = blockState.hasProperty(HorizontalDirectionalBlock.FACING) ? blockState.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
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
}
