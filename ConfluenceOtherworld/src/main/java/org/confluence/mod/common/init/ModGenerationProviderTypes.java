package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.util.generation.GenerationProvider;
import org.confluence.mod.util.generation.variant.AboveFallenGeneration;
import org.confluence.mod.util.generation.variant.ForwardGeneration;
import org.confluence.mod.util.generation.variant.StillGeneration;

/// 注册追踪编解码器的类型
public final class ModGenerationProviderTypes {
    public static final DeferredRegister<GenerationProvider> TYPES = DeferredRegister.create(ModCustomRegistries.Keys.GENERATION_PROVIER, Confluence.MODID);

    public static final RegistryObject<GenerationProvider> FORWARD_GENERATION = register("forward", ForwardGeneration.CODEC);
    public static final RegistryObject<GenerationProvider> ABOVE_FALLEN = register("above_fallen", AboveFallenGeneration.CODEC);
    public static final RegistryObject<GenerationProvider> STILL = register("still", StillGeneration.CODEC);

    private static RegistryObject<GenerationProvider> register(String name, MapCodec<? extends IGeneration> codec) {
        return TYPES.register(name, () -> new GenerationProvider(codec));
    }
}
