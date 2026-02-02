package org.confluence.terraentity.registries.chat_condition.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.npc.chat.IChatCondition;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.chat.ChatHolder;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProvider;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProviderTypes;

import java.util.Optional;

public record WeatherChatCondition(Optional<Boolean> isRaining, Optional<Boolean> isThundering) implements IChatCondition {

    public static final MapCodec<WeatherChatCondition> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("raining").forGetter(WeatherChatCondition::isRaining),
            Codec.BOOL.optionalFieldOf("thundering").forGetter(WeatherChatCondition::isThundering)
    ).apply(instance, WeatherChatCondition::new));

    @Override
    public ChatConditionProvider getProvider() {
        return ChatConditionProviderTypes.WEATHER.get();
    }

    @Override
    public boolean canChat(AbstractTerraNPC npc, ChatHolder chatHolder) {
        Level level = npc.level();
        return (this.isRaining.isEmpty() || this.isRaining.get() == level.isRaining()) &&
                (this.isThundering.isEmpty() || this.isThundering.get() == level.isThundering());
    }
}
