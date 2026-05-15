package org.confluence.mod.client.gui.hud.soul.quick_skill;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec2;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.soul.CurrentSelectedSkillHud;
import org.confluence.mod.client.gui.widget.SoulSkillBox;
import org.confluence.mod.client.handler.SoulQuickSkillHudHolder;
import org.confluence.mod.common.soulskill.SoulSkillStack;

import java.util.*;

public class RouletteWheelSmallHud extends BasicSoulQuickSkillHud {
    // 资源与常量定义
    public static final ResourceLocation TEXTURE = Confluence.asResource("hud/soul_quick_skill_hud/roulette_wheel_small");
    public static final ResourceLocation BOX_GRAY_TEXTURE = Confluence.asResource("hud/soul_quick_skill_hud/box_gray");
    private static final int DISPLAY_COUNT = 8;                    // 显示的技能框数量
    private static final float INTERVAL = 360f / DISPLAY_COUNT;    // 每个技能框的角度间隔
    private static final float INTERVAL_RADIANS = (float) Math.toRadians(INTERVAL); // 角度间隔（弧度）
    private static final float SKILL_BOX_RADIUS = 33f;             // 技能框距离中心的半径
    private static final int HALF_DISPLAY = DISPLAY_COUNT / 2;     // 显示数量的一半（4）

    // 状态变量
    private float angle;                                  // 当前角度
    private float targetAngle;                            // 目标角度
    private float renderAngle;                            // 渲染角度（用于平滑过渡）
    private HumanoidArm humanoidArm = HumanoidArm.RIGHT;  // 玩家主手
    private float timeO;
    private float time;
    private float renderTime;

