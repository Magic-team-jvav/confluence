package org.confluence.mod.common.recipe;

import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.AmountIngredient;

public abstract class EnhancedForgeRecipe extends AbstractAmountRecipe<RecipeInput> {
    protected final float experience;
    protected final int cookingTime;
    protected final boolean requiresFuel;

    public EnhancedForgeRecipe(ItemStack result, NonNullList<Ingredient> ingredients, float experience, int cookingTime, boolean requiresFuel) {
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

    public static <R extends EnhancedForgeRecipe> MapCodec<R> codec(Factory<R> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                INGREDIENTS_CODEC.forGetter(R::getIngredients),
                Codec.FLOAT.lenientOptionalFieldOf("experience", 0.0F).forGetter(R::getExperience),
                Codec.INT.lenientOptionalFieldOf("cookingtime", 100).forGetter(R::getCookingTime),
                Codec.BOOL.lenientOptionalFieldOf("requires_fuel", false).forGetter(R::isRequiresFuel)
        ).apply(instance, factory));
    }

    public static <R extends EnhancedForgeRecipe> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(Factory<R> factory) {
        return new StreamCodec<>() {
            @Override
            public R decode(RegistryFriendlyByteBuf buffer) {
                int size = buffer.readVarInt();
                NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, AmountIngredient.EMPTY);
                nonnulllist.replaceAll(ignore -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
                return factory.create(itemstack, nonnulllist, buffer.readFloat(), buffer.readVarInt(), buffer.readBoolean());
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, R recipe) {
                buffer.writeVarInt(recipe.ingredients.size());
                for (Ingredient ingredient : recipe.ingredients) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                }
                ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                buffer.writeFloat(recipe.experience);
                buffer.writeVarInt(recipe.cookingTime);
                buffer.writeBoolean(recipe.requiresFuel);
            }
        };
    }

    public interface Factory<R extends EnhancedForgeRecipe> extends Function5<ItemStack, NonNullList<Ingredient>, Float, Integer, Boolean, R> {
        R create(ItemStack result, NonNullList<Ingredient> ingredients, float experience, int cookingTime, boolean requiresFuel);

        @Override
        default R apply(ItemStack itemStack, NonNullList<Ingredient> ingredients, Float aFloat, Integer integer, Boolean aBoolean) {
            return create(itemStack, ingredients, aFloat, integer, aBoolean);
        }
    }
}
