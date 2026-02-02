package nowebsite.makertechno.the_trackers.api.component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import nowebsite.makertechno.the_trackers.client.gui.components.BasicComponentFactory;
import nowebsite.makertechno.the_trackers.client.gui.components.IRenderElement;
import nowebsite.makertechno.the_trackers.client.gui.components.Icon;
import nowebsite.makertechno.the_trackers.client.gui.components.ItemComponent;
import nowebsite.makertechno.the_trackers.client.gui.provider.TextureCache;
import nowebsite.makertechno.the_trackers.core.tool.TextureBuildTool;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 追踪指针构造器，构造并返回一个{@link BuilderResult}供后续注册指针使用。
 */
@SuppressWarnings("unused")
public class ComponentBuilder {
    private ComponentType type = ComponentType.DIRECT;
    private String cursorPattern = null;
    private Supplier<IRenderElement> icon1 = () -> Icon.NONE;
    private String component1Pattern = null;
    private Supplier<IRenderElement> icon2 = () -> Icon.NONE;
    private String component2Pattern = null;
    private Supplier<IRenderElement> icon3 = () -> Icon.NONE;
    private String component3Pattern = null;
    private boolean isSmoothMove = false;
    private boolean affectedByPlayerSettingsScale = false;
    private boolean autoLifecycle = false;
    private Function<Float, Float> rescaleFunc = scale -> scale;
    private BiFunction<Float, Float, Float> alphaTransformer = (distance, alpha) -> alpha;


    public ComponentBuilder() {}

    /**
     * 设置指针类型，默认为三维投影型。
     */
    public ComponentBuilder setComponentType(ComponentType type) {
        this.type = type;
        return this;
    }

    /**
     * 设置指针数据。
     */
    public ComponentBuilder setCursorPattern(String pattern) {
        this.cursorPattern = pattern;
        return this;
    }

    /**
     * <p>对于一般情况，该方法设置其中心图标。具有多个图标位的指针需要填充其它icon。</p>
     * <p>传入的索引应为图标索引。图标默认为空。</p>
     */
    public ComponentBuilder setIcon1(ResourceLocation location) {
        icon1 = () -> getIcon(location);
        return this;
    }

    /**
     * <p>对于一般情况，该方法设置其中心图标。具有多个图标位的指针需要填充其它icon。</p>
     */
    public ComponentBuilder setIcon1(ItemStack itemStack) {
        icon1 = () -> getItemComponent(itemStack);
        return this;
    }

    /**
     * <p>对于一般情况，该方法设置其中心图标。具有多个图标位的指针需要填充其它icon。</p>
     * <p>传入的值应为内部实体D。图标默认为空</p>
     */
    public ComponentBuilder setIcon1(String key) {
        icon1 = () -> TextureCache.getIcon(key);
        return this;
    }

    /**
     * 设置图标1的容器。默认为null(获取时使用default)，设置请参见{@link BasicComponentFactory}
     */
    public ComponentBuilder setIcon1Pattern(String pattern) {
        this.component1Pattern = pattern;
        return this;
    }


    /**
     * <p>对于一般情况，该方法设置其中心图标。具有多个图标位的指针需要填充其它icon。</p>
     * <p>将传入的物品的贴图作为图标。图标默认为空。</p>
     */
    public ComponentBuilder setIcon2(ItemStack itemStack) {
        icon2 = () -> getItemComponent(itemStack);
        return this;
    }

    /**
     * <p>对于一般情况，该方法设置其中心图标。具有多个图标位的指针需要填充其它icon。</p>
     * <p>传入的值应为内部实体贴图ID。图标默认为空</p>
     */
    public ComponentBuilder setIcon2(String key) {
        icon2 = () -> TextureCache.getIcon(key);
        return this;
    }

    /**
     * 设置图标1的容器。默认为null(获取时使用default)，设置请参见{@link BasicComponentFactory}
     */
    public ComponentBuilder setIcon2Pattern(String pattern) {
        this.component2Pattern = pattern;
        return this;
    }


    /**
     * <p>对于一般情况，该方法设置其中心图标。具有多个图标位的指针需要填充其它icon。</p>
     * <p>将传入的物品的贴图作为图标。图标默认为空。</p>
     */
    public ComponentBuilder setIcon3(ItemStack itemStack) {
        icon3 = () -> getItemComponent(itemStack);
        return this;
    }

