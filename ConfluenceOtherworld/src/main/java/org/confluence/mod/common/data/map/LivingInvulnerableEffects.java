package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModDataMaps;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public record LivingInvulnerableEffects(HolderSet<MobEffect> effects, List<Category> categories) {
    public static final Codec<LivingInvulnerableEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HolderSetCodec.create(Registries.MOB_EFFECT, MobEffect.CODEC, false).fieldOf("effects").forGetter(LivingInvulnerableEffects::effects),
            Category.CODEC.listOf().lenientOptionalFieldOf("category", List.of()).forGetter(LivingInvulnerableEffects::categories)
    ).apply(instance, LivingInvulnerableEffects::new));

    public LivingInvulnerableEffects(HolderSet<MobEffect> effects, Category... categories) {
        this(effects, Arrays.stream(categories).toList());
    }

    public static boolean isInvulnerableTo(LivingEntity living, Holder<MobEffect> effect) {
        LivingInvulnerableEffects data = living.getType().builtInRegistryHolder().getData(ModDataMaps.LIVING_INVULNERABLE_EFFECTS);
        return data != null && data.effects.contains(effect) && data.categories.stream().anyMatch(category -> category.is(effect));
    }

    public enum Category implements StringRepresentable {
        BENEFICIAL(MobEffectCategory.BENEFICIAL),
        HARMFUL(MobEffectCategory.HARMFUL),
        NEUTRAL(MobEffectCategory.NEUTRAL);

        public static final Codec<Category> CODEC = StringRepresentable.fromEnum(Category::values);

        private final MobEffectCategory value;

        Category(MobEffectCategory value) {
            this.value = value;
        }

        public boolean is(Holder<MobEffect> effect) {
            return value == effect.value().getCategory();
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
