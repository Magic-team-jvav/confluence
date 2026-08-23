package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.boss.Retinazer;
import org.confluence.mod.common.entity.boss.Spazmatism;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.util.RenderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/// 双子魔眼变形时的局部噪声溶解渲染。
///
/// 这里只叠加指定骨骼组的噪声边缘，不接管克苏鲁之脑那种整只实体淡出的透明度。
/// 这样变形特效能落在眼睛自己的结构上，也不会把主体、缆线或 Boss 血条相关渲染一起影响掉。
public class TwinEyeDissolveRenderer<T extends AbstractTwinEye> extends BossGeoRenderer<T> {
    private static final int DISSOLVE_TICKS = 24;

    private final Set<String> dissolveRoots;
    private final float edgeRed;
    private final float edgeGreen;
    private final float edgeBlue;
    private final Map<Integer, Float> transformStarts = new HashMap<>();
    private int selectedDepth;

    private TwinEyeDissolveRenderer(EntityRendererProvider.Context context, ResourceLocation path, Set<String> dissolveRoots, int edgeColor) {
        super(context, path);
        this.dissolveRoots = dissolveRoots;
        this.edgeRed = ((edgeColor >> 16) & 0xFF) / 255.0F;
        this.edgeGreen = ((edgeColor >> 8) & 0xFF) / 255.0F;
        this.edgeBlue = (edgeColor & 0xFF) / 255.0F;
    }

    public static TwinEyeDissolveRenderer<Retinazer> retinazer(EntityRendererProvider.Context context) {
        return new TwinEyeDissolveRenderer<>(context, Confluence.asResource("boss/retinazer"), Set.of("bone26", "bone31"), 0xFF3030);
    }

    public static TwinEyeDissolveRenderer<Spazmatism> spazmatism(EntityRendererProvider.Context context) {
        return new TwinEyeDissolveRenderer<>(
                context,
                Confluence.asResource("boss/spazmatism"),
                Set.of(
                        "bone36",
                        "bone37",
                        "left_up_tooth",
                        "left_down_tooth",
                        "right_up_tooth",
                        "right_down_tooth",
                        "bone38",
                        "bone39",
                        "bone40",
                        "bone41",
                        "bone42",
                        "bone44",
                        "bone45",
                        "bone46",
                        "bone47",
                        "bone48",
                        "bone49",
                        "bone50",
                        "bone51",
                        "bone52",
                        "bone53"),
                0x48FF5A);
    }

    @Override
    public void renderRecursively(
            PoseStack poseStack,
            T animatable,
            GeoBone bone,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        poseStack.pushPose();
        RenderUtils.prepMatrixForBone(poseStack, bone);
        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        if (!isReRender) {
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }

        boolean selected = selectedDepth > 0
                || dissolveRoots.contains(bone.getName());
        float dissolveProgress = dissolveProgress(animatable, partialTick);
        if (!isReRender && selected && dissolveProgress >= 0.0F) {
            VertexConsumer dissolveBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(getTextureLocation(animatable)));
            renderDissolveCubesOfBone(poseStack, bone, dissolveBuffer, packedOverlay, dissolveProgress, animatable.tickCount + partialTick);
        }

        int previousDepth = selectedDepth;
        if (selected) {
            selectedDepth++;
        }
        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        selectedDepth = previousDepth;
        poseStack.popPose();
    }

    private float dissolveProgress(T animatable, float partialTick) {
        if (!animatable.isTransformed() || !animatable.isAlive()) {
            transformStarts.remove(animatable.getId());
            return -1.0F;
        }
        float now = animatable.tickCount + partialTick;
        float start = transformStarts.computeIfAbsent(animatable.getId(), ignored -> now);
        float age = now - start;
        if (age > DISSOLVE_TICKS) {
            return -1.0F;
        }
        return Mth.clamp(age / DISSOLVE_TICKS, 0.0F, 1.0F);
    }

    private void renderDissolveCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedOverlay, float progress, float time) {
        if (bone.isHidden()) {
            return;
        }
        int cubeIndex = 0;
        for (GeoCube cube : bone.getCubes()) {
            float noise = noise01(bone.getName(), cubeIndex);
            float width = 0.22F + 0.04F * Mth.sin(time * 0.45F + cubeIndex);
            float distance = Math.abs(noise - progress);
            if (distance <= width) {
                float alpha = (1.0F - distance / width) * 0.82F;
                poseStack.pushPose();
                renderDissolveCube(poseStack, cube, buffer, packedOverlay, alpha);
                poseStack.popPose();
            }
            cubeIndex++;
        }
    }

    private void renderDissolveCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedOverlay, float alpha) {
        RenderUtils.translateToPivotPoint(poseStack, cube);
        RenderUtils.rotateMatrixAroundCube(poseStack, cube);
        RenderUtils.translateAwayFromPivotPoint(poseStack, cube);

        Matrix3f normalState = poseStack.last().normal();
        Matrix4f poseState = poseStack.last().pose();
        for (GeoQuad quad : cube.quads()) {
            if (quad == null) {
                continue;
            }
            Vector3f normal = normalState.transform(new Vector3f(quad.normal()));
            RenderUtils.fixInvertedFlatCube(cube, normal);
            for (GeoVertex vertex : quad.vertices()) {
                Vector3f position = vertex.position();
                Vector4f transformed = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0F));
                buffer.vertex(transformed.x(), transformed.y(), transformed.z(), edgeRed, edgeGreen, edgeBlue, alpha, vertex.texU(), vertex.texV(), packedOverlay, LightTexture.FULL_BRIGHT, normal.x(), normal.y(), normal.z());
            }
        }
    }

    private static float noise01(String boneName, int cubeIndex) {
        int hash = boneName.hashCode();
        hash ^= cubeIndex * 0x45D9F3B;
        hash ^= hash >>> 16;
        hash *= 0x45D9F3B;
        hash ^= hash >>> 16;
        return (hash & 0xFFFF) / 65535.0F;
    }
}
