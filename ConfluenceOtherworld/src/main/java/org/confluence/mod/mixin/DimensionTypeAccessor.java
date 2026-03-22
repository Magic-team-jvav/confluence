package org.confluence.mod.mixin;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.dimension.DimensionType.class)
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
