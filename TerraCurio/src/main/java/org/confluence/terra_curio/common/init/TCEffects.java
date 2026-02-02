package org.confluence.terra_curio.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.effect.PublicMobEffect;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.effect.GravitationEffect;
import org.confluence.terra_curio.common.effect.HoneyEffect;

public final class TCEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, TerraCurio.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> CONFUSED = EFFECTS.register("confused", () -> new PublicMobEffect(MobEffectCategory.HARMFUL, 0x8B008B));
    public static final DeferredHolder<MobEffect, MobEffect> GRAVITATION = EFFECTS.register("gravitation", GravitationEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> PALADINS_SHIELD = EFFECTS.register("paladins_shield", () -> new PublicMobEffect(MobEffectCategory.BENEFICIAL, 0x666666));
    public static final DeferredHolder<MobEffect, MobEffect> CEREBRAL_MINDTRICK = EFFECTS.register("cerebral_mindtrick", () -> new PublicMobEffect(MobEffectCategory.NEUTRAL, 0xFFA885).addAttributeModifier(TCAttributes.getCriticalChance(), TerraCurio.asResource("cerebral_mindtrick"), 0.04, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> HONEY = EFFECTS.register("honey", HoneyEffect::new);

    public static void healPerSecond(LivingEntity living, float amount) {
        if (living.level().getGameTime() % 20L == 0) {
            living.heal(amount);
        }
    }
}
