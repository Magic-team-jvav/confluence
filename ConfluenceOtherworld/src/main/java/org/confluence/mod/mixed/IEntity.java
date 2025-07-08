package org.confluence.mod.mixed;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.map.ImmunityDataMap;
import org.confluence.mod.common.init.ModDataMaps;

public interface IEntity extends Immunity, SelfGetter<Entity> {
    void confluence$entity_setCoolDown(int ticks);

    void confluence$setOriginalNoGravity(boolean bool);

    boolean confluence$isInShimmer();

    Vec3 confluence$deathMotion(Vec3... motion);

    @Override
    default Type confluence$getImmunityType() {
        ImmunityDataMap immunity = confluence$self().getType().builtInRegistryHolder().getData(ModDataMaps.IMMUNITY);
        if (immunity == null) {
            if (confluence$self() instanceof Projectile) return Type.LOCAL;
            return Type.STATIC;
        }
        return immunity.type();
    }

    @Override
    default int confluence$getImmunityDuration(DamageSource damageSource) {
        ImmunityDataMap immunity = confluence$self().getType().builtInRegistryHolder().getData(ModDataMaps.IMMUNITY);
        if (immunity == null) return Immunity.super.confluence$getImmunityDuration(damageSource);
        return immunity.duration();
    }

    byte HAD_SETUP = -1;
    byte HAS_GRAVITY = 0;
    byte NO_GRAVITY = 1;
}
