package org.confluence.mod.mixin.integration.xaero;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.mod.integration.xaero.PylonWaypointElementRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import xaero.map.element.render.ElementRenderer;

import java.util.ArrayList;

@Pseudo
@Mixin(targets = "xaero.map.element.MapElementRenderHandler$Builder", remap = false)
public abstract class MapElementRenderHandler$BuilderMixin {
    @ModifyExpressionValue(method = "build", at = @At(value = "NEW", target = "()Ljava/util/ArrayList;"))
    private ArrayList<ElementRenderer<?, ?, ?>> appendRenderer(ArrayList<ElementRenderer<?, ?, ?>> original) {
        if (WaystonesHelper.IS_LOADED) {
            original.add(new PylonWaypointElementRenderer());
        }
        return original;
    }
}
