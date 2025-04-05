package org.confluence.mod.common.item.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.confluence.mod.common.init.ModEffects;

public class FoodType {
    //常规熟肉
    public static FoodProperties preparedMeatProperties(int nutrition, float saturationModifier) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
                .build();
    }

    //泰拉基础鱼
    public static FoodProperties fishProperties(int nutrition, float saturationModifier) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible().build();
    }

    //常规生肉
    public static FoodProperties rawMeatProperties(int nutrition, float saturationModifier) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible().build();
    }

    //吃得好
    public static FoodProperties WellFedProperties(int duration, int nutrition, float saturationModifier) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration), 1.0f)
                .build();
    }

    //很满意
    public static FoodProperties PlentySatisfiedProperties(int duration, int nutrition, float saturationModifier) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 1), 1.0f)
                .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6), 1.0f)
                .build();
    }

    //吃饱喝足
    public static FoodProperties ExquisitelyStuffedProperties(int duration, int nutrition, float saturationModifier) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible()
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 2), 1.0f)
                .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6, 1), 1.0f)
                .build();
    }

    //吃得好 返还容器
    public static FoodProperties WellFedProperties(int duration, int nutrition, float saturationModifier, Item item) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible().usingConvertsTo(item)
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration), 1.0f)
                .build();
    }

    //很满意 返还容器
    public static FoodProperties PlentySatisfiedProperties(int duration, int nutrition, float saturationModifier, Item item) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible().usingConvertsTo(item)
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 1), 1.0f)
                .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6), 1.0f)
                .build();
    }

    //吃饱喝足 返还容器
    public static FoodProperties ExquisitelyStuffedProperties(int duration, int nutrition, float saturationModifier, Item item) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier).fast().alwaysEdible().usingConvertsTo(item)
                .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 2), 1.0f)
                .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6, 1), 1.0f)
                .build();
    }

    //金鲤鱼
    public static final FoodProperties GOLDEN_CARP = new FoodProperties.Builder().nutrition(8).saturationModifier(12.8f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 24000, 2), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400), 1.0F)
            .build();
    //云朵面包
    public static final FoodProperties CLOUD_BREAD = new FoodProperties.Builder().nutrition(5).saturationModifier(6f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 6000, 1), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 300, 1), 1.0F)
            .build();
    //巨石面包
    public static final FoodProperties BOULDER_BREAD = new FoodProperties.Builder().nutrition(20).saturationModifier(20.0f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.CHOKING, 6000), 1.0f)
            .build();
    //青团
    public static final FoodProperties GREEN_DUMPLING = new FoodProperties.Builder().nutrition(4).saturationModifier(1.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 6000, 1), 1.0f)
            .effect(() -> new MobEffectInstance(ModEffects.CHOKING, 2400), 1.0f)
            .effect(() -> new MobEffectInstance(ModEffects.HUNGER_DELAYED, 1000), 1.0f)
            .build();
    //月饼块
    public static final FoodProperties MOONCAKES = new FoodProperties.Builder().nutrition(1).saturationModifier(1.5f).fast().alwaysEdible().build();
    //粉色可乐
    public static final FoodProperties PINK_COLA = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200), 2.0f)
            .build();
    //东东的大饼
    public static final FoodProperties DONGDONGS_FLATBREAD = new FoodProperties.Builder().nutrition(5).saturationModifier(2.0f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
            .build();
    //生命蘑菇
    public static final FoodProperties LIFE_MUSHROOM = new FoodProperties.Builder().nutrition(2).saturationModifier(0.25f).fast().alwaysEdible().build();
}
