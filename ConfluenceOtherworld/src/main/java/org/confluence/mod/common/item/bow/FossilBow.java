package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.item.ArrowItems;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import static org.confluence.lib.common.component.ModRarity.BLUE;

public class FossilBow extends BaseTerraBowItem {
    public FossilBow() {
        super(4.6F, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return ArrowItems.FOSSIL_ARROW.get().createArrowEntity(shooter, ammo, weapon);
    }
}
