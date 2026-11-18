package org.confluence.mod.common.item.arrow;

import net.minecraft.world.entity.EntityType;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

public class ChlorophyteArrowItem extends BaseTerraArrowItem {
    public ChlorophyteArrowItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIME), 5.5F);
    }

    @Override
    protected EntityType<? extends BaseArrowEntity> getEntityType() {
        return ModEntities.CHLOROPHYTE_ARROW.get();
    }
}
