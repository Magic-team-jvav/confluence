package org.confluence.mod.mixed;

import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;

public interface IPalettedContainer<T> {
    /**
     * 创建一个新的，只包含一个元素的PalettedContainer
     */
    PalettedContainer<T> confluence$recreateSingle(T ele);

    @SuppressWarnings("unchecked")
    static <T> PalettedContainer<T> recreateSingle(PalettedContainerRO<T> container, T t) {
        return ((IPalettedContainer<T>) container).confluence$recreateSingle(t);
    }
}
