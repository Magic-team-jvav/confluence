package org.confluence.mod.common.init;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.advancement.ShimmerTransmutationTrigger;

import java.util.function.Supplier;

public final class ModCriterionTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, Confluence.MODID);

    public final static Supplier<ShimmerTransmutationTrigger> SHIMMER_TRANSMUTATION = TRIGGERS.register("shimmer_transmutation", ShimmerTransmutationTrigger::new);
}
