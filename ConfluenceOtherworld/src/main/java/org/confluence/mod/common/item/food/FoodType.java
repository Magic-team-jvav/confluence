package org.confluence.mod.common.item.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import org.confluence.mod.common.init.ModEffects;

public class FoodType {
    //低级，2:30
    public static final FoodProperties LOW = new FoodProperties.Builder().nutrition(2).saturationModifier(1.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //中级,5:00
    public static final FoodProperties MEDIUM = new FoodProperties.Builder().nutrition(3).saturationModifier(2.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 6000), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //中高级,7:30
    public static final FoodProperties SENIOR = new FoodProperties.Builder().nutrition(4).saturationModifier(3.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 9000), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //高级,10:00
    public static final FoodProperties HIGH = new FoodProperties.Builder().nutrition(6).saturationModifier(4.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 12000), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //常规肉
    public static final FoodProperties MEAT = new FoodProperties.Builder().nutrition(3).saturationModifier(2.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //泰拉基础鱼
    public static final FoodProperties FISH = new FoodProperties.Builder().nutrition(3).saturationModifier(1.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //金鲤鱼
    public static final FoodProperties GOLDEN_CARP = new FoodProperties.Builder().nutrition(6).saturationModifier(6.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 24000), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //云朵面包
    public static final FoodProperties CLOUD_BREAD = new FoodProperties.Builder().nutrition(3).saturationModifier(2.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 6000), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 300, 1), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //月饼块
    public static final FoodProperties MOONCAKES = new FoodProperties.Builder().nutrition(1).saturationModifier(1.5f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //粉色可乐
    public static final FoodProperties PINK_COLA = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200), 2.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f)
            .build();
    //东东的大饼
    public static final FoodProperties DONGDONGS_FLATBREAD = new FoodProperties.Builder().nutrition(3).saturationModifier(2.0f).fast().alwaysEdible()
            .effect(() -> new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false), 1.0f).build();
    //生命蘑菇
    public static final FoodProperties LIFE_MUSHROOM = new FoodProperties.Builder().nutrition(2).saturationModifier(0.25f).fast().alwaysEdible().build();
}
