package org.confluence.mod.client.gui.container.soulcerer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.phys.Vec2;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.widget.soulcerer.skill.SoulSkillBox;
import org.confluence.mod.client.gui.widget.soulcerer.skill.overview.*;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 灵魂技能总览界面 — 类似原版进度界面的技能树视图
 */
public class SoulOverviewScreen extends Screen {
    private static final int SURVIVAL_INSTINCT_NODE_X = -40;
    private static final int SURVIVAL_INSTINCT_NODE_Y = 40;
    /* 缩放：以屏幕中心为原点，节点位置与绘制尺寸同时按 zoom 缩放 */
    private static final double MIN_ZOOM = 0.5;
    private static final double MAX_ZOOM = 2.0;
    /// 滚轮每一格的缩放倍率。
    private static final double ZOOM_STEP = 1.1;
    /// 技能节点连线在 1.0× 下的芯线/辉光宽度，随缩放等比放大。
    private static final float CONNECTION_THICKNESS = 2.0F;
    private static final float CONNECTION_LIGHT_THICKNESS = 5.0F;
    /* 组件 */
    public final HotbarWidget hotbar;
    public final List<OverviewNode> nodes;
    public final Map<ResourceLocation, OverviewNode> nodeById = new HashMap<>();
    public final List<NavTab> navTabs = new ArrayList<>();
    private final NormalNoise backgroundNoise;
    private final CenterButton centerButton;
    /* 视口状态 */
    public double scrollX, scrollY;
    /* 选中状态 */
    @Nullable
    public SoulSkill theCurrentlySelectedSkill;
    public int selectedHotbarSlot = -1;
    private AbstractSliderButton hueSlider;
    private AbstractSliderButton contrastSlider;
    private boolean isSliderVisible = false;
    private Float targetScrollX, targetScrollY;
    private double minScrollX, maxScrollX, minScrollY, maxScrollY;
    private double zoom = 1.0;
    /* 拖拽 */
    private boolean isDragging;
    private double dragStartMouseX, dragStartMouseY;
    private double dragStartScrollX, dragStartScrollY;
    private List<CachedDot> cachedDots;

    public SoulOverviewScreen() {
        super(Component.translatable("confluence.screen.soulcerer_overview"));
        nodes = new ArrayList<>();
        hotbar = new HotbarWidget(this, 0, 0);
        centerButton = new CenterButton(this, 0, 0);
        backgroundNoise = NormalNoise.create(RandomSource.create(12345L), -5, 1.0, 1.0, 1.0, 1.0);

        initSkillsFromRegistry();
    }

    /// 背景开关按钮的文案：☑ = 画完整背景，☐ = 只留底色与技能。
    private static Component backgroundToggleLabel() {
        return Component.literal(ClientConfigs.isSoulcererBackgroundEnabled() ? "☑" : "☐");
    }

    /// 缓存背景光点的位置、颜色与偏移参数。
    private void buildDotCache(int count, float radius, float minSize, float maxSize, int color, float minOffsetFactor, float maxOffsetFactor) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        if (cachedDots != null && !cachedDots.isEmpty()) {
            float r1 = cachedDots.getFirst().r;
            float g1 = cachedDots.getFirst().g;
            float b1 = cachedDots.getFirst().b;
            if (r == r1 && g == g1 && b == b1) return;
        }
        cachedDots = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            long seed = (long) i * 73856093L ^ (long) i * 19349663L;
            seed = (seed ^ (seed >> 13)) * 1274126177L;
            seed = seed ^ (seed >> 16);

            float r1 = ((seed & 0xFFFF) / 65535.0f);
            float r2 = (((seed >> 16) & 0xFFFF) / 65535.0f);
            float r3 = (((seed >> 32) & 0xFFFF) / 65535.0f);
            float r4 = (((seed >> 48) & 0xFFFF) / 65535.0f);

            long rotSeed = seed * 4121539L;
            float r5 = ((rotSeed & 0xFFFF) / 65535.0f);
            float rotation = r5 * 2.0f * (float) Math.PI;

            float distance = radius * (float) Math.sqrt(r1);
            float angle = r2 * 2.0f * (float) Math.PI;
            float baseX = distance * (float) Math.cos(angle);
            float baseY = distance * (float) Math.sin(angle);

            float size = minSize + r3 * (maxSize - minSize);
            float halfSize = size / 2.0f;

            float a = ((rotSeed >> 16 & 0xFFFF) / 65535.0f);

            float pointOffsetFactor = minOffsetFactor + r4 * (maxOffsetFactor - minOffsetFactor);

