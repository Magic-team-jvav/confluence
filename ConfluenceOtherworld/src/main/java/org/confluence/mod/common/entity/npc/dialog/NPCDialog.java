package org.confluence.mod.common.entity.npc.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

import java.util.List;

public record NPCDialog(List<String> keys) {
    /**
     * 对话数据保留与数据包一致的对象结构，避免把 {@code {"dialogs": [...]}}
     * 误当作裸字符串数组解析。
     */
    public static final Codec<NPCDialog> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("dialogs").forGetter(NPCDialog::keys)
    ).apply(instance, NPCDialog::new));

    public String randomKey(RandomSource random) {
        return Util.getRandom(keys, random);
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }
}
