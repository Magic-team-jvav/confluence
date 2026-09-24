package org.confluence.mod.common.item.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.SwordProjectileAppearance;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.component.SwordProjectileParticleEffect;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.generation.variant.ForwardGeneration;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.List;
import java.util.Optional;

public class BladeOfGrassItem extends BaseSwordItem {
    public BladeOfGrassItem() {
        super(ModTiers.UNBREAKABLE, ModRarity.GREEN, 10, 2.0F, SwordDefinition.builder()
                .projectile(new SwordProjectileComponent(0.25F, 0.8F, 0.9F, 20, 0.0F, 10,
                        ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.GRASS.getId(), Optional.empty(), ForwardGeneration.of(0.0F, 20.0F),
                        SwordProjectileAppearance.Hidden.INSTANCE,
                        List.of(SwordProjectileParticleEffect.particle(SwordProjectileParticleEffect.Event.TRAIL, ModParticleTypes.LEAVES.get(), 2, 1, 0.0F, 0.0F))))
                .tooltipImage()
                .attribute(Attributes.ENTITY_INTERACTION_RANGE, 2, PortAttributeModifier.Operation.ADD_VALUE)
                .specialSweep(0.8F));
    }

    @Override
    protected void onDamage(ItemStack weapon, LivingEntity attacker, LivingEntity victim, DamageSource source) {
        if (attacker.getRandom().nextFloat() < 0.25F) {
            victim.addEffect(new MobEffectInstance(MobEffects.POISON, 140, 0), attacker);
        }
    }
}
