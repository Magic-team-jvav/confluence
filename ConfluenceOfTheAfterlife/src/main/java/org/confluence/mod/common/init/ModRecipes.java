package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.recipe.AltarRecipe;
import org.confluence.mod.common.recipe.SkyMillRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Confluence.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Confluence.MODID);

    public static final Supplier<RecipeType<AltarRecipe>> ALTAR_TYPE = TYPES.register("altar_type", AltarRecipe.Type::new);
    public static final Supplier<RecipeSerializer<?>> ALTAR_SERIALIZER = SERIALIZERS.register("altar", AltarRecipe.Serializer::new);
    public static final Supplier<RecipeType<SkyMillRecipe>> SKY_MILL_TYPE = TYPES.register("sky_mill_type", SkyMillRecipe.Type::new);
    public static final Supplier<RecipeSerializer<?>> SKY_MILL_SERIALIZER = SERIALIZERS.register("sky_mill", SkyMillRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
