package org.confluence.mod.client.gui.hud.soulcerer.quick_skill;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.util.soulcerer.SoulQuickSkillHudUtils;
import org.confluence.mod.client.gui.widget.soulcerer.skill.SoulSkillBox;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.confluence.mod.util.PlayerUtils;

import java.util.Arrays;
import java.util.Comparator;

public class CardHorizontalHud extends BasicSoulQuickSkillHud {
    // ==================== 可配置参数 ====================
    /**
     * 中心两侧额外显示的卡片数（默认 2 → 共 5 张）
     */
    protected static final int ADDITIONAL_COUNT = 3;
    /**
     * 总槽位数
     */
    protected static final int TOTAL_SLOTS = ADDITIONAL_COUNT * 2 + 1;
    /**
     * 弧形总跨度（度）
     */
    protected static final float ARC_ANGLE_DEGREES = 60f;
    /**
     * 弧形总跨度的一半（弧度）
     */
    private static final float HALF_ARC_RADIANS = (float) Math.toRadians(ARC_ANGLE_DEGREES / 2f);

    /**
     * 中心卡片贴边距离（像素），计入放大后的视觉间距
     */
    protected static final float BULGE_DEPTH = 6f;
    /**
     * 卡片纵向间距（像素）
     */
    protected static final float CARD_SPACING = 20f;
    /**
     * 弧线凸出幅度（像素），越大曲线越明显
     */
    protected static final float EXTRA_BULGE = 60f;
    /**
     * 中心卡片放大倍率
     */
    protected static final float CENTER_SCALE = 1.25f;
    /**
     * 边缘透明度，0=全透明
     */
    protected static final float EDGE_ALPHA = 0f;
    /**
     * 可见范围（槽位单位），超出后换位。较大值让卡片在屏幕外消失后再换位
     */
    private static final float VISIBLE_RANGE = ADDITIONAL_COUNT + 0.4f;

    // ==================== 纹理资源 ====================
    private static final ResourceLocation TEX = Confluence.asResource("hud/soulcerer_quick_skill_hud/card_horizontal");
    private static final ResourceLocation TEX_SEL = Confluence.asResource("hud/soulcerer_quick_skill_hud/card_horizontal_select");

    public static final int CARD_WIDTH = 128;
    public static final int CARD_HEIGHT = 32;

    protected final boolean isRight;

    private float targetRotation;
    private float prevRotation;
    private float renderRotation;
    private float timeO;
    private float targeTime;
    private float renderTime;

    public CardHorizontalHud(boolean isRight) {
        super();
        this.isRight = isRight;
    }

    /// 推进卡片切换和展开动画后渲染。
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float delta = deltaTracker.getRealtimeDeltaTicks();
        renderRotation = Mth.lerp(delta, prevRotation, targetRotation);
        prevRotation = renderRotation;
        if (active) {
            if (targeTime * 0.5f < 1) {
                targeTime = targeTime + delta * 0.5f;
            }
        } else {
            if (targeTime > 0) {
                targeTime = targeTime - delta * 0.1f;
            }
        }
        timeO = renderTime = Mth.lerp(delta, timeO, targeTime);

        if (renderTime <= 0.001f) {
            return;
        }

