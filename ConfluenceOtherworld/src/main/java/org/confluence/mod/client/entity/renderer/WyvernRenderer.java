package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 飞龙头部渲染器。飞龙模型同时保存了头部和三种体节，因此渲染头部时必须隐藏所有体节分组。
public final class WyvernRenderer<T extends BaseWormMonster> extends GeoNormalRenderer<T> {
    public WyvernRenderer(EntityRendererProvider.Context context, float scale) {
        super(context, Confluence.asResource("wyvern"), true, scale, 0.0F);
    }

    @Override
    public RenderType getRenderType(T wyvern, ResourceLocation texture, @Nullable MultiBufferSource buffers, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
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
            float red,
            float green,
            float blue,
            float alpha) {
        model.getBone("Bone").ifPresent(bone -> bone.setHidden(false));
        model.getBone("Bone2").ifPresent(bone -> bone.setHidden(true));
        model.getBone("Bone3").ifPresent(bone -> bone.setHidden(true));
        model.getBone("Bone4").ifPresent(bone -> bone.setHidden(true));
        super.preRender(poseStack, wyvern, model, buffers, buffer, reRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
