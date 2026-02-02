package org.confluence.terraentity.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.init.item.TESpawnEggItems;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NPCTradeRecipeCategory implements IRecipeCategory<NPCRecipe> {
    public static final RecipeType<NPCRecipe> TYPE = RecipeType.create(TerraEntity.MODID, "npc_trade", NPCRecipe.class);
    private final IDrawable icon;

    public NPCTradeRecipeCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(TESpawnEggItems.GUIDE_SPAWN_EGG.get()));
    }

    @Override
    public RecipeType<NPCRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.terra_entity.npc_trade");
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, NPCRecipe recipe, IFocusGroup focusGroup) {

//        List<ITrade> trades = recipe.getRawTrades().getAllSupportedTrades();

        List<Ingredient> ingredients = recipe.trade.normalizeCost();
        if (ingredients.isEmpty()) {
//            return;
        }
        List<ItemStack> outputs = recipe.trade.normalizeResult();
        if (outputs.isEmpty()) {
//            return;
        }
        // input
        int size = ingredients.size();
        int line = size / 3;
        boolean remain = size % 3 != 0;
        int x = 0;
        int y = 0;
        if (line == 0) {
            y = 24;
        } else if (line == 1) {
            y = remain ? 16 : 24;
        } else if (line == 2) {
            y = remain ? 8 : 16;
        } else if (line == 3) {
            y = remain ? 0 : 8;
        }
        for (Ingredient ingredient : ingredients) {

            TEJeiPlugin.addInput(builder, x, y, ingredient);
            x += 16;
            if (x == 48) {
                x = 0;
                y += 16;
            }
        }
        // output

        builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 24).addItemStacks(outputs);
    }

    /*
    total width 128

    slot 1 - 0 with 16 width
    slot 2 - 16 with 16 width
    slot 3 - 32 with 16 width
    arrow right - 50 with 35 width
    result - 96 with 16 width
    icon - 50 with 16 width
    name - 58 centered
    arrow padding - 2 width

    output x - 48 + 1 + 22 + 1 = 72
    output end - 72 + 16 = 88

    lock condition - 8 width
    lock column = 128 - 88 = 40 / 8 = 5
     */

    @Override
    public void draw(NPCRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        TEJeiPlugin.drawArrowRight(guiGraphics, 49, 24, true);
        recipe.drawResultCallback.draw(guiGraphics, 49, 22);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(recipe.id);
        var item = DeferredSpawnEggItem.deferredOnlyById(type);
        if (item != null) {
            guiGraphics.renderItem(item.getDefaultInstance(), 49, 10);
        }
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, type.getDescription(), 56, 0, 0xFFFFFF);
        drawLock(guiGraphics, recipe.trade.lock(), 88, 0, (int) mouseX, (int) mouseY);
    }

    private void drawLock(GuiGraphics guiGraphics, ITradeLock lock, int x, int y, int mouseX, int mouseY) {
        var drawer = lock.getCodec().drawer();
        if (drawer == null) {
            return;
        }
        drawer.drawRecipe(lock, guiGraphics, x, y, mouseX, mouseY);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(NPCRecipe recipe) {
        List<ItemStack> outputs = recipe.trade.normalizeResult();
        if (outputs.isEmpty()) {
            return null;
        }
        return TerraEntity.space("npc_trade/" + recipe.id.getPath() + "/" + BuiltInRegistries.ITEM.getKey(outputs.get(0).getItem()).getPath());
    }
}
