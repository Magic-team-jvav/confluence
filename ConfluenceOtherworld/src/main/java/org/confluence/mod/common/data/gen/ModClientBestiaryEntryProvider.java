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
        FilterEntry[] surfaceDaytime = {FilterEntry.SURFACE, FilterEntry.DAYTIME};
        FilterEntry[] surfaceNighttime = {FilterEntry.SURFACE, FilterEntry.NIGHTTIME};
        recipe(Codec.unboundedMap(Codec.STRING, ClientBestiaryEntry.CODEC), pathProvider().json(Confluence.asResource("bestiary"))).addRecipe(new Builder()
                .add(NpcEntities.GUIDE, builder -> builder.order(100).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.MERCHANT, builder -> builder.order(200).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.NURSE, builder -> builder.order(300).rarity(1).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(NpcEntities.DEMOLITIONIST, builder -> builder.order(400).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(NpcEntities.ANGLER, builder -> builder.order(500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN).entityNbt(tag -> tag.putBoolean(AnglerNPC.WAKE_UP_KEY, true)))
                .add(NpcEntities.DRYAD, builder -> builder.order(600).rarity(3).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(NpcEntities.ARMS_DEALER, builder -> builder.order(700).rarity(1).background(DESERT).filters(FilterEntry.DESERT))
                .add(NpcEntities.DYE_TRADER, builder -> builder.order(800).rarity(2).background(DESERT).filters(FilterEntry.DESERT))
                .add(NpcEntities.PAINTER, builder -> builder.order(900).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(NpcEntities.STYLIST, builder -> builder.order(1000).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(NpcEntities.ZOOLOGIST, builder -> builder.order(1100).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))
                // 酒馆老板.add(NpcEntities.TAVERNKEEP, builder -> builder.order(1200).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(NpcEntities.GOLFER, builder -> builder.order(1300).rarity(2).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.GOBLIN_TINKERER, builder -> builder.order(1400).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(NpcEntities.WITCH_DOCTOR, builder -> builder.order(1500).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(NpcEntities.MECHANIC, builder -> builder.order(1600).rarity(2).background(SNOW).filters(FilterEntry.SNOW))
                .add(NpcEntities.CLOTHIER, builder -> builder.order(1700).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(NpcEntities.WIZARD, builder -> builder.order(1800).rarity(3).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(NpcEntities.STEAMPUNKER, builder -> builder.order(1900).rarity(3).background(DESERT).filters(FilterEntry.DESERT))
                // 海盗.add(NpcEntities.PIRATE, builder -> builder.order(2000).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(NpcEntities.TRUFFLE, builder -> builder.order(2100).rarity(5).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                .add(NpcEntities.TAX_COLLECTOR, builder -> builder.order(2200).rarity(5).background(SNOW).filters(FilterEntry.SNOW))
                .add(NpcEntities.CYBORG, builder -> builder.order(2300).rarity(3).background(SNOW).filters(FilterEntry.SNOW))
                .add(NpcEntities.PARTY_GIRL, builder -> builder.order(2400).rarity(4).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                // 公主.add(NpcEntities.PRINCESS, builder -> builder.order(2500).rarity(5).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                // 圣诞老人.add(NpcEntities.SANTA_CLAUS, builder -> builder.order(2600).rarity(5).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.CHRISTMAS))
                // 宠物.add(NpcEntities.TOWN_CAT, builder -> builder.order(2700).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                // 城镇狗狗.add(NpcEntities.TOWN_DOG, builder -> builder.order(2800).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                // 城镇兔兔.add(NpcEntities.TOWN_BUNNY, builder -> builder.order(2900).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.NERDY_SLIME, builder -> builder.order(3000).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.COOL_SLIME, builder -> builder.order(3100).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.ELDER_SLIME, builder -> builder.order(3200).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.CLUMSY_SLIME, builder -> builder.order(3300).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.DIVA_SLIME, builder -> builder.order(3400).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.SURLY_SLIME, builder -> builder.order(3500).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.MYSTIC_SLIME, builder -> builder.order(3600).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.SQUIRE_SLIME, builder -> builder.order(3700).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.TRAVELING_MERCHANT, builder -> builder.order(3800).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.SKELETON_MERCHANT, builder -> builder.order(3900).rarity(4).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(NpcEntities.OLD_MAN, builder -> builder.order(4000).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(CritterEntities.MYSTIC_FROG, builder -> builder.order(4100).rarity(4).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(CritterEntities.BUNNY, builder -> builder.order(4200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.RABBIT, builder -> builder.order(4201).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                // 兔兔 （戴帽子）
                .variant(CritterEntities.BUNNY, Bunny.Variant.PARTY, builder -> builder.order(4300).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.PARTY, FilterEntry.DAYTIME))
                .add(CritterEntities.EXPLOSIVE_BUNNY, builder -> builder.order(4400).rarity(4).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 兔兔 （史莱姆）
                .variant(CritterEntities.BUNNY, Bunny.Variant.SLIMED, builder -> builder.order(4500).rarity(4).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.HALLOWEEN, FilterEntry.DAYTIME))
                // 兔兔 （圣诞节）
                .variant(CritterEntities.BUNNY, Bunny.Variant.XMAS, builder -> builder.order(4600).rarity(4).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.CHRISTMAS, FilterEntry.DAYTIME))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 4, Bunny.Variant.GOLD, builder -> builder.order(4700).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.BIRD, builder -> builder.order(4800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.BLUE_JAY, builder -> builder.order(4900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.CARDINAL, builder -> builder.order(5000).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                // 绯红金刚鹦鹉
                .add(EntityType.PARROT, builder -> builder.order(5100).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                // 蓝金刚鹦鹉.add(CritterEntities.BLUE_MACAW, builder -> builder.order(5200).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                // 巨嘴鸟.add(CritterEntities.TOUCAN, builder -> builder.order(5300).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                // 黄玄凤鹦鹉.add(CritterEntities.YELLOW_COCKATIEL, builder -> builder.order(5400).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                // 灰玄凤鹦鹉.add(CritterEntities.GRAY_COCKATIEL, builder -> builder.order(5500).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                // 金鸟.add(CritterEntities.GOLD_BIRD, builder -> builder.order(5600).rarity(5).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(CritterEntities.GOLDFISH, builder -> builder.order(5700).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                // 金金鱼.add(CritterEntities.GOLD_GOLDFISH, builder -> builder.order(5800).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))
                .numberedVariant(CritterEntities.SQUIRREL, 0, Squirrel.Variant.NORMAL, builder -> builder.order(5900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.RED_SQUIRREL, "entity.confluence.squirrel", 1, Squirrel.Variant.RED, builder -> builder.order(6000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 1, Squirrel.Variant.GOLD, builder -> builder.order(6100).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                // 老鼠.add(CritterEntities.MOUSE, builder -> builder.order(6200).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 金老鼠.add(CritterEntities.GOLD_MOUSE, builder -> builder.order(6300).rarity(5).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(EntityType.FROG, builder -> builder.order(6400).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 金青蛙.add(CritterEntities.GOLD_FROG, builder -> builder.order(6500).rarity(5).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .numberedVariant(CritterEntities.GRASSHOPPER, 1, Grasshopper.Variant.GREEN, builder -> builder.order(6600).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .numberedVariant(CritterEntities.GRASSHOPPER, 0, Grasshopper.Variant.GOLD, builder -> builder.order(6700).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))
                .numberedVariant(CritterEntities.BUTTERFLY, 1, Butterfly.Variant.JULIA, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 2, Butterfly.Variant.MONARCH, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 3, Butterfly.Variant.PURPLE_EMPEROR, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 4, Butterfly.Variant.RED_ADMIRAL, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 5, Butterfly.Variant.SULPHUR, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 6, Butterfly.Variant.TREE_NYMPH, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 7, Butterfly.Variant.ULYSSES, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 8, Butterfly.Variant.ZEBRA_SWALLOWTAIL, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 0, Butterfly.Variant.GOLD, builder -> builder.order(6900).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.WORM, 2, Worm.Variant.NORMAL, builder -> builder.order(7000).rarity(1).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                .numberedVariant(CritterEntities.WORM, 1, Worm.Variant.GOLD, builder -> builder.order(7100).rarity(5).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                .numberedVariant(CritterEntities.DRAGONFLY, 0, Dragonfly.Variant.BLACK, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 1, Dragonfly.Variant.BLUE, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 3, Dragonfly.Variant.GREEN, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 4, Dragonfly.Variant.ORANGE, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 5, Dragonfly.Variant.RED, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 6, Dragonfly.Variant.YELLOW, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 2, Dragonfly.Variant.GOLD, builder -> builder.order(7300).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                // 海马.add(CritterEntities.SEAHORSE, builder -> builder.order(7400).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                // 金海马.add(CritterEntities.GOLD_SEAHORSE, builder -> builder.order(7500).rarity(5).background(OCEAN).filters(FilterEntry.OCEAN))
                // 水黾.add(CritterEntities.WATER_STRIDER, builder -> builder.order(7600).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                // 金水黾.add(CritterEntities.GOLD_WATER_STRIDER, builder -> builder.order(7700).rarity(5).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .numberedVariant(CritterEntities.LADYBUG, 1, Ladybug.Variant.RED, builder -> builder.order(7800).rarity(3).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .numberedVariant(CritterEntities.LADYBUG, 0, Ladybug.Variant.GOLD, builder -> builder.order(7900).rarity(5).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .add(CritterEntities.STINKBUG, builder -> builder.order(8000).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.FEALING, builder -> builder.order(8100).rarity(5).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.DUCK, 0, Duck.Variant.MALLARD, builder -> builder.order(8200).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DUCK, 1, Duck.Variant.COMMON, builder -> builder.order(8300).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                // 龟.add(CritterEntities.TURTLE, builder -> builder.order(8400).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                // 猫头鹰.add(CritterEntities.OWL, builder -> builder.order(8500).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(CritterEntities.FIREFLY, builder -> builder.order(8600).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .numberedVariant(CritterEntities.WORM, 0, Worm.Variant.NIGHTCRAWLER, builder -> builder.order(8700).rarity(5).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .numberedVariant(CritterEntities.FAIRY, 0, Fairy.Variant.PINK, builder -> builder.order(8800).rarity(4).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .numberedVariant(CritterEntities.FAIRY, 1, Fairy.Variant.GREEN, builder -> builder.order(8900).rarity(4).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .numberedVariant(CritterEntities.FAIRY, 2, Fairy.Variant.BLUE, builder -> builder.order(9000).rarity(4).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                // 大鼠.add(CritterEntities.RAT, builder -> builder.order(9100).rarity(1).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                .add(CritterEntities.MAGGOT, builder -> builder.order(9200).rarity(1).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 2, Squirrel.Variant.AMETHYST, builder -> builder.order(9300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 7, Squirrel.Variant.TOPAZ, builder -> builder.order(9400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 6, Squirrel.Variant.SAPPHIRE, builder -> builder.order(9500).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 4, Squirrel.Variant.EMERALD, builder -> builder.order(9600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 5, Squirrel.Variant.RUBY, builder -> builder.order(9700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 3, Squirrel.Variant.DIAMOND, builder -> builder.order(9800).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 0, Squirrel.Variant.AMBER, builder -> builder.order(9900).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 1, Bunny.Variant.AMETHYST, builder -> builder.order(10000).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 7, Bunny.Variant.TOPAZ, builder -> builder.order(10100).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 6, Bunny.Variant.SAPPHIRE, builder -> builder.order(10200).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 3, Bunny.Variant.EMERALD, builder -> builder.order(10300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 5, Bunny.Variant.RUBY, builder -> builder.order(10400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 2, Bunny.Variant.DIAMOND, builder -> builder.order(10500).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 0, Bunny.Variant.AMBER, builder -> builder.order(10600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(CritterEntities.SNAIL, builder -> builder.order(10700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(CritterEntities.TRUFFLE_WORM, builder -> builder.order(10800).rarity(3).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(CritterEntities.PENGUIN, builder -> builder.order(10900).rarity(1).background(SNOW).filters(FilterEntry.SNOW))
                // 企鹅（黑）.add(CritterEntities.PENGUIN_BLACK, builder -> builder.order(11000).rarity(2).background(SNOW).filters(FilterEntry.SNOW))
                .numberedVariant(CritterEntities.SCORPION, 1, Scorpion.Variant.NORMAL, builder -> builder.order(11100).rarity(1).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                .numberedVariant(CritterEntities.SCORPION, 0, Scorpion.Variant.BLACK, builder -> builder.order(11200).rarity(2).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                // 䴙䴘.add(CritterEntities.GREBE, builder -> builder.order(11300).rarity(2).background(DESERT).filters(FilterEntry.DESERT))
                // 鳉鱼.add(CritterEntities.PUPFISH, builder -> builder.order(11400).rarity(2).background(DESERT).filters(FilterEntry.DESERT))
                // 海鸥.add(CritterEntities.SEAGULL, builder -> builder.order(11500).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(EntityType.TURTLE, builder -> builder.order(11600).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                // 河豚.add(CritterEntities.PUFFERFISH, builder -> builder.order(11700).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(EntityType.DOLPHIN, builder -> builder.order(11800).rarity(3).background(OCEAN).filters(FilterEntry.OCEAN))
                // 丛林龟.add(CritterEntities.JUNGLE_TURTLE, builder -> builder.order(11900).rarity(1).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                .add(CritterEntities.GRUBBY, builder -> builder.order(12000).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(CritterEntities.SLUGGY, builder -> builder.order(12100).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(CritterEntities.BUGGY, builder -> builder.order(12200).rarity(3).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 熔岩萤火虫.add(CritterEntities.LAVAFLY, builder -> builder.order(12300).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(CritterEntities.HELL_BUTTERFLY, builder -> builder.order(12400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(CritterEntities.MAGMA_SNAIL, builder -> builder.order(12500).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(CritterEntities.LIGHTNING_BUG, builder -> builder.order(12600).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW))
                .add(CritterEntities.PRISMATIC_LACEWING, builder -> builder.order(12700).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW)) //神圣夜晚
                .add(CritterEntities.GLOWING_SNAIL, builder -> builder.order(12800).rarity(3).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                // 侏儒
                .add(MonsterEntities.GNOME, builder -> builder.order(12900).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE))
                .add(MonsterEntities.GOBLIN_SCOUT, builder -> builder.order(13000).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE))
                .add(MonsterEntities.GREEN_SLIME, builder -> builder.order(13100).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.BLUE_SLIME, builder -> builder.order(13200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.PURPLE_SLIME, builder -> builder.order(13300).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.PINK_SLIME, builder -> builder.order(13400).rarity(4).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(MonsterEntities.GOLDEN_SLIME, builder -> builder.order(13401).rarity(5).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, surfaceDaytime[0], surfaceDaytime[1]))
                .add(MonsterEntities.SWEET_SLIME, builder -> builder.order(13402).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.SWAMP_SLIME, builder -> builder.order(13403).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.GREEN_DUMPLING_SLIME, builder -> builder.order(13404).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.SPIKED_SLIME, builder -> builder.order(13405).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.TROPIC_SLIME, builder -> builder.order(13406).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME))
                .add(MonsterEntities.WINDY_BALLOON, builder -> builder.order(13500).rarity(2).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .add(MonsterEntities.ANGRY_DANDELION, builder -> builder.order(13600).rarity(2).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                // 雨伞史莱姆.add(MonsterEntities.UMBRELLA_SLIME, builder -> builder.order(13700).rarity(2).background(SURFACE_RAIN).filters(FilterEntry.RAIN, FilterEntry.SURFACE))
                .add(MonsterEntities.FLYING_FISH, builder -> builder.order(13800).rarity(2).background(SURFACE_RAIN).filters(FilterEntry.RAIN, FilterEntry.SURFACE))
                .add(MonsterEntities.ANGRY_NIMBUS, builder -> builder.order(13900).rarity(3).background(SURFACE_RAIN).filters(FilterEntry.RAIN, FilterEntry.SURFACE))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.DILATED, builder -> builder.order(14000).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.DILATED_SMALL, builder -> builder.order(14000).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.SLEEPY, builder -> builder.order(14100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.SLEEPY_BIG, builder -> builder.order(14100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.PURPLE, builder -> builder.order(14200).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.PURPLE_BIG, builder -> builder.order(14200).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.NORMAL, builder -> builder.order(14300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.NORMAL_BIG, builder -> builder.order(14300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.GREEN, builder -> builder.order(14400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.GREEN_SMALL, builder -> builder.order(14400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.CATARACT, builder -> builder.order(14500).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.CATARACT_BIG, builder -> builder.order(14500).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.SPACESHIP, builder -> builder.order(47000).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.HALLOWEEN))
                .variant(MonsterEntities.DEMON_EYE, DemonEye.Variant.OWL, builder -> builder.order(47100).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.HALLOWEEN))
                // 游荡眼球怪.add(MonsterEntities.WANDERING_EYE, builder -> builder.order(14600).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                // 僵尸 （女性）.add(MonsterEntities.ZOMBIE_FEMALE, builder -> builder.order(14700).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "slime", builder -> builder.order(14800).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.SLIMED.getSerializedName())))
                // 僵尸 （光头）
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "bald", builder -> builder.order(14900).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.BALD.getSerializedName())))
                .add(EntityType.ZOMBIE, builder -> builder.order(15000).rarity(1).background(SURFACE_MOON).filters(surfaceNighttime))
                // 僵尸 （纤瘦）
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "twiggy", builder -> builder.order(15100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.TWIGGY.getSerializedName())))
                // 僵尸 （火把）.add(MonsterEntities.ZOMBIE_TORCH, builder -> builder.order(15200).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                // 僵尸 （沼泽）
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "swamp", builder -> builder.order(15300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.SWAMP.getSerializedName())))
                // 僵尸 （中箭）
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "pincushion", builder -> builder.order(15400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.PINCUSHION.getSerializedName())))
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "raincoat", builder -> builder.order(15500).rarity(2).background(SURFACE_MOON).filters(FilterEntry.RAIN, FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.RAINCOAT.getSerializedName())))
                .add(MonsterEntities.POSSESS_ARMOR, builder -> builder.order(15600).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(MonsterEntities.WEREWOLF, builder -> builder.order(15700).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(MonsterEntities.WRAITH, builder -> builder.order(15800).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(CritterEntities.HOSTILE_BUNNY, "entity.confluence.corrupt_bunny", "corrupt", builder -> builder.order(15900).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON).entityNbt(tag -> tag.putString("Variant", "corrupt")))
                .add(MonsterEntities.CORRUPT_PENGUIN, builder -> builder.order(16000).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(CritterEntities.HOSTILE_BUNNY, "entity.confluence.vicious_bunny", "vicious", builder -> builder.order(16100).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON).entityNbt(tag -> tag.putString("Variant", "vicious")))
                .add(MonsterEntities.VICIOUS_PENGUIN, builder -> builder.order(16200).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.BLOOD_ZOMBIE, builder -> builder.order(16300).rarity(1).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.THE_GROOM, builder -> builder.order(16400).rarity(5).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.THE_BRIDE, builder -> builder.order(16500).rarity(5).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.ZOMBIE_MERMAN, builder -> builder.order(16600).rarity(4).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                // 小丑.add(MonsterEntities.CLOWN, builder -> builder.order(16700).rarity(4).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                // 血乌贼.add(MonsterEntities.BLOOD_SQUID, builder -> builder.order(16800).rarity(2).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                // 血鳗鱼.add(MonsterEntities.BLOOD_EEL, builder -> builder.order(16900).rarity(5).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.CORRUPT_GOLDFISH, builder -> builder.order(17000).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.VICIOUS_GOLDFISH, builder -> builder.order(17100).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.DRIPPLER, builder -> builder.order(17200).rarity(2).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                // 嗒嗒牙齿炸弹.add(MonsterEntities.CHATTERING_TEETH_BOMB, builder -> builder.order(17300).rarity(2).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.WANDERING_EYE_FISH, builder -> builder.order(17400).rarity(4).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                // 血浆哥布林鲨鱼.add(MonsterEntities.HEMOGOBLIN_SHARK, builder -> builder.order(17500).rarity(5).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                // 恐惧鹦鹉螺.add(MonsterEntities.DREADNAUTILUS, builder -> builder.order(17600).rarity(5).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                // 弹跳杰克南瓜灯.add(MonsterEntities.HOPPIN_JACK, builder -> builder.order(17700).rarity(2).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                // 蝇蛆僵尸.add(MonsterEntities.MAGGOT_ZOMBIE, builder -> builder.order(17800).rarity(1).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                // 苔藓僵尸.add(MonsterEntities.MOSS_ZOMBIE, builder -> builder.order(17900).rarity(4).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                // 乌鸦.add(MonsterEntities.RAVEN, builder -> builder.order(18000).rarity(2).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                .add(MonsterEntities.GHOST, builder -> builder.order(18100).rarity(3).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                .add(MonsterEntities.RED_SLIME, builder -> builder.order(18300).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(MonsterEntities.YELLOW_SLIME, builder -> builder.order(18400).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 雕像（敌怪）.add(MonsterEntities.STATUE_ENEMY, builder -> builder.order(18200).rarity(5).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                // 毒泥.add(MonsterEntities.TOXIC_SLUDGE, builder -> builder.order(18500).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(MonsterEntities.GIANT_WORM, builder -> builder.order(18600).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(MonsterEntities.DIGGER, builder -> builder.order(18700).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(MonsterEntities.BABY_SLIME, builder -> builder.order(18800).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.BLACK_SLIME, builder -> builder.order(18900).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                // 微光史莱姆.add(MonsterEntities.SHIMMER_SLIME, builder -> builder.order(19000).rarity(5).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.MOTHER_SLIME, builder -> builder.order(19100).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                // 胭脂虫.add(MonsterEntities.COCHINEAL_BEETLE, builder -> builder.order(19200).rarity(3).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                // 骷髅 （畸形）.add(MonsterEntities.SKELETON_MISASSEMBLED, builder -> builder.order(19300).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.SKELETON, builder -> builder.order(19400).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                // 蝾螈.add(MonsterEntities.SALAMANDER, builder -> builder.order(19500).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                // 骷髅 （头痛）.add(MonsterEntities.SKELETON_HEADACHE, builder -> builder.order(19600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                // 骷髅 （无裤）.add(MonsterEntities.SKELETON_PANTLESS, builder -> builder.order(19700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.CRAWDAD, builder -> builder.order(19800).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .mobArmorItems(EntityType.SKELETON, "entity.confluence.undead_miner", "", List.of(ArmorItems.MINING_BOOTS.toStack(), ArmorItems.MINING_LEGGINGS.toStack(), ArmorItems.MINING_CHESTPLATE.toStack(), ArmorItems.MINING_HELMET.toStack()), null, builder -> builder.order(19900).rarity(3).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                // 骷髅弓箭手.add(MonsterEntities.SKELETON_ARCHER, builder -> builder.order(20000).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.NYMPH, builder -> builder.order(20100).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.ARMORED_SKELETON, builder -> builder.order(20200).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.ROCK_GOLEM, builder -> builder.order(20300).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.TIM, builder -> builder.order(20400).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.RUNE_WIZARD, builder -> builder.order(20500).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.CAVE_BAT, builder -> builder.order(20600).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.GIANT_BAT, builder -> builder.order(20700).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.BLUE_JELLYFISH, builder -> builder.order(20800).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.GREEN_JELLYFISH, builder -> builder.order(20900).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.WOODEN_MIMIC, builder -> builder.order(21000).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.GOLDEN_MIMIC, builder -> builder.order(21001).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.SHADOW_MIMIC, builder -> builder.order(21002).rarity(5).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))
                .add(MonsterEntities.GIANT_SHELLY, builder -> builder.order(21100).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                // 迷失女孩.add(MonsterEntities.LOST_GIRL, builder -> builder.order(21200).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.GRANITE_GOLEM, builder -> builder.order(21300).rarity(2).background(GRANITE).filters(FilterEntry.GRANITE))
                .add(MonsterEntities.GRANITE_ELEMENTAL, builder -> builder.order(21400).rarity(2).background(GRANITE).filters(FilterEntry.GRANITE))
                .add(MonsterEntities.HOPLITE, builder -> builder.order(21500).rarity(2).background(MARBLE).filters(FilterEntry.MARBLE))
                // 蛇发女妖.add(MonsterEntities.MEDUSA, builder -> builder.order(21600).rarity(4).background(MARBLE).filters(FilterEntry.RARE_CREATURE, FilterEntry.MARBLE))
                .add(MonsterEntities.SPORE_SKELETON, builder -> builder.order(21700).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.SPORE_BAT, builder -> builder.order(21800).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.WALL_CREEPER, builder -> builder.order(21900).rarity(2).background(SPIDER_NEST).filters(FilterEntry.SPIDER_NEST))
                .add(MonsterEntities.BLACK_RECLUSE, builder -> builder.order(22000).rarity(2).background(SPIDER_NEST).filters(FilterEntry.SPIDER_NEST))
                .add(MonsterEntities.ICE_SLIME, builder -> builder.order(22100).rarity(1).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.DAYTIME))
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "frozen", builder -> builder.order(22200).rarity(2).background(SNOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.SNOW).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.ESKIMO.getSerializedName())))
                .add(MonsterEntities.ICE_GOLEM, builder -> builder.order(22300).rarity(5).background(SNOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.RAIN, FilterEntry.SNOW))
                // 狼.add(MonsterEntities.WOLF, builder -> builder.order(22400).rarity(2).background(SNOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.SNOW))
                .add(MonsterEntities.SPIKED_ICE_SLIME, builder -> builder.order(22500).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                // 青壳虫.add(MonsterEntities.CYAN_BEETLE, builder -> builder.order(22600).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.ICE))
                .add(MonsterEntities.UNDEAD_VIKING, builder -> builder.order(22700).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.SNOW_FLINX, builder -> builder.order(22800).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.ARMORED_VIKING, builder -> builder.order(22900).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.ICY_MERMAN, builder -> builder.order(23000).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.ICE_BAT, builder -> builder.order(23100).rarity(1).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.ICE_ELEMENTAL, builder -> builder.order(23200).rarity(2).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.ICE))
                .add(MonsterEntities.ICE_MIMIC, builder -> builder.order(23300).rarity(5).background(UNDERGROUND_SNOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.ICE))
                .add(MonsterEntities.ICE_TORTOISE, builder -> builder.order(23400).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                // 秃鹰.add(MonsterEntities.VULTURE, builder -> builder.order(23500).rarity(1).background(DESERT).filters(FilterEntry.DESERT))
                .add(MonsterEntities.DESERT_SLIME, builder -> builder.order(23600).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 蚁狮幼虫
                .add(MonsterEntities.ANTLION_LARVA, builder -> builder.order(23700).rarity(1).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 巨型蚁狮马.add(MonsterEntities.GIANT_ANTLION_CHARGER, builder -> builder.order(23800).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.MUMMY, builder -> builder.order(23900).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.GHOUL, builder -> builder.order(24000).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.BASILISK, builder -> builder.order(24100).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.TOMB_CRAWLER, builder -> builder.order(24200).rarity(1).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.ANTLION, builder -> builder.order(24300).rarity(1).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.SAND_POACHER, builder -> builder.order(24400).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.GIANT_ANTLION_SWARMER, builder -> builder.order(24500).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.ANTLION_CHARGER, builder -> builder.order(24600).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT, FilterEntry.SANDSTORM))
                // 沙虫.add(MonsterEntities.DUNE_SPLICER, builder -> builder.order(24700).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.ANGRY_TUMBLER, builder -> builder.order(24800).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.ANTLION_SWARMER, builder -> builder.order(24900).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT, FilterEntry.SANDSTORM))
                // 沙尘精.add(MonsterEntities.SAND_ELEMENTAL, builder -> builder.order(25000).rarity(4).background(DESERT).filters(FilterEntry.RARE_CREATURE, FilterEntry.DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.SAND_SHARK, builder -> builder.order(25100).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.SANDSTORM))
                .add(CritterEntities.CRAB, builder -> builder.order(25200).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                // 海蜗牛.add(MonsterEntities.SEA_SNAIL, builder -> builder.order(25300).rarity(4).background(OCEAN).filters(FilterEntry.RARE_CREATURE, FilterEntry.OCEAN))
                .add(MonsterEntities.SHARK, builder -> builder.order(25400).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                // 虎鲸.add(MonsterEntities.ORCA, builder -> builder.order(25500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                // 乌贼.add(MonsterEntities.SQUID, builder -> builder.order(25600).rarity(3).background(OCEAN).filters(FilterEntry.RARE_CREATURE, FilterEntry.OCEAN))
                .add(MonsterEntities.PINK_JELLYFISH, builder -> builder.order(25700).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(MonsterEntities.JUNGLE_SLIME, builder -> builder.order(25800).rarity(1).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                .add(MonsterEntities.SNATCHER, builder -> builder.order(25900).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 巨型飞狐
                .add(MonsterEntities.GIANT_FLYING_FOX, builder -> builder.order(26000).rarity(2).background(THE_JUNGLE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_JUNGLE))
                .add(MonsterEntities.DERPLING, builder -> builder.order(26100).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(MonsterEntities.SPIKED_JUNGLE_SLIME, builder -> builder.order(26200).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 紫胶虫.add(MonsterEntities.LAC_BEETLE, builder -> builder.order(26300).rarity(3).background(UNDERGROUND_JUNGLE).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.DOCTOR_BONES, builder -> builder.order(26400).rarity(5).background(THE_JUNGLE_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE, FilterEntry.NIGHTTIME))
                // 蜜蜂.add(MonsterEntities.BEE, builder -> builder.order(26500).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 蜜蜂 （较大）.add(MonsterEntities.BEE_LARGER, builder -> builder.order(26600).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 黄蜂 （毒刺）.add(MonsterEntities.HORNET_STINGY, builder -> builder.order(26700).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 黄蜂 （尖刺）.add(MonsterEntities.HORNET_SPIKEY, builder -> builder.order(26800).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.HORNET, builder -> builder.order(26900).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 黄蜂（肥胖）.add(MonsterEntities.HORNET_FATTY, builder -> builder.order(27000).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 黄蜂（蜂蜜）.add(MonsterEntities.HORNET_HONEY, builder -> builder.order(27100).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 黄蜂（多叶）.add(MonsterEntities.HORNET_LEAFY, builder -> builder.order(27200).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.MOSS_HORNET, builder -> builder.order(27300).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 蛾.add(MonsterEntities.MOTH, builder -> builder.order(27400).rarity(4).background(UNDERGROUND_JUNGLE).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.MAN_EATER, builder -> builder.order(27500).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 愤怒捕手.add(MonsterEntities.ANGRY_TRAPPER, builder -> builder.order(27600).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.JUNGLE_BAT, builder -> builder.order(27700).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.PIRANHA, builder -> builder.order(27800).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.ANGLER_FISH, builder -> builder.order(27900).rarity(2).background(CAVE).filters(FilterEntry.CAVE, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.ARAPAIMA, builder -> builder.order(28000).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.JUNGLE_MIMIC, builder -> builder.order(28001).rarity(5).background(UNDERGROUND).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                // 巨型陆龟
                .add(MonsterEntities.GIANT_TORTOISE, builder -> builder.order(28100).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.JUNGLE_CREEPER, builder -> builder.order(28200).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.METEOR_HEAD, builder -> builder.order(28300).rarity(2).background(METEOR).filters(FilterEntry.METEOR))
                .add(MonsterEntities.DUNGEON_SLIME, builder -> builder.order(28400).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.ANGER_BONES, builder -> builder.order(28500).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.SHORT_BONES, builder -> builder.order(28501).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BIG_BONES, builder -> builder.order(28600).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BIG_ANGER_BONES, builder -> builder.order(28601).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BIG_MUSCLE_ANGER_BONES, builder -> builder.order(28700).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BIG_HELMET_ANGER_BONES, builder -> builder.order(28800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 蓝装甲骷髅 （锤矛）.add(MonsterEntities.BLUE_ARMORED_BONES_MACE, builder -> builder.order(28900).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 骷髅狙击手.add(MonsterEntities.SKELETON_SNIPER, builder -> builder.order(29000).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                // 骷髅特警.add(MonsterEntities.TACTICAL_SKELETON, builder -> builder.order(29100).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                // 骷髅突击手.add(MonsterEntities.SKELETON_COMMANDO, builder -> builder.order(29200).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                // 地狱装甲骷髅.add(MonsterEntities.HELL_ARMORED_BONES, builder -> builder.order(29300).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 生锈装甲骷髅 （剑无装甲）.add(MonsterEntities.RUSTY_ARMORED_BONES_SWORD_NO_ARMOR, builder -> builder.order(29400).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 生锈装甲骷髅 （连枷）.add(MonsterEntities.RUSTY_ARMORED_BONES_FLAIL, builder -> builder.order(29500).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 地狱装甲骷髅 （锤矛）.add(MonsterEntities.HELL_ARMORED_BONES_MACE, builder -> builder.order(29600).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 蓝装甲骷髅.add(MonsterEntities.BLUE_ARMORED_BONES, builder -> builder.order(29700).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 生锈装甲骷髅 （剑）.add(MonsterEntities.RUSTY_ARMORED_BONES_SWORD, builder -> builder.order(29800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 地狱装甲骷髅 （尖刺盾）.add(MonsterEntities.HELL_ARMORED_BONES_SPIKE_SHIELD, builder -> builder.order(29900).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 蓝装甲骷髅 （无裤）.add(MonsterEntities.BLUE_ARMORED_BONES_NO_PANTS, builder -> builder.order(30000).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 地狱装甲骷髅 （剑）.add(MonsterEntities.HELL_ARMORED_BONES_SWORD, builder -> builder.order(30100).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 生锈装甲骷髅 （斧头）.add(MonsterEntities.RUSTY_ARMORED_BONES_AXE, builder -> builder.order(30200).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 蓝装甲骷髅 （剑）.add(MonsterEntities.BLUE_ARMORED_BONES_SWORD, builder -> builder.order(30300).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BONE_LEE, builder -> builder.order(30400).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.PALADIN, builder -> builder.order(30500).rarity(5).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.DARK_CASTER, builder -> builder.order(30600).rarity(1).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 图书管理员骷髅.add(MonsterEntities.LIBRARIAN_SKELETON, builder -> builder.order(30700).rarity(1).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.WATER_BOLT_MIMIC, builder -> builder.order(31400).rarity(1).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.DIABOLIST, builder -> builder.order(30800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 魔教徒.add(MonsterEntities.DIABOLIST_WHITE, builder -> builder.order(30900).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 死灵法师
                .add(MonsterEntities.NECROMANCER, builder -> builder.order(31000).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 褴褛邪教徒法师
                .add(MonsterEntities.RAGGED_CASTER, builder -> builder.order(31100).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 死灵法师 （装甲）.add(MonsterEntities.NECROMANCER_ARMORED, builder -> builder.order(31200).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 褴褛邪教徒法师 （敞开外衣）.add(MonsterEntities.RAGGED_CASTER_OPEN_COAT, builder -> builder.order(31300).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.CURSED_SKULL, builder -> builder.order(31500).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 巨型诅咒骷髅头.add(MonsterEntities.GIANT_CURSED_SKULL, builder -> builder.order(31600).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(BossEntities.DUNGEON_GUARDIAN, builder -> builder.order(31700).rarity(4).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.DUNGEON_SPIRIT, builder -> builder.order(31800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.LAVA_SLIME, builder -> builder.order(31900).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                // 痛苦亡魂.add(MonsterEntities.TORTURED_SOUL, builder -> builder.order(32000).rarity(5).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))
                .add(MonsterEntities.BONE_SERPENT, builder -> builder.order(32100).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.FIRE_IMP, builder -> builder.order(32200).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.HELL_BAT, builder -> builder.order(32300).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.DEMON, builder -> builder.order(32400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.VOODOO_DEMON, builder -> builder.order(32500).rarity(3).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))
                .add(MonsterEntities.LAVA_BAT, builder -> builder.order(32600).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.RED_DEVIL, builder -> builder.order(32700).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.WYVERN, builder -> builder.order(32800).rarity(3).background(SKY).filters(FilterEntry.SKY))
                .add(MonsterEntities.HARPY, builder -> builder.order(32900).rarity(2).background(SKY).filters(FilterEntry.SKY))
                // 火星探测器.add(MonsterEntities.MARTIAN_PROBE, builder -> builder.order(33000).rarity(4).background(SKY).filters(FilterEntry.RARE_CREATURE, FilterEntry.SKY))
                // 小史莱姆.add(MonsterEntities.SLIMELING, builder -> builder.order(33100).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.CORRUPT_SLIME, builder -> builder.order(33200).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.EATER_OF_SOULS, builder -> builder.order(33300).rarity(1).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                // 腐化者.add(MonsterEntities.CORRUPTOR, builder -> builder.order(33400).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                // 噬魂怪
                .add(MonsterEntities.DEVOURER, builder -> builder.order(33500).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.WORLD_FEEDER, builder -> builder.order(33600).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.CLINGER, builder -> builder.order(33700).rarity(2).background(UNDERGROUND_CORRUPTION).filters(FilterEntry.UNDERGROUND_CORRUPTION))
                // 恶翅史莱姆
                .add(MonsterEntities.SLIMER, builder -> builder.order(33800).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                // 诅咒锤.add(MonsterEntities.CURSED_HAMMER, builder -> builder.order(33900).rarity(2).background(UNDERGROUND_CORRUPTION).filters(FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.CORRUPT_MIMIC, builder -> builder.order(34000).rarity(5).background(UNDERGROUND_CORRUPTION).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_CORRUPTION))
                // 猪龙 （腐化）.add(MonsterEntities.PIGRON_CORRUPT, builder -> builder.order(34100).rarity(3).background(CORRUPT_ICE).filters(FilterEntry.CORRUPT_ICE))
                .add(MonsterEntities.BONE_BITER, builder -> builder.order(34200).rarity(2).background(CORRUPT_DESERT).filters(FilterEntry.CORRUPT_DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.DARK_MUMMY, builder -> builder.order(34300).rarity(2).background(CORRUPT_DESERT).filters(FilterEntry.CORRUPT_DESERT, FilterEntry.CORRUPT_CAVE_DESERT))
                .add(MonsterEntities.VILE_GHOUL, builder -> builder.order(34400).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CORRUPT_CAVE_DESERT))
                .add(MonsterEntities.CRIMSLIME, builder -> builder.order(34500).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.FACE_MONSTER, builder -> builder.order(34600).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.CRIMERA, builder -> builder.order(34700).rarity(1).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                // 嗜血怪
                .add(MonsterEntities.BLOOD_FEEDER, builder -> builder.order(34800).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.BLOOD_JELLY, builder -> builder.order(34900).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                // 恶心浮游怪.add(MonsterEntities.FLOATY_GROSS, builder -> builder.order(35000).rarity(2).background(UNDERGROUND_CRIMSON).filters(FilterEntry.UNDERGROUND_CRIMSON))
                // 灵液黏黏怪.add(MonsterEntities.ICHOR_STICKER, builder -> builder.order(35100).rarity(2).background(UNDERGROUND_CRIMSON).filters(FilterEntry.UNDERGROUND_CRIMSON))
                // 猩红斧.add(MonsterEntities.CRIMSON_AXE, builder -> builder.order(35200).rarity(2).background(UNDERGROUND_CRIMSON).filters(FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.BLOOD_CRAWLER, builder -> builder.order(35300).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.HERPLING, builder -> builder.order(35400).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.CRIMSON_MIMIC, builder -> builder.order(35500).rarity(5).background(UNDERGROUND_CRIMSON).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_CRIMSON))
                // 猪龙 （猩红）.add(MonsterEntities.PIGRON_CRIMSON, builder -> builder.order(35600).rarity(3).background(CRIMSON_ICE).filters(FilterEntry.CRIMSON_ICE))
                .add(MonsterEntities.FLESH_REAVER, builder -> builder.order(35700).rarity(2).background(CRIMSON_DESERT).filters(FilterEntry.CRIMSON_DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.BLOOD_MUMMY, builder -> builder.order(35800).rarity(2).background(CRIMSON_DESERT).filters(FilterEntry.CRIMSON_DESERT, FilterEntry.CRIMSON_CAVE_DESERT))
                .add(MonsterEntities.TAINTED_GHOUL, builder -> builder.order(35900).rarity(2).background(CRIMSON_CAVE_DESERT).filters(FilterEntry.CRIMSON_CAVE_DESERT))
                .add(MonsterEntities.DARK_LAMIA, builder -> builder.order(36000).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CORRUPT_CAVE_DESERT, FilterEntry.CRIMSON_CAVE_DESERT))
                .add(MonsterEntities.DESERT_SPIRIT, builder -> builder.order(36100).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CORRUPT_CAVE_DESERT, FilterEntry.CRIMSON_CAVE_DESERT))
                // 彩虹史莱姆.add(MonsterEntities.RAINBOW_SLIME, builder -> builder.order(36200).rarity(4).background(THE_HALLOW_RAIN).filters(FilterEntry.RARE_CREATURE, FilterEntry.RAIN, FilterEntry.THE_HALLOW))
                .add(MonsterEntities.PIXIE, builder -> builder.order(36300).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                // 腹足怪
                .add(MonsterEntities.GASTROPOD, builder -> builder.order(36400).rarity(2).background(THE_HALLOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW))
                // 独角兽
                .add(MonsterEntities.UNICORN, builder -> builder.order(36500).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(MonsterEntities.LUMINOUS_SLIME, builder -> builder.order(36600).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                // 混沌精
                .add(MonsterEntities.CHAOS_ELEMENTAL, builder -> builder.order(36700).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                .add(MonsterEntities.ILLUMINANT_BAT, builder -> builder.order(36800).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                // 附魔剑
                .add(MonsterEntities.ENCHANTED_SWORD, builder -> builder.order(36900).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                .add(MonsterEntities.HALLOWED_MIMIC, builder -> builder.order(37000).rarity(5).background(UNDERGROUND_HALLOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_HALLOW))
                // 猪龙.add(MonsterEntities.PIGRON, builder -> builder.order(37100).rarity(3).background(HALLOW_ICE).filters(FilterEntry.HALLOW_ICE))
                .add(MonsterEntities.CRYSTAL_THRESHER, builder -> builder.order(37200).rarity(2).background(HALLOW_DESERT).filters(FilterEntry.HALLOW_DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.LIGHT_MUMMY, builder -> builder.order(37300).rarity(2).background(HALLOW_DESERT).filters(FilterEntry.HALLOW_DESERT, FilterEntry.HALLOW_CAVE_DESERT))
                .add(MonsterEntities.DREAMER_GHOUL, builder -> builder.order(37400).rarity(2).background(HALLOW_CAVE_DESERT).filters(FilterEntry.HALLOW_CAVE_DESERT))
                .add(MonsterEntities.LIGHT_LAMIA, builder -> builder.order(37500).rarity(2).background(HALLOW_CAVE_DESERT).filters(FilterEntry.UNDERGROUND_DESERT, FilterEntry.HALLOW_CAVE_DESERT))
                .add(MonsterEntities.SPORE_ZOMBIE, builder -> builder.order(37600).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                .add(MonsterEntities.HAT_SPORE_ZOMBIE, builder -> builder.order(37700).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                // 歪尾真菌.add(MonsterEntities.ANOMURA_FUNGUS, builder -> builder.order(37800).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                // 蘑菇瓢虫.add(MonsterEntities.MUSHI_LADYBUG, builder -> builder.order(37900).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.FUNGI_BULB, builder -> builder.order(38000).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.GIANT_FUNGI_BULB, builder -> builder.order(38100).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.FUNGO_FISH, builder -> builder.order(38200).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                // 丛林蜥蜴.add(MonsterEntities.LIHZAHRD, builder -> builder.order(38300).rarity(2).background(THE_TEMPLE).filters(FilterEntry.THE_TEMPLE))
                // 飞蛇.add(MonsterEntities.FLYING_SNAKE, builder -> builder.order(38400).rarity(2).background(THE_TEMPLE).filters(FilterEntry.THE_TEMPLE))
                .add(MonsterEntities.GOBLIN_PEON, builder -> builder.order(38500).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_THIEF, builder -> builder.order(38600).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_ARCHER, builder -> builder.order(38700).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_WARRIOR, builder -> builder.order(38800).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_SORCERER, builder -> builder.order(39000).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.ANGER_GOBLIN, builder -> builder.order(39001).rarity(3).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_WARLOCK, builder -> builder.order(38900).rarity(4).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.SHADOWFLAME_APPARITION, builder -> builder.order(39100).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                // 撒旦骷髅.add(MonsterEntities.OLD_ONE_S_SKELETON, builder -> builder.order(39200).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 埃特尼亚哥布林.add(MonsterEntities.ETHERIAN_GOBLIN, builder -> builder.order(39300).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 埃特尼亚哥布林投弹手.add(MonsterEntities.ETHERIAN_GOBLIN_BOMBER, builder -> builder.order(39400).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 小妖魔.add(MonsterEntities.KOBOLD, builder -> builder.order(39500).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 埃特尼亚标枪投掷怪.add(MonsterEntities.ETHERIAN_JAVELIN_THROWER, builder -> builder.order(39600).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 枯萎兽.add(MonsterEntities.WITHER_BEAST, builder -> builder.order(39700).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 德拉克龙.add(MonsterEntities.DRAKIN, builder -> builder.order(39800).rarity(3).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 食人魔.add(MonsterEntities.OGRE, builder -> builder.order(39900).rarity(3).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 小妖魔滑翔怪.add(MonsterEntities.KOBOLD_GLIDER, builder -> builder.order(40000).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 埃特尼亚飞龙.add(MonsterEntities.ETHERIAN_WYVERN, builder -> builder.order(40100).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 黑暗魔法师.add(MonsterEntities.DARK_MAGE, builder -> builder.order(40200).rarity(3).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 双足翼龙.add(MonsterEntities.BETSY, builder -> builder.order(40300).rarity(4).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                // 埃特尼亚荧光虫.add(MonsterEntities.ETHERIAN_LIGHTNING_BUG, builder -> builder.order(40400).rarity(2).background(SURFACE).filters(FilterEntry.OLD_ONES_ARMY))
                .add(MonsterEntities.PIRATE_DEADEYE, builder -> builder.order(40500).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_DECKHAND, builder -> builder.order(40600).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_CROSSBOWER, builder -> builder.order(40700).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_CORSAIR, builder -> builder.order(40800).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_CAPTAIN, builder -> builder.order(40900).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATES_CURSE, builder -> builder.order(40901).rarity(3).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_PARROT, builder -> builder.order(41000).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                // 荷兰飞盗船.add(MonsterEntities.FLYING_DUTCHMAN, builder -> builder.order(41100).rarity(4).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                // 扰脑怪.add(MonsterEntities.BRAIN_SCRAMBLER, builder -> builder.order(41200).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 激光枪手.add(MonsterEntities.RAY_GUNNER, builder -> builder.order(41300).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 火星工程师.add(MonsterEntities.MARTIAN_ENGINEER, builder -> builder.order(41400).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 火星军官.add(MonsterEntities.MARTIAN_OFFICER, builder -> builder.order(41500).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 电击怪.add(MonsterEntities.GIGAZAPPER, builder -> builder.order(41600).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 鳞甲怪.add(MonsterEntities.SCUTLIX, builder -> builder.order(41700).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 灰咕噜.add(MonsterEntities.GRAY_GRUNT, builder -> builder.order(41800).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 火星走妖.add(MonsterEntities.MARTIAN_WALKER, builder -> builder.order(41900).rarity(3).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 特斯拉炮塔.add(MonsterEntities.TESLA_TURRET, builder -> builder.order(42000).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 火星飞船.add(MonsterEntities.MARTIAN_DRONE, builder -> builder.order(42100).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 鳞甲怪枪手.add(MonsterEntities.SCUTLIX_GUNNER, builder -> builder.order(42200).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 弗里茨.add(MonsterEntities.FRITZ, builder -> builder.order(42400).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 科学怪人.add(MonsterEntities.FRANKENSTEIN, builder -> builder.order(42500).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 水月怪.add(MonsterEntities.CREATURE_FROM_THE_DEEP, builder -> builder.order(42600).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 沼泽怪.add(MonsterEntities.SWAMP_THING, builder -> builder.order(42700).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 苍蝇人博士.add(MonsterEntities.DR_MAN_FLY, builder -> builder.order(42800).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 攀爬魔.add(MonsterEntities.THE_POSSESSED, builder -> builder.order(42900).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 变态人.add(MonsterEntities.PSYCHO, builder -> builder.order(43000).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 屠夫.add(MonsterEntities.BUTCHER, builder -> builder.order(43100).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 吸血鬼.add(MonsterEntities.VAMPIRE, builder -> builder.order(43200).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 眼怪.add(MonsterEntities.EYEZOR, builder -> builder.order(43300).rarity(5).background(ECLIPSE).filters(FilterEntry.RARE_CREATURE, FilterEntry.ECLIPSE))
                // 钉头.add(MonsterEntities.NAILHEAD, builder -> builder.order(43400).rarity(3).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 死神.add(MonsterEntities.REAPER, builder -> builder.order(43500).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 致命球.add(MonsterEntities.DEADLY_SPHERE, builder -> builder.order(43600).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 蛾怪.add(MonsterEntities.MOTHRON, builder -> builder.order(43700).rarity(5).background(ECLIPSE).filters(FilterEntry.RARE_CREATURE, FilterEntry.ECLIPSE))
                // 蛾怪宝宝.add(MonsterEntities.BABY_MOTHRON, builder -> builder.order(43800).rarity(2).background(ECLIPSE).filters(FilterEntry.ECLIPSE))
                // 稻草人 （布衣，有脸，棍式）.add(MonsterEntities.SCARECROW_CLOTH_FACE_STICK, builder -> builder.order(43900).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （布衣，有脸）.add(MonsterEntities.SCARECROW_CLOTH_FACE, builder -> builder.order(44000).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （盖伊·福克斯，棍式）.add(MonsterEntities.SCARECROW_GUY_FAWKES_STICK, builder -> builder.order(44100).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （盖伊·福克斯）.add(MonsterEntities.SCARECROW_GUY_FAWKES, builder -> builder.order(44200).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （布衣，戴帽，棍式）.add(MonsterEntities.SCARECROW_CLOTH_HAT_STICK, builder -> builder.order(44300).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （布衣，戴帽）.add(MonsterEntities.SCARECROW_CLOTH_HAT, builder -> builder.order(44400).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （南瓜头，戴帽，棍式）.add(MonsterEntities.SCARECROW_PUMPKIN_HAT_STICK, builder -> builder.order(44500).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （南瓜头，戴帽）.add(MonsterEntities.SCARECROW_PUMPKIN_HAT, builder -> builder.order(44600).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （南瓜头，棍式）.add(MonsterEntities.SCARECROW_PUMPKIN_HEAD_STICK, builder -> builder.order(44700).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 稻草人 （南瓜头）.add(MonsterEntities.SCARECROW_PUMPKIN_HEAD, builder -> builder.order(44800).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 树精.add(MonsterEntities.SPLINTERLING, builder -> builder.order(44900).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 胡闹鬼.add(MonsterEntities.POLTERGEIST, builder -> builder.order(45000).rarity(2).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 地狱犬.add(MonsterEntities.HELLHOUND, builder -> builder.order(45100).rarity(3).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 无头骑士.add(MonsterEntities.HEADLESS_HORSEMAN, builder -> builder.order(45200).rarity(3).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 哀木.add(MonsterEntities.MOURNING_WOOD, builder -> builder.order(45300).rarity(3).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 南瓜王.add(MonsterEntities.PUMPKING, builder -> builder.order(45400).rarity(4).background(PUMPKIN_MOON).filters(FilterEntry.PUMPKIN_MOON))
                // 僵尸精灵 （女孩）.add(MonsterEntities.ZOMBIE_ELF_GIRL, builder -> builder.order(45500).rarity(2).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 僵尸精灵.add(MonsterEntities.ZOMBIE_ELF, builder -> builder.order(45600).rarity(2).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 僵尸精灵 （胡子）.add(MonsterEntities.ZOMBIE_ELF_BEARD, builder -> builder.order(45700).rarity(2).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 姜饼人.add(MonsterEntities.GINGERBREAD_MAN, builder -> builder.order(45800).rarity(2).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 精灵弓箭手.add(MonsterEntities.ELF_ARCHER, builder -> builder.order(45900).rarity(2).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 胡桃夹士.add(MonsterEntities.NUTCRACKER, builder -> builder.order(46000).rarity(3).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 坎卜斯.add(MonsterEntities.KRAMPUS, builder -> builder.order(46100).rarity(3).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 雪兽.add(MonsterEntities.YETI, builder -> builder.order(46200).rarity(3).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 礼物宝箱怪.add(MonsterEntities.PRESENT_MIMIC, builder -> builder.order(46300).rarity(2).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 常绿尖叫怪.add(MonsterEntities.EVERSCREAM, builder -> builder.order(46400).rarity(3).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 冰雪女王.add(MonsterEntities.ICE_QUEEN, builder -> builder.order(46500).rarity(4).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 圣诞坦克.add(MonsterEntities.SANTA_NK1, builder -> builder.order(46600).rarity(3).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 精灵直升机.add(MonsterEntities.ELF_COPTER, builder -> builder.order(46700).rarity(2).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 雪花怪.add(MonsterEntities.FLOCKO, builder -> builder.order(46800).rarity(2).background(FROST_MOON).filters(FilterEntry.FROST_MOON))
                // 史莱姆 （兔兔面具）.add(MonsterEntities.SLIME_BUNNY_MASK, builder -> builder.order(46900).rarity(3).background(SURFACE_SUN).filters(FilterEntry.HALLOWEEN, FilterEntry.SURFACE, FilterEntry.DAYTIME))
                // 恶魔眼 （太空船
                // 恶魔眼 （猫头鹰面具）
                // 僵尸 （医生）.add(MonsterEntities.ZOMBIE_DOCTOR, builder -> builder.order(47200).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.HALLOWEEN))
                // 僵尸 （超人）.add(MonsterEntities.ZOMBIE_SUPERMAN, builder -> builder.order(47300).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.HALLOWEEN))
                // 僵尸 （妖精）.add(MonsterEntities.ZOMBIE_PIXIE, builder -> builder.order(47400).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.HALLOWEEN))
                // 骷髅 （宇航员）.add(MonsterEntities.SKELETON_ASTRONAUT, builder -> builder.order(47500).rarity(3).background(CAVE).filters(FilterEntry.HALLOWEEN, FilterEntry.CAVE))
                // 骷髅 （异星）.add(MonsterEntities.SKELETON_ALIEN, builder -> builder.order(47600).rarity(3).background(CAVE).filters(FilterEntry.HALLOWEEN, FilterEntry.CAVE))
                // 骷髅 （高顶礼帽）.add(MonsterEntities.SKELETON_TOP_HAT, builder -> builder.order(47700).rarity(3).background(CAVE).filters(FilterEntry.HALLOWEEN, FilterEntry.CAVE))
                // 史莱姆 （红礼物史莱姆）.add(MonsterEntities.SLIME_RED_PRESENT_SLIME, builder -> builder.order(47800).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.CHRISTMAS, FilterEntry.DAYTIME))
                // 史莱姆 （黄礼物史莱姆）.add(MonsterEntities.SLIME_YELLOW_PRESENT_SLIME, builder -> builder.order(47900).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.CHRISTMAS, FilterEntry.DAYTIME))
                // 史莱姆 （白礼物史莱姆）.add(MonsterEntities.SLIME_WHITE_PRESENT_SLIME, builder -> builder.order(48000).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.CHRISTMAS, FilterEntry.DAYTIME))
                // 史莱姆 （绿礼物史莱姆）.add(MonsterEntities.SLIME_GREEN_PRESENT_SLIME, builder -> builder.order(48100).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.CHRISTMAS, FilterEntry.DAYTIME))
                // 僵尸 （圣诞节）.add(MonsterEntities.ZOMBIE_XMAS, builder -> builder.order(48200).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.CHRISTMAS))
                // 僵尸 （毛衣）.add(MonsterEntities.ZOMBIE_SWEATER, builder -> builder.order(48300).rarity(3).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.CHRISTMAS))
                // 雪人暴徒.add(MonsterEntities.SNOWMAN_GANGSTA, builder -> builder.order(48400).rarity(2).background(SURFACE).filters(FilterEntry.FROST_LEGION))
                // 巴拉雪人.add(MonsterEntities.SNOW_BALLA, builder -> builder.order(48500).rarity(2).background(SURFACE).filters(FilterEntry.FROST_LEGION))
                // 戳刺先生.add(MonsterEntities.MISTER_STABBY, builder -> builder.order(48600).rarity(2).background(SURFACE).filters(FilterEntry.FROST_LEGION))
                // 预言怪.add(MonsterEntities.PREDICTOR, builder -> builder.order(48700).rarity(2).background(NEBULA_PILLAR).filters(FilterEntry.NEBULA_PILLAR))
                // 进化兽.add(MonsterEntities.EVOLUTION_BEAST, builder -> builder.order(48800).rarity(2).background(NEBULA_PILLAR).filters(FilterEntry.NEBULA_PILLAR))
                // 吮脑怪.add(MonsterEntities.BRAIN_SUCKLER, builder -> builder.order(48900).rarity(2).background(NEBULA_PILLAR).filters(FilterEntry.NEBULA_PILLAR))
                // 星云浮怪.add(MonsterEntities.NEBULA_FLOATER, builder -> builder.order(49000).rarity(2).background(NEBULA_PILLAR).filters(FilterEntry.NEBULA_PILLAR))
                // 火龙怪.add(MonsterEntities.DRAKOMIRE, builder -> builder.order(49100).rarity(2).background(SOLAR_PILLAR).filters(FilterEntry.SOLAR_PILLAR))
                // 火月怪.add(MonsterEntities.SELENIAN, builder -> builder.order(49200).rarity(2).background(SOLAR_PILLAR).filters(FilterEntry.SOLAR_PILLAR))
                // 火龙战士.add(MonsterEntities.DRAKANIAN, builder -> builder.order(49300).rarity(2).background(SOLAR_PILLAR).filters(FilterEntry.SOLAR_PILLAR))
                // 千足蜈蚣.add(MonsterEntities.CRAWLTIPEDE, builder -> builder.order(49400).rarity(3).background(SOLAR_PILLAR).filters(FilterEntry.SOLAR_PILLAR))
                // 火滚怪.add(MonsterEntities.SROLLER, builder -> builder.order(49500).rarity(2).background(SOLAR_PILLAR).filters(FilterEntry.SOLAR_PILLAR))
                // 流星火怪.add(MonsterEntities.CORITE, builder -> builder.order(49600).rarity(2).background(SOLAR_PILLAR).filters(FilterEntry.SOLAR_PILLAR))
                // 火龙怪骑士.add(MonsterEntities.DRAKOMIRE_RIDER, builder -> builder.order(49700).rarity(2).background(SOLAR_PILLAR).filters(FilterEntry.SOLAR_PILLAR))
                // 异星幼虫.add(MonsterEntities.ALIEN_LARVA, builder -> builder.order(49800).rarity(2).background(VORTEX_PILLAR).filters(FilterEntry.VORTEX_PILLAR))
                // 异星黄蜂.add(MonsterEntities.ALIEN_HORNET, builder -> builder.order(49900).rarity(2).background(VORTEX_PILLAR).filters(FilterEntry.VORTEX_PILLAR))
                // 星旋怪.add(MonsterEntities.VORTEXIAN, builder -> builder.order(50000).rarity(2).background(VORTEX_PILLAR).filters(FilterEntry.VORTEX_PILLAR))
                // 漩泥怪.add(MonsterEntities.STORM_DIVER, builder -> builder.order(50100).rarity(2).background(VORTEX_PILLAR).filters(FilterEntry.VORTEX_PILLAR))
                // 异星蜂王.add(MonsterEntities.ALIEN_QUEEN, builder -> builder.order(50200).rarity(2).background(VORTEX_PILLAR).filters(FilterEntry.VORTEX_PILLAR))
                // 观星怪.add(MonsterEntities.STARGAZER, builder -> builder.order(50300).rarity(2).background(STARDUST_PILLAR).filters(FilterEntry.STARDUST_PILLAR))
                // 闪耀炮手.add(MonsterEntities.TWINKLE_POPPER, builder -> builder.order(50400).rarity(2).background(STARDUST_PILLAR).filters(FilterEntry.STARDUST_PILLAR))
                // 银河织妖.add(MonsterEntities.MILKYWAY_WEAVER, builder -> builder.order(50500).rarity(2).background(STARDUST_PILLAR).filters(FilterEntry.STARDUST_PILLAR))
                // 星细胞.add(MonsterEntities.STAR_CELL, builder -> builder.order(50600).rarity(2).background(STARDUST_PILLAR).filters(FilterEntry.STARDUST_PILLAR))
                // 迷你星细胞.add(MonsterEntities.MINI_STAR_CELL, builder -> builder.order(50700).rarity(2).background(STARDUST_PILLAR).filters(FilterEntry.STARDUST_PILLAR))
                // 流体入侵怪.add(MonsterEntities.FLOW_INVADER, builder -> builder.order(50800).rarity(2).background(STARDUST_PILLAR).filters(FilterEntry.STARDUST_PILLAR))
                // 火星飞碟.add(MonsterEntities.MARTIAN_SAUCER, builder -> builder.order(42300).rarity(4).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 火把神.add(BossEntities.THE_TORCH_GOD, builder -> builder.order(50900).rarity(5).background(UNDERGROUND).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND, FilterEntry.CAVE))
                .add(BossEntities.EYE_OF_CTHULHU, builder -> builder.order(51000).rarity(2).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                .add(BossEntities.SERVANT_OF_CTHULHU, "entity.confluence.demon_eye", "minion", builder -> builder.order(51100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(BossEntities.KING_SLIME, builder -> builder.order(51200).rarity(2).background(SURFACE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.SURFACE))
                .add(BossEntities.EATER_OF_WORLDS, builder -> builder.order(51300).rarity(3).background(THE_CORRUPTION).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CORRUPTION))
                .add(BossEntities.BRAIN_OF_CTHULHU, builder -> builder.order(51400).rarity(3).background(THE_CRIMSON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CRIMSON))
                .add(MonsterEntities.VISUAL_NEURON, builder -> builder.order(51500).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON))
                .add(BossEntities.DEERCLOPS, builder -> builder.order(51600).rarity(3).background(SNOW).filters(FilterEntry.BOSS_ENEMY, FilterEntry.SNOW))
                .add(BossEntities.SKELETRON, builder -> builder.order(51700).rarity(3).background(THE_DUNGEON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_DUNGEON))
                .add(BossEntities.QUEEN_BEE, builder -> builder.order(51800).rarity(3).background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE))
                .add(BossEntities.WALL_OF_FLESH, builder -> builder.order(51900).rarity(4).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER))
                .add(MonsterEntities.LEECH, builder -> builder.order(52000).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.THE_HUNGRY, builder -> builder.order(52100).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                // 饿鬼 （飞行）
                .add(MonsterEntities.THE_HUNGRY, "entity.confluence.the_hungry", "flying", builder -> builder.order(52200).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER).entityNbt(tag -> tag.putBoolean("Free", true)))
                // 史莱姆皇后.add(BossEntities.QUEEN_SLIME, builder -> builder.order(52300).rarity(4).background(THE_HALLOW).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_HALLOW))
                // 水晶史莱姆.add(BossEntities.CRYSTAL_SLIME, builder -> builder.order(52400).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                // 弹力史莱姆.add(BossEntities.BOUNCY_SLIME, builder -> builder.order(52500).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                // 飞翔史莱姆.add(BossEntities.HEAVENLY_SLIME, builder -> builder.order(52600).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(BossEntities.RETINAZER, builder -> builder.order(52700).rarity(4).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                .add(BossEntities.SPAZMATISM, builder -> builder.order(52800).rarity(4).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                // 魔焰眼
                // 毁灭者
                .add(BossEntities.THE_DESTROYER, builder -> builder.order(52900).rarity(4).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                // 探测怪
                .add(BossEntities.THE_DESTROYER_PROBE, builder -> builder.order(53000).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(BossEntities.SKELETRON_PRIME, builder -> builder.order(53100).rarity(4).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                .add(BossEntities.PLANTERA, builder -> builder.order(53200).rarity(4).background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE))
                // 光之女皇.add(BossEntities.EMPRESS_OF_LIGHT, builder -> builder.order(53300).rarity(5).background(THE_HALLOW_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW))
                // 石巨人.add(BossEntities.GOLEM, builder -> builder.order(53400).rarity(4).background(THE_TEMPLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_TEMPLE))
                // 猪龙鱼公爵.add(BossEntities.DUKE_FISHRON, builder -> builder.order(53500).rarity(5).background(OCEAN).filters(FilterEntry.BOSS_ENEMY, FilterEntry.OCEAN))
                // 鲨鱼龙.add(MonsterEntities.SHARKRON, builder -> builder.order(53600).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                // 拜月教邪教徒
                .add(BossEntities.LUNATIC_CULTIST, builder -> builder.order(53700).rarity(4).background(THE_DUNGEON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_DUNGEON))
                // 拜月教忠教徒.add(MonsterEntities.LUNATIC_DEVOTEE, builder -> builder.order(53800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 蓝邪教徒弓箭手.add(MonsterEntities.BLUE_CULTIST_ARCHER, builder -> builder.order(53900).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 远古幻影妖.add(MonsterEntities.ANCIENT_VISION, builder -> builder.order(54000).rarity(3).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 幻影龙
                .add(BossEntities.PHANTASM_DRAGON, builder -> builder.order(54100).rarity(3).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 星云柱.add(BossEntities.NEBULA_PILLAR, builder -> builder.order(54200).rarity(4).background(NEBULA_PILLAR).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NEBULA_PILLAR))
                // 日耀柱.add(BossEntities.SOLAR_PILLAR, builder -> builder.order(54300).rarity(4).background(SOLAR_PILLAR).filters(FilterEntry.BOSS_ENEMY, FilterEntry.SOLAR_PILLAR))
                // 星旋柱.add(BossEntities.VORTEX_PILLAR, builder -> builder.order(54400).rarity(4).background(VORTEX_PILLAR).filters(FilterEntry.BOSS_ENEMY, FilterEntry.VORTEX_PILLAR))
                // 星尘柱.add(BossEntities.STARDUST_PILLAR, builder -> builder.order(54500).rarity(4).background(STARDUST_PILLAR).filters(FilterEntry.BOSS_ENEMY, FilterEntry.STARDUST_PILLAR))
                // 月亮领主.add(BossEntities.MOON_LORD, builder -> builder.order(54600).rarity(5).background(SURFACE).filters(FilterEntry.BOSS_ENEMY))
                // MC原版生物 从60000开始  和泰拉生物重合归上面的泰拉生物编号
                .add(EntityType.ALLAY, builder -> builder.order(60000).rarity(4).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.BAT, builder -> builder.order(60200).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.CAMEL, builder -> builder.order(60300).rarity(2).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                .add(EntityType.CHICKEN, builder -> builder.order(60400).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(CritterEntities.CLUCKSHROOM, builder -> builder.order(60401).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(CritterEntities.CLUCKSHROOM, "entity.confluence.brown_cluckshroom", "brown", builder -> builder.order(60402).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE).entityNbt(tag -> tag.putBoolean("Brown", true)))
                .add(CritterEntities.GLOWING_CLUCKSHROOM, builder -> builder.order(60403).rarity(3).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(EntityType.COD, builder -> builder.order(60500).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME))
                .add(EntityType.COW, builder -> builder.order(60600).rarity(1).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.DONKEY, builder -> builder.order(60700).rarity(1).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.GLOW_SQUID, builder -> builder.order(60800).rarity(3).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.HORSE, builder -> builder.order(60900).rarity(1).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.MOOSHROOM, builder -> builder.order(61000).rarity(4).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(CritterEntities.GLOWING_MOOSHROOM, builder -> builder.order(61001).rarity(4).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(EntityType.MULE, builder -> builder.order(61100).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.PIG, builder -> builder.order(61200).rarity(1).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.SALMON, builder -> builder.order(61300).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME))
                .add(EntityType.SHEEP, builder -> builder.order(61400).rarity(1).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(CritterEntities.CLOUD_SHEEP, builder -> builder.order(61401).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.SKELETON_HORSE, builder -> builder.order(61500).rarity(4).background(SURFACE_NIGHTTIME_RAIN).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME, FilterEntry.RAIN))
                .add(EntityType.SNIFFER, builder -> builder.order(61600).rarity(4).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.SQUID, builder -> builder.order(61700).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME))
                .add(EntityType.STRIDER, builder -> builder.order(61800).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.TADPOLE, builder -> builder.order(61900).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(EntityType.TROPICAL_FISH, builder -> builder.order(62000).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME))
                .add(EntityType.WANDERING_TRADER, builder -> builder.order(62100).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.PUFFERFISH, builder -> builder.order(62200).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME))
                .add(EntityType.GOAT, builder -> builder.order(62300).rarity(2).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.VILLAGER, builder -> builder.order(62400).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.AXOLOTL, builder -> builder.order(62500).rarity(3).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(EntityType.CAT, builder -> builder.order(62600).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.OCELOT, builder -> builder.order(62700).rarity(4).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                .add(EntityType.SNOW_GOLEM, builder -> builder.order(62800).rarity(2).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.BEE, builder -> builder.order(62900).rarity(1).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.FOX, builder -> builder.order(63000).rarity(2).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.IRON_GOLEM, builder -> builder.order(63100).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.LLAMA, builder -> builder.order(63200).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.PANDA, builder -> builder.order(63300).rarity(5).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                .add(EntityType.POLAR_BEAR, builder -> builder.order(63400).rarity(3).background(SNOW).filters(FilterEntry.OCEAN, FilterEntry.SNOW, FilterEntry.DAYTIME))
                .add(EntityType.TRADER_LLAMA, builder -> builder.order(63500).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.WOLF, builder -> builder.order(63600).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.BLAZE, builder -> builder.order(63700).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.CREEPER, builder -> builder.order(64000).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .add(EntityType.ELDER_GUARDIAN, builder -> builder.order(64100).rarity(5).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME))
                .add(EntityType.ENDERMITE, builder -> builder.order(64200).rarity(2).background(SURFACE).filters(surfaceNighttime))
                .add(EntityType.EVOKER, builder -> builder.order(64300).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.GHAST, builder -> builder.order(64400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.GUARDIAN, builder -> builder.order(64500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.DAYTIME))
                .add(EntityType.HOGLIN, builder -> builder.order(64600).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.HUSK, builder -> builder.order(64700).rarity(1).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .add(EntityType.MAGMA_CUBE, builder -> builder.order(64800).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.PHANTOM, builder -> builder.order(64900).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .add(EntityType.PIGLIN_BRUTE, builder -> builder.order(65000).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.PILLAGER, builder -> builder.order(65100).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.RAVAGER, builder -> builder.order(65200).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.SHULKER, builder -> builder.order(65300).rarity(2).background(SURFACE).filters(surfaceNighttime))
                .add(EntityType.SILVERFISH, builder -> builder.order(65400).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.SLIME, builder -> builder.order(65500).rarity(3).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.STRAY, builder -> builder.order(65600).rarity(2).background(SNOW_MOON).filters(FilterEntry.SNOW, FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .add(EntityType.VEX, builder -> builder.order(65700).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.VINDICATOR, builder -> builder.order(65700).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.WARDEN, builder -> builder.order(65800).rarity(3).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.WITCH, builder -> builder.order(65900).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.WITHER_SKELETON, builder -> builder.order(66000).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.ZOGLIN, builder -> builder.order(66100).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.ZOMBIE_VILLAGER, builder -> builder.order(66200).rarity(2).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .add(EntityType.DROWNED, builder -> builder.order(66300).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN, FilterEntry.NIGHTTIME))
                .add(EntityType.ENDERMAN, builder -> builder.order(66400).rarity(3).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .add(EntityType.PIGLIN, builder -> builder.order(66500).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.SPIDER, builder -> builder.order(66600).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .add(EntityType.CAVE_SPIDER, builder -> builder.order(66700).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.ZOMBIFIED_PIGLIN, builder -> builder.order(66800).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.ENDER_DRAGON, builder -> builder.order(66900).rarity(5).background(THE_END).filters(FilterEntry.BOSS_ENEMY))
                .add(EntityType.WITHER, builder -> builder.order(67000).rarity(2).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY))
                // 原创生物 从70000开始
                .add(MonsterEntities.DECAYEDER, builder -> builder.order(70000).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME, FilterEntry.THE_CORRUPTION))
                .add(MonsterEntities.BLOODY_SPORE, builder -> builder.order(70100).rarity(2).background(THE_CRIMSON).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME, FilterEntry.THE_CRIMSON))
                .add(BossEntities.HILL_OF_FLESH, builder -> builder.order(70200).rarity(4).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER))
                .add(MonsterEntities.WITHER_BONE_SERPENT, builder -> builder.order(70300).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .map);
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

        public <E extends Enum<E> & IVariant> Builder numberedVariant(Supplier<? extends EntityType<?>> type, int displayVariant, E entityVariant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return numberedVariant(type, type.get().getDescriptionId(), displayVariant, entityVariant, consumer);
        }

        public <E extends Enum<E> & IVariant> Builder numberedVariant(Supplier<? extends EntityType<?>> type, String typeKey, int displayVariant, E entityVariant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(type, typeKey, Integer.toString(displayVariant), consumer.andThen(builder -> builder.entityNbt(entityVariant::serialize)));
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
