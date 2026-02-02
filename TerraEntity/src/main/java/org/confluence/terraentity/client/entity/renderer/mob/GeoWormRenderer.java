package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.monster.BaseWorm;
import org.confluence.terraentity.entity.monster.BaseWormPart;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;


public class GeoWormRenderer<T extends BaseWorm<S>, S extends BaseWormPart> extends GeoNormalRenderer<T> {
    GeoWormSegmentRenderer partRenderer;
    public double lerpx;
    public double lerpy;
    public double lerpz;

    /**
     * 文件命名：
     * <p>{path}.geo.json</p>
     * <p>{path}_segment.geo.json</p>
     * <p>{path}_tail.geo.json</p>
     *
     * @param path entity
     */
    public GeoWormRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        this(renderManager, path, 1.0f, 0.0f);
    }

    /**
     * 文件命名：
     * <p>{path}.geo.json</p>
     * <p>{path}_segment.geo.json</p>
     * <p>{path}_tail.geo.json</p>
     *
     * @param path entity
     */
    public GeoWormRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path, float scale, float offsetY) {
        super(renderManager, path, true, scale, offsetY);
        partRenderer = createPartRenderer(renderManager, path);
    }

    protected GeoWormSegmentRenderer createPartRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        String name = path.getPath();
        String segment = name + "_segment";
        String tail = name + "_tail";
        return new GeoWormSegmentRenderer<>(renderManager, this,
                TerraEntity.space(segment),
                TerraEntity.space(tail), scale, offsetY);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        S part1 = entity.bodySegments.getFirst();

        lerpx = Mth.lerp(partialTick, entity.xo, entity.getX());
        lerpy = Mth.lerp(partialTick, entity.yo, entity.getY());
        lerpz = Mth.lerp(partialTick, entity.zo, entity.getZ());
        double lerpDx = lerpx - Mth.lerp(partialTick, part1.xo, part1.getX());
        double lerpDy = lerpy - Mth.lerp(partialTick, part1.yo, part1.getY());
        double lerpDz = lerpz - Mth.lerp(partialTick, part1.zo, part1.getZ());

        float yRot = Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float rad = yRot * Mth.DEG_TO_RAD;
        float pitch = (float) (Mth.atan2(lerpDy, Math.sqrt(lerpDx * lerpDx + lerpDz * lerpDz)));
        poseStack.mulPose(Axis.of(new Vector3f(Mth.cos(rad), 0, Mth.sin(rad))).rotation(-pitch));

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        renderPart(entity, partialTick, poseStack, bufferSource);
    }

    protected void renderPart(T entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        for (S part : entity.bodySegments) {
            poseStack.pushPose();
            float lerpYRot = Mth.lerp(partialTick, part.yRotO, part.getYRot());
            partRenderer.render(part, lerpYRot, partialTick, poseStack, bufferSource, entityRenderDispatcher.getPackedLightCoords(part, partialTick));
            poseStack.popPose();
        }
    }

    protected void rotateX(PoseStack poseStack, T animatable, float partialTick) {}

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
