package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.crafting.RecipeInput;
import org.confluence.lib.common.recipe.EnvironmentRecipeInput;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.terra_curio.common.recipe.WorkshopRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WorkshopRecipe.class, remap = false)
public abstract class WorkshopRecipeMixin implements SelfGetter<WorkshopRecipe> {
    @ModifyExpressionValue(method = "matches", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object modify(Object original, @Local(argsOnly = true) RecipeInput input) {
        if (((Boolean) original) && input instanceof EnvironmentRecipeInput recipeInput) {
            return recipeInput.getAccess().matches(confluence$self());
        }
        return original;
    }
}
