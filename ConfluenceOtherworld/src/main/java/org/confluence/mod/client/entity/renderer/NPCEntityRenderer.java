package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.joml.Matrix4f;

/// 渲染城镇 NPC 及其由服务端同步的短时聊天气泡。
public class NPCEntityRenderer<T extends BaseNPC> extends GeoNormalRenderer<T> {
    public NPCEntityRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, path);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        NPCChat chat = entity.getCurrentChat();
        if (chat == null || entity.getChatDisplayTicks() <= 0 || entity.distanceToSqr(entityRenderDispatcher.camera.getPosition()) > 4096)
            return;

        MutableComponent content = Component.empty();
        chat.text().ifPresent(text -> content.append(Component.translatable(text)));
        chat.emoji().ifPresent(emoji -> content.append(content.getString().isEmpty() ? Component.literal(emoji) : Component.literal(" " + emoji)));
        chat.item().ifPresent(item -> content.append(content.getString().isEmpty() ? item.getHoverName() : Component.literal(" ").append(item.getHoverName())));
        if (content.getString().isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix = poseStack.last().pose();
        Font font = Minecraft.getInstance().font;
        float x = -font.width(content) / 2.0F;
        int background = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255) << 24;
        font.drawInBatch(content, x, 0, 0x20FFFFFF, false, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, background, packedLight);
        font.drawInBatch(content, x, 0, -1, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }
}
