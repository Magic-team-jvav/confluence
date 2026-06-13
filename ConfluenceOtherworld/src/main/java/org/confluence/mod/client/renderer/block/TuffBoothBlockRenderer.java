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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.block.functional.TuffBoothBlock;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;

import static org.confluence.lib.util.LibRenderUtils.drawCube;

public class TuffBoothBlockRenderer implements BlockEntityRenderer<TuffBoothBlock.TuffBoothBlockEntity> {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final TagKey<Item> CARPET_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "wool_carpets"));

    @Override
    public void render(TuffBoothBlock.TuffBoothBlockEntity boothEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Player player = MC.player;
        if (player == null || boothEntity.getLevel() == null) return;

        BlockPos pos = boothEntity.getBlockPos();
        Vector3f posVct = new Vector3f(pos.getX(), pos.getY(), pos.getZ());
        VertexConsumer quadBuffer = bufferSource.getBuffer(RenderType.debugQuads());
        //drawCube(poseStack, 1, 255, 255, 255, 255, posVct, new Vector3f(0, 0, 0), false, 0, 0, quadBuffer);

        ItemStack playHand = player.getMainHandItem();
        ItemStack displayStack = boothEntity.getItemHandler().getStackInSlot(0);

        Vec3 exactHit = null;
        HitResult hitResult = MC.hitResult;
        if (hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
            if (hit.getBlockPos().equals(boothEntity.getBlockPos())) {
                exactHit = hit.getLocation().subtract(Vec3.atLowerCornerOf(boothEntity.getBlockPos()));
            }
        }

        if (!MC.options.hideGui && exactHit != null && !player.isSpectator()) {
            renderHighlighter(poseStack, bufferSource, player, playHand, exactHit);
        }

        if (!displayStack.isEmpty() || exactHit != null) {
            renderContent(boothEntity, displayStack, exactHit, playHand, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private void renderHighlighter(PoseStack poseStack, MultiBufferSource buffer, Player player, ItemStack hand, Vec3 hit) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
        float alpha = 0.7F;
        float w = 1.0F;

        poseStack.pushPose();
        if (hit.y > 0.5 && !hand.isEmpty()) {
            LevelRenderer.renderLineBox(poseStack, consumer, 0.1865, 0.499, 0.1865, 0.8135, 0.8135, 0.8135, w, w, w, alpha);
            LevelRenderer.renderLineBox(poseStack, consumer, -0.001, 0.8115, -0.001, 1.001, 1.001, 1.001, w, w, w, alpha);

            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotation(Mth.PI * 0.25F));
            LevelRenderer.renderLineBox(poseStack, consumer, -0.5635, 0.5615, -0.126, 0.5635, 0.8135, 0.126, w, w, w, alpha);
            poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI));
            LevelRenderer.renderLineBox(poseStack, consumer, -0.5635, 0.5615, -0.126, 0.5635, 0.8135, 0.126, w, w, w, alpha);
        } else if (hit.y <= 0.5 && (hand.is(CARPET_TAG) || hand.is(Items.NAME_TAG))) {
            LevelRenderer.renderLineBox(poseStack, consumer, 0.1865, 0.1885, 0.1865, 0.8135, 0.501, 0.8135, w, w, w, alpha);
            renderLineBox(poseStack, consumer, 0.0615, -0.001, 0.0615, 0.9385, 0.1885, 0.9385, w, w, w, alpha, w, w, w, true);
            renderLineBox(poseStack, consumer, 0.0615, 0.1885, 0.0615, 0.251, 0.3135, 0.251, w, w, w, alpha, w, w, w, false);
            renderLineBox(poseStack, consumer, 0.0615, 0.1885, 0.749, 0.251, 0.3135, 0.9385, w, w, w, alpha, w, w, w, false);
            renderLineBox(poseStack, consumer, 0.749, 0.1885, 0.0615, 0.9385, 0.3135, 0.251, w, w, w, alpha, w, w, w, false);
            renderLineBox(poseStack, consumer, 0.749, 0.1885, 0.749, 0.9385, 0.3135, 0.9385, w, w, w, alpha, w, w, w, false);
        }

        if (player.getAbilities().mayBuild) {
            poseStack.setIdentity();
            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotation(Mth.PI * 0.25F));
            LevelRenderer.renderLineBox(poseStack, consumer, -0.5625, 0.5625, -0.125, 0.5625, 0.8125, 0.125, 0, 0, 0, 0.5F);
            poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI));
            LevelRenderer.renderLineBox(poseStack, consumer, -0.5625, 0.5625, -0.125, 0.5625, 0.8125, 0.125, 0, 0, 0, 0.5F);
        }
        poseStack.popPose();
    }

    private void renderContent(TuffBoothBlock.TuffBoothBlockEntity booth, ItemStack stack, @Nullable Vec3 hit, ItemStack hand, float partial, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        int time = 0;
        if (booth.getLevel() != null) {
            time = (int) (booth.getLevel().getGameTime() % 24000);
        }
        float renderTime = time + partial;
        float ySwing = (float) (Mth.sin(renderTime / 60.0F * Mth.TWO_PI) * 0.08);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.25 + ySwing, 0.5);
        if (!stack.isEmpty() && booth.getBlockState().getValue(TuffBoothBlock.SHOW_NAME)) {
            renderLabel(poseStack, buffer, stack);
        }
        poseStack.mulPose(Axis.YP.rotation((renderTime % 360) / 60.0F * Mth.TWO_PI));

        if (hit != null && !hand.isEmpty() && hit.y > 0.5) {
            float cubeAlpha = (float) Math.sin((renderTime / 60.0 * Mth.TWO_PI) * 1.5);
            VertexConsumer quadBuffer = buffer.getBuffer(RenderType.debugQuads());

            drawCube(poseStack, 0.52F, 255, 255, 255, (int) ((cubeAlpha + 1F) / 8F * 255),
                    new Vector3f(0, 0, 0), new Vector3f(0, 0.125F, 0), true, Mth.PI * 0.25F, 0, quadBuffer);

            LevelRenderer.renderLineBox(poseStack, buffer.getBuffer(RenderType.lines()), -0.26, -0.135, -0.26, 0.26, 0.385, 0.26, 1, 1, 1, (cubeAlpha + 1F) / 4F);
        }

        if (!stack.isEmpty()) {
            MC.getItemRenderer().render(stack, ItemDisplayContext.GROUND, false, poseStack, buffer, light, overlay,
                    MC.getItemRenderer().getModel(stack, booth.getLevel(), null, 0));
        }
        poseStack.popPose();
    }

    private void renderLabel(PoseStack poseStack, MultiBufferSource buffer, ItemStack stack) {
        poseStack.pushPose();
        poseStack.translate(0, 0.6, 0);
        poseStack.mulPose(MC.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.02F, -0.02F, 0.02F);

        Component name = stack.getDisplayName();
        String str = name.getString();
        if (str.startsWith("[") && str.endsWith("]")) str = str.substring(1, str.length() - 1);

        ModRarity rarity = ModRarity.getRarity(stack);
        int color = rarity != null ? rarity.color() : 0xFFFFFF;
        Component finalName = rarity != null ? Component.literal(str) : name;

        float xOff = -MC.font.width(finalName) / 2.0F;

        VertexConsumer bg = buffer.getBuffer(RenderType.debugQuads());
        Matrix4f pose = poseStack.last().pose();
        bg.vertex(pose, -xOff + 1, -1, -0.1F).color(0, 0, 0, 0.25F).endVertex();
        bg.vertex(pose, xOff - 1, -1, -0.1F).color(0, 0, 0, 0.25F).endVertex();
        bg.vertex(pose, xOff - 1, 9, -0.1F).color(0, 0, 0, 0.25F).endVertex();
        bg.vertex(pose, -xOff + 1, 9, -0.1F).color(0, 0, 0, 0.25F).endVertex();

        MC.font.drawInBatch(finalName, xOff, 0, color, false, pose, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        poseStack.popPose();
    }

    public static void renderLineBox(PoseStack poseStack, VertexConsumer consumer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a, float r2, float g2, float b2, boolean withoutUp) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float x1 = (float) minX, y1 = (float) minY, z1 = (float) minZ;
        float x2 = (float) maxX, y2 = (float) maxY, z2 = (float) maxZ;

        if (withoutUp) {
            consumer.vertex(pose, x1, y1, z1).color(r, g2, b2, a).normal(normal, 1, 0, 0).endVertex();
            consumer.vertex(pose, x2, y1, z1).color(r, g2, b2, a).normal(normal, 1, 0, 0).endVertex();
            consumer.vertex(pose, x1, y1, z1).color(r2, g2, b, a).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(pose, x1, y1, z2).color(r2, g2, b, a).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(pose, x2, y1, z2).color(r, g, b, a).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(pose, x2, y1, z1).color(r, g, b, a).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(pose, x1, y1, z2).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
            consumer.vertex(pose, x2, y1, z2).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
        } else {
            consumer.vertex(pose, x2, y2, z1).color(r, g, b, a).normal(normal, -1, 0, 0).endVertex();
            consumer.vertex(pose, x1, y2, z1).color(r, g, b, a).normal(normal, -1, 0, 0).endVertex();
            consumer.vertex(pose, x1, y2, z1).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(pose, x1, y2, z2).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(pose, x1, y2, z2).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
            consumer.vertex(pose, x2, y2, z2).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
            consumer.vertex(pose, x2, y2, z1).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(pose, x2, y2, z2).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
        }
        consumer.vertex(pose, x2, y1, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(pose, x2, y2, z2).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(pose, x1, y2, z2).color(r, g, b, a).normal(normal, 0, -1, 0).endVertex();
        consumer.vertex(pose, x1, y1, z2).color(r, g, b, a).normal(normal, 0, -1, 0).endVertex();
        consumer.vertex(pose, x1, y1, z1).color(r2, g, b2, a).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(pose, x1, y2, z1).color(r2, g, b2, a).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(pose, x2, y1, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(pose, x2, y2, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
    }
}
