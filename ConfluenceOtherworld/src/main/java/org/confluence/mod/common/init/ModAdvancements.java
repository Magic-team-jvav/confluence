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
import org.confluence.mod.common.entity.IVariant;

import java.util.Optional;
import java.util.function.Supplier;

public final class ModAdvancements {
    public static class CriterionTriggerz {
        public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, Confluence.MODID);

        public final static Supplier<ShimmerTransmutationTrigger> SHIMMER_TRANSMUTATION = TRIGGERS.register("shimmer_transmutation", ShimmerTransmutationTrigger::new);
    }

    public static class EntitySubPredicatez {
        public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> PREDICATES = DeferredRegister.create(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, Confluence.MODID);

        /// 按变体名匹配 {@link IVariant}。
        ///
        /// 数据驱动谓词只能写入可序列化的字面量，而本模组的小动物变体都是枚举
        /// （`Worm.Variant`、`Butterfly.Variant` 等），并不是 {@link Integer}；
        /// 因此这里取 {@link net.minecraft.util.StringRepresentable#getSerializedName()} 比对变体名，
        /// 而不是比对枚举序号 —— 序号会随枚举重排而静默错位（1.20.1 也正是按变体名匹配的）。
        public static final EntitySubPredicates.EntityVariantPredicateType<String> VARIANT_NAME = register("variant_name", EntitySubPredicates.EntityVariantPredicateType.create(
                Codec.STRING, entity -> entity instanceof VariantHolder<?> holder && holder.getVariant() instanceof IVariant variant ? Optional.of(variant.getSerializedName()) : Optional.empty()
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
