package org.confluence.terra_curio.mixed;

import net.minecraft.world.entity.Entity;
import org.confluence.lib.mixed.SelfGetter;

public interface IEntity extends SelfGetter<Entity> {
    int terra_curio$getCthulhuSprintingTime();

    void terra_curio$setCthulhuSprintingTime(int amount);

    void terra_curio$setShouldRot(boolean bool);

    boolean terra_curio$isShouldRot();

    float terra_curio$getDimensionHeight();

    boolean terra_curio$isPlayer();

    static IEntity of(Entity entity) {
        return (IEntity) entity;
    }
}
