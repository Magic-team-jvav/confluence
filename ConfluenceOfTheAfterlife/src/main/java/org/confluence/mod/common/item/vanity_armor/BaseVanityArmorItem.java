package org.confluence.mod.common.item.vanity_armor;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
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

    public boolean makesPiglinsNeutral(Player player, ItemStack stack) {
        return false;
    }

    @Override
    public MutableComponent getName(ItemStack pStack) {
        return Component.translatable(getDescriptionId(pStack)).withColor(pStack.get(TCDataComponentTypes.MOD_RARITY).getColor());
    }
}
