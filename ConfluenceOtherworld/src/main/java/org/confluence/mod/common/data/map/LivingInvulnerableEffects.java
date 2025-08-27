package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModDataMaps;

public record LivingInvulnerableEffects(HolderSet<MobEffect> effects) {
    public static final Codec<LivingInvulnerableEffects> CODEC = HolderSetCodec
            .create(Registries.MOB_EFFECT, MobEffect.CODEC, false)
            .xmap(LivingInvulnerableEffects::new, LivingInvulnerableEffects::effects);

    public static boolean isInvulnerableTo(LivingEntity living, Holder<MobEffect> effect) {
        LivingInvulnerableEffects data = living.getType().builtInRegistryHolder().getData(ModDataMaps.LIVING_INVULNERABLE_EFFECTS);
        return data != null && data.effects.contains(effect);
    }
}
