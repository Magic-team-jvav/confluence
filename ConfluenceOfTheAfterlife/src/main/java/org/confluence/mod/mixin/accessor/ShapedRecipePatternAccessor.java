package org.confluence.mod.mixin.accessor;

import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(ShapedRecipePattern.class)
public interface ShapedRecipePatternAccessor {
    @Accessor
    Optional<ShapedRecipePattern.Data> getData();

    @Accessor
    boolean getSymmetrical();
}
