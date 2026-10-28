package org.confluence.mod.common.entity.boss;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.entity.BaseLivingPart;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyRules;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;

import java.util.List;

/// Boss 活体部件仅扩展遭遇登记、受伤通知和本体属性倍率；所有权及生命周期由 BaseLivingPart 管理。
public abstract class BaseLivingBossPart<T extends BaseBoss> extends BaseLivingPart<T> implements Boss.BossPart {

    protected BaseLivingBossPart(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected final boolean attachToOwner(T owner) {
        owner.addSubEntity(this);
        return true;
    }

    @Override
    protected final void detachFromOwner(T owner, RemovalReason reason) {
        owner.removeSubEntity(this);
    }

    @Override
    protected final void onPartHurt(T owner, DamageSource source) {
        owner.onEncounterHurt(source);
    }

    @Override
    protected final boolean persistPart() {
        return false;
    }

    @Override
    protected final void updateOwnerAttributes(T owner) {
        if (attributeState() != null) {
            for (var attribute : List.of(Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS)) {
                var instance = getAttribute(attribute);
                if (instance != null)
                    instance.setBaseValue(registeredValue(attribute, instance.getBaseValue()));
            }
            var penetration = getAttribute(LibAttributes.getArmorPenetration().value());
            if (penetration != null) {
                double base = CreatureDefinition.get(getType()).attributes().armorPenetration() >= 0
                        ? CreatureDefinition.attributeValue(this, LibAttributes.getArmorPenetration().value(), 1)
                        : CreatureDifficultyRules.penetration(registeredValue(Attributes.ATTACK_DAMAGE, 0));
                penetration.setBaseValue(CreatureDefinition.stateAttributeValue(this, attributeState(), LibAttributes.getArmorPenetration().value(), base));
            }
            CreatureDifficultyScaling.apply(this, false);
        }
        var partMaximumHealth = getAttribute(Attributes.MAX_HEALTH);
        var ownerMaximumHealth = owner.getAttribute(Attributes.MAX_HEALTH);
        if (partMaximumHealth != null && ownerMaximumHealth != null && ownerMaximumHealth.getBaseValue() > 0.0D) {
            double registeredMaxHealth = registeredValue(Attributes.MAX_HEALTH, partMaximumHealth.getBaseValue());
            float healthRatio = getMaxHealth() <= 0.0F ? 1.0F : getHealth() / getMaxHealth();
            double scaledMaximum = registeredMaxHealth * ownerMaximumHealth.getValue() / ownerMaximumHealth.getBaseValue()
                    / CreatureDifficultyScaling.healthMultiplier(this);
            if (scaledMaximum > 0.0D && Math.abs(partMaximumHealth.getBaseValue() - scaledMaximum) > 1.0E-4D) {
                partMaximumHealth.setBaseValue(scaledMaximum);
                setHealth((float) (getMaxHealth() * healthRatio));
            }
        }

        var partAttackDamage = getAttribute(Attributes.ATTACK_DAMAGE);
        var ownerAttackDamage = owner.getAttribute(Attributes.ATTACK_DAMAGE);
        if (partAttackDamage != null && ownerAttackDamage != null && ownerAttackDamage.getBaseValue() > 0.0D) {
            double registeredAttackDamage = registeredValue(Attributes.ATTACK_DAMAGE, partAttackDamage.getBaseValue());
            double scaledDamage = attributeState() != null ? registeredAttackDamage : registeredAttackDamage * ownerAttackDamage.getValue() / ownerAttackDamage.getBaseValue()
                    / CreatureDifficultyScaling.attackMultiplier(this);
            if (scaledDamage >= 0.0D) {
                partAttackDamage.setBaseValue(scaledDamage);
            }
        }
    }

    /// 只有共用实体类型的部件才指定属性状态；数值留在注册表。
    protected Enum<?> attributeState() {return null;}

    /// 继承倍率始终从登记模板起算，不把存档中已放大的部件基础值当作原值再放大。
    private double registeredValue(Attribute attribute, double fallback) {
        double base = CreatureDefinition.attributeValue(this, attribute, fallback);
        return attributeState() == null ? base : CreatureDefinition.stateAttributeValue(this, attributeState(), attribute, base);
    }
}
