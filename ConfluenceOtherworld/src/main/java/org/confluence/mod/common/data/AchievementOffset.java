package org.confluence.mod.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.TranslatableEnum;
import org.confluence.lib.util.LibStreamCodecUtils;

import java.util.Locale;

public record AchievementOffset(float x, float y, boolean hideLink, Category category, int order) {
    public static final Codec<AchievementOffset> SERVER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(AchievementOffset::x),
            Codec.FLOAT.fieldOf("y").forGetter(AchievementOffset::y),
            Codec.BOOL.lenientOptionalFieldOf("hide_link", true).forGetter(AchievementOffset::hideLink)
    ).apply(instance, (x, y, hideLink) -> new AchievementOffset(x, y, hideLink, Category.COLLECTOR, 0)));
    public static final Codec<AchievementOffset> CLIENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Category.CODEC.fieldOf("category").forGetter(AchievementOffset::category),
            Codec.INT.fieldOf("order").forGetter(AchievementOffset::order)
    ).apply(instance, (category, order) -> new AchievementOffset(0, 0, false, category, order)));

    public AchievementOffset(float x, float y, Category category, int order) {
        this(x, y, true, category, order);
    }

    public enum Category implements StringRepresentable, TranslatableEnum {
        COLLECTOR,
        EXPLORER,
        SLAYER,
        CHALLENGER;

        public static final Category[] CATEGORIES = values();
        public static final Codec<Category> CODEC = StringRepresentable.fromEnum(() -> CATEGORIES);
        public static final StreamCodec<FriendlyByteBuf, Category> STREAM_CODEC = LibStreamCodecUtils.fromEnum(CATEGORIES);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable("achievements.confluence.category." + getSerializedName());
        }
    }
}
