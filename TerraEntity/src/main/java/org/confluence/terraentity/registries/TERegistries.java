package org.confluence.terraentity.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TEEffectStrategies;
import org.confluence.terraentity.registries.chat.ChatElementProvider;
import org.confluence.terraentity.registries.chat.ChatProviderTypes;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProvider;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProviderTypes;
import org.confluence.terraentity.registries.chester.ChesterConditionalType;
import org.confluence.terraentity.registries.chester.ChesterConditionalTypes;
import org.confluence.terraentity.registries.chester.ChesterType;
import org.confluence.terraentity.registries.chester.ChesterTypes;
import org.confluence.terraentity.registries.generation.GenerationProvider;
import org.confluence.terraentity.registries.generation.GenerationProviderTypes;
import org.confluence.terraentity.registries.hit_effect.EffectStrategy;
import org.confluence.terraentity.registries.hit_effect.EffectStrategyProvider;
import org.confluence.terraentity.registries.hit_effect.EffectStrategyProviderTypes;
import org.confluence.terraentity.registries.mappeddata.MappedDataType;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.confluence.terraentity.registries.npc_trade.TradeProviderTypes;
import org.confluence.terraentity.registries.npc_trade_list.TradeGeneratorProvider;
import org.confluence.terraentity.registries.npc_trade_list.TradeGeneratorProviderTypes;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProviderTypes;
import org.confluence.terraentity.registries.npc_trade_modify.TradeModifierProvider;
import org.confluence.terraentity.registries.npc_trade_modify.TradeModifierProviderTypes;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProvider;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProviderTypes;
import org.confluence.terraentity.registries.track.TrackTypeProvider;
import org.confluence.terraentity.registries.track.TrackTypeProviderTypes;

import static net.minecraft.resources.ResourceKey.createRegistryKey;

public class TERegistries {

    public static final Registry<GenerationProvider> GENERATION_PROVIERS = createRegistry(Keys.GENERATION_PROVIDER);
    public static final Registry<EffectStrategyProvider> EFFECT_STRATEGY_PROVIDERS = createRegistry(Keys.EFFECT_STRATEGY_PROVIDER);
    public static final Registry<EffectStrategy> EFFECT_STRATEGIES = createRegistry(Keys.EFFECT_STRATEGY);
    public static final Registry<TrackTypeProvider> TRACK_TYPE_PROVIDERS = createRegistry(Keys.TRACK_TYPE_PROVIDER);
    public static final Registry<TradeProvider> TRADE_PROVIDERS = createRegistry(Keys.TRADE_PROVIDER);
    public static final Registry<TradeTaskProvider> TRADE_TASK_PROVIDERS = createRegistry(Keys.TRADE_TASK_PROVIDER);
    public static final Registry<TradeLockProvider> TRADE_LOCK_PROVIDERS = createRegistry(Keys.TRADE_LOCK_PROVIDER);
    public static final Registry<TradeGeneratorProvider> TRADE_GENERATOR_PROVIDERS = createRegistry(Keys.TRADE_GENERATOR_PROVIDER);
    public static final Registry<ChesterType> CHESTER_TYPES = createRegistry(Keys.CHESTER_TYPE);
    public static final Registry<ChesterConditionalType> CHESTER_CONDITIONAL_TYPES = createRegistry(Keys.CHESTER_CONDITIONAL_TYPE);
    public static final Registry<TradeModifierProvider> TRADE_MODIFIER_PROVIDERS = createRegistry(Keys.TRADE_MODIFIER_PROVIDER);
    public static final Registry<ChatElementProvider> CHAT_ELEMENT_PROVIDERS = createRegistry(Keys.CHAT_ELEMENT_PROVIDER);
    public static final Registry<ChatConditionProvider> CHAT_CONDITION_PROVIDERS = createRegistry(Keys.CHAT_CONDITION_PROVIDER);
    public static final Registry<MappedDataType<?,?>> MAPPED_DATAS = createRegistry(Keys.MAPPED_DATA);


