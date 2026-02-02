package org.confluence.terraentity.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.data.component.ResourceLocationComponent;
import org.confluence.terraentity.data.component.SingleBooleanComponent;

import java.util.function.Supplier;

public final class TEDataComponentTypes {
    public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TerraEntity.MODID);

    public static final Supplier<DataComponentType<EffectStrategyComponent>> EFFECT_STRATEGY = TYPES.registerComponentType(
            "effect_strategy", builder -> builder.persistent(EffectStrategyComponent.CODEC).networkSynchronized(EffectStrategyComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<EffectStrategyComponent>> EFFECT_STRATEGY_BENEFICIAL = TYPES.registerComponentType(
            "effect_strategy_beneficial", builder -> builder.persistent(EffectStrategyComponent.CODEC).networkSynchronized(EffectStrategyComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<EffectStrategyComponent>> BOW_FULL_CHARGE_EFFECT_STRATEGY = TYPES.registerComponentType(
            "bow_full_charge_effect_strategy", builder -> builder.persistent(EffectStrategyComponent.CODEC).networkSynchronized(EffectStrategyComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<SingleBooleanComponent>> BOOMERANG_READY = TYPES.registerComponentType(
            "boomerang_ready", builder -> builder.persistent(SingleBooleanComponent.CODEC).networkSynchronized(SingleBooleanComponent.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<ResourceLocationComponent>> WHIP_PATH = TYPES.registerComponentType(
            "whip_path", builder -> builder.persistent(ResourceLocationComponent.CODEC).networkSynchronized(ResourceLocationComponent.STREAM_CODEC)
    );
}
