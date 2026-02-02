package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.Skeletron;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class SkeletronRenderer extends GeoNormalRenderer<Skeletron> {

    public SkeletronRenderer(EntityRendererProvider.Context renderManager, GeoBossModel<Skeletron> model) {
        super(renderManager, model, true, 1, 0);
    }

    @Override
    public void preRender(PoseStack poseStack, Skeletron entity, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        model.getBone("bone3").ifPresent(bone -> bone.setHidden(true));
        model.getBone("bone2").ifPresent(geoBone -> geoBone.setHidden(false));
//        poseStack.scale(0.3f, 0.3f, 0.3f);
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (entity.getEntityData().get(Skeletron.DATA_SPINNING)) {
            poseStack.translate(0, 1.15f * scale, 0);
            int spinTick = entity.phase;
            float yRot = Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
            double rad = yRot*Math.PI/180;
            float xRot = Mth.lerp(partialTick, spinTick * 36, (spinTick + 1) * 36);
            poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(rad), 0, (float) Math.sin(rad))).rotationDegrees(xRot));
            poseStack.translate(0, -1.15f * scale, 0);
        }
    }
}
