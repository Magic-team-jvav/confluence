package org.confluence.mod.common.item.spear;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;
import software.bernie.geckolib.core.animation.EasingType;

public class StreamstrikeHalberdItem extends AbstractSpearItem {
    public StreamstrikeHalberdItem() {
        super(new Properties().attributes(PortItemAttributeModifiers.builder()
                .add(PortAttributesExtension.entityInteractionRange(), new PortAttributeModifier(ModItems.BASE_ENTITY_INTERACTION_RANGE_ID, 6, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND)
                .add(LibAttributes.getAttackDamage(), new PortAttributeModifier(ModItems.BASE_ATTACK_DAMAGE_ID, 7.5, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND)
                .add(PortAttributesExtension.swimSpeed(), new PortAttributeModifier(Confluence.asResource("base_swim_speed"), 0.5, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL), PortEquipmentSlotGroup.MAINHAND)
                .build()), ModRarity.BLUE, 15, 5, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.25, 6, EasingType.EASE_OUT_BACK),
                K.of(0.5, -16, EasingType.EASE_IN_EXPO),
                K.of(0.75, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        LibEntityUtils.knockBackA2B(owner, victim, 0.25, 0.2);
    }
}
