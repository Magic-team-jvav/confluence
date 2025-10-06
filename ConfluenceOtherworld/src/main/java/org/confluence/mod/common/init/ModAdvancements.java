package org.confluence.mod.common.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.VariantHolder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.advancement.ShimmerTransmutationTrigger;

import java.util.Optional;
import java.util.function.Supplier;

public final class ModAdvancements {
    public static class CriterionTriggerz {
        public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, Confluence.MODID);

        public final static Supplier<ShimmerTransmutationTrigger> SHIMMER_TRANSMUTATION = TRIGGERS.register("shimmer_transmutation", ShimmerTransmutationTrigger::new);
    }

    public static class EntitySubPredicatez {
        public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> PREDICATES = DeferredRegister.create(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, Confluence.MODID);

        public static final EntitySubPredicates.EntityVariantPredicateType<Integer> INT_VARIANT = register("int", EntitySubPredicates.EntityVariantPredicateType.create(
                Codec.INT, entity -> entity instanceof VariantHolder<?> holder && holder.getVariant() instanceof Integer integer ? Optional.of(integer) : Optional.empty()
        ));

        private static <V> EntitySubPredicates.EntityVariantPredicateType<V> register(String name, EntitySubPredicates.EntityVariantPredicateType<V> type) {
            PREDICATES.register(name, () -> type.codec);
            return type;
        }
    }

    public static void register(IEventBus eventBus) {
        CriterionTriggerz.TRIGGERS.register(eventBus);
        EntitySubPredicatez.PREDICATES.register(eventBus);
    }
}
