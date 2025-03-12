package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.HurtnadoProjectileModel;
import org.confluence.mod.common.entity.projectile.mana.HurtnadoProjectile;

public class HurtnadoProjectileRenderer extends EntityRenderer<HurtnadoProjectile> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/hurtnado_projectile.png");
    private final HurtnadoProjectileModel model;

    public HurtnadoProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new HurtnadoProjectileModel(context.bakeLayer(HurtnadoProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(HurtnadoProjectile entity) {
        return TEXTURE;
    }

    @Override
    public void render(HurtnadoProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotation(Mth.lerp(partialTick, entity.rotateO, entity.rotate)));
        model.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
