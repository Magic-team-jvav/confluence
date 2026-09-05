package org.confluence.mod.common.data.map;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.mod.common.init.ModDataMaps;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public record LivingInvulnerableEffects(HolderSet<MobEffect> effects, List<Category> categories) {
    public static final Codec<LivingInvulnerableEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HolderSetCodec.create(Registries.MOB_EFFECT, MobEffect.CODEC, false).fieldOf("effects").forGetter(LivingInvulnerableEffects::effects),
            PortCodecExtension.lenientOptionalFieldOf(Category.CODEC.listOf(), "category", List.of()).forGetter(LivingInvulnerableEffects::categories)
    ).apply(instance, LivingInvulnerableEffects::new));

    public LivingInvulnerableEffects(HolderSet<MobEffect> effects, Category... categories) {
        this(effects, Arrays.stream(categories).toList());
    }

    public static boolean isInvulnerableTo(LivingEntity living, MobEffect effect) {
        LivingInvulnerableEffects data = ModDataMaps.getEntityData(ModDataMaps.LIVING_INVULNERABLE_EFFECTS, living);
        return data != null &&
                data.effects.contains(ForgeRegistries.MOB_EFFECTS.getDelegateOrThrow(effect)) &&
                (data.categories.isEmpty() || data.categories.stream().anyMatch(category -> category.is(effect)));
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

        public boolean is(MobEffect effect) {
            return value == effect.getCategory();
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
