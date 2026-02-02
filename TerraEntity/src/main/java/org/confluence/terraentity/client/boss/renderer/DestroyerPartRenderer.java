package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.thedestroyer.TheDestroyerPart;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class DestroyerPartRenderer extends GeoNormalRenderer<TheDestroyerPart> {

    // 预加载两个模型：身体和尾部
    // TODO: 使用真正的模型
    private static final GeoBossModel<TheDestroyerPart> BODY_MODEL = new GeoBossModel<>("eater_of_worlds_segment");
    private static final GeoBossModel<TheDestroyerPart> TAIL_MODEL = new GeoBossModel<>("eater_of_worlds_tail");

    public DestroyerPartRenderer(EntityRendererProvider.Context renderManager) {
        // 默认传入 BODY_MODEL，但在 getGeoModel 中会动态切换
        super(renderManager, BODY_MODEL, true, 2.2f, 0f);
    }

    @Override
    public GeoModel<TheDestroyerPart> getGeoModel() {
        TheDestroyerPart segment = this.getAnimatable();
        // 根据实体状态切换模型
        if (segment != null && segment.isTail()) {
            return TAIL_MODEL;
        }
        return BODY_MODEL;
    }

    @Override
    public void preRender(PoseStack poseStack, TheDestroyerPart animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        // 获取并应用 Roll
        float roll = animatable.getSegmentRoll();
        Vec3 axis = TEUtils.rotToDir(animatable.getYRot(), animatable.getXRot());
        poseStack.mulPose(new Quaternionf().fromAxisAngleDeg((float) axis.x, (float) axis.y, (float) axis.z, roll));

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
