package org.confluence.terraentity.registries.generation;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IGeneration;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.generation.variant.AboveFallenGeneration;
import org.confluence.terraentity.registries.generation.variant.ForwardGeneration;
import org.confluence.terraentity.registries.generation.variant.StillGeneration;

import java.util.function.Supplier;

/**
 * 注册追踪编解码器的类型
 */
public class GenerationProviderTypes {
    public static final DeferredRegister<GenerationProvider> TYPES = DeferredRegister.create(TERegistries.GENERATION_PROVIERS, TerraEntity.MODID);

    public static final Supplier<GenerationProvider> FORWARD_GENERATION = register("forward", ForwardGeneration.CODEC);
    public static final Supplier<GenerationProvider> ABOVE_FALLEN = register("above_fallen", AboveFallenGeneration.CODEC);
    public static final Supplier<GenerationProvider> STILL = register("still", StillGeneration.CODEC);


    private static Supplier<GenerationProvider> register(String name, MapCodec<? extends IGeneration> codec) {
        return TYPES.register(name, ()->new GenerationProvider(codec));
    }
}
