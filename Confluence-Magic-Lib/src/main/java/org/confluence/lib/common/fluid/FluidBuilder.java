package org.confluence.lib.common.fluid;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Hashtable;
import java.util.function.Consumer;

public class FluidBuilder {
    static final Hashtable<ResourceLocation, FluidBuilder> BUILDERS = new Hashtable<>();
    private final DeferredHolder<FluidType, FluidType> type;
    private final DeferredHolder<Fluid, FlowingFluid> fluid;
    private final DeferredHolder<Fluid, FlowingFluid> flowing;
    private Consumer<FluidType.Properties> typePropertiesConsumer = properties -> {};
    private Consumer<BaseFlowingFluid.Properties> basePropertiesConsumer = properties -> {};

    FluidBuilder(ResourceLocation location) {
        this.type = DeferredHolder.create(NeoForgeRegistries.Keys.FLUID_TYPES.location(), location);
        this.fluid = DeferredHolder.create(Registries.FLUID, location);
        this.flowing = DeferredHolder.create(Registries.FLUID, location.withPrefix("flowing_"));
    }

    public FluidBuilder typeProperties(Consumer<FluidType.Properties> consumer) {
        this.typePropertiesConsumer = consumer;
        return this;
    }

    public FluidBuilder baseProperties(Consumer<BaseFlowingFluid.Properties> consumer) {
        this.basePropertiesConsumer = consumer;
        return this;
    }

    public FluidTriple build() {
        return new FluidTriple(type, fluid, flowing);
    }

    public static void register(RegisterEvent event) {
        BUILDERS.forEach((location, builder) -> {
            event.register(NeoForgeRegistries.Keys.FLUID_TYPES, helper -> {
                FluidType.Properties properties = FluidType.Properties.create();
                builder.typePropertiesConsumer.accept(properties);
                helper.register(builder.type.unwrapKey().orElseThrow(), new FluidType(properties));
            });

            event.register(Registries.FLUID, helper -> {
                BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(builder.type::value, builder.fluid::value, builder.flowing::value);
                builder.basePropertiesConsumer.accept(properties);
                helper.register(builder.fluid.getId(), new BaseFlowingFluid.Source(properties));
                helper.register(builder.flowing.getId(), new BaseFlowingFluid.Flowing(properties));
            });
        });
    }
}
