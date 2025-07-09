package org.confluence.mod.mixed;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface IStructureStart {
    /**
     * 实际上就是getBoundingBox的代理，但是为了防止有人以为这个方法没经过缓存，于是换一个醒目的名字
     */
    BoundingBox confluence$cachedBoundingBox();

    static IStructureStart of(StructureStart structureStart) {
        return (IStructureStart) (Object) structureStart;
    }
}
