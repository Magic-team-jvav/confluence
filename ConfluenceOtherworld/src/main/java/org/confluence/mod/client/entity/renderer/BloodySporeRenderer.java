package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.monster.BloodySpore;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 根据服务端同步引信进度绘制血腥芽孢的膨胀效果。
public final class BloodySporeRenderer extends GeoNormalRenderer<BloodySpore> {
    public BloodySporeRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, path);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            BloodySpore spore,
            BakedGeoModel model,
            MultiBufferSource buffers,
            VertexConsumer buffer,
            boolean reRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        float swelling = spore.getSwelling(partialTick);
        float wobble = 1.0F
                + Mth.sin(swelling * 100.0F)
                * swelling * 0.01F;
        swelling = Mth.clamp(swelling, 0.0F, 1.0F);
        swelling *= swelling;
        swelling *= swelling;
        float horizontal = (1.0F + swelling * 0.4F) * wobble;
        float vertical = (1.0F + swelling * 0.1F) / wobble;
        poseStack.scale(horizontal, vertical, horizontal);
        super.preRender(poseStack, spore, model, buffers, buffer, reRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
