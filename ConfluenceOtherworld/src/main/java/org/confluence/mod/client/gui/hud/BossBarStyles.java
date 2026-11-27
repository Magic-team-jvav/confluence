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
import org.confluence.mod.common.entity.boss.BrainOfCthulhu;
import org.confluence.mod.common.entity.boss.EyeOfCthulhu;
import org.confluence.mod.common.entity.boss.Plantera;
import org.confluence.mod.common.init.entity.BossEntities;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// 内置血条在此注册，资源包可按 ID 覆盖布局。
public final class BossBarStyles extends SimpleJsonResourceReloadListener {
    public static final BossBarStyles INSTANCE = new BossBarStyles();
    private static final int BAR_WIDTH = 256;
    private static final int COMPACT_FRAME_HEIGHT = 46;
    private static final int TALL_FRAME_HEIGHT = 64;
    private Map<ResourceLocation, BarStyle> styles = Map.of();

    private BossBarStyles() {
        super(new Gson(), "boss_bar");
    }

    private static void builtInStyles(BiConsumer<ResourceLocation, BarStyle> output) {
        new Builder(BossEntities.BRAIN_OF_CTHULHU.getId()).tall().nameTopInset(12)
                .numberGradient(0xD39191, 0xF4BFB1).numberY(47)
                .phase(BrainOfCthulhu.PHASE_TWO_BAR, phase -> phase.numberY(59))
                .transition(BrainOfCthulhu.TRANSITION_BAR, transition -> transition.animation(24, 40).fill(BrainOfCthulhu.PHASE_TWO_BAR.getPath())
                        .numberYAt(11, 51)
                        .numberYAt(12, 65)
                        .numberYAt(13, 59)
                        .numberYAt(18, 60)
                        .numberYAt(20, 59))
                .register(output);
        new Builder(BossEntities.DEERCLOPS.getId())
                .numberGradient(0x80A9EF, 0xCAD9DC).register(output);
        new Builder(BossEntities.DUKE_FISHRON.getId()).tall().section(37, 182, false)
                .numberGradient(0x76B8ED, 0xA9F3EE).register(output);
        new Builder(BossEntities.EATER_OF_WORLDS.getId())
                .numberGradient(0xAA9C80, 0xD2C9D8).register(output);
        new Builder(BossEntities.EYE_OF_CTHULHU.getId()).tall().nameTopInset(20)
                .numberGradient(0xD79393, 0xFFFFEB)
                .phase(EyeOfCthulhu.PHASE_TWO_BAR, Builder::useBaseFill)
                .transition(EyeOfCthulhu.TRANSITION_BAR, transition -> transition.animation(25, 60).useBaseFill())
                .register(output);
        new Builder(BossEntities.HILL_OF_FLESH.getId())
                .numberGradient(0xD78D80, 0xEEBDA8).register(output);
        new Builder(BossEntities.KING_SLIME.getId())
                .numberGradient(0x4DA6FF, 0x7FD7EB).register(output);
        new Builder(BossEntities.PLANTERA.getId()).tall()
                .numberGradient(0xDB77A2, 0xF4B5C9)
                .phase(Plantera.PHASE_TWO_BAR, phase -> phase.useBaseFill().numberGradient(0x9FC745, 0xD0CA64))
                .transition(Plantera.TRANSITION_BAR, transition -> transition.animation(12, 72).animatedFill()
                        .numberGradient(0xDB77A2, 0xBBD675))
                .register(output);
        new Builder(BossEntities.QUEEN_BEE.getId())
                .numberGradient(0xF3AC38, 0xFADE69).register(output);
        new Builder(BossEntities.QUEEN_SLIME.getId()).fillRegion(17, 17, 6).section(38, 181, false)
                .numberGradient(0xE5ADC8, 0xB0E8E3).register(output);
        new Builder(BossEntities.SKELETRON.getId())
                .numberGradient(0xD8BE9D, 0xF8FFF5).register(output);
        new Builder(BossEntities.THE_DESTROYER.getId()).textures("destroyer", "destroyer").fillRegion(17, 17, 4).verticalIncrement(52)
                .numberGradient(0xFFB6A4, 0x7EF3F9).register(output);
        new Builder(AbstractTwinEye.BOSS_BAR).textures("twins", "twins").fillRegion(17, 17, 4)
                .sections(new BarSection(38, 65, true, 0xFF6868, 0xFFB6A4), new BarSection(153, 65, false, 0x75E65B, 0xD3FF9A)).register(output);
        new Builder(BossEntities.WALL_OF_FLESH.getId())
                .numberGradient(0xD78D80, 0xEEBDA8).register(output);
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

    /// 内置样式的构建器。阶段继承基础样式的布局和文字，贴图默认取阶段自身的 ID。
    private static final class Builder {
        private final ResourceLocation id;
        private String frame;
        private String fill;
        private String baseFill;
        private int width = BAR_WIDTH;
        private int height = COMPACT_FRAME_HEIGHT;
        private int textureWidth = BAR_WIDTH;
        private int textureHeight;
        private int frames = 1;
        private int frameMillis = 100;
        private int fillFrames = 1;
        private int sourceY;
        private int targetY;
        private int fillHeight = COMPACT_FRAME_HEIGHT;
        private int increment = 40;
        private int nameInset;
        private List<Integer> numberY = List.of();
        private List<BarSection> sections = List.of(new BarSection(37, 182, false, 0xBFA268));
        private final List<Variant> variants = new ArrayList<>();

        Builder(ResourceLocation id) {
            this.id = id;
            textures(id.getPath(), id.getPath());
        }

        private Builder(ResourceLocation id, Builder base) {
            this(id);
            baseFill = base.fill;
            frameSize(base.width, base.height);
            textureSize(base.textureWidth, base.textureHeight);
            animation(base.frames, base.frameMillis);
            fillFrames(base.fillFrames);
            fillRegion(base.sourceY, base.targetY, base.fillHeight);
            increment = base.increment;
            nameInset = base.nameInset;
            numberY = base.numberY;
            sections = base.sections;
        }

        /// 64 像素高的内置血条共用绘制区域，个别素材可继续调整。
        Builder tall() {
            return frameSize(BAR_WIDTH, TALL_FRAME_HEIGHT).fillRegion(32, 32, 6)
                    .verticalIncrement(48).section(38, 180, false);
        }

        /// 底框和填充的贴图名称，分别对应 _bar_1.png 和 _bar_2.png。
        Builder textures(String frame, String fill) {
            this.frame = frame;
            return fill(fill);
        }

        Builder fill(String fill) {
            this.fill = fill;
            return this;
        }

        Builder useBaseFill() {
            return fill(baseFill);
        }

        /// 单帧绘制尺寸，单位为像素。
        Builder frameSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /// 完整底框图集尺寸。高度为 0 时按帧高和帧数计算。
        Builder textureSize(int width, int height) {
            textureWidth = width;
            textureHeight = height;
            return this;
        }

        /// 底框帧数与每帧持续的毫秒数。
        Builder animation(int frames, int frameMillis) {
            this.frames = frames;
            this.frameMillis = frameMillis;
            return this;
        }

        /// 独立填充帧数，默认 1 表示复用静态填充。
        Builder fillFrames(int count) {
            fillFrames = count;
            return this;
        }

        /// 填充跟随底框图集的帧布局。
        Builder animatedFill() {
            return fillFrames(0);
        }

        /// 从填充帧的 sourceY 行取 height 行，在底框的 targetY 行开始绘制。
        Builder fillRegion(int sourceY, int targetY, int height) {
            this.sourceY = sourceY;
            this.targetY = targetY;
            fillHeight = height;
            return this;
        }

        /// 相邻血条顶部的最小像素间距。
        Builder verticalIncrement(int pixels) {
            increment = pixels;
            return this;
        }

        /// 名称位于上方时，文字行底边相对于帧顶的偏移。
        Builder nameTopInset(int pixels) {
            nameInset = pixels;
            return this;
        }

        Builder numberY(int pixels) {
            numberY = List.of(pixels);
            return this;
        }

        /// 从指定帧（从 0 起算）起使用新的读数纵坐标，按帧序声明。
        Builder numberYAt(int frame, int pixels) {
            if (frame < numberY.size() || frame >= frames) {
                throw new IllegalArgumentException("Number position outside animation sequence: " + frame);
            }
            int previous = numberY.isEmpty() ? height - 6 : numberY.get(numberY.size() - 1);
            List<Integer> positions = new ArrayList<>(numberY);
            positions.addAll(Collections.nCopies(frame - positions.size(), previous));
            positions.add(pixels);
            numberY = List.copyOf(positions);
            return this;
        }

        /// 单段血条的左边界、满血宽度和扣减方向。
        Builder section(int left, int width, boolean reverse) {
            BarSection old = sections.get(0);
            return sections(new BarSection(left, width, reverse, old.numberColor(), old.numberEndColor()));
        }

        Builder sections(BarSection... sections) {
            this.sections = List.of(sections);
            return this;
        }

        /// 将各段血量数字统一设为指定的 RGB 纯色。
        Builder numberColor(int color) {
            return numberGradient(color, color);
        }

        /// 为各段读数设置从左到右的 RGB 渐变。
        Builder numberGradient(int start, int end) {
            sections = sections.stream().map(s -> new BarSection(s.left(), s.width(), s.reverse(), start, end)).toList();
            return this;
        }

        Builder phase(ResourceLocation id, Consumer<Builder> configure) {
            variants.add(new Variant(id, configure));
            return this;
        }

        private record Variant(ResourceLocation id, Consumer<Builder> configure) {}

        Builder transition(ResourceLocation id, Consumer<Builder> configure) {
            return phase(id, configure);
        }

        void register(BiConsumer<ResourceLocation, BarStyle> output) {
            String directory = "textures/entity/boss_bar/";
            output.accept(id, new BarStyle(Confluence.asResource(directory + frame + "_bar_1.png"),
                    Confluence.asResource(directory + fill + "_bar_2.png"), textureWidth,
                    textureHeight == 0 ? height * frames : textureHeight, width, height, sourceY, targetY,
                    fillHeight, increment, sections, frames, frameMillis, nameInset, numberY, fillFrames));
            // 注册时继承完整基础样式，阶段自己的设置最后生效。
            for (Variant variant : variants) {
                Builder child = new Builder(variant.id(), this);
                variant.configure().accept(child);
                child.register(output);
            }
        }
    }

    /// 一套血条的贴图、裁切和排版定义，内置注册和资源包共用。
    /// 长度和坐标均以像素为单位。
    ///
    /// @param frame             底框贴图资源
    /// @param fill              填充贴图资源
    /// @param textureWidth      两张完整贴图的宽度，用于换算横向 UV
    /// @param textureHeight     完整底框贴图的高度，沿用底框帧布局的填充也用这个高度
    /// @param frameWidth        一帧中实际绘制的宽度，从贴图左边开始取样
    /// @param frameHeight       单帧高度，也是纵向图集相邻帧的间距
    /// @param fillY             填充图上相对于当前帧顶部的裁切起点
    /// @param healthY           屏幕上相对于底框顶部的填充绘制起点
    /// @param fillHeight        填充区域的高度
    /// @param verticalIncrement 多条血条叠放时相邻底框顶部的最小间距
    /// @param sections          按服务端血量顺序排列的填充段
    /// @param frames            底框帧数，资源包缺省为 1
    /// @param frameMillis       单帧显示时间，资源包缺省为 100 毫秒
    /// @param nameTopInset      名称在上方时，以底框顶部为原点的排版偏移，缺省为 0
    /// @param numberY           下方读数从底框顶部算起的逐帧纵坐标，空列表取帧高减 6，末项覆盖未列出的尾帧
    /// @param fillFrames        填充帧数，填 1 可在转场播放时复用静态贴图，缺省或填 0 时沿用底框帧布局
    public record BarStyle(ResourceLocation frame, ResourceLocation fill, int textureWidth,
                           int textureHeight,
                           int frameWidth, int frameHeight, int fillY, int healthY, int fillHeight,
                           int verticalIncrement, List<BarSection> sections, int frames,
                           int frameMillis, int nameTopInset, List<Integer> numberY,
                           int fillFrames) {
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
                Codec.intRange(0, 4096).optionalFieldOf("name_top_inset", 0).forGetter(BarStyle::nameTopInset),
                Codec.intRange(0, 4096).listOf().optionalFieldOf("number_y", List.of()).forGetter(BarStyle::numberY),
                Codec.intRange(0, 64).optionalFieldOf("fill_frames", 0).forGetter(BarStyle::fillFrames)
        ).apply(instance, BarStyle::new));

