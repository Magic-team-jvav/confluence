package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.LucyTheAxeHandler;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.mesdag.portlib.client.gui.components.PortSprite;

import java.util.Queue;
import java.util.function.Consumer;

public class LucyTheAxeDialogRenderer {
    private static final PortSprite background = new PortSprite(Confluence.asResource("dialog_background"), 48, 24);
    private static final PortSprite tail = new PortSprite(Confluence.asResource("dialog_tail"), 8, 8);
    private static final Quaternionf quaternion = new Quaternionf();
    private static final Matrix4f matrix = new Matrix4f();
    public static final int COLOR = 0xFFAA0000;
    public static Component dialog;
    private static Consumer<GuiGraphics> delayed;

    public static void renderInGui(Minecraft minecraft, PoseStack poseStack) {
        int textW = minecraft.font.width(dialog);
        float itemX = poseStack.last().pose().m30();
        float itemY = poseStack.last().pose().m31();
        float x = itemX - textW * 0.5F;
        float y = itemY - 48;
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        FormattedCharSequence text = dialog.getVisualOrderText();

        delayed = guiGraphics -> {
            int y1 = (int) y - 5;
            guiGraphics.blitSprite(background, (int) x - 4, y1, textW + 8, minecraft.font.lineHeight + 9);
            guiGraphics.blitSprite(tail, (int) itemX + 8, y1 + 17, 8, 8);

            minecraft.font.renderText(
                    text, x, y, COLOR, false, matrix,
                    bufferSource, Font.DisplayMode.SEE_THROUGH,
                    0, 0xF000F0
            );
        };
    }

    public static void reset() {
        dialog = null;
        delayed = null;
    }

    public static void renderDelayed(GuiGraphics graphics) {
        if (delayed != null) {
            delayed.accept(graphics);
            delayed = null;
        }
    }

    public static void renderInWorld(Minecraft minecraft, PoseStack poseStack) {
        Queue<LucyTheAxeHandler.Stack> stacks = LucyTheAxeHandler.getStacks();
        if (stacks.isEmpty()) return;
        poseStack.pushPose();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        float x = (float) camera.getPosition().x;
        float y = (float) camera.getPosition().y;
        float z = (float) camera.getPosition().z;
        Quaternionf rotation = camera.rotation();
        quaternion.set(0, rotation.y, 0, rotation.w).rotateY(Mth.PI);
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        float scale = 20;
        for (LucyTheAxeHandler.Stack stack : stacks) {
            poseStack.pushPose();
            poseStack.translate(stack.x - x, stack.y - y, stack.z - z);
            poseStack.mulPose(quaternion);
            poseStack.scale(-1 / scale, -1 / scale, -1 / scale);
            minecraft.font.renderText(
                    stack.dialog.getVisualOrderText(),
                    minecraft.font.width(stack.dialog) * -0.5F, 0,
                    COLOR, false, poseStack.last().pose(),
                    bufferSource, Font.DisplayMode.NORMAL,
                    0, 0xF000F0
            );
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
