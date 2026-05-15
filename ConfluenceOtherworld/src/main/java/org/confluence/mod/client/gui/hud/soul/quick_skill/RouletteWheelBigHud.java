package org.confluence.mod.client.gui.hud.soul.quick_skill;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.client.gui.widget.SoulSkillBox;
import org.confluence.mod.client.handler.SoulQuickSkillHudHolder;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class RouletteWheelBigHud extends BasicSoulQuickSkillHud {
    // 常量定义
    private static final int DEFAULT_SIDES_NUMBER = 24;             // 轮盘默认边数
    private static final float MAGNIFICATION_RATIO = 1.1f;          // 选中项的放大比例
    private static final float SKILL_INTERVAL_RATIO = 0.025f;       // 技能间隔占技能弧度的比例
    public static final float START_ANGLE_OFFSET = (float) Math.PI; // 起始角度偏移(从左边开始)
    private static final float TWO_PI = (float) (2 * Math.PI);      // 2π
    private static final float OUTER_RADIUS = 80.0f;                // 轮盘外半径
    private static final float INNER_RADIUS = 20.0f;                // 轮盘内半径

    // 颜色配置
    private static final Color DEFAULT_COLOR = new Color(10, 8, 3);
    private static final float DEFAULT_ALPHA = 0.5f;
    private static final Color HIGHLIGHT_COLOR = new Color(46, 240, 211);
    private static final float HIGHLIGHT_ALPHA = 0.7f;

    /**
     * 当前选中的技能索引（-1表示未选择）
     */
    private int wheelSelection = -1;

    private final MouseHandler mouseHandler;
    private final Window window;
    private final List<SoulSkillBox> boxList = new ArrayList<>();

    public RouletteWheelBigHud() {
        super();
        mouseHandler = getMinecraft().mouseHandler;
        window = getMinecraft().getWindow();
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        // 鼠标点击时关闭轮盘
        if ((mouseHandler.isLeftPressed() || mouseHandler.isRightPressed()) && active) {
            close();
            return;
        }

        if (!active) {
            return;
        }

        super.render(guiGraphics, deltaTracker);
    }

    @Override
    public void open() {
        if (!isType()) {
            return;
        }
        active = true;
        // 调试
//        hudHolder.setSkillTotalNumber(4);
        update();
        wheelSelection = -1;
        mouseHandler.releaseMouse();
    }

    @Override
    public void close() {
        active = false;
        if (!isType()) {
            return;
        }

        if (wheelSelection >= 0) {
            hudHolder.setCurrentIndex(wheelSelection);
        }

        mouseHandler.grabMouse();
    }

    @Override
    public void update() {
        int skillTotalNumber = hudHolder.getSkillTotalNumber();

        if (skillTotalNumber <= 0) {
            boxList.clear();
            return;
        }

        int currentSize = boxList.size();
        int disparity = skillTotalNumber - currentSize;

        if (disparity > 0) {
            // 添加缺失的技能框
            for (int i = 0; i < disparity; i++) {
                boxList.add(new SoulSkillBox());
            }
        } else if (disparity < 0) {
            // 移除多余的技能框
            for (int i = 0; i > disparity; i--) {
                boxList.removeLast();
            }
        }

        for (int i = 0; i < skillTotalNumber; i++){
            boxList.get(i).setSkill(hudHolder.getSkillStackList().get(i));
        }
    }

    @Override
    public SoulQuickSkillHudHolder.Type getType() {
        return SoulQuickSkillHudHolder.Type.ROULETTE_WHEEL_BIG;
    }


    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!isType()) {
            return;
        }

        RenderSystem.enableBlend();
        draw(guiGraphics, deltaTracker);
        guiGraphics.flushIfUnmanaged();
        RenderSystem.disableBlend();
    }

    @Override
    protected void sizeChange(int newScreenWidth, int newScreenHeight) {
        super.sizeChange(newScreenWidth, newScreenHeight);
    }

    /**
     * 绘制轮盘主方法
     */
    private void draw(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Font font = getFont();
        Vec2 screenCenter = getScreenCenter();
        int skillTotalNumber = hudHolder.getSkillTotalNumber();

        if (skillTotalNumber <= 0) {
            return;
        }

        // 获取半径配置
        float outerRadius = OUTER_RADIUS;
        float innerRadius = INNER_RADIUS;
        float middleRadius = innerRadius + (outerRadius - innerRadius) / 2;

        // 创建颜色数据
        ColorData defaultColor = new ColorData(DEFAULT_COLOR, DEFAULT_ALPHA);
        ColorData highlightColor = new ColorData(HIGHLIGHT_COLOR, HIGHLIGHT_ALPHA);

        // 计算角度参数
        WheelAngleParams angleParams = calculateWheelAngles(skillTotalNumber);

        // 计算当前选中的技能索引
        Vec2 mousePos = getMousePos();
        float startAngleOffset = START_ANGLE_OFFSET + angleParams.radiansPerSkill / (skillTotalNumber % 2 == 0 ? 2 : 4);
        wheelSelection = calculateWheelSelection(mousePos, screenCenter, innerRadius,
                angleParams.radiansPerSkill(), skillTotalNumber, startAngleOffset);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate((int) screenCenter.x, (int) screenCenter.y, 0);

        // 绘制轮盘扇形和技能框
        drawWheelSegments(guiGraphics, poseStack, font, skillTotalNumber, outerRadius,
                innerRadius, angleParams, defaultColor, highlightColor, startAngleOffset);
        drawSkillBoxes(guiGraphics, poseStack, font, middleRadius, angleParams.radiansPerSkill(),
                deltaTracker, highlightColor, startAngleOffset);

        renderCurrentSelectedSkillBox(guiGraphics, poseStack, mousePos, deltaTracker);

        poseStack.popPose();
        drawTooltip(guiGraphics, deltaTracker, poseStack, font);
    }

    /**
     * 计算轮盘角度参数
     */
    private WheelAngleParams calculateWheelAngles(int skillTotalNumber) {
        // 调整边数以匹配技能数量
        int sidesNumber = Math.max(DEFAULT_SIDES_NUMBER, skillTotalNumber);

        // 每个技能分配到的片数
        int skillSidesNumber = Math.max(1, sidesNumber / skillTotalNumber);

        float radiansPerSide = (float) Math.toRadians(360f / sidesNumber);
        float radiansPerSkill = (float) Math.toRadians(360f / skillTotalNumber);
        float intervalRadians = radiansPerSkill * SKILL_INTERVAL_RATIO;
        float radiusRatio = OUTER_RADIUS / INNER_RADIUS;

        return new WheelAngleParams(sidesNumber, skillSidesNumber, radiansPerSide,
                radiansPerSkill, intervalRadians, radiusRatio);
    }

    /**
     * 绘制轮盘扇形区域
     */
    private void drawWheelSegments(GuiGraphics guiGraphics, PoseStack poseStack, Font font,
                                   int skillTotalNumber, float outerRadius, float innerRadius,
                                   WheelAngleParams angleParams, ColorData defaultColor,
                                   ColorData highlightColor, float startAngleOffset) {
        poseStack.pushPose();
        for (int index = 0; index < angleParams.sidesNumber(); index++) {
            int nextIndex = index + 1;
            poseStack.pushPose();

            // 计算内外环的起始和结束弧度
            AngleRange outerRange = calculateSegmentAngleRange(index, nextIndex, skillTotalNumber,
                    angleParams, true, startAngleOffset);
            AngleRange innerRange = calculateSegmentAngleRange(index, nextIndex, skillTotalNumber,
                    angleParams, false, startAngleOffset);

            // 跳过无效的弧度范围
            if (outerRange.endRadians() == 0) {
                poseStack.popPose();
                continue;
            }

            // 确定颜色和高亮状态
            boolean isHighlighted = (index * skillTotalNumber / angleParams.sidesNumber()) == wheelSelection;
            ColorData colorData = isHighlighted ? highlightColor : defaultColor;

            // 应用高亮效果
            if (isHighlighted) {
                applyHighlightEffect(poseStack);
            }

            // 绘制扇形四边形
            drawSegmentQuad(guiGraphics, poseStack, innerRadius, outerRadius,
                    innerRange, outerRange, colorData);

            // 绘制索引文本
//            drawSegmentIndex(guiGraphics, poseStack, font, index, outerRadius,
//                    angleParams.radiansPerSide(), startAngleOffset);

            poseStack.popPose();
        }
        poseStack.popPose();
    }

    /**
     * 计算扇形的角度范围
     */
    private AngleRange calculateSegmentAngleRange(int index, int nextIndex, int skillTotalNumber,
                                                  WheelAngleParams angleParams, boolean isOuter,
                                                  float startAngleOffset) {
        // 只有一个技能时，内外环角度相同
        float radiansPerSide = angleParams.radiansPerSide();
        if (skillTotalNumber == 1) {
            float startRadians = index * radiansPerSide + startAngleOffset;
            float endRadians = nextIndex * radiansPerSide + startAngleOffset;
            return new AngleRange(startRadians, endRadians);
        }

        int skillSidesNumber = angleParams.skillSidesNumber();
        float intervalRadians = angleParams.intervalRadians();

        // 计算基础角度范围
        float startRadians = index * radiansPerSide + startAngleOffset;
        float endRadians = nextIndex * radiansPerSide + startAngleOffset;

        // 在技能组边界处，间隔平分到两侧
        float halfInterval = intervalRadians / 2f;
        if (!isOuter) {
            halfInterval *= angleParams.radiusRatio;
        }
        // 当前边是技能片组的第一块（不是第0块），在开头加上一半间隔
        if (index == 0 || index % skillSidesNumber == 0) {
            startRadians += halfInterval;
        }

        // 当前边是技能片组的最后一块，在末尾减去一半间隔
        if (nextIndex % skillSidesNumber == 0) {
            endRadians -= halfInterval;
        }

        return new AngleRange(startRadians, endRadians);
    }

    /**
     * 应用高亮效果（缩放和层级提升）
     */
    private void applyHighlightEffect(PoseStack poseStack) {
        poseStack.translate(0, 0, 10);
        poseStack.scale(MAGNIFICATION_RATIO, MAGNIFICATION_RATIO, MAGNIFICATION_RATIO);
    }

    /**
     * 绘制扇形四边形
     */
    private void drawSegmentQuad(GuiGraphics guiGraphics, PoseStack poseStack,
                                 float innerRadius, float outerRadius,
                                 AngleRange innerRange, AngleRange outerRange,
                                 ColorData color) {
        Vec2 startInner = LibMathUtils.pointFromAngle(innerRadius, innerRange.startRadians());
        Vec2 endInner = LibMathUtils.pointFromAngle(innerRadius, innerRange.endRadians());
        Vec2 startOuter = LibMathUtils.pointFromAngle(outerRadius, outerRange.startRadians());
        Vec2 endOuter = LibMathUtils.pointFromAngle(outerRadius, outerRange.endRadians());

        drawQuad(guiGraphics, poseStack, startInner, endInner, endOuter, startOuter, color);
    }

    /**
     * 绘制扇形索引文本
     */
    private void drawSegmentIndex(GuiGraphics guiGraphics, PoseStack poseStack, Font font,
                                  int index, float outerRadius, float radiansPerSide,
                                  float startAngleOffset) {
        float angle = (index + 1) * radiansPerSide - radiansPerSide / 2 + startAngleOffset;
        Vec2 pos = LibMathUtils.pointFromAngle(outerRadius, angle);

        String text = String.valueOf(index);
        int lineWidth = font.width(text);
        int lineHeight = font.lineHeight;
        guiGraphics.drawString(font, text,
                (int) pos.x - lineWidth / 2,
                (int) pos.y - lineHeight / 2,
                -1);
    }

    /**
     * 绘制技能框
     */
    private void drawSkillBoxes(GuiGraphics guiGraphics, PoseStack poseStack, Font font,
                                float middleRadius, float radiansPerSkill,
                                DeltaTracker deltaTracker, ColorData highlightColor,
                                float startAngleOffset) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 10);

        for (int boxIndex = 0; boxIndex < boxList.size(); boxIndex++) {
            SoulSkillBox soulSkillBox = boxList.get(boxIndex);
            poseStack.pushPose();

            boolean isHighlighted = boxIndex == wheelSelection;
            Vec2 pos = calculateSkillBoxPosition(boxIndex, middleRadius, radiansPerSkill, startAngleOffset);
            poseStack.translate(pos.x - soulSkillBox.getWidth() / 2f, pos.y - soulSkillBox.getHeight() / 2f, 0);
            // 设置选中状态并应用高亮效果
            soulSkillBox.isSelect = isHighlighted;
            soulSkillBox.isFlame = isHighlighted;
            soulSkillBox.isActivate = isHighlighted;
            if (isHighlighted) {
                applyHighlightEffect(poseStack);
            }

            // 渲染技能框
            renderSkillBox(guiGraphics, soulSkillBox, deltaTracker);

//             绘制索引文本
//            drawSkillBoxIndex(guiGraphics, poseStack, font, boxIndex, pos);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    /**
     * 渲染当前选中的技能框
     */
    private void renderCurrentSelectedSkillBox(GuiGraphics guiGraphics, PoseStack poseStack,
                                               Vec2 mousePos, DeltaTracker deltaTracker) {
        poseStack.pushPose();
        int currentIndex = hudHolder.getCurrentIndex();
        if (boxList.size() > currentIndex) {
            SoulSkillBox soulSkillBox = boxList.get(currentIndex);
            if (soulSkillBox != null) {
                poseStack.translate(-soulSkillBox.getWidth() / 2f, -soulSkillBox.getHeight() / 2f, 0);
                soulSkillBox.renderWidget(guiGraphics,
                        0, 0,
                        (int) mousePos.x, (int) mousePos.y,
                        deltaTracker.getRealtimeDeltaTicks());
            }
        }
        poseStack.popPose();
    }

    @Override
    protected void drawTooltip(GuiGraphics guiGraphics, DeltaTracker deltaTracker, PoseStack poseStack, Font font) {
        if (wheelSelection < 0 || wheelSelection >= boxList.size()) {
            return;
        }

        SoulSkillBox soulSkillBox = boxList.get(wheelSelection);
        if (soulSkillBox == null) {
            return;
        }

        Tooltip tooltip = soulSkillBox.getTooltip();
        List<FormattedCharSequence> charSequence;
        if (tooltip != null) {
            charSequence = tooltip.toCharSequence(getMinecraft());
        } else {
            charSequence = SoulSkillBox.DEFAULT_CHAR_SEQUENCE;
        }

        guiGraphics.renderTooltip(font, charSequence, (int) getMouseX(), (int) getMouseY());
    }

    /**
     * 计算技能框位置
     */
    private Vec2 calculateSkillBoxPosition(int boxIndex, float radius, float radiansPerSkill,
                                           float startAngleOffset) {
        float angle = boxIndex * radiansPerSkill + radiansPerSkill / 2 + startAngleOffset;
        return LibMathUtils.pointFromAngle(radius, angle);
    }

    /**
     * 渲染技能框
     */
    private void renderSkillBox(GuiGraphics guiGraphics, SoulSkillBox soulSkillBox, DeltaTracker deltaTracker) {
        soulSkillBox.renderWidget(guiGraphics, 0, 0,
                (int) getMouseX(), (int) getMouseY(),
                deltaTracker.getRealtimeDeltaTicks());
    }

    /**
     * 绘制技能框索引文本
     */
    private void drawSkillBoxIndex(GuiGraphics guiGraphics, PoseStack poseStack, Font font,
                                   int boxIndex, Vec2 pos) {
        String text = String.valueOf(boxIndex);
        int lineWidth = font.width(text);
        int lineHeight = font.lineHeight;
        guiGraphics.drawString(font, text,
                (int) pos.x - lineWidth / 2,
                (int) pos.y - lineHeight / 2,
                -1);
    }

    /**
     * 计算轮盘选中的技能索引
     */
    private int calculateWheelSelection(Vec2 mousePos, Vec2 screenCenter, float innerRadius,
                                        float radiansPerSkill, int skillTotalNumber,
                                        float startAngleOffset) {
        // 如果鼠标在内圆内，设置为未选中状态
        if (LibMathUtils.isPointInCircle(mousePos, screenCenter, innerRadius)) {
            return -1;
        }

        // 获取鼠标相对于屏幕中心的角度 [0, 2π)
        float mouseAngle = LibMathUtils.getAngleRadians(mousePos, screenCenter);

        // 将鼠标角度转换到渲染坐标系
        float adjustedAngle = normalizeAngle(mouseAngle - startAngleOffset);

        // 计算对应的技能索引
        int selection = (int) (adjustedAngle / radiansPerSkill);

        // 确保索引在有效范围内
        return Mth.clamp(selection, 0, skillTotalNumber - 1);
    }

    /**
     * 归一化角度到 [0, 2π) 范围
     */
    private float normalizeAngle(float angle) {
        angle = angle % TWO_PI;
        return angle < 0 ? angle + TWO_PI : angle;
    }

    /**
     * 绘制四边形
     */
    private void drawQuad(GuiGraphics guiGraphics, PoseStack poseStack,
                          Vec2 p1, Vec2 p2, Vec2 p3, Vec2 p4, ColorData color) {
        final VertexConsumer vertexConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        final Matrix4f pose = poseStack.last().pose();

        vertexConsumer.addVertex(pose, p1.x, p1.y, 0)
                .setColor(color.red, color.green, color.blue, color.alpha);
        vertexConsumer.addVertex(pose, p2.x, p2.y, 0)
                .setColor(color.red, color.green, color.blue, color.alpha);
        vertexConsumer.addVertex(pose, p3.x, p3.y, 0)
                .setColor(color.red, color.green, color.blue, color.alpha);
        vertexConsumer.addVertex(pose, p4.x, p4.y, 0)
                .setColor(color.red, color.green, color.blue, color.alpha);
    }

    private @NotNull Vec2 getMousePos() {
        return new Vec2(getMouseX(), getMouseY());
    }

    private float getMouseY() {
        return (float) mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();
    }

    private float getMouseX() {
        return (float) mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
    }

    private @NotNull Vec2 getScreenCenter() {
        return new Vec2(getScreenWidth() * 0.5f, getScreenHeight() * 0.5f);
    }

    /**
     * 角度范围数据类
     */
    private record AngleRange(float startRadians, float endRadians) {}

    /**
     * 轮盘角度参数数据类
     */
    private record WheelAngleParams(
            int sidesNumber,          // 总边数
            int skillSidesNumber,     // 每个技能的边数
            float radiansPerSide,     // 每条边的弧度
            float radiansPerSkill,    // 每个技能的弧度
            float intervalRadians,    // 间隔弧度
            float radiusRatio         // 内外半径比例
    ) {}

    /**
     * 颜色数据类
     */
    private static class ColorData {
        final int red;
        final int green;
        final int blue;
        final int alpha;

        ColorData(Color color, float alphaFactor) {
            this.red = color.getRed();
            this.green = color.getGreen();
            this.blue = color.getBlue();
            this.alpha = (int) (alphaFactor * 255);
        }
    }
}
