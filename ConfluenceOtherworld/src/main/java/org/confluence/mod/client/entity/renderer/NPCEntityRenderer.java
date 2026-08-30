package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.NPCHumanoidGeoModel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.joml.Matrix4f;

/// 渲染城镇 NPC 及其由服务端同步的短时聊天气泡。
public class NPCEntityRenderer<T extends BaseNPC> extends GeoNormalRenderer<T> {
    private static final ResourceLocation CHAT_BUBBLE = Confluence.asResource("textures/gui/chat_bubble.png");

    public NPCEntityRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, new NPCHumanoidGeoModel<>(path));
        addRenderLayer(new VanillaHumanoidRenderer.HeldItemLayer<>(this, "LeftArm", "RightArm"));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        NPCChat chat = entity.getCurrentChat();
        if (chat == null || entity.getChatDisplayTicks() <= 0 || entity.distanceToSqr(entityRenderDispatcher.camera.getPosition()) > 4096)
            return;

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() + 0.7, 0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        float remaining = entity.getChatDisplayTicks() - partialTick;
        float appearance = Math.min(1.0F, Math.min(100.0F - remaining, remaining) / 10.0F);
        if (appearance <= 0.0F) {
            poseStack.popPose();
            return;
        }
        poseStack.scale(appearance, appearance, appearance);
        renderQuad(CHAT_BUBBLE, poseStack, bufferSource, 1.25F, LightTexture.FULL_BRIGHT, -0.01F);
        if (chat.text().isPresent()) {
            renderText(Component.translatable(chat.text().get()), poseStack, bufferSource, LightTexture.FULL_BRIGHT);
        } else if (chat.emoji().isPresent()) {
            ResourceLocation texture = ResourceLocation.tryParse(chat.emoji().get());
            if (texture != null)
                renderQuad(texture, poseStack, bufferSource, 0.55F, LightTexture.FULL_BRIGHT, 0.0F);
        } else
            chat.item().ifPresent(item -> renderItem(entity, item, poseStack, bufferSource, LightTexture.FULL_BRIGHT));
        poseStack.popPose();
    }

    private static void renderText(Component content, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(-0.018F, -0.018F, 0.018F);
        Matrix4f matrix = poseStack.last().pose();
        Font font = Minecraft.getInstance().font;
        float x = -font.width(content) / 2.0F;
        int background = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255) << 24;
        font.drawInBatch(content, x, -font.lineHeight * 0.5F, 0xFF303030, false, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, background, packedLight);
        font.drawInBatch(content, x, -font.lineHeight * 0.5F, 0xFF303030, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
    }

    private static void renderQuad(ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferSource, float size, int packedLight, float z) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        float half = size * 0.5F;
        vertex(consumer, poseStack.last(), packedLight, -half, -half, z, 0.0F, 1.0F);
        vertex(consumer, poseStack.last(), packedLight, half, -half, z, 1.0F, 1.0F);
        vertex(consumer, poseStack.last(), packedLight, half, half, z, 1.0F, 0.0F);
        vertex(consumer, poseStack.last(), packedLight, -half, half, z, 0.0F, 0.0F);
    }

    private static void renderItem(BaseNPC entity, ItemStack item, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(0.5F, 0.5F, 0.5F);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GUI, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float z, float u, float v) {
        consumer.vertex(pose.pose(), x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
    }

}
