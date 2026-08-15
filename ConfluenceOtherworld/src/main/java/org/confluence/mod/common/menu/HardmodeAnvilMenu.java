package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;
import org.confluence.lib.common.menu.ToggleAmountResultSlot;
import org.confluence.lib.common.recipe.MenuRecipeInput;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.HardmodeAnvilRecipe;

/**
 * 困难模式砧菜单。
 * <p>当前只处理秘银砧和山铜砧自己的配方列表；若后续需要把工作台类配方合并进同一界面，
 * 应在配方查询层统一扩展，而不是在菜单有效性判断里硬编码更多方块。</p>
 */
public class HardmodeAnvilMenu extends EitherAmountContainerMenu4x<MenuRecipeInput, HardmodeAnvilRecipe, ToggleAmountResultSlot<HardmodeAnvilRecipe>, ContainerLevelAccess> {
    public HardmodeAnvilMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public HardmodeAnvilMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.HARDMODE_ANVIL.get(), ModRecipes.HARDMODE_ANVIL_TYPE.get(), containerId, inventory, access, MenuRecipeInput::new, ToggleAmountResultSlot.For4x::new);
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> {
            BlockState blockState = level.getBlockState(pos);
            return (blockState.is(FunctionalBlocks.MYTHRIL_ANVIL.get()) ||
                    blockState.is(FunctionalBlocks.ORICHALCUM_ANVIL.get())) &&
                    player.canInteractWithBlock(pos, 4.0);
        }, true);
    }
}
