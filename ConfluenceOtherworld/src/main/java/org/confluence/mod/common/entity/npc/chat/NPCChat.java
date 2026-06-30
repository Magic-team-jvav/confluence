package org.confluence.mod.common.entity.npc.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * 单条 NPC 对话——可选的文本、可选的表情 ID、可选的物品展示。
 */
public record NPCChat(Optional<String> text, Optional<String> emoji, Optional<ItemStack> item) {
    public static final Codec<NPCChat> CODEC = RecordCodecBuilder.create(b -> b.group(
            Codec.STRING.optionalFieldOf("text").forGetter(NPCChat::text),
            Codec.STRING.optionalFieldOf("emoji").forGetter(NPCChat::emoji),
            ItemStack.CODEC.optionalFieldOf("item").forGetter(NPCChat::item)
    ).apply(b, NPCChat::new));

    public static NPCChat text(String text) {
        return new NPCChat(Optional.of(text), Optional.empty(), Optional.empty());
    }

    public static NPCChat emoji(String emoji) {
        return new NPCChat(Optional.empty(), Optional.of(emoji), Optional.empty());
    }
}
