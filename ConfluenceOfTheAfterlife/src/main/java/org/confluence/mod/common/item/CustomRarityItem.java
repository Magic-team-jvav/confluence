package org.confluence.mod.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.jetbrains.annotations.NotNull;

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
        super(properties.component(TCDataComponentTypes.MOD_RARITY, rarity));
    }

    protected void addAttributeModifiers(Consumer<ItemAttributeModifiers.Builder> consumer) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        consumer.accept(builder);
        this.modifiers = builder.build();
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return modifiers == null ? super.getDefaultAttributeModifiers(stack) : modifiers;
    }

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack pStack) {
        return withRarity(pStack, Component.translatable(getDescriptionId(pStack)));
    }

    protected MutableComponent withRarity(@NotNull ItemStack pStack, MutableComponent component) {
        return component.withStyle(style -> style.withColor(pStack.get(TCDataComponentTypes.MOD_RARITY).getColor()));
    }
}
