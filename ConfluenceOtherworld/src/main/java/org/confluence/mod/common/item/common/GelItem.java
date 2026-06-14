package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.MaterialItems;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.IPortFoodProperties;

import java.util.List;

public class GelItem extends ColoredItem {
    public GelItem() {
        super(new Properties().food(createFoodProperties()), ModRarity.WHITE);
    }

    private static FoodProperties createFoodProperties() {
        FoodProperties properties = new FoodProperties.Builder()
                .nutrition(1)
                .saturationMod(0.15F)
                .effect(() -> new MobEffectInstance(ModEffects.CHOKING.get(), 1200), 0.01F)
                .build();
        IPortFoodProperties.of(properties).portlib$setEatSeconds(0.5F);
        return properties;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(MaterialItems.GEL.get());
        setRGBA(itemStack, 0xFF66CCFF);
        return itemStack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.gel.0").withStyle(ChatFormatting.GRAY));
    }
}
