package org.confluence.mod.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.ToolItems;

import java.util.List;

public record ItemTransmutationRecipe(Ingredient source, List<ItemStack> target, int shrink, GamePhase gamePhase) implements Recipe<SingleRecipeInput> {
    public boolean isValid() {
        return shrink > 0 && !target.isEmpty();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return input.item().getCount() >= shrink && source.test(input.item()) && KillBoard.INSTANCE.getGamePhase().isAtLeast(gamePhase);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ITEM_TRANSMUTATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ITEM_TRANSMUTATION_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, source);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String getGroup() {
        return "item_transmutation";
    }

    @Override
    public ItemStack getToastSymbol() {
        return ToolItems.BOTTOMLESS_SHIMMER_BUCKET.toStack();
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(SingleRecipeInput input) {
        return NonNullList.withSize(1, ItemStack.EMPTY);
    }

    public static class Serializer implements RecipeSerializer<ItemTransmutationRecipe> {
        public static final MapCodec<ItemTransmutationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("source").forGetter(ItemTransmutationRecipe::source),
                ItemStack.CODEC.listOf().lenientOptionalFieldOf("target", List.of()).forGetter(ItemTransmutationRecipe::target),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("shrink", 1).forGetter(ItemTransmutationRecipe::shrink),
                GamePhase.CODEC.lenientOptionalFieldOf("game_phase", GamePhase.BEFORE_SKELETRON).forGetter(ItemTransmutationRecipe::gamePhase)
        ).apply(instance, ItemTransmutationRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemTransmutationRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ItemTransmutationRecipe::source,
                ItemStack.LIST_STREAM_CODEC, ItemTransmutationRecipe::target,
                ByteBufCodecs.VAR_INT, ItemTransmutationRecipe::shrink,
                GamePhase.STREAM_CODEC, ItemTransmutationRecipe::gamePhase,
                ItemTransmutationRecipe::new
        );

        @Override
        public MapCodec<ItemTransmutationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ItemTransmutationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
