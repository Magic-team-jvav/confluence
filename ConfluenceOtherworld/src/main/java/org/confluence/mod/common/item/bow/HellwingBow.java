package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.HellBatArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import static org.confluence.lib.common.component.ModRarity.RED;

public class HellwingBow extends BaseTerraBowItem {
    public HellwingBow() {
        super(7.5F, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, RED));
    }

    @Override
    protected float getInaccuracy() { return 1f; }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new HellBatArrowEntity(ModEntities.HELL_BAT_ARROW.get(), shooter, ammo.copyWithCount(1), weapon);
    }
}
