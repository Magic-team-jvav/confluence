package org.confluence.mod.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.AboveYAddConstantDensityFunction;

public final class ModDensityFunctionTypes {
    public static final DeferredRegister<Codec<? extends DensityFunction>> TYPES = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, Confluence.MODID);

    static {
        TYPES.register("above_y_add_constant", () -> AboveYAddConstantDensityFunction.DATA_CODEC);
    }
}
