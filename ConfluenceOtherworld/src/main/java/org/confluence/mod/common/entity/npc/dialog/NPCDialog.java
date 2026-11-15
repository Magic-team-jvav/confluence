package org.confluence.mod.common.entity.npc.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Map;

public record NPCDialog(List<String> keys) {
    /// 对话数据保留与数据包一致的对象结构，避免把 {@code {"dialogs": [...]}}
    /// 误当作裸字符串数组解析。
    public static final Codec<NPCDialog> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.listOf().fieldOf("dialogs").forGetter(NPCDialog::keys)).apply(instance, NPCDialog::new));
    public static final Codec<Map<EntityType<?>, NPCDialog>> MAP_CODEC = Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), CODEC);

    public String randomKey(RandomSource random) {
        return Util.getRandom(keys, random);
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }
}
