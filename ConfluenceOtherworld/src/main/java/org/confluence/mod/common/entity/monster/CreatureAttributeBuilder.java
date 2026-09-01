package org.confluence.mod.common.entity.monster;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.LibAttributes;

/// 本体生物注册共用的属性模板构建器。
public final class CreatureAttributeBuilder extends AttributeSupplier.Builder {
    private CreatureAttributeBuilder(AttributeSupplier attributes) {
        super(attributes);
    }

    public static CreatureAttributeBuilder creature(double health, double armor, double attack, double followRange, double attackKnockback, double knockbackResistance) {
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(LibAttributes.getAttackDamage().get(), attack)
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, followRange)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.KNOCKBACK_RESISTANCE, knockbackResistance)
                .add(Attributes.ATTACK_KNOCKBACK, attackKnockback)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(PortAttributesExtension.scale().get(), 1.0D)
                .add(PortAttributesExtension.safeFallDistance().get(), 8.0)
                .build());
    }

    public static CreatureAttributeBuilder creature(double health, double armor, double attack) {
        return creature(health, armor, attack, 32.0, 1.0, 0.28);
    }

    public static CreatureAttributeBuilder boss(double attack, double health, double armor) {
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(LibAttributes.getAttackDamage().get(), attack)
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.MOVEMENT_SPEED, 1.0)
                .add(Attributes.FOLLOW_RANGE, 300.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(PortAttributesExtension.scale().get(), 1.0D)
                .add(PortAttributesExtension.safeFallDistance().get(), 8.0)
                .build());
    }

    public CreatureAttributeBuilder flying() {
        add(PortAttributesExtension.safeFallDistance().get(), 1000.0);
        return this;
    }

    public CreatureAttributeBuilder movementSpeed(double value) {
        add(Attributes.MOVEMENT_SPEED, value);
        return this;
    }

    public CreatureAttributeBuilder safeFallDistance(double value) {
        add(PortAttributesExtension.safeFallDistance().get(), value);
        return this;
    }

    public CreatureAttributeBuilder gravity(double value) {
        add(PortAttributesExtension.gravity().get(), value);
        return this;
    }

    public CreatureAttributeBuilder jumpStrength(double value) {
        add(PortAttributesExtension.jumpStrength().get(), value);
        return this;
    }

    public CreatureAttributeBuilder stepHeight(double value) {
        add(PortAttributesExtension.stepHeight().get(), value);
        return this;
    }

    public CreatureAttributeBuilder knockbackResistance(double value) {
        add(Attributes.KNOCKBACK_RESISTANCE, value);
        return this;
    }

    public CreatureAttributeBuilder spawnReinforcementsChance(double value) {
        add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, value);
        return this;
    }
}
