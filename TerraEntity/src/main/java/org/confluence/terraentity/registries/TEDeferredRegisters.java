package org.confluence.terraentity.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.registries.mappeddata.DeferredMappedType;
import org.confluence.terraentity.registries.mappeddata.MappedDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class TEDeferredRegisters {
    public static class MappedTypeRegister extends DeferredRegister<MappedDataType<?,?>> {

        public MappedTypeRegister(String namespace) {
            super(TERegistries.Keys.MAPPED_DATA, namespace);
        }

        @Override
        public <I extends MappedDataType<?,?>> @NotNull DeferredMappedType<I> register(@NotNull String name, @NotNull Function<ResourceLocation, ? extends I> func) {
            return (DeferredMappedType) super.register(name, func);
        }

        @Override
        public <I extends MappedDataType<?,?>> @NotNull DeferredMappedType<I> register(@NotNull String name, @NotNull Supplier<? extends I> sup) {
            return this.register(name, (key) -> sup.get());
        }

        @Override
        protected <I extends MappedDataType<?,?>> @NotNull DeferredMappedType<I> createHolder(ResourceKey<? extends Registry<MappedDataType<?,?>>> registryKey, ResourceLocation key) {
            return DeferredMappedType.createMappedType(ResourceKey.create(registryKey, key));
        }
    }


}
