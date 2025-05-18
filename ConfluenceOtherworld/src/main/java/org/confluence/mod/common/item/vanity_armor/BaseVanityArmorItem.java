package org.confluence.mod.common.item.vanity_armor;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModArmorMaterials;

import java.util.Collections;

public class BaseVanityArmorItem extends ArmorItem {
    public BaseVanityArmorItem(ArmorItem.Type type, ModRarity rarity) {
        this(ModArmorMaterials.VANITY_ARMOR_MATERIALS, type, new Properties(), rarity);
    }

    public BaseVanityArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, ModRarity rarity) {
        this(material, type, new Properties(), rarity);
    }

    public BaseVanityArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties, ModRarity rarity) {
        super(material, type, properties.stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.defaultModifiers = Suppliers.memoize(() -> new ItemAttributeModifiers(Collections.emptyList(), false));
    }
}
