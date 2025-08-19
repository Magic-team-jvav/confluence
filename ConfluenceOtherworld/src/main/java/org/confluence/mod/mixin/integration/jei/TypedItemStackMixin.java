package org.confluence.mod.mixin.integration.jei;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.integration.jei.ITypedItemStack;
import org.confluence.mod.integration.jei.category.ExtractinatorCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "mezz.jei.library.ingredients.itemStacks.TypedItemStack", remap = false)
public abstract class TypedItemStackMixin implements ITypedItemStack {
    @Unique
    private float confluence$chance;
    @Unique
    private int confluence$min;
    @Unique
    private int confluence$max;

    @Override
    public void confluence$setChance(float chance) {
        this.confluence$chance = chance;
    }

    @Override
    public void confluence$setBounds(int min, int max) {
        this.confluence$min = min;
        this.confluence$max = max;
    }

    @Override
    public float confluence$grtChance() {
        return confluence$chance;
    }

    @Override
    public int confluence$getMin() {
        return confluence$min;
    }

    @Override
    public int confluence$getMax() {
        return confluence$max;
    }

    @ModifyReturnValue(method = "create", at = @At("RETURN"))
    private static ITypedIngredient<ItemStack> modify(ITypedIngredient<ItemStack> original, @Local(argsOnly = true) ItemStack ingredient) {
        if (original instanceof ITypedItemStack typed && ingredient instanceof ExtractinatorCategory.DataItemStack data) {
            typed.confluence$setChance(data.chance);
            typed.confluence$setBounds(data.min, data.max);
        }
        return original;
    }

    @Pseudo
    @Mixin(targets = "mezz.jei.library.ingredients.itemStacks.TypedItemStack$1", remap = false)
    public static abstract class S1Mixin {
        @Inject(method = "load(Lmezz/jei/library/ingredients/itemStacks/TypedItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true)
        private void modify(@Coerce Object key, CallbackInfoReturnable<ItemStack> cir) {
            if (key instanceof ITypedItemStack typed && typed.confluence$grtChance() > 0.0F) {
                cir.setReturnValue(new ExtractinatorCategory.DataItemStack(
                        cir.getReturnValue(),
                        typed.confluence$getMin(),
                        typed.confluence$getMax(),
                        typed.confluence$grtChance()
                ));
            }
        }
    }
}
