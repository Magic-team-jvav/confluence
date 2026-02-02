package org.confluence.lib.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;

import java.util.function.Consumer;

public class CustomRarityItem extends Item {
    protected ItemAttributeModifiers modifiers;

    public CustomRarityItem(Properties properties) {
        this(properties, ModRarity.GRAY);
    }

    public CustomRarityItem(ModRarity rarity) {
        this(new Properties(), rarity);
    }

    public CustomRarityItem(Properties properties, ModRarity rarity) {
        super(properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    public CustomRarityItem addAttributeModifiers(Consumer<ItemAttributeModifiers.Builder> consumer) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        consumer.accept(builder);
        this.modifiers = builder.build();
        return this;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return modifiers == null ? super.getDefaultAttributeModifiers(stack) : modifiers;
    }
}
