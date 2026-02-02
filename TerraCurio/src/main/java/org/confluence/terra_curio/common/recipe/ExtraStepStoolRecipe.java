package org.confluence.terra_curio.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCRecipes;
import org.confluence.terra_curio.common.item.curio.movement.StepStool;

public class ExtraStepStoolRecipe extends SmithingTransformRecipe {
    private static ExtraStepStoolRecipe INSTANCE;

    private ExtraStepStoolRecipe() {
        super(Ingredient.EMPTY, Ingredient.of(TCItems.STEP_STOOL.get(), TCItems.HAND_OF_CREATION.get()), Ingredient.of(TCItems.STEP_STOOL.get()), TCItems.STEP_STOOL.get().getDefaultInstance());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.EXTRA_STEP_STOOL_SERIALIZER.get();
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        ItemStack base = input.getItem(1);
        ItemStack addition = input.getItem(2);
        if (isBaseIngredient(base) && isAdditionIngredient(addition)) {
            CompoundTag tag = LibUtils.getItemStackNbtNoCopy(base);
            return tag.getInt("extraStep") + tag.getInt("extraStep") < 15;
        }
        return false;
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return stack.getItem() instanceof StepStool && LibUtils.getItemStackNbtNoCopy(stack).getInt("extraStep") < 15;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack base = input.getItem(1).copyWithCount(1);
        int additional = LibUtils.getItemStackNbtNoCopy(input.getItem(2)).getInt("extraStep");
        LibUtils.updateItemStackNbt(base, nbt -> nbt.putInt("extraStep", nbt.getInt("extraStep") + additional + 1));
        return base;
    }

    public static ExtraStepStoolRecipe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExtraStepStoolRecipe();
        }
        return INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<ExtraStepStoolRecipe> {
        public static final MapCodec<ExtraStepStoolRecipe> CODEC = MapCodec.unit(ExtraStepStoolRecipe::getInstance);
        public static final StreamCodec<RegistryFriendlyByteBuf, ExtraStepStoolRecipe> STREAM_CODEC = LibStreamCodecUtils.unit(ExtraStepStoolRecipe::getInstance);

        @Override
        public MapCodec<ExtraStepStoolRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExtraStepStoolRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
