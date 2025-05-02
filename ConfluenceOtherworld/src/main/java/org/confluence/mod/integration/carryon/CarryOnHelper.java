package org.confluence.mod.integration.carryon;

import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.init.block.NatureBlocks;

public class CarryOnHelper {
    public static boolean shouldDeny(BlockState blockState) {
        return blockState.getBlock() instanceof AltarBlock ||
                blockState.is(NatureBlocks.CRIMSON_HEART) ||
                blockState.is(NatureBlocks.SHADOW_ORB) ||
                blockState.hasProperty(StateProperties.HORIZONTAL_TWO_PART) ||
                blockState.hasProperty(StateProperties.VERTICAL_TWO_PART) ||
                blockState.hasProperty(StateProperties.HORIZONTAL_FOUR_PART) ||
                blockState.hasProperty(StateProperties.VERTICAL_FOUR_PART);
    }
}
