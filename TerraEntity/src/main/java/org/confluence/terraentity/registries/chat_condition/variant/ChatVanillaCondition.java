package org.confluence.terraentity.registries.chat_condition.variant;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.confluence.terraentity.api.npc.chat.IChatCondition;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.chat.ChatHolder;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProvider;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProviderTypes;

public record ChatVanillaCondition(ICondition condition) implements IChatCondition {


    public static final MapCodec<? extends IChatCondition> CODEC = ICondition.CODEC.xmap(ChatVanillaCondition::new, ChatVanillaCondition::condition).fieldOf("condition");

    @Override
    public ChatConditionProvider getProvider() {
        return ChatConditionProviderTypes.VANILLA.get();
    }

    @Override
    public boolean canChat(AbstractTerraNPC npc, ChatHolder chatHolder) {
        return condition.test(ICondition.IContext.TAGS_INVALID);
    }
}
