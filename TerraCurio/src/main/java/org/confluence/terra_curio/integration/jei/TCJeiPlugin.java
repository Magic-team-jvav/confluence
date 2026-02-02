package org.confluence.terra_curio.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCRecipes;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public final class TCJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = TerraCurio.asResource("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(new WorkshopCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (!ConfluenceMagicLib.IS_CONFLUENCE_LOADED.get()) {
            registration.addItemStackInfo(TCItems.DEMON_HEART.get().getDefaultInstance(), Component.translatable("jei.tooltip.item.terra_curio.demon_heart.0"));
            registration.addItemStackInfo(TCItems.DIVING_HELMET.get().getDefaultInstance(), Component.translatable("jei.tooltip.item.terra_curio.diving_helmet.0"), Component.translatable("jei.tooltip.item.terra_curio.diving_helmet.1"));
            TCItems.CURIOS.getEntries().forEach(entry -> {
                if (entry.get() instanceof BaseCurioItem curioItem && curioItem.getJeiInformationCount() > 0) {
                    Component[] information = new Component[curioItem.getJeiInformationCount()];
                    for (int i = 0; i < information.length; i++) {
                        information[i] = Component.translatable("jei.tooltip." + curioItem.getDescriptionId() + "." + i);
                    }
                    registration.addItemStackInfo(entry.get().getDefaultInstance(), information);
                }
            });
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        RecipeManager recipeManager = level.getRecipeManager();
        registration.addRecipes(WorkshopCategory.TYPE, recipeManager.getAllRecipesFor(TCRecipes.WORKSHOP_TYPE.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(TCItems.WORKSHOP.get().getDefaultInstance(), WorkshopCategory.TYPE);
    }

    public static void drawArrowRight(GuiGraphics guiGraphics, int x, int y, boolean usable) {

    }
}
