package org.confluence.mod.common.block;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class StateProperties {
    public static final BooleanProperty SIGNAL = BooleanProperty.create("signal"); // 电网信号
    public static final BooleanProperty VISIBLE = BooleanProperty.create("visible"); // 可视
    public static final BooleanProperty REVERSE = BooleanProperty.create("reverse"); // 反转
    public static final BooleanProperty DRIVE = BooleanProperty.create("drive"); // 驱动
    public static final BooleanProperty IS_SUPPORTING = BooleanProperty.create("is_supporting"); // 支撑
    public static final EnumProperty<HorizontalTwoPart> HORIZONTAL_TWO_PART = EnumProperty.create("horizontal_two_part", HorizontalTwoPart.class);
    public static final EnumProperty<VerticalFourPart> FOUR_PART = EnumProperty.create("vertical_four_part", VerticalFourPart.class);

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
            Direction facing = blockState.getValue(HorizontalDirectionalBlock.FACING);
            return switch (blockState.getValue(HORIZONTAL_TWO_PART)) {
                case BASE -> facing.getCounterClockWise(); // 获取其相对右边
                case RIGHT -> facing.getClockWise(); // 获取其相对左边
            };
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
            Direction facing = blockState.getValue(HorizontalDirectionalBlock.FACING);
            return switch (blockState.getValue(FOUR_PART)) {
                case BASE, UP -> facing.getCounterClockWise(); // 获取其相对右边
                case RIGHT, RIGHT_UP -> facing.getClockWise(); // 获取其相对左边
            };
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
