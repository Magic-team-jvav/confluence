package org.confluence.mod.client.gui.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
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
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Supplier;

/**
 * 灵魂技能总览界面 — 类似原版进度界面的技能树视图
 */
public class SoulOverviewScreen extends Screen {
    private static final SoulSkillClientHolder HOLDER = SoulSkillClientHolder.INSTANCE;

    /* 组件 */
    public final HotbarWidget hotbar;
    public final List<OverviewNode> nodes;
    public final Map<ResourceLocation, OverviewNode> nodeById = new HashMap<>();
    public final List<NavTab> navTabs = new ArrayList<>();
    private CenterButton centerButton;

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
        initSkillsFromRegistry();
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
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        tickScrollAnimation(partialTick);
        renderConnections(guiGraphics, partialTick);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderConnections(GuiGraphics guiGraphics, float partialTick) {
        RenderSystem.enableBlend();

        Set<String> drawn = new HashSet<>();
//        guiGraphics.fill();
        VertexConsumer buf = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        PoseStack poseStack = guiGraphics.pose();
        Matrix4f pose = poseStack.last().pose();

        float ht = 1.0f;
        float cx = width / 2f;
        float cy = height / 2f;

        for (OverviewNode node : nodes) {
            Vec2 src = node.getCenterPos();
            float sx = src.x + (float) scrollX + cx;
            float sy = src.y + (float) scrollY + cy;

            for (ResourceLocation connId : node.getConnections()) {
                OverviewNode target = nodeById.get(connId);
                if (target == null) continue;
                boolean sourceRendered = node.visible && node.isOnScreen(width, height);
                boolean targetRendered = target.visible && target.isOnScreen(width, height);
                if (!sourceRendered && !targetRendered) continue;

                String key = node.getNodeId().toString().compareTo(connId.toString()) < 0
                        ? node.getNodeId() + ">" + connId
                        : connId + ">" + node.getNodeId();
                if (!drawn.add(key)) continue;

                Vec2 dst = target.getCenterPos();
                float tx = dst.x + (float) scrollX + cx;
                float ty = dst.y + (float) scrollY + cy;

                float dx = tx - sx;
                float dy = ty - sy;
                float len = (float) Math.sqrt(dx * dx + dy * dy);
                if (len < 0.001f) continue;
                float nx = -dy / len * ht;
                float ny = dx / len * ht;

                int color = 0xFF808080;
                buf.addVertex(pose, sx + nx, sy + ny, 0).setColor(color);
                buf.addVertex(pose, sx - nx, sy - ny, 0).setColor(color);
                buf.addVertex(pose, tx - nx, ty - ny, 0).setColor(color);
                buf.addVertex(pose, tx + nx, ty + ny, 0).setColor(color);

                buf.addVertex(pose, sx + nx, sy + ny, 0).setColor(color);
                buf.addVertex(pose, tx + nx, ty + ny, 0).setColor(color);
                buf.addVertex(pose, tx - nx, ty - ny, 0).setColor(color);
                buf.addVertex(pose, sx - nx, sy - ny, 0).setColor(color);

            }
        }
        guiGraphics.flushIfUnmanaged();
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
