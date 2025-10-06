package org.confluence.mod.mixed;

import net.minecraft.world.level.chunk.PalettedContainer;

public interface IPalettedContainer<T> {
    /**
     * 创建一个新的，只包含一个元素的PalettedContainer
     */
    PalettedContainer<T> confluence$recreateSingle(T ele);
}
