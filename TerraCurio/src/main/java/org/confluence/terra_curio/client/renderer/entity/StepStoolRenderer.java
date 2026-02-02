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
import org.confluence.terra_curio.client.model.entity.StepStoolModel;
import org.confluence.terra_curio.common.entity.StepStoolEntity;

public class StepStoolRenderer extends EntityRenderer<StepStoolEntity> {
    private static final ResourceLocation TEXTURE = TerraCurio.asResource("textures/entity/step_stool.png");

    private final StepStoolModel model;

    public StepStoolRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new StepStoolModel((pContext.bakeLayer(StepStoolModel.LAYER_LOCATION)));
    }

    @Override
    public ResourceLocation getTextureLocation(StepStoolEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(StepStoolEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, 1.5F, 0.0F);
        pPoseStack.mulPose(Axis.ZP.rotation(Mth.PI));
        for (int i = 0; i < pEntity.getStep(); i++) {
            model.renderToBuffer(pPoseStack, pBuffer.getBuffer(model.renderType(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.translate(0.0F, -1.0F, 0.0F);
        }
        pPoseStack.popPose();
    }
}