    public RouletteWheelSmallHud() {
        super();
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float realtimeDeltaTicks = deltaTracker.getRealtimeDeltaTicks();
        if (!active && time <= 0) {
//            return;
        }
        if (active) {
            if (time * 0.5f < 1) {
                time = time + realtimeDeltaTicks;
            }
        } else {
            if (time > 0) {
                time = time - realtimeDeltaTicks;
            }
        }
        timeO = renderTime = Mth.lerp(realtimeDeltaTicks, timeO, time);
        // 计算平滑过渡的渲染角度
        renderAngle = Mth.rotLerp(realtimeDeltaTicks, angle, targetAngle);
        angle = renderAngle;
        super.render(guiGraphics, deltaTracker);
    }

    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!isType()) {
            return;
        }
        CurrentSelectedSkillHud instance = SoulQuickSkillHudHolder.CURRENT_SELECTED_SKILL_HUD_INSTANCE;
        PoseStack poseStack = guiGraphics.pose();
        float realtimeDeltaTicks = deltaTracker.getRealtimeDeltaTicks();

        // 计算轮盘中心位置
        float centerX = instance.getX() + instance.box.getWidth() / 2f;
        float centerY = instance.getY() + instance.box.getHeight() / 2f;

        poseStack.pushPose();
        {
            poseStack.translate(0, (128 / 2f - 31) * Math.clamp(1 - renderTime, 0, 1), 0);
            poseStack.translate(centerX, centerY, 0);

            // 绘制轮盘背景
            poseStack.pushPose();
            {
                int v = -128 / 2;
                guiGraphics.blitSprite(TEXTURE, v, v, 128, 128);
                poseStack.mulPose(Axis.ZP.rotationDegrees(renderAngle));
                poseStack.translate(0, 0, 1);

                // 绘制技能框
                renderSkillBoxes(guiGraphics, poseStack, realtimeDeltaTicks);
            }
            poseStack.popPose();

            // 绘制顶部选中框
            poseStack.pushPose();
            {
                poseStack.translate(0, 0, 2);
                int x = -32 / 2;
                int y = (int) (-32 / 2f - SKILL_BOX_RADIUS - 2);
                guiGraphics.blitSprite(SoulSkillBox.BOX, x, y, 32, 32);
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    /**
     * 渲染所有技能框
     */
    private void renderSkillBoxes(GuiGraphics guiGraphics, PoseStack poseStack, float realtimeDeltaTicks) {
        poseStack.pushPose();
        float startAngleOffset = RouletteWheelBigHud.START_ANGLE_OFFSET;
        int totalSkills = hudHolder.getSkillTotalNumber();
        int currentIdx = hudHolder.getCurrentIndex();

        // 计算当前轮盘旋转对应的槽位偏移量
        int rotationOffset = (int) (-targetAngle / INTERVAL) % DISPLAY_COUNT;
        if (rotationOffset < 0) {
            rotationOffset += DISPLAY_COUNT;
        }

        // 调试信息构建
        // MutableComponent debugMsg = Component.empty();

        for (int slotIdx = 0; slotIdx < DISPLAY_COUNT; slotIdx++) {
            int skillIdx;
            // 计算当前槽位相对于旋转中心的逻辑偏移
            int slotOffset = slotIdx - rotationOffset;

            if (slotIdx == rotationOffset) {
                // 当前选中槽位，直接使用当前技能索引
                skillIdx = currentIdx;
            } else {
                // 处理循环偏移，将范围限制在 [-4, 4]
                if (slotOffset > HALF_DISPLAY) {
                    slotOffset -= DISPLAY_COUNT;
                } else if (slotOffset < -HALF_DISPLAY) {
                    slotOffset += DISPLAY_COUNT;
                }
                // 根据逻辑偏移计算实际技能索引
                skillIdx = calculateSkillIndex(slotOffset, totalSkills);
            }

            // 调试信息追加
            // appendDebugInfo(debugMsg, rotationOffset, slotIdx, slotOffset, skillIdx, slotIdx == rotationOffset);

            SoulSkillStack stack = hudHolder.getCurrentSkillStack(skillIdx);
            if (stack == null) {
                continue;
            }

            // 计算技能框在圆周上的位置
            Vec2 pos = LibMathUtils.pointFromAngle(SKILL_BOX_RADIUS, slotIdx * INTERVAL_RADIANS - startAngleOffset / 2f);

            // 绘制技能框
            poseStack.pushPose();
            {
                poseStack.translate((int) pos.x, (int) pos.y, 0);
                // 反向旋转以保持技能图标直立
                poseStack.mulPose(Axis.ZP.rotationDegrees(-renderAngle));
                guiGraphics.blitSprite(BOX_GRAY_TEXTURE, -22 / 2, -22 / 2, 22, 22);
                SoulSkillBox.renderSkillIcon(guiGraphics, stack, -16 / 2, -16 / 2);

                // 绘制技能索引
//                drawSkillBoxIndex(guiGraphics, poseStack, skillIdx);
                poseStack.translate(-10, -10, 0);
                // 绘制槽位索引
//                drawSkillBoxIndex(guiGraphics, poseStack, slotIdx);
            }
            poseStack.popPose();
        }

        // 显示调试信息
        // minecraft.gui.setOverlayMessage(debugMsg, false);

        poseStack.popPose();
    }

    /**
     * 构建调试信息并追加到组件中
     *
     * @param component      目标组件
     * @param rotationOffset 旋转偏移量
     * @param slotIdx        当前槽位索引
     * @param slotOffset     逻辑偏移量
     * @param skillIdx       计算出的技能索引
     * @param isCurrent      是否为当前选中槽位
     */
    private void appendDebugInfo(MutableComponent component, int rotationOffset, int slotIdx,
                                 int slotOffset, int skillIdx, boolean isCurrent) {
        String debugText = "%s %s %s %s".formatted(rotationOffset, slotIdx, slotOffset, skillIdx);
        if (isCurrent) {
            component.append(Component.literal(debugText).withColor(0xffec00));
        } else {
            component.append(debugText);
        }
        // 添加分隔符
        if (slotIdx != DISPLAY_COUNT - 1) {
            component.append(", ");
        }
    }

    /**
     * 绘制技能框索引文本
     */
    private void drawSkillBoxIndex(GuiGraphics guiGraphics, PoseStack poseStack, int skillIndex) {
        poseStack.pushPose();
        String text = String.valueOf(skillIndex);
        int lineWidth = font.width(text);
        int lineHeight = font.lineHeight;
        poseStack.translate(0, 0, 1);
        guiGraphics.drawString(font, text,
                (int) (lineWidth / 2f - 16 / 2f),
                (int) (lineHeight / 2f - 16 / 2f),
                -1);
        poseStack.popPose();
    }

    /**
     * 调整目标索引（处理滚动输入）
     *
     * @param scrollDelta 滚动增量
     */
    public void adjustTarget(int scrollDelta) {
        int totalSkills = hudHolder.getSkillTotalNumber();

        if (totalSkills <= 0) {
            targetAngle = 0;
            return;
        }

        targetAngle = (targetAngle - scrollDelta * INTERVAL) % 360;

        // 基于当前索引计算新索引
        int newIdx = calculateSkillIndex(scrollDelta, totalSkills);

        // 更新当前索引
        hudHolder.setCurrentIndex(newIdx);
    }

    /**
     * 根据偏移量计算实际技能索引（处理循环边界）
     *
     * @param offset      索引偏移量（可正可负）
     * @param totalSkills 技能总数
     * @return 计算后的实际技能索引
     */
    private int calculateSkillIndex(int offset, int totalSkills) {
        int currentIdx = hudHolder.getCurrentIndex();
        int resultIdx = currentIdx + offset;

        // 处理循环：确保索引在有效范围内 [0, totalSkills-1]
        resultIdx = resultIdx % totalSkills;
        if (resultIdx < 0) {
            resultIdx += totalSkills;
        }
        return resultIdx;
    }

    @Override
    public void open() {
        if (!isType()) {
            return;
        }
        active = true;

        // 调试
//        hudHolder.setCurrentIndex(0);
//        targetAngle = 0;
    }

    @Override
    public void close() {
        active = false;
        if (!isType()) {
            return;
        }
    }

    @Override
    public void update() {}

    @Override
    public void init(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        super.init(guiGraphics, deltaTracker);
        HumanoidArm mainArm = getPlayerThrow().getMainArm();
        if (mainArm != this.humanoidArm) {
            humanoidArm = mainArm;
        }
    }

    @Override
    public SoulQuickSkillHudHolder.Type getType() {
        return SoulQuickSkillHudHolder.Type.ROULETTE_WHEEL_SMALL;
    }

    @Override
    protected void drawTooltip(GuiGraphics guiGraphics, DeltaTracker deltaTracker, PoseStack poseStack, Font font) {

    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
