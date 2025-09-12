package org.confluence.mod.integration.prism_lib;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.confluence.lib.common.component.ModRarity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * <a href="https://www.curseforge.com/minecraft/mc-mods/prism-lib">Prism Lib</a>
 */
public class PrismLibHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("prism");

    public static @Nullable TextColor getRarityColor(ItemStack itemStack) {
        ModRarity rarity = ModRarity.getModRarity(itemStack, false);
        return rarity == null ? null : rarity.asTextColor();
    }

    public static @Nullable Pair<Supplier<Integer>, Supplier<Integer>> getSpecialColor(ItemStack itemStack) {
        ModRarity rarity = ModRarity.getModRarity(itemStack, false);
        if (rarity != null && rarity.isSpecial()) {
            Supplier<Integer> supplier = rarity::color;
            return new Pair<>(supplier, supplier);
        }
        return null;
    }
}
