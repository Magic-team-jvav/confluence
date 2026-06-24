package org.confluence.mod.common.item.arrow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.FlyFishArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

public class FlyFishArrowItem extends BaseTerraArrowItem {
    public FlyFishArrowItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.WHITE));
    }

    @Override
    protected BaseArrowEntity createArrowEntity(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new FlyFishArrowEntity(ModEntities.FLY_FISH_ARROW.get(), shooter, ammo, weapon);
    }
}
