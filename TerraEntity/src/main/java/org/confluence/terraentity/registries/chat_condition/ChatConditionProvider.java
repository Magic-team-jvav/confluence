package org.confluence.terraentity.registries.chat_condition;

import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.api.npc.chat.IChatCondition;

public record ChatConditionProvider (MapCodec<? extends IChatCondition> codec) {


}