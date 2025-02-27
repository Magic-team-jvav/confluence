package org.confluence.mod.common.item.vanity_armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.confluence.mod.common.init.ModArmorMaterials;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

public class BaseVanityArmorItem extends ArmorItem {
    public BaseVanityArmorItem(ArmorItem.Type type, ModRarity rarity) {
        this(ModArmorMaterials.VANITY_ARMOR_MATERIALS, type, new Properties(), rarity);
    }

    public BaseVanityArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, ModRarity rarity) {
        this(material, type, new Properties(), rarity);
    }

    public BaseVanityArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties, ModRarity rarity) {
        super(material, type, properties.stacksTo(1).component(TCDataComponentTypes.MOD_RARITY, rarity));
    }
}
