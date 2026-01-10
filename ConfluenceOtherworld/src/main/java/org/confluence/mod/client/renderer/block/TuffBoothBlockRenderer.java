
package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.block.functional.TuffBoothBlock;
import org.joml.Vector3d;

import static org.confluence.lib.util.RenderUtils.drawCube;

public class TuffBoothBlockRenderer implements BlockEntityRenderer<TuffBoothBlock.TuffBoothBlockEntity> {
    public static void renderLineBox(PoseStack poseStack, VertexConsumer consumer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float red2, float green2, float blue2, boolean withoutUp) {
        PoseStack.Pose posestack$pose = poseStack.last();
        float f = (float) minX;
        float f1 = (float) minY;
        float f2 = (float) minZ;
        float f3 = (float) maxX;
        float f4 = (float) maxY;
        float f5 = (float) maxZ;
        if (withoutUp) {
            consumer.addVertex(posestack$pose, f, f1, f2).setColor(red, green2, blue2, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
            consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green2, blue2, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);

            consumer.addVertex(posestack$pose, f, f1, f2).setColor(red2, green2, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
            consumer.addVertex(posestack$pose, f, f1, f5).setColor(red2, green2, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);

            consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F);
            consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, -1.0F);

            consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
            consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
        }

        if (!withoutUp) {
            consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F);
            consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, -1.0F, 0.0F, 0.0F);

            consumer.addVertex(posestack$pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
            consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);

            consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);
            consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 1.0F, 0.0F, 0.0F);

            consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
            consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 0.0F, 1.0F);
        }
        consumer.addVertex(posestack$pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);

        consumer.addVertex(posestack$pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);

        consumer.addVertex(posestack$pose, f, f1, f2).setColor(red2, green, blue2, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f, f4, f2).setColor(red2, green, blue2, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);

        consumer.addVertex(posestack$pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
        consumer.addVertex(posestack$pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(posestack$pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void render(TuffBoothBlock.TuffBoothBlockEntity boothEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Player player = Minecraft.getInstance().player;

        int posX = boothEntity.getBlockPos().getX();
        int posY = boothEntity.getBlockPos().getY();
        int posZ = boothEntity.getBlockPos().getZ();

        Minecraft mc = Minecraft.getInstance();
        boolean showUI = !mc.options.hideGui;
        Vec3 exactHitLocation = null;
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            net.minecraft.world.phys.BlockHitResult blockHitResult = (net.minecraft.world.phys.BlockHitResult) mc.hitResult;

            BlockPos lookedAtPos = blockHitResult.getBlockPos();

            if (lookedAtPos.equals(boothEntity.getBlockPos())) {
                exactHitLocation = blockHitResult.getLocation().add(new Vec3(-posX, -posY, -posZ));
            }
        }

        int lineColor = 0xB0FFFFFF;
        float a = ((lineColor >> 24) & 0xFF) / 255F;
        float r = ((lineColor >> 16) & 0xFF) / 255F;
        float g = ((lineColor >> 8) & 0xFF) / 255F;
        float b = (lineColor & 0xFF) / 255F;

        if (showUI && (exactHitLocation != null) && !player.isSpectator()) {
            if (exactHitLocation.y > 0.5) {
                poseStack.pushPose();
                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.1865, 0.499, 0.1865, 0.8135, 0.8135, 0.8135, r, g, b, a);
                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), -0.001, 0.8115, -0.001, 1.001, 1.001, 1.001, r, g, b, a);
                poseStack.translate(0.5, 0, 0.5);
                poseStack.mulPose(Axis.YP.rotation((float) (Math.PI / 4)));
                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), -0.5635, 0.5615, -0.126, 0.5635, 0.8135, 0.126, r, g, b, a);
                poseStack.mulPose(Axis.YP.rotation((float) (Math.PI / 2)));
                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), -0.5635, 0.5615, -0.126, 0.5635, 0.8135, 0.126, r, g, b, a);
                poseStack.popPose();
            } else {
                poseStack.pushPose();
                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.1865, 0.1885, 0.1865, 0.8135, 0.501, 0.8135, r, g, b, a);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.0615, -0.001, 0.0615, 0.9385, 0.1885, 0.9385, r, g, b, a, r, g, b, true);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.0615, 0.1885, 0.0615, 0.251, 0.3135, 0.251, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.0615, 0.1885, 0.749, 0.251, 0.3135, 0.9385, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.749, 0.1885, 0.0615, 0.9385, 0.3135, 0.251, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.749, 0.1885, 0.749, 0.9385, 0.3135, 0.9385, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.0615, 0.1885, 0.249, 0.9385, 0.1885, 0.751, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.249, 0.1885, 0.0615, 0.751, 0.1885, 0.9385, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.749, 0.1885, 0.749, 0.8135, 0.3135, 0.8135, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.749, 0.1885, 0.1865, 0.8135, 0.3135, 0.251, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.1865, 0.1885, 0.749, 0.251, 0.3135, 0.8135, r, g, b, a, r, g, b, false);
                renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), 0.1865, 0.1885, 0.1865, 0.251, 0.3135, 0.251, r, g, b, a, r, g, b, false);
                poseStack.popPose();
            }
            if (player.getAbilities().mayBuild) {
                poseStack.pushPose();
                poseStack.translate(0.5, 0, 0.5);
                poseStack.mulPose(Axis.YP.rotation((float) (Math.PI / 4)));
                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), -0.5625, 0.5625, -0.125, 0.5625, 0.8125, 0.125, 0, 0, 0, 0.5F);
                poseStack.mulPose(Axis.YP.rotation((float) (Math.PI / 2)));
                LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), -0.5625, 0.5625, -0.125, 0.5625, 0.8125, 0.125, 0, 0, 0, 0.5F);
                poseStack.popPose();
            }
        }

        float scale = 1;

        ItemStack itemStack = boothEntity.getItemHandler().getStackInSlot(0);
        if (itemStack.isEmpty() && (exactHitLocation == null)) {
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
        float cubeAlpha = (float) Math.sin(angleInRadians * 1.5);

        poseStack.translate(0.5, 1.25 + yOffset, 0.5);

        float rotationAngle = (float) ((timeWithPartialTick % 360) / 60.0F * (float) Math.PI * 2.0F);

        poseStack.mulPose(Axis.YP.rotation(rotationAngle));

        poseStack.scale(scale, scale, scale);

        ItemStack handItems = null;
        if (player != null) {
            handItems = player.getItemInHand(InteractionHand.MAIN_HAND);
        }

        if ((handItems != null) && (!handItems.isEmpty()) && (exactHitLocation != null) && (exactHitLocation.y > 0.5)) {
            drawCube(poseStack, bufferSource, 0.52, (int) (r * 255), (int) (g * 255), (int) (b * 255), (int) ((cubeAlpha + 1F) / 8F * 255), new Vector3d(posX, posY, posZ), new Vector3d(0, 0.125, 0), true, Math.PI / 4, 0);
            LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), -0.26, -0.135, -0.26, 0.26, 0.385, 0.26, r, g, b, (cubeAlpha + 1F) / 4F);
        }

        boolean pop = true;
        if (!itemStack.isEmpty()) {
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
            pop = false;

            if (boothEntity.getBlockState().getValue(TuffBoothBlock.SHOW_NAME)) {
                poseStack.pushPose();

                poseStack.translate(0.5D, 1.83D + yOffset, 0.5D);

                Minecraft minecraft = Minecraft.getInstance();

                poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                float scale1 = 0.02F;
                poseStack.scale(scale1, -scale1, scale1);

                Font font = minecraft.font;
                Component text = itemStack.getDisplayName();
                String plainText = text.getString();
                String cleanText = plainText.replaceAll("^\\[|]$", "");
                Component cleanComponent = Component.literal(cleanText).withStyle(text.getStyle().withColor((TextColor) null));
                Component component = Component.literal(cleanText).withStyle(text.getStyle());
                ModRarity rarity = ModRarity.getRarity(itemStack);
                int textColor;
                boolean useTextColor = true;
                if (rarity != null) {
                    textColor = rarity.color();
                } else {
                    textColor = 0xFFFFFF;
                    useTextColor = false;
                }

                int textWidth = font.width(cleanComponent);
                float xOffset = -textWidth / 2.0F;
                int backColor = 0x40000000;
                float alpha = ((backColor >> 24) & 0xFF) / 255F;
                float red = ((backColor >> 16) & 0xFF) / 255F;
                float green = ((backColor >> 8) & 0xFF) / 255F;
                float blue = (backColor & 0xFF) / 255F;

                PoseStack.Pose pose = poseStack.last();
                VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());
                consumer.addVertex(pose, -xOffset + 1, -1, -0.1F)
                        .setColor(red, green, blue, alpha);
                consumer.addVertex(pose, xOffset - 1, -1, -0.1F)
                        .setColor(red, green, blue, alpha);
                consumer.addVertex(pose, xOffset - 1, 9, -0.1F)
                        .setColor(red, green, blue, alpha);
                consumer.addVertex(pose, -xOffset + 1, 9, -0.1F)
                        .setColor(red, green, blue, alpha);

                font.drawInBatch(
                        useTextColor ? cleanComponent : component,
                        xOffset,
                        0,
                        textColor,
                        false,
                        poseStack.last().pose(),
                        bufferSource,
                        Font.DisplayMode.NORMAL,
                        0,
                        255
                );

                poseStack.popPose();
            }
        }
        if (pop) poseStack.popPose();
    }
}
