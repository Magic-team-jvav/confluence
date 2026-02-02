package org.confluence.terra_curio.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.model.entity.XBoneProjectileModel;
import org.confluence.terra_curio.common.entity.XBoneProjectile;
import org.jetbrains.annotations.NotNull;

public class XBoneProjectileRenderer extends EntityRenderer<XBoneProjectile> {
    private static final ResourceLocation TEXTURE = TerraCurio.asResource("textures/entity/x_bone_projectile.png");
    private final XBoneProjectileModel model;

    public XBoneProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new XBoneProjectileModel(pContext.bakeLayer(XBoneProjectileModel.LAYER_LOCATION));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull XBoneProjectile pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(XBoneProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.75F, 0.75F, 0.75F);
        poseStack.translate(0, -0.1f, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        poseStack.mulPose(Axis.ZP.rotation(-Mth.lerp(partialTick, entity.rotate.old, entity.rotate.neo)));
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
