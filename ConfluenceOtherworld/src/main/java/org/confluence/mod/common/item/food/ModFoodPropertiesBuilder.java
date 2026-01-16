package org.confluence.mod.common.item.food;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;
import java.util.function.Supplier;

public class ModFoodPropertiesBuilder {
    private final ImmutableList.Builder<FoodProperties.PossibleEffect> effects = ImmutableList.builder();

    private int nutrition;

    private float saturation;

    private boolean canAlwaysEat;

    private float eatSeconds;

    private Optional<ItemStack> usingConvertsTo = Optional.empty();

    public static ModFoodPropertiesBuilder Builder() {
        return new ModFoodPropertiesBuilder();
    }

    public ModFoodPropertiesBuilder nutrition(int nutrition) {
        this.nutrition = nutrition;
        return this;
    }

    public ModFoodPropertiesBuilder saturation(float saturation) {
        this.saturation = saturation;
        return this;
    }

    public ModFoodPropertiesBuilder alwaysEdible() {
        this.canAlwaysEat = true;
        return this;
    }

    public ModFoodPropertiesBuilder usingConvertsTo(ItemStack usingConvertsTo) {
        this.usingConvertsTo = Optional.of(usingConvertsTo);
        return this;
    }

    public ModFoodPropertiesBuilder fast() {
        this.eatSeconds = 0.8F;
        return this;
    }

    public ModFoodPropertiesBuilder eatSeconds(float eatSeconds) {
        this.eatSeconds = eatSeconds;
        return this;
    }

    public ModFoodPropertiesBuilder addEffect(Supplier<MobEffectInstance> effect, float probability) {
        this.effects.add(new FoodProperties.PossibleEffect(effect, probability));
        return this;
    }

    public ModFoodPropertiesBuilder addEffect(MobEffectInstance effect, float probability) {
        return addEffect(() -> effect, probability);
    }

    public ModFoodPropertiesBuilder addEffect(MobEffectInstance effect) {
        return addEffect(() -> effect, 1.0f);
    }

    public ModFoodPropertiesBuilder useCovertsTo(ItemLike usingConvertsTo) {
        this.usingConvertsTo = Optional.of(new ItemStack(usingConvertsTo));
        return this;
    }

    public FoodProperties build() {
        return new FoodProperties(nutrition, saturation, canAlwaysEat, eatSeconds, usingConvertsTo, effects.build());
    }

    public record EffectData(Holder<MobEffect> effect, int duration, int level, float probability) {
        public int amplifier() {
            return level;
        }

        public static EffectData of(Holder<MobEffect> effect, int duration) {
            return new EffectData(effect, duration, 0, 1.0f);
        }

        public static EffectData of(Holder<MobEffect> effect, int duration, float probability) {
            return new EffectData(effect, duration, 0, probability);
        }

        public static EffectData of(Holder<MobEffect> effect, int duration, int level) {
            return new EffectData(effect, duration, level, 1.0f);
        }

        public static EffectData of(Holder<MobEffect> effect, int duration, int level, float probability) {
            return new EffectData(effect, duration, level, probability);
        }
    }
}

