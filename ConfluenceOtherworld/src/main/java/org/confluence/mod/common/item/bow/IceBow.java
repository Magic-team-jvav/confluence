package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.FrostArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

/// 冰雪弓：把任何箭矢替换成高速的寒霜箭（不注册箭矢物品形态）。
public class IceBow extends BaseTerraBowItem {
    public IceBow(float baseDamage) {
        super(baseDamage, new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new FrostArrowEntity(ModEntities.FROST_ARROW.get(), shooter, ItemStack.EMPTY, weapon);
    }
}
