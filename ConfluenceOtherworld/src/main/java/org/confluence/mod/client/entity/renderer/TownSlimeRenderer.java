package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.effect.DivaSlimeVertexConsumer;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.npc.TownSlimeNPC;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

/// 城镇史莱姆先绘制内核、五官和饰品，再绘制贴图自带透明度的两层外壳。
public final class TownSlimeRenderer extends GeoSpecialSlimeRenderer<TownSlimeNPC> {
    private final boolean diva;
    private MultiBufferSource rainbowBuffers;
    private Matrix4f rainbowLocalTransform;
    private float rainbowTime;

    public TownSlimeRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, path);
        diva = path.getPath().equals("npc/diva_slime");
    }

    @Override
    protected boolean isShellCube(GeoBone bone, int index) {
        return bone.getName().equals("outer") && (index == 0 || diva) || bone.getName().equals("bb_main") && index == 0;
    }

    @Override
    public void actuallyRender(PoseStack poses, TownSlimeNPC entity, BakedGeoModel model, RenderType type,
                               MultiBufferSource buffers, VertexConsumer buffer, boolean reRender, float partialTick,
                               int light, int overlay, float red, float green, float blue, float alpha) {
        rainbowBuffers = buffers;
        if (diva) {
            rainbowLocalTransform = new Matrix4f(poses.last().pose()).invert();
            rainbowTime = (entity.level().getGameTime() % 240L + partialTick) / 240.0F;
        }
        try {
            super.actuallyRender(poses, entity, model, type, buffers, buffer, reRender, partialTick,
                    light, overlay, red, green, blue, alpha);
        } finally {
            if (diva) flushRainbow(entity, buffers);
            rainbowBuffers = null;
        }
    }

    // 先提交内核再绘制外壳，避免透明层与饰品混入同一批次。
    private void flushRainbow(TownSlimeNPC entity, MultiBufferSource buffers) {
        if (buffers instanceof MultiBufferSource.BufferSource source) {
            ResourceLocation texture = bodyTexture(entity);
            source.endBatch(RenderType.entityCutout(texture));
            source.endBatch(RenderStateShardAccessor.entityTranslucentCullOverlay(texture));
        }
    }

    @Override
    protected void renderSlimeCube(PoseStack poses, GeoBone bone, int index, VertexConsumer buffer,
                                   int light, int overlay, float red, float green, float blue, float alpha) {
        if (diva) {
            ResourceLocation texture = getTextureLocation(getAnimatable());
            boolean coloredBody = isShellCube(bone, index) || bone.getName().equals("inner") && index == 0;
            if (coloredBody) texture = bodyTexture(getAnimatable());
            buffer = rainbowBuffers.getBuffer(isShellCube(bone, index)
                    ? RenderStateShardAccessor.entityTranslucentCullOverlay(texture) : RenderType.entityCutout(texture));
            if (coloredBody)
                buffer = new DivaSlimeVertexConsumer(buffer, rainbowLocalTransform, rainbowTime);
        }
        super.renderSlimeCube(poses, bone, index, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    protected void finishSolidPass(TownSlimeNPC entity, MultiBufferSource buffers) {
        if (diva && buffers instanceof MultiBufferSource.BufferSource source)
            source.endBatch(RenderType.entityCutout(bodyTexture(entity)));
    }

    private ResourceLocation bodyTexture(TownSlimeNPC entity) {
        return DivaSlimeVertexConsumer.texture(getTextureLocation(entity));
    }
}
