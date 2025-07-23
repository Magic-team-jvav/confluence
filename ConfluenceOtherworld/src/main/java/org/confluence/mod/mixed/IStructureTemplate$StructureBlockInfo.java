package org.confluence.mod.mixed;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public interface IStructureTemplate$StructureBlockInfo {
    void confluence$setColors(int[] colors);

    int @Nullable [] confluence$getColors();

    void confluence$setDroplets(ParticleOptions particle);

    @Nullable ParticleOptions confluence$getDroplets();

    static IStructureTemplate$StructureBlockInfo of(StructureTemplate.StructureBlockInfo blockInfo) {
        return (IStructureTemplate$StructureBlockInfo) (Record) blockInfo;
    }
}
