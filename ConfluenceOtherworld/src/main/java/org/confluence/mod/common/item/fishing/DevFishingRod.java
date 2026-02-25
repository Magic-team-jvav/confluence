package org.confluence.mod.common.item.fishing;

import net.minecraft.core.BlockPos;
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
            @Override
            public void catchingFish(BlockPos pos) {
                if (!entityData.get(DATA_BITING)) {
                    entityData.set(DATA_BITING, true);
                    this.nibble = 0x3F3F3F3F;
                }
            }

            @Override
            public boolean isOpenWaterFishing() {
                return true;
            }
        };
    }
}
