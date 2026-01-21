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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Items;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.armor.ArmorSetBonusData;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.confluence.mod.common.init.item.ArmorItems;

import java.util.Map;

public class ArmorSetBonusCategory implements IRecipeCategory<ArmorSetBonusCategory.Holder> {
    public static final RecipeType<Holder> TYPE = RecipeType.create(Confluence.MODID, "armor_set_bonus", Holder.class);
    private final IDrawable icon;

    public ArmorSetBonusCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(ArmorItems.TITANIUM_CHESTPLATE.toStack());
    }

    @Override
    public RecipeType<Holder> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.confluence.armor_set_bonus");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 144;
    }

    @Override
    public int getHeight() {
        return 112;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Holder holder, IFocusGroup focuses) {
        IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.CATALYST, 0, 0).setStandardSlotBackground();
        if (holder.key.head() != Items.AIR) {
            slotBuilder.addItemStack(holder.key.head().getDefaultInstance());
        }
        slotBuilder = builder.addSlot(RecipeIngredientRole.CATALYST, 0, 18).setStandardSlotBackground();
        if (holder.key.chest() != Items.AIR) {
            slotBuilder.addItemStack(holder.key.chest().getDefaultInstance());
        }
        slotBuilder = builder.addSlot(RecipeIngredientRole.CATALYST, 0, 36).setStandardSlotBackground();
        if (holder.key.legs() != Items.AIR) {
            slotBuilder.addItemStack(holder.key.legs().getDefaultInstance());
        }
        slotBuilder = builder.addSlot(RecipeIngredientRole.CATALYST, 0, 54).setStandardSlotBackground();
        if (holder.key.feet() != Items.AIR) {
            slotBuilder.addItemStack(holder.key.feet().getDefaultInstance());
        }
    }

    @Override
    public ResourceLocation getRegistryName(Holder holder) {
        return holder.key.getId().withPrefix("armor_set_bonus/");
    }

    @Override
    public void draw(Holder holder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (holder.data.tooltipCount() == 0) return;
        String descriptionKey = holder.key.getDescriptionKey();
        Font font = Minecraft.getInstance().font;
        int y = 0;
        for (int i = 0; i < holder.data.tooltipCount(); i++) {
            Component number = Component.literal((i + 1) + ". ").withStyle(ChatFormatting.DARK_GREEN);
            guiGraphics.drawString(font, number, 18, y, -1, false);
            int w = font.width(number);
            Component text = Component.translatable("armor_set_bonus." + descriptionKey + "." + i).withStyle(ChatFormatting.BLACK);
            for (FormattedCharSequence sequence : font.split(text, 126 - w)) {
                guiGraphics.drawString(font, sequence, 18 + w, y, -1, false);
                y += font.lineHeight + 1;
            }
        }
    }

    public record Holder(ArmorSetBonusKey key, ArmorSetBonusData data) {
        public Holder(Map.Entry<ArmorSetBonusKey, ArmorSetBonusData> entry) {
            this(entry.getKey(), entry.getValue());
        }
    }
}
