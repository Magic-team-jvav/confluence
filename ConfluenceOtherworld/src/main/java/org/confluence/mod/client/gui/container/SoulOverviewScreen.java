package org.confluence.mod.client.gui.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
import org.confluence.mod.client.gui.widget.soul_skill.SoulSkillBox;
import org.confluence.mod.client.gui.widget.soul_skill.soul_overview.CenterButton;
import org.confluence.mod.client.gui.widget.soul_skill.soul_overview.HotbarWidget;
import org.confluence.mod.client.gui.widget.soul_skill.soul_overview.NavTab;
import org.confluence.mod.client.gui.widget.soul_skill.soul_overview.OverviewNavButton;
import org.confluence.mod.client.gui.widget.soul_skill.soul_overview.OverviewNode;
import org.confluence.mod.client.handler.SoulSkillClientHolder;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.soulskill.SoulSkill;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Supplier;

/**
 * 灵魂技能总览界面 — 类似原版进度界面的技能树视图
 */
public class SoulOverviewScreen extends Screen {
    private static final SoulSkillClientHolder HOLDER = SoulSkillClientHolder.INSTANCE;

    private final NormalNoise backgroundNoise;
    /* 组件 */
    public final HotbarWidget hotbar;
    public final List<OverviewNode> nodes;
    public final Map<ResourceLocation, OverviewNode> nodeById = new HashMap<>();
    public final List<NavTab> navTabs = new ArrayList<>();
    private final CenterButton centerButton;

    /* 视口状态 */
    public double scrollX, scrollY;
    private Float targetScrollX, targetScrollY;
    private double minScrollX, maxScrollX, minScrollY, maxScrollY;

    /* 拖拽 */
    private boolean isDragging;
    private double dragStartMouseX, dragStartMouseY;
    private double dragStartScrollX, dragStartScrollY;

    /* 选中状态 */
    @Nullable
    public SoulSkill theCurrentlySelectedSkill;

    public int selectedHotbarSlot = -1;

    public SoulOverviewScreen() {
        super(Component.translatable("confluence.screen.soul_overview"));
        nodes = new ArrayList<>();
        hotbar = new HotbarWidget(this, 0, 0);
        centerButton = new CenterButton(this, 0, 0);
        backgroundNoise = NormalNoise.create(RandomSource.create(12345L), -5, 1.0, 1.0, 1.0, 1.0);

        initSkillsFromRegistry();
    }

    private record CachedDot(
            float baseX, float baseY,
            float rotationRad,
            float halfSize,
            float r, float g, float b, float a,
            float offsetFactor) {
    }

    private List<CachedDot> cachedDots;

