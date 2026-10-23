package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.FallenStarArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

/// 附魔弓：把普通木箭替换为坠落之星箭。
public class EnchantedBowItem extends BaseTerraBowItem {
    public EnchantedBowItem(float baseDamage, int durability) {
        super(baseDamage, new Item.Properties().durability(durability));
    }

    @Override
    public @Nullable BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        // 只替换普通木箭，其它箭矢保持原样。
        if (!ammo.is(Items.ARROW)) return null;
        FallenStarArrowEntity arrow = new FallenStarArrowEntity(ModEntities.FALLEN_STAR_ARROW.get(), shooter, ammo.copyWithCount(1), weapon);
        arrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        arrow.setOwner(shooter);
        return arrow;
    }
}
