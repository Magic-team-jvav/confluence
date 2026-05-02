package org.confluence.mod.integration.prism_lib;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.util.PrefixUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/// [Prism Lib](https://www.curseforge.com/minecraft/mc-mods/prism-lib)
public class PrismLibHelper {
    public static final boolean IS_LOADED = LibUtils.isModLoaded("prism");
    public static final boolean LEGENDARY_TOOLTIPS = LibUtils.isModLoaded("legendarytooltips");
    private static TriState renderItemModel = TriState.DEFAULT;

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

    public static boolean shouldDisableAltImageTooltip() {
        if (LEGENDARY_TOOLTIPS) {
            if (PrismLibHelper.renderItemModel.isDefault()) {
                try {
                    Class<?> config = PrismLibHelper.class.getClassLoader().loadClass("com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig");
                    Object instance = config.getDeclaredMethod("getInstance").invoke(null);
                    Field renderItemModel = config.getDeclaredField("renderItemModel");
                    renderItemModel.setAccessible(true);
                    PrismLibHelper.renderItemModel = ((Enum<?>) ((Supplier<?>) renderItemModel.get(instance)).get()).ordinal() == 0 ? TriState.FALSE : TriState.TRUE;
                } catch (Exception e) {
                    PrismLibHelper.renderItemModel = TriState.FALSE;
                }
            }
            return PrismLibHelper.renderItemModel.isTrue();
        }
        return false;
    }

    public static boolean shouldSkipOriginalPrefixGather(ItemStack itemStack, List<Either<FormattedText, TooltipComponent>> tooltipElements) {
        if (LEGENDARY_TOOLTIPS) {
            if (tooltipElements.size() < 2) return false;
            Optional<FormattedText> displayName = tooltipElements.get(1).left();
            if (displayName.isPresent() && displayName.get() instanceof Component component) {
                PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
                if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
                    tooltipElements.set(1, Either.left(
                            prefix.getName().setStyle(component.getStyle()).append(Component.translatable("confluence.prefix_separator")).append(component)
                    ));
                    return true;
                }
            }
        }
        return false;
    }
}
