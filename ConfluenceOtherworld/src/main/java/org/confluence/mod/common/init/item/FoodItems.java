package org.confluence.mod.common.init.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.common.HerbSeedItem;
import org.confluence.mod.common.item.food.BaseFoodItem;
import org.confluence.mod.common.item.food.FoodType;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.item.Items.*;

public class FoodItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    //常规食物
    public static final DeferredItem<BaseFoodItem> COOKED_SHRIMP = registerNormalFood("cooked_shrimp", FoodType.PlentySatisfiedProperties(6000));
    public static final DeferredItem<BaseFoodItem> FROG_MEAT = registerNormalFood("frog_meat", FoodType.RAW_MEAT);
    public static final DeferredItem<BaseFoodItem> SQUIRREL_MEAT = registerNormalFood("squirrel_meat", FoodType.RAW_MEAT);
    public static final DeferredItem<BaseFoodItem> ESCARGOT = registerNormalFood("escargot", FoodType.PlentySatisfiedProperties(6000));
    public static final DeferredItem<BaseFoodItem> FROGGLE_BUNWICH = registerNormalFood("froggle_bunwich", FoodType.PlentySatisfiedProperties(7200));
    public static final DeferredItem<BaseFoodItem> GOLDEN_DELIGHT = registerNormalFood("golden_delight", FoodType.GOLDEN_CARP); //金美味
    public static final DeferredItem<BaseFoodItem> GRILLED_SQUIRREL = registerNormalFood("grilled_squirrel", FoodType.PREPARED_MEAT); //松鼠尾
    public static final DeferredItem<BaseFoodItem> LOBSTER_TAIL = registerNormalFood("lobster_tail", FoodType.PlentySatisfiedProperties(6000)); //龙虾尾
    public static final DeferredItem<BaseFoodItem> MONSTER_LASAGNA = registerNormalFood("monster_lasagna", FoodType.PlentySatisfiedProperties(9600)); //怪物千层面
    public static final DeferredItem<BaseFoodItem> COOK_FISH = registerNormalFood("cook_fish", FoodType.WellFedProperties(9600)); //熟鱼
    public static final DeferredItem<BaseFoodItem> SASHIMI = registerNormalFood("sashimi", FoodType.PlentySatisfiedProperties(9600)); //生鱼片
    public static final DeferredItem<BaseFoodItem> ROASTED_BIRD = registerNormalFood("roasted_bird", FoodType.PREPARED_MEAT); //烤鸟腿
    public static final DeferredItem<BaseFoodItem> ROASTED_DUCK = registerNormalFood("roasted_duck", FoodType.PREPARED_MEAT); //鸭肉
    public static final DeferredItem<BaseFoodItem> SAUTEED_FROG_LEGS = registerNormalFood("sauteed_frog_legs", FoodType.PREPARED_MEAT); //爆炒青蛙腿
    public static final DeferredItem<BaseFoodItem> SEAFOOD_DINNER = registerNormalFood("seafood_dinner", FoodType.PlentySatisfiedProperties(16800)); //海鲜大餐
    public static final DeferredItem<BaseFoodItem> BACON = registerNormalFood("bacon", FoodType.ExquisitelyStuffedProperties(28800)); //培根
    public static final DeferredItem<BaseFoodItem> BANANA_SPLIT = registerNormalFood("banana_split", FoodType.PlentySatisfiedProperties(12000)); //香蕉船
    public static final DeferredItem<BaseFoodItem> BBQ_RIBS = registerNormalFood("bbq_ribs", FoodType.ExquisitelyStuffedProperties(28800)); //烧烤肋排
    public static final DeferredItem<BaseFoodItem> BURGER = registerNormalFood("burger", FoodType.ExquisitelyStuffedProperties(9600)); //汉堡
    public static final DeferredItem<BaseFoodItem> CHICKEN_NUGGET = registerNormalFood("chicken_nugget", FoodType.PlentySatisfiedProperties(16800)); //鸡块
    public static final DeferredItem<BaseFoodItem> CHOCOLATE_CHIP_COOKIE = registerNormalFood("chocolate_chip_cookie", FoodType.PlentySatisfiedProperties(24000)); //巧克力大曲奇
    public static final DeferredItem<BaseFoodItem> FRIED_EGG = registerNormalFood("fried_egg", FoodType.PlentySatisfiedProperties(16800));//煎蛋
    public static final DeferredItem<BaseFoodItem> FRIES = registerNormalFood("fries", FoodType.PlentySatisfiedProperties(12000)); //炸薯条
    public static final DeferredItem<BaseFoodItem> HOTDOG = registerNormalFood("hotdog", FoodType.ExquisitelyStuffedProperties(24000)); //热狗
    public static final DeferredItem<BaseFoodItem> PIZZA = registerNormalFood("pizza", FoodType.ExquisitelyStuffedProperties(9600)); //披萨
    public static final DeferredItem<BaseFoodItem> POTATO_CHIPS = registerNormalFood("potato_chips", FoodType.WellFedProperties(30000)); //薯片
    public static final DeferredItem<BaseFoodItem> SHRIMP_PO_BOY = registerNormalFood("shrimp_po_boy", FoodType.PlentySatisfiedProperties(21600)); //鲜虾三明治
    public static final DeferredItem<BaseFoodItem> SHUCKED_OYSTER = registerNormalFood("shucked_oyster", FoodType.WellFedProperties(6000)); //去壳牡蛎
    public static final DeferredItem<BaseFoodItem> SPAGHETTI = registerNormalFood("spaghetti", FoodType.ExquisitelyStuffedProperties(9600)); //意大利面
    public static final DeferredItem<BaseFoodItem> SURPER_STEAK = registerNormalFood("surper_steak", FoodType.ExquisitelyStuffedProperties(19200)); //超大肉排
    public static final DeferredItem<BaseFoodItem> APPLE_PIE = registerNormalFood("apple_pie", FoodType.ExquisitelyStuffedProperties(19200)); //苹果派
    public static final DeferredItem<BaseFoodItem> CHRISTMAS_PUDDING = registerNormalFood("christmas_pudding", FoodType.ExquisitelyStuffedProperties(4800)); //圣诞布丁
    public static final DeferredItem<BaseFoodItem> GINGERBREAD_COOKIE = registerNormalFood("gingerbread_cookie", FoodType.ExquisitelyStuffedProperties(4800)); //姜饼人
    public static final DeferredItem<BaseFoodItem> SUGAR_COOKIE = registerNormalFood("sugar_cookie", FoodType.ExquisitelyStuffedProperties(4800)); //糖曲奇
    public static final DeferredItem<BaseFoodItem> MARSHMALLOW = registerNormalFood("marshmallow", FoodType.WellFedProperties(1200)); //棉花糖
    public static final DeferredItem<BaseFoodItem> COOKED_MARSHMALLOW = registerNormalFood("cooked_marshmallow", FoodType.WellFedProperties(2400)); //烤棉花糖
    public static final DeferredItem<BaseFoodItem> PAD_THAI = registerNormalFood("pad_thai", FoodType.PlentySatisfiedProperties(9600)); //泰式河粉
    public static final DeferredItem<BaseFoodItem> BOWL_OF_SOUP = registerDrinkingFood("bowl_of_soup", FoodType.PlentySatisfiedProperties(9600), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //鱼菇汤
    public static final DeferredItem<BaseFoodItem> FRUIT_SALAD = registerDrinkingFood("fruit_salad", FoodType.WellFedProperties(30000), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //水果沙拉
    public static final DeferredItem<BaseFoodItem> GRUB_SOUP = registerDrinkingFood("grub_soup", FoodType.PlentySatisfiedProperties(16000), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //蛆虫汤
    public static final DeferredItem<BaseFoodItem> NACHOS = registerDrinkingFood("nachos", FoodType.PlentySatisfiedProperties(19200), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //一碗玉米粒
    public static final DeferredItem<BaseFoodItem> PHO = registerDrinkingFood("pho", FoodType.PlentySatisfiedProperties(12000), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //河粉
    public static final DeferredItem<BaseFoodItem> CLOUD_DOUGH = registerNormalFood("cloud_dough", FoodType.WellFedProperties(1200)); // 粗制云朵面包
    public static final DeferredItem<BaseFoodItem> CLOUD_BREAD = registerNormalFood("cloud_bread", FoodType.CLOUD_BREAD); // 云朵面包
    public static final DeferredItem<BaseFoodItem> FLUTTERING_LAMB_CHOPS = registerNormalFood("fluttering_lamb_chops", FoodType.WellFedProperties(1200));
    public static final DeferredItem<BaseFoodItem> COOKED_FLUTTERING_LAMB_CHOPS = registerNormalFood("cooked_fluttering_lamb_chops", FoodType.WellFedProperties(2400));
    public static final DeferredItem<BaseFoodItem> BAOBAB_FRUIT = registerNormalFood("baobab_fruit", Foods.BREAD); //猴面包果
    public static final DeferredItem<BaseFoodItem> COOKED_BAOBAB_FRUIT = registerNormalFood("cooked_baobab_fruit", Foods.BREAD); //烤猴面包果
    public static final DeferredItem<BaseFoodItem.BlockItem> BOULDER_BREAD = registerBlockItemFood("boulder_bread", builder -> builder.initialize().food(FoodType.BOULDER_BREAD).duration(d -> 48).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT), ModBlocks.BOULDER_BREAD_BLOCK); //巨石面包
    // 水果
    public static final DeferredItem<BaseFoodItem> APRICOT = registerNormalFood("apricot", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> BANANA = registerNormalFood("banana", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> CHERRY = registerNormalFood("cherry", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> COCONUT = registerNormalFood("coconut", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> DRAGON_FRUIT = registerNormalFood("dragon_fruit", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> GRAPE_FRUIT = registerNormalFood("grape_fruit", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> LEMON = registerNormalFood("lemon", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> MANGO = registerNormalFood("mango", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> PEACH = registerNormalFood("peach", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> PINEAPPLE = registerNormalFood("pineapple", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> PLUM = registerNormalFood("plum", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> GRAPE = registerNormalFood("grape", FoodType.WellFedProperties(6000));//葡萄
    public static final DeferredItem<BaseFoodItem> SPICY_PEPPER = registerNormalFood("spicy_pepper", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> STAR_FRUIT = registerNormalFood("star_fruit", FoodType.PlentySatisfiedProperties(6000));
    public static final DeferredItem<BaseFoodItem> POMEGRANATE = registerNormalFood("pomegranate", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> RAMBUTAN = registerNormalFood("rambutan", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> BLOOD_ORANGE = registerNormalFood("blood_orange", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> ELDERBERRY = registerNormalFood("elderberry", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> BLACKCURRANT = registerNormalFood("blackcurrant", FoodType.WellFedProperties(6000));
    public static final DeferredItem<BaseFoodItem> PEELED_SUGAR_TANGERINE = registerNormalFood("peeled_sugar_tangerine", FoodType.WellFedProperties(6000));
    //返还容器
    public static final DeferredItem<BaseFoodItem> FRUIT_JUICE = registerDrinkingFood("fruit_juice", FoodType.WellFedProperties(18000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //混合果汁
    public static final DeferredItem<BaseFoodItem> APPLE_JUICE = registerDrinkingFood("apple_juice", FoodType.WellFedProperties(12000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK);
    public static final DeferredItem<BaseFoodItem> FROZEN_BANANA_DAIQUIRI = registerDrinkingFood("frozen_banana_daiquiri", FoodType.WellFedProperties(18000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //香蕉圣代
    public static final DeferredItem<BaseFoodItem> GRAPE_JUICE = registerDrinkingFood("grape_juice", FoodType.GOLDEN_CARP, 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //葡萄汁
    public static final DeferredItem<BaseFoodItem> LEMONADE = registerDrinkingFood("lemonade", FoodType.WellFedProperties(12000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //柠檬水
    public static final DeferredItem<BaseFoodItem> PEACH_SANGRIA = registerDrinkingFood("peach_sangria", FoodType.WellFedProperties(12000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //桃子桑格利亚汽酒
    public static final DeferredItem<BaseFoodItem> PINA_COLADA = registerDrinkingFood("pina_colada", FoodType.WellFedProperties(24000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //皮尼亚·科拉达
    public static final DeferredItem<BaseFoodItem> PRISMATIC_PUNCH = registerDrinkingFood("prismatic_punch", FoodType.PlentySatisfiedProperties(24000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //味蕾冲击者
    public static final DeferredItem<BaseFoodItem> TROPICAL_SMOOTHIE = registerDrinkingFood("tropical_smoothie", FoodType.WellFedProperties(24000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //热带冰沙
    public static final DeferredItem<BaseFoodItem> SMOOTHIE_OF_DARKNESS = registerDrinkingFood("smoothie_of_darkness", FoodType.WellFedProperties(24000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //黑暗奶昔
    public static final DeferredItem<BaseFoodItem> BLOODY_MOSCATO = registerDrinkingFood("bloody_moscato", FoodType.WellFedProperties(24000, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK);
    public static final DeferredItem<BaseFoodItem> CREAM_SODA = registerDrinkingFood("cream_soda", FoodType.PlentySatisfiedProperties(19200, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //奶油苏打
    public static final DeferredItem<BaseFoodItem> ICE_CREAM = registerDrinkingFood("ice_cream", FoodType.PlentySatisfiedProperties(16800, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //冰淇淋
    public static final DeferredItem<BaseFoodItem> MILKSHAKE = registerDrinkingFood("milkshake", FoodType.ExquisitelyStuffedProperties(19200, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //奶昔
    public static final DeferredItem<BaseFoodItem> BUNNY_STEW = registerDrinkingFood("bunny_stew", FoodType.WellFedProperties(12000, BOWL), 20, UseAnim.DRINK, SoundEvents.GENERIC_DRINK, SoundEvents.GENERIC_DRINK); //炖兔兔
    //不返还容器
    public static final DeferredItem<BaseFoodItem> JOJA_COLA = registerDrinkingFood("joja_cola", FoodType.WellFedProperties(2400), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //乔家可乐
    public static final DeferredItem<BaseFoodItem> CARTON_OF_MILK = registerDrinkingFood("carton_of_milk", FoodType.WellFedProperties(24000), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //卡通牛奶
    public static final DeferredItem<BaseFoodItem> TEACUP = registerDrinkingFood("teacup", FoodType.WellFedProperties(6000), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //一小杯茶
    public static final DeferredItem<BaseFoodItem> COFFEE = registerDrinkingFood("coffee", FoodType.PlentySatisfiedProperties(12000), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //咖啡
    public static final DeferredItem<BaseFoodItem> SAKE = registerDrinkingFood("sake", FoodType.WellFedProperties(1200), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //清酒
    //鱼
    public static final DeferredItem<BaseFoodItem> GOLDFISH = registerNormalFood("goldfish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> SEA_BASS = registerNormalFood("sea_bass", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> ATLANTIC_COD = registerNormalFood("atlantic_cod", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> FROSTY_MINNOW = registerNormalFood("frosty_minnow", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> PISCES_FIN_COD = registerNormalFood("pisces_fin_cod", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> PARTIAL_MOUTH_FISH = registerNormalFood("partial_mouth_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> ROCK_LOBSTER = registerNormalFood("rock_lobster", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> SHRIMP = registerNormalFood("shrimp", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> TR_SALMON = registerNormalFood("tr_salmon", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> TUNA = registerNormalFood("tuna", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> RED_SNAPPER = registerNormalFood("red_snapper", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> TROUT = registerNormalFood("trout", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> ARMORED_CAVE_FISH = registerNormalFood("armored_cave_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> MIRROR_FISH = registerNormalFood("mirror_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> STINKY_FISH = registerNormalFood("stinky_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> NEON_GREASE_CARP = registerNormalFood("neon_grease_carp", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> DAMSEL_FISH = registerNormalFood("damsel_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> EBONY_KOI = registerNormalFood("ebony_koi", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> SCARLET_TIGER_FISH = registerNormalFood("scarlet_tiger_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> BLOODY_PIRANHAS = registerNormalFood("bloody_piranhas", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> PRINCESS_FISH = registerNormalFood("princess_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> COLORFUL_MINERAL_FISH = registerNormalFood("colorful_mineral_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> CHAOS_FISH = registerNormalFood("chaos_fish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> MOTTLED_OILFISH = registerNormalFood("mottled_oilfish", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> YELLOW_EEL = registerNormalFood("yellow_eel", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> TILAPIA = registerNormalFood("tilapia", FoodType.FISH);
    public static final DeferredItem<BaseFoodItem> GOLDEN_CARP = registerNormalFood("golden_carp", FoodType.GOLDEN_CARP);
    public static final DeferredItem<BaseFoodItem> OBSIDIAN_FISH = registerFood("obsidian_fish", builder -> builder.initialize().food(FoodType.FISH).isFireResistant());
    public static final DeferredItem<BaseFoodItem> FLASHFIN_KOI = registerFood("flashfin_koi", builder -> builder.initialize().food(FoodType.FISH).isFireResistant());

    //赞助
    public static final DeferredItem<BaseFoodItem> PINK_COLA = registerToolTipFood("pink_cola", builder -> builder.initialize().food(FoodType.PINK_COLA).drinkingSound(s -> SoundEvents.GENERIC_DRINK).duration(d -> 15).useAnim(u -> UseAnim.DRINK), 1);
    public static final DeferredItem<BaseFoodItem> DONGDONGS_FLATBREAD = registerToolTipFood("dongdongs_flatbread", builder -> builder.initialize().food(FoodType.DONGDONGS_FLATBREAD).eatingSound(s -> SoundEvents.GENERIC_EAT).duration(d -> 15).useAnim(u -> UseAnim.EAT), 1);
    //节日特有
    public static final DeferredItem<BaseFoodItem> ZONGZI = registerFood("zongzi", builder -> builder.initialize().food(FoodType.WellFedProperties(6000)).isFireResistant());

    public static final DeferredItem<BaseFoodItem> HONEY_MOONCAKES = registerFood("honey_mooncakes", builder -> builder.initialize().food(FoodType.PlentySatisfiedProperties(6000)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> HONEY_MOONCAKES_CHUNKS = registerFood("honey_mooncakes_chunks", builder -> builder.initialize().food(FoodType.MOONCAKES).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> EGG_YOLK_MOONCAKES = registerFood("egg_yolk_mooncakes", builder -> builder.initialize().food(FoodType.PlentySatisfiedProperties(6000)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> EGG_YOLK_MOONCAKES_CHUNKS = registerFood("egg_yolk_mooncakes_chunks", builder -> builder.initialize().food(FoodType.PlentySatisfiedProperties(6000)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());

    public static final DeferredItem<BaseFoodItem> LONGEVITY_NOODLES = registerNormalFood("longevity_noodles", FoodType.WellFedProperties(6000));

    public static final Supplier<BaseFoodItem.BlockItem> GREEN_DUMPLING = registerBlockItemFood("green_dumpling", builder -> builder.initialize().food(FoodType.GREEN_DUMPLING).duration(d -> 48).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT), ModBlocks.GREEN_DUMPLING_BLOCK);

    // 种子
    public static final Supplier<Item> STELLAR_BLOSSOM_SEED = ITEMS.register("stellar_blossom_seed", () -> new ItemNameBlockItem(NatureBlocks.STELLAR_BLOSSOM.get(), new Item.Properties()));
    public static final Supplier<Item> CLOUDWEAVER_SEED = ITEMS.register("cloudweaver_seed", () -> new ItemNameBlockItem(NatureBlocks.CLOUDWEAVER.get(), new Item.Properties()));
    public static final Supplier<Item> FLOATING_WHEAT_SEED = ITEMS.register("floating_wheat_seed", () -> new ItemNameBlockItem(NatureBlocks.FLOATING_WHEAT.get(), new Item.Properties()));
    public static final Supplier<Item> WATERLEAF_SEED = ITEMS.register("waterleaf_seed", () -> new HerbSeedItem(ModBlocks.WATERLEAF.get()));
    public static final Supplier<Item> FIREBLOSSOM_SEED = ITEMS.register("fireblossom_seed", () -> new HerbSeedItem(ModBlocks.FIREBLOSSOM.get(), new Item.Properties().fireResistant()));
    public static final Supplier<Item> MOONGLOW_SEED = ITEMS.register("moonglow_seed", () -> new HerbSeedItem(ModBlocks.MOONGLOW.get()));
    public static final Supplier<Item> BLINKROOT_SEED = ITEMS.register("blinkroot_seed", () -> new HerbSeedItem(ModBlocks.BLINKROOT.get()));
    public static final Supplier<Item> SHIVERTHORN_SEED = ITEMS.register("shiverthorn_seed", () -> new HerbSeedItem(ModBlocks.SHIVERTHORN.get()));
    public static final Supplier<Item> DAYBLOOM_SEED = ITEMS.register("daybloom_seed", () -> new HerbSeedItem(ModBlocks.DAYBLOOM.get()));
    public static final Supplier<Item> DEATHWEED_SEED = ITEMS.register("deathweed_seed", () -> new HerbSeedItem(ModBlocks.DEATHWEED.get()));

    public static DeferredItem<BaseFoodItem> registerFood(String name, Consumer<BaseFoodItem.Builder> consumer) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder();
            consumer.accept(builder);
            return builder.build();
        });
    }

    public static DeferredItem<BaseFoodItem.BlockItem> registerBlockItemFood(String name, Consumer<BaseFoodItem.Builder> consumer, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder();
            consumer.accept(builder);
            return new BaseFoodItem.BlockItem(block.get(), builder.getProperties());
        });
    }

    public static DeferredItem<BaseFoodItem> registerToolTipFood(String name, Consumer<BaseFoodItem.Builder> consumer, int line) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().tooltip(name, line);
            consumer.accept(builder);
            return builder.build();
        });
    }

    public static DeferredItem<BaseFoodItem> registerNormalFood(String name, FoodProperties foodProperties) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().initialize().food(foodProperties).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT);
            return builder.build();
        });
    }

    public static DeferredItem<BaseFoodItem> registerDrinkingFood(String name, FoodProperties foodProperties, int duration, UseAnim useAnim, SoundEvent drinkingSoundType, SoundEvent eatingSoundType) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().initialize().food(foodProperties).duration(d -> duration).useAnim(u -> useAnim).drinkingSound(s -> drinkingSoundType).eatingSound(e -> eatingSoundType);
            return builder.build();
        });
    }
}
