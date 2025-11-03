package org.confluence.mod.mixed;

import net.minecraft.world.item.CreativeModeTab;

public interface ICreativeModeTab {
    void confluence$buildGroup();

    static ICreativeModeTab of(CreativeModeTab tab) {
        return (ICreativeModeTab) tab;
    }
}
