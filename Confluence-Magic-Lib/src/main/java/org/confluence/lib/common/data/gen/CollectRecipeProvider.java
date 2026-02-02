package org.confluence.lib.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CollectRecipeProvider extends RecipeProvider {
    private final List<AbstractRecipeProvider> subProviders;
    private final String name;

    public CollectRecipeProvider(String name, PackOutput output, CompletableFuture<HolderLookup.Provider> registries, Factory... factories) {
        super(output, registries);
        this.name = name;
        this.subProviders = Arrays.stream(factories).map(factory -> factory.create(output, registries)).toList();
    }

    @Override
    protected final void buildRecipes(RecipeOutput p_recipeOutput, HolderLookup.Provider holderLookup) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void buildRecipes(RecipeOutput recipeOutput) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider registries) {
        return CompletableFuture.allOf(subProviders.stream()
                .map(subProvider -> subProvider.run(output, registries))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return name;
    }

    @FunctionalInterface
    public interface Factory {
        AbstractRecipeProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries);
    }
}
