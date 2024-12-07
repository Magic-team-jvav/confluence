package org.confluence.mod.common.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class StateProperties {
    public static final BooleanProperty SIGNAL = BooleanProperty.create("signal"); // 电网信号
    public static final BooleanProperty VISIBLE = BooleanProperty.create("visible");
    public static final BooleanProperty REVERSE = BooleanProperty.create("reverse");
    public static final BooleanProperty DRIVE = BooleanProperty.create("drive");
    public static final BooleanProperty IS_SUPPORTING = BooleanProperty.create("is_supporting");
}
