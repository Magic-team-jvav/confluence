package org.confluence.mod.mixed;

import net.minecraft.world.level.chunk.PalettedContainer;

public interface IPalettedContainer<T> {
    PalettedContainer<T> confluence$recreateSingle(T ele);
}
