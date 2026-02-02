package org.confluence.terra_curio.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.recipe.ExtraStepStoolRecipe;
import org.confluence.terra_curio.common.recipe.WorkshopRecipe;

import java.util.function.Supplier;

public final class TCRecipes {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, TerraCurio.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, TerraCurio.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, TerraCurio.MODID);

    public static final Supplier<RecipeType<WorkshopRecipe>> WORKSHOP_TYPE = RECIPE_TYPES.register("workshop", () -> RecipeType.simple(TerraCurio.asResource("workshop")));
    public static final Supplier<RecipeSerializer<WorkshopRecipe>> WORKSHOP_SERIALIZER = RECIPE_SERIALIZERS.register("workshop", WorkshopRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<ExtraStepStoolRecipe>> EXTRA_STEP_STOOL_SERIALIZER = RECIPE_SERIALIZERS.register("extra_step_stool", ExtraStepStoolRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        INGREDIENT_TYPES.register(eventBus);
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
