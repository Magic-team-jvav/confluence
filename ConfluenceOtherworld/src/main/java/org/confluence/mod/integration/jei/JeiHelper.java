package org.confluence.mod.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.confluence.mod.mixin.accessor.KeyMappingAccessor;
import org.jetbrains.annotations.Nullable;

public class JeiHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("jei");
    private static final KeyMapping showUses = KeyMappingAccessor.getALL().get("key.jei.showUses");

    public static boolean handleShowUses(int keyCode, int scanCode, @Nullable ItemStack spawnEgg) {
        if (IS_LOADED && spawnEgg != null && ModJeiPlugin.jeiRuntime != null && showUses != null && showUses.matches(keyCode, scanCode)) {
            IFocus<ItemStack> focus = ModJeiPlugin.jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.CATALYST, VanillaTypes.ITEM_STACK, spawnEgg);
            ModJeiPlugin.jeiRuntime.getRecipesGui().show(focus);
            return true;
        }
        return false;
    }
}
