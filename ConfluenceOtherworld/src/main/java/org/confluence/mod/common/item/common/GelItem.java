package org.confluence.mod.common.item.common;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.List;
import java.util.Optional;

public class GelItem extends ColoredItem {
    public GelItem() {
        super(new Properties().food(new FoodProperties(1, 0.15F, false, 0.5F, Optional.empty(), List.of(new FoodProperties.PossibleEffect(() -> new MobEffectInstance(ModEffects.CHOKING, 1200), 0.01F)))), ModRarity.WHITE);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(MaterialItems.GEL.get());
        setRGBA(itemStack, 0xFF66CCFF);
        return itemStack;
    }

}
