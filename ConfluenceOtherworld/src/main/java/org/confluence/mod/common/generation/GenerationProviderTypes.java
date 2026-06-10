package org.confluence.mod.common.generation;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

/// 注册追踪编解码器的类型
public class GenerationProviderTypes {
    public static final DeferredRegister<GenerationProvider> TYPES = DeferredRegister.create(TERegistries.Keys.GENERATION_PROVIDER, TerraEntity.MODID);

    public static final Supplier<GenerationProvider> FORWARD_GENERATION = register("forward", ForwardGeneration.CODEC);
    public static final Supplier<GenerationProvider> ABOVE_FALLEN = register("above_fallen", AboveFallenGeneration.CODEC);
    public static final Supplier<GenerationProvider> STILL = register("still", StillGeneration.CODEC);

    private static Supplier<GenerationProvider> register(String name, MapCodec<? extends IGeneration> codec) {
        return TYPES.register(name, () -> new GenerationProvider(codec));
    }
}