        public BarStyle {
            sections = List.copyOf(sections);
            numberY = List.copyOf(numberY);
        }

        // 旧资源包省略 fill_frames 时，填充仍使用底框的帧布局。
        int fillTextureHeight() {
            return fillFrames == 0 ? textureHeight : fillFrames * frameHeight;
        }

        int fillSourceY(int frame) {
            int fillFrame = fillFrames == 0 ? frame : Math.min(frame, fillFrames - 1);
            return fillY + fillFrame * frameHeight;
        }

        // 下方读数的坐标以帧顶为原点，未列出的尾帧沿用最后一个坐标。
        int numberTop(int frame) {
            return numberY.isEmpty() ? frameHeight - 6 : numberY.get(Math.min(frame, numberY.size() - 1));
        }

        int maxNumberTop() {
            return numberY.isEmpty() ? frameHeight - 6 : Collections.max(numberY);
        }
    }

    /// 一段独立裁切的血量填充及其数字颜色。
    ///
    /// @param left           从血条左侧到填充段左端的像素距离
    /// @param width          满血时填充段的像素宽度，装饰留白排除在外
    /// @param reverse        为 `true` 时保留右端、从左侧扣减，为 `false` 时保留左端、从右侧扣减
    /// @param numberColor    血量数字左侧的 RGB 颜色
    /// @param numberEndColor 血量数字右侧的 RGB 颜色，资源包省略此项时整段使用左侧颜色
    public record BarSection(int left, int width, boolean reverse, int numberColor,
                             int numberEndColor) {
        /// 指定单个血量段的纯色，其余段可独立配色。
        public BarSection(int left, int width, boolean reverse, int numberColor) {
            this(left, width, reverse, numberColor, numberColor);
        }

        public static final Codec<BarSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 4096).fieldOf("left").forGetter(BarSection::left),
                Codec.intRange(1, 4096).fieldOf("width").forGetter(BarSection::width),
                Codec.BOOL.fieldOf("reverse").forGetter(BarSection::reverse),
                Codec.intRange(0, 0xFFFFFF).optionalFieldOf("number_color", 0xBFA268).forGetter(BarSection::numberColor),
                Codec.intRange(0, 0xFFFFFF).optionalFieldOf("number_end_color").forGetter(section ->
                        section.numberColor() == section.numberEndColor() ? Optional.empty() : Optional.of(section.numberEndColor()))
        ).apply(instance, (left, width, reverse, color, endColor) ->
                new BarSection(left, width, reverse, color, endColor.orElse(color))));
    }
}
