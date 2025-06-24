package org.confluence.mod.mixin.integration.xaero;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.confluence.mod.integration.xaero.PylonWaypointElementRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import xaero.map.element.MapElementRenderHandler;
import xaero.map.element.MapElementRenderer;

import java.util.List;

@Pseudo
@Mixin(targets = "xaero.map.element.MapElementRenderHandler$Builder", remap = false)
public abstract class MapElementRenderHandler$BuilderMixin {
    @WrapOperation(method = "build",at= @At(value = "NEW", target = "(Ljava/util/List;I)Lxaero/map/element/MapElementRenderHandler;"))
    private MapElementRenderHandler appendRenderer(List<MapElementRenderer<?, ?, ?>> renderers, int location, Operation<MapElementRenderHandler> original) {
        renderers.add(new PylonWaypointElementRenderer());
        return original.call(renderers, location);
    }
}
