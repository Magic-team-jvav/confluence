package org.confluence.mod.common.entity;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;

public interface IVariant extends StringRepresentable {
    Codec<? extends IVariant> codec();

    String serializeKey();

    default void serialize(CompoundTag tag) {
        tag.putString(serializeKey(), getSerializedName());
    }
}
