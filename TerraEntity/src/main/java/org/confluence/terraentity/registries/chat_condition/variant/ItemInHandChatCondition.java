package org.confluence.terraentity.registries.chat_condition.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.confluence.terraentity.api.npc.chat.IChatCondition;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.chat.ChatHolder;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProvider;
import org.confluence.terraentity.registries.chat_condition.ChatConditionProviderTypes;

public record ItemInHandChatCondition(Item item) implements IChatCondition {

    public static final MapCodec<ItemInHandChatCondition> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemInHandChatCondition::item)
    ).apply(instance, ItemInHandChatCondition::new));

    @Override
    public ChatConditionProvider getProvider() {
        return ChatConditionProviderTypes.ITEM_IN_HAND.get();
    }

    @Override
    public boolean canChat(AbstractTerraNPC npc, ChatHolder chatHolder) {
        return npc.getMainHandItem().getItem() == item;
    }
}
