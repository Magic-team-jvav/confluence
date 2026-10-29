package org.confluence.mod.common.item.fishing;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.fishing.BaseFishingHook;
import org.confluence.mod.mixed.IFishingHook;

public class DevFishingRod extends AbstractFishingPole {
    public DevFishingRod() {
        super(new Properties().unbreakable(), ModRarity.MASTER);
    }

    @Override
    public FishingHook getHook(ItemStack itemStack, Player player, Level level, int luckBonus, int speedBonus) {
        return new BaseFishingHook(player, level, luckBonus, speedBonus, BaseFishingHook.Variant.GOLDEN) {
            /// 有效液体中收线即可捕获；平时保留原版计时，离开液体后不保留强制咬钩状态。
            @Override
            public int retrieve(ItemStack stack) {
                if (!level().isClientSide && !isRemoved() && getHookedIn() == null) {
                    this.nibble = getInBlockState().getFluidState().is(IFishingHook.isValidFluid(this)) ? 20 : 0;
                }
                return super.retrieve(stack);
            }

            /// 开发竿用于测试鱼获，不受开阔水域限制。
            @Override
            public boolean isOpenWaterFishing() {
                return true;
            }
        };
    }
}