            cachedDots.add(new CachedDot(baseX, baseY, rotation, halfSize, r, g, b, a, pointOffsetFactor));
        }
    }

    // ============ 初始化 ============

    /// 建立技能树节点，学习和可用状态由玩家数据提供。
    private void initSkillsFromRegistry() {
        // 横向布局，中点开始，坐标原点 = 屏幕中心，左边为buff列技能树，右边为攻击列，坐标原点 = 屏幕中心，40为一段  -x为往左，正x往右，-y往上，正y往下
// 按照时期排序，第一次献祭为boss前，第二次献祭为邪恶boss时
// 左列 buff技能树
        // 强力附魂
        addNode(OverviewNode.Builder.of(ModSoulSkills.ENHANCED_SOUL)
                .pos(-40, 0));
        addNode(OverviewNode.Builder.of(ModSoulSkills.SURVIVAL_INSTINCT)
                .pos(SURVIVAL_INSTINCT_NODE_X, SURVIVAL_INSTINCT_NODE_Y));
        // 魂引天星
        addNode(OverviewNode.Builder.of(ModSoulSkills.STAR_CALL)
                .pos(-80, -40)
                .connections(ModSoulSkills.ENHANCED_SOUL));
        // 血液沸腾
        addNode(OverviewNode.Builder.of(ModSoulSkills.BOILING_BLOOD)
                .pos(-80, 80)
                .connections(ModSoulSkills.ENHANCED_SOUL));
        // 掠夺之魄
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_PLUNDER)
                .pos(-80, 40)
                .connections(ModSoulSkills.ENHANCED_SOUL));
        // 自然法则
        addNode(OverviewNode.Builder.of(ModSoulSkills.LAW_OF_NATURE)
                .pos(-80, -80)
                .connections(ModSoulSkills.ENHANCED_SOUL));
        // 附魂激荡
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_SURGE)
                .pos(-120, 0)
                .connections(ModSoulSkills.ENHANCED_SOUL));
        // 灵魂透支
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_DRAIN)
                .pos(-120, -40));
        // 星魂联结
        addNode(OverviewNode.Builder.of(ModSoulSkills.STAR_LINK)
                .pos(-160, -40)
                .connections(ModSoulSkills.STAR_REVERSAL));
        // 星魂逆转
        addNode(OverviewNode.Builder.of(ModSoulSkills.STAR_REVERSAL)
                .pos(-160, -80)
                .connections(ModSoulSkills.STAR_LINK));
        // 血怒
        addNode(OverviewNode.Builder.of(ModSoulSkills.BLOOD_RAGE).pos(-200, -120));
        // 右列 攻击技能树
        // 引魂
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_LURE)
                .pos(40, 0));
        // 灵魂激荡
        addNode(OverviewNode.Builder.of(ModSoulSkills.SPIRIT_SURGE)
                .pos(80, -120));
        // 自然之惩戒
        addNode(OverviewNode.Builder.of(ModSoulSkills.NATURES_WRATH)
                .pos(80, 40)
                .connections(ModSoulSkills.SOUL_LURE));
        // 迷魂孢雾
        addNode(OverviewNode.Builder.of(ModSoulSkills.CONFUSE_SPORES)
                .pos(80, 80)
                .connections(ModSoulSkills.SOUL_LURE));
        // 亵渎之魂
        addNode(OverviewNode.Builder.of(ModSoulSkills.PROFANE_SOUL)
                .pos(80, -80)
                .connections(ModSoulSkills.SOUL_LURE));
        // 灭却业火
        addNode(OverviewNode.Builder.of(ModSoulSkills.KARMA_FLAME)
                .pos(80, -40));
        // 附魂印记
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_MARK)
                .pos(120, 50)
                .connections(ModSoulSkills.SOUL_LURE));
        // 强力引魂
        addNode(OverviewNode.Builder.of(ModSoulSkills.ENHANCED_LURE)
                .pos(120, -40)
                .connections(ModSoulSkills.SOUL_LURE));
        // 引魂激荡
        addNode(OverviewNode.Builder.of(ModSoulSkills.LURE_SURGE)
                .pos(120, 0)
                .connections(ModSoulSkills.SPIRIT_SURGE, ModSoulSkills.SOUL_LURE));
        // 强力激荡
        addNode(OverviewNode.Builder.of(ModSoulSkills.EMPOWERED_SURGE)
                .pos(120, -120)
                .connections(ModSoulSkills.SPIRIT_SURGE));
        // 灵魂激荡冲击
        addNode(OverviewNode.Builder.of(ModSoulSkills.SURGE_BLAST)
                .pos(160, -120)
                .connections(ModSoulSkills.SPIRIT_SURGE));
        // 灵魂引发
        addNode(OverviewNode.Builder.of(ModSoulSkills.SPIRIT_TRIGGER)
                .pos(200, -120)
                .connections(ModSoulSkills.SURGE_BLAST));
        // 灵魂突击
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_ASSAULT)
                .pos(120, 80)
                .connections(ModSoulSkills.SOUL_LURE));
        // 灵魂收割
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_HARVEST)
                .pos(-120, 40)
                .connections(ModSoulSkills.ENHANCED_SOUL));
        // 噬魂借力
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_DEVOUR)
                .pos(-160, 40)
                .connections(ModSoulSkills.SOUL_HARVEST));
        // 反噬抵抗
        addNode(OverviewNode.Builder.of(ModSoulSkills.BACKLASH_RESISTANCE)
                .pos(-200, 40)
                .connections(ModSoulSkills.SOUL_DEVOUR));
        // 分魂驾驭
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_CONTROL)
                .pos(-200, 80)
                .connections(ModSoulSkills.BACKLASH_RESISTANCE));
        // 魂术轮转
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_ROTATION)
                .pos(-240, 80)
                .connections(ModSoulSkills.SOUL_CONTROL));
        // 生命归还
        addNode(OverviewNode.Builder.of(ModSoulSkills.LIFE_RETURN)
                .pos(-240, 120)
                .connections(ModSoulSkills.SOUL_ROTATION));
        // 天穹倒转
        addNode(OverviewNode.Builder.of(ModSoulSkills.FIRMAMENT_REVERSED)
                .pos(-120, -80)
                .connections(ModSoulSkills.STAR_CALL));
    }

    /// 构建节点并登记其标识。
    private void addNode(OverviewNode.Builder builder) {
        OverviewNode node = builder.build(this);
        nodes.add(node);
        nodeById.put(node.getNodeId(), node);
    }

    /// 初始化导航、技能节点、快捷栏和背景调节控件。
    @Override
    protected void init() {
        super.init();
        buildNavTabs();

        for (OverviewNode node : nodes) {
            addRenderableWidget(node);
        }
        for (NavTab tab : navTabs) {
            addRenderableWidget(tab);
        }

        centerButton.setPosition(width - 22, height / 2 + 60);
        addRenderableWidget(centerButton);
        positionAllNodes();

        hotbar.rebuildFromHolder();
        updateHotbarPos();
        calcScrollBounds();

        addRenderableWidget(hotbar);
        for (HotbarWidget.HotbarSlot widget : hotbar.getSlots()) {
            addRenderableWidget(widget);
        }

        Button hueSliderButton = Button.builder(Component.literal("⚙"), button -> {
            isSliderVisible = !isSliderVisible;
        }).bounds(5, this.height - 25, 20, 20).build();
        addRenderableWidget(hueSliderButton);

        /// 背景总开关：和色相 / 明度滑块放在同一组。
        /// 关掉后 `renderBackground` 只铺静态底色，噪点、树、魔法与滚动全部停掉，界面只留底色和技能。
        Button backgroundToggle = Button.builder(backgroundToggleLabel(), button -> {
                    ClientConfigs.setSoulcererBackgroundEnabled(!ClientConfigs.isSoulcererBackgroundEnabled());
                    button.setMessage(backgroundToggleLabel());
                }).bounds(29, this.height - 25, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("confluence.screen.soulcerer_overview.background_toggle")))
                .build();
        addRenderableWidget(backgroundToggle);

        int hue = ClientConfigs.soulcererBackgroundHue;
        double hueP = hue / 360.0;

        hueSlider = new AbstractSliderButton(5, this.height - 50, 100, 20, Component.literal(hue == 270 ? "默认" : (String.valueOf(hue))), hueP) {
            /// 更新色相滑块的显示文本。
            @Override
            protected void updateMessage() {
                int newHue = (int) (this.value * 360);
                this.setMessage(Component.literal(newHue == 270 ? "默认" : (String.valueOf(newHue))));
            }

            /// 将色相滑块值写入客户端配置。
            @Override
            protected void applyValue() {
                int newHue = (int) (this.value * 360);
                ClientConfigs.SOULCERER_BACKGROUND_HUE.set(newHue);
                ClientConfigs.soulcererBackgroundHue = newHue;
            }
        };

        int contrast = ClientConfigs.soulcererBackgroundContrast;
        double contrastP = contrast / 255.0;

        contrastSlider = new AbstractSliderButton(5, this.height - 75, 100, 20, Component.literal(contrast == 255 ? "默认" : (String.valueOf(contrast))), contrastP) {
            /// 更新对比度滑块的显示文本。
            @Override
            protected void updateMessage() {
                int newContrast = (int) (this.value * 255);
                this.setMessage(Component.literal(newContrast == 255 ? "默认" : (String.valueOf(newContrast))));
            }

            /// 将对比度滑块值写入客户端配置。
            @Override
            protected void applyValue() {
                int newContrast = (int) (this.value * 255);
                ClientConfigs.SOULCERER_BACKGROUND_CONTRAST.set(newContrast);
                ClientConfigs.soulcererBackgroundContrast = newContrast;
            }
        };
    }

    /// 为非空技能分类创建侧边导航标签。
    private void buildNavTabs() {
        navTabs.clear();
        int tabY = height / 2 - 60;
        for (ModSoulSkills.SkillCategory cat : ModSoulSkills.SkillCategory.values()) {
            if (ModSoulSkills.SkillCategory.EMPTY == cat) {
                continue;
            }
            navTabs.add(new NavTab(this, cat, width - 22, tabY));
            tabY += 24;
        }
    }

    /// 窗口变化后重新定位快捷栏并计算滚动范围。
    @Override
    protected void repositionElements() {
        super.repositionElements();
        updateHotbarPos();
        calcScrollBounds();
    }

    /// 根据节点范围计算视口滚动边界。
    ///
    /// 节点屏幕坐标是 `中心 + (orig + scroll) * zoom`，所以同一段滚动量在放大后对应的
    /// 屏幕位移更大：边界按 1/zoom 收缩，保证任何缩放下都能把内容拖到边距内。
    private void calcScrollBounds() {
        if (nodes.isEmpty()) {
            minScrollX = maxScrollX = minScrollY = maxScrollY = 0;
            return;
        }
        double marginX = width / 3.0;
        double marginY = height / 3.0;
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        // 节点范围，orig 空间（不含 scroll 与 zoom）
        double minLeft = Double.MAX_VALUE;
        double maxRight = -Double.MAX_VALUE;
        double minTop = Double.MAX_VALUE;
        double maxBottom = -Double.MAX_VALUE;

        for (OverviewNode node : nodes) {
            double left = node.getOrigX();
            double right = left + SoulSkillBox.BOX_SIZE;
            double top = node.getOrigY();
            double bottom = top + SoulSkillBox.BOX_SIZE;

            if (left < minLeft) minLeft = left;
            if (right > maxRight) maxRight = right;
            if (top < minTop) minTop = top;
            if (bottom > maxBottom) maxBottom = bottom;
        }

        minScrollX = (width - marginX - centerX) / zoom - maxRight;
        maxScrollX = (marginX - centerX) / zoom - minLeft;
        minScrollY = (height - marginY - centerY) / zoom - maxBottom;
        maxScrollY = (marginY - centerY) / zoom - minTop;

        if (minScrollX > maxScrollX) {
            double center = (minScrollX + maxScrollX) / 2.0;
            minScrollX = maxScrollX = center;
        }
        if (minScrollY > maxScrollY) {
            double center = (minScrollY + maxScrollY) / 2.0;
            minScrollY = maxScrollY = center;
        }
    }

    /// 将视口偏移限制在滚动边界内。
    private void clampScroll() {
        scrollX = Math.clamp(scrollX, minScrollX, maxScrollX);
        scrollY = Math.clamp(scrollY, minScrollY, maxScrollY);
    }

    /// 根据视口偏移与缩放定位所有技能节点。
    private void positionAllNodes() {
        int cx = width / 2;
        int cy = height / 2;
        float scale = (float) zoom;
        for (OverviewNode node : nodes) {
            node.setUiScale(scale);
            node.setPosition((int) Math.round(cx + (node.getOrigX() + scrollX) * zoom),
                    (int) Math.round(cy + (node.getOrigY() + scrollY) * zoom));
        }
    }

    // ============ 滚动控制 ============

    /// 直接设置视口偏移并取消滚动动画。
    public void setScroll(double sx, double sy) {
        scrollX = sx;
        scrollY = sy;
        targetScrollX = null;
        targetScrollY = null;
        positionAllNodes();
    }

    /// 将滚动动画的目标设为界面原点。
    public void centerToOrigin() {
        targetScrollX = 0f;
        targetScrollY = 0f;
    }

    /// 将滚动动画的目标设为指定导航位置。
    public void navigateTo(OverviewNavButton.Target target) {
        targetScrollX = (float) target.scrollX();
        targetScrollY = (float) target.scrollY();
    }

    /// 当前缩放倍率。
    public double getZoom() {return zoom;}

    /// 以鼠标位置为锚点缩放：缩放前后，鼠标下的技能树位置保持不动。
    ///
    /// 节点屏幕坐标 `m = c + (o + scroll) * zoom`，令锚点处的 `o` 不变即可解出偏移补偿：
    /// `scroll += (m - c) * (1 / newZoom - 1 / oldZoom)`。
    public void setZoom(double targetZoom, double anchorX, double anchorY) {
        double newZoom = Math.clamp(targetZoom, MIN_ZOOM, MAX_ZOOM);
        if (Math.abs(newZoom - zoom) < 1.0E-4) {
            return;
        }
        double cx = width / 2.0;
        double cy = height / 2.0;
        double compensation = 1.0 / newZoom - 1.0 / zoom;
        scrollX += (anchorX - cx) * compensation;
        scrollY += (anchorY - cy) * compensation;
        zoom = newZoom;
        targetScrollX = null;
        targetScrollY = null;
        calcScrollBounds();
        clampScroll();
        positionAllNodes();
    }

    /// 滚轮缩放界面；按住快捷栏或滑块时仍然交给原逻辑处理。
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        if (scrollDeltaY == 0.0) {
            return super.mouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY);
        }
        setZoom(scrollDeltaY > 0.0 ? zoom * ZOOM_STEP : zoom / ZOOM_STEP, mouseX, mouseY);
        return true;
    }

    /// 平滑移动视口并更新节点位置。
    private void tickScrollAnimation(float partialTick) {
        if (targetScrollX != null) {
            double diff = targetScrollX - scrollX;
            if (Math.abs(diff) < 0.5) {
                scrollX = targetScrollX;
                targetScrollX = null;
            } else {
                scrollX += diff * 0.15;
            }
        }
        if (targetScrollY != null) {
            double diff = targetScrollY - scrollY;
            if (Math.abs(diff) < 0.5) {
                scrollY = targetScrollY;
                targetScrollY = null;
            } else {
                scrollY += diff * 0.15;
            }
        }
        positionAllNodes();
    }

    /// 获取总览界面使用的字体。
    public Font getFont() {return font;}

    // ============ 快捷栏交互 ============

    /// 获取当前选中的快捷栏槽位。
    public int getSelectedHotbarSlot() {return selectedHotbarSlot;}

    /// 设置当前选中的快捷栏槽位。
    public void setSelectedHotbarSlot(int slot) {selectedHotbarSlot = slot;}

    // ============ 渲染 ============

    /// 绘制动态背景并推进视口滚动动画。
    ///
    /// 总开关关掉后仍铺一层静态底色（沿用色相 / 对比度），只是不再画噪点、树、魔法，也不推进滚动，
    /// 否则界面会直接透出世界。
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;

        if (level == null) return;

        long gameTime = level.getGameTime();
        float time = gameTime + partialTick;

        float h1 = (ClientConfigs.soulcererBackgroundHue - 34) % 360 / 360F;
        float h2 = ClientConfigs.soulcererBackgroundHue / 360F;
        float h3 = (ClientConfigs.soulcererBackgroundHue - 2) % 360 / 360F;
        float lit = ClientConfigs.soulcererBackgroundContrast / 255F;
        float lit1 = lit * 0.6F;

        /// 静态底色：开关关掉时也保留，保证界面不透出世界。
        guiGraphics.fill(0, 0, this.width, this.height, (Color.HSBtoRGB(h3, 0.93f + Mth.sqrt(1 - lit * lit) * 0.07f, 0.05f * lit) & 0x00FFFFFF) | 0xFF000000);

        float offsetScale = 0.15f;
        float noiseScale = 0.25f;
        float noiseOffsetX = (float) -this.scrollX * offsetScale;
        float noiseOffsetY = (float) -this.scrollY * offsetScale;
        float noiseOffsetZ = Mth.sin(time * 0.002f) * 100;

        if (ClientConfigs.isSoulcererBackgroundEnabled()) {
            renderScreenBlendNoise(guiGraphics, noiseOffsetZ,
                    (Color.HSBtoRGB(h1, 0.78f + Mth.sqrt(1 - lit * lit) * 0.22f, 0.45f * lit1 + 0.4f) & 0x00FFFFFF) | 0xFF000000, noiseOffsetX, noiseOffsetY, noiseScale, 1.0F,
                    (Color.HSBtoRGB(h2, 0.67f + Mth.sqrt(1 - lit * lit) * 0.33f, 0.5167f * lit1 + 0.4f) & 0x00FFFFFF) | 0xFF000000, noiseOffsetX * 2.5F, noiseOffsetY * 2.5F, noiseScale * 0.8f, 1.0F);

            int centerX = (int) (this.width * 0.5);
            int centerY = (int) (this.height * 0.5);

            drawLivingTree(guiGraphics, centerX, centerY, time);
            renderBackgroundDots(guiGraphics, centerX, centerY, 1000, 1500, 1, 3, (float) this.scrollX, (float) this.scrollY, 0.1F, 1.0F, (Color.HSBtoRGB(h2, Mth.sqrt(1 - lit * lit), lit1 + 0.4F) & 0x00FFFFFF) | 0xFF000000);
            drawMagic(guiGraphics, centerX, centerY, time);
        }

        tickScrollAnimation(partialTick);
        renderConnections(guiGraphics, partialTick);
    }

    /// 绘制随视口偏移的魔法阵背景。
    private void drawMagic(GuiGraphics guiGraphics, int centerX, int centerY, float time) {
        final ResourceLocation font = ResourceLocation.fromNamespaceAndPath("minecraft", "illageralt");
        final float magicOffsetScale = 0.7f;
        final float magicOffsetX = (float) this.scrollX * magicOffsetScale;
        final float magicOffsetY = (float) this.scrollY * magicOffsetScale;
        final float rotate = time * 0.01f;

        float h = ClientConfigs.soulcererBackgroundHue / 360F;
        float lit = ClientConfigs.soulcererBackgroundContrast / 255F;
        float lit1 = lit * 0.6F;

        final int lineColor = (Color.HSBtoRGB(h, Mth.sqrt(1 - lit * lit), lit1 + 0.4f) & 0x00FFFFFF) | 0xFF000000;
        final int lightColor = Color.HSBtoRGB(h, 0.78f + Mth.sqrt(1 - lit * lit) * 0.22f, 0.45f * lit1 + 0.4f);

        final int lightOuterColor = lightColor & 0x00FFFFFF;
        final int lightInnerColor = lightOuterColor | 0xFF000000;
        final int emptyColor = 0x00000000;

        final float rotateFactor1 = Mth.sin(time * 0.01f) * 0.5f + 0.5f;
        final float trueRotate1 = 360 * rotateFactor1;
        final float rotateFactor2 = Mth.sin(time * 0.005f) * 0.5f + 0.5f;
        final float trueRotate2 = 360 * rotateFactor2;

        final float virtualTotalWidth = (float) (this.width - this.minScrollX + this.maxScrollX);
        final float side = virtualTotalWidth * 0.5f;
        final float rotate2 = (float) Math.toRadians(trueRotate1) - rotate;
        final float rotate3 = (float) Math.toRadians(trueRotate2) - rotate;
        final float r360 = Mth.PI * 2;
        for (int i = 0; i < 3; i++) {
            float trueRotateForLittleRoll = rotate2 + i * (Mth.PI * 2 / 3);
            float pointX = Mth.sin(trueRotateForLittleRoll);
            float pointY = Mth.cos(trueRotateForLittleRoll);
            float radius1 = 115.5F;
            float radius2 = 135F;

            drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX + pointX * radius1, centerY + magicOffsetY + pointY * radius1, 34.5F, 0, r360, 3f, 5f, lightInnerColor, lightOuterColor, 48);
            drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX + pointX * radius2, centerY + magicOffsetY + pointY * radius2, 15F, 0, r360, 2f, 5f, lightInnerColor, lightOuterColor, 48);
        }
        drawGradientLine(guiGraphics, centerX + magicOffsetX + 81, centerY + magicOffsetY, centerX + magicOffsetX + side, centerY + magicOffsetY, 2F, 5F, 20F, emptyColor, lightInnerColor, lightOuterColor);
        drawGradientLine(guiGraphics, centerX + magicOffsetX + 81, centerY + magicOffsetY, centerX + magicOffsetX + side, centerY + magicOffsetY, 1F, 5F, 30F, emptyColor, lightInnerColor, lightOuterColor);
        drawGradientLine(guiGraphics, centerX + magicOffsetX - 81, centerY + magicOffsetY, centerX + magicOffsetX - side, centerY + magicOffsetY, 2F, 5F, 20F, emptyColor, lightInnerColor, lightOuterColor);
        drawGradientLine(guiGraphics, centerX + magicOffsetX - 81, centerY + magicOffsetY, centerX + magicOffsetX - side, centerY + magicOffsetY, 1F, 5F, 30F, emptyColor, lightInnerColor, lightOuterColor);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 32F, 0f, r360, 1f, 5f, lightInnerColor, lightOuterColor, 48);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 150F, rotate3, rotate3 + r360, 1.414f, 5f, lightInnerColor, lightOuterColor, 4);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 150F, rotate3 + Mth.PI * 0.25f, rotate3 + r360 + Mth.PI * 0.25f, 1.414f, 5f, lightInnerColor, lightOuterColor, 4);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 65F, rotate2, rotate2 + r360, 4f, 5f, lightInnerColor, lightOuterColor, 3);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 65F, rotate2, rotate2 + r360, 4f, 5f, lightInnerColor, lightOuterColor, 6);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 67F, 0f, r360, 1f, 5f, lineColor, lightInnerColor, lightOuterColor, 48);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 81F, 0f, r360, 4f, 5f, lineColor, lightInnerColor, lightOuterColor, 48);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 120F, 0f, r360, 2f, 5f, lineColor, lightInnerColor, lightOuterColor, 48);
        drawSmoothGlowingArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 150F, 0f, r360, 4f, 5f, lineColor, lightInnerColor, lightOuterColor, 48);
        drawSmoothArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 32F, 0f, r360, 1f, lineColor, 48);
        drawSmoothArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 65F, rotate2, rotate2 + r360, 4f, lineColor, 3);
        drawSmoothArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 65F, rotate2, rotate2 + r360, 4f, lineColor, 6);
        drawSmoothArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 150F, rotate3, rotate3 + r360, 1.414f, lineColor, 4);
        drawSmoothArc(guiGraphics, centerX + magicOffsetX, centerY + magicOffsetY, 150F, rotate3 + Mth.PI * 0.25f, rotate3 + r360 + Mth.PI * 0.25f, 1.414f, lineColor, 4);
        drawGradientLine(guiGraphics, centerX + magicOffsetX + 81, centerY + magicOffsetY, centerX + magicOffsetX + side, centerY + magicOffsetY, 2F, 5F, 20F, lineColor, emptyColor, emptyColor);
        drawGradientLine(guiGraphics, centerX + magicOffsetX + 81, centerY + magicOffsetY, centerX + magicOffsetX + side, centerY + magicOffsetY, 1F, 5F, 30F, lineColor, emptyColor, emptyColor);
        drawGradientLine(guiGraphics, centerX + magicOffsetX - 81, centerY + magicOffsetY, centerX + magicOffsetX - side, centerY + magicOffsetY, 2F, 5F, 20F, lineColor, emptyColor, emptyColor);
        drawGradientLine(guiGraphics, centerX + magicOffsetX - 81, centerY + magicOffsetY, centerX + magicOffsetX - side, centerY + magicOffsetY, 1F, 5F, 30F, lineColor, emptyColor, emptyColor);

        for (int i = 0; i < 3; i++) {
            float trueRotateForLittleRoll = rotate2 + i * (Mth.PI * 2 / 3);
            float pointX = Mth.sin(trueRotateForLittleRoll);
            float pointY = Mth.cos(trueRotateForLittleRoll);
            float radius1 = 115.5F;
            float radius2 = 135F;

            drawSmoothArc(guiGraphics, centerX + magicOffsetX + pointX * radius1, centerY + magicOffsetY + pointY * radius1, 34.5F, 0, r360, 3f, lineColor, 48);
            drawSmoothArc(guiGraphics, centerX + magicOffsetX + pointX * radius2, centerY + magicOffsetY + pointY * radius2, 15F, 0, r360, 2f, lineColor, 48);
        }

        drawTextOnArc(guiGraphics, "SOULS  YIELD, ARCANE  WAKES, THE  WORLD  BENDS  AS  SOULCERER  COMMANDS", centerX + magicOffsetX, centerY + magicOffsetY, 85F, rotate, lineColor, false, font);

        drawTextOnArc(guiGraphics, "FLESH TO ASH   TITHE UNPAID   CYCLE UNBROKEN", centerX + magicOffsetX, centerY + magicOffsetY, 125F, rotate2, lineColor, false, font);
        drawTextOnArc(guiGraphics, "FLESH TO ASH   TITHE UNPAID   CYCLE UNBROKEN", centerX + magicOffsetX, centerY + magicOffsetY, 125F, rotate2 + Mth.PI, lineColor, false, font);

        drawScrollingText(guiGraphics, "ALEPH  TO  TAV", centerX + magicOffsetX + 81, centerY + magicOffsetY + 1, centerX + magicOffsetX + side, centerY + magicOffsetY + 1,
                lineColor, lineColor, font, 50f, time, 1, true, 1.3F);
        drawScrollingText(guiGraphics, "PATH  OF  QLIPHOTH", centerX + magicOffsetX - side, centerY + magicOffsetY + 1, centerX + magicOffsetX - 81, centerY + magicOffsetY + 1,
                lineColor, lineColor, font, 50f, time, 1, false, 1.3F);
    }

    /// 绘制随视口偏移的生命树背景。
    private void drawLivingTree(GuiGraphics guiGraphics, int centerX, int centerY, float time) {
        final ResourceLocation font = ResourceLocation.fromNamespaceAndPath("minecraft", "illageralt");
        final float treeOffsetScale = 0.55f;
        final float treeOffsetX = (float) this.scrollX * treeOffsetScale;
        final float treeOffsetY = (float) this.scrollY * treeOffsetScale + 20;

        float treeLineH = ClientConfigs.soulcererBackgroundHue / 360F;
        float lit = ClientConfigs.soulcererBackgroundContrast / 255F;
        float lit1 = lit * 0.6F;

        final int treeLineColor = Color.HSBtoRGB(treeLineH, 0.58f + Mth.sqrt(1 - lit * lit) * 0.42f, 0.65f * lit1 + 0.4F) | 0xFF000000;
        final int treeLightInnerColor = (Color.HSBtoRGB(treeLineH, 0.67f + Mth.sqrt(1 - lit * lit) * 0.33f, 0.367f * lit1 + 0.4F) & 0x00FFFFFF) | 0xAA000000;
        final int treeLightOuterColor = (Color.HSBtoRGB(treeLineH, 0.67f + Mth.sqrt(1 - lit * lit) * 0.33f, 0.367f * lit1 + 0.4F) & 0x00FFFFFF);
        final float treeScale = 0.7f;
        final int emptyColor = 0x00000000;
        final float pointRadius = 40F * treeScale;
        final float pointRadius2 = pointRadius * 0.5f;
        final float lineWidth = 15F * treeScale;
        final float lineThickness = 1F;
        final float rollThickness = 2F;
        final float g3 = 1.732050f;

        final float pointX1 = centerX + treeScale * (treeOffsetX + 130) - pointRadius;
        final float pointX2 = centerX + treeScale * (treeOffsetX - 130) + pointRadius;
        final float pointX3 = centerX + treeScale * (treeOffsetX + 130) - pointRadius2;
        final float pointX4 = centerX + treeScale * (treeOffsetX - 130) + pointRadius2;
        final float pointX5 = centerX + treeScale * (treeOffsetX + 130) - pointRadius2 * g3;
        final float pointX6 = centerX + treeScale * (treeOffsetX - 130) + pointRadius2 * g3;
        final float pointX7 = centerX + treeScale * treeOffsetX + pointRadius2 * g3;
        final float pointX8 = centerX + treeScale * treeOffsetX - pointRadius2 * g3;

        final float pointY1 = centerY + treeScale * (treeOffsetY + 75) - pointRadius;
        final float pointY2 = centerY + treeScale * (treeOffsetY + 75) + pointRadius2 * g3;
        final float pointY3 = centerY + treeScale * (treeOffsetY + 300) - pointRadius2 * g3;
        final float pointY4 = centerY + treeScale * (treeOffsetY - 75) + pointRadius;
        final float pointY5 = centerY + treeScale * (treeOffsetY - 75) - pointRadius;
        final float pointY6 = centerY + treeScale * (treeOffsetY - 225) + pointRadius;
        final float pointY7 = centerY + treeScale * (treeOffsetY - 225) + pointRadius2 * g3;
        final float pointY8 = centerY + treeScale * treeOffsetY - pointRadius2 * g3;
        final float pointY9 = centerY + treeScale * (treeOffsetY + 75) - pointRadius2;
        final float pointY10 = centerY + treeScale * (treeOffsetY + 75) + pointRadius2;
        final float pointY11 = centerY + treeScale * (treeOffsetY - 225) - pointRadius2;
        final float pointY12 = centerY + treeScale * (treeOffsetY - 75) + pointRadius2;
        final float pointY13 = centerY + treeScale * (treeOffsetY + 150) - pointRadius2;
        final float pointY14 = centerY + treeScale * (treeOffsetY - 300) + pointRadius2;

        drawGradientLine(guiGraphics, pointX1, centerY + treeScale * (treeOffsetY + 75), pointX2, centerY + treeScale * (treeOffsetY + 75), lineThickness, 5F, lineWidth, emptyColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX1, centerY + treeScale * (treeOffsetY - 75), pointX2, centerY + treeScale * (treeOffsetY - 75), lineThickness, 5F, lineWidth, emptyColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX1, centerY + treeScale * (treeOffsetY - 225), pointX2, centerY + treeScale * (treeOffsetY - 225), lineThickness, 5F, lineWidth, emptyColor, treeLightInnerColor, treeLightOuterColor);

        drawGradientLine(guiGraphics, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY) + pointRadius, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY + 150) - pointRadius, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY + 150) + pointRadius, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY + 300) - pointRadius, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX3, pointY2, centerX + treeScale * (treeOffsetX) + pointRadius2, pointY3, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX4, pointY2, centerX + treeScale * (treeOffsetX) - pointRadius2, pointY3, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX3, pointY7, centerX + treeScale * (treeOffsetX) + pointRadius2, pointY8, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX4, pointY7, centerX + treeScale * (treeOffsetX) - pointRadius2, pointY8, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX5, pointY9, pointX7, centerY + treeScale * treeOffsetY + pointRadius2, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX6, pointY9, pointX8, centerY + treeScale * treeOffsetY + pointRadius2, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX5, pointY10, pointX7, pointY13, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX6, pointY10, pointX8, pointY13, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX5, pointY11, pointX7, pointY14, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX6, pointY11, pointX8, pointY14, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX5, pointY12, pointX7, centerY + treeScale * treeOffsetY - pointRadius2, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, pointX6, pointY12, pointX8, centerY + treeScale * treeOffsetY - pointRadius2, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, centerX + treeScale * (treeOffsetX + 130), pointY1, centerX + treeScale * (treeOffsetX + 130), pointY4, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, centerX + treeScale * (treeOffsetX - 130), pointY1, centerX + treeScale * (treeOffsetX - 130), pointY4, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, centerX + treeScale * (treeOffsetX + 130), pointY5, centerX + treeScale * (treeOffsetX + 130), pointY6, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, centerX + treeScale * (treeOffsetX - 130), pointY5, centerX + treeScale * (treeOffsetX - 130), pointY6, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);
        drawGradientLine(guiGraphics, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY) - pointRadius, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY - 300) + pointRadius, lineThickness, 5F, lineWidth, treeLineColor, treeLightInnerColor, treeLightOuterColor);

        drawGradientLine(guiGraphics, pointX1, centerY + treeScale * (treeOffsetY + 75), pointX2, centerY + treeScale * (treeOffsetY + 75), lineThickness, 5F, lineWidth, treeLineColor, emptyColor, emptyColor);
        drawGradientLine(guiGraphics, pointX1, centerY + treeScale * (treeOffsetY - 75), pointX2, centerY + treeScale * (treeOffsetY - 75), lineThickness, 5F, lineWidth, treeLineColor, emptyColor, emptyColor);
        drawGradientLine(guiGraphics, pointX1, centerY + treeScale * (treeOffsetY - 225), pointX2, centerY + treeScale * (treeOffsetY - 225), lineThickness, 5F, lineWidth, treeLineColor, emptyColor, emptyColor);

        float r360 = Mth.PI * 2;
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "T I P H E R E T H", centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY + 150), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "Y E S O D", centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY + 150), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY + 300), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "M A L K U T H", centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY + 300), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX - 130), centerY + treeScale * (treeOffsetY + 75), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "H O D", centerX + treeScale * (treeOffsetX - 130), centerY + treeScale * (treeOffsetY + 75), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX + 130), centerY + treeScale * (treeOffsetY + 75), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "N E T Z A C H", centerX + treeScale * (treeOffsetX + 130), centerY + treeScale * (treeOffsetY + 75), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX - 130), centerY + treeScale * (treeOffsetY - 75), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "G E B U R A H", centerX + treeScale * (treeOffsetX - 130), centerY + treeScale * (treeOffsetY - 75), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX + 130), centerY + treeScale * (treeOffsetY - 75), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "C H E S E D", centerX + treeScale * (treeOffsetX + 130), centerY + treeScale * (treeOffsetY - 75), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX - 130), centerY + treeScale * (treeOffsetY - 225), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "B I N A H", centerX + treeScale * (treeOffsetX - 130), centerY + treeScale * (treeOffsetY - 225), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX + 130), centerY + treeScale * (treeOffsetY - 225), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "C H O K M A H", centerX + treeScale * (treeOffsetX + 130), centerY + treeScale * (treeOffsetY - 225), pointRadius, Mth.PI, treeLineColor, false, font);
        drawSmoothGlowingArc(guiGraphics, centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY - 300), pointRadius, 0f, r360, rollThickness, 5f, treeLineColor, treeLightInnerColor, treeLightOuterColor, 48);
        drawTextOnArc(guiGraphics, "K E T H E R", centerX + treeScale * (treeOffsetX), centerY + treeScale * (treeOffsetY - 300), pointRadius, Mth.PI, treeLineColor, false, font);
    }

    /// 绘制中心线及两侧渐变辉光。
    private void drawGradientLine(GuiGraphics guiGraphics,
                                  float x1, float y1, float x2, float y2,
                                  float thickness, float lightThickness, int lineColor, int innerColor, int outerColor) {
        float offsetX = x2 - x1;
        float offsetY = y2 - y1;

        float length = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);

        float unitNormalX = offsetY / length * (thickness + lightThickness) * 0.5f;
        float unitNormalY = -offsetX / length * (thickness + lightThickness) * 0.5f;

        drawGradientLine(guiGraphics, x1, y1, x2, y2, thickness, lineColor, lineColor);
        drawGradientLine(guiGraphics, x1 + unitNormalX, y1 + unitNormalY, x2 + unitNormalX, y2 + unitNormalY, lightThickness, innerColor, outerColor);
        drawGradientLine(guiGraphics, x1 - unitNormalX, y1 - unitNormalY, x2 - unitNormalX, y2 - unitNormalY, lightThickness, outerColor, innerColor);
    }

    /// 绘制指定宽度的双边发光线。
    @SuppressWarnings("SameParameterValue")
    private void drawGradientLine(GuiGraphics guiGraphics,
                                  float x1, float y1, float x2, float y2,
                                  float thickness, float lightThickness, float width, int lineColor, int innerColor, int outerColor) {
        float offsetX = x2 - x1;
        float offsetY = y2 - y1;

        float length = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);

        float unitNormalX = offsetY / length * width * 0.5f;
        float unitNormalY = -offsetX / length * width * 0.5f;

        drawGradientLine(guiGraphics, x1 + unitNormalX, y1 + unitNormalY, x2 + unitNormalX, y2 + unitNormalY, thickness, lightThickness, lineColor, innerColor, outerColor);
        drawGradientLine(guiGraphics, x1 - unitNormalX, y1 - unitNormalY, x2 - unitNormalX, y2 - unitNormalY, thickness, lightThickness, lineColor, innerColor, outerColor);
    }

    /// 绘制两侧颜色渐变的线段。
    private void drawGradientLine(GuiGraphics guiGraphics,
                                  float x1, float y1, float x2, float y2,
                                  float thickness, int leftColor, int rightColor) {

        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length == 0) return;

        float normalX = -dy / length;
        float normalY = dx / length;

        float halfWidth = thickness / 2.0f;
        float offsetLeftX = normalX * halfWidth;
        float offsetLeftY = normalY * halfWidth;
        float offsetRightX = -offsetLeftX;
        float offsetRightY = -offsetLeftY;

        float lx1 = x1 + offsetLeftX, ly1 = y1 + offsetLeftY;
        float rx1 = x1 + offsetRightX, ry1 = y1 + offsetRightY;
        float rx2 = x2 + offsetRightX, ry2 = y2 + offsetRightY;
        float lx2 = x2 + offsetLeftX, ly2 = y2 + offsetLeftY;

        PoseStack.Pose pose = guiGraphics.pose().last();
        VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(RenderType.gui());

        buffer.addVertex(pose.pose(), lx2, ly2, 0).setColor(leftColor);
        buffer.addVertex(pose.pose(), rx2, ry2, 0).setColor(rightColor);
        buffer.addVertex(pose.pose(), rx1, ry1, 0).setColor(rightColor);
        buffer.addVertex(pose.pose(), lx1, ly1, 0).setColor(leftColor);

        guiGraphics.bufferSource().endBatch(RenderType.gui());
    }

    /// 使用缓存光点绘制背景粒子层。
    @SuppressWarnings("SameParameterValue")
    private void renderBackgroundDots(GuiGraphics guiGraphics, float centerX, float centerY, float radius,
                                      int count, float minSize, float maxSize,
                                      float maxOffsetX, float maxOffsetY,
                                      float minOffsetFactor, float maxOffsetFactor, int color) {

        buildDotCache(count, radius, minSize, maxSize, color, minOffsetFactor, maxOffsetFactor); // 确保缓存已建立

        VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        PoseStack.Pose pose = guiGraphics.pose().last();

        for (CachedDot dot : cachedDots) {
            float distFactor = dot.offsetFactor;
            float pointOffsetFactor = minOffsetFactor + distFactor * (maxOffsetFactor - minOffsetFactor);

            float drawX = centerX + dot.baseX + maxOffsetX * pointOffsetFactor;
            float drawY = centerY + dot.baseY + maxOffsetY * pointOffsetFactor;

            float cos = (float) Math.cos(dot.rotationRad);
            float sin = (float) Math.sin(dot.rotationRad);
            float hs = dot.halfSize;

            float px1 = drawX + (-hs * cos - hs * sin);
            float py1 = drawY + (-hs * sin + hs * cos);
            float px2 = drawX + (hs * cos - hs * sin);
            float py2 = drawY + (hs * sin + hs * cos);
            float px3 = drawX + (hs * cos - (-hs) * sin);
            float py3 = drawY + (hs * sin + (-hs) * cos);
            float px4 = drawX + (-hs * cos - (-hs) * sin);
            float py4 = drawY + (-hs * sin + (-hs) * cos);

            buffer.addVertex(pose.pose(), px1, py1, 0).setColor(dot.r, dot.g, dot.b, dot.a);
            buffer.addVertex(pose.pose(), px2, py2, 0).setColor(dot.r, dot.g, dot.b, dot.a);
            buffer.addVertex(pose.pose(), px3, py3, 0).setColor(dot.r, dot.g, dot.b, dot.a);
            buffer.addVertex(pose.pose(), px4, py4, 0).setColor(dot.r, dot.g, dot.b, dot.a);
        }

        guiGraphics.bufferSource().endBatch(RenderType.gui());
    }

    /// 绘制带中心线的平滑发光圆弧。
    @SuppressWarnings("SameParameterValue")
    private void drawSmoothGlowingArc(GuiGraphics guiGraphics, float cx, float cy, float radius,
                                      float startAngle, float endAngle, float lineWidth, float lightWidth,
                                      int lineColor, int innerColor, int outerColor, int segments) {
        drawSmoothArc(guiGraphics, cx, cy, radius, startAngle, endAngle, lineWidth, lineColor, segments);
        drawSmoothArc(guiGraphics, cx, cy, radius + (lineWidth + lightWidth) / 2, startAngle, endAngle, lightWidth, innerColor, outerColor, segments);
        drawSmoothArc(guiGraphics, cx, cy, radius - (lineWidth + lightWidth) / 2, startAngle, endAngle, lightWidth, outerColor, innerColor, segments);

    }

    /// 绘制圆弧内外侧的渐变辉光。
    @SuppressWarnings("SameParameterValue")
    private void drawSmoothGlowingArc(GuiGraphics guiGraphics, float cx, float cy, float radius,
                                      float startAngle, float endAngle, float lineWidth, float lightWidth,
                                      int innerColor, int outerColor, int segments) {
        drawSmoothArc(guiGraphics, cx, cy, radius + (lineWidth + lightWidth) / 2, startAngle, endAngle, lightWidth, innerColor, outerColor, segments);
        drawSmoothArc(guiGraphics, cx, cy, radius - (lineWidth + lightWidth) / 2, startAngle, endAngle, lightWidth, outerColor, innerColor, segments);

    }

    /// 沿线段绘制带渐变的滚动文字。
    public void drawScrollingText(GuiGraphics guiGraphics, String text,
                                  float x1, float y1, float x2, float y2,
                                  int color1, int color2,
                                  @Nullable ResourceLocation fontLocation,
                                  float gradientLen, float time, float speed, boolean isLeftToRight,
                                  float scale) {

        Font font = this.font;

        float dx = x2 - x1;
        float dy = y2 - y1;
        float totalWidth = (float) Math.sqrt(dx * dx + dy * dy);
        if (totalWidth <= 0 || text.isEmpty()) return;

        float angleRad = (float) Math.atan2(dy, dx);

        int a1 = (color1 >> 24) & 0xFF, r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF, r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF;

        float textWidth = font.width(text);
        float spacing = 40f * scale;
        float cycleWidth = textWidth * scale + spacing;

        float timeOffset = time * speed;
        float baseOffset = timeOffset % cycleWidth;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(x1, y1, 0);
        poseStack.mulPose(new Quaternionf(new AxisAngle4f(angleRad, 0, 0, 1)));

        float lineHeight = font.lineHeight * scale;
        poseStack.translate(0, -lineHeight / 2f, 0);
        poseStack.scale(scale, scale, 1.0f);

        float logicTotalWidth = totalWidth / scale;
        float logicGradientLen = gradientLen / scale;

        int screenW = guiGraphics.guiWidth();
        int screenH = guiGraphics.guiHeight();
        int safeScreenRight = screenW + 10;
        int safeScreenBottom = screenH + 10;

        int cyclesOnLine = (int) Math.ceil(totalWidth / cycleWidth);

        int bufferCycles = 2;

        int preCycles = (int) Math.ceil(baseOffset / cycleWidth) + bufferCycles;

        int totalCycles = preCycles + cyclesOnLine + bufferCycles;

        for (int cycle = -preCycles; cycle < (totalCycles - preCycles); cycle++) {

            float charX = (isLeftToRight ? (baseOffset + cycle * cycleWidth) : (-baseOffset + cycle * cycleWidth)) / scale;

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                MutableComponent charComponent = Component.literal(String.valueOf(c));
                if (fontLocation != null) {
                    charComponent.withStyle(Style.EMPTY.withFont(fontLocation));
                }
                float charWidth = font.width(charComponent);

                float charLeft = charX;
                float charRight = charX + charWidth;
                float charCenterX = charX + charWidth / 2f;

                float progress = 1.0f;
                if (charCenterX < 0 || charCenterX > logicTotalWidth) {
                    progress = 0.0f;
                } else if (charCenterX < logicGradientLen) {
                    progress = charCenterX / logicGradientLen;
                } else if (charCenterX > logicTotalWidth - logicGradientLen) {
                    progress = (logicTotalWidth - charCenterX) / logicGradientLen;
                }

                if (progress <= 0.001f && (charRight <= 0 || charLeft >= logicTotalWidth)) {
                    charX += charWidth;
                    continue;
                }

                int finalAlpha = (int) (a2 + (a1 - a2) * progress);
                int finalR = (int) (r2 + (r1 - r2) * progress);
                int finalG = (int) (g2 + (g1 - g2) * progress);
                int finalB = (int) (b2 + (b1 - b2) * progress);
                int finalColor = (finalAlpha << 24) | (finalR << 16) | (finalG << 8) | finalB;

                boolean needsLeftCut = charLeft < 0 && charRight > 0;
                boolean needsRightCut = charLeft < logicTotalWidth && charRight > logicTotalWidth;

                if (needsLeftCut || needsRightCut) {
                    float cutLocalX = needsLeftCut ? 0 : logicTotalWidth;
                    float screenCutX = x1 + (cutLocalX * scale * (float) Math.cos(angleRad));

                    float screenTopY = y1 - (lineHeight / 2f);
                    float screenBottomY = screenTopY + lineHeight;

                    int scissorY = Math.max(0, (int) screenTopY - 2);
                    int scissorH = Math.min(safeScreenBottom, (int) (screenBottomY - screenTopY) + 4);

                    int scissorX, scissorW;

                    if (needsLeftCut) {
                        scissorX = Math.max(0, (int) screenCutX - 1);
                        scissorW = safeScreenRight - scissorX;
                    } else {
                        scissorX = 0;
                        scissorW = Math.max(0, (int) screenCutX + 1);
                    }

                    if (scissorW > 0 && scissorH > 0) {
                        guiGraphics.enableScissor(scissorX, scissorY, scissorX + scissorW, scissorY + scissorH);
                        guiGraphics.drawString(font, charComponent, (int) charX, 0, finalColor, false);
                        guiGraphics.disableScissor();
                    } else {
                        guiGraphics.drawString(font, charComponent, (int) charX, 0, finalColor, false);
                    }
                } else {
                    guiGraphics.drawString(font, charComponent, (int) charX, 0, finalColor, false);
                }

                charX += charWidth;
            }
        }

        poseStack.popPose();
    }

    /// 沿圆弧排列并绘制文字。
    @SuppressWarnings("SameParameterValue")
    private void drawTextOnArc(GuiGraphics guiGraphics, String text, float cx, float cy, float radius,
                               float startAngle, int color, boolean isCounterClockwise,
                               @Nullable ResourceLocation fontLocation) {

        Font font = this.font;
        float charSpacingRad = 1.0f / Math.max(1, radius);
        float currentAngle = startAngle;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            MutableComponent charComponent = Component.literal(String.valueOf(c));
            if (fontLocation != null) {
                charComponent.withStyle(Style.EMPTY.withFont(fontLocation));
            }

            float charWidth = font.width(charComponent);
            float halfCharWidth = charWidth / 2f;
            float charHeight = font.lineHeight;

            float posX = cx + (float) Math.cos(currentAngle) * radius;
            float posY = cy + (float) Math.sin(currentAngle) * radius;

            float rotationDegrees;

            if (isCounterClockwise) {
                rotationDegrees = (float) Math.toDegrees(currentAngle) - 90f;

                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(posX, posY, 0);
                poseStack.mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians(rotationDegrees), 0, 0, 1)));
                poseStack.translate(-halfCharWidth, 0, 0);

                guiGraphics.drawString(font, charComponent, 0, 0, color, false);

                poseStack.popPose();

                currentAngle -= (charWidth + charSpacingRad + 2) / radius;
            } else {
                rotationDegrees = (float) Math.toDegrees(currentAngle) + 90f;

                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(posX, posY, 0);
                poseStack.mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians(rotationDegrees), 0, 0, 1)));
                poseStack.translate(-halfCharWidth, charHeight, 0);

                guiGraphics.drawString(font, charComponent, 0, 0, color, false);

                poseStack.popPose();

                currentAngle += (charWidth + charSpacingRad + 2) / radius;
            }
        }
    }

    /// 绘制内外颜色渐变的平滑圆弧。
    private void drawSmoothArc(GuiGraphics guiGraphics, float cx, float cy, float radius,
                               float startAngle, float endAngle, float width,
                               int innerColor, int outerColor, int segments) {
        if (segments < 2) segments = 2;
        float angleStep = (endAngle - startAngle) / segments;
        float innerR = radius - width / 2f;
        float outerR = radius + width / 2f;

        PoseStack.Pose pose = guiGraphics.pose().last();
        VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(RenderType.gui());

        for (int i = 0; i < segments; i++) {
            float a1 = startAngle + angleStep * i;
            float a2 = a1 + angleStep;

            float ix1 = cx + (float) Math.cos(a1) * innerR;
            float iy1 = cy + (float) Math.sin(a1) * innerR;
            float ox1 = cx + (float) Math.cos(a1) * outerR;
            float oy1 = cy + (float) Math.sin(a1) * outerR;

            float ix2 = cx + (float) Math.cos(a2) * innerR;
            float iy2 = cy + (float) Math.sin(a2) * innerR;
            float ox2 = cx + (float) Math.cos(a2) * outerR;
            float oy2 = cy + (float) Math.sin(a2) * outerR;

            buffer.addVertex(pose.pose(), ox1, oy1, 0).setColor(outerColor);
            buffer.addVertex(pose.pose(), ix1, iy1, 0).setColor(innerColor);
            buffer.addVertex(pose.pose(), ix2, iy2, 0).setColor(innerColor);
            buffer.addVertex(pose.pose(), ox2, oy2, 0).setColor(outerColor);
        }

        guiGraphics.bufferSource().endBatch(RenderType.gui());
    }

    /// 绘制单色平滑圆弧。
    private void drawSmoothArc(GuiGraphics guiGraphics, float cx, float cy, float radius,
                               float startAngle, float endAngle, float width, int color, int segments) {
        if (segments < 2) segments = 2;
        float angleStep = (endAngle - startAngle) / segments;
        float innerR = radius - width / 2f;
        float outerR = radius + width / 2f;

        PoseStack.Pose pose = guiGraphics.pose().last();
        VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(RenderType.gui());

        for (int i = 0; i < segments; i++) {
            float a1 = startAngle + angleStep * i;
            float a2 = a1 + angleStep;

            float ix1 = cx + (float) Math.cos(a1) * innerR;
            float iy1 = cy + (float) Math.sin(a1) * innerR;
            float ox1 = cx + (float) Math.cos(a1) * outerR;
            float oy1 = cy + (float) Math.sin(a1) * outerR;

            float ix2 = cx + (float) Math.cos(a2) * innerR;
            float iy2 = cy + (float) Math.sin(a2) * innerR;
            float ox2 = cx + (float) Math.cos(a2) * outerR;
            float oy2 = cy + (float) Math.sin(a2) * outerR;

            buffer.addVertex(pose.pose(), ox1, oy1, 0).setColor(color);
            buffer.addVertex(pose.pose(), ix1, iy1, 0).setColor(color);
            buffer.addVertex(pose.pose(), ix2, iy2, 0).setColor(color);
            buffer.addVertex(pose.pose(), ox2, oy2, 0).setColor(color);
        }

        guiGraphics.bufferSource().endBatch(RenderType.gui());
    }

    /// 叠加两层噪声形成背景混色。
    @SuppressWarnings("SameParameterValue")
    private void renderScreenBlendNoise(GuiGraphics guiGraphics, float zOffset,
                                        int layer1Color, float offsetX1, float offsetY1, float scale1, float maxAlpha1,
                                        int layer2Color, float offsetX2, float offsetY2, float scale2, float maxAlpha2) {
        PoseStack poseStack = guiGraphics.pose();
        Matrix4f pose = poseStack.last().pose();

        VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        // 背景噪聲步長
        int step = 8;

        int startX = -step;
        int startY = -step;
        int endX = this.width + step;
        int endY = this.height + step;

        int rowLength = (endX - startX) / step + 2;

        int[] currentRowColors = new int[rowLength];
        int[] prevRowColors = new int[rowLength];

        for (int ix = startX, idx = 0; ix <= endX; ix += step, idx++) {
            double noiseX1 = (ix + offsetX1) * scale1;
            double noiseY1 = (startY + offsetY1) * scale1;
            double noiseX2 = (ix + offsetX2) * scale2;
            double noiseY2 = (startY + offsetY2) * scale2;
            currentRowColors[idx] = calculateScreenBlendColor(noiseX1, noiseY1, layer1Color, maxAlpha1, noiseX2, noiseY2, layer2Color, maxAlpha2, zOffset);
        }

        for (int y = 0; y <= endY; y += step) {
            int[] temp = prevRowColors;
            prevRowColors = currentRowColors;
            currentRowColors = temp;

            for (int ix = startX, idx = 0; ix <= endX; ix += step, idx++) {
                double noiseX1 = (ix + offsetX1) * scale1;
                double noiseY1 = (y + offsetY1) * scale1;
                double noiseX2 = (ix + offsetX2) * scale2;
                double noiseY2 = (y + offsetY2) * scale2;
                currentRowColors[idx] = calculateScreenBlendColor(noiseX1, noiseY1, layer1Color, maxAlpha1, noiseX2, noiseY2, layer2Color, maxAlpha2, zOffset);
            }

            for (int ix = startX, idx = 0; ix < endX; ix += step, idx++) {
                int c00 = prevRowColors[idx];
                int c01 = currentRowColors[idx];
                int c10 = prevRowColors[idx + 1];
                int c11 = currentRowColors[idx + 1];

                buffer.addVertex(pose, ix, y, 0).setColor(c01);
                buffer.addVertex(pose, ix + step, y, 0).setColor(c11);
                buffer.addVertex(pose, ix + step, y - step, 0).setColor(c10);
                buffer.addVertex(pose, ix, y - step, 0).setColor(c00);
            }
        }

        guiGraphics.bufferSource().endBatch(RenderType.gui());
    }

    /// 计算两层噪声混合后的颜色。
    private int calculateScreenBlendColor(double noiseX1, double noiseY1, int color1, float maxAlpha1,
                                          double noiseX2, double noiseY2, int color2, float maxAlpha2, float zOffset) {
        double noiseVal1 = backgroundNoise.getValue(noiseX1, noiseY1, zOffset);
        float brightness1 = Math.clamp((float) (noiseVal1 * 0.5 + 0.5), 0.0f, 1.0f);

        float r1 = ((color1 >> 16) & 0xFF) / 255.0f * brightness1;
        float g1 = ((color1 >> 8) & 0xFF) / 255.0f * brightness1;
        float b1 = (color1 & 0xFF) / 255.0f * brightness1;
        float a1 = ((color1 >> 24) & 0xFF) / 255.0f * brightness1 * maxAlpha1;

        double noiseVal2 = backgroundNoise.getValue(noiseX2, noiseY2, zOffset);
        float brightness2 = Math.clamp((float) (noiseVal2 * 0.5 + 0.5), 0.0f, 1.0f);

        float r2 = ((color2 >> 16) & 0xFF) / 255.0f * brightness2;
        float g2 = ((color2 >> 8) & 0xFF) / 255.0f * brightness2;
        float b2 = (color2 & 0xFF) / 255.0f * brightness2;
        float a2 = ((color2 >> 24) & 0xFF) / 255.0f * brightness2 * maxAlpha2;

        float finalR = 1.0f - (1.0f - r1) * (1.0f - r2);
        float finalG = 1.0f - (1.0f - g1) * (1.0f - g2);
        float finalB = 1.0f - (1.0f - b1) * (1.0f - b2);
        float finalA = Math.max(a1, a2);

        int outR = Math.clamp((int) (finalR * 255), 0, 255);
        int outG = Math.clamp((int) (finalG * 255), 0, 255);
        int outB = Math.clamp((int) (finalB * 255), 0, 255);
        int outA = Math.clamp((int) (finalA * 255), 0, 255);

        return (outA << 24) | (outR << 16) | (outG << 8) | outB;
    }

    /// 绘制总览控件及可见的背景调节滑块。
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (isSliderVisible) {
            hueSlider.setPosition(5, this.height - 50);
            hueSlider.render(guiGraphics, mouseX, mouseY, partialTick);
            contrastSlider.setPosition(5, this.height - 75);
            contrastSlider.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    /// 绘制可见节点之间的连线并避免重复。
    ///
    /// 端点直接取两端节点的**屏幕包围盒中心**（`OverviewNode#getScreenCenter`），
    /// 不再自己按 `orig + scroll` 推算：节点位置早已由 `positionAllNodes` 摆好，
    /// 自行推算会与控件实际位置差半个框，缩放后误差还会被放大。
    private void renderConnections(GuiGraphics guiGraphics, float partialTick) {
        RenderSystem.enableBlend();
        Set<String> drawn = new HashSet<>();

        /// 线宽跟着缩放走，否则放大后节点变粗、连线还是原来那么细。
        float thickness = CONNECTION_THICKNESS * (float) zoom;
        float lightThickness = CONNECTION_LIGHT_THICKNESS * (float) zoom;

        for (OverviewNode node : nodes) {
            Vec2 src = node.getScreenCenter();
            float sx = src.x;
            float sy = src.y;

            for (ResourceLocation connId : node.getConnections()) {
                OverviewNode target = nodeById.get(connId);
                if (target == null) continue;

                String key = node.getNodeId().toString().compareTo(connId.toString()) < 0 ? node.getNodeId() + ">" + connId : connId + ">" + node.getNodeId();
                if (!drawn.add(key)) continue;

                Vec2 dst = target.getScreenCenter();
                float tx = dst.x;
                float ty = dst.y;

                float minX = Math.min(sx, tx);
                float maxX = Math.max(sx, tx);
                float minY = Math.min(sy, ty);
                float maxY = Math.max(sy, ty);

                if (maxX < 0 || minX > this.width || maxY < 0 || minY > this.height) {
                    continue;
                }
                drawGradientLine(guiGraphics, sx, sy, tx, ty, thickness, lightThickness, 0xFF66FFE3, 0xFF28698d, 0x0028698d);
            }
        }
        RenderSystem.disableBlend();
    }

    /// 处理滑块、控件点击及视口拖拽起点。
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (isSliderVisible && button == 0) {
            if (hueSlider.isMouseOver(mouseX, mouseY))
                return hueSlider.mouseClicked(mouseX, mouseY, button);
            if (contrastSlider.isMouseOver(mouseX, mouseY))
                return contrastSlider.mouseClicked(mouseX, mouseY, button);
        }

        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (button == 0 && !hotbar.isMouseOver(mouseX, mouseY)) {
            isDragging = true;
            targetScrollX = null;
            targetScrollY = null;
            dragStartMouseX = mouseX;
            dragStartMouseY = mouseY;
            dragStartScrollX = scrollX;
            dragStartScrollY = scrollY;
            return true;
        }
        return false;
    }


    // ============ 输入处理 ============

    /// 处理滑块拖动或拖拽移动视口。
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (isSliderVisible && button == 0) {
            if (hueSlider.isMouseOver(mouseX, mouseY))
                return hueSlider.mouseDragged(mouseX, mouseY, button, dx, dy);
            if (contrastSlider.isMouseOver(mouseX, mouseY))
                return contrastSlider.mouseDragged(mouseX, mouseY, button, dx, dy);
        }
        if (isDragging && button == 0) {
            /// scroll 是节点空间量，屏幕位移还要乘 zoom；这里除掉 zoom 让节点始终贴着鼠标走。
            scrollX = dragStartScrollX + (mouseX - dragStartMouseX) / zoom;
            scrollY = dragStartScrollY + (mouseY - dragStartMouseY) / zoom;
            clampScroll();
            positionAllNodes();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    /// 结束滑块操作和视口拖拽。
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isSliderVisible) {
            hueSlider.onRelease(mouseX, mouseY);
            contrastSlider.onRelease(mouseX, mouseY);
        }
        if (button == 0 && isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /// 按数字键将选中技能装备到对应槽位。
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int slot = keyCode - GLFW.GLFW_KEY_1;
        if (slot >= 0 && slot < PlayerUtils.getEquippedSoulSkillMaxNumber(Minecraft.getInstance().player)) {
            if (theCurrentlySelectedSkill != null) {
                ClientSoulHandler.equipSoulSkill(slot, theCurrentlySelectedSkill.getStack());
                hotbar.rebuildFromHolder();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /// 将当前选中技能装备到指定槽位。
    public boolean equipSelectedToSlot(int slot) {
        if (theCurrentlySelectedSkill == null) return false;
        return ClientSoulHandler.equipSoulSkill(slot, theCurrentlySelectedSkill.getStack());
    }

    // ============ 装配逻辑 ============

    /// 用当前装备数据刷新底部快捷栏。
    public void rebuildHotbar() {
        List<HotbarWidget.HotbarSlot> previousSlots = new ArrayList<>(hotbar.getSlots());
        hotbar.rebuildFromHolder();
        if (!previousSlots.equals(hotbar.getSlots())) {
            previousSlots.forEach(this::removeWidget);
            for (HotbarWidget.HotbarSlot slot : hotbar.getSlots()) {
                addRenderableWidget(slot);
            }
        }
        if (selectedHotbarSlot >= hotbar.getSlots().size()) {
            selectedHotbarSlot = -1;
        }
        updateHotbarPos();
    }

    /// 将快捷栏定位到界面底部中央（略微下移，给技能树留出更多竖向空间）。
    protected void updateHotbarPos() {
        hotbar.setPosition(width / 2 - hotbar.getWidth() / 2, (int) (height * 0.87f));
    }

    // ============ 通用工具 ============

    /// 播放总览界面的按钮点击音效。
    public void uiButtonClickSound() {
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /// 保持总览打开时游戏继续运行。
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record CachedDot(
            float baseX, float baseY,
            float rotationRad,
            float halfSize,
            float r, float g, float b, float a,
            float offsetFactor) {
    }
}
