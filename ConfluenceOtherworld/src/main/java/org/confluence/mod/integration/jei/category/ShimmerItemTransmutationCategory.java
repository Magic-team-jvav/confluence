package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.recipe.ItemTransmutationRecipe;
import org.confluence.mod.integration.jei.ModJeiPlugin;

public class ShimmerItemTransmutationCategory implements IRecipeCategory<RecipeHolder<ItemTransmutationRecipe>> {
    public static final RecipeType<RecipeHolder<ItemTransmutationRecipe>> TYPE = RecipeType.createRecipeHolderType(Confluence.asResource("item_transmutation"));
    private static final Component TITLE = Component.translatable("title.confluence.shimmer_transmutation");
    private final IDrawable icon;

    public ShimmerItemTransmutationCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get()));
    }

    @Override
    public RecipeType<RecipeHolder<ItemTransmutationRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 128;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ItemTransmutationRecipe> recipe, IFocusGroup focuses) {
        // input
        ItemTransmutationRecipe value = recipe.value();
        ItemStack[] items = value.source().getItems();
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 56, 16);
        if (items.length > 1) {
            inputSlot.addIngredients(value.source());
        } else {
            ItemStack input = items.length == 0 ? new ItemStack(Items.BARRIER) : items[0].copy();
            input.setCount(value.shrink());
            inputSlot.addItemStack(input);
        }
        // output
        if (value.isValid()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 88).addItemStacks(value.target());
        } else {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 56, 88).addItemStack(Items.BARRIER.getDefaultInstance());
        }
    }

    @Override
    public void draw(RecipeHolder<ItemTransmutationRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GamePhase gamePhase = recipe.value().gamePhase();
        if (gamePhase.isAboveThan(KillBoard.INSTANCE.getGamePhase())) {
            ModJeiPlugin.drawArrowDown(guiGraphics, 54, 46, false);
            if (mouseX >= 54 && mouseX <= 75 && mouseY >= 46 && mouseY <= 74) {
                Component text = Component.translatable("condition.confluence.shimmer_transmutation." + gamePhase.getSerializedName()).withStyle(style -> style.withColor(ChatFormatting.RED));
                guiGraphics.renderTooltip(Minecraft.getInstance().font, text, (int) mouseX, (int) mouseY);
            }
        } else {
            ModJeiPlugin.drawArrowDown(guiGraphics, 54, 46, recipe.value().isValid());
        }
    }
}
