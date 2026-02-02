package org.confluence.lib.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.confluence.lib.ConfluenceMagicLib;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

public record AmountIngredient(Ingredient ingredient, int amount) implements ICustomIngredient {
    public static final Ingredient EMPTY = new Ingredient(new AmountIngredient(Ingredient.EMPTY, 0));
    public static final MapCodec<AmountIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.lenientOptionalFieldOf("ingredient", Ingredient.EMPTY).forGetter(AmountIngredient::ingredient),
            ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("count", 0).forGetter(AmountIngredient::amount)
    ).apply(instance, AmountIngredient::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AmountIngredient> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    @Override
    public Stream<ItemStack> getItems() {
        return Arrays.stream(ingredient.getItems()).peek(itemStack -> itemStack.setCount(amount));
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) return false;
        if (stack.getCount() < amount) {
            return false;
        } else {
            return ingredient.test(stack);
        }
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<AmountIngredient> getType() {
        return ConfluenceMagicLib.AMOUNT_INGREDIENT_TYPE.get();
    }

    public static int getAmount(Ingredient ingredient) {
        return ingredient.getCustomIngredient() instanceof AmountIngredient ai ? ai.amount : 1;
    }

    public static Ingredient of(int amount, Ingredient ingredient) {
        return new AmountIngredient(ingredient, amount).toVanilla();
    }

    public static Ingredient of(int amount, ItemLike... items) {
        return new AmountIngredient(Ingredient.of(items), amount).toVanilla();
    }

    public static Ingredient of(int amount, ItemStack... stacks) {
        return new AmountIngredient(Ingredient.of(stacks), amount).toVanilla();
    }

    public static Ingredient of(int amount, TagKey<Item> tag) {
        return new AmountIngredient(Ingredient.of(tag), amount).toVanilla();
    }

    public static Ingredient of(ItemStack stack) {
        return new AmountIngredient(Ingredient.of(stack), stack.getCount()).toVanilla();
    }
}
