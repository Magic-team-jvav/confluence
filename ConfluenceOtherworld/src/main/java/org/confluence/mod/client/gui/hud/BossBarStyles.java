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
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.boss.EyeOfCthulhu;
import org.confluence.mod.common.entity.boss.Plantera;
import org.confluence.mod.common.init.entity.BossEntities;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

// 内置血条在此注册，资源包可按 ID 覆盖布局。
public final class BossBarStyles extends SimpleJsonResourceReloadListener {
    public static final BossBarStyles INSTANCE = new BossBarStyles();
    // 克苏鲁之眼的血条主体从第 21 行开始，转场时向上飞溅的血滴使用上方留白。
    private static final int EYE_NAME_INSET = 20;
    private Map<ResourceLocation, BarStyle> styles = Map.of();

    private BossBarStyles() {
        super(new Gson(), "boss_bar");
    }

    private static void builtInStyles(BiConsumer<ResourceLocation, BarStyle> output) {
        BiConsumer<ResourceLocation, BarStyle> eyeStyle = (id, style) -> output.accept(id, style.withNameTopInset(EYE_NAME_INSET));
        standard(output, BossEntities.BRAIN_OF_CTHULHU.getId(), 0xD39191, 0xF4BFB1);
        standard(output, BossEntities.DEERCLOPS.getId(), 0x80A9EF, 0xCAD9DC);
        add(output, BossEntities.DUKE_FISHRON.getId(), 64, 32, 6, 48, 1, 100,
                new BarSection(37, 182, false, 0x76B8ED, 0xA9F3EE));
        standard(output, BossEntities.EATER_OF_WORLDS.getId(), 0xAA9C80, 0xD2C9D8);
        add(eyeStyle, BossEntities.EYE_OF_CTHULHU.getId(), 64, 32, 6, 48, 1, 100,
                new BarSection(38, 180, false, 0xD79393, 0xFFFFEB));
        add(eyeStyle, EyeOfCthulhu.PHASE_TWO_BAR, EyeOfCthulhu.PHASE_TWO_BAR.getPath(), BossEntities.EYE_OF_CTHULHU.getId().getPath(), 64, 32, 6, 48, 1, 100,
                new BarSection(38, 180, false, 0xD79393, 0xFFFFEB));
        add(eyeStyle, EyeOfCthulhu.TRANSITION_BAR, 64, 32, 6, 48, 25, 60,
                new BarSection(38, 180, false, 0xD79393, 0xFFFFEB));
        standard(output, BossEntities.HILL_OF_FLESH.getId(), 0xD78D80, 0xEEBDA8);
        standard(output, BossEntities.KING_SLIME.getId(), 0x4DA6FF, 0x7FD7EB);
        add(output, BossEntities.PLANTERA.getId(), 64, 32, 6, 48, 1, 100,
                new BarSection(38, 180, false, 0xDB77A2, 0xF4B5C9));
        add(output, Plantera.PHASE_TWO_BAR, Plantera.PHASE_TWO_BAR.getPath(), BossEntities.PLANTERA.getId().getPath(), 64, 32, 6, 48, 1, 100,
                new BarSection(38, 180, false, 0x9FC745, 0xD0CA64));
        add(output, Plantera.TRANSITION_BAR, 64, 32, 6, 48, 12, 72,
                new BarSection(38, 180, false, 0xDB77A2, 0xBBD675));
        standard(output, BossEntities.QUEEN_BEE.getId(), 0xF3AC38, 0xFADE69);
        add(output, BossEntities.QUEEN_SLIME.getId(), 46, 17, 6, 40, 1, 100,
                new BarSection(38, 181, false, 0xE5ADC8, 0xB0E8E3));
        standard(output, BossEntities.SKELETRON.getId(), 0xD8BE9D, 0xF8FFF5);
        add(output, BossEntities.THE_DESTROYER.getId(), "destroyer", "destroyer", 46, 17, 4, 52, 1, 100,
                new BarSection(37, 182, false, 0xFFB6A4, 0x7EF3F9));
        add(output, AbstractTwinEye.BOSS_BAR, "twins", "twins", 46, 17, 4, 40, 1, 100,
                new BarSection(38, 65, true, 0xFF6868, 0xFFB6A4), new BarSection(153, 65, false, 0x75E65B, 0xD3FF9A));
        standard(output, BossEntities.WALL_OF_FLESH.getId(), 0xD78D80, 0xEEBDA8);
    }

