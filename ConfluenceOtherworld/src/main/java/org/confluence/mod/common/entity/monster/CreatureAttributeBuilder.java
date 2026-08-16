package org.confluence.mod.common.entity.monster;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.LibAttributes;

/// 本体生物注册共用的属性模板构建器。
///
/// <p>这里保存的是从 1.21.1 侧迁移过来的基础数值。具体实体仍然可以继续覆盖移动速度、
/// 击退抗性等差异项，但不应该把这些零散数值重新塞回每个实体类里。</p>
///
/// <p>原版飞行移动控制在实体离地后读取 {@link Attributes#FLYING_SPEED}，因此标记为飞行的
/// 实体必须同步写入飞行速度，否则会出现空中移动速度为零、贴地滑行或无法追击的问题。</p>
public final class CreatureAttributeBuilder extends AttributeSupplier.Builder {
    private double movementSpeed;
    private boolean flying;

    private CreatureAttributeBuilder(AttributeSupplier attributes, double movementSpeed) {
        super(attributes);
        this.movementSpeed = movementSpeed;
    }

    public static CreatureAttributeBuilder creature(
            double health, double armor, double attack, double followRange,
            double attackKnockback, double knockbackResistance) {
        double movementSpeed = 0.25;
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(LibAttributes.getAttackDamage().get(), attack)
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                .add(Attributes.FOLLOW_RANGE, followRange)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.KNOCKBACK_RESISTANCE, knockbackResistance)
                .add(Attributes.ATTACK_KNOCKBACK, attackKnockback)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(PortAttributesExtension.safeFallDistance().get(), 8.0)
                .build(), movementSpeed);
    }

    public static CreatureAttributeBuilder creature(
            double health, double armor, double attack) {
        return creature(health, armor, attack, 32.0, 1.0, 0.28);
    }

    public static CreatureAttributeBuilder boss(
            double attack, double health, double armor) {
        double movementSpeed = 1.0;
        return new CreatureAttributeBuilder(Mob.createMobAttributes()
                .add(LibAttributes.getAttackDamage().get(), attack)
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                .add(Attributes.FOLLOW_RANGE, 300.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(PortAttributesExtension.safeFallDistance().get(), 8.0)
                .build(), movementSpeed);
    }

    public CreatureAttributeBuilder flying() {
        flying = true;
        add(Attributes.FLYING_SPEED, movementSpeed);
        add(PortAttributesExtension.safeFallDistance().get(), 1000.0);
        return this;
    }

    public CreatureAttributeBuilder movementSpeed(double value) {
        movementSpeed = value;
        add(Attributes.MOVEMENT_SPEED, value);
        if (flying) {
            add(Attributes.FLYING_SPEED, value);
        }
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
