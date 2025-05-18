package org.confluence.mod.common.item.food;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.confluence.mod.common.init.ModEffects;

public class ModFoodProperties {
    public static float calcSaturationModifier(int nutrition, float rawSaturation) {
        return rawSaturation / nutrition / 2;
    }

    //常规熟肉
    public static FoodProperties preparedMeatProperties(int nutrition, float rawSaturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
                .build();
    }

    //无效果食物
    public static FoodProperties noEffectProperties(int nutrition, float rawSaturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible().build();
    }

    //自填效果食物
    public static FoodProperties hasEffectProperties(int nutrition, float rawSaturation, Holder<MobEffect> effects, int duration, int level) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(effects, duration, level), 1.0f)
                .build();
    }

    //吃得好
    public static FoodProperties WellFedProperties(int duration, int nutrition, float rawSaturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration), 1.0f)
                .build();
    }

    //很满意
    public static FoodProperties PlentySatisfiedProperties(int duration, int nutrition, float rawSaturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 1), 1.0f)
                .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6), 1.0f)
                .build();
    }

    //吃饱喝足
    public static FoodProperties ExquisitelyStuffedProperties(int duration, int nutrition, float rawSaturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 2), 1.0f)
                .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6, 1), 1.0f)
                .build();
    }

    //吃得好 返还容器
    public static FoodProperties WellFedProperties(int duration, int nutrition, float rawSaturation, Item item) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible().usingConvertsTo(item)
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration), 1.0f)
                .build();
    }

    //很满意 返还容器
    public static FoodProperties PlentySatisfiedProperties(int duration, int nutrition, float rawSaturation, Item item) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible().usingConvertsTo(item)
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 1), 1.0f)
                .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6), 1.0f)
                .build();
    }

    //吃饱喝足 返还容器
    public static FoodProperties ExquisitelyStuffedProperties(int duration, int nutrition, float rawSaturation, Item item) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(calcSaturationModifier(nutrition, rawSaturation)).fast().alwaysEdible().usingConvertsTo(item)
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 2), 1.0f)
                .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6, 1), 1.0f)
                .build();
    }

    //金鲤鱼
    public static final FoodProperties GOLDEN_CARP = new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 24000, 2), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400), 1.0F)
            .build();
    //云朵面包
    public static final FoodProperties CLOUD_BREAD = new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 6000, 1), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 300, 1), 1.0F)
            .build();
    //巨石面包
    public static final FoodProperties BOULDER_BREAD = new FoodProperties.Builder().nutrition(20).saturationModifier(2.5F).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.CHOKING, 6000), 1.0f)
            .build();
    //青团
    public static final FoodProperties GREEN_DUMPLING = new FoodProperties.Builder().nutrition(4).saturationModifier(0.1875F).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 6000, 1), 1.0f)
            .effect(() -> new MobEffectInstance(ModEffects.CHOKING, 2400), 1.0f)
            .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, 1000), 1.0f)
            .build();
    //月饼块
    public static final FoodProperties MOONCAKES = new FoodProperties.Builder().nutrition(1).saturationModifier(0.75F).fast().alwaysEdible().build();
    //粉色可乐
    public static final FoodProperties PINK_COLA = new FoodProperties.Builder().nutrition(1).saturationModifier(0.5F).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200), 2.0f)
            .build();
    //东东的大饼
    public static final FoodProperties DONGDONGS_FLATBREAD = new FoodProperties.Builder().nutrition(5).saturationModifier(0.2F).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
            .build();
    //生命蘑菇
    public static final FoodProperties LIFE_MUSHROOM = new FoodProperties.Builder().nutrition(2).saturationModifier(0.0625F).fast().alwaysEdible().build();
}
