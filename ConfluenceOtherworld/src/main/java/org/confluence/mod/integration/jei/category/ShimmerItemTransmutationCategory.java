package org.confluence.mod.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.recipe.ItemTransmutationRecipe;
import org.confluence.mod.integration.jei.ModJeiPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShimmerItemTransmutationCategory implements IRecipeCategory<ItemTransmutationRecipe> {
    public static final RecipeType<ItemTransmutationRecipe> TYPE = new RecipeType<>(Confluence.asResource("item_transmutation"), ItemTransmutationRecipe.class);
    private final IDrawable icon;

    public ShimmerItemTransmutationCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get()));
    }

    @Override
    public RecipeType<ItemTransmutationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.shimmer_transmutation");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ItemTransmutationRecipe recipe, IFocusGroup focuses) {
        // input
        ItemStack[] items = recipe.source().getItems();
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 56, 16);
        if (items.length > 1) {
            inputSlot.addIngredients(recipe.source());
        } else {
            ItemStack input = items.length == 0 ? new ItemStack(Items.BARRIER) : items[0].copy();
            input.setCount(recipe.shrink());
            inputSlot.addItemStack(input);
        }
        // output
        if (recipe.isValid()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 88).addItemStacks(recipe.target());
        } else {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 56, 88).addItemStack(Items.BARRIER.getDefaultInstance());
        }
    }

    @Override
    public void draw(ItemTransmutationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GamePhase gamePhase = recipe.gamePhase();
        if (gamePhase.isAboveThan(KillBoard.INSTANCE.getGamePhase())) {
            ModJeiPlugin.drawArrowDown(guiGraphics, 54, 46, false);
            if (mouseX >= 54 && mouseX <= 75 && mouseY >= 46 && mouseY <= 74) {
                Component text = Component.translatable("condition.confluence.shimmer_transmutation." + gamePhase.getSerializedName()).withStyle(style -> style.withColor(ChatFormatting.RED));
                guiGraphics.renderTooltip(Minecraft.getInstance().font, text, (int) mouseX, (int) mouseY);
            }
        } else {
            ModJeiPlugin.drawArrowDown(guiGraphics, 54, 46, recipe.isValid());
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ItemTransmutationRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (!recipe.isValid()) {
            tooltip.add(Component.translatable("tooltip.jei.shimmer_black_list").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(ItemTransmutationRecipe recipe) {
        List<ItemStack> target = recipe.target();
        if (target.isEmpty()) {
            return null;
        }
        return Confluence.asResource(recipe.getGroup() + "/" + BuiltInRegistries.ITEM.getKey(target.get(0).getItem()).getPath());
    }
}
