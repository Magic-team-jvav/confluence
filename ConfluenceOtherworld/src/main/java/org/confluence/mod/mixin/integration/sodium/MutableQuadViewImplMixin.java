package org.confluence.mod.mixin.integration.sodium;

import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import org.confluence.mod.integration.sodium.IMutableQuadViewImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl", remap = false)
public abstract class MutableQuadViewImplMixin implements IMutableQuadViewImpl {
    @Shadow
    public abstract MutableQuadViewImpl color(int vertexIndex, int color);

    @Override
    public void confluence$color(int vertexIndex, int color) {
        color(vertexIndex, color);
    }
}
