package org.confluence.mod.mixin.world.level.dimension;

import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor
    int getMinY();

    @Mutable
    @Accessor
    void setMinY(int minY);

    @Accessor
    int getHeight();

    @Mutable
    @Accessor
    void setHeight(int height);

    @Accessor
    int getLogicalHeight();

    @Mutable
    @Accessor
    void setLogicalHeight(int logicalHeight);
}
