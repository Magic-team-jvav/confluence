package org.confluence.mod.common.data.gen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.confluence.mod.client.handler.bestiary.ClientBestiary.Entry.*;

public class ModClientBestiaryEntryProvider extends AbstractRecipeProvider {
    private final PackOutput.PathProvider pathProvider;

    public ModClientBestiaryEntryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "");
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider provider) {
        FilterEntry[] surfaceDaytime = {FilterEntry.SURFACE, FilterEntry.DAYTIME};
        FilterEntry[] surfaceNighttime = {FilterEntry.SURFACE, FilterEntry.NIGHTTIME};
        recipe(Codec.unboundedMap(Codec.STRING, ClientBestiary.Entry.CODEC), pathProvider().json(Confluence.asResource("bestiary"))).addRecipe(new Builder()
                .add(NpcEntities.GUIDE, builder -> builder.order(100).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.MERCHANT, builder -> builder.order(200).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.NURSE, builder -> builder.order(300).rarity(1).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(NpcEntities.DEMOLITIONIST, builder -> builder.order(400).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(NpcEntities.ANGLER, builder -> builder.order(500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN).entityNbt(nbt -> nbt.putBoolean(AnglerNPC.WAKE_UP_KEY, true)))
                .add(NpcEntities.DRYAD, builder -> builder.order(600).rarity(3).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(NpcEntities.ARMS_DEALER, builder -> builder.order(700).rarity(1).background(DESERT).filters(FilterEntry.DESERT))
                .add(NpcEntities.DYE_TRADER, builder -> builder.order(800).rarity(2).background(DESERT).filters(FilterEntry.DESERT))
                .add(NpcEntities.PAINTER, builder -> builder.order(900).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(NpcEntities.STYLIST, builder -> builder.order(1000).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                // 发型师
                .add(NpcEntities.ZOOLOGIST, builder -> builder.order(1100).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.GOLFER, builder -> builder.order(1300).rarity(2).background(SURFACE).filters(FilterEntry.SURFACE))
                // 酒馆老板
                // 高尔夫球手
                .add(NpcEntities.GOBLIN_TINKERER, builder -> builder.order(1400).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(NpcEntities.WITCH_DOCTOR, builder -> builder.order(1500).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(NpcEntities.MECHANIC, builder -> builder.order(1600).rarity(2).background(SNOW).filters(FilterEntry.SNOW))
                .add(NpcEntities.CLOTHIER, builder -> builder.order(1700).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(NpcEntities.WIZARD, builder -> builder.order(1800).rarity(3).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(NpcEntities.STEAMPUNKER, builder -> builder.order(1900).rarity(3).background(DESERT).filters(FilterEntry.DESERT))
                // 蒸汽朋克人
                // 海盗
                .add(NpcEntities.TRUFFLE, builder -> builder.order(2100).rarity(5).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                .add(NpcEntities.TAX_COLLECTOR, builder -> builder.order(2200).rarity(5).background(SNOW).filters(FilterEntry.SNOW))
                .add(NpcEntities.CYBORG, builder -> builder.order(2300).rarity(3).background(SNOW).filters(FilterEntry.SNOW))
                // 税收官
                // 机器侠
                .add(NpcEntities.PARTY_GIRL, builder -> builder.order(2400).rarity(4).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(NpcEntities.NERDY_SLIME, builder -> builder.order(3000).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.COOL_SLIME, builder -> builder.order(3100).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.ELDER_SLIME, builder -> builder.order(3200).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.CLUMSY_SLIME, builder -> builder.order(3300).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.DIVA_SLIME, builder -> builder.order(3400).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.SURLY_SLIME, builder -> builder.order(3500).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.MYSTIC_SLIME, builder -> builder.order(3600).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.SQUIRE_SLIME, builder -> builder.order(3700).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                // 公主
                // 圣诞老人
                // 宠物
                .add(NpcEntities.TRAVELING_MERCHANT, builder -> builder.order(3800).rarity(3).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(NpcEntities.SKELETON_MERCHANT, builder -> builder.order(3900).rarity(4).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 骷髅商人
                .add(NpcEntities.OLD_MAN, builder -> builder.order(4000).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(CritterEntities.MYSTIC_FROG, builder -> builder.order(4100).rarity(4).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 神秘青蛙
                .add(CritterEntities.BUNNY, builder -> builder.order(4200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.RABBIT, builder -> builder.order(4210).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                // 兔兔 （戴帽子）
                .variant(CritterEntities.BUNNY, Bunny.Variant.PARTY, builder -> builder.order(4300).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.PARTY, FilterEntry.DAYTIME))
                .add(CritterEntities.EXPLOSIVE_BUNNY, builder -> builder.order(4250).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                // 兔兔 （史莱姆）
                .variant(CritterEntities.BUNNY, Bunny.Variant.SLIMED, builder -> builder.order(4500).rarity(4).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.HALLOWEEN, FilterEntry.DAYTIME))
                // 兔兔 （圣诞节）
                .variant(CritterEntities.BUNNY, Bunny.Variant.XMAS, builder -> builder.order(4600).rarity(4).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.CHRISTMAS, FilterEntry.DAYTIME))
                .numberedVariant(CritterEntities.JEWEL_BUNNY, 4, Bunny.Variant.GOLD, builder -> builder.order(4700).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.BIRD, builder -> builder.order(4800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.BLUE_JAY, builder -> builder.order(4900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.CARDINAL, builder -> builder.order(5000).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                // 绯红金刚鹦鹉
                .add(CritterEntities.GOLDFISH, builder -> builder.order(5700).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .add(EntityType.PARROT, builder -> builder.order(5200).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE,FilterEntry.DAYTIME))
                // 巨嘴鸟
                // 黄玄凤鹦鹉
                // 灰玄凤鹦鹉
                // 金鸟
                // 金鱼
                // 金金鱼
                .numberedVariant(CritterEntities.SQUIRREL, 0, Squirrel.Variant.NORMAL, builder -> builder.order(5900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.RED_SQUIRREL, "entity.confluence.squirrel", 1, Squirrel.Variant.RED, builder -> builder.order(6000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.JEWEL_SQUIRREL, 1, Squirrel.Variant.GOLD, builder -> builder.order(6100).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                // 老鼠
                // 金老鼠
                .add(EntityType.FROG, builder -> builder.order(6400).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 金青蛙
                .numberedVariant(CritterEntities.GRASSHOPPER, 1, Grasshopper.Variant.GREEN, builder -> builder.order(6600).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .numberedVariant(CritterEntities.GRASSHOPPER, 0, Grasshopper.Variant.GOLD, builder -> builder.order(6700).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))
                .numberedVariant(CritterEntities.BUTTERFLY, 1, Butterfly.Variant.JULIA, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 2, Butterfly.Variant.MONARCH, builder -> builder.order(6801).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 3, Butterfly.Variant.PURPLE_EMPEROR, builder -> builder.order(6802).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 4, Butterfly.Variant.RED_ADMIRAL, builder -> builder.order(6803).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 5, Butterfly.Variant.SULPHUR, builder -> builder.order(6804).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 6, Butterfly.Variant.TREE_NYMPH, builder -> builder.order(6805).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 7, Butterfly.Variant.ULYSSES, builder -> builder.order(6806).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 8, Butterfly.Variant.ZEBRA_SWALLOWTAIL, builder -> builder.order(6807).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.BUTTERFLY, 0, Butterfly.Variant.GOLD, builder -> builder.order(6900).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.WORM, 2, Worm.Variant.NORMAL, builder -> builder.order(7000).rarity(1).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                .numberedVariant(CritterEntities.WORM, 1, Worm.Variant.GOLD, builder -> builder.order(7100).rarity(5).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                .numberedVariant(CritterEntities.DRAGONFLY, 0, Dragonfly.Variant.BLACK, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 1, Dragonfly.Variant.BLUE, builder -> builder.order(7201).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 3, Dragonfly.Variant.GREEN, builder -> builder.order(7202).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 4, Dragonfly.Variant.ORANGE, builder -> builder.order(7203).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 5, Dragonfly.Variant.RED, builder -> builder.order(7204).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 6, Dragonfly.Variant.YELLOW, builder -> builder.order(7205).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DRAGONFLY, 2, Dragonfly.Variant.GOLD, builder -> builder.order(7300).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                // 海马
                // 金海马
                // 水黾
                // 金水黾
                .numberedVariant(CritterEntities.LADYBUG, 1, Ladybug.Variant.RED, builder -> builder.order(7800).rarity(3).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .numberedVariant(CritterEntities.LADYBUG, 0, Ladybug.Variant.GOLD, builder -> builder.order(7900).rarity(5).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .add(CritterEntities.STINKBUG, builder -> builder.order(8000).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                // 臭虫
                .add(CritterEntities.FEALING, builder -> builder.order(8100).rarity(5).background(CAVE).filters(FilterEntry.CAVE))
                .numberedVariant(CritterEntities.DUCK, 0, Duck.Variant.MALLARD, builder -> builder.order(8200).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .numberedVariant(CritterEntities.DUCK, 1, Duck.Variant.COMMON, builder -> builder.order(8300).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.FIREFLY, builder -> builder.order(8600).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                // 龟
                // 猫头鹰
                // 萤火虫
                .numberedVariant(CritterEntities.WORM, 0, Worm.Variant.NIGHTCRAWLER, builder -> builder.order(8700).rarity(5).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .numberedVariant(CritterEntities.FAIRY, 0, Fairy.Variant.PINK, builder -> builder.order(8800).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .numberedVariant(CritterEntities.FAIRY, 1, Fairy.Variant.GREEN, builder -> builder.order(8900).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .numberedVariant(CritterEntities.FAIRY, 2, Fairy.Variant.BLUE, builder -> builder.order(9000).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                // 大鼠
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
                // 松露虫
                // 企鹅
                // 企鹅（黑）
                .numberedVariant(CritterEntities.SCORPION, 1, Scorpion.Variant.NORMAL, builder -> builder.order(11100).rarity(1).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                .numberedVariant(CritterEntities.SCORPION, 0, Scorpion.Variant.BLACK, builder -> builder.order(11200).rarity(2).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                // 䴙䴘
                // 鳉鱼
                // 海鸥
                .add(EntityType.TURTLE, builder -> builder.order(11600).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(EntityType.DOLPHIN, builder -> builder.order(11700).rarity(3).background(OCEAN).filters(FilterEntry.OCEAN))
                // 丛林龟
                .add(CritterEntities.GRUBBY, builder -> builder.order(11900).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(CritterEntities.BUGGY, builder -> builder.order(12200).rarity(3).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(CritterEntities.SLUGGY, builder -> builder.order(12000).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 蚜虫
                // 熔岩萤火虫
                .add(CritterEntities.HELL_BUTTERFLY, builder -> builder.order(12300).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(CritterEntities.LIGHTNING_BUG, builder -> builder.order(12600).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW))
                .add(CritterEntities.MAGMA_SNAIL, builder -> builder.order(12400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                // 荧火虫
                .add(CritterEntities.PRISMATIC_LACEWING, builder -> builder.order(12700).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW)) //神圣夜晚
                .add(CritterEntities.PRISMATIC_LACEWING, builder -> builder.order(12600).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.THE_HALLOW, FilterEntry.NIGHTTIME)) //神圣夜晚
                .add(MonsterEntities.GNOME, builder -> builder.order(12900).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE))
                .add(CritterEntities.GLOWING_SNAIL, builder -> builder.order(12700).rarity(3).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                // 侏儒
                .add(MonsterEntities.GOBLIN_SCOUT, builder -> builder.order(12900).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE))
                .add(MonsterEntities.GREEN_SLIME, builder -> builder.order(13000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.BLUE_SLIME, builder -> builder.order(13100).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.PURPLE_SLIME, builder -> builder.order(13200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.PINK_SLIME, builder -> builder.order(13300).rarity(4).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(MonsterEntities.SWEET_SLIME, builder -> builder.order(13402).rarity(3).background(THE_JUNGLE_SUN).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.GOLDEN_SLIME, builder -> builder.order(13301).rarity(5).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(MonsterEntities.SWAMP_SLIME, builder -> builder.order(13303).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.GREEN_DUMPLING_SLIME, builder -> builder.order(13304).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.SPIKED_SLIME, builder -> builder.order(13305).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(MonsterEntities.WINDY_BALLOON, builder -> builder.order(13500).rarity(2).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .add(MonsterEntities.ANGRY_DANDELION, builder -> builder.order(13600).rarity(2).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .add(MonsterEntities.TROPIC_SLIME, builder -> builder.order(13306).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))
                // 大风气球怪
                // 愤怒蒲公英
                // 雨伞史莱姆
                .add(MonsterEntities.ANGRY_NIMBUS, builder -> builder.order(13900).rarity(3).background(SURFACE_RAIN).filters(FilterEntry.RAIN, FilterEntry.SURFACE))
                .add(MonsterEntities.FLYING_FISH, builder -> builder.order(13700).rarity(2).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                // 愤怒雨云怪
                .demonEyeVariant(DemonEye.Variant.DILATED, builder -> builder.order(13900).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.DILATED_SMALL, builder -> builder.order(13910).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.SLEEPY, builder -> builder.order(14000).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.SLEEPY_BIG, builder -> builder.order(14010).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.PURPLE, builder -> builder.order(14100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.PURPLE_BIG, builder -> builder.order(14110).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.NORMAL, builder -> builder.order(14200).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.NORMAL_BIG, builder -> builder.order(14210).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.GREEN, builder -> builder.order(14300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.GREEN_SMALL, builder -> builder.order(14310).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.CATARACT, builder -> builder.order(14400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.CATARACT_BIG, builder -> builder.order(14410).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.SPACESHIP, builder -> builder.order(14411).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEye.Variant.OWL, builder -> builder.order(14412).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 游荡眼球怪
                // 僵尸 （女性）
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "slime", builder -> builder.order(14800).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.SLIMED.getSerializedName())))
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "bald", builder -> builder.order(14900).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.BALD.getSerializedName())))
                .add(EntityType.ZOMBIE, "slime", builder -> builder.order(14700).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 僵尸 （光头）
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "twiggy", builder -> builder.order(15100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.TWIGGY.getSerializedName())))
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "swamp", builder -> builder.order(15300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.SWAMP.getSerializedName())))
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "pincushion", builder -> builder.order(15400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.PINCUSHION.getSerializedName())))
                .add(EntityType.ZOMBIE, builder -> builder.order(14900).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 僵尸 （纤瘦）
                // 僵尸 （火把）
                // 僵尸 （沼泽）
                // 僵尸 （中箭）
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "raincoat", builder -> builder.order(15500).rarity(2).background(SURFACE_MOON).filters(FilterEntry.RAIN, FilterEntry.NIGHTTIME).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.RAINCOAT.getSerializedName())))
                .mobArmorItems(EntityType.ZOMBIE, "raincoat", List.of(ItemStack.EMPTY, ItemStack.EMPTY, ArmorItems.RAINCOAT.toStack(), ArmorItems.RAIN_CAP.toStack()), provider, builder -> builder.order(15400).rarity(2).background(SURFACE_NIGHTTIME_RAIN).filters(FilterEntry.NIGHTTIME, FilterEntry.RAIN))
                .add(MonsterEntities.WEREWOLF, builder -> builder.order(15700).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(MonsterEntities.POSSESS_ARMOR, builder -> builder.order(15500).rarity(2).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 狼人
                .add(CritterEntities.HOSTILE_BUNNY, "entity.confluence.corrupt_bunny", "corrupt", builder -> builder.order(15900).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON).entityNbt(tag -> tag.putString("Variant", "corrupt")))
                .add(MonsterEntities.CORRUPT_PENGUIN, builder -> builder.order(16000).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(CritterEntities.HOSTILE_BUNNY, "entity.confluence.vicious_bunny", "vicious", builder -> builder.order(16100).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON).entityNbt(tag -> tag.putString("Variant", "vicious")))
                .add(MonsterEntities.VICIOUS_PENGUIN, builder -> builder.order(16200).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.WRAITH, builder -> builder.order(15700).rarity(2).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 腐化兔兔
                // 腐化企鹅
                // 毒兔兔
                // 毒企鹅
                .add(MonsterEntities.THE_GROOM, builder -> builder.order(16400).rarity(5).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.THE_BRIDE, builder -> builder.order(16500).rarity(5).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.ZOMBIE_MERMAN, builder -> builder.order(16600).rarity(4).background(BLOOD_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.CORRUPT_GOLDFISH, builder -> builder.order(17000).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.VICIOUS_GOLDFISH, builder -> builder.order(17100).rarity(3).background(BLOOD_MOON).filters(FilterEntry.BLOOD_MOON))
                .add(MonsterEntities.BLOOD_ZOMBIE, builder -> builder.order(16200).rarity(1).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))
                // 僵尸新郎
                // 僵尸新娘
                // 僵尸人鱼
                // 小丑
                // 血乌贼
                // 血鳗鱼
                // 腐化金鱼
                // 毒金鱼
                .add(MonsterEntities.DRIPPLER, builder -> builder.order(17100).rarity(2).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))
                // 嗒嗒牙齿炸弹
                .add(MonsterEntities.WANDERING_EYE_FISH, builder -> builder.order(17300).rarity(4).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))
                // 血浆哥布林鲨鱼
                // 恐惧鹦鹉螺
                // 弹跳杰克南瓜灯
                // 蝇蛆僵尸
                // 乌鸦
                .add(MonsterEntities.GHOST, builder -> builder.order(17900).rarity(3).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                .add(MonsterEntities.RED_SLIME, builder -> builder.order(18000).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(MonsterEntities.YELLOW_SLIME, builder -> builder.order(18100).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 毒泥
                .add(MonsterEntities.DIGGER, builder -> builder.order(18700).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(MonsterEntities.GIANT_WORM, builder -> builder.order(18300).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 挖掘怪
                .add(MonsterEntities.BABY_SLIME, builder -> cave(builder, 18500, 1))
                .add(MonsterEntities.BLACK_SLIME, builder -> cave(builder, 18600, 1))
                // 微光史莱姆
                .add(MonsterEntities.MOTHER_SLIME, builder -> cave(builder, 18800, 2))
                // 胭脂虫
                // 骷髅 （畸形）
                .add(EntityType.SKELETON, builder -> cave(builder, 19100, 1))
                // 骷髅 （头痛）
                // 骷髅 （无裤）
                .add(MonsterEntities.CRAWDAD, builder -> builder.order(19500).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .mobArmorItems(EntityType.SKELETON, "entity.confluence.undead_miner", "", List.of(ArmorItems.MINING_BOOTS.toStack(), ArmorItems.MINING_LEGGINGS.toStack(), ArmorItems.MINING_CHESTPLATE.toStack(), ArmorItems.MINING_HELMET.toStack()), provider, builder -> builder.order(19600).rarity(3).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                // 骷髅弓箭手
                .add(MonsterEntities.ARMORED_SKELETON, builder -> builder.order(20200).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.ROCK_GOLEM, builder -> builder.order(20300).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.TIM, builder -> builder.order(20400).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.RUNE_WIZARD, builder -> builder.order(20500).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.NYMPH, builder -> builder.order(19800).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                // 装甲骷髅
                // 岩石巨人
                // 蒂姆
                // 符文巫师
                .add(MonsterEntities.GIANT_BAT, builder -> builder.order(20700).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(MonsterEntities.CAVE_BAT, builder -> cave(builder, 20300, 1))
                // 巨型蝙蝠
                .add(MonsterEntities.BLUE_JELLYFISH, builder -> cave(builder, 20500, 1))
                .add(MonsterEntities.GREEN_JELLYFISH, builder -> cave(builder, 20600, 2))
                .add(MonsterEntities.WOODEN_MIMIC, builder -> builder.order(20700).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.GOLDEN_MIMIC, builder -> builder.order(20701).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                .add(MonsterEntities.SHADOW_MIMIC, builder -> builder.order(20701).rarity(5).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))
                .add(MonsterEntities.GRANITE_GOLEM, builder -> builder.order(21300).rarity(2).background(GRANITE).filters(FilterEntry.GRANITE))
                .add(MonsterEntities.GIANT_SHELLY, builder -> cave(builder, 20800, 2))
                // 迷失女孩
                // 花岗岩巨人
                .add(MonsterEntities.HOPLITE, builder -> builder.order(21500).rarity(2).background(MARBLE).filters(FilterEntry.MARBLE))
                .add(MonsterEntities.GRANITE_ELEMENTAL, builder -> builder.order(21100).rarity(2).background(GRANITE).filters(FilterEntry.GRANITE))
                // 装甲步兵
                // 蛇发女妖
                .add(MonsterEntities.SPORE_SKELETON, builder -> builder.order(21400).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.WALL_CREEPER, builder -> builder.order(21900).rarity(2).background(SPIDER_NEST).filters(FilterEntry.SPIDER_NEST))
                .add(MonsterEntities.BLACK_RECLUSE, builder -> builder.order(22000).rarity(2).background(SPIDER_NEST).filters(FilterEntry.SPIDER_NEST))
                .add(MonsterEntities.SPORE_BAT, builder -> builder.order(21500).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                // 爬墙蜘蛛
                // 黑隐士
                .add(MonsterEntities.ICE_SLIME, builder -> builder.order(21800).rarity(1).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.DAYTIME))
                .add(MonsterEntities.ZOMBIE, "entity.minecraft.zombie", "frozen", builder -> builder.order(22200).rarity(2).background(SNOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.SNOW).entityNbt(tag -> tag.putString("Variant", Zombie.Variant.ESKIMO.getSerializedName())))
                .add(MonsterEntities.ICE_GOLEM, builder -> builder.order(22300).rarity(5).background(SNOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.RAIN, FilterEntry.SNOW))
                .mobArmorItems(EntityType.ZOMBIE, "frozen", List.of(ArmorItems.INSULATED_SHOES.toStack(), ArmorItems.INSULATED_PANTS.toStack(), ArmorItems.SNOW_SUITS.toStack(), ArmorItems.SNOW_CAPS.toStack()), provider, builder -> builder.order(21900).rarity(2).background(SNOW_MOON).filters(FilterEntry.SNOW, FilterEntry.NIGHTTIME))
                .mobArmorItems(EntityType.ZOMBIE,  "frozen.pink", List.of(ArmorItems.PINK_INSULATED_SHOES.toStack(), ArmorItems.PINK_INSULATED_PANTS.toStack(), ArmorItems.PINK_SNOW_SUITS.toStack(), ArmorItems.PINK_SNOW_CAPS.toStack()), provider, builder -> builder.order(21910).rarity(5).background(SNOW_MOON).filters(FilterEntry.SNOW, FilterEntry.NIGHTTIME))
                // 冰雪巨人
                // 狼
                .add(MonsterEntities.SPIKED_ICE_SLIME, builder -> builder.order(22200).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                // 青壳虫
                .add(MonsterEntities.UNDEAD_VIKING, builder -> builder.order(22400).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.ARMORED_VIKING, builder -> builder.order(22900).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.ICY_MERMAN, builder -> builder.order(23000).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.SNOW_FLINX, builder -> builder.order(22500).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                // 装甲维京海盗
                // 冰雪人鱼
                .add(MonsterEntities.ICE_ELEMENTAL, builder -> builder.order(23200).rarity(2).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.ICE))
                .add(MonsterEntities.ICE_BAT, builder -> builder.order(22800).rarity(1).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                // 冰雪精
                .add(MonsterEntities.ICE_TORTOISE, builder -> builder.order(23400).rarity(2).background(UNDERGROUND_SNOW).filters(FilterEntry.ICE))
                .add(MonsterEntities.ICE_MIMIC, builder -> builder.order(23000).rarity(5).background(UNDERGROUND_SNOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.ICE))
                // 冰雪陆龟
                // 秃鹰
                .add(MonsterEntities.ANTLION_LARVA, builder -> builder.order(23700).rarity(1).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.DESERT_SLIME, builder -> builder.order(23300).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 蚁狮幼虫
                // 巨型蚁狮马
                .add(MonsterEntities.MUMMY, builder -> builder.order(23600).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.BASILISK, builder -> builder.order(24100).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.GHOUL, builder -> builder.order(23700).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 蛇蜥怪
                .add(MonsterEntities.ANTLION, builder -> builder.order(24300).rarity(1).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.TOMB_CRAWLER, builder -> builder.order(23900).rarity(1).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 蚁狮
                .add(MonsterEntities.SAND_POACHER, builder -> builder.order(24100).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                .add(MonsterEntities.ANTLION_CHARGER, builder -> builder.order(24600).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.UNDERGROUND_DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.ANGRY_TUMBLER, builder -> builder.order(24800).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.GIANT_ANTLION_SWARMER, builder -> builder.order(24200).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 蚁狮马
                // 沙虫
                // 愤怒翻滚怪
                .add(MonsterEntities.SAND_SHARK, builder -> builder.order(25100).rarity(2).background(DESERT).filters(FilterEntry.DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.ANTLION_SWARMER, builder -> builder.order(24600).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 沙尘精
                // 沙鲨
                .add(CritterEntities.CRAB, builder -> builder.order(24900).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                // 海蜗牛
                .add(MonsterEntities.SHARK, builder -> builder.order(25100).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                // 乌贼
                .add(MonsterEntities.PINK_JELLYFISH, builder -> builder.order(25300).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(MonsterEntities.JUNGLE_SLIME, builder -> builder.order(25400).rarity(1).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                .add(MonsterEntities.GIANT_FLYING_FOX, builder -> builder.order(26000).rarity(2).background(THE_JUNGLE_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_JUNGLE))
                .add(MonsterEntities.SNATCHER, builder -> builder.order(25500).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 巨型飞狐
                .add(MonsterEntities.DERPLING, builder -> builder.order(25700).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(MonsterEntities.DOCTOR_BONES, builder -> builder.order(26400).rarity(5).background(THE_JUNGLE_MOON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE, FilterEntry.NIGHTTIME))
                .add(MonsterEntities.SPIKED_JUNGLE_SLIME, builder -> builder.order(25800).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 紫胶虫
                // 骷髅博士
                // 蜜蜂
                // 蜜蜂 （较大）
                // 黄蜂 （毒刺）
                // 黄蜂 （尖刺）
                .add(MonsterEntities.MOSS_HORNET, builder -> builder.order(27300).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.HORNET, builder -> builder.order(26500).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 苔藓黄蜂
                // 蛾
                .add(MonsterEntities.MAN_EATER, builder -> builder.order(27100).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 愤怒捕手
                .add(MonsterEntities.JUNGLE_BAT, builder -> builder.order(27300).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.ANGLER_FISH, builder -> builder.order(27900).rarity(2).background(CAVE).filters(FilterEntry.CAVE, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.PIRANHA, builder -> builder.order(27400).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                // 琵琶鱼
                .add(MonsterEntities.ARAPAIMA, builder -> builder.order(27600).rarity(2).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.GIANT_TORTOISE, builder -> builder.order(28100).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.JUNGLE_CREEPER, builder -> builder.order(28200).rarity(2).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(MonsterEntities.JUNGLE_MIMIC, builder -> builder.order(27601).rarity(5).background(UNDERGROUND).filters(FilterEntry.RARE_CREATURE,  FilterEntry.THE_JUNGLE,FilterEntry.UNDERGROUND_JUNGLE))
                // 巨型陆龟
                // 丛林蜘蛛
                // 流星头
                .add(MonsterEntities.METEOR_HEAD, builder -> builder.order(27900).rarity(2).background(METEOR).filters(FilterEntry.METEOR))
                .add(MonsterEntities.DUNGEON_SLIME, builder -> builder.order(28000).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.ANGER_BONES, builder -> builder.order(28100).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.SHORT_BONES, builder -> builder.order(28101).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BIG_BONES, builder -> builder.order(28102).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BIG_ANGER_BONES, builder -> builder.order(28200).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BIG_MUSCLE_ANGER_BONES, builder -> builder.order(28300).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BONE_LEE, builder -> builder.order(30400).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.PALADIN, builder -> builder.order(30500).rarity(5).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.BIG_HELMET_ANGER_BONES, builder -> builder.order(28400).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 蓝装甲骷髅 （锤矛）
                // 骷髅狙击手
                // 骷髅特警
                // 骷髅突击手
                // 地狱装甲骷髅
                // 生锈装甲骷髅 （剑无装甲）
                // 生锈装甲骷髅 （连枷）
                // 地狱装甲骷髅 （锤矛）
                // 蓝装甲骷髅
                // 生锈装甲骷髅 （剑）
                // 地狱装甲骷髅 （尖刺盾）
                // 蓝装甲骷髅 （无裤）
                // 地狱装甲骷髅 （剑）
                // 生锈装甲骷髅 （斧头）
                // 蓝装甲骷髅 （剑）
                // 骷髅李
                // 圣骑士
                .add(MonsterEntities.WATER_BOLT_MIMIC, builder -> builder.order(31400).rarity(1).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.DIABOLIST, builder -> builder.order(30800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.NECROMANCER, builder -> builder.order(31000).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.RAGGED_CASTER, builder -> builder.order(31100).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(MonsterEntities.DARK_CASTER, builder -> builder.order(30200).rarity(1).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 魔教徒
                // 魔教徒
                // 死灵法师
                // 褴褛邪教徒法师
                // 死灵法师 （装甲）
                // 褴褛邪教徒法师 （敞开外衣）
                .add(MonsterEntities.CURSED_SKULL, builder -> builder.order(30900).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 巨型诅咒骷髅头
                .add(MonsterEntities.DUNGEON_SPIRIT, builder -> builder.order(31800).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(BossEntities.DUNGEON_GUARDIAN, builder -> builder.order(31100).rarity(4).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 地牢幽魂
                .add(MonsterEntities.LAVA_SLIME, builder -> builder.order(31300).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                // 痛苦亡魂
                .add(MonsterEntities.BONE_SERPENT, builder -> builder.order(31500).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.FIRE_IMP, builder -> builder.order(31600).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.HELL_BAT, builder -> builder.order(31700).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.DEMON, builder -> builder.order(31800).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.LAVA_BAT, builder -> builder.order(32600).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.RED_DEVIL, builder -> builder.order(32700).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.VOODOO_DEMON, builder -> builder.order(31900).rarity(3).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))
                // 熔岩蝙蝠
                // 红魔鬼
                .add(MonsterEntities.WYVERN, builder -> builder.order(32200).rarity(3).background(SKY).filters(FilterEntry.SKY))
                .add(MonsterEntities.HARPY, builder -> builder.order(32300).rarity(2).background(SKY).filters(FilterEntry.SKY))
                // 火星探测器
                // 小史莱姆
                .add(MonsterEntities.CORRUPT_SLIME, builder -> builder.order(32600).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.EATER_OF_SOULS, builder -> builder.order(32700).rarity(1).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                // 腐化者
                .add(MonsterEntities.WORLD_FEEDER, builder -> builder.order(33600).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.CLINGER, builder -> builder.order(33700).rarity(2).background(UNDERGROUND_CORRUPTION).filters(FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.SLIMER, builder -> builder.order(33800).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                .add(MonsterEntities.DEVOURER, builder -> builder.order(32900).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                // 吞世怪
                // 爬藤怪
                // 恶翅史莱姆
                // 诅咒锤
                .add(MonsterEntities.BONE_BITER, builder -> builder.order(34200).rarity(2).background(CORRUPT_DESERT).filters(FilterEntry.CORRUPT_DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.CORRUPT_MIMIC, builder -> builder.order(33400).rarity(5).background(UNDERGROUND_CORRUPTION).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_CORRUPTION))
                // 猪龙 （腐化）
                // 噬骨沙鲨
                .add(MonsterEntities.DARK_MUMMY, builder -> builder.order(33700).rarity(2).background(CORRUPT_DESERT).filters(FilterEntry.CORRUPT_DESERT))
                .add(MonsterEntities.VILE_GHOUL, builder -> builder.order(33800).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.CORRUPT_DESERT))
                .add(MonsterEntities.CRIMSLIME, builder -> builder.order(33900).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.FACE_MONSTER, builder -> builder.order(34000).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.BLOOD_FEEDER, builder -> builder.order(34800).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.BLOOD_JELLY, builder -> builder.order(34900).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.CRIMERA, builder -> builder.order(34100).rarity(1).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                // 嗜血怪
                // 血水母
                // 恶心浮游怪
                // 灵液黏黏怪
                // 猩红斧
                .add(MonsterEntities.BLOOD_CRAWLER, builder -> builder.order(34700).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.HERPLING, builder -> builder.order(34800).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(MonsterEntities.FLESH_REAVER, builder -> builder.order(35700).rarity(2).background(CRIMSON_DESERT).filters(FilterEntry.CRIMSON_DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.CRIMSON_MIMIC, builder -> builder.order(34900).rarity(5).background(UNDERGROUND_CRIMSON).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_CRIMSON))
                // 猪龙 （猩红）
                // 戮血沙鲨
                .add(MonsterEntities.BLOOD_MUMMY, builder -> builder.order(35200).rarity(2).background(CRIMSON_DESERT).filters(FilterEntry.CRIMSON_DESERT))
                .add(MonsterEntities.TAINTED_GHOUL, builder -> builder.order(35300).rarity(2).background(CRIMSON_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.CRIMSON_DESERT))
                .add(MonsterEntities.DESERT_SPIRIT, builder -> builder.order(36100).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CORRUPT_CAVE_DESERT, FilterEntry.CRIMSON_CAVE_DESERT))
                .add(MonsterEntities.DARK_LAMIA, builder -> builder.order(35400).rarity(2).background(CORRUPT_CAVE_DESERT).filters(FilterEntry.CORRUPT_CAVE_DESERT, FilterEntry.CRIMSON_CAVE_DESERT))
                // 沙漠幽魂
                // 彩虹史莱姆
                .add(MonsterEntities.GASTROPOD, builder -> builder.order(36400).rarity(2).background(THE_HALLOW_MOON).filters(FilterEntry.NIGHTTIME, FilterEntry.THE_HALLOW))
                .add(MonsterEntities.UNICORN, builder -> builder.order(36500).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                .add(MonsterEntities.PIXIE, builder -> builder.order(35700).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                // 腹足怪
                // 独角兽
                .add(MonsterEntities.CHAOS_ELEMENTAL, builder -> builder.order(36700).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                .add(MonsterEntities.ILLUMINANT_BAT, builder -> builder.order(36800).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                .add(MonsterEntities.ENCHANTED_SWORD, builder -> builder.order(36900).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                .add(MonsterEntities.LUMINOUS_SLIME, builder -> builder.order(36000).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                // 混沌精
                // 夜明蝙蝠
                // 附魔剑
                .add(MonsterEntities.CRYSTAL_THRESHER, builder -> builder.order(37200).rarity(2).background(HALLOW_DESERT).filters(FilterEntry.HALLOW_DESERT, FilterEntry.SANDSTORM))
                .add(MonsterEntities.HALLOWED_MIMIC, builder -> builder.order(36400).rarity(5).background(UNDERGROUND_HALLOW).filters(FilterEntry.RARE_CREATURE, FilterEntry.UNDERGROUND_HALLOW))
                // 猪龙
                // 水晶沙鲨
                .add(MonsterEntities.LIGHT_MUMMY, builder -> builder.order(36700).rarity(2).background(HALLOW_DESERT).filters(FilterEntry.RARE_CREATURE, FilterEntry.HALLOW_DESERT))
                .add(MonsterEntities.DREAMER_GHOUL, builder -> builder.order(36800).rarity(2).background(HALLOW_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.HALLOW_DESERT))
                .add(MonsterEntities.LIGHT_LAMIA, builder -> builder.order(36900).rarity(2).background(HALLOW_CAVE_DESERT).filters(FilterEntry.CAVE, FilterEntry.HALLOW_DESERT))
                .add(MonsterEntities.SPORE_ZOMBIE, builder -> builder.order(37000).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                .add(MonsterEntities.FUNGI_BULB, builder -> builder.order(38000).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.GIANT_FUNGI_BULB, builder -> builder.order(38100).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.FUNGO_FISH, builder -> builder.order(38200).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM, FilterEntry.UNDERGROUND_MUSHROOM))
                .add(MonsterEntities.HAT_SPORE_ZOMBIE, builder -> builder.order(37100).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                // 歪尾真菌
                // 蘑菇瓢虫
                // 真菌球怪
                // 巨型真菌球怪
                // 蘑菇鱼
                // 丛林蜥蜴
                // 飞蛇
                .add(MonsterEntities.GOBLIN_PEON, builder -> builder.order(37900).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_THIEF, builder -> builder.order(38000).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_ARCHER, builder -> builder.order(38100).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_WARRIOR, builder -> builder.order(38200).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_SORCERER, builder -> builder.order(38300).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.GOBLIN_WARLOCK, builder -> builder.order(38900).rarity(4).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.SHADOWFLAME_APPARITION, builder -> builder.order(39100).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(MonsterEntities.PIRATE_DEADEYE, builder -> builder.order(40500).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_DECKHAND, builder -> builder.order(40600).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_CROSSBOWER, builder -> builder.order(40700).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_CORSAIR, builder -> builder.order(40800).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_CAPTAIN, builder -> builder.order(40900).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATES_CURSE, builder -> builder.order(40901).rarity(3).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.PIRATE_PARROT, builder -> builder.order(41000).rarity(2).background(SURFACE).filters(FilterEntry.PIRATE_INVASION))
                .add(MonsterEntities.MARTIAN_PROBE, builder -> builder.order(33000).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE))
                .add(MonsterEntities.ANGER_GOBLIN, builder -> builder.order(38301).rarity(3).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                // 哥布林巫士
                // 暗影焰幻鬼
                // 撒旦骷髅
                // 埃特尼亚哥布林
                // 埃特尼亚哥布林投弹手
                // 小妖魔
                // 埃特尼亚标枪投掷怪
                // 枯萎兽
                // 德拉克龙
                // 食人魔
                // 小妖魔滑翔怪
                // 埃特尼亚飞龙
                // 黑暗魔法师
                // 双足翼龙
                // 埃特尼亚荧光虫
                // 海盗神射手
                // 海盗水手
                // 海盗弩手
                // 私船海盗
                // 海盗船长
                // 鹦鹉
                // 荷兰飞盗船
                .add(MonsterEntities.BRAIN_SCRAMBLER, builder -> builder.order(41200).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                .add(MonsterEntities.RAY_GUNNER, builder -> builder.order(41300).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                .add(MonsterEntities.MARTIAN_ENGINEER, builder -> builder.order(41400).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                .add(MonsterEntities.MARTIAN_OFFICER, builder -> builder.order(41500).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                .add(MonsterEntities.GIGAZAPPER, builder -> builder.order(41600).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                .add(MonsterEntities.SCUTLIX, builder -> builder.order(41700).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                .add(MonsterEntities.GRAY_GRUNT, builder -> builder.order(41800).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                .add(MonsterEntities.MARTIAN_WALKER, builder -> builder.order(41900).rarity(3).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                .add(MonsterEntities.TESLA_TURRET, builder -> builder.order(42000).rarity(2).background(SURFACE).filters(FilterEntry.MARTIAN_MADNESS))
                // 火星飞船
                // 鳞甲怪枪手
                // 弗里茨
                // 科学怪人
                // 水月怪
                // 沼泽怪
                // 苍蝇人博士
                // 攀爬魔
                // 变态人
                // 屠夫
                // 吸血鬼
                // 眼怪
                // 钉头
                // 死神
                // 致命球
                // 蛾怪
                // 蛾怪宝宝
                // 稻草人 （布衣，有脸，棍式）
                // 稻草人 （布衣，有脸）
                // 稻草人 （盖伊·福克斯，棍式）
                // 稻草人 （盖伊·福克斯）
                // 稻草人 （布衣，戴帽，棍式）
                // 稻草人 （布衣，戴帽）
                // 稻草人 （南瓜头，戴帽，棍式）
                // 稻草人 （南瓜头，戴帽）
                // 稻草人 （南瓜头，棍式）
                // 稻草人 （南瓜头）
                // 树精
                // 胡闹鬼
                // 地狱犬
                // 无头骑士
                // 哀木
                // 南瓜王
                // 僵尸精灵 （女孩）
                // 僵尸精灵
                // 僵尸精灵 （胡子）
                // 姜饼人
                // 精灵弓箭手
                // 胡桃夹士
                // 坎卜斯
                // 雪兽
                // 礼物宝箱怪
                // 常绿尖叫怪
                // 冰雪女王
                // 圣诞坦克
                // 精灵直升机
                // 雪花怪
                // 史莱姆 （兔兔面具）
                // 恶魔眼 （太空船
                // 恶魔眼 （猫头鹰面具）
                // 僵尸 （医生）
                // 僵尸 （超人）
                // 僵尸 （妖精）
                // 骷髅 （宇航员）
                // 骷髅 （异星）
                // 骷髅 （高顶礼帽）
                // 史莱姆 （红礼物史莱姆）
                // 史莱姆 （黄礼物史莱姆）
                // 史莱姆 （白礼物史莱姆）
                // 史莱姆 （绿礼物史莱姆）
                // 僵尸 （圣诞节）
                // 僵尸 （毛衣）
                // 雪人暴徒
                // 巴拉雪人
                // 戳刺先生
                // 预言怪
                // 进化兽
                // 吮脑怪
                // 星云浮怪
                // 火龙怪
                // 火月怪
                // 火龙战士
                // 千足蜈蚣
                // 火滚怪
                // 流星火怪
                // 火龙怪骑士
                // 异星幼虫
                // 异星黄蜂
                // 星旋怪
                // 漩泥怪
                // 异星蜂王
                // 观星怪
                // 闪耀炮手
                // 银河织妖
                // 星细胞
                // 迷你星细胞
                // 流体入侵怪
                // 火星飞碟
                // 火把神
                .add(BossEntities.SERVANT_OF_CTHULHU, "entity.confluence.demon_eye", "minion", builder -> builder.order(51100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(BossEntities.EYE_OF_CTHULHU, builder -> builder.order(50400).rarity(2).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                .add(MonsterEntities.DEMON_EYE, "minion", builder -> builder.order(50500).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(BossEntities.KING_SLIME, builder -> builder.order(50600).rarity(2).background(SURFACE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.SURFACE))
                .add(BossEntities.EATER_OF_WORLDS, builder -> builder.order(50700).rarity(3).background(THE_CORRUPTION).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CORRUPTION))
                .add(BossEntities.BRAIN_OF_CTHULHU, builder -> builder.order(50800).rarity(3).background(THE_CRIMSON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_CRIMSON))
                .add(MonsterEntities.VISUAL_NEURON, builder -> builder.order(50900).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON))
                .add(BossEntities.DEERCLOPS, builder -> builder.order(51000).rarity(3).background(SNOW).filters(FilterEntry.BOSS_ENEMY, FilterEntry.SNOW))
                .add(BossEntities.SKELETRON, builder -> builder.order(51100).rarity(3).background(THE_DUNGEON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_DUNGEON))
                .add(BossEntities.QUEEN_BEE, builder -> builder.order(51200).rarity(3).background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE))
                .add(BossEntities.WALL_OF_FLESH, builder -> builder.order(51300).rarity(4).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER))
                .add(MonsterEntities.LEECH, builder -> builder.order(51400).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(MonsterEntities.THE_HUNGRY, "entity.confluence.the_hungry", "flying", builder -> builder.order(52200).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER).entityNbt(tag -> tag.putBoolean("Free", true)))
                .add(MonsterEntities.THE_HUNGRY, builder -> builder.order(51500).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                // 饿鬼 （飞行）
                // 史莱姆皇后
                // 水晶史莱姆
                // 弹力史莱姆
                // 飞翔史莱姆
                .add(BossEntities.RETINAZER, builder -> builder.order(52100).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                .add(BossEntities.THE_DESTROYER, builder -> builder.order(52900).rarity(4).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                .add(BossEntities.THE_DESTROYER_PROBE, builder -> builder.order(53000).rarity(2).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                .add(BossEntities.SPAZMATISM, builder -> builder.order(52200).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                // 魔焰眼
                // 毁灭者
                // 探测怪
                .add(BossEntities.SKELETRON_PRIME, builder -> builder.order(52500).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                .add(BossEntities.LUNATIC_CULTIST, builder -> builder.order(53700).rarity(4).background(THE_DUNGEON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_DUNGEON))
                .add(BossEntities.PHANTASM_DRAGON, builder -> builder.order(54100).rarity(3).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(BossEntities.PLANTERA, builder -> builder.order(52600).rarity(4).background(UNDERGROUND_JUNGLE).filters(FilterEntry.BOSS_ENEMY, FilterEntry.UNDERGROUND_JUNGLE))
                // 光之女皇
                // 石巨人
                // 猪龙鱼公爵
                // 鲨鱼龙
                // 拜月教邪教徒
                // 拜月教忠教徒
                // 蓝邪教徒弓箭手
                // 远古幻影妖
                // 幻影龙
                // 星云柱
                // 日耀柱
                // 星旋柱
                // 星尘柱
                // 月亮领主  54000

                // MC原版生物 从60000开始  和泰拉生物重合归上面的泰拉生物编号
                .add(EntityType.ALLAY, builder -> builder.order(60000).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.ARMADILLO, builder -> builder.order(60100).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.BAT, builder -> builder.order(60200).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.CAMEL, builder -> builder.order(60300).rarity(2).background(DESERT_SUN).filters(FilterEntry.DESERT,FilterEntry.DAYTIME))
                .add(CritterEntities.CLUCKSHROOM, builder -> builder.order(60401).rarity(2).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(CritterEntities.CLUCKSHROOM, "entity.confluence.brown_cluckshroom", "brown", builder -> builder.order(60402).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE).entityNbt(tag -> tag.putBoolean("Brown", true)))
                .add(CritterEntities.GLOWING_CLUCKSHROOM, builder -> builder.order(60403).rarity(3).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(EntityType.CHICKEN, builder -> builder.order(60400).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.COD, builder -> builder.order(60500).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))
                .add(EntityType.COW, builder -> builder.order(60600).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.DONKEY, builder -> builder.order(60700).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.GLOW_SQUID, builder -> builder.order(60800).rarity(3).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.HORSE, builder -> builder.order(60900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(CritterEntities.GLOWING_MOOSHROOM, builder -> builder.order(61001).rarity(4).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(EntityType.MOOSHROOM, builder -> builder.order(61000).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.MULE, builder -> builder.order(61100).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.PIG, builder -> builder.order(61200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.SALMON, builder -> builder.order(61300).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))
                .add(CritterEntities.CLOUD_SHEEP, builder -> builder.order(61401).rarity(3).background(SURFACE_SUN).filters(FilterEntry.SURFACE, FilterEntry.DAYTIME))
                .add(EntityType.SHEEP, builder -> builder.order(61400).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.SKELETON_HORSE, builder -> builder.order(61500).rarity(4).background(SURFACE_NIGHTTIME_RAIN).filters(FilterEntry.SURFACE,FilterEntry.NIGHTTIME,FilterEntry.RAIN))
                .add(EntityType.SNIFFER, builder -> builder.order(61600).rarity(4).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.SQUID, builder -> builder.order(61700).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))
                .add(EntityType.STRIDER, builder -> builder.order(61800).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.TADPOLE, builder -> builder.order(61900).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(EntityType.TROPICAL_FISH, builder -> builder.order(62000).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))
                .add(EntityType.WANDERING_TRADER, builder -> builder.order(62100).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.PUFFERFISH, builder -> builder.order(62200).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))
                .add(EntityType.GOAT, builder -> builder.order(62300).rarity(2).background(SNOW).filters(FilterEntry.SNOW,FilterEntry.SURFACE,FilterEntry.DAYTIME))
                .add(EntityType.VILLAGER, builder -> builder.order(62400).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.AXOLOTL, builder -> builder.order(62500).rarity(3).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                .add(EntityType.CAT, builder -> builder.order(62600).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.OCELOT, builder -> builder.order(62700).rarity(4).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE,FilterEntry.DAYTIME))
                .add(EntityType.SNOW_GOLEM, builder -> builder.order(62800).rarity(2).background(SNOW).filters(FilterEntry.SNOW,FilterEntry.SURFACE,FilterEntry.DAYTIME))
                .add(EntityType.BEE, builder -> builder.order(62900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.FOX, builder -> builder.order(63000).rarity(2).background(SNOW).filters(FilterEntry.SNOW,FilterEntry.SURFACE,FilterEntry.DAYTIME))
                .add(EntityType.IRON_GOLEM, builder -> builder.order(63100).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.LLAMA, builder -> builder.order(63200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.PANDA, builder -> builder.order(63300).rarity(5).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE,FilterEntry.DAYTIME))
                .add(EntityType.POLAR_BEAR, builder -> builder.order(63400).rarity(3).background(SNOW).filters(FilterEntry.OCEAN,FilterEntry.SNOW,FilterEntry.DAYTIME))
                .add(EntityType.TRADER_LLAMA, builder -> builder.order(63500).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.WOLF, builder -> builder.order(63600).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.BLAZE, builder -> builder.order(63700).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.BOGGED, builder -> builder.order(63800).rarity(2).background(SURFACE_MOON).filters(surfaceNighttime))
                .add(EntityType.BREEZE, builder -> builder.order(63900).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.CREEPER, builder -> builder.order(64000).rarity(1).background(SURFACE_MOON).filters(surfaceNighttime))
                .add(EntityType.ELDER_GUARDIAN, builder -> builder.order(64100).rarity(5).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))
                .add(EntityType.ENDERMITE, builder -> builder.order(64200).rarity(2).background(SURFACE).filters(surfaceNighttime))
                .add(EntityType.EVOKER, builder -> builder.order(64300).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.GHAST, builder -> builder.order(64400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.GUARDIAN, builder -> builder.order(64500).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.DAYTIME))
                .add(EntityType.HOGLIN, builder -> builder.order(64600).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.HUSK, builder -> builder.order(64700).rarity(1).background(DESERT).filters(FilterEntry.DESERT,FilterEntry.SURFACE,FilterEntry.NIGHTTIME))
                .add(EntityType.MAGMA_CUBE, builder -> builder.order(64800).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.PHANTOM, builder -> builder.order(64900).rarity(1).background(SURFACE_MOON).filters(surfaceNighttime))
                .add(EntityType.PIGLIN_BRUTE, builder -> builder.order(65000).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.PILLAGER, builder -> builder.order(65100).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.RAVAGER, builder -> builder.order(65200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.SHULKER, builder -> builder.order(65300).rarity(2).background(SURFACE).filters(surfaceNighttime))
                .add(EntityType.SILVERFISH, builder -> builder.order(65400).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.SLIME, builder -> builder.order(65500).rarity(3).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.STRAY, builder -> builder.order(65600).rarity(2).background(SNOW_MOON).filters(FilterEntry.SNOW,FilterEntry.SURFACE,FilterEntry.NIGHTTIME))
                .add(EntityType.VEX, builder -> builder.order(65700).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.VINDICATOR, builder -> builder.order(65700).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.VINDICATOR, builder -> builder.order(65700).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.WARDEN, builder -> builder.order(65800).rarity(3).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.WITCH, builder -> builder.order(65900).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.WITHER_SKELETON, builder -> builder.order(66000).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.ZOGLIN, builder -> builder.order(66100).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.ZOMBIE_VILLAGER, builder -> builder.order(66200).rarity(2).background(SURFACE_MOON).filters(surfaceNighttime))
                .add(EntityType.DROWNED, builder -> builder.order(66300).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN,FilterEntry.NIGHTTIME))
                .add(EntityType.ENDERMAN, builder -> builder.order(66400).rarity(3).background(SURFACE_MOON).filters(surfaceNighttime))
                .add(EntityType.PIGLIN, builder -> builder.order(66500).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.SPIDER, builder -> builder.order(66600).rarity(1).background(SURFACE_MOON).filters(surfaceNighttime))
                .add(EntityType.CAVE_SPIDER, builder -> builder.order(66700).rarity(2).background(CAVE).filters(FilterEntry.CAVE))
                .add(EntityType.ZOMBIFIED_PIGLIN, builder -> builder.order(66800).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(EntityType.ENDER_DRAGON, builder -> builder.order(66900).rarity(5).background(THE_END).filters(FilterEntry.BOSS_ENEMY))
                .add(EntityType.WITHER, builder -> builder.order(67000).rarity(2).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY))

                // 原创生物 从70000开始
                .add(MonsterEntities.DECAYEDER, builder -> builder.order(70000).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.SURFACE,FilterEntry.DAYTIME,FilterEntry.THE_CORRUPTION))
                .add(MonsterEntities.BLOODY_SPORE, builder -> builder.order(70100).rarity(2).background(THE_CRIMSON).filters(FilterEntry.SURFACE,FilterEntry.DAYTIME,FilterEntry.THE_CRIMSON))
                .add(BossEntities.HILL_OF_FLESH, builder -> builder.order(70200).rarity(4).background(THE_NETHER).filters(FilterEntry.BOSS_ENEMY, FilterEntry.THE_NETHER))
                .add(MonsterEntities.WITHER_BONE_SERPENT, builder -> builder.order(70300).rarity(3).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .map);
    }

    private static void cave(ClientBestiary.Entry.Builder builder, int order, int rarity) {
        builder.order(order).rarity(rarity).background(CAVE).filters(FilterEntry.CAVE);
    }

    @Override
    protected PackOutput.PathProvider pathProvider() {
        return pathProvider;
    }

    public static class Builder {
        private final Map<String, ClientBestiary.Entry> map = Maps.newHashMap();

        public Builder add(IHolderExtension<EntityType<?>> holder, String typeKey, String variant, Consumer<ClientBestiary.Entry.Builder> consumer) {
            String key = variant.isEmpty() ? typeKey : typeKey + '.' + variant;
            ClientBestiary.Entry.Builder builder = ClientBestiary.Entry.builderc(holder.getDelegate().value(), key);
            consumer.accept(builder);
            builder.description(Component.translatable("bestiary." + key + ".desc"));
            map.put(key, builder.build());
            return this;
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, String variant, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return add(holder, holder.getDelegate().value().getDescriptionId(), variant, consumer);
        }

        public Builder add(EntityType<?> type, String variant, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return add(type.builtInRegistryHolder(), variant, consumer);
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return add(holder, "", consumer);
        }

        public Builder add(EntityType<?> type, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return add(type.builtInRegistryHolder(), consumer);
        }

        /// 使用变种的稳定序列化名称作为条目键，并写入实体预览的变种数据。
        public <E extends Enum<E> & IVariant> Builder variant(IHolderExtension<EntityType<?>> holder, E entityVariant, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return add(holder, entityVariant.getSerializedName(), consumer.andThen(builder -> builder.entityNbt(entityVariant::serialize)));
        }

        public <E extends Enum<E> & IVariant> Builder numberedVariant(IHolderExtension<EntityType<?>> holder, int displayVariant, E entityVariant, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return numberedVariant(holder, holder.getDelegate().value().getDescriptionId(), displayVariant, entityVariant, consumer);
        }

        public <E extends Enum<E> & IVariant> Builder numberedVariant(IHolderExtension<EntityType<?>> holder, String typeKey, int displayVariant, E entityVariant, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return add(holder, typeKey, Integer.toString(displayVariant), consumer.andThen(builder -> builder.entityNbt(entityVariant::serialize)));
        }

        public Builder demonEyeVariant(DemonEye.Variant variant, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return add(MonsterEntities.DEMON_EYE, variant.getSerializedName(), consumer.andThen(builder -> builder.entityNbt(variant::serialize)));
        }

        /// @param armorItems \[鞋子，裤子，衣服，帽子\]
        public Builder mobArmorItems(IHolderExtension<EntityType<?>> holder, String typeKey, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiary.Entry.Builder> consumer) {
            if (armorItems.size() != 4) throw new IllegalArgumentException();
            return add(holder, typeKey, variant, consumer.andThen(builder -> builder.entityNbt(nbt -> {
                ListTag listTag = new ListTag();
                for (ItemStack itemStack : armorItems) {
                    if (itemStack.isEmpty()) {
                        listTag.add(new CompoundTag());
                    } else {
                        listTag.add(itemStack.save(provider));
                    }
                }
                nbt.put("ArmorItems", listTag);
            })));
        }

        /// @param armorItems \[鞋子，裤子，衣服，帽子\]
        public Builder mobArmorItems(EntityType<?> type, String typeKey, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return mobArmorItems(type.builtInRegistryHolder(), typeKey, variant, armorItems, provider, consumer);
        }

        /// @param armorItems \[鞋子，裤子，衣服，帽子\]
        public Builder mobArmorItems(IHolderExtension<EntityType<?>> holder, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return mobArmorItems(holder, holder.getDelegate().value().getDescriptionId(), variant, armorItems, provider, consumer);
        }

        /// @param armorItems \[鞋子，裤子，衣服，帽子\]
        public Builder mobArmorItems(EntityType<?> type, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiary.Entry.Builder> consumer) {
            return mobArmorItems(type.builtInRegistryHolder(), variant, armorItems, provider, consumer);
        }
    }
}
