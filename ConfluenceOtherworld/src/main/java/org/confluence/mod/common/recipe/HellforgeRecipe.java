package org.confluence.mod.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class HellforgeRecipe extends AbstractAmountRecipe<RecipeInput> {
    protected final float experience;
    protected final int cookingTime;
    protected final boolean requiresFuel;

    public HellforgeRecipe(ItemStack result, NonNullList<Ingredient> ingredients, float experience, int cookingTime, boolean requiresFuel) {
        super(result, ingredients);
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.requiresFuel = requiresFuel;
    }

    public float getExperience() {
        return experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public boolean isRequiresFuel() {
        return requiresFuel;
    }

    @Override
    protected int maxIngredientSize() {
        return 4;
    }

    @Override
    public String getGroup() {
        return "hellforge";
    }

    @Override
    public ItemStack getToastSymbol() {
        return FunctionalBlocks.HELLFORGE.toStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HELLFORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HELLFORGE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<HellforgeRecipe> {
        public static final MapCodec<HellforgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                INGREDIENTS_CODEC.forGetter(recipe -> recipe.ingredients),
                Codec.FLOAT.lenientOptionalFieldOf("experience", 0.0F).forGetter(recipe -> recipe.experience),
                Codec.INT.lenientOptionalFieldOf("cookingtime", 100).forGetter(recipe -> recipe.cookingTime),
                Codec.BOOL.lenientOptionalFieldOf("requires_fuel", false).forGetter(recipe -> recipe.requiresFuel)
        ).apply(instance, HellforgeRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, HellforgeRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<HellforgeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HellforgeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static HellforgeRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, AmountIngredient.EMPTY);
            nonnulllist.replaceAll(ignore -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            return new HellforgeRecipe(itemstack, nonnulllist, buffer.readFloat(), buffer.readVarInt(), buffer.readBoolean());
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, HellforgeRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookingTime);
            buffer.writeBoolean(recipe.requiresFuel);
        }
    }
}
