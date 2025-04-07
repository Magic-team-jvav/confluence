package org.confluence.mod.mixin.integration.patchouli;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.integration.patchouli.PatchouliHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.gui.GuiBookLanding;

@Pseudo
@Mixin(targets = "vazkii.patchouli.client.book.gui.GuiBookLanding", remap = false)
public abstract class GuiBookLandingMixin implements SelfGetter<GuiBookLanding> {
    @WrapWithCondition(method = "init", at = @At(value = "INVOKE", target = "Lvazkii/patchouli/client/book/gui/GuiBookLanding;addCategoryButton(ILvazkii/patchouli/client/book/BookCategory;)V", ordinal = 1))
    private boolean hide(GuiBookLanding instance, int i, BookCategory category) {
        return !PatchouliHelper.isBookFromConfluence(confluence$self().book.id);
    }
}
