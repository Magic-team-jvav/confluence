package org.confluence.mod.mixed;

import software.bernie.geckolib.cache.object.GeoCube;

public interface IGeoCube {
    GeoCube confluence$getCopy();

    void confluence$setMinCoords(float[] minCoords);
    void confluence$setMaxCoords(float[] maxCoords);
    float[] confluence$getMinCoords();
    float[] confluence$getMaxCoords();

    static IGeoCube of(GeoCube geoCube) {
        return (IGeoCube) (Record) geoCube;
    }
}
