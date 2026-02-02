package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.Collections;
import java.util.List;

public record EntityTypeTagValue(TagKey<EntityType<?>> tagKey) implements PrimitiveValue<TagKey<EntityType<?>>> {
    public static final Codec<EntityTypeTagValue> CODEC = TagKey.codec(Registries.ENTITY_TYPE).xmap(EntityTypeTagValue::new, EntityTypeTagValue::get);
    public static final CombineRule<TagKey<EntityType<?>>, EntityTypeTagValue> GET_SELF = CombineRule.register(PrimitiveValue.identity(), "entity_types_get_self");

    @Override
    public TagKey<EntityType<?>> get() {
        return tagKey;
    }

    @Override
    public Codec<EntityTypeTagValue> codec() {
        return CODEC;
    }

    @Override
    public List<String> getDescription() {
        return Collections.singletonList(tagKey.toString());
    }
}
