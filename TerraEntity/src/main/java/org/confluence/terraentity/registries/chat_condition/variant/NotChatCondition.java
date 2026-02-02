package org.confluence.terraentity.registries.chat_condition.variant;

import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.api.npc.chat.IChatCondition;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.chat.ChatHolder;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProvider;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProviderTypes;

public record NotChatCondition(IChatCondition condition) implements IChatCondition {


    public static final MapCodec<NotChatCondition> CODEC = IChatCondition.TYPE_CODEC
            .xmap(NotChatCondition::new, NotChatCondition::condition).fieldOf("condition");


    @Override
    public ChatConditionProvider getProvider() {
        return ChatConditionProviderTypes.NOT.get();
    }

    @Override
    public boolean canChat(AbstractTerraNPC npc, ChatHolder chatHolder) {
        return !condition.canChat(npc, chatHolder);
    }
}
