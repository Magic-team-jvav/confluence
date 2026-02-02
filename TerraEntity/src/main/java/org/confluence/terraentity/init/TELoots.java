package org.confluence.terraentity.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.init.loot.conditioin.VariantCondition;
import org.confluence.terraentity.data.init.loot.number.VariantProvider;

public class TELoots {


    public static class TELootNumberProviders {

        public static final DeferredRegister<LootNumberProviderType> TYPE = DeferredRegister.create(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, TerraEntity.MODID);

        public static final DeferredHolder<LootNumberProviderType, LootNumberProviderType> VARIANT = TYPE.register("variant", () ->
                new LootNumberProviderType(VariantProvider.CODEC));


    }

    public static class TELootItemConditions {
        public static final DeferredRegister<LootItemConditionType> TYPE = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, TerraEntity.MODID);

        public static final DeferredHolder<LootItemConditionType, LootItemConditionType> VARIANT_CONDITION = TYPE.register("variant_condition", () ->
                new LootItemConditionType(VariantCondition.CODEC));


    }

    public static void register(IEventBus bus) {
        TELootNumberProviders.TYPE.register(bus);
        TELootItemConditions.TYPE.register(bus);
    }
}
