package org.confluence.mod.mixin.integration.geckolib;

import org.confluence.mod.mixed.IGeoCube;
import org.confluence.mod.util.DeathAnimUtils;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.cache.object.GeoCube;

@Mixin(value = GeoCube.class, remap = false)
public class GeoCubeMixin implements IGeoCube, SelfGetter<GeoCube> {
    @Unique GeoCube confluence$copy;

    @Override
    public GeoCube confluence$getCopy(){
        if(confluence$copy == null){
            confluence$copy = DeathAnimUtils.duplicateGeoCube(self());
        }
        return confluence$copy;
    }
}
