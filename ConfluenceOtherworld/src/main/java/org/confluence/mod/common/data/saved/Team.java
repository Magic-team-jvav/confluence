package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.mesdag.portlib.network.codec.PortStreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import org.confluence.lib.util.LibStreamCodecUtils;

public enum Team implements StringRepresentable {
    WHITE(DyeColor.WHITE),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY),
    GRAY(DyeColor.GRAY),
    BLACK(DyeColor.BLACK),
    BROWN(DyeColor.BROWN),
    RED(DyeColor.RED),
    ORANGE(DyeColor.ORANGE),
    YELLOW(DyeColor.YELLOW),
    LIME(DyeColor.LIME),
    GREEN(DyeColor.GREEN),
    CYAN(DyeColor.CYAN),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE),
    BLUE(DyeColor.BLUE),
    PURPLE(DyeColor.PURPLE),
    MAGENTA(DyeColor.MAGENTA),
    PINK(DyeColor.PINK);

    public static final Team[] TEAMS = values();
    public static final Codec<Team> CODEC = StringRepresentable.fromEnum(() -> TEAMS);
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, Team> STREAM_CODEC = LibStreamCodecUtils.fromEnum(TEAMS);

    private final DyeColor color;
    private final Component titleCaseName;
    private final Component lowerCaseName;

    Team(DyeColor color) {
        this.color = color;
        String key = "team.confluence." + getSerializedName();
        this.titleCaseName = Component.translatable(key);
        this.lowerCaseName = Component.translatable(key + ".lower_case");
    }

    public DyeColor getColor() {
        return color;
    }

    public Component getTitleCaseName() {
        return titleCaseName;
    }

    public Component getLowerCaseName() {
        return lowerCaseName;
    }

    @Override
    public String getSerializedName() {
        return color.getSerializedName();
    }

    public static Team fromDyeColor(DyeColor color) {
        return switch (color) {
            case ORANGE -> ORANGE;
            case MAGENTA -> MAGENTA;
            case LIGHT_BLUE -> LIGHT_BLUE;
            case YELLOW -> YELLOW;
            case LIME -> LIME;
            case PINK -> PINK;
            case GRAY -> GRAY;
            case LIGHT_GRAY -> LIGHT_GRAY;
            case CYAN -> CYAN;
            case PURPLE -> PURPLE;
            case BLUE -> BLUE;
            case BROWN -> BROWN;
            case GREEN -> GREEN;
            case RED -> RED;
            case BLACK -> BLACK;
            default -> WHITE;
        };
    }
}
