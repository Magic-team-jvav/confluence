package org.confluence.lib.mixin;

import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.mixed.LibShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ShapedRecipePattern.class, priority = 0)
public abstract class ShapedRecipePatternMixin implements LibShapedRecipePattern {
    @Unique
    private boolean confluence$nonSymmetricalMatching = false;

    @Override
    public void confluence$setNonSymmetricalMatching() {
        this.confluence$nonSymmetricalMatching = true;
    }

    @Override
    public boolean confluence$isNonSymmetricalMatching() {
        return confluence$nonSymmetricalMatching;
    }

    @ModifyVariable(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Z)Z", at = @At("HEAD"), argsOnly = true)
    private boolean nonSymmetrical(boolean symmetrical) {
        if (symmetrical && confluence$nonSymmetricalMatching) return false;
        return symmetrical;
    }
}
