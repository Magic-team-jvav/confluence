package org.confluence.terraentity.registries.chat_condition;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.chat.IChatCondition;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.chat_condition.variant.*;

import java.util.function.Supplier;

public class ChatConditionProviderTypes {

    public static final DeferredRegister<ChatConditionProvider> TYPES = DeferredRegister.create(TERegistries.CHAT_CONDITION_PROVIDERS, TerraEntity.MODID);

    public static final Supplier<ChatConditionProvider> WEATHER = register("weather", WeatherChatCondition.CODEC);
    public static final Supplier<ChatConditionProvider> VANILLA = register("vanilla", ChatVanillaCondition.CODEC);
    public static final Supplier<ChatConditionProvider> MEMORY_STATES = register("memory_states", MemoryStateCondition.CODEC);
    public static final Supplier<ChatConditionProvider> ITEM_IN_HAND = register("item_in_hand", ItemInHandChatCondition.CODEC);
    public static final Supplier<ChatConditionProvider> NOT = register("not", NotChatCondition.CODEC);


    private static Supplier<ChatConditionProvider> register(String name, MapCodec<? extends IChatCondition> codec) {
        return TYPES.register(name, ()->new ChatConditionProvider(codec));
    }


}