    private void buildDotCache(int count, float radius, float minSize, float maxSize, int color, float minOffsetFactor, float maxOffsetFactor) {
        if (cachedDots != null) return;
        cachedDots = new ArrayList<>(count);

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

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

    private void initSkillsFromRegistry() {
        for (Supplier<SoulSkill> supplier : ModSoulSkills.getAllSkills()) {
            SoulSkill skill = supplier.get();
            HOLDER.addUnlockedSkill(skill);
        }

        // 三列树状布局，坐标原点 = 屏幕中心
        // 左列 — Soul 系
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_DRAIN)
                .pos(-160, -160)
                .connections(ModSoulSkills.SOUL_PLUNDER, ModSoulSkills.BLOOD_RAGE));
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_PLUNDER)
                .pos(-160, -80)
                .connections(ModSoulSkills.ENHANCED_SOUL, ModSoulSkills.SOUL_DRAIN));
        addNode(OverviewNode.Builder.of(ModSoulSkills.ENHANCED_SOUL)
                .pos(-160, 0)
                .connections(ModSoulSkills.SOUL_MARK, ModSoulSkills.SOUL_PLUNDER));
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_MARK)
                .pos(-160, 80)
                .connections(ModSoulSkills.PROFANE_SOUL, ModSoulSkills.ENHANCED_SOUL));
        addNode(OverviewNode.Builder.of(ModSoulSkills.PROFANE_SOUL)
                .pos(-160, 160)
                .connections(ModSoulSkills.SOUL_LURE, ModSoulSkills.SOUL_MARK));
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_LURE)
                .pos(-160, 240)
                .connections(ModSoulSkills.SOUL_SURGE, ModSoulSkills.PROFANE_SOUL));
        addNode(OverviewNode.Builder.of(ModSoulSkills.SOUL_SURGE)
                .pos(-160, 320)
                .connections(ModSoulSkills.LURE_SURGE, ModSoulSkills.SOUL_LURE));

        // 中列 — Blood/Nature 系
        addNode(OverviewNode.Builder.of(ModSoulSkills.BLOOD_RAGE).pos(0, -240));
        addNode(OverviewNode.Builder.of(ModSoulSkills.BOILING_BLOOD)
                .pos(0, -160)
                .connections(ModSoulSkills.CONFUSE_SPORES, ModSoulSkills.BLOOD_RAGE));
        addNode(OverviewNode.Builder.of(ModSoulSkills.CONFUSE_SPORES)
                .pos(0, -80)
                .connections(ModSoulSkills.KARMA_FLAME, ModSoulSkills.BOILING_BLOOD));
        addNode(OverviewNode.Builder.of(ModSoulSkills.KARMA_FLAME)
                .pos(0, 0)
                .connections(ModSoulSkills.LAW_OF_NATURE, ModSoulSkills.CONFUSE_SPORES));
        addNode(OverviewNode.Builder.of(ModSoulSkills.LAW_OF_NATURE)
                .pos(0, 80)
                .connections(ModSoulSkills.NATURES_WRATH, ModSoulSkills.KARMA_FLAME));
        addNode(OverviewNode.Builder.of(ModSoulSkills.NATURES_WRATH)
                .pos(0, 160)
                .connections(ModSoulSkills.SPIRIT_TRIGGER, ModSoulSkills.LAW_OF_NATURE));
        addNode(OverviewNode.Builder.of(ModSoulSkills.SPIRIT_TRIGGER)
                .pos(0, 240)
                .connections(ModSoulSkills.SPIRIT_SURGE, ModSoulSkills.NATURES_WRATH));
        addNode(OverviewNode.Builder.of(ModSoulSkills.SPIRIT_SURGE)
                .pos(0, 320)
                .connections(ModSoulSkills.EMPOWERED_SURGE, ModSoulSkills.SPIRIT_TRIGGER));

        // 右列 — Star/Lure 系
        addNode(OverviewNode.Builder.of(ModSoulSkills.ENHANCED_LURE)
                .pos(160, -160)
                .connections(ModSoulSkills.LURE_SURGE, ModSoulSkills.BLOOD_RAGE));
        addNode(OverviewNode.Builder.of(ModSoulSkills.LURE_SURGE)
                .pos(160, -80)
                .connections(ModSoulSkills.STAR_CALL, ModSoulSkills.ENHANCED_LURE));
        addNode(OverviewNode.Builder.of(ModSoulSkills.STAR_CALL)
                .pos(160, 0)
                .connections(ModSoulSkills.STAR_LINK, ModSoulSkills.LURE_SURGE));
        addNode(OverviewNode.Builder.of(ModSoulSkills.STAR_LINK)
                .pos(160, 80)
                .connections(ModSoulSkills.STAR_REVERSAL, ModSoulSkills.STAR_CALL));
        addNode(OverviewNode.Builder.of(ModSoulSkills.STAR_REVERSAL)
                .pos(160, 160)
                .connections(ModSoulSkills.EMPOWERED_SURGE, ModSoulSkills.STAR_LINK));
        addNode(OverviewNode.Builder.of(ModSoulSkills.EMPOWERED_SURGE)
                .pos(160, 240)
                .connections(ModSoulSkills.SURGE_BLAST, ModSoulSkills.STAR_REVERSAL));
        addNode(OverviewNode.Builder.of(ModSoulSkills.SURGE_BLAST)
                .pos(160, 320)
                .connections(ModSoulSkills.EMPOWERED_SURGE));
    }

    private void addNode(OverviewNode.Builder builder) {
        OverviewNode node = builder.build(this);
        nodes.add(node);
        nodeById.put(node.getNodeId(), node);
    }

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
    }

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

    @Override
    protected void repositionElements() {
        super.repositionElements();
        updateHotbarPos();
        calcScrollBounds();
    }

    private void calcScrollBounds() {
        if (nodes.isEmpty()) {
            minScrollX = maxScrollX = minScrollY = maxScrollY = 0;
            return;
        }
        double marginX = width / 3.0;
        double marginY = height / 3.0;

        double minLeft = Double.MAX_VALUE;
        double maxRight = -Double.MAX_VALUE;
        double minTop = Double.MAX_VALUE;
        double maxBottom = -Double.MAX_VALUE;

        for (OverviewNode node : nodes) {
            double left = node.getOrigX() + width / 2.0;
            double right = left + SoulSkillBox.BOX_SIZE;
            double top = node.getOrigY() + height / 2.0;
            double bottom = top + SoulSkillBox.BOX_SIZE;

            if (left < minLeft) minLeft = left;
            if (right > maxRight) maxRight = right;
            if (top < minTop) minTop = top;
            if (bottom > maxBottom) maxBottom = bottom;
        }

        minScrollX = width - marginX - maxRight;
        maxScrollX = marginX - minLeft;
        minScrollY = height - marginY - maxBottom;
        maxScrollY = marginY - minTop;

        if (minScrollX > maxScrollX) {
            double center = (minScrollX + maxScrollX) / 2.0;
            minScrollX = maxScrollX = center;
        }
        if (minScrollY > maxScrollY) {
            double center = (minScrollY + maxScrollY) / 2.0;
            minScrollY = maxScrollY = center;
        }
    }

    private void clampScroll() {
        scrollX = Math.clamp(scrollX, minScrollX, maxScrollX);
        scrollY = Math.clamp(scrollY, minScrollY, maxScrollY);
    }

    private void positionAllNodes() {
        int cx = width / 2;
        int cy = height / 2;
        for (OverviewNode node : nodes) {
            node.setPosition((int) (node.getOrigX() + scrollX + cx), (int) (node.getOrigY() + scrollY + cy));
        }
    }

    // ============ 滚动控制 ============

    public void setScroll(double sx, double sy) {
        scrollX = sx;
        scrollY = sy;
        targetScrollX = null;
        targetScrollY = null;
        positionAllNodes();
    }

    public void centerToOrigin() {
        targetScrollX = 0f;
        targetScrollY = 0f;
    }

    public void navigateTo(OverviewNavButton.Target target) {
        targetScrollX = (float) target.scrollX();
        targetScrollY = (float) target.scrollY();
    }

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

    public Font getFont() {return font;}

    // ============ 快捷栏交互 ============

    public int getSelectedHotbarSlot() {return selectedHotbarSlot;}

    public void setSelectedHotbarSlot(int slot) {selectedHotbarSlot = slot;}

    // ============ 渲染 ============

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;

        if (level == null) return;

        long gameTime = level.getGameTime();
        float time = gameTime + partialTick;

        guiGraphics.fill(0, 0, this.width, this.height, 0xFF07010e);
        float offsetScale = 0.15f;
        float noiseScale = 0.25f;
        float noiseOffsetX = (float) - this.scrollX * offsetScale;
        float noiseOffsetY = (float) - this.scrollY * offsetScale;
        float noiseOffsetZ = Mth.sin(time * 0.002f) * 100;

        renderScreenBlendNoise(guiGraphics, noiseOffsetZ,
                0xFF252dab, noiseOffsetX, noiseOffsetY, noiseScale, 1.0F,
                0xFF773bb4, noiseOffsetX * 2.5F, noiseOffsetY * 2.5F, noiseScale * 0.8f, 1.0F);

        int centerX = (int) (this.width * 0.5);
        int centerY = (int) (this.height * 0.5);

        drawLivingTree(guiGraphics, centerX, centerY, time);
        renderBackgroundDots(guiGraphics, centerX, centerY, 1000, 1500, 1, 3, (float) this.scrollX, (float) this.scrollY, 0.3F, 1.0F, 0xFFFFFFFF);
        drawMagic(guiGraphics, centerX, centerY, time);
        tickScrollAnimation(partialTick);
        renderConnections(guiGraphics, partialTick);
    }

    private void drawMagic(GuiGraphics guiGraphics, int centerX, int centerY, float time) {
        final ResourceLocation font = ResourceLocation.fromNamespaceAndPath("minecraft", "illageralt");
        final float magicOffsetScale = 0.7f;
        final float magicOffsetX = (float) this.scrollX * magicOffsetScale;
        final float magicOffsetY = (float) this.scrollY * magicOffsetScale;
        final float rotate = time * 0.01f;
        final int lineColor = 0xFFFFFFFF;
        final int lightInnerColor = 0xFF68349d;
        final int lightOuterColor = 0x0068349d;
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
        for (int i = 0; i < 3; i++){
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

        for (int i = 0; i < 3; i++){
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

    private void drawLivingTree(GuiGraphics guiGraphics, int centerX, int centerY, float time) {
        final ResourceLocation font = ResourceLocation.fromNamespaceAndPath("minecraft", "illageralt");
        final float treeOffsetScale = 0.55f;
        final float treeOffsetX = (float) this.scrollX * treeOffsetScale;
        final float treeOffsetY = (float) this.scrollY * treeOffsetScale + 20;

        final int treeLineColor = 0xFF8e54c9;
        final int treeLightInnerColor = 0xAA68349d;
        final int treeLightOuterColor = 0x0068349d;
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

    @SuppressWarnings("SameParameterValue")
    private void drawSmoothGlowingArc(GuiGraphics guiGraphics, float cx, float cy, float radius,
                                      float startAngle, float endAngle, float lineWidth, float lightWidth,
                                      int lineColor, int innerColor, int outerColor, int segments) {
        drawSmoothArc(guiGraphics, cx, cy, radius, startAngle, endAngle, lineWidth, lineColor, segments);
        drawSmoothArc(guiGraphics, cx, cy, radius + (lineWidth + lightWidth) / 2, startAngle, endAngle, lightWidth, innerColor, outerColor, segments);
        drawSmoothArc(guiGraphics, cx, cy, radius - (lineWidth + lightWidth) / 2, startAngle, endAngle, lightWidth, outerColor, innerColor, segments);

    }

    @SuppressWarnings("SameParameterValue")
    private void drawSmoothGlowingArc(GuiGraphics guiGraphics, float cx, float cy, float radius,
                                      float startAngle, float endAngle, float lineWidth, float lightWidth,
                                      int innerColor, int outerColor, int segments) {
        drawSmoothArc(guiGraphics, cx, cy, radius + (lineWidth + lightWidth) / 2, startAngle, endAngle, lightWidth, innerColor, outerColor, segments);
        drawSmoothArc(guiGraphics, cx, cy, radius - (lineWidth + lightWidth) / 2, startAngle, endAngle, lightWidth, outerColor, innerColor, segments);

    }

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
                int finalR     = (int) (r2 + (r1 - r2) * progress);
                int finalG     = (int) (g2 + (g1 - g2) * progress);
                int finalB     = (int) (b2 + (b1 - b2) * progress);
                int finalColor = (finalAlpha << 24) | (finalR << 16) | (finalG << 8) | finalB;

                boolean needsLeftCut = charLeft < 0 && charRight > 0;
                boolean needsRightCut = charLeft < logicTotalWidth && charRight > logicTotalWidth;

                if (needsLeftCut || needsRightCut) {
                    float cutLocalX = needsLeftCut ? 0 : logicTotalWidth;
                    float screenCutX = x1 + (cutLocalX * scale * (float)Math.cos(angleRad));

                    float screenTopY = y1 - (lineHeight / 2f);
                    float screenBottomY = screenTopY + lineHeight;

                    int scissorY = Math.max(0, (int)screenTopY - 2);
                    int scissorH = Math.min(safeScreenBottom, (int)(screenBottomY - screenTopY) + 4);

                    int scissorX, scissorW;

                    if (needsLeftCut) {
                        scissorX = Math.max(0, (int)screenCutX - 1);
                        scissorW = safeScreenRight - scissorX;
                    } else {
                        scissorX = 0;
                        scissorW = Math.max(0, (int)screenCutX + 1);
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

        int outR = Math.clamp((int)(finalR * 255), 0, 255);
        int outG = Math.clamp((int)(finalG * 255), 0, 255);
        int outB = Math.clamp((int)(finalB * 255), 0, 255);
        int outA = Math.clamp((int)(finalA * 255), 0, 255);

        return (outA << 24) | (outR << 16) | (outG << 8) | outB;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderConnections(GuiGraphics guiGraphics, float partialTick) {
        RenderSystem.enableBlend();
        Set<String> drawn = new HashSet<>();

        float cx = width / 2f;
        float cy = height / 2f;

        for (OverviewNode node : nodes) {
            Vec2 src = node.getCenterPos();
            float sx = src.x + (float) scrollX + cx;
            float sy = src.y + (float) scrollY + cy;

            for (ResourceLocation connId : node.getConnections()) {
                OverviewNode target = nodeById.get(connId);
                if (target == null) continue;

                String key = node.getNodeId().toString().compareTo(connId.toString()) < 0 ? node.getNodeId() + ">" + connId : connId + ">" + node.getNodeId();
                if (!drawn.add(key)) continue;

                Vec2 dst = target.getCenterPos();
                float tx = dst.x + (float) scrollX + cx;
                float ty = dst.y + (float) scrollY + cy;

                float minX = Math.min(sx, tx);
                float maxX = Math.max(sx, tx);
                float minY = Math.min(sy, ty);
                float maxY = Math.max(sy, ty);

                if (maxX < 0 || minX > this.width || maxY < 0 || minY > this.height) {
                    continue;
                }
                drawGradientLine(guiGraphics, sx, sy, tx, ty, 2, 5, 0xFF66FFE3, 0xFF28698d, 0x0028698d);
            }
        }
        RenderSystem.disableBlend();
    }


    // ============ 输入处理 ============

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (isDragging && button == 0) {
            scrollX = dragStartScrollX + (mouseX - dragStartMouseX);
            scrollY = dragStartScrollY + (mouseY - dragStartMouseY);
            clampScroll();
            positionAllNodes();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int slot = keyCode - GLFW.GLFW_KEY_1;
        if (slot >= 0 && slot < HOLDER.getEquippedSkillMaxNumber()) {
            if (theCurrentlySelectedSkill != null) {
                HOLDER.equipSkill(slot, theCurrentlySelectedSkill.getStack());
                hotbar.rebuildFromHolder();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ============ 装配逻辑 ============

    public boolean equipSelectedToSlot(int slot) {
        if (theCurrentlySelectedSkill == null) return false;
        return HOLDER.equipSkill(slot, theCurrentlySelectedSkill.getStack());
    }

    public void rebuildHotbar() {hotbar.rebuildFromHolder();}

    // ============ 通用工具 ============

    protected void updateHotbarPos() {
        hotbar.setPosition(width / 2 - hotbar.getWidth() / 2, (int) (height * 0.82f));
    }

    public void uiButtonClickSound() {
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
