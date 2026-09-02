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
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.NPCHumanoidGeoModel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.joml.Matrix4f;

import java.util.List;

/// 渲染城镇 NPC 及其由服务端同步的短时聊天气泡。
public class NPCEntityRenderer<T extends BaseNPC> extends GeoNormalRenderer<T> {
    private static final ResourceLocation CHAT_BUBBLE = Confluence.asResource("textures/gui/chat_bubble.png");
    private static final float ICON_SIZE = 0.4F;
    private static final int MAX_TEXT_WIDTH = 68;
    private static final int MAX_TEXT_LINES = 4;

    public NPCEntityRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, new NPCHumanoidGeoModel<>(path));
        addRenderLayer(new VanillaHumanoidRenderer.HeldItemLayer<>(this, "LeftArm", "RightArm"));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        NPCChat chat = entity.getCurrentChat();
        if (chat == null || entity.getChatDisplayTicks() <= 0 || entity.distanceToSqr(entityRenderDispatcher.camera.getPosition()) > 1024)
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
        // 贴图的气泡尾位于左下方，因此气泡主体需要右移，才能让尾部准确指向 NPC 头部。
        poseStack.translate(0.44F, 0.0F, 0.0F);
        renderQuad(CHAT_BUBBLE, poseStack, bufferSource, 1.25F, LightTexture.FULL_BRIGHT, -0.01F);
        poseStack.translate(0.0F, 0.17F, 0.0F);
        if (chat.text().isPresent()) {
            renderText(Component.translatable(chat.text().get()), poseStack, bufferSource, LightTexture.FULL_BRIGHT);
        } else if (chat.emoji().isPresent()) {
            ResourceLocation texture = ResourceLocation.tryParse(chat.emoji().get());
            if (texture != null)
                renderQuad(texture, poseStack, bufferSource, ICON_SIZE, LightTexture.FULL_BRIGHT, 0.0F);
        } else
            chat.item().ifPresent(item -> renderItem(entity, item, poseStack, bufferSource, LightTexture.FULL_BRIGHT));
        poseStack.popPose();
    }

    private static void renderText(Component content, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = font.split(content, MAX_TEXT_WIDTH);
        int lineCount = Math.min(lines.size(), MAX_TEXT_LINES);
        poseStack.scale(-0.014F, -0.014F, 0.014F);
        Matrix4f matrix = poseStack.last().pose();
        float firstY = -lineCount * font.lineHeight * 0.5F;
        for (int line = 0; line < lineCount; line++) {
            FormattedCharSequence text = lines.get(line);
            font.drawInBatch(text, -font.width(text) * 0.5F, firstY + line * font.lineHeight, 0xFF303030, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        }
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
        poseStack.scale(ICON_SIZE, ICON_SIZE, ICON_SIZE);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GUI, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float z, float u, float v) {
        consumer.vertex(pose.pose(), x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), 0.0F, 0.0F, 1.0F).endVertex();
    }

}
