package org.confluence.terraentity.registries.chester;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;

/**
 * 切斯特绑定并可以打开的方块菜单
 */
public class ChesterConditionalType {

    int priority;
    TriFunction<BlockPos, Player, Level,  Boolean> canOpen;
    TriFunction<BlockPos, Player, Level, ? extends MenuProvider> menuProviderFunc;
    public ChesterConditionalType(int priority, TriFunction<BlockPos, Player, Level, Boolean> canOpen, TriFunction<BlockPos, Player, Level, ? extends MenuProvider> menuProviderFunc) {
        this.priority = priority;
        this.canOpen = canOpen;
        this.menuProviderFunc = menuProviderFunc;
    }

    public boolean canOpen(BlockPos pos, Player player, Level level) {
        return canOpen.apply(pos, player,level);
    }

    public boolean tryOpen(BlockPos pos, Player player, Level level) {
        if(canOpen.apply(pos, player, level)) {
            MenuProvider provider = menuProviderFunc.apply(pos, player, level);
            if(provider == null) {
                return false;
            }
            player.openMenu(provider);
            return true;
        }
        return false;
    }

    public int getPriority() {
        return priority;
    }

}
