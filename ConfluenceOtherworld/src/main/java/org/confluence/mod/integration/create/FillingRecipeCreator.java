package org.confluence.mod.integration.create;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.apache.commons.lang3.mutable.MutableObject;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.recipe.ItemTransmutationRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FillingRecipeCreator {
    public static void createShimmer(MutableObject<Map<ResourceLocation, RecipeHolder<?>>> byName, MutableObject<Multimap<RecipeType<?>, RecipeHolder<?>>> byType) {
        List<RecipeHolder<FillingRecipe>> toAdd = new ArrayList<>();

        for (RecipeHolder<?> recipeHolder : byType.getValue().get(ModRecipes.ITEM_TRANSMUTATION_TYPE.get())) {
            ItemTransmutationRecipe recipe = (ItemTransmutationRecipe) recipeHolder.value();
            if (recipe.shrink() == 1 && recipe.target().size() == 1 && recipe.gamePhase() == GamePhase.BEFORE_SKELETRON) {
                ResourceLocation id = recipeHolder.id().withPrefix("create_compat_");
                FillingRecipe fillingRecipe = new StandardProcessingRecipe.Builder<>(FillingRecipe::new, id)
                        .withItemIngredients(recipe.source())
                        .withFluidIngredients(SizedFluidIngredient.of(ModFluids.SHIMMER.fluid().get(), 1))
                        .withSingleItemOutput(recipe.target().getFirst())
                        .build();
                toAdd.add(new RecipeHolder<>(id, fillingRecipe));
            }
        }

        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byName1 = ImmutableMap.builder();
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType1 = ImmutableMultimap.builder();
        for (RecipeHolder<FillingRecipe> recipeHolder : toAdd) {
            byName1.put(recipeHolder.id(), recipeHolder);
            byType1.put(AllRecipeTypes.FILLING.getType(), recipeHolder);
        }

        byName.setValue(byName1.putAll(byName.getValue()).build());
        byType.setValue(byType1.putAll(byType.getValue()).build());
    }
}
