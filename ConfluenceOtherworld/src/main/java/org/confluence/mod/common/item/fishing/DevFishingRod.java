package org.confluence.mod.common.item.fishing;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.fishing.BaseFishingHook;
import org.confluence.mod.common.init.item.ModItems;

public class DevFishingRod extends AbstractFishingPole {
    public DevFishingRod() {
        super(new Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE), ModRarity.MASTER);
    }

    @Override
    public FishingHook getHook(ItemStack itemStack, Player player, Level level, int luckBonus, int speedBonus) {
        return new BaseFishingHook(player, level, luckBonus, speedBonus, BaseFishingHook.Variant.GOLDEN) {
            /// 开发竿要的是“无视等待时间，收线即上钩”。以前是覆盖 `catchingFish`，把 `nibble` 钉成
            /// `0x3F3F3F3F` 让钩子永远停在 BITING —— 等于把原版状态机整段拆掉：`nibble` 的递减
            /// 没人做了、`DATA_BITING` 永不复位、连挂在旱地上的钩子右键也会走
            /// `FishingHook#retrieve` 的 `nibble > 0` 分支刷战利品（每件物品再刷一个经验球）。
            /// 现在 tick 状态机完全交回原版，只在“玩家收线”的那一瞬间把咬钩状态补齐：
            /// 落水就能立刻收线拿货（手感、功能和以前一致），而钩子平时与原版完全相同，不留常驻脏状态。
            @Override
            public int retrieve(ItemStack stack) {
                if (this.nibble <= 0) this.nibble = 20;
                return super.retrieve(stack);
            }

            @Override
            public boolean isOpenWaterFishing() {
                return true;
            }
        };
    }
}
