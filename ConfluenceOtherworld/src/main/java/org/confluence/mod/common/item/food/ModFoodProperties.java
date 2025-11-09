package org.confluence.mod.common.item.food;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.confluence.mod.common.init.ModEffects;

import java.util.Arrays;

public class ModFoodProperties {
    // 常规熟肉
    public static FoodProperties preparedMeatProperties(int nutrition, float saturation) {
        return ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible()
            .addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 3000), 1.0f)
            .build();
    }

    // 无效果食物
    public static FoodProperties noEffectProperties(int nutrition, float saturation) {
        return ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible()
            .build();
    }

    // 自定义效果食物
    public static FoodProperties hasEffectProperties(int nutrition, float saturation, ModFoodPropertiesBuilder.EffectData... effects) {
        ModFoodPropertiesBuilder builder = ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible();
        Arrays.stream(effects).forEach(e -> builder.addEffect(new MobEffectInstance(e.effect(), e.duration(), e.level()), e.probability()));
        return builder.build();
    }

    // 吃得好
    public static FoodProperties wellFedProperties(int duration, int nutrition, float saturation) {
        return ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible()
            .addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration), 1.0f)
            .build();
    }

    // 很满意
    public static FoodProperties plentySatisfiedProperties(int duration, int nutrition, float saturation) {
        return ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible()
            .addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 1), 1.0f)
            .addEffect(new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6), 1.0f)
            .build();
    }

    // 吃饱喝足
    public static FoodProperties exquisitelyStuffedProperties(int duration, int nutrition, float saturation) {
        return ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible()
            .addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 2), 1.0f)
            .addEffect(new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6, 1), 1.0f)
            .build();
    }

    // 带容器返还的版本
    public static FoodProperties wellFedProperties(int duration, int nutrition, float saturation, ItemLike item) {
        return ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible()
            .useCovertsTo(item)
            .addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration), 1.0f)
            .build();
    }

    public static FoodProperties plentySatisfiedProperties(int duration, int nutrition, float saturation, ItemLike item) {
        return ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible()
            .useCovertsTo(item)
            .addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 1), 1.0f)
            .addEffect(new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6), 1.0f)
            .build();
    }

    public static FoodProperties exquisitelyStuffedProperties(int duration, int nutrition, float saturation, ItemLike item) {
        return ModFoodPropertiesBuilder.Builder()
            .nutrition(nutrition)
            .saturation(saturation)
            .fast()
            .alwaysEdible()
            .useCovertsTo(item)
            .addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, duration, 2), 1.0f)
            .addEffect(new MobEffectInstance(ModEffects.HUNGER_DELAYED, duration / 6, 1), 1.0f)
            .build();
    }

    // 金鲤鱼
    public static final FoodProperties GOLDEN_CARP = ModFoodPropertiesBuilder.Builder()
        .nutrition(8)
        .saturation(0.8F)
        .fast()
        .alwaysEdible()
        .addEffect(new MobEffectInstance(ModEffects.EXQUISITELY_STUFFED, 24000, 2), 1.0f)
        .addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
        .addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400), 1.0F)
        .build();

    // 生命蘑菇
    public static final FoodProperties LIFE_MUSHROOM = ModFoodPropertiesBuilder.Builder()
        .nutrition(2)
        .saturation(0.0625F)
        .fast()
        .alwaysEdible()
        .build();
}