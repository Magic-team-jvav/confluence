package org.confluence.mod.client.gui.hud.soul.quick_skill;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouletteWheelSmallHud extends BasicSoulQuickSkillHud {
    public static final ResourceLocation TEXTURE = Confluence.asResource("hud/soul_quick_skill_hud/roulette_wheel_small");
    public static final float INTERVAL = 360 / 8f;
    public static final float INTERVAL_RADIANS = (float) Math.toRadians(INTERVAL);
    private float time;
    private float angle;
    private float renderAngle;
    private int targetIndex;
    private HumanoidArm humanoidArm = HumanoidArm.RIGHT;

    private final List<SoulSkillBox> boxes;

    public RouletteWheelSmallHud() {
        super();
        var list = new ArrayList<SoulSkillBox>();
        for (int i = 0; i < 8; i++) {
            SoulSkillBox box = new SoulSkillBox(SoulSkillStack.EMPTY);
            box.isSkill = true;
            box.isBox = false;
            list.add(box);
        }
        boxes = Collections.unmodifiableList(list);
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float realtimeDeltaTicks = deltaTracker.getRealtimeDeltaTicks();
//        if (!active && time <= 0) {
//            return;
//        }
//        if (active) {
//            if (time * 0.1f < 1) {
//                time += tick;
//            }
//        } else {
//            time -= tick;
//        }
        renderAngle = Mth.rotLerp(realtimeDeltaTicks, angle, targetIndex * INTERVAL);
        angle = renderAngle;
        super.render(guiGraphics, deltaTracker);
    }

    @Override
    protected void renderDrawLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
//        if (!isType()) {
//            return;
//        }
        CurrentSelectedSkillHud instance = SoulQuickSkillHudHolder.CURRENT_SELECTED_SKILL_HUD_INSTANCE;
        PoseStack poseStack = guiGraphics.pose();
        float realtimeDeltaTicks = deltaTracker.getRealtimeDeltaTicks();
        poseStack.pushPose();
        {
            float x = instance.getX() + instance.box.getWidth() / 2f;
            float y = instance.getY() + instance.box.getHeight() / 2f;
            poseStack.translate(x, y, 0);
            poseStack.pushPose();
            {
//                float degrees = LibMathUtils.lerpDiscrete(realtimeDeltaTicks, angleO, angle);
//                float degrees = Mth.lerp(realtimeDeltaTicks, angleO, angle);
                poseStack.mulPose(Axis.ZP.rotationDegrees(renderAngle));
                guiGraphics.blitSprite(TEXTURE, -128 / 2, -128 / 2, 128, 128);
                poseStack.translate(0, 0, 1);
                poseStack.pushPose();
                {
                    int size = boxes.size();
                    float startAngleOffset = RouletteWheelBigHud.START_ANGLE_OFFSET;
                    int skillTotalNumber = hudHolder.getSkillTotalNumber();
                    int currentIndex = hudHolder.getCurrentIndex();
                    int halfDisplay = size / 2; // 4
                    
                    for (int i = 0; i < size; i++) {
                        SoulSkillBox soulSkillBox = boxes.get(i);
                        Vec2 pos = LibMathUtils.pointFromAngle(33, i * INTERVAL_RADIANS + startAngleOffset);
                        
                        // 计算实际技能索引
                        int offset = i - halfDisplay;
                        int actualIndex = currentIndex + offset;
                        actualIndex = actualIndex % skillTotalNumber;
                        if (actualIndex < 0) {
                            actualIndex += skillTotalNumber;
                        }
                        
                        poseStack.pushPose();
                        poseStack.translate((int) pos.x, (int) pos.y, 0);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(-renderAngle));
                        soulSkillBox.renderWidget(guiGraphics, -soulSkillBox.getWidth() / 2, -soulSkillBox.getHeight() / 2, 0, 0, realtimeDeltaTicks);
                        drawSkillBoxIndex(guiGraphics, poseStack, actualIndex);
                        poseStack.popPose();
                    }
                }
                poseStack.popPose();
            }
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0, 0, 2);
            guiGraphics.blitSprite(SoulSkillBox.BOX, -32 / 2, -32 / 2 - 33 - 2, 32, 32);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private void drawSkillBoxIndex(GuiGraphics guiGraphics, PoseStack poseStack, int i) {
        poseStack.pushPose();
        String text = String.valueOf(i);
        int lineWidth = font.width(text);
        int lineHeight = font.lineHeight;
        poseStack.translate(0, 0, 1);
        guiGraphics.drawString(font, text, lineWidth / 2 - 16 / 2, lineHeight / 2 - 16 / 2, -1);
        poseStack.popPose();
    }

    public int adjustTarget(int index) {
        int skillTotalNumber = hudHolder.getSkillTotalNumber();
        
        if (skillTotalNumber <= 0) {
            return hudHolder.getCurrentIndex();
        }

        // 获取当前索引并加上偏移量
        int currentIndex = hudHolder.getCurrentIndex();
        int newIndex = currentIndex + index;
        
        // 处理循环：确保索引在有效范围内
        newIndex = newIndex % skillTotalNumber;
        if (newIndex < 0) {
            newIndex += skillTotalNumber;
        }
        
        // 更新targetIndex用于角度计算
        targetIndex = newIndex;

        return newIndex;
    }

    @Override
    public void open() {
        if (!isType()) {
            return;
        }
        active = true;
        // 初始化targetIndex为当前索引，确保角度正确
        targetIndex = hudHolder.getCurrentIndex();
        update();
    }

    @Override
    public void close() {
        active = false;
        if (!isType()) {
            return;
        }
//        if (wheelSelection >= 0) {
//            ClientMagicData.getSpellSelectionManager().makeSelection(wheelSelection);
//        }
    }

    @Override
    public void update() {
        List<SoulSkillStack> skillStackList = hudHolder.getSkillStackList();
        int skillTotalNumber = hudHolder.getSkillTotalNumber();
        int currentIndex = hudHolder.getCurrentIndex();
        
        if (skillTotalNumber <= 0 || skillStackList.isEmpty()) {
            // 如果没有技能，清空所有框
            for (SoulSkillBox box : boxes) {
                box.setSkill(SoulSkillStack.EMPTY);
            }
            return;
        }
        
        // 计算8个位置对应的实际技能索引
        // 布局：[当前-4, 当前-3, 当前-2, 当前-1, 当前, 当前+1, 当前+2, 当前+3]
        int displayCount = boxes.size(); // 8个
        int halfDisplay = displayCount / 2; // 4
        
        for (int i = 0; i < displayCount; i++) {
            // 计算相对位置：从 -4 到 +3
            int offset = i - halfDisplay;
            // 计算实际索引，处理循环
            int actualIndex = currentIndex + offset;
            
            // 处理负数循环：如果 currentIndex=0, offset=-1，应该指向最后一个技能
            actualIndex = actualIndex % skillTotalNumber;
            if (actualIndex < 0) {
                actualIndex += skillTotalNumber;
            }
            
            // 设置对应位置的技能
            SoulSkillStack skill = skillStackList.get(actualIndex);
            boxes.get(i).setSkill(skill != null ? skill : SoulSkillStack.EMPTY);
        }
    }

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
