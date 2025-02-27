package org.confluence.mod.mixed;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public interface IStructureTemplate$StructureBlockInfo {
    void confluence$setColors(int[] colors);

    int @Nullable [] confluence$getColors();

    static IStructureTemplate$StructureBlockInfo of(StructureTemplate.StructureBlockInfo blockInfo) {
        return (IStructureTemplate$StructureBlockInfo) (Record) blockInfo;
    }
}
