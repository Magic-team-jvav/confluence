package org.confluence.mod.mixed;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.NotNull;

public interface IStructureStart {
    /**
     * 实际上就是getBoundingBox的代理，但是为了防止有人以为这个方法没经过缓存，于是换一个醒目的名字
     */
    BoundingBox confluence$cachedBoundingBox();

    static @NotNull IStructureStart of(@NotNull StructureStart structureStart) {
        return (IStructureStart) (Object) structureStart;
    }
}
