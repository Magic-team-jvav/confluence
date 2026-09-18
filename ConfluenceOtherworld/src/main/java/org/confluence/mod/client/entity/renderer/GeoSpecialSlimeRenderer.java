package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.monster.slime.SpikedSlime;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

/// 带外壳和饰物的 Geo 史莱姆渲染器。
///
/// 内核和饰物先绘制，透明外壳后绘制；不能按父骨骼把五官或饰品划入外壳。
public class GeoSpecialSlimeRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    @Override
    protected float getEffectiveModelScale(T entity) {
        return super.getEffectiveModelScale(entity) * (entity instanceof SpikedSlime ? SpikedSlime.MODEL_SCALE : 1.0F);
    }

    private boolean shellPass;
    public GeoSpecialSlimeRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        this(context, path, false, 1.0F);
    }

    public GeoSpecialSlimeRenderer(EntityRendererProvider.Context context, ResourceLocation path, boolean rotateAlongPitch) {
        this(context, path, rotateAlongPitch, 1.0F);
    }

    public GeoSpecialSlimeRenderer(EntityRendererProvider.Context context, ResourceLocation path, boolean rotateAlongPitch, float modelScale) {
        super(context, path, rotateAlongPitch, modelScale, 0.0F);
    }

    protected GeoSpecialSlimeRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    @Override
    public void actuallyRender(PoseStack poses, T entity, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource buffers, VertexConsumer buffer, boolean reRender, float partialTick,
                               int light, int overlay, float red, float green, float blue, float alpha) {
        if (!usesSlimeLayers(entity)) {
            super.actuallyRender(poses, entity, model, renderType, buffers, buffer, reRender,
                    partialTick, light, overlay, red, green, blue, alpha);
            return;
        }
        RenderType solid = RenderType.entityCutout(getTextureLocation(entity));
        RenderType shell = RenderType.entityTranslucentCull(getTextureLocation(entity));
        shellPass = false;
        super.actuallyRender(poses, entity, model, solid, buffers, buffers.getBuffer(solid), reRender,
                partialTick, light, overlay, red, green, blue, alpha);
        if (buffers instanceof MultiBufferSource.BufferSource source) source.endBatch(solid);
        finishSolidPass(entity, buffers);
        shellPass = true;
        try {
            super.actuallyRender(poses, entity, model, shell, buffers, buffers.getBuffer(shell), true,
                    partialTick, light, overlay, red, green, blue, alpha);
        } finally {
            shellPass = false;
        }
    }

    @Override
    public void renderCubesOfBone(PoseStack poses, GeoBone bone, VertexConsumer buffer, int light,
                                  int overlay, float red, float green, float blue, float alpha) {
        if (bone.isHidden()) return;
        if (!usesSlimeLayers(getAnimatable())) {
            super.renderCubesOfBone(poses, bone, buffer, light, overlay, red, green, blue, alpha);
            return;
        }
        for (int index = 0; index < bone.getCubes().size(); index++) {
            if (isShellCube(bone, index) != shellPass) continue;
            poses.pushPose();
            renderSlimeCube(poses, bone, index, buffer, light, overlay, red, green, blue, alpha);
            poses.popPose();
        }
    }

    protected boolean isShellCube(GeoBone bone, int index) {
        var size = bone.getCubes().get(index).size();
        return bone.getName().equals("outer") && (index == 0 || size.x == 8 && size.z == 8);
    }

    protected boolean usesSlimeLayers(T entity) {
        return true;
    }

    protected void finishSolidPass(T entity, MultiBufferSource buffers) {}

    protected void renderSlimeCube(PoseStack poses, GeoBone bone, int index, VertexConsumer buffer,
                                   int light, int overlay, float red, float green, float blue, float alpha) {
        renderCube(poses, bone.getCubes().get(index), buffer, light, overlay, red, green, blue, alpha);
    }
}
