package org.confluence.mod.mixin.accessor;

import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipePattern.class)
public interface ShapedRecipePatternAccessor {
    @Accessor
    boolean getSymmetrical();
}