    private static  <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
        return new RegistryBuilder<>(key).create();
    }

    public static class Keys {
        public static final ResourceKey<Registry<GenerationProvider>> GENERATION_PROVIDER = createRegistryKey(TerraEntity.space("generation_provider"));
        public static final ResourceKey<Registry<EffectStrategyProvider>> EFFECT_STRATEGY_PROVIDER = createRegistryKey(TerraEntity.space("effect_strategy_type"));
        public static final ResourceKey<Registry<EffectStrategy>> EFFECT_STRATEGY = createRegistryKey(TerraEntity.space("effect_strategy"));
        public static final ResourceKey<Registry<TrackTypeProvider>> TRACK_TYPE_PROVIDER = createRegistryKey(TerraEntity.space("track_type_provider"));
        public static final ResourceKey<Registry<TradeProvider>> TRADE_PROVIDER = createRegistryKey(TerraEntity.space("trade_provider"));
        public static final ResourceKey<Registry<TradeTaskProvider>> TRADE_TASK_PROVIDER = createRegistryKey(TerraEntity.space("trade_task_provider"));
        public static final ResourceKey<Registry<TradeLockProvider>> TRADE_LOCK_PROVIDER = createRegistryKey(TerraEntity.space("trade_lock_provider"));
        public static final ResourceKey<Registry<TradeGeneratorProvider>> TRADE_GENERATOR_PROVIDER = createRegistryKey(TerraEntity.space("trade_generator_provider"));
        public static final ResourceKey<Registry<ChesterType>> CHESTER_TYPE = createRegistryKey(TerraEntity.space("chester_type"));
        public static final ResourceKey<Registry<ChesterConditionalType>> CHESTER_CONDITIONAL_TYPE = createRegistryKey(TerraEntity.space("chester_conditional_type"));
        public static final ResourceKey<Registry<TradeModifierProvider>> TRADE_MODIFIER_PROVIDER = createRegistryKey(TerraEntity.space("trade_modifier_provider"));
        public static final ResourceKey<Registry<ChatElementProvider>> CHAT_ELEMENT_PROVIDER = createRegistryKey(TerraEntity.space("chat_element"));
        public static final ResourceKey<Registry<ChatConditionProvider>> CHAT_CONDITION_PROVIDER = createRegistryKey(TerraEntity.space("chat_condition"));
        public static final ResourceKey<Registry<MappedDataType<?,?>>> MAPPED_DATA = createRegistryKey(TerraEntity.space("mapped_data_type"));


    }


    // 注册监听
    public static void newRegistry(NewRegistryEvent event) {
        event.register(EFFECT_STRATEGY_PROVIDERS);
        event.register(TRACK_TYPE_PROVIDERS);
        event.register(GENERATION_PROVIERS);
        event.register(EFFECT_STRATEGIES);
        event.register(TRADE_PROVIDERS);
        event.register(TRADE_TASK_PROVIDERS);
        event.register(TRADE_LOCK_PROVIDERS);
        event.register(TRADE_GENERATOR_PROVIDERS);
        event.register(CHESTER_TYPES);
        event.register(CHESTER_CONDITIONAL_TYPES);
        event.register(TRADE_MODIFIER_PROVIDERS);
        event.register(CHAT_ELEMENT_PROVIDERS);
        event.register(CHAT_CONDITION_PROVIDERS);
        event.register(MAPPED_DATAS);

    }

    public static void register(IEventBus bus) {
        EffectStrategyProviderTypes.TYPES.register(bus);
        GenerationProviderTypes.TYPES.register(bus);
        TrackTypeProviderTypes.TYPES.register(bus);
        TradeProviderTypes.TYPES.register(bus);
        TEEffectStrategies.EFFECT_STRATEGY.register(bus);
        TradeTaskProviderTypes.TYPES.register(bus);
        TradeLockProviderTypes.TYPES.register(bus);
        TradeGeneratorProviderTypes.TYPES.register(bus);
        ChesterTypes.TYPES.register(bus);
        ChesterConditionalTypes.TYPES.register(bus);
        TradeModifierProviderTypes.TYPES.register(bus);
        ChatProviderTypes.TYPES.register(bus);
        ChatConditionProviderTypes.TYPES.register(bus);
        MappedDataTypes.TYPES.register(bus);

    }

}
