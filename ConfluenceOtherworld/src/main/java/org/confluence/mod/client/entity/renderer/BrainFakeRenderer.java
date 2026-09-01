package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.BrainFake;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 克苏鲁之脑幻象的透明渲染器。
public final class BrainFakeRenderer extends BossGeoRenderer<BrainFake> {
    public BrainFakeRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/brain_of_cthulhu"));
    }

    @Override
    public void render(BrainFake fake, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight) {
        if (!fake.hasDistinctRenderPosition(partialTick)) return;
        net.minecraft.world.phys.Vec3 correction = fake.getSmoothRenderPosition(partialTick)
                .subtract(fake.getPosition(partialTick));
        poseStack.translate(correction.x, correction.y, correction.z);
        super.render(fake, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public RenderType getRenderType(BrainFake fake, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        // 脑模型是闭合表面；透明且禁用背面剔除会把前后两层近共面纹理同时混合，
        // 在移动时产生明显闪烁。保留透明淡入，但只绘制朝向镜头的表面。
        return RenderType.entityTranslucentCull(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, BrainFake fake, BakedGeoModel model,
                          MultiBufferSource buffers, VertexConsumer buffer, boolean reRender,
                          float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, fake, model, buffers, buffer, reRender, partialTick,
                packedLight, packedOverlay, red, green, blue,
                alpha * fake.getFadeProgress(partialTick));
    }
}
