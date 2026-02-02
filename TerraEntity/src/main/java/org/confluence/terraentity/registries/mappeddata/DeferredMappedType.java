package org.confluence.terraentity.registries.mappeddata;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeferredMappedType<T extends MappedDataType<?,?>> extends DeferredHolder<MappedDataType<?,?>, T> {

    protected DeferredMappedType(ResourceKey<MappedDataType<?, ?>> key) {
        super(key);
    }

    public static <T extends MappedDataType<?,?>> DeferredMappedType<T> createMappedType(ResourceKey<MappedDataType<?, ?>> key){
        return new DeferredMappedType<>(key);
    }

}
