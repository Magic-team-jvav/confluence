package org.confluence.mod.common.entity.boss;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import org.confluence.lib.api.entity.Boss;
import org.confluence.mod.common.entity.BaseLivingPart;
import org.confluence.mod.common.entity.monster.difficulty.CreatureDifficultyScaling;
import org.confluence.mod.common.init.entity.ModEntities;

/// Boss 活体部件仅扩展遭遇登记、受伤通知和本体属性倍率；所有权及生命周期由 BaseLivingPart 管理。
public abstract class BaseLivingBossPart<T extends BaseBoss> extends BaseLivingPart<T> implements Boss.BossPart {
    private double registeredMaxHealth = -1.0D;
    private double registeredAttackDamage = -1.0D;

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
    protected final void onPartLoaded() {
        registeredMaxHealth = -1.0D;
        registeredAttackDamage = -1.0D;
    }

    @Override
    protected final void updateOwnerAttributes(T owner) {
        var partMaximumHealth = getAttribute(Attributes.MAX_HEALTH);
        var ownerMaximumHealth = owner.getAttribute(Attributes.MAX_HEALTH);
        if (partMaximumHealth != null && ownerMaximumHealth != null && ownerMaximumHealth.getBaseValue() > 0.0D) {
            if (registeredMaxHealth < 0.0D) {
                registeredMaxHealth = registeredValue(Attributes.MAX_HEALTH, partMaximumHealth.getBaseValue());
            }
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
            if (registeredAttackDamage < 0.0D) {
                registeredAttackDamage = registeredValue(Attributes.ATTACK_DAMAGE, partAttackDamage.getBaseValue());
            }
            double scaledDamage = registeredAttackDamage * ownerAttackDamage.getValue() / ownerAttackDamage.getBaseValue()
                    / CreatureDifficultyScaling.attackMultiplier(this);
            if (scaledDamage >= 0.0D) {
                partAttackDamage.setBaseValue(scaledDamage);
            }
        }
    }

    /// 继承倍率始终从登记模板起算，不把存档中已放大的部件基础值当作原值再放大。
    private double registeredValue(Holder<Attribute> attribute, double fallback) {
        var definition = ModEntities.creatureAttributes(getType());
        return definition != null && definition.attributes().hasAttribute(attribute) ? definition.attributes().getBaseValue(attribute) : fallback;
    }
}