    private static void standard(BiConsumer<ResourceLocation, BarStyle> output, ResourceLocation id, int color, int endColor) {
        add(output, id, 46, 0, 46, 40, 1, 100, new BarSection(37, 182, false, color, endColor));
    }

    private static void add(BiConsumer<ResourceLocation, BarStyle> output, ResourceLocation id, int height, int fillY, int fillHeight,
                            int increment, int frames, int frameMillis, BarSection... sections) {
        add(output, id, id.getPath(), id.getPath(), height, fillY, fillHeight, increment, frames, frameMillis, sections);
    }

    private static void add(BiConsumer<ResourceLocation, BarStyle> output, ResourceLocation id, String frame, String fill,
                            int height, int fillY, int fillHeight, int increment, int frames, int frameMillis,
                            BarSection... sections) {
        String directory = "textures/entity/boss_bar/";
        output.accept(id, new BarStyle(Confluence.asResource(directory + frame + "_bar_1.png"),
                Confluence.asResource(directory + fill + "_bar_2.png"), 256, height * frames, 256, height,
                fillY, fillY, fillHeight, increment, List.of(sections), frames, frameMillis, 0));
    }

    public @Nullable BarStyle get(ResourceLocation id) {
        return styles.get(id);
    }

    // 每次重载重新建立映射，删除或解析失败的样式不会残留。
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, BarStyle> loaded = new HashMap<>();
        builtInStyles(loaded::put);
        resources.forEach((id, json) -> BarStyle.CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(message -> Confluence.LOGGER.error("Failed to load boss bar style {}: {}", id, message))
                .ifPresent(style -> loaded.put(id, style)));
        styles = Map.copyOf(loaded);
    }

    // 底框与填充共用像素坐标系；sections 的顺序对应 Boss 同步的血量段。
    public record BarStyle(ResourceLocation frame, ResourceLocation fill, int textureWidth,
                           int textureHeight,
                           int frameWidth, int frameHeight, int fillY, int healthY, int fillHeight,
                           int verticalIncrement, List<BarSection> sections, int frames,
                           int frameMillis, int nameTopInset) {
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
                BarSection.CODEC.listOf().fieldOf("sections").forGetter(BarStyle::sections),
                Codec.intRange(1, 64).optionalFieldOf("frames", 1).forGetter(BarStyle::frames),
                Codec.intRange(1, 10000).optionalFieldOf("frame_millis", 100).forGetter(BarStyle::frameMillis),
                Codec.intRange(0, 4096).optionalFieldOf("name_top_inset", 0).forGetter(BarStyle::nameTopInset)
        ).apply(instance, BarStyle::new));

        private BarStyle withNameTopInset(int inset) {
            return new BarStyle(frame, fill, textureWidth, textureHeight, frameWidth, frameHeight,
                    fillY, healthY, fillHeight, verticalIncrement, sections, frames, frameMillis, inset);
        }

        public BarStyle {
            sections = List.copyOf(sections);
        }
    }

    // reverse 为真时保留右端，否则保留左端；装饰区域不计入有效血量宽度。
    public record BarSection(int left, int width, boolean reverse, int numberColor,
                             int numberEndColor) {
        public static final Codec<BarSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 4096).fieldOf("left").forGetter(BarSection::left),
                Codec.intRange(1, 4096).fieldOf("width").forGetter(BarSection::width),
                Codec.BOOL.fieldOf("reverse").forGetter(BarSection::reverse),
                Codec.intRange(0, 0xFFFFFF).optionalFieldOf("number_color", 0xBFA268).forGetter(BarSection::numberColor),
                Codec.intRange(0, 0xFFFFFF).optionalFieldOf("number_end_color", 0xBFA268).forGetter(BarSection::numberEndColor)
        ).apply(instance, BarSection::new));
    }
}
