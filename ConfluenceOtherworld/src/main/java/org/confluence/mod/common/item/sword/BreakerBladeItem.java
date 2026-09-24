package org.confluence.mod.common.item.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTiers;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

public class BreakerBladeItem extends BaseSwordItem {
    public BreakerBladeItem() {
        super(ModTiers.UNBREAKABLE, ModRarity.LIGHT_RED, 37, 1.0F, SwordDefinition.builder()
                .specialSweep(0.8F)
                .tooltipImage()
                .attribute(Attributes.ENTITY_INTERACTION_RANGE, 9, PortAttributeModifier.Operation.ADD_VALUE)
                .attribute(Attributes.ATTACK_KNOCKBACK, 0.8F, PortAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public float modifyDamage(ItemStack stack, DamageSource source, @Nullable Entity attacker, LivingEntity victim, float amount) {
        /// 只放大对高血量目标的这一击，不改注册的基础伤害。
        return source.is(PortTags.DamageTypes.IS_PLAYER_ATTACK) && victim.getHealth() >= victim.getMaxHealth() * 0.9F ? amount * 2.5F : amount;
    }
}