        super.render(guiGraphics, deltaTracker);
    }

    /// 按当前技能槽位绘制左右卡片栏。
    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!isType()) return;

        int currentIdx = PlayerUtils.getCurrentSoulSkillIndex(player);
        int totalSkills = PlayerUtils.getEquippedSoulSkillMaxNumber(player);
        if (totalSkills <= 0) return;

        float totalSlide = renderRotation;
        float centerY = getScreenHeight() * 0.8f;
        float screenWidth = getScreenWidth();

        // 第一遍：计算所有卡片数据
        float[] virtualPosArr = new float[TOTAL_SLOTS];
        float[] xPosArr = new float[TOTAL_SLOTS];
        float[] yPosArr = new float[TOTAL_SLOTS];
        float[] scaleArr = new float[TOTAL_SLOTS];
        float[] alphaArr = new float[TOTAL_SLOTS];
        int[] skillIdxArr = new int[TOTAL_SLOTS];
        boolean[] selectedArr = new boolean[TOTAL_SLOTS];
        SoulSkillStack[] stackArr = new SoulSkillStack[TOTAL_SLOTS];
        Arrays.fill(stackArr, SoulSkillStack.Empty);

        for (int i = 0; i < TOTAL_SLOTS; i++) {
            int slotOffset = i - ADDITIONAL_COUNT;

            float virtualPos = slotOffset + totalSlide;
            while (virtualPos > VISIBLE_RANGE) virtualPos -= TOTAL_SLOTS;
            while (virtualPos < -VISIBLE_RANGE) virtualPos += TOTAL_SLOTS;

            int effectiveOffset = Math.round(virtualPos);
            int skillIdx = SoulQuickSkillHudUtils.calculateSkillIndex(effectiveOffset, currentIdx, totalSkills);
            SoulSkillStack stack = PlayerUtils.getSkillStack(player, skillIdx);
            boolean selected = Math.abs(virtualPos) < 0.5f;

            // 弧形坐标
            float t = virtualPos / ADDITIONAL_COUNT;
            float angle = Math.clamp(t, -1f, 1f) * HALF_ARC_RADIANS;
            float cosA = (float) Math.cos(angle);
            float arcShift = EXTRA_BULGE * (1f - cosA); // 0=中心, 正值=远离中心向屏幕外偏移
            float yOffset = CARD_SPACING * virtualPos;

            // 距中心距离 → 透明度、缩放（超出 arc 范围时完全透明）
            float dist = Math.abs(virtualPos) / ADDITIONAL_COUNT;
            float alpha = Math.clamp(1f - (1f - EDGE_ALPHA) * dist, 0f, 1f);
            float scale = 1f + (CENTER_SCALE - 1f) * Math.clamp(1f - dist, 0f, 1f);

            // X 位置：中心贴边，远离中心的卡片向屏幕外偏移
            float visualEdge = BULGE_DEPTH - arcShift;
            float xPos, yPos = centerY + yOffset - CARD_HEIGHT / 2f;
            if (isRight) {
                xPos = screenWidth - CARD_WIDTH - visualEdge;
            } else {
                xPos = visualEdge;
            }

            virtualPosArr[i] = virtualPos;
            xPosArr[i] = xPos;
            yPosArr[i] = yPos;
            scaleArr[i] = scale;
            alphaArr[i] = alpha;
            skillIdxArr[i] = skillIdx;
            selectedArr[i] = selected;
            stackArr[i] = stack;
        }

        // 按距中心距离降序排列（远的先画，近的在上层）
        Integer[] renderOrder = new Integer[TOTAL_SLOTS];
        for (int i = 0; i < TOTAL_SLOTS; i++) renderOrder[i] = i;
        Arrays.sort(renderOrder, Comparator.comparingDouble(i -> -Math.abs(virtualPosArr[i])));

        // 第二遍：按 Z 顺序渲染
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        float v2 = CARD_WIDTH * CENTER_SCALE;
        float clamp = CARD_WIDTH * CENTER_SCALE - Math.clamp(renderTime, 0, 1) * v2;
        poseStack.translate(isRight ? clamp : -clamp, 0, 0);
        for (int idx : renderOrder) {
            float x = xPosArr[idx];
            float y = yPosArr[idx];
            float s = scaleArr[idx];
            float a = alphaArr[idx];
            int sidx = skillIdxArr[idx];
            boolean sel = selectedArr[idx];

            poseStack.pushPose();
            poseStack.translate(x, y, a * 10);

            float scaleAmount = 1f + (s - 1f) * 0.5f;
            float v = CARD_WIDTH / 2f;
            poseStack.translate(v, v, 0);
            poseStack.scale(scaleAmount, scaleAmount, 1f);
            poseStack.translate(-v, -v, 0);

            RenderSystem.enableBlend();
            guiGraphics.setColor(1f, 1f, 1f, a);
            renderCard(guiGraphics, stackArr[idx], sel, sidx, s);
            guiGraphics.setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    /// 绘制单张技能卡片及其选中效果。
    private void renderCard(GuiGraphics guiGraphics, SoulSkillStack stack, boolean selected, int skillIdx, float scale) {
        ResourceLocation tex = selected ? TEX_SEL : TEX;
        guiGraphics.blitSprite(tex, 0, 0, CARD_WIDTH, CARD_HEIGHT);

        if (selected) {
            SoulSkillBox.renderBoxActivate(guiGraphics, 0, 0);
        }

        if (!stack.isEmpty()) {
            SoulSkillBox.renderSkillIcon(guiGraphics, stack, 8, 8);
            SoulSkillBox.drawSkillStackName(guiGraphics, font, 28, CARD_HEIGHT / 2, stack, false);
        }
    }

    /// 按滚动方向调整卡片目标位置。
    public void adjustTarget(int offset) {
        targetRotation -= offset;
    }

    /// 打开当前选中的卡片样式。
    @Override
    public void open() {
        if (!isType()) return;
        update();
        active = true;
    }

    /// 关闭卡片栏并保留收起动画。
    @Override
    public void close() {
        active = false;
        if (!isType()) return;
    }

    /// 卡片直接读取玩家数据，无需额外刷新缓存。
    @Override
    public void update() {}

    /// 按左右方向返回对应的卡片样式。
    @Override
    public ClientSoulHandler.Type getType() {
        return isRight ? ClientSoulHandler.Type.CARD_HORIZONTAL_R
                : ClientSoulHandler.Type.CARD_HORIZONTAL_L;
    }
}
