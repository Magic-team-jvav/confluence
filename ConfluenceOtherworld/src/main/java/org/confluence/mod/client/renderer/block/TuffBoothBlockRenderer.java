// 在 TuffBoothBlockRenderer.java 文件中
package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.block.functional.TuffBoothBlock;
import org.confluence.mod.common.init.block.FunctionalBlocks;

import java.util.ArrayList;
import java.util.List;

public class TuffBoothBlockRenderer implements BlockEntityRenderer<TuffBoothBlock.TuffBoothBlockEntity> {


    public TuffBoothBlockRenderer(BlockEntityRendererProvider.Context context) {
        System.out.println("TuffBoothBlockRenderer initialized!");
    }

    @Override
    public void render(TuffBoothBlock.TuffBoothBlockEntity boothEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {



        float scale = 1;

        ItemStack itemStack = boothEntity.getItemHandler().getStackInSlot(0);
        if (itemStack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        long gameTime = 0;
        if (Minecraft.getInstance().level != null) {
            gameTime = Minecraft.getInstance().level.getGameTime();
        }
        double timeWithPartialTick = gameTime + partialTick;

        double angleInRadians = (timeWithPartialTick / 60.0) * 2.0 * Math.PI;

        float yOffset = (float) (Math.sin(angleInRadians) * 0.08);

        poseStack.translate(0.5, 1.25 + yOffset, 0.5);

        float rotationAngle = (float) ((timeWithPartialTick % 360) / 60.0F * (float) Math.PI * 2.0F);

        poseStack.mulPose(Axis.YP.rotation(rotationAngle));

        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getItemRenderer().render(
                itemStack,
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0)
        );

        poseStack.popPose();
        if (boothEntity.getBlockState().getValue(TuffBoothBlock.SHOW_NAME)) {
            poseStack.pushPose();

            poseStack.translate(0.5D, 1.8D, 0.5D);

            Minecraft minecraft = Minecraft.getInstance();

            poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            float scale1 = 0.015F;
            poseStack.scale(scale1, -scale1, scale1);

            Font font = minecraft.font;
            Component text = itemStack.getDisplayName();
            int textColor = 0xFFFFFF;

            int textWidth = font.width(text);
            float xOffset = -textWidth / 2.0F;

            font.drawInBatch(
                    text,
                    xOffset,
                    0,
                    textColor,
                    false,
                    poseStack.last().pose(),
                    bufferSource,
                    Font.DisplayMode.NORMAL,
                    0,
                    packedLight
            );

            poseStack.popPose(); // 结束操作，恢复PoseStack
        }
    }
}
