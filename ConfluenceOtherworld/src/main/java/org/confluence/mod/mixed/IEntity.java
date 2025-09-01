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

    Vec3 confluence$deathMotion(Vec3... motion);

    @Override
    default Type confluence$getImmunityType() {
        return ImmunityDataMap.getImmunityType(confluence$self());
    }

    @Override
    default int confluence$getImmunityDuration(DamageSource damageSource) {
        return ImmunityDataMap.getImmunityDuration(confluence$self(), damageSource, Immunity.super::confluence$getImmunityDuration);
    }

    byte HAD_SETUP = -1;
    byte HAS_GRAVITY = 0;
    byte NO_GRAVITY = 1;
}
