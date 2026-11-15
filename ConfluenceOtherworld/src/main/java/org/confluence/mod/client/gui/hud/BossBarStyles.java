package org.confluence.mod.client.gui.hud;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 客户端从资源加载像素布局，资源包可覆盖样式；未知样式保留原版血条。
public final class BossBarStyles extends SimpleJsonResourceReloadListener {
    public static final BossBarStyles INSTANCE = new BossBarStyles();
    private Map<ResourceLocation, BarStyle> styles = Map.of();

    private BossBarStyles() {
        super(new Gson(), "boss_bar");
    }

    public @Nullable BarStyle get(ResourceLocation id) {
        return styles.get(id);
    }

    // 每次重载重新建立映射，删除或解析失败的样式不会残留。
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, BarStyle> loaded = new HashMap<>();
        resources.forEach((id, json) -> BarStyle.CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(message ->
                        Confluence.LOGGER.error("Failed to load boss bar style {}: {}", id, message))
                .ifPresent(style -> loaded.put(id, style)));
        styles = Map.copyOf(loaded);
    }

    // 底框与填充共用像素坐标系；sections 的顺序对应 Boss 同步的血量段。
    public record BarStyle(ResourceLocation frame, ResourceLocation fill, int textureWidth,
                           int textureHeight,
                           int frameWidth, int frameHeight, int fillY, int healthY, int fillHeight,
                           int verticalIncrement, List<BarSection> sections) {
        public static final Codec<BarStyle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("frame").forGetter(BarStyle::frame),
                ResourceLocation.CODEC.fieldOf("fill").forGetter(BarStyle::fill),
                Codec.intRange(1, 4096).fieldOf("texture_width").forGetter(BarStyle::textureWidth),
                Codec.intRange(1, 4096).fieldOf("texture_height").forGetter(BarStyle::textureHeight),
                Codec.intRange(1, 4096).fieldOf("frame_width").forGetter(BarStyle::frameWidth),
                Codec.intRange(1, 4096).fieldOf("frame_height").forGetter(BarStyle::frameHeight),
                Codec.intRange(0, 4096).fieldOf("fill_source_y").forGetter(BarStyle::fillY),
                Codec.INT.fieldOf("fill_target_y").forGetter(BarStyle::healthY),
                Codec.intRange(1, 4096).fieldOf("fill_height").forGetter(BarStyle::fillHeight),
                Codec.intRange(1, 4096).fieldOf("vertical_increment").forGetter(BarStyle::verticalIncrement),
                BarSection.CODEC.listOf().fieldOf("sections").forGetter(BarStyle::sections)
        ).apply(instance, BarStyle::new));

        public BarStyle {
            sections = List.copyOf(sections);
        }
    }

    // reverse 为真时保留右端，否则保留左端；装饰区域不计入有效血量宽度。
    public record BarSection(int left, int width, boolean reverse, float numberCenter, int numberY,
                             int numberColor, int numberEndColor) {
        public static final Codec<BarSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 4096).fieldOf("left").forGetter(BarSection::left),
                Codec.intRange(1, 4096).fieldOf("width").forGetter(BarSection::width),
                Codec.BOOL.fieldOf("reverse").forGetter(BarSection::reverse),
                Codec.FLOAT.fieldOf("number_center").forGetter(BarSection::numberCenter),
                Codec.INT.fieldOf("number_y").forGetter(BarSection::numberY),
                Codec.intRange(0, 0xFFFFFF).optionalFieldOf("number_color", 0xBFA268).forGetter(BarSection::numberColor),
                Codec.intRange(0, 0xFFFFFF).optionalFieldOf("number_end_color", 0xBFA268).forGetter(BarSection::numberEndColor)
        ).apply(instance, BarSection::new));
    }
}
