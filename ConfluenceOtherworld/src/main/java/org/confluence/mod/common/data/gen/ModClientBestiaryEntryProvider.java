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
                .add(TENpcEntities.GUIDE, builder -> builder.order(0).rarity(1).background(ClientBestiaryEntry.SURFACE).filters(FilterEntry.SURFACE))
                .add(TENpcEntities.MERCHANT, builder -> builder.order(100).rarity(1).background(ClientBestiaryEntry.SURFACE).filters(FilterEntry.SURFACE))
                .add(TENpcEntities.NURSE, builder -> builder.order(200).rarity(1).background(ClientBestiaryEntry.THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(TENpcEntities.DEMOLITIONIST, builder -> builder.order(300).rarity(1).background(ClientBestiaryEntry.UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(TENpcEntities.ANGLER, builder -> builder.order(400).rarity(2).background(ClientBestiaryEntry.OCEAN).filters(FilterEntry.OCEAN))
                .add(TENpcEntities.DRYAD, builder -> builder.order(500).rarity(3).background(ClientBestiaryEntry.THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(TENpcEntities.ARMS_DEALER, builder -> builder.order(600).rarity(1).background(ClientBestiaryEntry.DESERT).filters(FilterEntry.DESERT))
                .add(TENpcEntities.DYE_TRADER, builder -> builder.order(700).rarity(2).background(ClientBestiaryEntry.DESERT).filters(FilterEntry.DESERT))
                .add(TENpcEntities.PAINTER, builder -> builder.order(800).rarity(2).background(ClientBestiaryEntry.THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 发型师 900 2 海洋
                .add(TENpcEntities.ZOOLOGIST, builder -> builder.order(1000).rarity(5).background(ClientBestiaryEntry.SURFACE).filters(FilterEntry.SURFACE))
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
