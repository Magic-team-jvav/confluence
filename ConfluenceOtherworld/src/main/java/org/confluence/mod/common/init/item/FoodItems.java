package org.confluence.mod.common.init.item;

import com.google.common.base.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.common.HerbSeedItem;
import org.confluence.mod.common.item.food.BaseFoodItem;
import org.confluence.mod.common.item.food.ModFoodProperties;
import org.confluence.mod.common.item.food.ModFoodPropertiesBuilder;
import org.confluence.mod.common.item.food.ModFoodPropertiesBuilder.EffectData;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.common.PortEffectCures;

import java.util.function.Consumer;

import static net.minecraft.world.item.Items.BOWL;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.item.food.ModFoodProperties.hasEffectProperties;

public class FoodItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    // 常规食物
    public static final PortDeferredItem<BaseFoodItem> COOKED_SHRIMP = registerNormalFood("cooked_shrimp", "熟虾", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f));
    public static final PortDeferredItem<BaseFoodItem> RAW_FROG = registerNormalFood("raw_frog", "生青蛙肉", ModRarity.GREEN, () -> ModFoodProperties.noEffectProperties(2, 1.8f));
    public static final PortDeferredItem<BaseFoodItem> RAW_SQUIRREL = registerNormalFood("raw_squirrel", "生松鼠肉", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 1.8f));
    public static final PortDeferredItem<BaseFoodItem> RAW_BIRD = registerNormalFood("raw_bird", "生鸟肉", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 1.8f));
    public static final PortDeferredItem<BaseFoodItem> RAW_DUCK = registerNormalFood("raw_duck", "生鸭肉", ModRarity.GREEN, () -> ModFoodProperties.noEffectProperties(3, 1.8f));
    public static final PortDeferredItem<BaseFoodItem> COOKED_FROG = registerNormalFood("cooked_frog", "熟青蛙肉", ModRarity.GREEN, () -> ModFoodProperties.noEffectProperties(4, 3.6f));
    public static final PortDeferredItem<BaseFoodItem> COOKED_SQUIRREL = registerNormalFood("cooked_squirrel", "熟松鼠肉", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(4, 3.6f));
    public static final PortDeferredItem<BaseFoodItem> COOKED_BIRD = registerNormalFood("cooked_bird", "熟鸟肉", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(4, 3.6f));
    public static final PortDeferredItem<BaseFoodItem> COOKED_DUCK = registerNormalFood("cooked_duck", "熟鸭肉", ModRarity.GREEN, () -> ModFoodProperties.noEffectProperties(6, 7.2f));
    public static final PortDeferredItem<BaseFoodItem> PINE_NUT = registerFood("pine_nut", "松子", builder -> builder.rarity(ModRarity.GREEN)
                    .food(ModFoodProperties.noEffectProperties(1, 1.5f))
                    .duration(d -> 8));
    public static final PortDeferredItem<BaseFoodItem> ROASTED_PINE_NUT = registerFood("roasted_pine_nut", "烤松子", builder -> builder.rarity(ModRarity.GREEN)
                    .food(ModFoodProperties.plentySatisfiedProperties(40, 2, 1.5f))
                    .duration(d -> 8));
    public static final PortDeferredItem<BaseFoodItem> ESCARGOT = registerNormalFood("escargot", "食用蜗牛", ModRarity.BLUE, () -> ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f));
    public static final PortDeferredItem<BaseFoodItem> FROGGLE_BUNWICH = registerNormalFood("froggle_bunwich", "蛙腿三明治", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(7200, 6, 3.5f));
    public static final PortDeferredItem<BaseFoodItem> GOLDEN_DELIGHT = registerNormalFood("golden_delight", "金美味", ModRarity.LIGHT_PURPLE, ModFoodProperties.GOLDEN_CARP); //金美味
    public static final PortDeferredItem<BaseFoodItem> GRILLED_SQUIRREL = registerNormalFood("grilled_squirrel", "烤松鼠", ModRarity.BLUE, () -> ModFoodProperties.preparedMeatProperties(8, 12.8f)); //松鼠尾
    public static final PortDeferredItem<BaseFoodItem> LOBSTER_TAIL = registerNormalFood("lobster_tail", "龙虾尾", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f)); //龙虾尾
    public static final PortDeferredItem<BaseFoodItem> MONSTER_LASAGNA = registerNormalFood("monster_lasagna", "怪物千层面", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(9600, 6, 3.5f)); //怪物千层面
    public static final PortDeferredItem<BaseFoodItem> COOK_FISH = registerNormalFood("cook_fish", "熟鱼", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(9600, 6, 3.5f)); //熟鱼
    public static final PortDeferredItem<BaseFoodItem> SASHIMI = registerNormalFood("sashimi", "生鱼片", ModRarity.WHITE, () -> ModFoodProperties.plentySatisfiedProperties(9600, 5, 3.0f)); //生鱼片
    public static final PortDeferredItem<BaseFoodItem> ROASTED_BIRD = registerNormalFood("roasted_bird", "烤鸟", ModRarity.BLUE, () -> ModFoodProperties.preparedMeatProperties(6, 3.6f)); //烤鸟
    public static final PortDeferredItem<BaseFoodItem> ROASTED_DUCK = registerNormalFood("roasted_duck", "烤鸭", ModRarity.GREEN, () -> ModFoodProperties.preparedMeatProperties(8, 7.2f)); //鸭肉
    public static final PortDeferredItem<BaseFoodItem> SAUTEED_FROG_LEGS = registerNormalFood("sauteed_frog_legs", "炒蛙腿", ModRarity.BLUE, () -> ModFoodProperties.preparedMeatProperties(8, 12.8f)); //爆炒青蛙腿
    public static final PortDeferredItem<BaseFoodItem> SEAFOOD_DINNER = registerNormalFood("seafood_dinner", "海鲜大餐", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(16800, 8, 12.8f)); //海鲜大餐
    public static final PortDeferredItem<BaseFoodItem> BACON = registerNormalFood("bacon", "培根", ModRarity.LIGHT_RED, () -> ModFoodProperties.exquisitelyStuffedProperties(28800, 8, 5.5f)); //培根
    public static final PortDeferredItem<BaseFoodItem> BANANA_SPLIT = registerNormalFood("banana_split", "香蕉船", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(12000, 6, 3.5f)); //香蕉船
    public static final PortDeferredItem<BaseFoodItem> BBQ_RIBS = registerNormalFoodWithEnglishName("bbq_ribs", "烧烤肋排", "BBQ Ribs", ModRarity.LIGHT_RED, () -> ModFoodProperties.exquisitelyStuffedProperties(28800, 8, 5.5f)); //烧烤肋排
    public static final PortDeferredItem<BaseFoodItem> BURGER = registerNormalFood("burger", "汉堡", ModRarity.ORANGE, () -> ModFoodProperties.exquisitelyStuffedProperties(9600, 8, 5.5f)); //汉堡
    public static final PortDeferredItem<BaseFoodItem> CHICKEN_NUGGET = registerNormalFood("chicken_nugget", "鸡块", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(16800, 6, 3.5f)); //鸡块
    public static final PortDeferredItem<BaseFoodItem> CHOCOLATE_CHIP_COOKIE = registerNormalFood("chocolate_chip_cookie", "巧克力曲奇饼干", ModRarity.ORANGE, () -> ModFoodProperties.plentySatisfiedProperties(24000, 6, 3.5f)); //巧克力大曲奇
    public static final PortDeferredItem<BaseFoodItem> FRIED_EGG = registerNormalFood("fried_egg", "煎蛋", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(16800, 6, 3.5f));//煎蛋
    public static final PortDeferredItem<BaseFoodItem> FRIES = registerNormalFood("fries", "炸薯条", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(12000, 6, 3.5f)); //炸薯条
    public static final PortDeferredItem<BaseFoodItem> HOTDOG = registerNormalFood("hotdog", "热狗", ModRarity.ORANGE, () -> ModFoodProperties.exquisitelyStuffedProperties(24000, 8, 5.5f)); //热狗
    public static final PortDeferredItem<BaseFoodItem> PIZZA = registerNormalFood("pizza", "披萨", ModRarity.ORANGE, () -> ModFoodProperties.exquisitelyStuffedProperties(9600, 8, 5.5f)); //披萨
    public static final PortDeferredItem<BaseFoodItem> POTATO_CHIPS = registerNormalFood("potato_chips", "薯片", ModRarity.GREEN, () -> ModFoodProperties.wellFedProperties(30000, 4, 1.5f)); //薯片
    public static final PortDeferredItem<BaseFoodItem> SHRIMP_PO_BOY = registerNormalFoodWithEnglishName("shrimp_po_boy", "鲜虾三明治", "Shrimp Po' Boy", ModRarity.ORANGE, () -> ModFoodProperties.plentySatisfiedProperties(21600, 6, 3.5f)); //鲜虾三明治
    public static final PortDeferredItem<BaseFoodItem> SHUCKED_OYSTER = registerNormalFood("shucked_oyster", "去壳牡蛎", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f)); //去壳牡蛎
    public static final PortDeferredItem<BaseFoodItem> SPAGHETTI = registerNormalFood("spaghetti", "意大利面", ModRarity.ORANGE, () -> ModFoodProperties.exquisitelyStuffedProperties(9600, 8, 5.5f)); //意大利面
    public static final PortDeferredItem<BaseFoodItem> SURPER_STEAK = registerNormalFood("surper_steak", "超大牛排", ModRarity.LIGHT_RED, () -> ModFoodProperties.exquisitelyStuffedProperties(19200, 8, 5.5f)); //超大肉排
    public static final PortDeferredItem<BaseFoodItem> APPLE_PIE = registerNormalFood("apple_pie", "苹果派", ModRarity.LIGHT_RED, () -> ModFoodProperties.exquisitelyStuffedProperties(19200, 8, 5.5f)); //苹果派
    public static final PortDeferredItem<BaseFoodItem> CHRISTMAS_PUDDING = registerNormalFood("christmas_pudding", "圣诞布丁", ModRarity.ORANGE, () -> ModFoodProperties.exquisitelyStuffedProperties(4800, 8, 5.5f)); //圣诞布丁
    public static final PortDeferredItem<BaseFoodItem> GINGERBREAD_COOKIE = registerNormalFood("gingerbread_cookie", "姜饼", ModRarity.ORANGE, () -> ModFoodProperties.exquisitelyStuffedProperties(4800, 8, 5.5f)); //姜饼人
    public static final PortDeferredItem<BaseFoodItem> SUGAR_COOKIE = registerNormalFood("sugar_cookie", "蜜糖饼干", ModRarity.ORANGE, () -> ModFoodProperties.exquisitelyStuffedProperties(4800, 8, 5.5f)); //糖曲奇
    public static final PortDeferredItem<BaseFoodItem> MARSHMALLOW = registerNormalFood("marshmallow", "棉花糖", ModRarity.WHITE, () -> ModFoodProperties.wellFedProperties(1200, 4, 1.5f)); //棉花糖
    public static final PortDeferredItem<BaseFoodItem> COOKED_MARSHMALLOW = registerNormalFood("cooked_marshmallow", "熟棉花糖", ModRarity.WHITE, () -> ModFoodProperties.wellFedProperties(2400, 4, 1.5f)); //烤棉花糖
    public static final PortDeferredItem<BaseFoodItem> PAD_THAI = registerNormalFood("pad_thai", "泰式炒面", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(9600, 6, 5.5f)); //泰式河粉
    public static final PortDeferredItem<BaseFoodItem> BOWL_OF_SOUP = registerDrinkingFoodWithEnglishName("bowl_of_soup", "鱼菇汤", "Bowl of Soup", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(9600, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //鱼菇汤
    public static final PortDeferredItem<BaseFoodItem> FRUIT_SALAD = registerDrinkingFood("fruit_salad", "水果沙拉", ModRarity.GREEN, () -> ModFoodProperties.wellFedProperties(30000, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //水果沙拉
    public static final PortDeferredItem<BaseFoodItem> GRUB_SOUP = registerDrinkingFood("grub_soup", "蛆虫汤", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(16000, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //蛆虫汤
    public static final PortDeferredItem<BaseFoodItem> NACHOS = registerDrinkingFood("nachos", "玉米片", ModRarity.ORANGE, () -> ModFoodProperties.plentySatisfiedProperties(19200, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //一碗玉米粒
    public static final PortDeferredItem<BaseFoodItem> PHO = registerDrinkingFood("pho", "越南河粉", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(12000, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //河粉
    public static final PortDeferredItem<BaseFoodItem> CLOUD_DOUGH = registerNormalFood("cloud_dough", "云朵面团", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(1200, 4, 1.5f)); // 粗制云朵面包
    public static final PortDeferredItem<BaseFoodItem> CLOUD_BREAD = registerNormalFood("cloud_bread", "云朵面包", ModRarity.BLUE, () -> hasEffectProperties(5, 0.6f,
                    EffectData.of(ModEffects.EXQUISITELY_STUFFED.get(), 6000, 1),
                    EffectData.of(MobEffects.SLOW_FALLING, 600, 1),
                    EffectData.of(MobEffects.LEVITATION, 300, 1))); // 云朵面包
    public static final PortDeferredItem<BaseFoodItem> FLUTTERING_LAMB_CHOPS = registerNormalFood("fluttering_lamb_chops", "飘飘羊排", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(3, 1.8f));
    public static final PortDeferredItem<BaseFoodItem> COOKED_FLUTTERING_LAMB_CHOPS = registerNormalFood("cooked_fluttering_lamb_chops", "熟飘飘羊排", ModRarity.BLUE, () -> ModFoodProperties.preparedMeatProperties(8, 12.8f));
    public static final PortDeferredItem<BaseFoodItem> BAOBAB_FRUIT = registerNormalFood("baobab_fruit", "猴面包果", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(3, 1.8f)); //猴面包果
    public static final PortDeferredItem<BaseFoodItem> COOKED_BAOBAB_FRUIT = registerNormalFood("cooked_baobab_fruit", "烤猴面包果", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(5, 3.8f));  //烤猴面包果
    public static final PortDeferredItem<BaseFoodItem.BItem> BOULDER_BREAD = registerBlockItemFood("boulder_bread",
            builder -> builder.rarity(ModRarity.GREEN)
                    .food(hasEffectProperties(20, 2.5f, EffectData.of(ModEffects.CHOKING.get(), 6000)))
                    .duration(d -> 48).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT), ModBlocks.BOULDER_BREAD); //巨石面包
    // 水果
    public static final PortDeferredItem<BaseFoodItem> APRICOT = registerNormalFood("apricot", "杏", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> BANANA = registerNormalFood("banana", "香蕉", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> CHERRY = registerNormalFood("cherry", "樱桃", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> COCONUT = registerNormalFood("coconut", "椰子", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> DRAGON_FRUIT = registerNormalFood("dragon_fruit", "火龙果", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f));
    public static final PortDeferredItem<BaseFoodItem> END_DRAGON_PEPPER = registerFood("end_dragon_pepper", "龙息辣椒", builder -> builder.rarity(ModRarity.BLUE)
                    .food(ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f))
                    .duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT));
    public static final PortDeferredItem<BaseFoodItem> GRAPE_FRUIT = registerNormalFoodWithEnglishName("grape_fruit", "葡萄柚", "Grapefruit", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> LEMON = registerNormalFood("lemon", "柠檬", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> MANGO = registerNormalFood("mango", "芒果", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> PEACH = registerNormalFood("peach", "桃子", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> PINEAPPLE = registerNormalFood("pineapple", "菠萝", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> PLUM = registerNormalFood("plum", "李子", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> GRAPE = registerNormalFood("grape", "葡萄", ModRarity.BLUE, () -> ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f));//葡萄
    public static final PortDeferredItem<BaseFoodItem> SPICY_PEPPER = registerNormalFood("spicy_pepper", "辣椒", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> STAR_FRUIT = registerNormalFood("star_fruit", "杨桃", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f));
    public static final PortDeferredItem<BaseFoodItem> POMEGRANATE = registerNormalFood("pomegranate", "石榴", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> RAMBUTAN = registerNormalFood("rambutan", "红毛丹", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> BLOOD_ORANGE = registerNormalFood("blood_orange", "血橙", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> ELDERBERRY = registerNormalFood("elderberry", "接骨木果", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> BLACKCURRANT = registerNormalFood("blackcurrant", "黑醋栗", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem> PEELED_SUGAR_TANGERINE = registerNormalFood("peeled_sugar_tangerine", "剥开的砂糖橘", ModRarity.RED, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));
    public static final PortDeferredItem<BaseFoodItem.BItem> SHIMMER_BERRIES = registerBlockItemFood("shimmer_berries",
            builder -> builder.rarity(ModRarity.GREEN)
                    .food(ModFoodProperties.noEffectProperties(3, 1.8f))
                    .duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT), NatureBlocks.SHIMMER_DROOPING_VINE);
    public static final Supplier<@Nullable ItemStack> GLASS_BOTTLE = Items.GLASS_BOTTLE::getDefaultInstance;
    //返还容器
    public static final PortDeferredItem<BaseFoodItem> FRUIT_JUICE = registerDrinkingFood("fruit_juice", "果汁", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(18000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //混合果汁
    public static final PortDeferredItem<BaseFoodItem> APPLE_JUICE = registerDrinkingFood("apple_juice", "苹果汁", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(12000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK);
    public static final PortDeferredItem<BaseFoodItem> FROZEN_BANANA_DAIQUIRI = registerDrinkingFood("frozen_banana_daiquiri", "冰冻香蕉代基里", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(18000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //香蕉圣代
    public static final PortDeferredItem<BaseFoodItem> GRAPE_JUICE = registerDrinkingFood("grape_juice", "葡萄汁", ModRarity.LIGHT_RED, ModFoodProperties.GOLDEN_CARP, 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //葡萄汁
    public static final PortDeferredItem<BaseFoodItem> LEMONADE = registerDrinkingFood("lemonade", "柠檬水", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(12000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //柠檬水
    public static final PortDeferredItem<BaseFoodItem> PEACH_SANGRIA = registerDrinkingFood("peach_sangria", "桃子果酒", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(12000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //桃子桑格利亚汽酒
    public static final PortDeferredItem<BaseFoodItem> PINA_COLADA = registerDrinkingFoodWithEnglishName("pina_colada", "椰林飘香", "Piña Colada", ModRarity.GREEN, () -> ModFoodProperties.wellFedProperties(24000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //皮尼亚·科拉达
    public static final PortDeferredItem<BaseFoodItem> PRISMATIC_PUNCH = registerDrinkingFood("prismatic_punch", "七彩潘趣酒", ModRarity.ORANGE, () -> ModFoodProperties.plentySatisfiedProperties(24000, 6, 3.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //味蕾冲击者
    public static final PortDeferredItem<BaseFoodItem> TROPICAL_SMOOTHIE = registerDrinkingFood("tropical_smoothie", "热带奶昔", ModRarity.GREEN, () -> ModFoodProperties.wellFedProperties(24000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //热带冰沙
    public static final PortDeferredItem<BaseFoodItem> SMOOTHIE_OF_DARKNESS = registerDrinkingFoodWithEnglishName("smoothie_of_darkness", "暗黑奶昔", "Smoothie of Darkness", ModRarity.GREEN, () -> ModFoodProperties.wellFedProperties(24000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //黑暗奶昔
    public static final PortDeferredItem<BaseFoodItem> BLOODY_MOSCATO = registerDrinkingFood("bloody_moscato", "血腥麝香葡萄", ModRarity.GREEN, () -> ModFoodProperties.wellFedProperties(24000, 4, 1.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK);
    public static final PortDeferredItem<BaseFoodItem> CREAM_SODA = registerDrinkingFood("cream_soda", "奶油苏打水", ModRarity.ORANGE, () -> ModFoodProperties.plentySatisfiedProperties(19200, 6, 3.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //奶油苏打
    public static final PortDeferredItem<BaseFoodItem> ICE_CREAM = registerDrinkingFood("ice_cream", "冰淇淋", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(16800, 6, 3.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //冰淇淋
    public static final PortDeferredItem<BaseFoodItem> MILKSHAKE = registerDrinkingFood("milkshake", "奶昔", ModRarity.LIGHT_RED, () -> ModFoodProperties.exquisitelyStuffedProperties(19200, 8, 5.5f, GLASS_BOTTLE), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //奶昔
    public static final PortDeferredItem<BaseFoodItem> BUNNY_STEW = registerDrinkingFood("bunny_stew", "炖兔兔", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(12000, 4, 1.5f, BOWL::getDefaultInstance), 20, UseAnim.DRINK, SoundEvents.GENERIC_DRINK, SoundEvents.GENERIC_DRINK); //炖兔兔
    //不返还容器
    public static final PortDeferredItem<BaseFoodItem> JOJA_COLA = registerDrinkingFood("joja_cola", "Joja可乐", ModRarity.WHITE, () -> ModFoodProperties.wellFedProperties(2400, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //乔家可乐
    public static final PortDeferredItem<BaseFoodItem> CARTON_OF_MILK = registerDrinkingFoodWithEnglishName("carton_of_milk", "盒装牛奶", "Carton of Milk", ModRarity.GREEN, () -> ModFoodProperties.wellFedProperties(24000, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK, builder -> builder.setFinishUsingCallback((stack, level, living) -> living.removeEffectsCuredBy(PortEffectCures.MILK))); //卡通牛奶
    public static final PortDeferredItem<BaseFoodItem> TEACUP = registerDrinkingFood("teacup", "一杯茶", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //一小杯茶
    public static final PortDeferredItem<BaseFoodItem> COFFEE = registerDrinkingFood("coffee", "咖啡", ModRarity.GREEN, () -> ModFoodProperties.plentySatisfiedProperties(12000, 6, 3.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //咖啡
    public static final PortDeferredItem<BaseFoodItem> SAKE = registerDrinkingFood("sake", "清酒", ModRarity.BLUE, () -> ModFoodProperties.wellFedProperties(1200, 4, 1.5f), 20, UseAnim.DRINK, SoundEvents.HONEY_DRINK, SoundEvents.HONEY_DRINK); //清酒
    //鱼
    public static final PortDeferredItem<BaseFoodItem> GOLDFISH = registerNormalFood("goldfish", "金鱼", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> GOLD_GOLDFISH = registerNormalFood("gold_goldfish", "金金鱼", ModRarity.ORANGE, ModFoodProperties.GOLDEN_CARP);
    public static final PortDeferredItem<BaseFoodItem> SEA_BASS = registerNormalFood("sea_bass", "鲈鱼", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> ATLANTIC_COD = registerNormalFood("atlantic_cod", "大西洋鳕鱼", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> FROSTY_MINNOW = registerNormalFood("frosty_minnow", "寒霜鲦鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> PISCES_FIN_COD = registerNormalFood("pisces_fin_cod", "双鳍鳕鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> PARTIAL_MOUTH_FISH = registerNormalFood("partial_mouth_fish", "偏口鱼", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> ROCK_LOBSTER = registerNormalFood("rock_lobster", "岩石龙虾", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> SHRIMP = registerNormalFood("shrimp", "虾", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> SALMON = registerNormalFood("salmon", "三文鱼", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> TUNA = registerNormalFood("tuna", "金枪鱼", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> RED_SNAPPER = registerNormalFood("red_snapper", "红鲷鱼", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> TROUT = registerNormalFood("trout", "鳟鱼", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> ARMORED_CAVE_FISH = registerNormalFoodWithEnglishName("armored_cave_fish", "装甲洞穴鱼", "Armored Cavefish", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> MIRROR_FISH = registerNormalFood("mirror_fish", "镜面鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> STINKY_FISH = registerNormalFood("stinky_fish", "臭味鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> NEON_GREASE_CARP = registerNormalFood("neon_grease_carp", "霓虹脂鲤", ModRarity.WHITE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> DAMSEL_FISH = registerNormalFoodWithEnglishName("damsel_fish", "雀鲷", "Damselfish", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> EBONY_KOI = registerNormalFood("ebony_koi", "黑檀锦鲤", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> SCARLET_TIGER_FISH = registerNormalFood("scarlet_tiger_fish", "猩红虎鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> BLOODY_PIRANHAS = registerNormalFood("bloody_piranhas", "血腥食人鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> PRINCESS_FISH = registerNormalFood("princess_fish", "公主鱼", ModRarity.GREEN, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> COLORFUL_MINERAL_FISH = registerNormalFood("colorful_mineral_fish", "七彩矿鱼", ModRarity.ORANGE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> CHAOS_FISH = registerNormalFood("chaos_fish", "混沌鱼", ModRarity.LIGHT_RED, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> MOTTLED_OILFISH = registerNormalFood("mottled_oilfish", "斑驳油鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> YELLOW_EEL = registerNormalFood("yellow_eel", "黄鳝", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> TILAPIA = registerNormalFood("tilapia", "罗非鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(2, 0.4f));
    public static final PortDeferredItem<BaseFoodItem> RED_PLEATFISH = registerNormalFood("red_pleatfish", "朱褶鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(4, 1.8f));
    public static final PortDeferredItem<BaseFoodItem> BROWN_STALKSPINE = registerNormalFood("brown_stalkspine", "棕柄脊鱼", ModRarity.BLUE, () -> ModFoodProperties.noEffectProperties(4, 1.8f));
    public static final PortDeferredItem<BaseFoodItem> GOLDEN_CARP = registerNormalFood("golden_carp", "金鲤鱼", ModRarity.LIGHT_RED, ModFoodProperties.GOLDEN_CARP);
    public static final PortDeferredItem<BaseFoodItem> OBSIDIFISH = registerFood("obsidifish", "黑曜石鱼", builder -> builder.rarity(ModRarity.WHITE).food(ModFoodProperties.noEffectProperties(2, 0.4f))
                    .duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final PortDeferredItem<BaseFoodItem> FLASHFIN_KOI = registerFood("flashfin_koi", "闪鳍锦鲤", builder -> builder.rarity(ModRarity.GREEN).food(ModFoodProperties.noEffectProperties(2, 0.4f))
                    .duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final PortDeferredItem<BaseFoodItem> HONEYFIN = registerFood("honeyfin", "蜂蜜鱼", builder -> builder.rarity(ModRarity.BLUE).food(ModFoodProperties.noEffectProperties(1, 1))
                    .setFinishUsingCallback((k, l, p) -> {
                        if (!l.isClientSide && !p.hasEffect(ModEffects.POTION_SICKNESS.get())) {
                            p.heal(24);
                            p.addEffect(new MobEffectInstance(ModEffects.POTION_SICKNESS.get(), 1200));
                        }
                    }));
    //赞助
    public static final PortDeferredItem<BaseFoodItem> PINK_COLA = registerFood("pink_cola", "粉色可乐", builder -> builder.rarity(ModRarity.EXPERT).food(hasEffectProperties(1, 0.5f,
                    EffectData.of(MobEffects.MOVEMENT_SLOWDOWN, 1200),
                    EffectData.of(MobEffects.REGENERATION, 1200))
            ).drinkingSound(s -> SoundEvents.GENERIC_DRINK).duration(d -> 15).useAnim(u -> UseAnim.DRINK));
    public static final PortDeferredItem<BaseFoodItem> DONGDONGS_FLATBREAD = registerFood("dongdongs_flatbread", "东东的大饼", builder -> builder.rarity(ModRarity.EXPERT).food(hasEffectProperties(5, 0.2f,
                    EffectData.of(ModEffects.EXQUISITELY_STUFFED.get(), 3000))
            ).eatingSound(s -> SoundEvents.GENERIC_EAT).duration(d -> 15).useAnim(u -> UseAnim.EAT));
    public static final PortDeferredItem<BaseFoodItem> PIGLIN_STEW = registerFood("piglin_stew", "猪灵煲", builder -> builder.rarity(ModRarity.EXPERT).food(hasEffectProperties(20, 80.0f,
                    EffectData.of(MobEffects.DAMAGE_RESISTANCE, 1200, 4))
            ).eatingSound(s -> SoundEvents.GENERIC_EAT).duration(d -> 15).useAnim(u -> UseAnim.EAT));
    //节日特有
    public static final PortDeferredItem<BaseFoodItem> ZONGZI = registerFood("zongzi", "粽子", builder -> builder.rarity(ModRarity.RED).food(ModFoodProperties.wellFedProperties(6000, 4, 1.5f)
            ).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final PortDeferredItem<BaseFoodItem> MEAT_STUFFED_ZONGZI = registerFood("meat_stuffed_zongzi", "肉粽", builder -> builder.rarity(ModRarity.RED).food(ModFoodProperties.wellFedProperties(6000, 5, 3f)
            ).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());

    public static final PortDeferredItem<BaseFoodItem> HONEY_MOONCAKES = registerFood("honey_mooncakes", "蜂蜜月饼", builder -> builder.rarity(ModRarity.RED).food(ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f)
            ).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final PortDeferredItem<BaseFoodItem> HONEY_MOONCAKES_CHUNKS = registerFood("honey_mooncakes_chunks", "小块蜂蜜月饼", builder -> builder.rarity(ModRarity.RED).food(ModFoodProperties.noEffectProperties(2, 1.25f)
            ).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final PortDeferredItem<BaseFoodItem> EGG_YOLK_MOONCAKES = registerFood("egg_yolk_mooncakes", "蛋黄月饼", builder -> builder.rarity(ModRarity.RED).food(ModFoodProperties.plentySatisfiedProperties(6000, 6, 3.5f)
            ).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());
    public static final PortDeferredItem<BaseFoodItem> EGG_YOLK_MOONCAKES_CHUNKS = registerFood("egg_yolk_mooncakes_chunks", "小块蛋黄月饼", builder -> builder.rarity(ModRarity.RED).food(ModFoodProperties.plentySatisfiedProperties(6000, 3, 1.75f)
            ).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT).isFireResistant());

    public static final PortDeferredItem<BaseFoodItem> LONGEVITY_NOODLES = registerNormalFood("longevity_noodles", "长寿面", ModRarity.RED, () -> ModFoodProperties.wellFedProperties(6000, 4, 1.5f));

    public static final PortDeferredItem<BaseFoodItem.BItem> GREEN_DUMPLING = registerBlockItemFood("green_dumpling",
            builder -> builder.rarity(ModRarity.RED).food(ModFoodProperties.plentySatisfiedProperties(6000, 3, 1.5f)
            ).duration(d -> 48).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT), ModBlocks.GREEN_DUMPLING);

    public static final PortDeferredItem<BaseFoodItem> HONEY_GUMMI = registerFood("honey_gummi", "蜜凝糖", builder -> builder.rarity(ModRarity.ORANGE).food(hasEffectProperties(12, 6.0f, new ModFoodPropertiesBuilder.EffectData(LibEffects.HONEY.get(), 200, 0, 1.0f)))
                    .duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT));

    public static final PortDeferredItem<BaseFoodItem> ICE_MELON_SLICE = registerFood("ice_melon_slice", "冰镇西瓜片", builder -> builder.rarity(ModRarity.BLUE)
            .food(ModFoodProperties.plentySatisfiedProperties(600, 3, 2.5f)).duration(d -> 10).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT));

    public static final PortDeferredItem<BaseFoodItem> COLDBLOOD_PUMPKIN_PIE = registerFood("coldblood_pumpkin_pie", "冷血南瓜派", builder -> builder.rarity(ModRarity.ORANGE).food(hasEffectProperties(8, 4.8f, new ModFoodPropertiesBuilder.EffectData(MobEffects.DAMAGE_BOOST, 2400, 0, 1.0f)))
                    .duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT));
    // 种子
    public static final PortDeferredItem<Item> STELLAR_BLOSSOM_SEED = registerItem("stellar_blossom_seed", "星辰花种子", () -> new ItemNameBlockItem(NatureBlocks.STELLAR_BLOSSOM.get(), new Item.Properties()));
    public static final PortDeferredItem<Item> CLOUDWEAVER_SEED = registerItem("cloudweaver_seed", "云织草种子", () -> new ItemNameBlockItem(NatureBlocks.CLOUDWEAVER.get(), new Item.Properties()));
    public static final PortDeferredItem<Item> FLOATING_WHEAT_SEED = registerItem("floating_wheat_seed", "飘飘麦种子", () -> new ItemNameBlockItem(NatureBlocks.FLOATING_WHEAT.get(), new Item.Properties()));
    public static final PortDeferredItem<Item> WATERLEAF_SEED = registerItem("waterleaf_seed", "水叶草种子", () -> new HerbSeedItem(NatureBlocks.WATERLEAF.get()));
    public static final PortDeferredItem<Item> FIREBLOSSOM_SEED = registerItem("fireblossom_seed", "火焰花种子", () -> new HerbSeedItem(NatureBlocks.FIREBLOSSOM.get(), new Item.Properties().fireResistant()));
    public static final PortDeferredItem<Item> MOONGLOW_SEED = registerItem("moonglow_seed", "月光草种子", () -> new HerbSeedItem(NatureBlocks.MOONGLOW.get()));
    public static final PortDeferredItem<Item> BLINKROOT_SEED = registerItem("blinkroot_seed", "闪耀根种子", () -> new HerbSeedItem(NatureBlocks.BLINKROOT.get()));
    public static final PortDeferredItem<Item> SHIVERTHORN_SEED = registerItem("shiverthorn_seed", "寒颤棘种子", () -> new HerbSeedItem(NatureBlocks.SHIVERTHORN.get()));
    public static final PortDeferredItem<Item> DAYBLOOM_SEED = registerItem("daybloom_seed", "太阳花种子", () -> new HerbSeedItem(NatureBlocks.DAYBLOOM.get()));
    public static final PortDeferredItem<Item> DEATHWEED_SEED = registerItem("deathweed_seed", "死亡草种子", () -> new HerbSeedItem(NatureBlocks.DEATHWEED.get()));
    public static final PortDeferredItem<Item> WHITE_PUMPKIN_SEED = registerItem("white_pumpkin_seed", "白南瓜种子", () -> new ItemNameBlockItem(NatureBlocks.WHITE_PUMPKIN_STEM.get(), new Item.Properties()));
    public static final PortDeferredItem<Item> BALLOON_SEED = registerItem("balloon_seed", "气球瓜种子", () -> new ItemNameBlockItem(NatureBlocks.BALLOON_STEM.get(), new Item.Properties()));
    public static final PortDeferredItem<Item> END_DRAGON_PEPPER_SEED = registerItem("end_dragon_pepper_seed", "龙息辣椒种子", () -> new ItemNameBlockItem(NatureBlocks.DRAGONS_BREATH_PEPPER.get(), new Item.Properties()));

    public static PortDeferredItem<BaseFoodItem> registerFood(String name, Consumer<BaseFoodItem.Builder> consumer) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().stackTo(64);
            consumer.accept(builder);
            return builder.build();
        });
    }

    public static PortDeferredItem<BaseFoodItem.BItem> registerBlockItemFood(String name, Consumer<BaseFoodItem.Builder> consumer, PortDeferredBlock<?> block) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().stackTo(64);
            consumer.accept(builder);
            return new BaseFoodItem.BItem(block.get(), builder.getProperties());
        });
    }

    public static PortDeferredItem<BaseFoodItem> registerNormalFood(String name, ModRarity rarity, Supplier<FoodProperties> foodProperties) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().rarity(rarity).stackTo(64).food(foodProperties.get()).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT);
            return builder.build();
        });
    }

    public static PortDeferredItem<BaseFoodItem> registerDrinkingFood(String name, ModRarity rarity, Supplier<FoodProperties> foodProperties, int duration, UseAnim useAnim, SoundEvent drinkingSoundType, SoundEvent eatingSoundType) {
        return registerDrinkingFood(name, rarity, foodProperties, duration, useAnim, drinkingSoundType, eatingSoundType, builder -> {});
    }

    public static PortDeferredItem<BaseFoodItem> registerDrinkingFood(String name, ModRarity rarity, Supplier<FoodProperties> foodProperties, int duration, UseAnim useAnim, SoundEvent drinkingSoundType, SoundEvent eatingSoundType, Consumer<BaseFoodItem.Builder> consumer) {
        return ITEMS.register(name, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder()
                    .rarity(rarity).stackTo(64).food(foodProperties.get())
                    .duration(d -> duration)
                    .useAnim(u -> useAnim)
                    .drinkingSound(s -> drinkingSoundType)
                    .eatingSound(e -> eatingSoundType);
            consumer.accept(builder);
            return builder.build();
        });
    }

    private static PortDeferredItem<BaseFoodItem> registerFood(String registryId, String chineseName, Consumer<BaseFoodItem.Builder> consumer) {
        PortDeferredItem<BaseFoodItem> entry = ITEMS.register(registryId, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().stackTo(64);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseFoodItem> registerNormalFood(String registryId, String chineseName, ModRarity rarity, Supplier<FoodProperties> foodProperties) {
        PortDeferredItem<BaseFoodItem> entry = ITEMS.register(registryId, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().rarity(rarity).stackTo(64).food(foodProperties.get()).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseFoodItem> registerDrinkingFood(String registryId, String chineseName, ModRarity rarity, Supplier<FoodProperties> foodProperties, int duration, UseAnim useAnim, SoundEvent drinkingSoundType, SoundEvent eatingSoundType) {
        PortDeferredItem<BaseFoodItem> entry = registerDrinkingFood(registryId, rarity, foodProperties, duration, useAnim, drinkingSoundType, eatingSoundType, builder -> {});
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseFoodItem> registerDrinkingFood(String registryId, String chineseName, ModRarity rarity, Supplier<FoodProperties> foodProperties, int duration, UseAnim useAnim, SoundEvent drinkingSoundType, SoundEvent eatingSoundType, Consumer<BaseFoodItem.Builder> consumer) {
        PortDeferredItem<BaseFoodItem> entry = ITEMS.register(registryId, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder()
                    .rarity(rarity).stackTo(64).food(foodProperties.get())
                    .duration(d -> duration)
                    .useAnim(u -> useAnim)
                    .drinkingSound(s -> drinkingSoundType)
                    .eatingSound(e -> eatingSoundType);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseFoodItem> registerNormalFoodWithEnglishName(String registryId, String chineseName, String englishName, ModRarity rarity, Supplier<FoodProperties> foodProperties) {
        PortDeferredItem<BaseFoodItem> entry = ITEMS.register(registryId, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder().rarity(rarity).stackTo(64).food(foodProperties.get()).duration(d -> 15).useAnim(u -> UseAnim.EAT).eatingSound(s -> SoundEvents.GENERIC_EAT);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseFoodItem> registerDrinkingFoodWithEnglishName(String registryId, String chineseName, String englishName, ModRarity rarity, Supplier<FoodProperties> foodProperties, int duration, UseAnim useAnim, SoundEvent drinkingSoundType, SoundEvent eatingSoundType) {
        PortDeferredItem<BaseFoodItem> entry = registerDrinkingFood(registryId, rarity, foodProperties, duration, useAnim, drinkingSoundType, eatingSoundType, builder -> {});
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }

    private static PortDeferredItem<BaseFoodItem> registerDrinkingFoodWithEnglishName(String registryId, String chineseName, String englishName, ModRarity rarity, Supplier<FoodProperties> foodProperties, int duration, UseAnim useAnim, SoundEvent drinkingSoundType, SoundEvent eatingSoundType, Consumer<BaseFoodItem.Builder> consumer) {
        PortDeferredItem<BaseFoodItem> entry = ITEMS.register(registryId, () -> {
            BaseFoodItem.Builder builder = BaseFoodItem.builder()
                    .rarity(rarity).stackTo(64).food(foodProperties.get())
                    .duration(d -> duration)
                    .useAnim(u -> useAnim)
                    .drinkingSound(s -> drinkingSoundType)
                    .eatingSound(e -> eatingSoundType);
            consumer.accept(builder);
            return builder.build();
        });
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
