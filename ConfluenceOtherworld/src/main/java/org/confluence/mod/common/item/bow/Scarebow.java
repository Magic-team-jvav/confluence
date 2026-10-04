package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.DriveAwayArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

import static org.confluence.lib.common.component.ModRarity.BLUE;

public class Scarebow extends BaseTerraBowItem {
    public Scarebow() {
        super(3.5F, new Properties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return DriveAwayArrowEntity.create(ModEntities.DRIVE_AWAY_ARROW.get(), shooter, ammo.copyWithCount(1), weapon);
    }
}
