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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.block.functional.TuffBoothBlock;
import org.joml.Vector3d;

import static org.confluence.lib.util.RenderUtils.drawCube;

public class TuffBoothBlockRenderer implements BlockEntityRenderer<TuffBoothBlock.TuffBoothBlockEntity> {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final Font FONT = MC.font;
    private static final TagKey<Item> carpetTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "wool_carpets"));

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
        Player player = MC.player;
        if (player == null) return;

        final float alpha = 0xB0 / 255F;
        final float white = 1.0F;

        ItemStack playHand = player.getMainHandItem();

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());

        boolean showUI = !MC.options.hideGui;
        Vec3 exactHitLocation = null;

        if (MC.hitResult != null && MC.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos lookedAtPos = ((net.minecraft.world.phys.BlockHitResult) MC.hitResult).getBlockPos();
            if (lookedAtPos.equals(boothEntity.getBlockPos())) {
                exactHitLocation = MC.hitResult.getLocation().subtract(boothEntity.getBlockPos().getX(), boothEntity.getBlockPos().getY(), boothEntity.getBlockPos().getZ());
            }
        }

        if (showUI && exactHitLocation != null && !player.isSpectator()) {
            poseStack.pushPose();

            if (exactHitLocation.y > 0.5 && !playHand.isEmpty()) {
                LevelRenderer.renderLineBox(poseStack, buffer, 0.1865, 0.499, 0.1865, 0.8135, 0.8135, 0.8135, white, white, white, alpha);
                LevelRenderer.renderLineBox(poseStack, buffer, -0.001, 0.8115, -0.001, 1.001, 1.001, 1.001, white, white, white, alpha);
                poseStack.translate(0.5, 0, 0.5);
                poseStack.mulPose(Axis.YP.rotation(Mth.PI * 0.25F));
                LevelRenderer.renderLineBox(poseStack, buffer, -0.5635, 0.5615, -0.126, 0.5635, 0.8135, 0.126, white, white, white, alpha);
                poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI));
                LevelRenderer.renderLineBox(poseStack, buffer, -0.5635, 0.5615, -0.126, 0.5635, 0.8135, 0.126, white, white, white, alpha);
            } else if (exactHitLocation.y <= 0.5 && (playHand.is(carpetTag) || playHand.is(Items.NAME_TAG))) {
                LevelRenderer.renderLineBox(poseStack, buffer, 0.1865, 0.1885, 0.1865, 0.8135, 0.501, 0.8135, white, white, white, alpha, white, white, white);
                renderLineBox(poseStack, buffer, 0.0615, -0.001, 0.0615, 0.9385, 0.1885, 0.9385, white, white, white, alpha, white, white, white, true);
                renderLineBox(poseStack, buffer, 0.0615, 0.1885, 0.0615, 0.251, 0.3135, 0.251, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.0615, 0.1885, 0.749, 0.251, 0.3135, 0.9385, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.749, 0.1885, 0.0615, 0.9385, 0.3135, 0.251, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.749, 0.1885, 0.749, 0.9385, 0.3135, 0.9385, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.0615, 0.1885, 0.249, 0.9385, 0.1885, 0.751, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.249, 0.1885, 0.0615, 0.751, 0.1885, 0.9385, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.749, 0.1885, 0.749, 0.8135, 0.3135, 0.8135, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.749, 0.1885, 0.1865, 0.8135, 0.3135, 0.251, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.1865, 0.1885, 0.749, 0.251, 0.3135, 0.8135, white, white, white, alpha, white, white, white, false);
                renderLineBox(poseStack, buffer, 0.1865, 0.1885, 0.1865, 0.251, 0.3135, 0.251, white, white, white, alpha, white, white, white, false);
                poseStack.translate(0.5, 0, 0.5);
                poseStack.mulPose(Axis.YP.rotation(Mth.PI * 0.25F));
            } else {
                poseStack.translate(0.5, 0, 0.5);
                poseStack.mulPose(Axis.YP.rotation(Mth.PI * 0.25F));
            }
            if (player.getAbilities().mayBuild) {
                LevelRenderer.renderLineBox(poseStack, buffer, -0.5625, 0.5625, -0.125, 0.5625, 0.8125, 0.125, 0, 0, 0, 0.5F);
                poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI));
                LevelRenderer.renderLineBox(poseStack, buffer, -0.5625, 0.5625, -0.125, 0.5625, 0.8125, 0.125, 0, 0, 0, 0.5F);
            }

            poseStack.popPose();
        }

        ItemStack itemStack = boothEntity.getItemHandler().getStackInSlot(0);
        if (itemStack.isEmpty() && exactHitLocation == null) {
            return;
        }

        poseStack.pushPose();
        long gameTime = MC.level != null ? MC.level.getGameTime() : 0;
        double timeWithPartialTick = gameTime + partialTick;
        double angleInRadians = (timeWithPartialTick / 60.0) * Math.TAU;

        float yOffset = (float) (Math.sin(angleInRadians) * 0.08);
        float cubeAlpha = (float) Math.sin(angleInRadians * 1.5);

        poseStack.translate(0.5F, 1.25F + yOffset, 0.5F);
        poseStack.mulPose(Axis.YP.rotation((float) ((timeWithPartialTick % 360) / 60.0F * Mth.TWO_PI)));

        if (!playHand.isEmpty() && exactHitLocation != null && exactHitLocation.y > 0.5) {
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());
            drawCube(poseStack, 0.52, 255, 255, 255, (int) ((cubeAlpha + 1F) / 8F * 255),
                    new Vector3d(boothEntity.getBlockPos().getX(), boothEntity.getBlockPos().getY(), boothEntity.getBlockPos().getZ()),
                    new Vector3d(0, 0.125, 0), true, Math.PI * 0.25F, 0, consumer);

            LevelRenderer.renderLineBox(poseStack, buffer, -0.26, -0.135, -0.26, 0.26, 0.385, 0.26, white, white, white, (cubeAlpha + 1F) / 4F);
        }

        if (!itemStack.isEmpty()) {
            MC.getItemRenderer().render(
                    itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight, packedOverlay,
                    MC.getItemRenderer().getModel(itemStack, null, null, 0)
            );
            poseStack.popPose();

            if (boothEntity.getBlockState().getValue(TuffBoothBlock.SHOW_NAME)) {
                poseStack.pushPose();
                poseStack.translate(0.5D, 1.83D + yOffset, 0.5D);
                poseStack.mulPose(MC.getEntityRenderDispatcher().cameraOrientation());
                float scale1 = 0.02F;
                poseStack.scale(scale1, -scale1, scale1);

                Component text = itemStack.getDisplayName();
                String rawString = text.getString();
                String cleanText = rawString;
                int length = rawString.length();
                if (length >= 2) {
                    if (rawString.charAt(0) == '[' && rawString.charAt(length - 1) == ']') {
                        cleanText = rawString.substring(1, length - 1);
                    }
                }
                ModRarity rarity = ModRarity.getRarity(itemStack);
                int textColor = rarity != null ? rarity.color() : 0xFFFFFF;
                boolean useTextColor = rarity != null;

                Component displayComponent = useTextColor ? Component.literal(cleanText) : text;

                int textWidth = FONT.width(displayComponent);
                float xOffset = -textWidth / 2.0F;

                VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());
                PoseStack.Pose pose = poseStack.last();
                float bgAlpha = 0x40 / 255F;
                consumer.addVertex(pose, -xOffset + 1, -1, -0.1F).setColor(0, 0, 0, bgAlpha);
                consumer.addVertex(pose, xOffset - 1, -1, -0.1F).setColor(0, 0, 0, bgAlpha);
                consumer.addVertex(pose, xOffset - 1, 9, -0.1F).setColor(0, 0, 0, bgAlpha);
                consumer.addVertex(pose, -xOffset + 1, 9, -0.1F).setColor(0, 0, 0, bgAlpha);

                FONT.drawInBatch(displayComponent, xOffset, 0, textColor, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 255);
                poseStack.popPose();
            }
        } else {
            poseStack.popPose();
        }
    }
}
