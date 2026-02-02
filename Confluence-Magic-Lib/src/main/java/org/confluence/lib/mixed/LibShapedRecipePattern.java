package org.confluence.lib.mixed;

import net.minecraft.world.item.crafting.ShapedRecipePattern;

public interface LibShapedRecipePattern {
    void confluence$setNonSymmetricalMatching();

    boolean confluence$isNonSymmetricalMatching();

    static void setNonSymmetricalMatching(ShapedRecipePattern pattern) {
        ((LibShapedRecipePattern) (Object) pattern).confluence$setNonSymmetricalMatching();
    }

    static boolean isNonSymmetricalMatching(ShapedRecipePattern pattern) {
        return ((LibShapedRecipePattern) (Object) pattern).confluence$isNonSymmetricalMatching();
    }
}
