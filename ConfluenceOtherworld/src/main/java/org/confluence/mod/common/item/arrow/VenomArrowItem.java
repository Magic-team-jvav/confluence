package org.confluence.mod.common.item.arrow;

import net.minecraft.world.entity.EntityType;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

public class VenomArrowItem extends BaseTerraArrowItem {
    public VenomArrowItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE), 7.0F);
    }

    @Override
    protected EntityType<? extends BaseArrowEntity> getEntityType() {
        return ModEntities.VENOM_ARROW.get();
    }
}
