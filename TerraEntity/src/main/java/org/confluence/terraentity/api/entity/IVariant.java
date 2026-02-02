package org.confluence.terraentity.api.entity;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * 泰拉变种接口
 */
public interface IVariant<T> {

    default ResourceLocation getTexture(){
        return getTexturesMap().get(getTEVariant());
    }

    Map<T, ResourceLocation> getTexturesMap();

    T getTEVariant();

    void setTEVariant(T variant);
}
