package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.client.entity.model.ZombieGeoModel;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;

/// 僵尸十种同步变体的共享渲染器。
///
/// 专属逐变体素材迁移完成前，所有变体安全复用现有美术，并通过枚举中的颜色与缩放形成
/// 可见差异。渲染参数来自服务端同步的变体身份，不使用客户端随机数，因此重进世界、远离后
/// 重新跟踪以及多人客户端看到的外观都保持一致。
public class ZombieRenderer extends GeoNormalRenderer<Zombie> {
    public ZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombieGeoModel(context));
        this.shadowRadius = 0.5F;
    }

    @Override
    public Color getRenderColor(Zombie zombie, float partialTick, int packedLight) {
        int tint = zombie.getVariant().tint();
        return Color.ofRGBA(tint >> 16 & 0xFF, tint >> 8 & 0xFF, tint & 0xFF, 0xFF);
    }

    @Override
    public void preRender(PoseStack poseStack, Zombie zombie, BakedGeoModel model,
                          MultiBufferSource bufferSource,
                          com.mojang.blaze3d.vertex.VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = zombie.getVariant().scale();
        poseStack.scale(scale, scale, scale);
        super.preRender(poseStack, zombie, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
