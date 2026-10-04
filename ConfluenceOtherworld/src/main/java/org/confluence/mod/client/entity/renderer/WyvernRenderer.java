package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

///
public final class WyvernRenderer<T extends BaseWormMonster> extends GeoNormalRenderer<T> {
    public WyvernRenderer(EntityRendererProvider.Context context, float scale) {
        super(context, Confluence.asResource("wyvern"), false, scale, 0.0F);
    }

    @Override
    protected void applyRotations(T wyvern, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        float yaw = Mth.rotLerp(partialTick, wyvern.yRotO, wyvern.getYRot());
        float pitch = Mth.rotLerp(partialTick, wyvern.xRotO, wyvern.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T wyvern, GeoBone bone,
                                  RenderType renderType, MultiBufferSource buffers, VertexConsumer buffer,
                                  boolean reRender, float partialTick, int packedLight, int packedOverlay,
                                  int colour) {
        poseStack.pushPose();
        if (bone.getName().equals("Bone")) {
            // 头部主块为 [-4, 0, -6] + [8, 9, 11]，以主块中心而非颈端对齐轨迹。
            poseStack.translate(0.0, -4.5 / 16.0, 0.5 / 16.0);
        }
        super.renderRecursively(poseStack, wyvern, bone, renderType, buffers, buffer, reRender,
                partialTick, packedLight, packedOverlay, colour);
        poseStack.popPose();
    }

    @Override
    protected boolean usesInterpolatedLight(T wyvern) {
        return true;
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            T wyvern,
            BakedGeoModel model,
            MultiBufferSource buffers,
            VertexConsumer buffer,
            boolean reRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int colour) {
        model.getBone("Bone").ifPresent(bone -> bone.setHidden(false));
        model.getBone("Bone2").ifPresent(bone -> bone.setHidden(true));
        model.getBone("Bone3").ifPresent(bone -> bone.setHidden(true));
        model.getBone("Bone4").ifPresent(bone -> bone.setHidden(true));
        super.preRender(poseStack, wyvern, model, buffers, buffer, reRender, partialTick, packedLight, packedOverlay, colour);
    }
}
