package org.confluence.mod.common.recipe;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import PortLib.extensions.net.minecraft.world.item.crafting.Ingredient.PortIngredientExtension;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import org.mesdag.portlib.wrapper.world.item.crafting.PortRecipeInput;

public abstract class EnhancedForgeRecipe extends AbstractAmountRecipe<PortRecipeInput> {
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
                PortItemStackExtension.strictCodec().fieldOf("result").forGetter(recipe -> recipe.result),
                INGREDIENTS_CODEC.forGetter(R::getIngredients),
                PortCodecExtension.lenientOptionalFieldOf(Codec.FLOAT, "experience", 0.0F).forGetter(R::getExperience),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "cookingtime", 100).forGetter(R::getCookingTime),
                PortCodecExtension.lenientOptionalFieldOf(Codec.BOOL, "requires_fuel", false).forGetter(R::isRequiresFuel)
        ).apply(instance, factory));
    }

    public static <R extends EnhancedForgeRecipe> PortStreamCodec<PortRegistryFriendlyByteBuf, R> streamCodec(Factory<R> factory) {
        return new PortStreamCodec<>() {
            @Override
            public R decode(PortRegistryFriendlyByteBuf buffer) {
                int size = buffer.readVarInt();
                NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, AmountIngredient.EMPTY);
                nonnulllist.replaceAll(ignore -> PortIngredientExtension.contentsStreamCodec().decode(buffer));
                ItemStack itemstack = PortItemStackExtension.streamCodec().decode(buffer);
                return factory.create(itemstack, nonnulllist, buffer.readFloat(), buffer.readVarInt(), buffer.readBoolean());
            }

            @Override
            public void encode(PortRegistryFriendlyByteBuf buffer, R recipe) {
                buffer.writeVarInt(recipe.ingredients.size());
                for (Ingredient ingredient : recipe.ingredients) {
                    PortIngredientExtension.contentsStreamCodec().encode(buffer, ingredient);
                }
                PortItemStackExtension.streamCodec().encode(buffer, recipe.result);
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
