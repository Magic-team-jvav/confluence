package org.confluence.mod.mixed;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.confluence.lib.mixed.IExtraSyncedData;

public interface IAbstractArrow extends IExtraSyncedData<AbstractArrow> {
    boolean confluence$isShootFromShortBow();

    void confluence$setShootFromShortBow(boolean is);

    static IAbstractArrow of(AbstractArrow arrow) {
        return (IAbstractArrow) arrow;
    }
}
