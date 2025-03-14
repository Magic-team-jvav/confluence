package org.confluence.mod.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.item.IconItems;
import org.confluence.terra_curio.integration.jei.JeiBackGround;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LootTableCategory implements IRecipeCategory<ModLootTables.Data> {
    public static final RecipeType<ModLootTables.Data> TYPE = RecipeType.create(Confluence.MODID, "loot_table", ModLootTables.Data.class);
    private static final Component TITLE = Component.translatable("title.confluence.loot_table");
    private static final IDrawable BACKGROUND = new JeiBackGround(128, 128, null);
    private final IDrawable icon;

    public LootTableCategory(IJeiHelpers jeiHelpers) {
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(IconItems.PRECIOUS_ICON.toStack());
    }

    @Override
    public RecipeType<ModLootTables.Data> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ModLootTables.Data data, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 64 - 8, 0).addItemStack(data.itemStack());
        int x = 0, y = 1;
        for (ModLootTables.Stack stack : data.stacks()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, x * 18, y * 18).addIngredients(STACK_TYPE, Collections.singletonList(stack));
            if (x < 6) {
                x++;
            } else {
                x = 0;
                y++;
            }
        }
    }

    public static void registerLootTables(IRecipeRegistration registration) {

        registration.addRecipes(TYPE, ModLootTables.LOOT_TABLES);
    }

    public static void registerLootTableCatalysts(IRecipeCatalystRegistration registration) {
        for (ModLootTables.Data data : ModLootTables.LOOT_TABLES) {
            registration.addRecipeCatalyst(data.itemStack(), LootTableCategory.TYPE);
        }
    }

    public static void registerStacks(IModIngredientRegistration registration) {
        List<ModLootTables.Stack> stackList = new ArrayList<>();
        for (ModLootTables.Data data : ModLootTables.LOOT_TABLES) {
            stackList.addAll(Arrays.asList(data.stacks()));
        }
        registration.register(STACK_TYPE, stackList, STACK_HELPER, STACK_RENDERER);
    }

    public static final IIngredientType<ModLootTables.Stack> STACK_TYPE = () -> ModLootTables.Stack.class;

    public static final IIngredientHelper<ModLootTables.Stack> STACK_HELPER = new IIngredientHelper<>() {
        @Override
        public IIngredientType<ModLootTables.Stack> getIngredientType() {
            return STACK_TYPE;
        }

        @Override
        public String getDisplayName(ModLootTables.Stack stack) {
            Component displayNameTextComponent = stack.itemStack().getHoverName();
            return displayNameTextComponent.getString();
        }

        @Override
        public String getUniqueId(ModLootTables.Stack stack, UidContext context) {
            return BuiltInRegistries.ITEM.getKey(stack.itemStack().getItem()).toString();
        }

        @Override
        public ResourceLocation getResourceLocation(ModLootTables.Stack stack) {
            return BuiltInRegistries.ITEM.getKey(stack.itemStack().getItem());
        }

        @Override
        public ModLootTables.Stack copyIngredient(ModLootTables.Stack stack) {
            return new ModLootTables.Stack(stack.itemStack().copy(), stack.min(), stack.max());
        }

        @Override
        public String getErrorInfo(@Nullable ModLootTables.Stack stack) {
            return "";
        }
    };

    public static final IIngredientRenderer<ModLootTables.Stack> STACK_RENDERER = new IIngredientRenderer<>() {
        @SuppressWarnings("all")
        @Override
        public void render(GuiGraphics guiGraphics, ModLootTables.Stack stack) {
            RenderSystem.enableDepthTest();
            Font font = Minecraft.getInstance().font;
            ItemStack itemStack = stack.itemStack();
            guiGraphics.renderFakeItem(itemStack, 0, 0);
            guiGraphics.pose().pushPose();

            if (stack.max() != 1) {
                String s = stack.min() + (stack.min() == stack.max() ? "" : "-" + stack.max());
                guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                guiGraphics.drawString(font, s, 19 - 2 - font.width(s), 6 + 3, 16777215, true);
            }

            guiGraphics.pose().popPose();
            net.neoforged.neoforge.client.ItemDecoratorHandler.of(itemStack).render(guiGraphics, font, itemStack, 0, 0);
            RenderSystem.disableBlend();
        }

        @SuppressWarnings("removal")
        @Override
        public List<Component> getTooltip(ModLootTables.Stack stack, TooltipFlag tooltipFlag) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            Item.TooltipContext tooltipContext = Item.TooltipContext.of(minecraft.level);
            return stack.itemStack().getTooltipLines(tooltipContext, player, tooltipFlag);
        }
    };
}
