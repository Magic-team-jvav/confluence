package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.ShadowflameArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

/// 暗影焰弓：把任何箭矢替换成暗影焰箭（不注册箭矢物品形态）。
public class ShadowflameBow extends BaseTerraBowItem {
    public ShadowflameBow(float baseDamage) {
        super(baseDamage, new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PINK), 0.45);
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new ShadowflameArrowEntity(ModEntities.SHADOWFLAME_ARROW.get(), shooter, ItemStack.EMPTY, weapon);
    }
}
