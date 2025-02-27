package org.confluence.mod.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

import java.util.function.Consumer;

@javax.annotation.ParametersAreNonnullByDefault
@net.minecraft.MethodsReturnNonnullByDefault
public class CustomRarityItem extends Item {
    protected ItemAttributeModifiers modifiers;

    public CustomRarityItem(Properties properties) {
        this(properties, ModRarity.GRAY);
    }

    public CustomRarityItem(ModRarity rarity) {
        this(new Properties(), rarity);
    }

    public CustomRarityItem(Properties properties, ModRarity rarity) {
        super(properties.component(TCDataComponentTypes.MOD_RARITY, rarity));
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
