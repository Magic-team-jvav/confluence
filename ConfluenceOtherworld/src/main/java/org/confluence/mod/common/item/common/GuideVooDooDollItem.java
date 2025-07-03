package org.confluence.mod.common.item.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.LibUtils;

public class GuideVooDooDollItem extends TooltipItem {
    public GuideVooDooDollItem() {
        super(new Item.Properties(), ModRarity.WHITE, "tooltip.item.confluence.guide_voodoo_doll.0");
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        if (damageSource.is(DamageTypes.LAVA)) {
            CompoundTag tag = LibUtils.getItemStackNbtIfPresent(itemEntity.getItem());
            if (tag == null) {
                // 按绝对坐标决定
            } else {
                // 按玩家朝向决定
            }
        }
    }
}
