package org.confluence.terraentity.data.gen.biome;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.biome.ExtendedAddSpawnsBiomeModifier;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.Arrays;
import java.util.List;


public class TEBiomeModifier {

    public static void createBiomeModifier(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatureLookup = context.lookup(Registries.PLACED_FEATURE);
        HolderSet<Biome> desertAndBadlands = new OrHolderSet<>(biomeLookup.getOrThrow(Tags.Biomes.IS_DESERT), biomeLookup.getOrThrow(Tags.Biomes.IS_BADLANDS));
        HolderSet<Biome> snowyAndIcy = new OrHolderSet<>(biomeLookup.getOrThrow(Tags.Biomes.IS_SNOWY), biomeLookup.getOrThrow(Tags.Biomes.IS_ICY));
        HolderSet<Biome> jungleAndLush = new OrHolderSet<>(biomeLookup.getOrThrow(Tags.Biomes.IS_JUNGLE), biomeLookup.getOrThrow(Tags.Biomes.IS_LUSH));
        register(context, TENpcEntities.GUIDE,
                biomeLookup.getOrThrow(BiomeTags.IS_FOREST),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.DEMOLITIONIST,
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.DRIPSTONE_CAVES), biomeLookup.getOrThrow(Biomes.LUSH_CAVES), biomeLookup.getOrThrow(Biomes.NETHER_WASTES)),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.GOBLIN_TINKERER,
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.DRIPSTONE_CAVES), biomeLookup.getOrThrow(Biomes.LUSH_CAVES), biomeLookup.getOrThrow(Biomes.NETHER_WASTES)),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.ARMS_DEALER,
                biomeLookup.getOrThrow(BiomeTags.HAS_VILLAGE_DESERT),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.NURSE,
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.CHERRY_GROVE)),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.MERCHANT,
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.FOREST)),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.PAINTER,
                biomeLookup.getOrThrow(BiomeTags.IS_BADLANDS),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.ANGLER,
                biomeLookup.getOrThrow(BiomeTags.IS_RIVER),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.DRYAD,
                biomeLookup.getOrThrow(BiomeTags.IS_JUNGLE),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.DYE_TRADER,
                biomeLookup.getOrThrow(BiomeTags.HAS_VILLAGE_DESERT),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.WITCH_DOCTOR,
                biomeLookup.getOrThrow(BiomeTags.IS_JUNGLE),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.TRUFFLE,
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.MUSHROOM_FIELDS)),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.CLOTHIER,
                biomeLookup.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS),
                HolderSet.direct(), 2, 1, 1);
        register(context, TENpcEntities.TRAVELING_MERCHANT,
                biomeLookup.getOrThrow(TETags.Biomes.IS_EVER_WHERE),
                HolderSet.direct(), 1, 1, 1);
        register(context, TENpcEntities.PARTY_GIRL,
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.BEACH)),
                HolderSet.direct(), 1, 1, 1);
        register(context, createModifierKey("addition_bloody"), new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.CRIMSON_FOREST)),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_ZOMBIE.get(), 30, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.DRIPPLER.get(), 15, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.WANDERING_EYE_FISH.get(), 10, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 15, 1, 1)
                )
        ));
        register(context, createModifierKey("addition_corruption"), new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(
                        biomeLookup.getOrThrow(Biomes.BASALT_DELTAS),
                        biomeLookup.getOrThrow(Biomes.SOUL_SAND_VALLEY)
                ),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2)
                )
        ));
        register(context, createModifierKey("addition_crimson"), new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.NETHER_WASTES)),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMERA.get(), 60, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1)
                )
        ));
        register(context, createModifierKey("common_beach"), new BiomeModifiers.AddSpawnsBiomeModifier(
                biomeLookup.getOrThrow(Tags.Biomes.IS_BEACH),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.TROPIC_SLIME.get(), 5, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_SCOUT.get(), 1, 1, 1)
                )
        ));
        register(context, createModifierKey("common_desert"), new BiomeModifiers.AddSpawnsBiomeModifier(
                desertAndBadlands,
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.DESERT_SLIME.get(), 30, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.TOMB_CRAWLER.get(), 250, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANTLION_SWARMER.get(), 500, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), 200, 1, 1)
                )
        ));
        register(context, createModifierKey("common_hell"), new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(
                        biomeLookup.getOrThrow(Biomes.NETHER_WASTES),
                        biomeLookup.getOrThrow(Biomes.CRIMSON_FOREST),
                        biomeLookup.getOrThrow(Biomes.BASALT_DELTAS)
                ),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.HELL_BAT.get(), 60, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.FIRE_IMP.get(), 60, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON.get(), 45, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.VOODOO_DEMON.get(), 15, 1, 1)
                )
        ));
        register(context, createModifierKey("common_highlevel"), new BiomeModifiers.AddSpawnsBiomeModifier(
                biomeLookup.getOrThrow(Tags.Biomes.IS_OVERWORLD),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.HARPY.get(), 60, 1, 2)
                )
        ));
        register(context, createModifierKey("common_icy"), new BiomeModifiers.AddSpawnsBiomeModifier(
                snowyAndIcy,
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.ICE_BAT.get(), 80, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.UNDEAD_VIKING.get(), 80, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.SNOW_FLINX.get(), 80, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.ICE_SLIME.get(), 30, 1, 2)
                )
        ));
        register(context, createModifierKey("common_jungle"), new BiomeModifiers.AddSpawnsBiomeModifier(
                jungleAndLush,
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.HORNET.get(), 150, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.JUNGLE_BAT.get(), 40, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.JUNGLE_SLIME.get(), 40, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.MAN_EATER.get(), 150, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.SNATCHER.get(), 40, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIRANHA.get(), 40, 2, 3)
                )
        ));
        register(context, createModifierKey("common_overworld"), new BiomeModifiers.AddSpawnsBiomeModifier(
                biomeLookup.getOrThrow(TETags.Biomes.IS_EVER_WHERE),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLACK_SLIME.get(), 120, 1, 3),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLUE_SLIME.get(), 60, 2, 4),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.CAVE_BAT.get(), 80, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_SHELLY.get(), 60, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_WORM.get(), 60, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), 30, 1, 3),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_SLIME.get(), 90, 3, 3),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.NYMPH.get(), 1, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.PINK_SLIME.get(), 3, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.PURPLE_SLIME.get(), 30, 1, 3),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.RED_SLIME.get(), 90, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.YELLOW_SLIME.get(), 90, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON_EYE.get(), 65, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.FLYING_FISH.get(), 60, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.BUNNY.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.SQUIRREL.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.DUCK.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.BIRD.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.BLUE_JAY.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.CARDINAL.get(), 10, 1, 2)
                )
        ));
        register(context, createModifierKey("common_plain"), new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomeLookup.get(Biomes.PLAINS).get()),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_ARCHER.get(), 5, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_PEON.get(), 5, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_WARRIOR.get(), 5, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_THIEF.get(), 5, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_SCOUT.get(), 5, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANGER_GOBLIN.get(), 3, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_SORCERER.get(), 5, 1, 1)
                )));
        register(context, createModifierKey("skeleton_arm"), new BiomeModifiers.AddSpawnsBiomeModifier(
                biomeLookup.getOrThrow(BiomeTags.IS_OVERWORLD), List.of(
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), 20, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), 20, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_BONES.get(), 40, 1, 2),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHORT_BONES.get(), 40, 1, 2),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANGER_BONES.get(), 40, 1, 2),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_ANGER_BONES.get(), 30, 1, 2),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.CURSED_SKULL.get(), 15, 1, 1),
                new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_CASTER.get(), 20, 1, 1)
        )));
        register(context, createModifierKey("common_swamp"), new BiomeModifiers.AddSpawnsBiomeModifier(
                biomeLookup.getOrThrow(Tags.Biomes.IS_SWAMP),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.SWAMP_SLIME.get(), 90, 1, 3)
                )
        ));
        register(context, createModifierKey("lava_slime"), new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomeLookup.getOrThrow(Biomes.NETHER_WASTES)),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEMonsterEntities.LAVA_SLIME.get(), 90, 1, 1)
                )
        ));
        register(context, createModifierKey("only_forest"), new BiomeModifiers.AddSpawnsBiomeModifier(
                biomeLookup.getOrThrow(Tags.Biomes.IS_FOREST),
                List.of(
                        new MobSpawnSettings.SpawnerData(TEAnimals.BUNNY.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.SQUIRREL.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.DUCK.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.BIRD.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.BLUE_JAY.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.CARDINAL.get(), 10, 1, 2),
                        new MobSpawnSettings.SpawnerData(TEAnimals.JEWEL_BUNNY.get(), 2, 1, 1),
                        new MobSpawnSettings.SpawnerData(TEAnimals.JEWEL_SQUIRREL.get(), 2, 1, 1)
                )
        ));
    }

    private static  ResourceKey<BiomeModifier>  createModifierKey(DeferredHolder<EntityType<?>,?> entityType) {return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, TerraEntity.fromSpaceAndPath(entityType.getId().getNamespace(),"mob_spawner/" + entityType.getId().getPath()));}
    private static  ResourceKey<BiomeModifier>  createModifierKey(String name) {return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, TerraEntity.space("mob_spawner/" + name));}

    private static Holder.Reference<BiomeModifier> register(BootstrapContext<BiomeModifier> context, ResourceKey<BiomeModifier> key, BiomeModifier value) {
        return context.register(key, value, Lifecycle.stable());
    }
    private static Holder.Reference<BiomeModifier> register(BootstrapContext<BiomeModifier> context, DeferredHolder<EntityType<?>,?> entityType, HolderSet<Biome> biomes, HolderSet<Biome> excludedBiomes, int weight, int minCount, int maxCount) {
        return register(context, createModifierKey(entityType), ExtendedAddSpawnsBiomeModifier.singleSpawn(biomes, excludedBiomes, new ExtendedAddSpawnsBiomeModifier.ExtendedSpawnData(entityType.get(), weight, minCount, maxCount, entityType.get().getCategory())));
    }

    private HolderSet.Direct<Biome> getHolderSet(HolderGetter<Biome> biomeLookup, ResourceKey<Biome>... biomeNames){
        return HolderSet.direct(Arrays.stream(biomeNames).map(biomeLookup::getOrThrow).toList());
    }

}
