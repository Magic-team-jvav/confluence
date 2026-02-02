package org.confluence.terraentity.entity.npc.mood;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * npc心情类型绑定的信息
 *
 * @param entityType 测试对象的实体类型
 * @param info       心情翻译描述
 * @param mood       心情类型枚举
 */
public record MoodInfo(EntityType<?> entityType, String info, Mood mood) {
    public static final MoodInfo EMPTY = new MoodInfo(null, "", Mood.NEUTRAL);

    public static final Codec<MoodInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("entity_type").forGetter(info -> BuiltInRegistries.ENTITY_TYPE.getKey(info.entityType)),
            Codec.STRING.fieldOf("info").forGetter(info -> info.info),
            Mood.CODEC.fieldOf("mood").forGetter(info -> info.mood)
    ).apply(instance, (a, b, c) -> new MoodInfo(BuiltInRegistries.ENTITY_TYPE.get(a), b, c)));

    public static MoodInfo of(EntityType<?> entityType, String info, Mood mood) {
        return new MoodInfo(entityType, info, mood);
    }
}
