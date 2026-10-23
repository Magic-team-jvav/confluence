package org.confluence.mod.common.item.arrow;

import net.minecraft.world.entity.EntityType;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

/// 坠落之星箭：附魔弓替换木箭时使用的箭矢，无重力、发光。
public class FallenStarArrowItem extends BaseTerraArrowItem {
    public FallenStarArrowItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE), 2.0F);
    }

    @Override
    protected EntityType<? extends BaseArrowEntity> getEntityType() {
        return ModEntities.FALLEN_STAR_ARROW.get();
    }
}
