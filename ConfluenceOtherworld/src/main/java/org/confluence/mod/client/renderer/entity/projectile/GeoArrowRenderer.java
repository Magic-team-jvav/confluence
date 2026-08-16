package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.client.entity.renderer.GeoNormalRenderer;
import org.confluence.mod.common.entity.projectile.arrow.HellBatArrowEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class GeoArrowRenderer extends GeoNormalRenderer<HellBatArrowEntity> {
    public GeoArrowRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path);
    }

    /// 在 Geo 渲染器已经绑定本帧实体后修正箭矢模型朝向。
    ///
    /// <p>不能在 {@code render} 调用父类之前读取渲染器的 {@code animatable} 字段，
    /// 因为 GeckoLib 此时尚未为本帧赋值；直接读取会在首次渲染箭矢时触发空指针。</p>
    @Override
    protected void adjustPose(
            PoseStack poseStack,
            HellBatArrowEntity entity,
            BakedGeoModel model,
            float partialTick
    ) {
        poseStack.translate(0, 0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(60F));
        poseStack.translate(0F, -0.5, -0.3F);
    }
}
