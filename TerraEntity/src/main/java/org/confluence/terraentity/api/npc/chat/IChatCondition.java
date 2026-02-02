package org.confluence.terraentity.api.npc.chat;

import com.mojang.serialization.Codec;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.chat.ChatHolder;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProvider;
import org.confluence.terraentity.registries.chat_condition.variant.NotChatCondition;

/**
 * npc 触发对话条件
 */
public interface IChatCondition {

    boolean canChat(AbstractTerraNPC npc, ChatHolder chatHolder);

    ChatConditionProvider getProvider();

    Codec<IChatCondition> TYPE_CODEC = TERegistries.CHAT_CONDITION_PROVIDERS
            .byNameCodec()
            .dispatch(IChatCondition::getProvider, ChatConditionProvider::codec);

    static NotChatCondition not(IChatCondition condition) {
        return new NotChatCondition(condition);
    }


}
