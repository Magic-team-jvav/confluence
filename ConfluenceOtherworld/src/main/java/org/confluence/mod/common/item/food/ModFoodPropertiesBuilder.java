package org.confluence.mod.common.item.food;

import it.unimi.dsi.fastutil.objects.ObjectFloatImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectFloatPair;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.IPortFoodProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModFoodPropertiesBuilder {
    private final List<ObjectFloatPair<Supplier<MobEffectInstance>>> effects = new ArrayList<>();

    private int nutrition;

    private float saturation;

    private boolean canAlwaysEat;

    private float eatSeconds;

    private @Nullable ItemStack usingConvertsTo;

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
        this.usingConvertsTo = usingConvertsTo;
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
        this.effects.add(new ObjectFloatImmutablePair<>(effect, probability));
        return this;
    }

    public ModFoodPropertiesBuilder addEffect(MobEffectInstance effect, float probability) {
        return addEffect(() -> effect, probability);
    }

    public ModFoodPropertiesBuilder addEffect(MobEffectInstance effect) {
        return addEffect(() -> effect, 1.0f);
    }

    public ModFoodPropertiesBuilder useCovertsTo(ItemLike usingConvertsTo) {
        this.usingConvertsTo = usingConvertsTo.asItem().getDefaultInstance();
        return this;
    }

    public FoodProperties build() {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationMod(saturation);
        if (canAlwaysEat) {
            builder.alwaysEat();
        }
        for (ObjectFloatPair<Supplier<MobEffectInstance>> pair : effects) {
            builder.effect(pair.first(), pair.rightFloat());
        }
        FoodProperties properties = builder.build();
        IPortFoodProperties i = IPortFoodProperties.of(properties);
        i.portlib$setEatSeconds(eatSeconds);
        i.portlib$setUsingConvertsTo(usingConvertsTo);
        return properties;
    }

    public record EffectData(MobEffect effect, int duration, int level, float probability) {
        public int amplifier() {
            return level;
        }

        public static EffectData of(MobEffect effect, int duration) {
            return new EffectData(effect, duration, 0, 1.0f);
        }

        public static EffectData of(MobEffect effect, int duration, float probability) {
            return new EffectData(effect, duration, 0, probability);
        }

        public static EffectData of(MobEffect effect, int duration, int level) {
            return new EffectData(effect, duration, level, 1.0f);
        }

        public static EffectData of(MobEffect effect, int duration, int level, float probability) {
            return new EffectData(effect, duration, level, probability);
        }
    }
}