    /**
     * <p>对于一般情况，该方法设置其中心图标。具有多个图标位的指针需要填充其它icon。</p>
     * <p>传入的值应为内部实体贴图ID。图标默认为空</p>
     */
    public ComponentBuilder setIcon3(String key) {
        icon3 = () -> TextureCache.getIcon(key);
        return this;
    }

    /**
     * 设置图标1的容器。默认为null(获取时使用default)，设置请参见{@link BasicComponentFactory}
     */
    public ComponentBuilder setIcon3Pattern(String pattern) {
        this.component3Pattern = pattern;
        return this;
    }

    /**
     * 设置是否启用插值帧移动。
     */
    public ComponentBuilder setSmoothMove(boolean smoothMove) {
        isSmoothMove = smoothMove;
        return this;
    }

    /**
     * 设置是否受到用户设置大小的影响。
     */
    public ComponentBuilder setAffectedByPlayerSettingsScale(boolean affectedByPlayerSettingsScale) {
        this.affectedByPlayerSettingsScale = affectedByPlayerSettingsScale;
        return this;
    }

    /**
     * 设置是否自动管理生命周期。仅对实体类指针生效，当实体不再扫描到时清理指针。
     */
    public ComponentBuilder setAutoLifecycle(boolean autoLifecycle) {
        this.autoLifecycle = autoLifecycle;
        return this;
    }

    /**
     * 设置乘数再运算器，一般建议在这里对最终大小进行区间移动和缩放。
     * */
    public ComponentBuilder defineRescale(Function<Float, Float> rescale) {
        this.rescaleFunc = rescale;
        return this;
    }

    /**
     * 设置透明度再运算器。
     * */
    public ComponentBuilder defineAlphaTransformer(BiFunction<Float, Float, Float> alphaTransformer) {
        this.alphaTransformer = alphaTransformer;
        return this;
    }

    public BuilderResult build() {
        return new BuilderResult(
                type,
                icon1,
                component1Pattern,
                icon2,
                component2Pattern,
                icon3,
                component3Pattern,
                cursorPattern,
                isSmoothMove,
                autoLifecycle,
                affectedByPlayerSettingsScale,
                rescaleFunc,
                alphaTransformer
        );
    }

    private static Icon getIcon(ResourceLocation location) {
        return TextureBuildTool.initIcon("dynamic", location.withSuffix(".png"), Icon::new).orElse(Icon.NONE);
    }

    private static ItemComponent getItemComponent(ItemStack stack) {
        return new ItemComponent(stack);
    }

    public static final class BuilderResult {
        public final @Nullable String component1Pattern, component2Pattern, component3Pattern, cursorPattern;
        public final ComponentType type;
        public final Supplier<IRenderElement> element1, element2, element3;
        public final boolean isSmoothMove, autoLifecycle, affectedBySettings;
        public final Function<Float, Float> rescale;
        public final BiFunction<Float, Float, Float>  transformAlpha;
        private BuilderResult(
                ComponentType type,
                Supplier<IRenderElement> element1,
                @Nullable String component1Pattern,
                Supplier<IRenderElement> element2,
                @Nullable String component2Pattern,
                Supplier<IRenderElement> element3,
                @Nullable String component3Pattern,
                @Nullable String cursorPattern,
                boolean isSmoothMove,
                boolean autoLifecycle,
                boolean affectedBySettings,
                Function<Float, Float> rescale,
                BiFunction<Float, Float, Float> transformAlpha
        ) {
            this.type = type;
            this.element1 = element1;
            this.component1Pattern = component1Pattern;
            this.element2 = element2;
            this.component2Pattern = component2Pattern;
            this.element3 = element3;
            this.component3Pattern = component3Pattern;
            this.cursorPattern = cursorPattern;
            this.isSmoothMove = isSmoothMove;
            this.autoLifecycle = autoLifecycle;
            this.affectedBySettings = affectedBySettings;
            this.rescale = rescale;
            this.transformAlpha = transformAlpha;
        }
    }

    public enum ComponentType {
        RELATIVE,
        DIRECT,
        HEAD_TAG
    }
}
