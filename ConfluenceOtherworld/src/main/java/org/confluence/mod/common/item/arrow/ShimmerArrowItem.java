package org.confluence.mod.common.item.arrow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.ShimmerArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.portlib.wrapper.world.item.PortItem;

public class ShimmerArrowItem extends BaseTerraArrowItem {
    public ShimmerArrowItem() {
        super(new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.WHITE));
    }

    @Override
    protected BaseArrowEntity createArrowEntity(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new ShimmerArrowEntity(ModEntities.SHIMMER_ARROW.get(), shooter, ammo, weapon);
    }
}
