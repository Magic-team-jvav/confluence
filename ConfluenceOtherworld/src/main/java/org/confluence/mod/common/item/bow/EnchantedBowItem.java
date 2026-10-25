package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.item.ArrowItems;
import org.jetbrains.annotations.Nullable;

/// 附魔弓：把普通木箭替换为已有的耀星之箭。无耐久。
public class EnchantedBowItem extends BaseTerraBowItem {
    public EnchantedBowItem(float baseDamage) {
        super(baseDamage, new Item.Properties());
    }

    @Override
    public @Nullable BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        // 只替换普通木箭，其它箭矢保持原样。
        if (!ammo.is(Items.ARROW)) return null;
        // 直接复用耀星之箭自己的实体创建流程，转化结果与真正的耀星之箭完全一致。
        return ArrowItems.STAR_ARROW.get().createArrowEntity(shooter, ArrowItems.STAR_ARROW.get().getDefaultInstance(), weapon);
    }
}
