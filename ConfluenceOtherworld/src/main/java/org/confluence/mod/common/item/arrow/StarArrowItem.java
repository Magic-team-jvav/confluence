package org.confluence.mod.common.item.arrow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.StarArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

public class StarArrowItem extends BaseTerraArrowItem {
    public StarArrowItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE));
    }

    @Override
    protected BaseArrowEntity createArrowEntity(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new StarArrowEntity(ModEntities.STAR_ARROW.get(), shooter, ammo, weapon);
    }
}
