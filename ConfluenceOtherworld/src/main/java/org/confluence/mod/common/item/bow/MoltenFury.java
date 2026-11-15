package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.item.ArrowItems;

import static org.confluence.lib.common.component.ModRarity.ORANGE;

public class MoltenFury extends BaseTerraBowItem {
    public MoltenFury() {
        super(5.8F, new Properties().component(ConfluenceMagicLib.MOD_RARITY, ORANGE));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return ArrowItems.HELLFIRE_ARROW.get().createArrowEntity(shooter, ammo, weapon);
    }
}
