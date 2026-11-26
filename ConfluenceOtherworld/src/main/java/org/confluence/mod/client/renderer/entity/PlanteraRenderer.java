package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.GeoNormalModel;
import org.confluence.mod.common.entity.boss.Plantera;

public class PlanteraRenderer extends BossGeoRenderer<Plantera> {
    private static final ResourceLocation PHASE_ONE = Confluence.asResource("boss/plantera");
    private static final ResourceLocation PHASE_TWO_MODEL = Confluence.asResource("geo/entity/boss/plantera_phase2.geo.json");
    private static final ResourceLocation PHASE_TWO_TEXTURE = Confluence.asResource("textures/entity/boss/plantera_phase2.png");

    public PlanteraRenderer(EntityRendererProvider.Context context) {
        super(context, new GeoNormalModel<>(PHASE_ONE, false) {
            @Override
            public ResourceLocation getModelResource(Plantera boss) {
                return boss.getPhase() == 0 ? super.getModelResource(boss) : PHASE_TWO_MODEL;
            }

            @Override
            public ResourceLocation getTextureResource(Plantera boss) {
                return boss.getPhase() == 0 ? super.getTextureResource(boss) : PHASE_TWO_TEXTURE;
            }
        }, false, 1.0F, 0.0F);
    }

    @Override
    protected boolean usesInterpolatedLight(Plantera entity) {
        return true;
    }

    @Override
    protected void applyRotations(Plantera animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        poseStack.translate(0, animatable.getBbHeight() * 0.5, 0);
        // 花冠沿模型的正 Y 轴开放，俯仰时需将这条轴转到实体视线方向。
        poseStack.mulPose(animatable.flowerOrientation(partialTick));
        poseStack.scale(Plantera.MODEL_SCALE, Plantera.MODEL_SCALE, Plantera.MODEL_SCALE);
        poseStack.translate(0, -1.0, 0);
    }
}
