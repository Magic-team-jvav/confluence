package org.confluence.mod.client.gui.hud.soul.quick_skill;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.widget.soul_skill.SoulSkillBox;
import org.confluence.mod.client.handler.SoulSkillClientHolder;
import org.confluence.mod.client.util.SoulQuickSkillHudUtils;
import org.confluence.mod.common.soulskill.SoulSkillStack;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.client.PortDeltaTicker;

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
    private static final ResourceLocation TEX = Confluence.asResource("hud/soul_quick_skill_hud/card_horizontal");
    private static final ResourceLocation TEX_SEL = Confluence.asResource("hud/soul_quick_skill_hud/card_horizontal_select");

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

    @Override
    public void render(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
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

    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        if (!isType()) return;

        int currentIdx = soulSkillHolder.getCurrentIndex();
        int totalSkills = soulSkillHolder.getEquippedSkillMaxNumber();
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

        for (int i = 0; i < TOTAL_SLOTS; i++) {
            int slotOffset = i - ADDITIONAL_COUNT;

            float virtualPos = slotOffset + totalSlide;
            while (virtualPos > VISIBLE_RANGE) virtualPos -= TOTAL_SLOTS;
            while (virtualPos < -VISIBLE_RANGE) virtualPos += TOTAL_SLOTS;

            int effectiveOffset = Math.round(virtualPos);
            int skillIdx = SoulQuickSkillHudUtils.calculateSkillIndex(effectiveOffset, currentIdx, totalSkills);
            SoulSkillStack stack = soulSkillHolder.getCurrentSkillStack(skillIdx);
            boolean selected = Math.abs(virtualPos) < 0.5f;

            // 弧形坐标
            float t = virtualPos / ADDITIONAL_COUNT;
            float angle = Mth.clamp(t, -1f, 1f) * HALF_ARC_RADIANS;
            float cosA = (float) Math.cos(angle);
            float arcShift = EXTRA_BULGE * (1f - cosA); // 0=中心, 正值=远离中心向屏幕外偏移
            float yOffset = CARD_SPACING * virtualPos;

            // 距中心距离 → 透明度、缩放（超出 arc 范围时完全透明）
            float dist = Math.abs(virtualPos) / ADDITIONAL_COUNT;
            float alpha = Mth.clamp(1f - (1f - EDGE_ALPHA) * dist, 0f, 1f);
            float scale = 1f + (CENTER_SCALE - 1f) * Mth.clamp(1f - dist, 0f, 1f);

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
        float clamp = CARD_WIDTH * CENTER_SCALE - Mth.clamp(renderTime, 0, 1) * v2;
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

    private void renderCard(GuiGraphics guiGraphics, @Nullable SoulSkillStack stack, boolean selected, int skillIdx, float scale) {
        ResourceLocation tex = selected ? TEX_SEL : TEX;
        guiGraphics.blitSprite(tex, 0, 0, CARD_WIDTH, CARD_HEIGHT);

        if (selected) {
            SoulSkillBox.renderBoxActivate(guiGraphics, 0, 0);
        }

        if (!SoulSkillStack.isEmpty(stack)) {
            SoulSkillBox.renderSkillIcon(guiGraphics, stack, 8, 8);
            SoulSkillBox.drawSkillStackName(guiGraphics, font, 28, CARD_HEIGHT / 2, stack, false);
        }
    }

    public void adjustTarget(int offset) {
        targetRotation -= offset;
    }

    @Override
    public void open() {
        if (!isType()) return;
        update();
        active = true;
    }

    @Override
    public void close() {
        active = false;
        if (!isType()) {}
    }

    @Override
    public void update() {}

    @Override
    public SoulSkillClientHolder.Type getType() {
        return isRight ? SoulSkillClientHolder.Type.CARD_HORIZONTAL_R
                : SoulSkillClientHolder.Type.CARD_HORIZONTAL_L;
    }
}
