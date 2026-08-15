package org.confluence.mod.client.handler;

import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.Nullable;

/**
 * 魂师界面的临时开发门禁。
 *
 * <p>总览界面、快捷选择界面和常驻技能栏必须共用这里的判断，避免只隐藏绘制，
 * 但按键和滚轮仍能在后台修改魂师状态。主手或副手持有测试物品都视为“手持”。</p>
 */
public final class SoulGuiAccess {
    private SoulGuiAccess() {}

    public static boolean isAllowed(@Nullable Player player) {
        return player != null
                && player.isHolding(stack -> stack.is(ModItems.TEST_SOUL_GUI.get()));
    }
}
