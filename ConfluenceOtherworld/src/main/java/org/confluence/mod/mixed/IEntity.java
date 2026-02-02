package org.confluence.mod.mixed;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.map.ImmunityDataMap;

public interface IEntity extends Immunity, SelfGetter<Entity> {
    void confluence$entity_setCoolDown(int ticks);

    void confluence$setOriginalNoGravity(boolean bool);

    boolean confluence$isInShimmer();

    @Override
    default Type confluence$getImmunityType() {
        return ImmunityDataMap.getImmunityType(confluence$self());
    }

    @Override
    default int confluence$getImmunityDuration(DamageSource damageSource) {
        return ImmunityDataMap.getImmunityDuration(confluence$self(), damageSource, Immunity.super::confluence$getImmunityDuration);
    }

    static IEntity of(Entity entity) {
        return (IEntity) entity;
    }

    byte HAD_SETUP = -1;
    byte HAS_GRAVITY = 0;
    byte NO_GRAVITY = 1;

    Vec3 ANTI_GRAVITY = new Vec3(0.0, -5.0E-4F, 0.0);
}
