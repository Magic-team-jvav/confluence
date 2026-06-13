package org.confluence.mod.client.renderer.entity.projectile.bomb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.projectile.bomb.BaseBombEntity;

public abstract class BombEntityRenderer<E extends BaseBombEntity> extends EntityRenderer<E> {
    public BombEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public abstract ResourceLocation getTextureLocation(E pEntity);

    public abstract EntityModel<E> getModel(E pEntity);

    @Override
    public void render(E pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntity.getYRot() - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotation(-Mth.lerp(pPartialTick, pEntity.rotateO, pEntity.rotate)));
        EntityModel<E> model = getModel(pEntity);
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(model.renderType(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        pPoseStack.popPose();
    }
}
