package org.confluence.mod.mixed;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;

public interface IAbstractMinecart {
    Item confluence$getDropItem();

    static IAbstractMinecart of(AbstractMinecart minecart) {
        return (IAbstractMinecart) minecart;
    }
}
