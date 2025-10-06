package org.confluence.mod.mixed;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.confluence.lib.mixed.SelfGetter;

public interface IDamageSource extends SelfGetter<DamageSource> {
    void confluence$setCritical(boolean critical);

    boolean confluence$isCritical();

    default boolean confluence$checkBypassesCooldown(TagKey<DamageType> damageTypeKey) {
        return damageTypeKey == DamageTypeTags.BYPASSES_COOLDOWN && Immunity.getCause(confluence$self()) != null;
    }

    static IDamageSource of(DamageSource damageSource) {
        return (IDamageSource) damageSource;
    }
}
