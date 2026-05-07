package org.confluence.mod.mixed;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.confluence.lib.mixed.ILibDamageSource;
import org.confluence.lib.mixed.SelfGetter;
import org.jetbrains.annotations.ApiStatus;

@Deprecated(since = "1.3.0", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "1.4.0")
public interface IDamageSource extends ILibDamageSource, SelfGetter<DamageSource> {
    default boolean confluence$checkBypassesCooldown(TagKey<DamageType> damageTypeKey) {
        return damageTypeKey == DamageTypeTags.BYPASSES_COOLDOWN && Immunity.getCause(confluence$self()) != null;
    }

    static IDamageSource of(DamageSource damageSource) {
        return (IDamageSource) damageSource;
    }
}
