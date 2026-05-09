package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.AboveYAddConstantDensityFunction;

public final class ModDensityFunctionTypes {
    public static final DeferredRegister<MapCodec<? extends DensityFunction>> TYPES = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, Confluence.MODID);

    public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<AboveYAddConstantDensityFunction>> ABOVE_Y_ADD_CONSTANT = TYPES.register("above_y_add_constant", () -> AboveYAddConstantDensityFunction.DATA_CODEC);
}
