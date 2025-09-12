package org.confluence.mod.integration.legendarytooltips;

import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.confluence.lib.common.component.ModRarity;
import org.jetbrains.annotations.Nullable;

public class LegendaryTooltipsHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("legendarytooltips");

    public static @Nullable TextColor rarityColor(ItemStack itemStack) {
        ModRarity rarity = ModRarity.getModRarity(itemStack, false);
        return rarity == null ? null : rarity.asTextColor();
    }
}
