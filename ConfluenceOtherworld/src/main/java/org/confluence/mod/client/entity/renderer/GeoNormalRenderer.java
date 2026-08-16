package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.chat.NPCChat;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeoNormalRenderer<T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {
    private static final Minecraft MC = Minecraft.getInstance();
    protected final boolean rotateAlongPitch;
    protected final float modelScale;
    protected final float modelOffsetY;
    protected float motionAnimThreshold = 0.01F;

    public GeoNormalRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        this(context, path, false, 1.0F, 0.0F);
    }

    public GeoNormalRenderer(
            EntityRendererProvider.Context context,
            ResourceLocation path,
            boolean rotateAlongPitch,
            float modelScale,
            float modelOffsetY) {
        this(context, new GeoNormalModel<>(path), rotateAlongPitch, modelScale, modelOffsetY);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        this(context, model, false, 1.0F, 0.0F);
    }

    public GeoNormalRenderer(
            EntityRendererProvider.Context context,
            GeoModel<T> model,
            boolean rotateAlongPitch,
            float modelScale,
            float modelOffsetY) {
        super(context, model);
        this.rotateAlongPitch = rotateAlongPitch;
        this.modelScale = modelScale;
        this.modelOffsetY = modelOffsetY;
        this.shadowRadius = 0.25F;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        if (entity instanceof BaseNPC npc && npc.getChatDisplayTicks() > 0 && npc.getCurrentChat() != null)
            renderChat(poseStack, buffer, packedLight, npc);
    }

    private static void renderChat(PoseStack poseStack, MultiBufferSource buffer, int packedLight, BaseNPC npc) {
        NPCChat chat = npc.getCurrentChat();
        MutableComponent content = Component.empty();
        chat.text().ifPresent(key -> content.append(Component.translatable(key)));
        chat.emoji().ifPresent(emoji -> content.append(content.getString().isEmpty() ? "" : " ").append(Component.literal("[" + emoji + "]")));
        chat.item().ifPresent(item -> content.append(content.getString().isEmpty() ? "" : " ").append(item.getHoverName()));
        if (content.getString().isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.0, npc.getBbHeight() + 0.65, 0.0);
        poseStack.mulPose(MC.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);
        float x = -MC.font.width(content) / 2.0F;
        Matrix4f pose = poseStack.last().pose();
        VertexConsumer background = buffer.getBuffer(RenderType.debugQuads());
        background.vertex(pose, x - 2, -2, -0.1F).color(0, 0, 0, 0.55F).endVertex();
        background.vertex(pose, -x + 2, -2, -0.1F).color(0, 0, 0, 0.55F).endVertex();
        background.vertex(pose, -x + 2, 10, -0.1F).color(0, 0, 0, 0.55F).endVertex();
        background.vertex(pose, x - 2, 10, -0.1F).color(0, 0, 0, 0.55F).endVertex();
        MC.font.drawInBatch(content, x, 0, 0xFFFFFF, false, pose, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }

    /// 应用通用 Geo 渲染参数。
    ///
    /// <p>这里仅处理模型缩放、垂直偏移和沿俯仰方向旋转。它们只影响客户端显示，
    /// 不参与服务端实体碰撞箱、移动路径或伤害判定。</p>
    @Override
    public void preRender(
            PoseStack poseStack,
            T animatable,
            BakedGeoModel model,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        poseStack.scale(modelScale, modelScale, modelScale);
        poseStack.translate(0.0F, modelOffsetY, 0.0F);
        if (rotateAlongPitch) {
            double yaw = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())
                    * Mth.DEG_TO_RAD;
            Vector3f axis = new Vector3f((float) Math.cos(yaw), 0.0F, (float) Math.sin(yaw));
            poseStack.mulPose(Axis.of(axis).rotationDegrees(
                    Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        }
        adjustPose(poseStack, animatable, model, partialTick);
        super.preRender(
                poseStack, animatable, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected void adjustPose(
            PoseStack poseStack,
            T animatable,
            BakedGeoModel model,
            float partialTick) {}

    @Override
    public float getMotionAnimThreshold(T animatable) {
        return motionAnimThreshold;
    }

    @Override
    public GeoNormalRenderer<T> withScale(float scale) {
        super.withScale(scale);
        return this;
    }

    public GeoNormalRenderer<T> setMotionAnimThreshold(float threshold) {
        this.motionAnimThreshold = threshold;
        return this;
    }

    public GeoNormalRenderer<T> setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }
}
