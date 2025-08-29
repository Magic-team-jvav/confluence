package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.SkullProjectileModel;
import org.confluence.mod.common.entity.projectile.mana.SkullProjectile;

public class SkullProjectileRenderer extends EntityRenderer<SkullProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/skull_projectile.png");
    private final SkullProjectileModel model;

    public SkullProjectileRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new SkullProjectileModel(pContext.bakeLayer(SkullProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(SkullProjectile pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(SkullProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.0F, 0.6f, 0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.mulPose(LibClientUtils.ANGLE_N90);
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
