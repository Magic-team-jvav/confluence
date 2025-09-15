package org.confluence.mod.common.data.gen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.client.handler.bestiary.FilterEntry;
import org.confluence.terraentity.entity.animal.*;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry.*;

public class ModClientBestiaryEntryProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider pathProvider;

    public ModClientBestiaryEntryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "");
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        FilterEntry[] surfaceDaytime = {FilterEntry.SURFACE, FilterEntry.DAYTIME};
        recipe(Codec.unboundedMap(Codec.STRING, ClientBestiaryEntry.CODEC), pathProvider().json(Confluence.asResource("bestiary"))).addRecipe(new Builder()
                .add(TENpcEntities.GUIDE, builder -> builder.order(100).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(TENpcEntities.MERCHANT, builder -> builder.order(200).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(TENpcEntities.NURSE, builder -> builder.order(300).rarity(1).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(TENpcEntities.DEMOLITIONIST, builder -> builder.order(400).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(TENpcEntities.ANGLER, builder -> builder.order(500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN).entityNbt(nbt -> nbt.putBoolean(AnglerNPC.WAKE_UP_KEY, true)))
                .add(TENpcEntities.DRYAD, builder -> builder.order(600).rarity(3).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(TENpcEntities.ARMS_DEALER, builder -> builder.order(700).rarity(1).background(DESERT).filters(FilterEntry.DESERT))
                .add(TENpcEntities.DYE_TRADER, builder -> builder.order(800).rarity(2).background(DESERT).filters(FilterEntry.DESERT))
                .add(TENpcEntities.PAINTER, builder -> builder.order(900).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 发型师
                .add(TENpcEntities.ZOOLOGIST, builder -> builder.order(1100).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))
                // 酒馆老板
                // 高尔夫球手
                .add(TENpcEntities.GOBLIN_TINKERER, builder -> builder.order(1400).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(TENpcEntities.WITCH_DOCTOR, builder -> builder.order(1500).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(TENpcEntities.MECHANIC, builder -> builder.order(1600).rarity(2).background(SNOW).filters(FilterEntry.SNOW))
                .add(TENpcEntities.CLOTHIER, builder -> builder.order(1700).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 巫师
                // 蒸汽朋克人
                // 海盗
                .add(TENpcEntities.TRUFFLE, builder -> builder.order(2100).rarity(5).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                // 税收官
                // 机器侠
                .add(TENpcEntities.PARTY_GIRL, builder -> builder.order(2400).rarity(4).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                // 公主
                // 圣诞老人
                // 宠物
                .add(TENpcEntities.TRAVELING_MERCHANT, builder -> builder.order(3800).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                // 骷髅商人
                .add(TENpcEntities.OLD_MAN, builder -> builder.order(4000).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(TEAnimals.BUNNY, builder -> builder.order(4200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.RABBIT.builtInRegistryHolder(), builder -> builder.order(4210).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEAnimals.BOOM_BUNNY, builder -> builder.order(4250).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.GOLDEN_ID, builder -> builder.order(4700).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEAnimals.BIRD, builder -> builder.order(4800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEAnimals.BLUE_JAY, builder -> builder.order(4900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEAnimals.CARDINAL, builder -> builder.order(5000).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.SQUIRREL, Squirrel.VARIANT_KEY, Squirrel.COMMON_ID, builder -> builder.order(5900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.SQUIRREL, Squirrel.VARIANT_KEY, Squirrel.RED_ID, builder -> builder.order(6000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.GOLDEN_ID, builder -> builder.order(6100).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.FROG.builtInRegistryHolder(), builder -> builder.order(6400).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .addIntVariant(TEAnimals.GRASSHOPPER, JumpableVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_GRASSHOPPER_ID, builder -> builder.order(6600).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .addIntVariant(TEAnimals.GRASSHOPPER, JumpableVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_GRASSHOPPER_ID, builder -> builder.order(6700).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))
                .addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_BUTTERFLY_ID, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_BUTTERFLY_ID, builder -> builder.order(6900).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_WORM_ID, builder -> builder.order(7000).rarity(1).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                .addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_WORM_ID, builder -> builder.order(7100).rarity(5).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                .addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_DRAGONFLY_ID, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_DRAGONFLY_ID, builder -> builder.order(7300).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.LADYBUG, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_LADYBUG_ID, builder -> builder.order(7800).rarity(3).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .addIntVariant(TEAnimals.LADYBUG, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_LADYBUG_ID, builder -> builder.order(7900).rarity(5).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .addIntVariant(TEAnimals.FEALING, Fairy.VARIANT_KEY, VariantsTextureMaps.COMMON_FEALING_ID, builder -> builder.order(8100).rarity(5).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.DUCK, Duck.VARIANT_KEY, Duck.MALLARD_ID, builder -> builder.order(8200).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.DUCK, Duck.VARIANT_KEY, Duck.COMMON_ID, builder -> builder.order(8300).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.ENCHANTED_NIGHTCRAWLER_ID, builder -> builder.order(8700).rarity(5).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.PINK_FAIRY_ID, builder -> builder.order(8800).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.GREEN_FAIRY_ID, builder -> builder.order(8900).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.BLUE_FAIRY_ID, builder -> builder.order(9000).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .add(TEAnimals.MAGGOT, builder -> builder.order(9200).rarity(1).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.AMETHYST_ID, builder -> builder.order(9300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.TOPAZ_ID, builder -> builder.order(9400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.SAPPHIRE_ID, builder -> builder.order(9500).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.EMERALD_ID, builder -> builder.order(9600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.RUBY_ID, builder -> builder.order(9700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.DIAMOND_ID, builder -> builder.order(9800).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.AMBER_ID, builder -> builder.order(9900).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.AMETHYST_ID, builder -> builder.order(10000).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.TOPAZ_ID, builder -> builder.order(10100).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.SAPPHIRE_ID, builder -> builder.order(10200).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.EMERALD_ID, builder -> builder.order(10300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.RUBY_ID, builder -> builder.order(10400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.DIAMOND_ID, builder -> builder.order(10500).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.AMBER_ID, builder -> builder.order(10600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(TEAnimals.SNAIL, builder -> builder.order(10600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.SCORPION, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.SCORPION_ID, builder -> builder.order(11100).rarity(1).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                .addIntVariant(TEAnimals.SCORPION, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.BLACK_SCORPION_ID, builder -> builder.order(11200).rarity(2).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                .add(EntityType.TURTLE.builtInRegistryHolder(), builder -> builder.order(11600).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(EntityType.DOLPHIN.builtInRegistryHolder(), builder -> builder.order(11700).rarity(3).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(TEAnimals.SLUGGY, builder -> builder.order(12000).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(TEAnimals.HELL_BUTTERFLY, builder -> builder.order(12200).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .map);
    }

    @Override
    protected PackOutput.PathProvider pathProvider() {
        return pathProvider;
    }

    public static class Builder {
        private final Map<String, ClientBestiaryEntry> map = Maps.newHashMap();

        public Builder add(IHolderExtension<EntityType<?>> holder, String typeKey, String variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            String key = variant.isEmpty() ? typeKey : typeKey + '.' + variant;
            ClientBestiaryEntry.Builder builder = ClientBestiaryEntry.builder(holder.getDelegate().value(), key);
            consumer.accept(builder);
            builder.description(Component.translatable("bestiary." + key + ".desc"));
            map.put(key, builder.build());
            return this;
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, String variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(holder, holder.getDelegate().value().getDescriptionId(), variant, consumer);
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(holder, "", consumer);
        }

        public Builder addIntVariant(IHolderExtension<EntityType<?>> holder, String variantKey, int variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(holder, Integer.toString(variant), consumer.andThen(builder -> builder.entityNbt(nbt -> nbt.putInt(variantKey, variant))));
        }
    }
}
