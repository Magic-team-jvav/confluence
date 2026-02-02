package org.confluence.terra_furniture.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.mixed.LibShapedRecipePattern;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFRegistries;
import org.jetbrains.annotations.Nullable;

public class GlassKilnRecipe extends AbstractAmountRecipe<CraftingInput> {
    public final ShapedRecipePattern pattern;
    protected final float experience;
    protected final int cookingTime;

    public GlassKilnRecipe(ItemStack result, ShapedRecipePattern pattern, float experience, int cookingTime) {
        super(result, pattern.ingredients());
        this.pattern = pattern;
        this.experience = experience;
        this.cookingTime = cookingTime;
        LibShapedRecipePattern.setNonSymmetricalMatching(pattern);
    }

    public float getExperience() {
        return experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= pattern.width() && height >= pattern.height();
    }

    @Override
    public boolean matches(CraftingInput input, Level pLevel) {
        return pattern.matches(input);
    }

    @Override
    public ItemStack assembleAndExtract(CraftingInput input, HolderLookup.@Nullable Provider registries) {
        consumeShaped(input, 4, 4, pattern);
        return assemble(input, registries);
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(Ingredient::hasNoItems);
    }

    @Override
    protected int maxIngredientSize() {
        return 16;
    }

    @Override
    public String getGroup() {
        return "glass_kiln";
    }

    @Override
    public ItemStack getToastSymbol() {
        return TFBlocks.GLASS_KILN.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TFRegistries.GLASS_KILN_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TFRegistries.GLASS_KILN_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<GlassKilnRecipe> {
        public static final MapCodec<GlassKilnRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                Codec.FLOAT.lenientOptionalFieldOf("experience", 0.0F).forGetter(recipe -> recipe.experience),
                Codec.INT.lenientOptionalFieldOf("cookingtime", 100).forGetter(recipe -> recipe.cookingTime)
        ).apply(instance, GlassKilnRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, GlassKilnRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<GlassKilnRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GlassKilnRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static GlassKilnRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            return new GlassKilnRecipe(itemstack, shapedrecipepattern, buffer.readFloat(), buffer.readVarInt());
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, GlassKilnRecipe recipe) {
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookingTime);
        }
    }
}
