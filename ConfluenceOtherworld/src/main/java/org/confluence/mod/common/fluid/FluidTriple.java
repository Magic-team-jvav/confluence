package org.confluence.mod.common.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

public record FluidTriple(DeferredHolder<FluidType, FluidType> type, DeferredHolder<Fluid, FlowingFluid> fluid, DeferredHolder<Fluid, FlowingFluid> flowing) {
    public static FluidBuilder builder(ResourceLocation location) {
        FluidBuilder builder = new FluidBuilder(location);
        FluidBuilder.BUILDERS.put(location, builder);
        return builder;
    }
}
