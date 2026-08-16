package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/// 1.21.1 水生敌怪注册数值的集中映射。
///
/// <p>字段顺序与同步侧 {@code AttBuilder.createAttributes} 的语义保持一致，具体实体注册时只选择
/// 对应档案，避免同一生物的生命、护甲或击退参数分散在多个类中。档案最终仍生成原版属性表，
/// 不在运行时保存额外状态。</p>
final class AquaticAttributeProfiles {
    static final Profile PIRANHA = new Profile(15.0, 2.0, 13.0, 20.0, 1.2, 0.5, 0.1);
    static final Profile ARAPAIMA = new Profile(104.0, 30.0, 39.0, 32.0, 1.2, 0.1, 0.1);
    static final Profile BLUE_JELLYFISH = new Profile(17.0, 4.0, 13.0, 16.0, 1.2, 0.5, 0.1);
    static final Profile PINK_JELLYFISH = new Profile(36.0, 6.0, 15.0, 16.0, 1.2, 0.5, 0.1);
    static final Profile GREEN_JELLYFISH = new Profile(62.0, 18.0, 41.0, 20.0, 1.2, 0.5, 0.1);
    static final Profile SHARK = new Profile(156.0, 2.0, 20.0, 48.0, 1.2, 0.37, 0.1);

    private AquaticAttributeProfiles() {}

    record Profile(double health, double armor, double damage, double followRange,
                   double movementSpeed, double attackKnockback, double knockbackResistance) {
        AttributeSupplier.Builder createBuilder() {
            return Mob.createMobAttributes()
                    .add(Attributes.MAX_HEALTH, health)
                    .add(Attributes.ARMOR, armor)
                    .add(Attributes.ATTACK_DAMAGE, damage)
                    .add(Attributes.ATTACK_KNOCKBACK, attackKnockback)
                    .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                    .add(Attributes.FOLLOW_RANGE, followRange)
                    .add(Attributes.KNOCKBACK_RESISTANCE, knockbackResistance);
        }
    }
}
