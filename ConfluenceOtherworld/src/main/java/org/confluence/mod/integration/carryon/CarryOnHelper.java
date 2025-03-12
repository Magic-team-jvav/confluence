package org.confluence.mod.integration.carryon;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.confluence.mod.common.block.StateProperties;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.init.block.NatureBlocks;

public class CarryOnHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("carryon");

    public static boolean shouldDeny(BlockState blockState) {
        if (IS_LOADED) {
            return blockState.getBlock() instanceof AltarBlock ||
                    blockState.hasProperty(StateProperties.HORIZONTAL_TWO_PART) ||
                    blockState.hasProperty(StateProperties.VERTICAL_TWO_PART) ||
                    blockState.hasProperty(StateProperties.HORIZONTAL_FOUR_PART) ||
                    blockState.hasProperty(StateProperties.VERTICAL_FOUR_PART) ||
                    blockState.is(NatureBlocks.CRIMSON_HEART) ||
                    blockState.is(NatureBlocks.SHADOW_ORB);
        }
        return false;
    }
}
