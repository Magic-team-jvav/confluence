package org.confluence.terraentity.api.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.VariantHolder;

/**
 * 适配原版的变种
 * @param <T>
 */
public interface IVanillaVariant<T> extends IVariant<T> , VariantHolder<T> {

    default ResourceLocation getTexture(){
        return getTexturesMap().get(getVariant());
    }

    default T getTEVariant(){
        return getVariant();
    }

    default void setTEVariant(T variant){
        setVariant(variant);
    }
}
