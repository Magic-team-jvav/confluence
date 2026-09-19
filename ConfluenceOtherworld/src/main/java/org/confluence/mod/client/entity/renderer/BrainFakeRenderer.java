package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.BrainDissolveTexture;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.boss.BrainFake;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.object.Color;

/// 克苏鲁之脑幻象的透明渲染器。
public final class BrainFakeRenderer extends BossGeoRenderer<BrainFake> {
    public BrainFakeRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/brain_of_cthulhu"));
    }

    @Override
    public void render(BrainFake fake, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        if (!fake.hasDistinctRenderPosition(partialTick) || fake.getFadeProgress(partialTick) <= 0.0F)
            return;
        Vec3 correction = fake.getSmoothRenderPosition(partialTick).subtract(fake.getPosition(partialTick));
        poseStack.pushPose();
        poseStack.translate(correction.x, correction.y, correction.z);
        super.render(fake, entityYaw, partialTick, poseStack, buffers, packedLight);
        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(BrainFake fake, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        var owner = fake.getOwner();
        return RenderStateShardAccessor.entityTranslucentCullOverlay(BrainDissolveTexture.texture(texture, owner == null ? 0.0F : owner.getFadeProgress(partialTick)));
    }

    @Override
    public Color getRenderColor(BrainFake fake, float partialTick, int packedLight) {
        return Color.ofRGBA(255, 255, 255, Math.round(255 * fake.getFadeProgress(partialTick)));
    }
}
