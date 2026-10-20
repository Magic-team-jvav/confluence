package org.confluence.mod.client.gui.hud.soulcerer.quick_skill;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.mesdag.portlib.client.PortDeltaTicker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec2;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.soulcerer.CurrentSelectedSkillHud;
import org.confluence.mod.client.util.soulcerer.SoulQuickSkillHudUtils;
import org.confluence.mod.client.gui.widget.soulcerer.skill.SoulSkillBox;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.confluence.mod.util.PlayerUtils;

public class RouletteWheelSmallHud extends BasicSoulQuickSkillHud {
    // 资源与常量定义
    public static final ResourceLocation TEXTURE = Confluence.asResource("hud/soulcerer_quick_skill_hud/roulette_wheel_small");
    private static final int DISPLAY_COUNT = SoulQuickSkillHudUtils.DEFAULT_DISPLAY_COUNT;                    // 显示的技能框数量
    private static final float INTERVAL = SoulQuickSkillHudUtils.DEFAULT_INTERVAL;    // 每个技能框的角度间隔
    private static final float INTERVAL_RADIANS = SoulQuickSkillHudUtils.DEFAULT_INTERVAL_RADIANS; // 角度间隔（弧度）
    private static final float SKILL_BOX_RADIUS = 33.0f;            // 技能框距离中心的半径
    private static final int HALF_DISPLAY = SoulQuickSkillHudUtils.DEFAULT_HALF_DISPLAY;      // 显示数量的一半
    public static final int TEXT_FADE_OUT_TIME = (int) (0.5 * 20);  // 文本淡出时间
    public static final int TEXT_TIME = 2 * 20;                     // 文本显示时间
    public static final int ROULETTE_SIZE = 128;

    // 状态变量
    private HumanoidArm humanoidArm = HumanoidArm.RIGHT;    // 玩家主手
    private float angleO;
    private float targetAngle;
    private float renderAngle;
    private float timeO;
    private float targeTime;
    private float renderTime;
    private float textTime;

    public RouletteWheelSmallHud() {
        super();
    }

    /// 推进轮盘旋转、展开和技能名称淡出动画。
    @Override
    public void render(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        float realtimeDeltaTicks = deltaTracker.getRealtimeDeltaTicks();
        if (active) {
            if (targeTime * 0.5f < 1) {
                targeTime = targeTime + realtimeDeltaTicks;
            }
        } else {
            if (targeTime > 0) {
                targeTime = targeTime - realtimeDeltaTicks;
            }
        }
        timeO = renderTime = Mth.lerp(realtimeDeltaTicks, timeO, targeTime);
        // 计算平滑过渡的渲染角度
        renderAngle = Mth.rotLerp(realtimeDeltaTicks, angleO, targetAngle);
        angleO = renderAngle;

        if (textTime > 0) {
            textTime -= realtimeDeltaTicks;
        }

        super.render(guiGraphics, deltaTracker);
    }

    /// 围绕当前技能框绘制小轮盘及选中提示。
    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        if (!isType()) {
            return;
        }
        CurrentSelectedSkillHud instance = ClientSoulHandler.CURRENT_SELECTED_SKILL_HUD_INSTANCE;
        PoseStack poseStack = guiGraphics.pose();
        float realtimeDeltaTicks = deltaTracker.getRealtimeDeltaTicks();

        // 计算轮盘中心位置
        float centerX = instance.getX() + instance.box.getWidth() / 2f;
        float centerY = instance.getY() + instance.box.getHeight() / 2f;

        poseStack.pushPose();
        {
            poseStack.translate(0, (ROULETTE_SIZE / 2f - 31) * Mth.clamp(1 - renderTime, 0, 1), 0);
            poseStack.translate(centerX, centerY, 0);

            // 绘制轮盘背景
            poseStack.pushPose();
            {
                int offset = -ROULETTE_SIZE / 2;
                guiGraphics.blitSprite(TEXTURE, offset, offset, ROULETTE_SIZE, ROULETTE_SIZE);
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
                int offset = -SoulSkillBox.BOX_SIZE / 2;
                int x = offset;
                int y = offset - (int) SKILL_BOX_RADIUS;
                SoulSkillBox.renderBox(guiGraphics, x, y);

                // 绘制技能框内技能名称
                if (textTime > 0) {
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(1, 1, 1, Mth.clamp(textTime / TEXT_FADE_OUT_TIME, 0, 1));
                    drawSkillStackName(guiGraphics, font, 0, y - 5);
                    guiGraphics.flushIfUnmanaged();
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                    RenderSystem.disableBlend();
                }
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
        int totalSkills = PlayerUtils.getEquippedSoulSkillMaxNumber(player);
        int currentIdx = PlayerUtils.getCurrentSoulSkillIndex(player);

        // 计算当前轮盘旋转对应的槽位偏移量
        int rotationOffset = SoulQuickSkillHudUtils.calculateRotationOffset(targetAngle, INTERVAL, DISPLAY_COUNT);

        for (int slotIdx = 0; slotIdx < DISPLAY_COUNT; slotIdx++) {
            int skillIdx = SoulQuickSkillHudUtils.getSkillIndex(slotIdx, rotationOffset, currentIdx, totalSkills, DISPLAY_COUNT, HALF_DISPLAY);

            SoulSkillStack stack = PlayerUtils.getSkillStack(player, skillIdx);
            if (stack.isEmpty()) {
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
                SoulSkillBox.renderBoxGray(guiGraphics, -SoulSkillBox.BOX_GRAY_SIZE / 2, -SoulSkillBox.BOX_GRAY_SIZE / 2);
                SoulSkillBox.renderSkillIcon(guiGraphics, stack, -SoulSkillBox.SKILL_SIZE / 2, -SoulSkillBox.SKILL_SIZE / 2);

                poseStack.translate(-10, -10, 0);
            }
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    /**
     * 调整目标索引
     */
    public void adjustTarget(int offset) {
        int totalSkills = PlayerUtils.getEquippedSoulSkillMaxNumber(player);

        if (totalSkills <= 0) {
            targetAngle = 0;
            return;
        }

        targetAngle = (targetAngle - offset * INTERVAL) % 360;
        textTime = TEXT_FADE_OUT_TIME + TEXT_TIME;
    }

    /// 打开当前选中的小轮盘样式。
    @Override
    public void open() {
        if (!isType()) {
            return;
        }
        update();
        active = true;
    }

    /// 关闭小轮盘并保留收起动画。
    @Override
    public void close() {
        active = false;
        if (!isType()) {
            return;
        }
    }

    /// 小轮盘直接读取玩家数据，无需额外刷新缓存。
    @Override
    public void update() {

    }

    /// 更新基础 HUD 状态和玩家主手方向。
    @Override
    public void init(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        super.init(guiGraphics, deltaTracker);
        HumanoidArm mainArm = getPlayerThrow().getMainArm();
        if (mainArm != this.humanoidArm) {
            humanoidArm = mainArm;
        }
    }

    /// 返回小轮盘对应的配置样式。
    @Override
    public ClientSoulHandler.Type getType() {
        return ClientSoulHandler.Type.ROULETTE_WHEEL_SMALL;
    }

    /// 居中绘制当前槽位技能名称，空槽不显示。
    protected void drawSkillStackName(GuiGraphics guiGraphics, Font font, int x, int y) {
        SoulSkillStack stack = PlayerUtils.getCurrentSkillStack(player);
        if (stack.isEmpty()) {
            return;
        }
        SoulSkillBox.drawSkillStackName(guiGraphics, font, x, y, stack, true);
    }
}
