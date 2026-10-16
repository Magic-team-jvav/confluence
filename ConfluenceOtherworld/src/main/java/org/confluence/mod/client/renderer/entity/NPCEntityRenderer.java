package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import org.confluence.mod.client.gui.container.npc_screen.NPCReforgeScreen;
import org.confluence.mod.client.model.entity.NPCHumanoidGeoModel;
import org.confluence.mod.common.entity.npc.AnglerNPC;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/// 渲染城镇 NPC 及其由服务端同步的短时聊天气泡。
public class NPCEntityRenderer<T extends BaseNPC> extends GeoNormalRenderer<T> {
    private static final ResourceLocation CHAT_BUBBLE = Confluence.asResource("textures/gui/chat_bubble.png");
    private static final ResourceLocation REFORGE_HAMMER_MODEL = Confluence.asResource("geo/gui/reforge_hammer.geo.json");
    private static final ResourceLocation REFORGE_HAMMER_TEXTURE = Confluence.asResource("textures/gui/reforge_hammer.png");
    private static final float HAMMER_GRIP_Y = 3.0F / 16.0F;
    private static final float HAMMER_SCALE = 0.85F;
    private static final float ICON_SIZE = 0.4F;
    private static final int MAX_TEXT_WIDTH = 68;
    private static final int MAX_TEXT_LINES = 4;

    public NPCEntityRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, new NPCHumanoidGeoModel<>(path));
        addRenderLayer(new VanillaHumanoidRenderer.HeldItemLayer<>(this, "LeftArm", "RightArm"));
        addRenderLayer(new GeoRenderLayer<>(this) {
            @Override
            public void renderForBone(PoseStack poseStack, T entity, GeoBone bone, RenderType renderType,
                                      MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                                      int packedLight, int packedOverlay) {
                if (!bone.getName().equals("RightArm")
                        || !(Minecraft.getInstance().screen instanceof NPCReforgeScreen screen)
                        || !screen.isTinkererPreview(entity)) return;
                BakedGeoModel hammerModel = GeckoLibCache.getBakedModels().get(REFORGE_HAMMER_MODEL);
                if (hammerModel == null) return;
                GeoBone hammer = hammerModel.getBone("reforge_hammer").orElse(null);
                if (hammer == null) return;
                RenderType hammerType = RenderType.entityCutoutNoCull(REFORGE_HAMMER_TEXTURE);
                poseStack.pushPose();
                try {
                    RenderUtil.translateToPivotPoint(poseStack, bone);
                    VanillaHumanoidRenderer.HeldItemLayer.applyHandTransform(poseStack);
                    poseStack.scale(HAMMER_SCALE, HAMMER_SCALE, HAMMER_SCALE);
                    poseStack.translate(0, -HAMMER_GRIP_Y, 0);
                    getRenderer().renderRecursively(poseStack, entity, hammer, hammerType, bufferSource, bufferSource.getBuffer(hammerType), true, partialTick, packedLight, packedOverlay, -1);
                } finally {
                    poseStack.popPose();
                    bufferSource.getBuffer(renderType);
                }
            }
        });
    }

    @Override
    protected void renderNameTag(T entity, Component name, PoseStack poseStack, MultiBufferSource buffers, int packedLight, float partialTick) {
        poseStack.pushPose();
        if (entity instanceof AnglerNPC angler && angler.isWakeUp()) poseStack.translate(0, 0.4, 0);
        super.renderNameTag(entity, name, poseStack, buffers, packedLight, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void adjustPose(PoseStack poseStack, T animatable, BakedGeoModel model, float partialTick) {
        if (animatable instanceof AnglerNPC angler && !angler.isWakeUp()) {
            if (angler.isPassenger()) poseStack.translate(0.0F, 0.5F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.translate(0.0F, -1.0F, 0.0F);
        }
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
        renderQuad(CHAT_BUBBLE, poseStack, bufferSource, 1.25F, -0.01F);
        poseStack.translate(0.0F, 0.17F, 0.0F);
        if (chat.text().isPresent()) {
            renderText(Component.translatable(chat.text().get()), poseStack, bufferSource, LightTexture.FULL_BRIGHT);
        } else if (chat.emoji().isPresent()) {
            ResourceLocation texture = ResourceLocation.tryParse(chat.emoji().get());
            if (texture != null)
                renderQuad(texture, poseStack, bufferSource, ICON_SIZE, 0.0F);
        } else
            chat.item().ifPresent(item -> renderItem(entity, item, poseStack, bufferSource));
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

    private static void renderQuad(ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferSource, float size, float z) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        float half = size * 0.5F;
        vertex(consumer, poseStack.last(), LightTexture.FULL_BRIGHT, -half, -half, z, 0.0F, 1.0F);
        vertex(consumer, poseStack.last(), LightTexture.FULL_BRIGHT, half, -half, z, 1.0F, 1.0F);
        vertex(consumer, poseStack.last(), LightTexture.FULL_BRIGHT, half, half, z, 1.0F, 0.0F);
        vertex(consumer, poseStack.last(), LightTexture.FULL_BRIGHT, -half, half, z, 0.0F, 0.0F);
    }

    private static void renderItem(BaseNPC entity, ItemStack item, PoseStack poseStack, MultiBufferSource bufferSource) {
        poseStack.scale(ICON_SIZE, ICON_SIZE, ICON_SIZE);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float z, float u, float v) {
        consumer.addVertex(pose.pose(), x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

}
