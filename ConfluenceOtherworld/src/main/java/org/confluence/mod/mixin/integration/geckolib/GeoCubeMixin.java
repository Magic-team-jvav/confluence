package org.confluence.mod.mixin.integration.geckolib;

import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.mixed.IGeoCube;
import org.confluence.mod.util.DeathAnimUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.cache.object.GeoCube;

@Mixin(value = GeoCube.class, remap = false)
public abstract class GeoCubeMixin implements IGeoCube, SelfGetter<GeoCube> {
    @Unique GeoCube confluence$copy;
    @Unique float[] confluence$minCoords;
    @Unique float[] confluence$maxCoords;

    @Override
    public GeoCube confluence$getCopy(){
        if(confluence$copy == null){
            confluence$copy = DeathAnimUtils.duplicateGeoCube(confluence$self());
        }
        return confluence$copy;
    }

    @Override
    public void confluence$setMinCoords(float[] minCoords){
        confluence$minCoords = minCoords;
    }

    @Override
    public void confluence$setMaxCoords(float[] maxCoords){
        confluence$maxCoords = maxCoords;
    }

    @Override
    public float[] confluence$getMinCoords(){
        return confluence$minCoords;
    }

    @Override
    public float[] confluence$getMaxCoords(){
        return confluence$maxCoords;
    }
}
