package org.confluence.mod.mixed;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.confluence.lib.mixed.CriticalDamageSource;
import org.confluence.lib.mixed.SelfGetter;

public interface IDamageSource extends CriticalDamageSource, SelfGetter<DamageSource> {

    default boolean confluence$checkBypassesCooldown(TagKey<DamageType> damageTypeKey) {
        return damageTypeKey == DamageTypeTags.BYPASSES_COOLDOWN && Immunity.getCause(confluence$self()) != null;
    }

    static IDamageSource of(DamageSource damageSource) {
        return (IDamageSource) damageSource;
    }
}
