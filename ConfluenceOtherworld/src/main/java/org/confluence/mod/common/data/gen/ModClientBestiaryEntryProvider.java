package org.confluence.mod.common.data.gen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.client.handler.bestiary.FilterEntry;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.animal.*;
import org.confluence.mod.common.entity.monster.DemonEye;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.npc.AnglerNPC;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.item.ArmorItems;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry.*;

public class ModClientBestiaryEntryProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider pathProvider;

    public ModClientBestiaryEntryProvider(PackOutput output) {
        super(output);
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "");
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        Builder entries = new Builder();
        registerNpcs(entries);
        registerCritters(entries);
        registerMonsters(entries);
        registerBosses(entries);
        registerVanillaCreatures(entries);
        recipe(Codec.unboundedMap(Codec.STRING, ClientBestiaryEntry.CODEC),
                pathProvider().json(Confluence.asResource("bestiary"))).addRecipe(entries.map);
    }

    /**
     * 按 1.21 图鉴顺序显式登记当前版本已经实现的 NPC。
     */
    private static void registerNpcs(Builder entries) {
        entries.add(NpcEntities.GUIDE, builder -> builder.order(100).rarity(1)
                .background(SURFACE).filters(FilterEntry.SURFACE));
        entries.add(NpcEntities.MERCHANT, builder -> builder.order(200).rarity(1)
                .background(SURFACE).filters(FilterEntry.SURFACE));
        entries.add(NpcEntities.NURSE, builder -> builder.order(300).rarity(1)
                .background(THE_HALLOW).filters(FilterEntry.THE_HALLOW));
        entries.add(NpcEntities.DEMOLITIONIST, builder -> builder.order(400).rarity(1)
                .background(UNDERGROUND).filters(FilterEntry.UNDERGROUND));
        entries.add(NpcEntities.ANGLER, builder -> builder.order(500).rarity(2)
                .background(OCEAN).filters(FilterEntry.OCEAN)
                .entityNbt(tag -> tag.putBoolean(AnglerNPC.WAKE_UP_KEY, true)));
        entries.add(NpcEntities.DRYAD, builder -> builder.order(600).rarity(3)
                .background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE));
        entries.add(NpcEntities.ARMS_DEALER, builder -> builder.order(700).rarity(1)
                .background(DESERT).filters(FilterEntry.DESERT));
        entries.add(NpcEntities.DYE_TRADER, builder -> builder.order(800).rarity(2)
                .background(DESERT).filters(FilterEntry.DESERT));
        entries.add(NpcEntities.PAINTER, builder -> builder.order(900).rarity(2)
                .background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE));
        entries.add(NpcEntities.ZOOLOGIST, builder -> builder.order(1100).rarity(5)
                .background(SURFACE).filters(FilterEntry.SURFACE));
        entries.add(NpcEntities.GOBLIN_TINKERER, builder -> builder.order(1400).rarity(3)
                .background(UNDERGROUND).filters(FilterEntry.UNDERGROUND));
        entries.add(NpcEntities.WITCH_DOCTOR, builder -> builder.order(1500).rarity(2)
                .background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE));
        entries.add(NpcEntities.MECHANIC, builder -> builder.order(1600).rarity(2)
                .background(SNOW).filters(FilterEntry.SNOW));
        entries.add(NpcEntities.CLOTHIER, builder -> builder.order(1700).rarity(2)
                .background(UNDERGROUND).filters(FilterEntry.UNDERGROUND));
        entries.add(NpcEntities.WIZARD, builder -> builder.order(1800).rarity(3)
                .background(THE_HALLOW).filters(FilterEntry.THE_HALLOW));
        entries.add(NpcEntities.TRUFFLE, builder -> builder.order(2100).rarity(5)
                .background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM));
        entries.add(NpcEntities.PARTY_GIRL, builder -> builder.order(2400).rarity(4)
                .background(THE_HALLOW).filters(FilterEntry.THE_HALLOW));
        entries.add(NpcEntities.TRAVELING_MERCHANT, builder -> builder.order(3800).rarity(3)
                .background(SURFACE).filters(FilterEntry.SURFACE));
        entries.add(NpcEntities.OLD_MAN, builder -> builder.order(4000).rarity(2)
                .background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON));
    }

    /**
     * 小动物的顺序、稀有度、背景和变体均逐项来自 1.21。
     * 1.20 的变体 NBT 使用枚举序列化，因此这里只保留 1.21 的图鉴数字键，不照搬其整数 NBT。
     */
    private static void registerCritters(Builder entries) {
        FilterEntry[] surfaceDaytime = {FilterEntry.SURFACE, FilterEntry.DAYTIME};
        entries.add(CritterEntities.BUNNY, builder -> builder.order(4200).rarity(1)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(EntityType.RABBIT, builder -> builder.order(4210).rarity(1)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(CritterEntities.EXPLOSIVE_BUNNY, builder -> builder.order(4250).rarity(1)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.numberedVariant(CritterEntities.JEWEL_BUNNY, 4, Bunny.Variant.GOLD,
                builder -> builder.order(4700).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(CritterEntities.BIRD, builder -> builder.order(4800).rarity(1)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(CritterEntities.BLUE_JAY, builder -> builder.order(4900).rarity(1)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(CritterEntities.CARDINAL, builder -> builder.order(5000).rarity(2)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(EntityType.PARROT, builder -> builder.order(5200).rarity(3)
                .background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME));
        entries.numberedVariant(CritterEntities.SQUIRREL, 0, Squirrel.Variant.NORMAL,
                builder -> builder.order(5900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime));
        entries.numberedVariant(CritterEntities.RED_SQUIRREL, "entity.confluence.squirrel", 1,
                Squirrel.Variant.RED, builder -> builder.order(6000).rarity(1)
                        .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 1, Squirrel.Variant.GOLD,
                builder -> builder.order(6100).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(EntityType.FROG, builder -> builder.order(6400).rarity(1)
                .background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE));

        entries.numberedVariant(CritterEntities.GRASSHOPPER, 0, Grasshopper.Variant.GOLD,
                builder -> builder.order(6700).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE));
        entries.numberedVariant(CritterEntities.GRASSHOPPER, 1, Grasshopper.Variant.GREEN,
                builder -> builder.order(6600).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE));
        entries.numberedVariant(CritterEntities.BUTTERFLY, 0, Butterfly.Variant.GOLD,
                builder -> builder.order(6900).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime));
        Butterfly.Variant[] butterflyVariants = {
                Butterfly.Variant.JULIA, Butterfly.Variant.MONARCH,
                Butterfly.Variant.PURPLE_EMPEROR, Butterfly.Variant.RED_ADMIRAL,
                Butterfly.Variant.SULPHUR, Butterfly.Variant.TREE_NYMPH,
                Butterfly.Variant.ULYSSES, Butterfly.Variant.ZEBRA_SWALLOWTAIL
        };
        for (int index = 0; index < butterflyVariants.length; index++) {
            int variantKey = index + 1;
            int entryOrder = 6800 + index;
            entries.numberedVariant(CritterEntities.BUTTERFLY, variantKey, butterflyVariants[index],
                    builder -> builder.order(entryOrder).rarity(1)
                            .background(SURFACE_SUN).filters(surfaceDaytime));
        }
        entries.numberedVariant(CritterEntities.WORM, 0, Worm.Variant.NIGHTCRAWLER,
                builder -> builder.order(8700).rarity(5)
                        .background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME));
        entries.numberedVariant(CritterEntities.WORM, 1, Worm.Variant.GOLD,
                builder -> builder.order(7100).rarity(5)
                        .background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN));
        entries.numberedVariant(CritterEntities.WORM, 2, Worm.Variant.NORMAL,
                builder -> builder.order(7000).rarity(1)
                        .background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN));

        addDragonflies(entries, surfaceDaytime);
        entries.numberedVariant(CritterEntities.LADYBUG, 0, Ladybug.Variant.GOLD,
                builder -> builder.order(7900).rarity(5).background(SURFACE)
                        .filters(FilterEntry.WINDY_DAY));
        entries.numberedVariant(CritterEntities.LADYBUG, 1, Ladybug.Variant.RED,
                builder -> builder.order(7800).rarity(3).background(SURFACE)
                        .filters(FilterEntry.WINDY_DAY));
        entries.numberedVariant(CritterEntities.FEALING, 0, Fairy.Variant.PINK,
                builder -> builder.order(8100).rarity(5).background(CAVE).filters(FilterEntry.CAVE));
        entries.numberedVariant(CritterEntities.DUCK, 0, Duck.Variant.MALLARD,
                builder -> builder.order(8200).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime));
        entries.numberedVariant(CritterEntities.DUCK, 1, Duck.Variant.COMMON,
                builder -> builder.order(8300).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime));
        entries.numberedVariant(CritterEntities.FAIRY, 0, Fairy.Variant.PINK,
                builder -> builder.order(8800).rarity(4)
                        .background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME));
        entries.numberedVariant(CritterEntities.FAIRY, 1, Fairy.Variant.GREEN,
                builder -> builder.order(8900).rarity(4)
                        .background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME));
        entries.numberedVariant(CritterEntities.FAIRY, 2, Fairy.Variant.BLUE,
                builder -> builder.order(9000).rarity(4)
                        .background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME));
        entries.add(CritterEntities.MAGGOT, builder -> builder.order(9200).rarity(1)
                .background(GRAVEYARD).filters(FilterEntry.GRAVEYARD));

        addJewelCritters(entries);
        entries.add(CritterEntities.SNAIL, builder -> builder.order(10700).rarity(1)
                .background(CAVE).filters(FilterEntry.CAVE));
        entries.numberedVariant(CritterEntities.SCORPION, 0, Scorpion.Variant.BLACK,
                builder -> builder.order(11200).rarity(2)
                        .background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME));
        entries.numberedVariant(CritterEntities.SCORPION, 1, Scorpion.Variant.NORMAL,
                builder -> builder.order(11100).rarity(1)
                        .background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME));
        entries.add(EntityType.TURTLE, builder -> builder.order(11600).rarity(1)
                .background(OCEAN).filters(FilterEntry.OCEAN));
        entries.add(EntityType.DOLPHIN, builder -> builder.order(11700).rarity(3)
                .background(OCEAN).filters(FilterEntry.OCEAN));
        entries.add(CritterEntities.GRUBBY, builder -> builder.order(11900).rarity(1)
                .background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE));
        entries.add(CritterEntities.SLUGGY, builder -> builder.order(12000).rarity(2)
                .background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE));
        entries.add(CritterEntities.HELL_BUTTERFLY, builder -> builder.order(12300).rarity(2)
                .background(THE_NETHER).filters(FilterEntry.THE_NETHER));
        entries.add(CritterEntities.MAGMA_SNAIL, builder -> builder.order(12400).rarity(2)
                .background(THE_NETHER).filters(FilterEntry.THE_NETHER));
        entries.add(CritterEntities.PRISMATIC_LACEWING, builder -> builder.order(12600).rarity(3)
                .background(THE_HALLOW_MOON).filters(FilterEntry.THE_HALLOW, FilterEntry.NIGHTTIME));
        entries.add(CritterEntities.GLOWING_SNAIL, builder -> builder.order(12700).rarity(3)
                .background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM));
        entries.add(CritterEntities.CRAB, builder -> builder.order(24900).rarity(1)
                .background(OCEAN).filters(FilterEntry.OCEAN));
    }

    private static void addDragonflies(Builder entries, FilterEntry[] surfaceDaytime) {
        Dragonfly.Variant[] variants = {
                Dragonfly.Variant.BLACK, Dragonfly.Variant.BLUE, Dragonfly.Variant.GOLD,
                Dragonfly.Variant.GREEN, Dragonfly.Variant.ORANGE,
                Dragonfly.Variant.RED, Dragonfly.Variant.YELLOW
        };
        int[] orders = {7200, 7201, 7300, 7202, 7203, 7204, 7205};
        for (int index = 0; index < variants.length; index++) {
            int variantKey = index;
            int entryOrder = orders[index];
            int rarity = variants[index] == Dragonfly.Variant.GOLD ? 5 : 3;
            entries.numberedVariant(CritterEntities.DRAGONFLY, variantKey, variants[index],
                    builder -> builder.order(entryOrder).rarity(rarity)
                            .background(SURFACE_SUN).filters(surfaceDaytime));
        }
    }

    private static void addJewelCritters(Builder entries) {
        entries.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 0, Squirrel.Variant.AMBER,
                builder -> cave(builder, 9900, 1));
        entries.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 2, Squirrel.Variant.AMETHYST,
                builder -> cave(builder, 9300, 1));
        entries.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 3, Squirrel.Variant.DIAMOND,
                builder -> cave(builder, 9800, 1));
        entries.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 4, Squirrel.Variant.EMERALD,
                builder -> cave(builder, 9600, 1));
        entries.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 5, Squirrel.Variant.RUBY,
                builder -> cave(builder, 9700, 1));
        entries.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 6, Squirrel.Variant.SAPPHIRE,
                builder -> cave(builder, 9500, 1));
        entries.numberedVariant(CritterEntities.JEWEL_SQUIRREL, 7, Squirrel.Variant.TOPAZ,
                builder -> cave(builder, 9400, 1));

        entries.numberedVariant(CritterEntities.JEWEL_BUNNY, 0, Bunny.Variant.AMBER,
                builder -> cave(builder, 10600, 1));
        entries.numberedVariant(CritterEntities.JEWEL_BUNNY, 1, Bunny.Variant.AMETHYST,
                builder -> cave(builder, 10000, 1));
        entries.numberedVariant(CritterEntities.JEWEL_BUNNY, 2, Bunny.Variant.DIAMOND,
                builder -> cave(builder, 10500, 1));
        entries.numberedVariant(CritterEntities.JEWEL_BUNNY, 3, Bunny.Variant.EMERALD,
                builder -> cave(builder, 10300, 1));
        entries.numberedVariant(CritterEntities.JEWEL_BUNNY, 5, Bunny.Variant.RUBY,
                builder -> cave(builder, 10400, 1));
        entries.numberedVariant(CritterEntities.JEWEL_BUNNY, 6, Bunny.Variant.SAPPHIRE,
                builder -> cave(builder, 10200, 1));
        entries.numberedVariant(CritterEntities.JEWEL_BUNNY, 7, Bunny.Variant.TOPAZ,
                builder -> cave(builder, 10100, 1));
    }

    /**
     * 按 1.21 已登记条目逐项配置当前版本存在的普通敌怪。
     */
    private static void registerMonsters(Builder entries) {
        FilterEntry[] surfaceDaytime = {FilterEntry.SURFACE, FilterEntry.DAYTIME};
        FilterEntry[] surfaceNighttime = {FilterEntry.SURFACE, FilterEntry.NIGHTTIME};
        entries.add(MonsterEntities.GOBLIN_SCOUT, builder -> builder.order(12900).rarity(3)
                .background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE));
        entries.add(MonsterEntities.GREEN_SLIME, builder -> builder.order(13000).rarity(1)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(MonsterEntities.BLUE_SLIME, builder -> builder.order(13100).rarity(1)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(MonsterEntities.PURPLE_SLIME, builder -> builder.order(13200).rarity(1)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(MonsterEntities.PINK_SLIME, builder -> builder.order(13300).rarity(4)
                .background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, surfaceDaytime[0], surfaceDaytime[1]));
        entries.add(MonsterEntities.GOLDEN_SLIME, builder -> builder.order(13301).rarity(5)
                .background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, surfaceDaytime[0], surfaceDaytime[1]));
        entries.add(MonsterEntities.HONEY_SLIME, builder -> builder.order(13302).rarity(3)
                .background(THE_JUNGLE_SUN).filters(FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(MonsterEntities.SWAMP_SLIME, builder -> builder.order(13303).rarity(2)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(MonsterEntities.GREEN_DUMPLING_SLIME, builder -> builder.order(13304).rarity(4)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(MonsterEntities.SPIKED_SLIME, builder -> builder.order(13305).rarity(4)
                .background(SURFACE_SUN).filters(surfaceDaytime));
        entries.add(MonsterEntities.TROPIC_SLIME, builder -> builder.order(13306).rarity(2)
                .background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME));
        entries.add(MonsterEntities.FLYING_FISH, builder -> builder.order(13700).rarity(2)
                .background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN));

        addDemonEyeVariants(entries);
        entries.add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "slime",
                builder -> builder.order(14700).rarity(1).background(SURFACE_MOON)
                        .filters(surfaceNighttime)
                        .entityNbt(tag -> tag.putString("Variant", Zombie.Variant.SLIMED.getSerializedName())));
        entries.add(EntityType.ZOMBIE, builder -> builder.order(14900).rarity(1)
                .background(SURFACE_MOON).filters(surfaceNighttime));
        entries.add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "raincoat",
                builder -> builder.order(15400).rarity(2).background(SURFACE_NIGHTTIME_RAIN)
                        .filters(FilterEntry.NIGHTTIME, FilterEntry.RAIN)
                        .entityNbt(tag -> tag.putString("Variant", Zombie.Variant.RAINCOAT.getSerializedName())));
        entries.add(MonsterEntities.POSSESS_ARMOR, builder -> builder.order(15500).rarity(2)
                .background(SURFACE_MOON).filters(surfaceNighttime));
        entries.add(MonsterEntities.WRAITH, builder -> builder.order(15700).rarity(2)
                .background(SURFACE_MOON).filters(surfaceNighttime));
        entries.add(MonsterEntities.BLOOD_ZOMBIE, builder -> builder.order(16200).rarity(1)
                .background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME));
        entries.add(MonsterEntities.DRIPPLER, builder -> builder.order(17100).rarity(2)
                .background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME));
        entries.add(MonsterEntities.WANDERING_EYE_FISH, builder -> builder.order(17300).rarity(4)
                .background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME));
        entries.add(MonsterEntities.GHOST, builder -> builder.order(17900).rarity(3)
                .background(GRAVEYARD).filters(FilterEntry.GRAVEYARD));
        entries.add(MonsterEntities.RED_SLIME, builder -> builder.order(18000).rarity(1)
                .background(UNDERGROUND).filters(FilterEntry.UNDERGROUND));
        entries.add(MonsterEntities.YELLOW_SLIME, builder -> builder.order(18100).rarity(3)
                .background(UNDERGROUND).filters(FilterEntry.UNDERGROUND));
        entries.add(MonsterEntities.GIANT_WORM, builder -> builder.order(18300).rarity(1)
                .background(UNDERGROUND).filters(FilterEntry.UNDERGROUND));
        entries.add(MonsterEntities.BLACK_SLIME, "entity.confluence.baby_slime", "",
                builder -> {
                    cave(builder, 18500, 1);
                    builder.entityNbt(tag -> tag.putInt("SlimeSize", 1));
                });
        entries.add(MonsterEntities.BLACK_SLIME, builder -> cave(builder, 18600, 1));
        entries.add(MonsterEntities.BLACK_SLIME, "entity.confluence.mother_slime", "",
                builder -> {
                    cave(builder, 18800, 2);
                    builder.entityNbt(tag -> tag.putInt("SlimeSize", 4));
                });
        entries.add(EntityType.SKELETON, builder -> cave(builder, 19100, 1));
        entries.add(MonsterEntities.CRAWDAD, builder -> cave(builder, 19500, 2));
        entries.mobArmorItems(EntityType.SKELETON, "entity.confluence.undead_miner", "",
                List.of(ArmorItems.MINING_BOOTS.toStack(), ArmorItems.MINING_LEGGINGS.toStack(),
                        ArmorItems.MINING_CHESTPLATE.toStack(), ArmorItems.MINING_HELMET.toStack()),
                null, builder -> builder.order(19600).rarity(3)
                        .background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE));
        entries.add(MonsterEntities.NYMPH, builder -> builder.order(19800).rarity(5)
                .background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE));
        entries.add(MonsterEntities.CAVE_BAT, builder -> cave(builder, 20300, 1));
        entries.add(MonsterEntities.BLUE_JELLYFISH, builder -> cave(builder, 20500, 1));
        entries.add(MonsterEntities.GREEN_JELLYFISH, builder -> cave(builder, 20600, 2));
        entries.add(MonsterEntities.WOODEN_MIMIC, builder -> rareCave(builder, 20700));
        entries.add(MonsterEntities.GOLDEN_MIMIC, builder -> rareCave(builder, 20701));
        entries.add(MonsterEntities.SHADOW_MIMIC, builder -> builder.order(20701).rarity(5)
                .background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER));
        entries.add(MonsterEntities.GIANT_SHELLY, builder -> cave(builder, 20800, 2));
        entries.add(MonsterEntities.GRANITE_ELEMENTAL, builder -> builder.order(21100).rarity(2)
                .background(GRANITE).filters(FilterEntry.GRANITE));
        entries.add(MonsterEntities.SPORE_SKELETON, builder -> builder.order(21400).rarity(1)
                .background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM));
        entries.add(MonsterEntities.SPORE_BAT, builder -> builder.order(21500).rarity(1)
                .background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM));
        entries.add(MonsterEntities.ICE_SLIME, builder -> builder.order(21800).rarity(1)
                .background(SNOW).filters(FilterEntry.SNOW, FilterEntry.DAYTIME));
        entries.add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "frozen",
                builder -> builder.order(21900).rarity(2).background(SNOW_MOON)
                        .filters(FilterEntry.SNOW, FilterEntry.NIGHTTIME)
                        .entityNbt(tag -> tag.putString("Variant", Zombie.Variant.ESKIMO.getSerializedName())));
        entries.add(MonsterEntities.SPIKED_ICE_SLIME, builder -> builder.order(22200).rarity(2)
                .background(UNDERGROUND_SNOW).filters(FilterEntry.ICE));
        entries.add(MonsterEntities.UNDEAD_VIKING, builder -> builder.order(22400).rarity(2)
                .background(UNDERGROUND_SNOW).filters(FilterEntry.ICE));
        entries.add(MonsterEntities.SNOW_FLINX, builder -> builder.order(22500).rarity(3)
                .background(UNDERGROUND_SNOW).filters(FilterEntry.ICE));
        entries.add(MonsterEntities.ICE_BAT, builder -> builder.order(22800).rarity(1)
                .background(UNDERGROUND_SNOW).filters(FilterEntry.ICE));
        entries.add(MonsterEntities.ICE_MIMIC, builder -> builder.order(23000).rarity(5)
                .background(UNDERGROUND_SNOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.ICE));
        entries.add(MonsterEntities.DESERT_SLIME, builder -> undergroundDesert(builder, 23300, 2));
        entries.add(MonsterEntities.MUMMY, builder -> undergroundDesert(builder, 23600, 2));
        entries.add(MonsterEntities.GHOUL, builder -> undergroundDesert(builder, 23700, 2));
        entries.add(MonsterEntities.TOMB_CRAWLER, builder -> undergroundDesert(builder, 23900, 1));
        entries.add(MonsterEntities.SAND_POACHER, builder -> undergroundDesert(builder, 24100, 2));
        entries.add(MonsterEntities.GIANT_ANTLION_SWARMER, builder -> undergroundDesert(builder, 24200, 2));
        entries.add(MonsterEntities.ANTLION_SWARMER, builder -> undergroundDesert(builder, 24600, 2));
        entries.add(MonsterEntities.SHARK, builder -> builder.order(25100).rarity(2)
                .background(OCEAN).filters(FilterEntry.OCEAN));
        entries.add(MonsterEntities.PINK_JELLYFISH, builder -> builder.order(25300).rarity(2)
                .background(OCEAN).filters(FilterEntry.OCEAN));
        entries.add(MonsterEntities.JUNGLE_SLIME, builder -> builder.order(25400).rarity(1)
                .background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME));
        entries.add(MonsterEntities.SNATCHER, builder -> jungle(builder, 25500, 1));
        entries.add(MonsterEntities.DERPLING, builder -> jungle(builder, 25700, 2));
        entries.add(MonsterEntities.SPIKED_JUNGLE_SLIME, builder -> builder.order(25800).rarity(2)
                .background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(MonsterEntities.HORNET, builder -> builder.order(26500).rarity(1)
                .background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(MonsterEntities.MAN_EATER, builder -> builder.order(27100).rarity(2)
                .background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(MonsterEntities.JUNGLE_BAT, builder -> builder.order(27300).rarity(1)
                .background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(MonsterEntities.PIRANHA, builder -> builder.order(27400).rarity(1)
                .background(UNDERGROUND).filters(FilterEntry.UNDERGROUND, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(MonsterEntities.ARAPAIMA, builder -> builder.order(27600).rarity(2)
                .background(UNDERGROUND).filters(FilterEntry.UNDERGROUND, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(MonsterEntities.JUNGLE_MIMIC, builder -> builder.order(27601).rarity(5)
                .background(UNDERGROUND).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE));
        registerDungeonAndNetherMonsters(entries);
        registerEvilAndEventMonsters(entries);
    }

    private static void addDemonEyeVariants(Builder entries) {
        FilterEntry[] filters = {FilterEntry.SURFACE, FilterEntry.NIGHTTIME};
        DemonEye.Variant[] variants = {
                DemonEye.Variant.DILATED, DemonEye.Variant.DILATED_SMALL,
                DemonEye.Variant.SLEEPY, DemonEye.Variant.SLEEPY_BIG,
                DemonEye.Variant.PURPLE, DemonEye.Variant.PURPLE_BIG,
                DemonEye.Variant.NORMAL, DemonEye.Variant.NORMAL_BIG,
                DemonEye.Variant.GREEN, DemonEye.Variant.GREEN_SMALL,
                DemonEye.Variant.CATARACT, DemonEye.Variant.CATARACT_BIG,
                DemonEye.Variant.SPACESHIP, DemonEye.Variant.OWL
        };
        int[] orders = {13900, 13910, 14000, 14010, 14100, 14110, 14200, 14210,
                14300, 14310, 14400, 14410, 14411, 14412};
        for (int index = 0; index < variants.length; index++) {
            int order = orders[index];
            entries.variant(MonsterEntities.DEMON_EYE, variants[index], builder -> builder.order(order).rarity(1)
                    .background(SURFACE_MOON).filters(filters));
        }
    }

    private static void registerDungeonAndNetherMonsters(Builder entries) {
        entries.add(MonsterEntities.METEOR_HEAD, builder -> builder.order(27900).rarity(2)
                .background(METEOR).filters(FilterEntry.METEOR));
        entries.add(MonsterEntities.DUNGEON_SLIME, builder -> builder.order(28000).rarity(4)
                .background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON));
        entries.add(MonsterEntities.ANGER_BONES, builder -> dungeon(builder, 28100, 2));
        entries.add(MonsterEntities.SHORT_BONES, builder -> dungeon(builder, 28101, 2));
        entries.add(MonsterEntities.BIG_BONES, builder -> dungeon(builder, 28102, 2));
        entries.add(MonsterEntities.BIG_ANGER_BONES, builder -> dungeon(builder, 28200, 2));
        entries.add(MonsterEntities.BIG_MUSCLE_ANGER_BONES, builder -> dungeon(builder, 28300, 2));
        entries.add(MonsterEntities.BIG_HELMET_ANGER_BONES, builder -> dungeon(builder, 28400, 2));
        entries.add(MonsterEntities.DARK_CASTER, builder -> dungeon(builder, 30200, 1));
        entries.add(MonsterEntities.CURSED_SKULL, builder -> dungeon(builder, 30900, 2));
        entries.add(MonsterEntities.LAVA_SLIME, builder -> nether(builder, 31300, 1));
        entries.add(MonsterEntities.BONE_SERPENT, builder -> nether(builder, 31500, 2));
        entries.add(MonsterEntities.FIRE_IMP, builder -> nether(builder, 31600, 2));
        entries.add(MonsterEntities.HELL_BAT, builder -> nether(builder, 31700, 2));
        entries.add(MonsterEntities.DEMON, builder -> nether(builder, 31800, 2));
        entries.add(MonsterEntities.VOODOO_DEMON, builder -> builder.order(31900).rarity(3)
                .background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER));
        entries.add(MonsterEntities.WYVERN, builder -> builder.order(32200).rarity(3)
                .background(SKY).filters(FilterEntry.SKY));
        entries.add(MonsterEntities.HARPY, builder -> builder.order(32300).rarity(2)
                .background(SKY).filters(FilterEntry.SKY));
    }

    private static void registerEvilAndEventMonsters(Builder entries) {
        entries.add(MonsterEntities.CORRUPT_SLIME, builder -> corruption(builder, 32600, 2));
        entries.add(MonsterEntities.EATER_OF_SOULS, builder -> corruption(builder, 32700, 1));
        entries.add(MonsterEntities.DEVOURER, builder -> corruption(builder, 32900, 2));
        entries.add(MonsterEntities.CORRUPT_MIMIC, builder -> builder.order(33400).rarity(5)
                .background(UNDERGROUND_CORRUPTION).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_CORRUPTION));
        entries.add(MonsterEntities.DARK_MUMMY, builder -> builder.order(33700).rarity(2)
                .background(CORRUPT_DESERT).filters(FilterEntry.CORRUPT_DESERT));
        entries.add(MonsterEntities.VILE_GHOUL, builder -> builder.order(33800).rarity(2)
                .background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.CORRUPT_DESERT));
        entries.add(MonsterEntities.CRIMSLIME, builder -> crimson(builder, 33900, 2));
        entries.add(MonsterEntities.FACE_MONSTER, builder -> crimson(builder, 34000, 2));
        entries.add(MonsterEntities.CRIMERA, builder -> crimson(builder, 34100, 1));
        entries.add(MonsterEntities.BLOOD_CRAWLER, builder -> crimson(builder, 34700, 2));
        entries.add(MonsterEntities.HERPLING, builder -> crimson(builder, 34800, 2));
        entries.add(MonsterEntities.CRIMSON_MIMIC, builder -> builder.order(34900).rarity(5)
                .background(UNDERGROUND_CRIMSON).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_CRIMSON));
        entries.add(MonsterEntities.BLOOD_MUMMY, builder -> builder.order(35200).rarity(2)
                .background(CRIMSON_DESERT).filters(FilterEntry.CRIMSON_DESERT));
        entries.add(MonsterEntities.TAINTED_GHOUL, builder -> builder.order(35300).rarity(2)
                .background(CRIMSON_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.CRIMSON_DESERT));
        entries.add(MonsterEntities.DARK_LAMIA, builder -> builder.order(35400).rarity(2)
                .background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CORRUPT_CAVE_DESERT, FilterEntry.CRIMSON_CAVE_DESERT));
        entries.add(MonsterEntities.PIXIE, builder -> builder.order(35700).rarity(2)
                .background(THE_HALLOW).filters(FilterEntry.THE_HALLOW));
        entries.add(MonsterEntities.LUMINOUS_SLIME, builder -> builder.order(36000).rarity(2)
                .background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW));
        entries.add(MonsterEntities.HALLOWED_MIMIC, builder -> builder.order(36400).rarity(5)
                .background(UNDERGROUND_HALLOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_HALLOW));
        entries.add(MonsterEntities.LIGHT_MUMMY, builder -> builder.order(36700).rarity(2)
                .background(HALLOW_DESERT).filters(FilterEntry.RARE_CREATURE, FilterEntry.HALLOW_DESERT));
        entries.add(MonsterEntities.DREAMER_GHOUL, builder -> builder.order(36800).rarity(2)
                .background(HALLOW_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.HALLOW_DESERT));
        entries.add(MonsterEntities.LIGHT_LAMIA, builder -> builder.order(36900).rarity(2)
                .background(HALLOW_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.HALLOW_DESERT));
        entries.add(MonsterEntities.SPORE_ZOMBIE, builder -> builder.order(37000).rarity(2)
                .background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM));
        entries.add(MonsterEntities.HAT_SPORE_ZOMBIE, builder -> builder.order(37100).rarity(2)
                .background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM));
        entries.add(MonsterEntities.GOBLIN_PEON, builder -> goblinInvasion(builder, 37900, 1));
        entries.add(MonsterEntities.GOBLIN_THIEF, builder -> goblinInvasion(builder, 38000, 1));
        entries.add(MonsterEntities.GOBLIN_ARCHER, builder -> goblinInvasion(builder, 38100, 1));
        entries.add(MonsterEntities.GOBLIN_WARRIOR, builder -> goblinInvasion(builder, 38200, 2));
        entries.add(MonsterEntities.GOBLIN_SORCERER, builder -> goblinInvasion(builder, 38300, 2));
        entries.add(MonsterEntities.ANGER_GOBLIN, builder -> goblinInvasion(builder, 38301, 3));
        entries.add(MonsterEntities.DECAYEDER, builder -> builder.order(70000).rarity(2)
                .background(THE_CORRUPTION).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME, FilterEntry.THE_CORRUPTION));
        entries.add(MonsterEntities.BLOODY_SPORE, builder -> builder.order(70100).rarity(2)
                .background(THE_CRIMSON).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME, FilterEntry.THE_CRIMSON));
        entries.add(MonsterEntities.WITHER_BONE_SERPENT, builder -> nether(builder, 70300, 3));
    }

    /**
     * Boss 仅登记 1.21 当前已经正式写入图鉴的主体与随从。
     */
    private static void registerBosses(Builder entries) {
        entries.add(BossEntities.EYE_OF_CTHULHU, builder -> builder.order(50400).rarity(2)
                .background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME));
        entries.add(BossEntities.SERVANT_OF_CTHULHU, "entity.confluence.demon_eye", "minion",
                builder -> builder.order(50500).rarity(1)
                        .background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME));
        entries.add(BossEntities.KING_SLIME, builder -> builder.order(50600).rarity(2)
                .background(SURFACE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.SURFACE));
        entries.add(BossEntities.EATER_OF_WORLDS, builder -> builder.order(50700).rarity(3)
                .background(THE_CORRUPTION).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CORRUPTION));
        entries.add(BossEntities.BRAIN_OF_CTHULHU, builder -> builder.order(50800).rarity(3)
                .background(THE_CRIMSON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CRIMSON));
        entries.add(MonsterEntities.VISUAL_NEURON, builder -> builder.order(50900).rarity(2)
                .background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON));
        entries.add(BossEntities.DEERCLOPS, builder -> builder.order(51000).rarity(3)
                .background(SNOW).filters(FilterEntry.BOSS_ENEMY, FilterEntry.SNOW));
        entries.add(BossEntities.SKELETRON, builder -> builder.order(51100).rarity(3)
                .background(THE_DUNGEON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_DUNGEON));
        entries.add(BossEntities.QUEEN_BEE, builder -> builder.order(51200).rarity(3)
                .background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(BossEntities.WALL_OF_FLESH, builder -> builder.order(51300).rarity(4)
                .background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER));
        entries.add(MonsterEntities.LEECH, builder -> nether(builder, 51400, 1));
        entries.add(MonsterEntities.THE_HUNGRY, builder -> nether(builder, 51500, 2));
        entries.add(BossEntities.RETINAZER, builder -> builder.order(52100).rarity(4)
                .background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME));
        entries.add(BossEntities.SPAZMATISM, builder -> builder.order(52200).rarity(4)
                .background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME));
        entries.add(BossEntities.SKELETRON_PRIME, builder -> builder.order(52500).rarity(4)
                .background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME));
        entries.add(BossEntities.PLANTERA, builder -> builder.order(52600).rarity(4)
                .background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(BossEntities.HILL_OF_FLESH, builder -> builder.order(70200).rarity(4)
                .background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER));
        entries.add(BossEntities.DUNGEON_GUARDIAN, builder -> builder.order(31100).rarity(4)
                .background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON));
    }

    /**
     * 登记 1.20 中存在、且 1.21 图鉴已经显式配置的原版生物。
     */
    private static void registerVanillaCreatures(Builder entries) {
        FilterEntry[] night = {FilterEntry.SURFACE, FilterEntry.NIGHTTIME};
        entries.add(EntityType.ALLAY, builder -> vanillaDay(builder, 60000, 4));
        entries.add(EntityType.BAT, builder -> cave(builder, 60200, 1));
        entries.add(EntityType.CAMEL, builder -> builder.order(60300).rarity(2)
                .background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME));
        entries.add(EntityType.CHICKEN, builder -> vanillaDay(builder, 60400, 2));
        entries.add(EntityType.COD, builder -> oceanDay(builder, 60500, 1));
        entries.add(EntityType.COW, builder -> vanillaDay(builder, 60600, 1));
        entries.add(EntityType.DONKEY, builder -> vanillaDay(builder, 60700, 1));
        entries.add(EntityType.GLOW_SQUID, builder -> cave(builder, 60800, 3));
        entries.add(EntityType.HORSE, builder -> vanillaDay(builder, 60900, 1));
        entries.add(EntityType.MOOSHROOM, builder -> vanillaDay(builder, 61000, 4));
        entries.add(EntityType.MULE, builder -> vanillaDay(builder, 61100, 2));
        entries.add(EntityType.PIG, builder -> vanillaDay(builder, 61200, 1));
        entries.add(EntityType.SALMON, builder -> oceanDay(builder, 61300, 1));
        entries.add(EntityType.SHEEP, builder -> vanillaDay(builder, 61400, 1));
        entries.add(EntityType.SKELETON_HORSE, builder -> builder.order(61500).rarity(4)
                .background(SURFACE_NIGHTTIME_RAIN).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME, FilterEntry.RAIN));
        entries.add(EntityType.SNIFFER, builder -> vanillaDay(builder, 61600, 4));
        entries.add(EntityType.SQUID, builder -> oceanDay(builder, 61700, 1));
        entries.add(EntityType.STRIDER, builder -> nether(builder, 61800, 1));
        entries.add(EntityType.TADPOLE, builder -> jungle(builder, 61900, 1));
        entries.add(EntityType.TROPICAL_FISH, builder -> oceanDay(builder, 62000, 1));
        entries.add(EntityType.WANDERING_TRADER, builder -> vanillaDay(builder, 62100, 3));
        entries.add(EntityType.PUFFERFISH, builder -> oceanDay(builder, 62200, 2));
        entries.add(EntityType.GOAT, builder -> builder.order(62300).rarity(2)
                .background(SNOW).filters(FilterEntry.SNOW, FilterEntry.SURFACE, FilterEntry.DAYTIME));
        entries.add(EntityType.VILLAGER, builder -> vanillaDay(builder, 62400, 2));
        entries.add(EntityType.AXOLOTL, builder -> builder.order(62500).rarity(3)
                .background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE));
        entries.add(EntityType.CAT, builder -> vanillaDay(builder, 62600, 3));
        entries.add(EntityType.OCELOT, builder -> builder.order(62700).rarity(4)
                .background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME));
        entries.add(EntityType.SNOW_GOLEM, builder -> builder.order(62800).rarity(2)
                .background(SNOW).filters(FilterEntry.SNOW, FilterEntry.SURFACE, FilterEntry.DAYTIME));
        entries.add(EntityType.BEE, builder -> vanillaDay(builder, 62900, 1));
        entries.add(EntityType.FOX, builder -> builder.order(63000).rarity(2)
                .background(SNOW).filters(FilterEntry.SNOW, FilterEntry.SURFACE, FilterEntry.DAYTIME));
        entries.add(EntityType.IRON_GOLEM, builder -> vanillaDay(builder, 63100, 2));
        entries.add(EntityType.LLAMA, builder -> vanillaDay(builder, 63200, 3));
        entries.add(EntityType.PANDA, builder -> builder.order(63300).rarity(5)
                .background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME));
        entries.add(EntityType.POLAR_BEAR, builder -> builder.order(63400).rarity(3)
                .background(SNOW).filters(FilterEntry.OCEAN, FilterEntry.SNOW, FilterEntry.DAYTIME));
        entries.add(EntityType.TRADER_LLAMA, builder -> vanillaDay(builder, 63500, 3));
        entries.add(EntityType.WOLF, builder -> vanillaDay(builder, 63600, 2));
        entries.add(EntityType.BLAZE, builder -> nether(builder, 63700, 3));
        entries.add(EntityType.CREEPER, builder -> vanillaNight(builder, 64000, 1));
        entries.add(EntityType.ELDER_GUARDIAN, builder -> oceanDay(builder, 64100, 5));
        entries.add(EntityType.ENDERMITE, builder -> builder.order(64200).rarity(2).background(SURFACE).filters(night));
        entries.add(EntityType.EVOKER, builder -> vanillaDay(builder, 64300, 3));
        entries.add(EntityType.GHAST, builder -> nether(builder, 64400, 2));
        entries.add(EntityType.GUARDIAN, builder -> oceanDay(builder, 64500, 2));
        entries.add(EntityType.HOGLIN, builder -> nether(builder, 64600, 2));
        entries.add(EntityType.HUSK, builder -> builder.order(64700).rarity(1)
                .background(DESERT).filters(FilterEntry.DESERT, FilterEntry.SURFACE, FilterEntry.NIGHTTIME));
        entries.add(EntityType.MAGMA_CUBE, builder -> nether(builder, 64800, 2));
        entries.add(EntityType.PHANTOM, builder -> vanillaNight(builder, 64900, 1));
        entries.add(EntityType.PIGLIN_BRUTE, builder -> nether(builder, 65000, 3));
        entries.add(EntityType.PILLAGER, builder -> vanillaDay(builder, 65100, 2));
        entries.add(EntityType.RAVAGER, builder -> vanillaDay(builder, 65200, 3));
        entries.add(EntityType.SHULKER, builder -> builder.order(65300).rarity(2).background(SURFACE).filters(night));
        entries.add(EntityType.SILVERFISH, builder -> cave(builder, 65400, 2));
        entries.add(EntityType.SLIME, builder -> cave(builder, 65500, 3));
        entries.add(EntityType.STRAY, builder -> builder.order(65600).rarity(2)
                .background(SNOW_MOON).filters(FilterEntry.SNOW, FilterEntry.SURFACE, FilterEntry.NIGHTTIME));
        entries.add(EntityType.VEX, builder -> vanillaDay(builder, 65700, 3));
        entries.add(EntityType.VINDICATOR, builder -> vanillaDay(builder, 65700, 2));
        entries.add(EntityType.WARDEN, builder -> cave(builder, 65800, 3));
        entries.add(EntityType.WITCH, builder -> vanillaDay(builder, 65900, 3));
        entries.add(EntityType.WITHER_SKELETON, builder -> nether(builder, 66000, 2));
        entries.add(EntityType.ZOGLIN, builder -> nether(builder, 66100, 2));
        entries.add(EntityType.ZOMBIE_VILLAGER, builder -> vanillaNight(builder, 66200, 2));
        entries.add(EntityType.DROWNED, builder -> builder.order(66300).rarity(2)
                .background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.NIGHTTIME));
        entries.add(EntityType.ENDERMAN, builder -> vanillaNight(builder, 66400, 3));
        entries.add(EntityType.PIGLIN, builder -> nether(builder, 66500, 2));
        entries.add(EntityType.SPIDER, builder -> vanillaNight(builder, 66600, 1));
        entries.add(EntityType.CAVE_SPIDER, builder -> cave(builder, 66700, 2));
        entries.add(EntityType.ZOMBIFIED_PIGLIN, builder -> nether(builder, 66800, 2));
        entries.add(EntityType.ENDER_DRAGON, builder -> builder.order(66900).rarity(5)
                .background(THE_END).filters(FilterEntry.BOSS_ENEMY));
        entries.add(EntityType.WITHER, builder -> builder.order(67000).rarity(2)
                .background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY));
    }

    private static void rareCave(ClientBestiaryEntry.Builder builder, int order) {
        builder.order(order).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE);
    }

    private static void undergroundDesert(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT);
    }

    private static void jungle(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE);
    }

    private static void dungeon(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON);
    }

    private static void nether(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(THE_NETHER).filters(FilterEntry.THE_NETHER);
    }

    private static void corruption(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(THE_CORRUPTION)
                .filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION);
    }

    private static void crimson(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(THE_CRIMSON)
                .filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON);
    }

    private static void goblinInvasion(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION);
    }

    private static void vanillaDay(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(SURFACE_SUN)
                .filters(FilterEntry.SURFACE, FilterEntry.DAYTIME);
    }

    private static void vanillaNight(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(SURFACE_MOON)
                .filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME);
    }

    private static void oceanDay(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(OCEAN)
                .filters(FilterEntry.OCEAN, FilterEntry.DAYTIME);
    }

    private static void cave(ClientBestiaryEntry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(CAVE).filters(FilterEntry.CAVE);
    }

    @Override
    protected PackOutput.PathProvider pathProvider() {
        return pathProvider;
    }

    public static class Builder {
        private final Map<String, ClientBestiaryEntry> map = Maps.newHashMap();

        public Builder add(Supplier<? extends EntityType<?>> holder, String typeKey, String variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            String key = variant.isEmpty() ? typeKey : typeKey + '.' + variant;
            ClientBestiaryEntry.Builder builder = ClientBestiaryEntry.builderc(holder.get(), key);
            consumer.accept(builder);
            builder.description(Component.translatable("bestiary." + key + ".desc"));
            map.put(key, builder.build());
            return this;
        }

        public Builder add(Supplier<? extends EntityType<?>> holder, String variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(holder, holder.get().getDescriptionId(), variant, consumer);
        }

        public Builder add(EntityType<?> type, String variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(type.builtInRegistryHolder(), variant, consumer);
        }

        public Builder add(Supplier<? extends EntityType<?>> holder, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(holder, "", consumer);
        }

        public Builder add(EntityType<?> type, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(type.builtInRegistryHolder(), consumer);
        }

        public <E extends Enum<E> & IVariant> Builder variant(EntityType<?> type, E variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(type, variant.getSerializedName(), consumer.andThen(builder -> builder.entityNbt(variant::serialize)));
        }

        public <E extends Enum<E> & IVariant> Builder variant(Supplier<? extends EntityType<?>> type, E variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return variant(type.get(), variant, consumer);
        }

        public <E extends Enum<E> & IVariant> Builder numberedVariant(
                Supplier<? extends EntityType<?>> type,
                int displayVariant,
                E entityVariant,
                Consumer<ClientBestiaryEntry.Builder> consumer
        ) {
            return numberedVariant(type, type.get().getDescriptionId(), displayVariant, entityVariant, consumer);
        }

        public <E extends Enum<E> & IVariant> Builder numberedVariant(
                Supplier<? extends EntityType<?>> type,
                String typeKey,
                int displayVariant,
                E entityVariant,
                Consumer<ClientBestiaryEntry.Builder> consumer
        ) {
            return add(type, typeKey, Integer.toString(displayVariant),
                    consumer.andThen(builder -> builder.entityNbt(entityVariant::serialize)));
        }

        /// @param armorItems \[鞋子，裤子，衣服，帽子\]
        public Builder mobArmorItems(Supplier<EntityType<?>> holder, String typeKey, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiaryEntry.Builder> consumer) {
            if (armorItems.size() != 4) {
                throw new IllegalArgumentException("Mob armor preview requires exactly four armor item stacks");
            }
            return add(holder, typeKey, variant, consumer.andThen(builder -> builder.entityNbt(nbt -> {
                ListTag listTag = new ListTag();
                for (ItemStack itemStack : armorItems) {
                    if (itemStack.isEmpty()) {
                        listTag.add(new CompoundTag());
                    } else {
                        listTag.add(itemStack.save(new CompoundTag()));
                    }
                }
                nbt.put("ArmorItems", listTag);
            })));
        }

        /// @param armorItems \[鞋子，裤子，衣服，帽子\]
        public Builder mobArmorItems(EntityType<?> type, String typeKey, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return mobArmorItems(type.builtInRegistryHolder(), typeKey, variant, armorItems, provider, consumer);
        }

        /// @param armorItems \[鞋子，裤子，衣服，帽子\]
        public Builder mobArmorItems(Supplier<EntityType<?>> holder, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return mobArmorItems(holder, holder.get().getDescriptionId(), variant, armorItems, provider, consumer);
        }

        /// @param armorItems \[鞋子，裤子，衣服，帽子\]
        public Builder mobArmorItems(EntityType<?> type, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return mobArmorItems(type.builtInRegistryHolder(), variant, armorItems, provider, consumer);
        }
    }
}
