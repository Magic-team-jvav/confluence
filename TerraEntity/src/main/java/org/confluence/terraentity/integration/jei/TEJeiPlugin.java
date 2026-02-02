package org.confluence.terraentity.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHealth;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.confluence.terraentity.client.gui.container.TETradeScreen.MENU_LOCATION;

@JeiPlugin
public final class TEJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = TerraEntity.space("jei_plugin");
    public static final ResourceLocation ARROW_RIGHT = TerraEntity.space("textures/gui/sprites/random_gift.png");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(new NPCTradeRecipeCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        NPCTradeManager.Loader.getInstance().getTradeMap().forEach((npc, manager) -> {
            List<ITrade> trades = manager.getRawTrades().getAllSupportedTrades();
            if (ConfluenceMagicLib.IS_CONFLUENCE_LOADED.get()) {
                // 汇流加载时，替换掉配方
                if(npc.getNamespace().equals(TerraEntity.MODID)){
                    return;
                }
            }
            registration.addRecipes(NPCTradeRecipeCategory.TYPE, trades.stream().map(t->{
                NPCRecipe recipe = new NPCRecipe(t, TerraEntity.space(npc.getPath()));
                if(recipe.trade instanceof ITradeHealth re){
                    recipe.setDrawResultCallback((guiGraphics,x,y)->{
                        Font font = Minecraft.getInstance().font;
                        ResourceLocation iconSprite = TerraEntity.defaultPath("hud/heart/full");
                        guiGraphics.blitSprite(iconSprite, x + 46, y+2, 16, 16);
                        String s = "↑" + re.getHealth(Minecraft.getInstance().player);
                        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                        guiGraphics.drawString(font, s, x + 19 - 2 - font.width(s)+46, y + 11, 0x12bc63, true);
                    });
                }
                return recipe;
            }).filter(t -> {
                var cost = t.trade.normalizeCost();
                // var id = t.id;
                if (cost.isEmpty()) {
                    // TerraEntity.LOGGER.warn("NPC Trade Recipe {} has empty cost, skipping registration.", id);
                    // Reason: DeferredMoneyTradeItem
                    return false;
                }
                var result = t.trade.normalizeResult();
                if (result.isEmpty()) {
                    // TerraEntity.LOGGER.warn("NPC Trade Recipe {} has empty result, skipping registration.", id);
                    // Reason: ItemTradeLootTable
                    return false;
                }
                return true;
            }).toList());
        });
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//        registration.addRecipeCatalyst(TCItems.WORKSHOP.get().getDefaultInstance(), WorkshopCategory.TYPE);

    }

    public static void drawArrowRight(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(MENU_LOCATION,x,y,277,0,22,17,512,256);
    }

    public static void addInput(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient) {
        if (!ingredient.isEmpty()) {
            if (ingredient.getCustomIngredient() instanceof AmountIngredient amountIngredient) {
                builder.addInputSlot(x, y).addIngredients(VanillaTypes.ITEM_STACK, amountIngredient.getItems().toList());
            } else {
                builder.addInputSlot(x, y).addIngredients(ingredient);
            }
        }
    }
}
