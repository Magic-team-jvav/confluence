package org.confluence.mod.common.item.arrow;

import net.minecraft.world.entity.EntityType;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.WindArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

/// 风矢：箭速更慢、伤害更高，命中会减速敌人，并且能穿过一个敌怪。
public class WindArrowItem extends BaseTerraArrowItem {
    public WindArrowItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE), 3.0F);
    }

    @Override
    protected EntityType<? extends BaseArrowEntity> getEntityType() {
        return ModEntities.WIND_ARROW.get();
    }

    /// 让 IDE 与后续扩展知道该箭矢的运行实体类型。
    public Class<? extends WindArrowEntity> entityClass() {
        return WindArrowEntity.class;
    }
}
