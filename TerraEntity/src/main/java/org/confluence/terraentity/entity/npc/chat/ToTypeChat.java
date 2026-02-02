package org.confluence.terraentity.entity.npc.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;

public class ToTypeChat implements IToOtherChat {

    Map<EntityType<?>, List<ChatHolder>> chatMap;
    public static final Codec<ToTypeChat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec(),
                    Codec.list(ChatHolder.CODEC)
            ).fieldOf("chat_map").forGetter(ToTypeChat::getChatMap)
    ).apply(instance, ToTypeChat::new));

    private Map<EntityType<?>, List<ChatHolder>> getChatMap() {
        return chatMap;
    }

    public ToTypeChat(Map<EntityType<?>, List<ChatHolder>> chatMap) {
        this.chatMap = chatMap;
    }

    @Override
    public ChatHolder getChatHolder(LivingEntity entity) {
        List<ChatHolder> chatHolders = chatMap.get(entity.getType());
        if (chatHolders == null || chatHolders.isEmpty()) {
            return null;
        }
        int index = entity.getRandom().nextInt(chatHolders.size());
        return chatHolders.get(index);
    }
}
