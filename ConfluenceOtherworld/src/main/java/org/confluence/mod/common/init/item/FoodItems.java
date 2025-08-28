package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.common.HerbSeedItem;
import org.confluence.mod.common.item.food.BaseFoodItem;
import org.confluence.mod.common.item.food.ModFoodProperties;
import org.confluence.mod.common.item.food.ModFoodProperties.EffectData;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.item.Items.BOWL;
import static net.minecraft.world.item.Items.GLASS_BOTTLE;

public class FoodItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    //常规食物
    public static final DeferredItem<BaseFoodItem> COOKED_SHRIMP = registerNormalFood("cooked_shrimp", ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f));
    public static final DeferredItem<BaseFoodItem> RAW_FROG = registerNormalFood("raw_frog", ModFoodProperties.noEffectProperties(2, 1.8f));
    public static final DeferredItem<BaseFoodItem> RAW_SQUIRREL = registerNormalFood("raw_squirrel", ModFoodProperties.noEffectProperties(2, 1.8f));
    public static final DeferredItem<BaseFoodItem> RAW_BIRD = registerNormalFood("raw_bird", ModFoodProperties.noEffectProperties(2, 1.8f));
    public static final DeferredItem<BaseFoodItem> RAW_DUCK = registerNormalFood("raw_duck", ModFoodProperties.noEffectProperties(3, 1.8f));
    public static final DeferredItem<BaseFoodItem> COOKED_FROG = registerNormalFood("cooked_frog", ModFoodProperties.noEffectProperties(4, 3.6f));
    public static final DeferredItem<BaseFoodItem> COOKED_SQUIRREL = registerNormalFood("cooked_squirrel", ModFoodProperties.noEffectProperties(4, 3.6f));
    public static final DeferredItem<BaseFoodItem> COOKED_BIRD = registerNormalFood("cooked_bird", ModFoodProperties.noEffectProperties(4, 3.6f));
    public static final DeferredItem<BaseFoodItem> COOKED_DUCK = registerNormalFood("cooked_duck", ModFoodProperties.noEffectProperties(6, 7.2f));
    public static final DeferredItem<BaseFoodItem> ESCARGOT = registerNormalFood("escargot", ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f));
    public static final DeferredItem<BaseFoodItem> FROGGLE_BUNWICH = registerNormalFood("froggle_bunwich", ModFoodProperties.PlentySatisfiedProperties(7200, 6, 3.5f));
    public static final DeferredItem<BaseFoodItem> GOLDEN_DELIGHT = registerNormalFood("golden_delight", ModFoodProperties.GOLDEN_CARP); //金美味
    public static final DeferredItem<BaseFoodItem> GRILLED_SQUIRREL = registerNormalFood("grilled_squirrel", ModFoodProperties.preparedMeatProperties(8, 12.8f)); //松鼠尾
    public static final DeferredItem<BaseFoodItem> LOBSTER_TAIL = registerNormalFood("lobster_tail", ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f)); //龙虾尾
    public static final DeferredItem<BaseFoodItem> MONSTER_LASAGNA = registerNormalFood("monster_lasagna", ModFoodProperties.PlentySatisfiedProperties(9600, 6, 3.5f)); //怪物千层面
    public static final DeferredItem<BaseFoodItem> COOK_FISH = registerNormalFood("cook_fish", ModFoodProperties.WellFedProperties(9600, 6, 3.5f)); //熟鱼
    public static final DeferredItem<BaseFoodItem> SASHIMI = registerNormalFood("sashimi", ModFoodProperties.PlentySatisfiedProperties(9600, 5, 3.0f)); //生鱼片
    public static final DeferredItem<BaseFoodItem> ROASTED_BIRD = registerNormalFood("roasted_bird", ModFoodProperties.preparedMeatProperties(8, 12.8f)); //烤鸟腿
    public static final DeferredItem<BaseFoodItem> ROASTED_DUCK = registerNormalFood("roasted_duck", ModFoodProperties.preparedMeatProperties(8, 12.8f)); //鸭肉
    public static final DeferredItem<BaseFoodItem> SAUTEED_FROG_LEGS = registerNormalFood("sauteed_frog_legs", ModFoodProperties.preparedMeatProperties(8, 12.8f)); //爆炒青蛙腿
    public static final DeferredItem<BaseFoodItem> SEAFOOD_DINNER = registerNormalFood("seafood_dinner", ModFoodProperties.PlentySatisfiedProperties(16800, 8, 12.8f)); //海鲜大餐
    public static final DeferredItem<BaseFoodItem> BACON = registerNormalFood("bacon", ModFoodProperties.ExquisitelyStuffedProperties(28800, 8, 5.5f)); //培根
    public static final DeferredItem<BaseFoodItem> BANANA_SPLIT = registerNormalFood("banana_split", ModFoodProperties.PlentySatisfiedProperties(12000, 6, 3.5f)); //香蕉船
    public static final DeferredItem<BaseFoodItem> BBQ_RIBS = registerNormalFood("bbq_ribs", ModFoodProperties.ExquisitelyStuffedProperties(28800, 8, 5.5f)); //烧烤肋排
    public static final DeferredItem<BaseFoodItem> BURGER = registerNormalFood("burger", ModFoodProperties.ExquisitelyStuffedProperties(9600, 8, 5.5f)); //汉堡
    public static final DeferredItem<BaseFoodItem> CHICKEN_NUGGET = registerNormalFood("chicken_nugget", ModFoodProperties.PlentySatisfiedProperties(16800, 6, 3.5f)); //鸡块
    public static final DeferredItem<BaseFoodItem> CHOCOLATE_CHIP_COOKIE = registerNormalFood("chocolate_chip_cookie", ModFoodProperties.PlentySatisfiedProperties(24000, 6, 3.5f)); //巧克力大曲奇
    public static final DeferredItem<BaseFoodItem> FRIED_EGG = registerNormalFood("fried_egg", ModFoodProperties.PlentySatisfiedProperties(16800, 6, 3.5f));//煎蛋
    public static final DeferredItem<BaseFoodItem> FRIES = registerNormalFood("fries", ModFoodProperties.PlentySatisfiedProperties(12000, 6, 3.5f)); //炸薯条
    public static final DeferredItem<BaseFoodItem> HOTDOG = registerNormalFood("hotdog", ModFoodProperties.ExquisitelyStuffedProperties(24000, 8, 5.5f)); //热狗
    public static final DeferredItem<BaseFoodItem> PIZZA = registerNormalFood("pizza", ModFoodProperties.ExquisitelyStuffedProperties(9600, 8, 5.5f)); //披萨
    public static final DeferredItem<BaseFoodItem> POTATO_CHIPS = registerNormalFood("potato_chips", ModFoodProperties.WellFedProperties(30000, 4, 1.5f)); //薯片
    public static final DeferredItem<BaseFoodItem> SHRIMP_PO_BOY = registerNormalFood("shrimp_po_boy", ModFoodProperties.PlentySatisfiedProperties(21600, 6, 3.5f)); //鲜虾三明治
    public static final DeferredItem<BaseFoodItem> SHUCKED_OYSTER = registerNormalFood("shucked_oyster", ModFoodProperties.WellFedProperties(6000, 4, 1.5f)); //去壳牡蛎
    public static final DeferredItem<BaseFoodItem> SPAGHETTI = registerNormalFood("spaghetti", ModFoodProperties.ExquisitelyStuffedProperties(9600, 8, 5.5f)); //意大利面
    public static final DeferredItem<BaseFoodItem> SURPER_STEAK = registerNormalFood("surper_steak", ModFoodProperties.ExquisitelyStuffedProperties(19200, 8, 5.5f)); //超大肉排
    public static final DeferredItem<BaseFoodItem> APPLE_PIE = registerNormalFood("apple_pie", ModFoodProperties.ExquisitelyStuffedProperties(19200, 8, 5.5f)); //苹果派
    public static final DeferredItem<BaseFoodItem> CHRISTMAS_PUDDING = registerNormalFood("christmas_pudding", ModFoodProperties.ExquisitelyStuffedProperties(4800, 8, 5.5f)); //圣诞布丁
    public static final DeferredItem<BaseFoodItem> GINGERBREAD_COOKIE = registerNormalFood("gingerbread_cookie", ModFoodProperties.ExquisitelyStuffedProperties(4800, 8, 5.5f)); //姜饼人
    public static final DeferredItem<BaseFoodItem> SUGAR_COOKIE = registerNormalFood("sugar_cookie", ModFoodProperties.ExquisitelyStuffedProperties(4800, 8, 5.5f)); //糖曲奇
    public static final DeferredItem<BaseFoodItem> MARSHMALLOW = registerNormalFood("marshmallow", ModFoodProperties.WellFedProperties(1200, 4, 1.5f)); //棉花糖
    public static final DeferredItem<BaseFoodItem> COOKED_MARSHMALLOW = registerNormalFood("cooked_marshmallow", ModFoodProperties.WellFedProperties(2400, 4, 1.5f)); //烤棉花糖
    public static final DeferredItem<BaseFoodItem> PAD_THAI = registerNormalFood("pad_thai", ModFoodProperties.PlentySatisfiedProperties(9600, 6, 3.5f)); //泰式河粉
    public static final DeferredItem<BaseFoodItem> BOWL_OF_SOUP = registerDrinkingFood("bowl_of_soup", ModFoodProperties.PlentySatisfiedProperties(9600, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //鱼菇汤
    public static final DeferredItem<BaseFoodItem> FRUIT_SALAD = registerDrinkingFood("fruit_salad", ModFoodProperties.WellFedProperties(30000, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //水果沙拉
    public static final DeferredItem<BaseFoodItem> GRUB_SOUP = registerDrinkingFood("grub_soup", ModFoodProperties.PlentySatisfiedProperties(16000, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //蛆虫汤
    public static final DeferredItem<BaseFoodItem> NACHOS = registerDrinkingFood("nachos", ModFoodProperties.PlentySatisfiedProperties(19200, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //一碗玉米粒
    public static final DeferredItem<BaseFoodItem> PHO = registerDrinkingFood("pho", ModFoodProperties.PlentySatisfiedProperties(12000, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //河粉
    public static final DeferredItem<BaseFoodItem> CLOUD_DOUGH = registerNormalFood("cloud_dough", ModFoodProperties.WellFedProperties(1200, 4, 1.5f)); // 粗制云朵面包
    public static final DeferredItem<BaseFoodItem> CLOUD_BREAD = registerNormalFood("cloud_bread", ModFoodProperties.hasEffectProperties(5, 0.6f,
            EffectData.of(ModEffects.EXQUISITELY_STUFFED, 6000, 1),
            EffectData.of(MobEffects.SLOW_FALLING, 600, 1),
            EffectData.of(MobEffects.LEVITATION, 300, 1))); // 云朵面包
    public static final DeferredItem<BaseFoodItem> FLUTTERING_LAMB_CHOPS = registerNormalFood("fluttering_lamb_chops", ModFoodProperties.noEffectProperties(3, 1.8f));
    public static final DeferredItem<BaseFoodItem> COOKED_FLUTTERING_LAMB_CHOPS = registerNormalFood("cooked_fluttering_lamb_chops", ModFoodProperties.preparedMeatProperties(8, 12.8f));
    public static final DeferredItem<BaseFoodItem> BAOBAB_FRUIT = registerNormalFood("baobab_fruit", ModFoodProperties.noEffectProperties(3, 1.8f)); //猴面包果
    public static final DeferredItem<BaseFoodItem> COOKED_BAOBAB_FRUIT = registerNormalFood("cooked_baobab_fruit", ModFoodProperties.noEffectProperties(5, 3.8f));  //烤猴面包果
    public static final DeferredItem<BaseFoodItem.BItem> BOULDER_BREAD = registerBlockItemFood("boulder_bread", builder -> builder.food(ModFoodProperties.hasEffectProperties(20, 2.5f, EffectData.of(ModEffects.CHOKING, 6000))).duration(d -> 48).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT), ModBlocks.BOULDER_BREAD_BLOCK); //巨石面包
    // 水果
    public static final DeferredItem<BaseFoodItem> APRICOT = registerNormalFood("apricot", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> BANANA = registerNormalFood("banana", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> CHERRY = registerNormalFood("cherry", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> COCONUT = registerNormalFood("coconut", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> DRAGON_FRUIT = registerNormalFood("dragon_fruit", ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f));
    public static final DeferredItem<BaseFoodItem> GRAPE_FRUIT = registerNormalFood("grape_fruit", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> LEMON = registerNormalFood("lemon", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> MANGO = registerNormalFood("mango", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> PEACH = registerNormalFood("peach", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> PINEAPPLE = registerNormalFood("pineapple", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> PLUM = registerNormalFood("plum", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> GRAPE = registerNormalFood("grape", ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f));//葡萄
    public static final DeferredItem<BaseFoodItem> SPICY_PEPPER = registerNormalFood("spicy_pepper", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> STAR_FRUIT = registerNormalFood("star_fruit", ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f));
    public static final DeferredItem<BaseFoodItem> POMEGRANATE = registerNormalFood("pomegranate", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> RAMBUTAN = registerNormalFood("rambutan", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> BLOOD_ORANGE = registerNormalFood("blood_orange", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> ELDERBERRY = registerNormalFood("elderberry", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> BLACKCURRANT = registerNormalFood("blackcurrant", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem> PEELED_SUGAR_TANGERINE = registerNormalFood("peeled_sugar_tangerine", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));
    public static final DeferredItem<BaseFoodItem.BItem> SHIMMER_BERRIES = registerBlockItemFood("shimmer_berries",
            builder -> builder
                    .food(ModFoodProperties.noEffectProperties(3, 1.8f))
                    .duration(d -> 15)
                    .useAnim(u -> UseAnim.EAT)
                    .eatingSound(s -> SoundEvents.GENERIC_EAT),
            NatureBlocks.SHIMMER_DROOPING_VINE);
    //返还容器
    public static final DeferredItem<BaseFoodItem> FRUIT_JUICE = registerDrinkingFood("fruit_juice", ModFoodProperties.WellFedProperties(18000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //混合果汁
    public static final DeferredItem<BaseFoodItem> APPLE_JUICE = registerDrinkingFood("apple_juice", ModFoodProperties.WellFedProperties(12000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK);
    public static final DeferredItem<BaseFoodItem> FROZEN_BANANA_DAIQUIRI = registerDrinkingFood("frozen_banana_daiquiri", ModFoodProperties.WellFedProperties(18000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //香蕉圣代
    public static final DeferredItem<BaseFoodItem> GRAPE_JUICE = registerDrinkingFood("grape_juice", ModFoodProperties.GOLDEN_CARP, 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //葡萄汁
    public static final DeferredItem<BaseFoodItem> LEMONADE = registerDrinkingFood("lemonade", ModFoodProperties.WellFedProperties(12000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //柠檬水
    public static final DeferredItem<BaseFoodItem> PEACH_SANGRIA = registerDrinkingFood("peach_sangria", ModFoodProperties.WellFedProperties(12000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //桃子桑格利亚汽酒
    public static final DeferredItem<BaseFoodItem> PINA_COLADA = registerDrinkingFood("pina_colada", ModFoodProperties.WellFedProperties(24000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //皮尼亚·科拉达
    public static final DeferredItem<BaseFoodItem> PRISMATIC_PUNCH = registerDrinkingFood("prismatic_punch", ModFoodProperties.PlentySatisfiedProperties(24000, 6, 3.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //味蕾冲击者
    public static final DeferredItem<BaseFoodItem> TROPICAL_SMOOTHIE = registerDrinkingFood("tropical_smoothie", ModFoodProperties.WellFedProperties(24000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //热带冰沙
    public static final DeferredItem<BaseFoodItem> SMOOTHIE_OF_DARKNESS = registerDrinkingFood("smoothie_of_darkness", ModFoodProperties.WellFedProperties(24000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //黑暗奶昔
    public static final DeferredItem<BaseFoodItem> BLOODY_MOSCATO = registerDrinkingFood("bloody_moscato", ModFoodProperties.WellFedProperties(24000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK);
    public static final DeferredItem<BaseFoodItem> CREAM_SODA = registerDrinkingFood("cream_soda", ModFoodProperties.PlentySatisfiedProperties(19200, 6, 3.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //奶油苏打
    public static final DeferredItem<BaseFoodItem> ICE_CREAM = registerDrinkingFood("ice_cream", ModFoodProperties.PlentySatisfiedProperties(16800, 6, 3.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //冰淇淋
    public static final DeferredItem<BaseFoodItem> MILKSHAKE = registerDrinkingFood("milkshake", ModFoodProperties.ExquisitelyStuffedProperties(19200, 8, 5.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //奶昔
    public static final DeferredItem<BaseFoodItem> BUNNY_STEW = registerDrinkingFood("bunny_stew", ModFoodProperties.WellFedProperties(12000, 4, 1.5f, BOWL), 20, UseAnim.DRINK, SoundEvents.GENERIC_DRINK, SoundEvents.GENERIC_DRINK); //炖兔兔
    //不返还容器
    public static final DeferredItem<BaseFoodItem> JOJA_COLA = registerDrinkingFood("joja_cola", ModFoodProperties.WellFedProperties(2400, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //乔家可乐
    public static final DeferredItem<BaseFoodItem> CARTON_OF_MILK = registerDrinkingFood("carton_of_milk", ModFoodProperties.WellFedProperties(24000, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //卡通牛奶
    public static final DeferredItem<BaseFoodItem> TEACUP = registerDrinkingFood("teacup", ModFoodProperties.WellFedProperties(6000, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //一小杯茶
    public static final DeferredItem<BaseFoodItem> COFFEE = registerDrinkingFood("coffee", ModFoodProperties.PlentySatisfiedProperties(12000, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //咖啡
    public static final DeferredItem<BaseFoodItem> SAKE = registerDrinkingFood("sake", ModFoodProperties.WellFedProperties(1200, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //清酒
    //鱼
    public static final DeferredItem<BaseFoodItem> GOLDFISH = registerNormalFood("goldfish", ModFoodProperties.GOLDEN_CARP);
    public static final DeferredItem<BaseFoodItem> SEA_BASS = registerNormalFood("sea_bass", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> ATLANTIC_COD = registerNormalFood("atlantic_cod", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> FROSTY_MINNOW = registerNormalFood("frosty_minnow", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> PISCES_FIN_COD = registerNormalFood("pisces_fin_cod", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> PARTIAL_MOUTH_FISH = registerNormalFood("partial_mouth_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> ROCK_LOBSTER = registerNormalFood("rock_lobster", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> SHRIMP = registerNormalFood("shrimp", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> SALMON = registerNormalFood("salmon", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> TUNA = registerNormalFood("tuna", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> RED_SNAPPER = registerNormalFood("red_snapper", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> TROUT = registerNormalFood("trout", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> ARMORED_CAVE_FISH = registerNormalFood("armored_cave_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> MIRROR_FISH = registerNormalFood("mirror_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> STINKY_FISH = registerNormalFood("stinky_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> NEON_GREASE_CARP = registerNormalFood("neon_grease_carp", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> DAMSEL_FISH = registerNormalFood("damsel_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> EBONY_KOI = registerNormalFood("ebony_koi", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> SCARLET_TIGER_FISH = registerNormalFood("scarlet_tiger_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> BLOODY_PIRANHAS = registerNormalFood("bloody_piranhas", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> PRINCESS_FISH = registerNormalFood("princess_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> COLORFUL_MINERAL_FISH = registerNormalFood("colorful_mineral_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> CHAOS_FISH = registerNormalFood("chaos_fish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> MOTTLED_OILFISH = registerNormalFood("mottled_oilfish", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> YELLOW_EEL = registerNormalFood("yellow_eel", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> TILAPIA = registerNormalFood("tilapia", ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final DeferredItem<BaseFoodItem> GOLDEN_CARP = registerNormalFood("golden_carp", ModFoodProperties.GOLDEN_CARP);
    public static final DeferredItem<BaseFoodItem> OBSIDIAN_FISH = registerFood("obsidian_fish", builder -> builder.food(ModFoodProperties.noEffectProperties(2, 0.4f)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> FLASHFIN_KOI = registerFood("flashfin_koi", builder -> builder.food(ModFoodProperties.noEffectProperties(2, 0.4f)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> HONEYFIN = registerFood("honeyfin", builder -> builder.food(new FoodProperties(1, 1, true, 1, Optional.empty(), Collections.singletonList(new FoodProperties.PossibleEffect(() -> new MobEffectInstance(ModEffects.POTION_SICKNESS, 1200), 1.0F)))).setFinishUsingCallback((k, v, s) -> {
        if (!s.level().isClientSide) s.heal(24);
    }));

    //赞助
    public static final DeferredItem<BaseFoodItem> PINK_COLA = registerToolTipFood("pink_cola", builder -> builder.food(ModFoodProperties.hasEffectProperties(1, 0.5f,
            EffectData.of(MobEffects.MOVEMENT_SLOWDOWN, 1200),
            EffectData.of(MobEffects.REGENERATION, 1200))
    ).drinkingSound(s -> SoundEvents.GENERIC_DRINK).duration(d -> 15).useAnim(u -> UseAnim.DRINK), 1, ChatFormatting.GRAY);
    public static final DeferredItem<BaseFoodItem> DONGDONGS_FLATBREAD = registerToolTipFood("dongdongs_flatbread", builder -> builder.food(ModFoodProperties.hasEffectProperties(5, 0.2f, EffectData.of(ModEffects.EXQUISITELY_STUFFED, 3000))).eatingSound(s -> SoundEvents.GENERIC_EAT).duration(d -> 15).useAnim(u -> UseAnim.EAT), 1, ChatFormatting.GRAY);
    public static final DeferredItem<BaseFoodItem> PIGLIN_STEW = registerToolTipFood("piglin_stew", builder -> builder.food(ModFoodProperties.hasEffectProperties(20, 80.0f, EffectData.of(MobEffects.DAMAGE_RESISTANCE, 1200, 4))).eatingSound(s -> SoundEvents.GENERIC_EAT).duration(d -> 15).useAnim(u -> UseAnim.EAT), 1, ChatFormatting.GRAY);
    //节日特有
    public static final DeferredItem<BaseFoodItem> ZONGZI = registerFood("zongzi", builder -> builder.food(ModFoodProperties.WellFedProperties(6000, 4, 1.5f)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> MEAT_STUFFED_ZONGZI = registerFood("meat_stuffed_zongzi", builder -> builder.food(ModFoodProperties.WellFedProperties(6000, 5, 3f)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());

    public static final DeferredItem<BaseFoodItem> HONEY_MOONCAKES = registerFood("honey_mooncakes", builder -> builder.food(ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> HONEY_MOONCAKES_CHUNKS = registerFood("honey_mooncakes_chunks", builder -> builder.food(ModFoodProperties.noEffectProperties(1, 0.75f)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> EGG_YOLK_MOONCAKES = registerFood("egg_yolk_mooncakes", builder -> builder.food(ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final DeferredItem<BaseFoodItem> EGG_YOLK_MOONCAKES_CHUNKS = registerFood("egg_yolk_mooncakes_chunks", builder -> builder.food(ModFoodProperties.PlentySatisfiedProperties(6000, 6, 3.5f)).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());

    public static final DeferredItem<BaseFoodItem> LONGEVITY_NOODLES = registerNormalFood("longevity_noodles", ModFoodProperties.WellFedProperties(6000, 4, 1.5f));

    public static final DeferredItem<BaseFoodItem.BItem> GREEN_DUMPLING = registerBlockItemFood("green_dumpling", builder -> builder.food(ModFoodProperties.PlentySatisfiedProperties(6000, 3, 1.5f)).duration(d -> 48).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT), ModBlocks.GREEN_DUMPLING_BLOCK);

    // 种子
    public static final DeferredItem<Item> STELLAR_BLOSSOM_SEED = ITEMS.register("stellar_blossom_seed", () -> new ItemNameBlockItem(NatureBlocks.STELLAR_BLOSSOM.get(), new Item.Properties()));
    public static final DeferredItem<Item> CLOUDWEAVER_SEED = ITEMS.register("cloudweaver_seed", () -> new ItemNameBlockItem(NatureBlocks.CLOUDWEAVER.get(), new Item.Properties()));
    public static final DeferredItem<Item> FLOATING_WHEAT_SEED = ITEMS.register("floating_wheat_seed", () -> new ItemNameBlockItem(NatureBlocks.FLOATING_WHEAT.get(), new Item.Properties()));
    public static final DeferredItem<Item> WATERLEAF_SEED = ITEMS.register("waterleaf_seed", () -> new HerbSeedItem(ModBlocks.WATERLEAF.get()));
    public static final DeferredItem<Item> FIREBLOSSOM_SEED = ITEMS.register("fireblossom_seed", () -> new HerbSeedItem(ModBlocks.FIREBLOSSOM.get(), new Item.Properties().fireResistant()));
    public static final DeferredItem<Item> MOONGLOW_SEED = ITEMS.register("moonglow_seed", () -> new HerbSeedItem(ModBlocks.MOONGLOW.get()));
    public static final DeferredItem<Item> BLINKROOT_SEED = ITEMS.register("blinkroot_seed", () -> new HerbSeedItem(ModBlocks.BLINKROOT.get()));
    public static final DeferredItem<Item> SHIVERTHORN_SEED = ITEMS.register("shiverthorn_seed", () -> new HerbSeedItem(ModBlocks.SHIVERTHORN.get()));
    public static final DeferredItem<Item> DAYBLOOM_SEED = ITEMS.register("daybloom_seed", () -> new HerbSeedItem(ModBlocks.DAYBLOOM.get()));
    public static final DeferredItem<Item> DEATHWEED_SEED = ITEMS.register("deathweed_seed", () -> new HerbSeedItem(ModBlocks.DEATHWEED.get()));

    public static DeferredItem<BaseFoodItem> registerFood(String name, Consumer<BaseFoodItem.Builder> consumer) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().stackTo(64);
            consumer.accept(builder);
            return builder.build();
        });
    }

    public static DeferredItem<BaseFoodItem.BItem> registerBlockItemFood(String name, Consumer<BaseFoodItem.Builder> consumer, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().stackTo(64);
            consumer.accept(builder);
            return new BaseFoodItem.BItem(block.get(), builder.getProperties());
        });
    }

    public static DeferredItem<BaseFoodItem> registerToolTipFood(String name, Consumer<BaseFoodItem.Builder> consumer, int line, ChatFormatting chatFormatting) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().stackTo(64).tooltip(name, line, chatFormatting);
            consumer.accept(builder);
            return builder.build();
        });
    }

    public static DeferredItem<BaseFoodItem> registerNormalFood(String name, FoodProperties foodProperties) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().stackTo(64).food(foodProperties).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT);
            return builder.build();
        });
    }

    public static DeferredItem<BaseFoodItem> registerDrinkingFood(String name, FoodProperties foodProperties, int duration, UseAnim useAnim, SoundEvent drinkingSoundType, SoundEvent eatingSoundType) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().stackTo(64).food(foodProperties).duration(d -> duration).useAnim(u -> useAnim).drinkingSound(s -> drinkingSoundType).eatingSound(e -> eatingSoundType);
            return builder.build();
        });
    }
    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        for (DeferredHolder<Item, ? extends Item> foods : ITEMS.getEntries()) tag.add(foods.get());
    }
}
