package org.confluence.mod.common.entity.npc.mood;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum Mood implements StringRepresentable {
    LOVER,
    LIKE,
    NEUTRAL,
    HATE,
    DISLIKE;

    public static final Codec<Mood> CODEC = StringRepresentable.fromEnum(Mood::values);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
