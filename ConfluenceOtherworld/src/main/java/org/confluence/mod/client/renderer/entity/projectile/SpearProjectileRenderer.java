package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.projectile.spear.SpearProjectile;
import org.jetbrains.annotations.Nullable;

/**
 * <h1>通用长矛弹射物渲染器</h1>
 * 读取实体渲染元数据（纹理、模型层、自旋角度），实现一类一物品。
 */
public class SpearProjectileRenderer extends EntityRenderer<SpearProjectile> {
    @Nullable
    private final EntityModel<SpearProjectile> model;

    public SpearProjectileRenderer(EntityRendererProvider.Context context,
                                    @Nullable ModelLayerLocation layer) {
        super(context);
        this.model = layer != null ? new ProxyModel(context.bakeLayer(layer)) : null;
    }

    @Override
    public ResourceLocation getTextureLocation(SpearProjectile entity) {
        ResourceLocation tex = entity.getProjTexture();
        return tex != null ? tex : InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public void render(SpearProjectile entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (model == null || entity.getProjTexture() == null) return;

        poseStack.pushPose();
        poseStack.translate(0, 0.375F, 0);
        poseStack.scale(2, 2, 2);
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot()); 
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(Axis.ZP.rotation(entity.getSpinRotation(partialTick)));
        model.renderToBuffer(poseStack, buffer.getBuffer(model.renderType(getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static class ProxyModel extends EntityModel<SpearProjectile> {
        private final ModelPart root;

        ProxyModel(ModelPart root) { this.root = root; }

        @Override
        public void setupAnim(SpearProjectile e, float a, float b, float c, float d, float f) {}

        @Override
        public void renderToBuffer(PoseStack ps, com.mojang.blaze3d.vertex.VertexConsumer vc,
                                    int light, int overlay, int color) {
            root.render(ps, vc, light, overlay, color);
        }
    }
}