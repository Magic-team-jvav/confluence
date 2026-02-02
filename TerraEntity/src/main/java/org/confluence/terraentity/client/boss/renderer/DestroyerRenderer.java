package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.boss.model.DestroyerModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.thedestroyer.TheDestroyer;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class DestroyerRenderer extends GeoNormalRenderer<TheDestroyer> {

    public DestroyerRenderer(EntityRendererProvider.Context renderManager) {
        // 使用自定义的 DestroyerModel 以支持纹理切换
        super(renderManager, new DestroyerModel(), true, 2.2f, 0f);
    }

    @Override
    public void preRender(PoseStack poseStack, TheDestroyer animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        // 获取并应用 Roll
        float roll = animatable.getBodyRoll();
        Vec3 axis = TEUtils.rotToDir(animatable.getYHeadRot(), animatable.getXRot());
        poseStack.mulPose(new Quaternionf().fromAxisAngleDeg((float) axis.x, (float) axis.y, (float) axis.z, roll));

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
