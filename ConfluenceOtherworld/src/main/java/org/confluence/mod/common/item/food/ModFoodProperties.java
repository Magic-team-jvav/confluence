package org.confluence.mod.common.item.food;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.confluence.mod.common.init.ModEffects;

import java.util.Arrays;

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
    public static FoodProperties hasEffectProperties(int nutrition, float saturation, EffectData... effects) {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).fast().alwaysEdible();
        Arrays.stream(effects).forEach(e -> builder.effect(() -> new MobEffectInstance(e.effect, e.duration, e.level), e.probability));
        return builder.build();
    }

    public record EffectData(Holder<MobEffect> effect, int duration, int level, float probability) {

        public static EffectData of(Holder<MobEffect> effect, int duration) {
            return new EffectData(effect, duration, 0, 1.0f);
        }

        public static EffectData of(Holder<MobEffect> effect, int duration, float probability) {
            return new EffectData(effect, duration, 0, probability);
        }

        public static EffectData of(Holder<MobEffect> effect, int duration, int level, float probability) {
            return new EffectData(effect, duration, level, probability);
        }
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
    //生命蘑菇
    public static final FoodProperties LIFE_MUSHROOM = new FoodProperties.Builder().nutrition(2).saturationModifier(0.0625F).fast().alwaysEdible().build();
}
