package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.recipe.*;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Confluence.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Confluence.MODID);

    public static final Supplier<RecipeType<AltarRecipe>> ALTAR_TYPE = registerType("altar");
    public static final Supplier<RecipeSerializer<?>> ALTAR_SERIALIZER = SERIALIZERS.register("altar", AltarRecipe.Serializer::new);
    public static final Supplier<RecipeType<SkyMillRecipe>> SKY_MILL_TYPE = registerType("sky_mill");
    public static final Supplier<RecipeSerializer<?>> SKY_MILL_SERIALIZER = SERIALIZERS.register("sky_mill", SkyMillRecipe.Serializer::new);
    public static final Supplier<RecipeType<HeavyWorkBenchRecipe>> HEAVY_WORK_BENCH_TYPE = registerType("heavy_work_bench");
    public static final Supplier<RecipeSerializer<?>> HEAVY_WORK_BENCH_SERIALIZER = SERIALIZERS.register("heavy_work_bench", HeavyWorkBenchRecipe.Serializer::new);
    public static final Supplier<RecipeType<HellforgeRecipe>> HELLFORGE_TYPE = registerType("hellforge");
    public static final Supplier<RecipeSerializer<?>> HELLFORGE_SERIALIZER = SERIALIZERS.register("hellforge", HellforgeRecipe.Serializer::new);
    public static final Supplier<RecipeType<FletchingTableRecipe>> FLETCHING_TABLE_TYPE = registerType("fletching_table");
    public static final Supplier<RecipeSerializer<?>> FLETCHING_TABLE_SERIALIZER = SERIALIZERS.register("fletching_table", FletchingTableRecipe.Serializer::new);

    private static <R extends Recipe<?>> Supplier<RecipeType<R>> registerType(String id) {
        return TYPES.register(id + "_type", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return "confluence:" + id;
            }
        });
    }

    public static void register(IEventBus bus) {
        ShapedRecipePattern.setCraftingSize(4, 4);
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
