package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.client.entity.renderer.GeoNormalRenderer;
import org.confluence.mod.common.entity.projectile.DeerclopsIcePillarProjectile;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 使用独眼巨鹿冰刺的专用几何与贴图。
public final class DeerclopsIcePillarRenderer extends GeoNormalRenderer<DeerclopsIcePillarProjectile> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/proj/ice_pillar.geo.json");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/proj/ice_pillar.png");

    public DeerclopsIcePillarRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplicitGeoModel<>(MODEL, TEXTURE, null));
        shadowRadius = 0.0F;
    }

    @Override
    public void render(DeerclopsIcePillarProjectile entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        if (entity.rendersWaveModel()) {
            super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
        }
    }

    @Override
    protected boolean usesInterpolatedLight(DeerclopsIcePillarProjectile entity) {
        return true;
    }

    @Override
    protected void applyRotations(DeerclopsIcePillarProjectile entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        var direction = entity.getWaveDirection();
        float modelYaw = (float) Math.toDegrees(Math.atan2(-direction.x, -direction.z));
        poseStack.mulPose(Axis.YP.rotationDegrees(modelYaw));
    }

    @Override
    public void preRender(PoseStack poseStack, DeerclopsIcePillarProjectile entity,
                          BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          int colour) {
        float age = entity.tickCount + partialTick;
        setSectionVisible(model, "bone4", age, 0.0F);
        setSectionVisible(model, "bone3", age, 2.0F);
        setSectionVisible(model, "bone2", age, 5.0F);
        setSectionVisible(model, "bone", age, 8.0F);
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    private static void setSectionVisible(BakedGeoModel model, String boneName, float age, float appearanceDelay) {
        // 近端先升起，随后向攻击方向长到最高处；消失阶段按相反次序收回。
        float disappearanceTime = 40.0F - appearanceDelay;
        model.getBone(boneName).ifPresent(bone -> bone.setHidden(age < appearanceDelay || age >= disappearanceTime));
    }

}
