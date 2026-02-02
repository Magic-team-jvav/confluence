package org.confluence.terraentity.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * <P>当右手持物品左键时触发</P>
 * <p>玩家的鼠标状态存储在{@link org.confluence.terraentity.attachment.WeaponStorage#leftClicking}</p>
 */
public interface ILeftClickStateItem {

    void onLeftClick(Player player, ItemStack itemStack);

    void onLeftRelease(Player player, ItemStack itemStack);

    default void onWhellScroll(Player player, ItemStack itemStack, int scrollAmount){

    }

    boolean canSwitchWithoutRelease(Player player, ItemStack itemStack);

}
