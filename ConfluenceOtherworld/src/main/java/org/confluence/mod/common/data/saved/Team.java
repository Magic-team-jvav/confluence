package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import org.confluence.lib.util.LibStreamCodecUtils;

import java.util.Locale;

public enum Team implements StringRepresentable {
    WHITE(DyeColor.WHITE),
    ORANGE(DyeColor.ORANGE),
    MAGENTA(DyeColor.MAGENTA),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE),
    YELLOW(DyeColor.YELLOW),
    LIME(DyeColor.LIME),
    PINK(DyeColor.PINK),
    GRAY(DyeColor.GRAY),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY),
    CYAN(DyeColor.CYAN),
    PURPLE(DyeColor.PURPLE),
    BLUE(DyeColor.BLUE),
    BROWN(DyeColor.BROWN),
    GREEN(DyeColor.GREEN),
    RED(DyeColor.RED),
    BLACK(DyeColor.BLACK);

    public static final Team[] TEAMS = values();
    public static final Codec<Team> CODEC = StringRepresentable.fromEnum(() -> TEAMS);
    public static final StreamCodec<FriendlyByteBuf, Team> STREAM_CODEC = LibStreamCodecUtils.fromEnum(TEAMS);

    private final DyeColor color;

    Team(DyeColor color) {
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Team fromDyeColor(DyeColor color) {
        return TEAMS[color.getId()];
    }
}
