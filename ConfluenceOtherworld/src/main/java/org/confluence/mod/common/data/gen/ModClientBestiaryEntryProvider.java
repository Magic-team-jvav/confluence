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
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.client.handler.bestiary.FilterEntry;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.terraentity.entity.animal.*;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import org.confluence.terraentity.entity.monster.demoneye.DemonEyeVariant;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.List;
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
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider provider) {
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
                // 神秘青蛙
                .add(TEAnimals.BUNNY, builder -> builder.order(4200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(EntityType.RABBIT.builtInRegistryHolder(), builder -> builder.order(4210).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                // 兔兔 （戴帽子）
                .add(TEAnimals.BOOM_BUNNY, builder -> builder.order(4250).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                // 兔兔 （史莱姆）
                // 兔兔 （圣诞节）
                .addIntVariant(TEAnimals.JEWEL_BUNNY, JewelBunny.VARIANT_KEY, JewelBunny.GOLDEN_ID, builder -> builder.order(4700).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEAnimals.BIRD, builder -> builder.order(4800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEAnimals.BLUE_JAY, builder -> builder.order(4900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEAnimals.CARDINAL, builder -> builder.order(5000).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                // 绯红金刚鹦鹉
                // 蓝金刚鹦鹉
                // 巨嘴鸟
                // 黄玄凤鹦鹉
                // 灰玄凤鹦鹉
                // 金鸟
                // 金鱼
                // 金金鱼
                .addIntVariant(TEAnimals.SQUIRREL, Squirrel.VARIANT_KEY, Squirrel.COMMON_ID, builder -> builder.order(5900).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.SQUIRREL, Squirrel.VARIANT_KEY, Squirrel.RED_ID, builder -> builder.order(6000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.JEWEL_SQUIRREL, JewelSquirrel.VARIANT_KEY, JewelSquirrel.GOLDEN_ID, builder -> builder.order(6100).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                // 老鼠
                // 金老鼠
                .add(EntityType.FROG.builtInRegistryHolder(), builder -> builder.order(6400).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 金青蛙
                .addIntVariant(TEAnimals.GRASSHOPPER, JumpableVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_GRASSHOPPER_ID, builder -> builder.order(6600).rarity(1).background(SURFACE).filters(FilterEntry.SURFACE))
                .addIntVariant(TEAnimals.GRASSHOPPER, JumpableVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_GRASSHOPPER_ID, builder -> builder.order(6700).rarity(5).background(SURFACE).filters(FilterEntry.SURFACE))
                .addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_BUTTERFLY_ID, builder -> builder.order(6800).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.BUTTERFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_BUTTERFLY_ID, builder -> builder.order(6900).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_WORM_ID, builder -> builder.order(7000).rarity(1).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                .addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_WORM_ID, builder -> builder.order(7100).rarity(5).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                .addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_DRAGONFLY_ID, builder -> builder.order(7200).rarity(3).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.DRAGONFLY, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_DRAGONFLY_ID, builder -> builder.order(7300).rarity(5).background(SURFACE_SUN).filters(surfaceDaytime))
                // 海马
                // 金海马
                // 水黾
                // 金水黾
                .addIntVariant(TEAnimals.LADYBUG, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.COMMON_LADYBUG_ID, builder -> builder.order(7800).rarity(3).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                .addIntVariant(TEAnimals.LADYBUG, BirdVariantAnimal.VARIANT_KEY, VariantsTextureMaps.GOLD_LADYBUG_ID, builder -> builder.order(7900).rarity(5).background(SURFACE).filters(FilterEntry.WINDY_DAY))
                // 臭虫
                .addIntVariant(TEAnimals.FEALING, Fairy.VARIANT_KEY, VariantsTextureMaps.COMMON_FEALING_ID, builder -> builder.order(8100).rarity(5).background(CAVE).filters(FilterEntry.CAVE))
                .addIntVariant(TEAnimals.DUCK, Duck.VARIANT_KEY, Duck.MALLARD_ID, builder -> builder.order(8200).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                .addIntVariant(TEAnimals.DUCK, Duck.VARIANT_KEY, Duck.COMMON_ID, builder -> builder.order(8300).rarity(2).background(SURFACE_SUN).filters(surfaceDaytime))
                // 龟
                // 猫头鹰
                // 萤火虫
                .addIntVariant(TEAnimals.WORM, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.ENCHANTED_NIGHTCRAWLER_ID, builder -> builder.order(8700).rarity(5).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.PINK_FAIRY_ID, builder -> builder.order(8800).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.GREEN_FAIRY_ID, builder -> builder.order(8900).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                .addIntVariant(TEAnimals.FAIRY, Fairy.VARIANT_KEY, VariantsTextureMaps.BLUE_FAIRY_ID, builder -> builder.order(9000).rarity(4).background(SURFACE_NIGHTTIME).filters(FilterEntry.NIGHTTIME))
                // 大鼠
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
                .add(TEAnimals.SNAIL, builder -> builder.order(10700).rarity(1).background(CAVE).filters(FilterEntry.CAVE))
                // 松露虫
                // 企鹅
                // 企鹅（黑）
                .addIntVariant(TEAnimals.SCORPION, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.SCORPION_ID, builder -> builder.order(11100).rarity(1).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                .addIntVariant(TEAnimals.SCORPION, SimpleVariantAnimal.VARIANT_KEY, VariantsTextureMaps.BLACK_SCORPION_ID, builder -> builder.order(11200).rarity(2).background(DESERT_SUN).filters(FilterEntry.DESERT, FilterEntry.DAYTIME))
                // 䴙䴘
                // 鳉鱼
                // 海鸥
                .add(EntityType.TURTLE.builtInRegistryHolder(), builder -> builder.order(11600).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(EntityType.DOLPHIN.builtInRegistryHolder(), builder -> builder.order(11700).rarity(3).background(OCEAN).filters(FilterEntry.OCEAN))
                // 丛林龟
                .add(TEAnimals.GRUBBY, builder -> builder.order(11900).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                .add(TEAnimals.SLUGGY, builder -> builder.order(12000).rarity(2).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 蚜虫
                // 熔岩萤火虫
                .add(TEAnimals.HELL_BUTTERFLY, builder -> builder.order(12300).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(TEAnimals.MAGMA_SNAIL, builder -> builder.order(12400).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                // 荧火虫
                .add(TEAnimals.PRISMATIC_LACEWING, builder -> builder.order(12600).rarity(3).background(THE_HALLOW_MOON).filters(FilterEntry.THE_HALLOW, FilterEntry.NIGHTTIME)) //神圣夜晚
                .add(TEAnimals.GLOWING_SNAIL, builder -> builder.order(12700).rarity(3).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                // 侏儒
                .add(TEMonsterEntities.GOBLIN_SCOUT, builder -> builder.order(12900).rarity(3).background(SURFACE).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE))
                .add(TEMonsterEntities.GREEN_SLIME, builder -> builder.order(13000).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEMonsterEntities.BLUE_SLIME, builder -> builder.order(13100).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEMonsterEntities.PURPLE_SLIME, builder -> builder.order(13200).rarity(1).background(SURFACE_SUN).filters(surfaceDaytime))
                .add(TEMonsterEntities.PINK_SLIME, builder -> builder.order(13300).rarity(4).background(SURFACE_SUN).filters(FilterEntry.RARE_CREATURE, FilterEntry.SURFACE, FilterEntry.DAYTIME))
                // 大风气球怪
                // 愤怒蒲公英
                // 雨伞史莱姆
                .add(TEMonsterEntities.FLYING_FISH, builder -> builder.order(13700).rarity(2).background(SURFACE_RAIN).filters(FilterEntry.SURFACE, FilterEntry.RAIN))
                // 愤怒雨云怪
                .demonEyeVariant(DemonEyeVariant.DILATED, builder -> builder.order(13900).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.DILATED_SMALL, builder -> builder.order(13910).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.SLEEPY, builder -> builder.order(14000).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.SLEEPY_BIG, builder -> builder.order(14010).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.PURPLE, builder -> builder.order(14100).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.PURPLE_BIG, builder -> builder.order(14110).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.NORMAL, builder -> builder.order(14200).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.NORMAL_BIG, builder -> builder.order(14210).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.GREEN, builder -> builder.order(14300).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.GREEN_SMALL, builder -> builder.order(14310).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.CATARACT, builder -> builder.order(14400).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                .demonEyeVariant(DemonEyeVariant.CATARACT_BIG, builder -> builder.order(14410).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 游荡眼球怪
                // 僵尸 （女性）
                .add(EntityType.ZOMBIE.builtInRegistryHolder(), "slime", builder -> builder.order(14700).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 僵尸 （光头）
                .add(EntityType.ZOMBIE.builtInRegistryHolder(), builder -> builder.order(14900).rarity(1).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 僵尸 （纤瘦）
                // 僵尸 （火把）
                // 僵尸 （沼泽）
                // 僵尸 （中箭）
                .mobArmorItems(EntityType.ZOMBIE.builtInRegistryHolder(), "entity.confluence.raincoat_zombie", "", List.of(ArmorItems.RAIN_CAP.toStack(), ArmorItems.RAINCOAT.toStack(), ItemStack.EMPTY, ItemStack.EMPTY), provider, builder -> builder.order(15400).rarity(2).background(SURFACE_NIGHTTIME_RAIN).filters(FilterEntry.NIGHTTIME, FilterEntry.RAIN))
                .add(TEMonsterEntities.POSSESS_ARMOR, builder -> builder.order(15500).rarity(2).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 狼人
                .add(TEMonsterEntities.WRAITH, builder -> builder.order(15700).rarity(2).background(SURFACE_MOON).filters(FilterEntry.SURFACE, FilterEntry.NIGHTTIME))
                // 腐化兔兔
                // 腐化企鹅
                // 毒兔兔
                // 毒企鹅
                .add(TEMonsterEntities.BLOOD_ZOMBIE, builder -> builder.order(16200).rarity(1).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))
                // 僵尸新郎
                // 僵尸新娘
                // 僵尸人鱼
                // 小丑
                // 血乌贼
                // 血鳗鱼
                // 腐化金鱼
                // 毒金鱼
                .add(TEMonsterEntities.DRIPPLER, builder -> builder.order(17100).rarity(2).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))
                // 嗒嗒牙齿炸弹
                .add(TEMonsterEntities.WANDERING_EYE_FISH, builder -> builder.order(17300).rarity(4).background(BLOOD_MOON).filters(FilterEntry.SURFACE, FilterEntry.BLOOD_MOON, FilterEntry.NIGHTTIME))
                // 血浆哥布林鲨鱼
                // 恐惧鹦鹉螺
                // 弹跳杰克南瓜灯
                // 蝇蛆僵尸
                // 乌鸦
                .add(TEMonsterEntities.GHOST, builder -> builder.order(17900).rarity(3).background(GRAVEYARD).filters(FilterEntry.GRAVEYARD))
                .add(TEMonsterEntities.RED_SLIME, builder -> builder.order(18000).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                .add(TEMonsterEntities.YELLOW_SLIME, builder -> builder.order(18100).rarity(3).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 毒泥
                .add(TEMonsterEntities.GIANT_WORM, builder -> builder.order(18300).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND))
                // 挖掘怪
                .add(TEMonsterEntities.BLACK_SLIME, "entity.terra_entity.baby_slime", "", builder -> cave(builder, 18500, 1))
                .add(TEMonsterEntities.BLACK_SLIME, builder -> cave(builder, 18600, 1))
                // 微光史莱姆
                .add(TEMonsterEntities.BLACK_SLIME, "entity.terra_entity.mother_slime", "", builder -> cave(builder, 18800, 2))
                // 胭脂虫
                // 骷髅 （畸形）
                .add(EntityType.SKELETON.builtInRegistryHolder(), builder -> cave(builder, 19100, 1))
                // 骷髅 （头痛）
                // 骷髅 （无裤）
                // 龙虾
                .mobArmorItems(EntityType.SKELETON.builtInRegistryHolder(), "entity.confluence.undead_miner", "", List.of(ArmorItems.MINING_HELMET.toStack(), ArmorItems.MINING_CHESTPLATE.toStack(), ArmorItems.MINING_LEGGINGS.toStack(), ArmorItems.MINING_BOOTS.toStack()), provider, builder -> builder.order(19600).rarity(3).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                // 骷髅弓箭手
                .add(TEMonsterEntities.NYMPH, builder -> builder.order(19800).rarity(5).background(CAVE).filters(FilterEntry.RARE_CREATURE, FilterEntry.CAVE))
                // 装甲骷髅
                // 岩石巨人
                // 蒂姆
                // 符文巫师
                .add(TEMonsterEntities.CAVE_BAT, builder -> cave(builder, 20300, 1))
                // 巨型蝙蝠
                .add(TEMonsterEntities.BLUE_JELLYFISH, builder -> cave(builder, 20500, 1))
                .add(TEMonsterEntities.GREEN_JELLYFISH, builder -> cave(builder, 20600, 2))
                // 宝箱怪
                .add(TEMonsterEntities.GIANT_SHELLY, builder -> cave(builder, 20800, 2))
                // 迷失女孩
                // 花岗岩巨人
                .add(TEMonsterEntities.GRANITE_ELEMENTAL, builder -> builder.order(21100).rarity(2).background(GRANITE).filters(FilterEntry.GRANITE))
                // 装甲步兵
                // 蛇发女妖
                .add(TEMonsterEntities.SPORE_SKELETON, builder -> builder.order(21400).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                .add(TEMonsterEntities.SPORE_BAT, builder -> builder.order(21500).rarity(1).background(GLOWING_MUSHROOM).filters(FilterEntry.UNDERGROUND_MUSHROOM))
                // 爬墙蜘蛛
                // 黑隐士
                .add(TEMonsterEntities.ICE_SLIME, builder -> builder.order(21800).rarity(1).background(SNOW).filters(FilterEntry.SNOW, FilterEntry.DAYTIME))
                .mobArmorItems(EntityType.ZOMBIE.builtInRegistryHolder(), "entity.confluence.frozen_zombie", "", List.of(ArmorItems.SNOW_CAPS.toStack(), ArmorItems.SNOW_SUITS.toStack(), ArmorItems.INSULATED_PANTS.toStack(), ArmorItems.INSULATED_SHOES.toStack()), provider, builder -> builder.order(21900).rarity(2).background(SNOW_MOON).filters(FilterEntry.SNOW, FilterEntry.NIGHTTIME))
                .mobArmorItems(EntityType.ZOMBIE.builtInRegistryHolder(), "entity.confluence.frozen_zombie.pink", "", List.of(ArmorItems.PINK_SNOW_CAPS.toStack(), ArmorItems.PINK_SNOW_SUITS.toStack(), ArmorItems.PINK_INSULATED_PANTS.toStack(), ArmorItems.PINK_INSULATED_SHOES.toStack()), provider, builder -> builder.order(21910).rarity(5).background(SNOW_MOON).filters(FilterEntry.SNOW, FilterEntry.NIGHTTIME))
                // 冰雪巨人
                // 狼
                // 尖刺冰雪史莱姆
                // 青壳虫
                // 亡灵维京海盗
                .add(TEMonsterEntities.SNOW_FLINX, builder -> builder.order(22500).rarity(3).background(UNDERGROUND_SNOW).filters(FilterEntry.UNDERGROUND_SNOW))
                // 装甲维京海盗
                // 冰雪人鱼
                .add(TEMonsterEntities.ICE_BAT, builder -> builder.order(22800).rarity(1).background(UNDERGROUND_SNOW).filters(FilterEntry.UNDERGROUND_SNOW))
                // 冰雪精
                // 冰雪宝箱怪
                // 冰雪陆龟
                // 秃鹰
                .add(TEMonsterEntities.DESERT_SLIME, builder -> builder.order(23300).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 蚁狮幼虫
                // 巨型蚁狮马
                // 木乃伊
                // 食尸鬼
                // 蛇蜥怪
                .add(TEMonsterEntities.TOMB_CRAWLER, builder -> builder.order(23900).rarity(1).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 蚁狮
                // 沙贼
                .add(TEMonsterEntities.GIANT_ANTLION_SWARMER, builder -> builder.order(24200).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 蚁狮马
                // 沙虫
                // 愤怒翻滚怪
                .add(TEMonsterEntities.ANTLION_SWARMER, builder -> builder.order(24600).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 沙尘精
                // 沙鲨
                .add(TEAnimals.CRAB, builder -> builder.order(24900).rarity(1).background(OCEAN).filters(FilterEntry.OCEAN))
                // 海蜗牛
                // 鲨鱼
                // 乌贼
                .add(TEMonsterEntities.PINK_JELLYFISH, builder -> builder.order(25300).rarity(2).background(OCEAN).filters(FilterEntry.OCEAN))
                .add(TEMonsterEntities.JUNGLE_SLIME, builder -> builder.order(25400).rarity(1).background(THE_JUNGLE_SUN).filters(FilterEntry.THE_JUNGLE, FilterEntry.DAYTIME))
                .add(TEMonsterEntities.SNATCHER, builder -> builder.order(25500).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE))
                // 巨型飞狐
                // 跳跳兽
                // 尖刺丛林史莱姆
                // 紫胶虫
                // 骷髅博士
                // 蜜蜂
                // 蜜蜂 （较大）
                // 黄蜂 （毒刺）
                // 黄蜂 （尖刺）
                .add(TEMonsterEntities.HORNET, builder -> builder.order(26500).rarity(1).background(UNDERGROUND_JUNGLE).filters(FilterEntry.UNDERGROUND_JUNGLE))
                // 苔藓黄蜂
                // 蛾
                .add(TEMonsterEntities.MAN_EATER, builder -> builder.order(27100).rarity(2).background(UNDERGROUND_DESERT).filters(FilterEntry.UNDERGROUND_DESERT))
                // 愤怒捕手
                .add(TEMonsterEntities.JUNGLE_BAT, builder -> builder.order(27300).rarity(1).background(THE_JUNGLE).filters(FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                .add(TEMonsterEntities.PIRANHA, builder -> builder.order(27400).rarity(1).background(UNDERGROUND).filters(FilterEntry.UNDERGROUND, FilterEntry.THE_JUNGLE, FilterEntry.UNDERGROUND_JUNGLE))
                // 琵琶鱼
                // 巨骨舌鱼
                // 巨型陆龟
                // 丛林蜘蛛
                // 流星头
                .add(TEMonsterEntities.DUNGEON_SLIME, builder -> builder.order(28000).rarity(4).background(THE_DUNGEON).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_DUNGEON))
                // 愤怒骷髅怪
                .add(TEMonsterEntities.BIG_ANGER_BONES, builder -> builder.order(28200).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES, builder -> builder.order(28300).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                .add(TEMonsterEntities.BIG_HELMET_ANGER_BONES, builder -> builder.order(28400).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
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
                .add(TEMonsterEntities.DARK_CASTER, builder -> builder.order(30200).rarity(1).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 魔教徒
                // 魔教徒
                // 死灵法师
                // 褴褛邪教徒法师
                // 死灵法师 （装甲）
                // 褴褛邪教徒法师 （敞开外衣）
                .add(TEMonsterEntities.CURSED_SKULL, builder -> builder.order(30900).rarity(2).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 巨型诅咒骷髅头
                .add(TEBossEntities.DUNGEON_GUARDIAN, builder -> builder.order(31100).rarity(4).background(THE_DUNGEON).filters(FilterEntry.THE_DUNGEON))
                // 地牢幽魂
                .add(TEMonsterEntities.LAVA_SLIME, builder -> builder.order(31300).rarity(1).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                // 痛苦亡魂
                // 骨蛇
                .add(TEMonsterEntities.FIRE_IMP, builder -> builder.order(31600).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(TEMonsterEntities.HELL_BAT, builder -> builder.order(31700).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(TEMonsterEntities.DEMON, builder -> builder.order(31800).rarity(2).background(THE_NETHER).filters(FilterEntry.THE_NETHER))
                .add(TEMonsterEntities.VOODOO_DEMON, builder -> builder.order(31900).rarity(3).background(THE_NETHER).filters(FilterEntry.RARE_CREATURE, FilterEntry.THE_NETHER))
                // 熔岩蝙蝠
                // 红魔鬼
                .add(TEMonsterEntities.WYVERN, builder -> builder.order(32200).rarity(3).background(SKY).filters(FilterEntry.SKY))
                .add(TEMonsterEntities.HARPY, builder -> builder.order(32300).rarity(2).background(SKY).filters(FilterEntry.SKY))
                // 火星探测器
                // 小史莱姆
                .add(TEMonsterEntities.CORRUPT_SLIME, builder -> builder.order(32600).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                .add(TEMonsterEntities.EATER_OF_SOULS, builder -> builder.order(32700).rarity(1).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                // 噬魂怪
                .add(TEMonsterEntities.DEVOURER, builder -> builder.order(32900).rarity(2).background(THE_CORRUPTION).filters(FilterEntry.THE_CORRUPTION, FilterEntry.UNDERGROUND_CORRUPTION))
                // 吞世怪
                // 爬藤怪
                // 恶翅史莱姆
                // 诅咒锤
                // 腐化宝箱怪
                // 猪龙 （腐化）
                // 噬骨沙鲨
                // 暗黑木乃伊
                // 腐恶食尸鬼
                .add(TEMonsterEntities.CRIMSLIME, builder -> builder.order(33900).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(TEMonsterEntities.FACE_MONSTER, builder -> builder.order(34000).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                .add(TEMonsterEntities.CRIMSON_KEMERA, builder -> builder.order(34100).rarity(1).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                // 嗜血怪
                // 血水母
                // 恶心浮游怪
                // 灵液黏黏怪
                // 猩红斧
                .add(TEMonsterEntities.BLOOD_CRAWLER, builder -> builder.order(34700).rarity(2).background(THE_CRIMSON).filters(FilterEntry.THE_CRIMSON, FilterEntry.UNDERGROUND_CRIMSON))
                // 蹦蹦兽
                // 猩红宝箱怪
                // 猪龙 （猩红）
                // 戮血沙鲨
                // 血木乃伊
                // 红染食尸鬼
                // 暗黑拉弥亚
                // 沙漠幽魂
                // 彩虹史莱姆
                .add(TEMonsterEntities.PIXIE, builder -> builder.order(35700).rarity(2).background(THE_HALLOW).filters(FilterEntry.THE_HALLOW))
                // 腹足怪
                // 独角兽
                .add(TEMonsterEntities.LUMINOUS_SLIME, builder -> builder.order(36000).rarity(2).background(UNDERGROUND_HALLOW).filters(FilterEntry.UNDERGROUND_HALLOW))
                // 混沌精
                // 夜明蝙蝠
                // 附魔剑
                // 神圣宝箱怪
                // 猪龙
                // 水晶沙鲨
                // 光明木乃伊
                // 神梦食尸鬼
                // 光明拉弥亚
                .add(TEMonsterEntities.SPORE_ZOMBIE, builder -> builder.order(37000).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                .add(TEMonsterEntities.HAT_SPORE_ZOMBIE, builder -> builder.order(37100).rarity(2).background(GLOWING_MUSHROOM).filters(FilterEntry.SURFACE_MUSHROOM))
                // 歪尾真菌
                // 蘑菇瓢虫
                // 真菌球怪
                // 巨型真菌球怪
                // 蘑菇鱼
                // 丛林蜥蜴
                // 飞蛇
                .add(TEMonsterEntities.GOBLIN_PEON, builder -> builder.order(37900).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(TEMonsterEntities.GOBLIN_THIEF, builder -> builder.order(38000).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(TEMonsterEntities.GOBLIN_ARCHER, builder -> builder.order(38100).rarity(1).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(TEMonsterEntities.GOBLIN_WARRIOR, builder -> builder.order(38200).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
                .add(TEMonsterEntities.GOBLIN_SORCERER, builder -> builder.order(38300).rarity(2).background(SURFACE).filters(FilterEntry.GOBLIN_INVASION))
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
                // 扰脑怪
                // 激光枪手
                // 火星工程师
                // 火星军官
                // 电击怪
                // 鳞甲怪
                // 灰咕噜
                // 火星走妖
                // 特斯拉炮塔
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
                .add(TEBossEntities.EYE_OF_CTHULHU, builder -> builder.order(50400).rarity(2).background(SURFACE_MOON).filters(FilterEntry.BOSS_ENEMY, FilterEntry.NIGHTTIME))
                .add(TEMonsterEntities.DEMON_EYE, "minion", builder -> builder.order(50500).rarity(1).background(SURFACE_MOON).filters(FilterEntry.NIGHTTIME))
                // 史莱姆王
                // 世界吞噬怪
                // 克苏鲁之脑
                // 飞眼怪
                // 独眼巨鹿
                // 骷髅王
                // 蜂王
                // 血肉墙
                // 血蛭
                // 饿鬼 （连接）
                // 饿鬼 （飞行）
                // 史莱姆皇后
                // 水晶史莱姆
                // 弹力史莱姆
                // 飞翔史莱姆
                // 激光眼
                // 魔焰眼
                // 毁灭者
                // 探测怪
                // 机械骷髅王
                // 世纪之花
                // 光之女皇
                // 石巨人
                // 猪龙鱼公爵
                // 鲨鱼龙
                // 	拜月教邪教徒
                // 拜月教忠教徒
                // 蓝邪教徒弓箭手
                // 远古幻影妖
                // 幻影龙
                // 星云柱
                // 日耀柱
                // 星旋柱
                // 星尘柱
                // 月亮领主
                .map);
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

        public Builder add(IHolderExtension<EntityType<?>> holder, String typeKey, String variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            String key = variant.isEmpty() ? typeKey : typeKey + '.' + variant;
            ClientBestiaryEntry.Builder builder = ClientBestiaryEntry.builderc(holder.getDelegate().value(), key);
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

        public Builder demonEyeVariant(DemonEyeVariant variant, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return add(TEMonsterEntities.DEMON_EYE, variant.getSerializedName(), consumer.andThen(builder -> builder.entityNbt(nbt -> nbt.putInt(DemonEye.VARIANT_KEY, variant.getId()))));
        }

        public Builder mobArmorItems(IHolderExtension<EntityType<?>> holder, String typeKey, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiaryEntry.Builder> consumer) {
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

        public Builder mobArmorItems(IHolderExtension<EntityType<?>> holder, String variant, List<ItemStack> armorItems, HolderLookup.Provider provider, Consumer<ClientBestiaryEntry.Builder> consumer) {
            return mobArmorItems(holder, holder.getDelegate().value().getDescriptionId(), variant, armorItems, provider, consumer);
        }
    }
}
