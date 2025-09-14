package org.confluence.mod.common.data.gen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.client.handler.bestiary.FilterEntry;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModClientBestiaryEntryProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider pathProvider;

    public ModClientBestiaryEntryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "");
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        recipe(Codec.unboundedMap(ResourceLocation.CODEC, ClientBestiaryEntry.CODEC), pathProvider().json(Confluence.asResource("bestiary"))).addRecipe(new Builder()
                .add(TENpcEntities.GUIDE, builder -> builder.order(0).rarity(1).background(ClientBestiaryEntry.SURFACE_BACKGROUND).filters(FilterEntry.SURFACE))
                .add(TENpcEntities.MERCHANT, builder -> builder.order(100).rarity(1).background(ClientBestiaryEntry.SURFACE_BACKGROUND).filters(FilterEntry.SURFACE))
                .add(TENpcEntities.NURSE, builder -> builder.order(200).rarity(1).background(ClientBestiaryEntry.THE_HALLOW_BACKGROUND).filters(FilterEntry.THE_HALLOW))
                .map);
    }

    @Override
    protected PackOutput.PathProvider pathProvider() {
        return pathProvider;
    }

    public static class Builder {
        private final Map<ResourceLocation, ClientBestiaryEntry> map = Maps.newHashMap();

        public Builder add(IHolderExtension<EntityType<?>> holder, Consumer<ClientBestiaryEntry.Builder> consumer) {
            ClientBestiaryEntry.Builder builder = ClientBestiaryEntry.builder(holder.getDelegate().value());
            consumer.accept(builder);
            ResourceLocation id = Objects.requireNonNull(holder.getKey()).location();
            builder.description(Component.translatable(Util.makeDescriptionId("bestiary", id) + ".desc"));
            map.put(id, builder.build());
            return this;
        }
    }
}
