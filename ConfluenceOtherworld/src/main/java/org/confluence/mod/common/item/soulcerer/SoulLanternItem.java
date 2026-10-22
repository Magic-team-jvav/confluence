package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;

import javax.annotation.ParametersAreNonnullByDefault;

/// 魂灯主武器：在通用灵魂武器之上提供物品自身的基础暴击率，使魂灯一系共享同一套灵魂资源与施法入口。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulLanternItem extends SoulWeaponItem {

    public SoulLanternItem(Item.Properties properties, ModRarity rarity, SoulWeaponProperties soulProperties) {
        super(properties, rarity, soulProperties);
    }
}
