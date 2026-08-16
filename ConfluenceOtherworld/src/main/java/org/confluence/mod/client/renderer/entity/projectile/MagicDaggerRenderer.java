package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.renderer.GeoNegativeVolumeRenderer;
import org.confluence.mod.common.entity.projectile.mana.MagicDaggerProjectile;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class MagicDaggerRenderer extends GeoNegativeVolumeRenderer<MagicDaggerProjectile> {
    public MagicDaggerRenderer(EntityRendererProvider.Context context) {
        // 魔法飞刀的实体注册名不含 projectile，而资源文件名包含该后缀。
        super(context, Confluence.asResource("magic_dagger_projectile"));
    }

    @Override
    public void render(MagicDaggerProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.tickCount > 1) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 0xF000F0);
        }
    }

    /// 在父类开始 Geo 渲染后使用本帧实体计算飞刀朝向，避免读取尚未绑定的渲染状态。
    @Override
    protected void adjustPose(
            PoseStack poseStack,
            MagicDaggerProjectile entity,
            BakedGeoModel model,
            float partialTick
    ) {
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() + 180));
        poseStack.mulPose(Axis.XP.rotationDegrees(
                Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
    }
}
