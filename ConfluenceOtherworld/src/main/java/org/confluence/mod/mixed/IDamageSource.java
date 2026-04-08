package org.confluence.mod.mixed;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.confluence.lib.mixed.CriticalDamageSource;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.jetbrains.annotations.Nullable;

public interface IDamageSource extends CriticalDamageSource, SelfGetter<DamageSource> {
    default boolean confluence$checkBypassesCooldown(TagKey<DamageType> damageTypeKey) {
        return damageTypeKey == DamageTypeTags.BYPASSES_COOLDOWN && Immunity.getCause(confluence$self()) != null;
    }

    @Deprecated
    static IDamageSource of(DamageSource damageSource) {
        return (IDamageSource) damageSource;
    }

    MutableBoolean WARNED = new MutableBoolean();

    static float processCritical(@Nullable Entity attacker, float amount, LivingEntity victim, DamageSource damageSource) {
        boolean crit = false;
        if (!TCAttributes.hasCustomAttribute(TCAttributes.CRIT_CHANCE) && attacker instanceof Player player) {
            if (LibMathUtils.checkChance(player.getAttributeValue(TCAttributes.CRIT_CHANCE), player.getRandom())) {
                amount *= 1.5F;
                player.crit(victim);
                crit = true;
            }
        }
        if (damageSource.getDirectEntity() instanceof AbstractArrow arrow) {
            crit |= arrow.isCritArrow();
        }
        if (!(damageSource instanceof IDamageSource iDamageSource)) {
            if (WARNED.isFalse()) {
                WARNED.setTrue();
                Confluence.LOGGER.warn("DamageSource had remodified by unknown mod, so critical damage indicator expired now");
            }
            return amount;
        }
        crit |= iDamageSource.confluence$isCritical();
        iDamageSource.confluence$setCritical(crit);
        return amount;
    }
}
