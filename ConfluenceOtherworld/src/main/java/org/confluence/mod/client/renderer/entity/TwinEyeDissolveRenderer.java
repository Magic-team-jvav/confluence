package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.boss.Retinazer;
import org.confluence.mod.common.entity.boss.Spazmatism;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.*;
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
        super(context, path, true, 1.0F, 0.8F);
        this.dissolveRoots = dissolveRoots;
        this.edgeRed = ((edgeColor >> 16) & 0xFF) / 255.0F;
        this.edgeGreen = ((edgeColor >> 8) & 0xFF) / 255.0F;
        this.edgeBlue = (edgeColor & 0xFF) / 255.0F;
    }

    @Override
    protected void adjustPose(PoseStack poseStack, T entity, BakedGeoModel model, float partialTick) {
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getVisualBank(partialTick)));
        poseStack.translate(0, -0.5, 0);
        model.getBone("bone31").ifPresent(bone -> {
            bone.setHidden(!entity.isTransformed());
            bone.setChildrenHidden(!entity.isTransformed());
        });
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        return entity instanceof Retinazer eye && eye.getBeamLength() > 0 || super.shouldRender(entity, frustum, x, y, z);
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
        if (!(entity instanceof Retinazer eye) || eye.getBeamLength() <= 0 || !eye.isAlive())
            return;
        Vec3 direction = eye.getBeamDirection();
        Vec3 start = eye.getMuzzlePosition(partialTick);
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double projection = camera.subtract(start).dot(direction);
        double perpendicularSqr = camera.distanceToSqr(start.add(direction.scale(projection)));
        double cameraClearance = LaserProjectileRenderer.CAMERA_CLEARANCE;
        if (perpendicularSqr >= cameraClearance * cameraClearance) {
            renderBeamSection(eye, partialTick, poseStack, buffers, direction, 0, eye.getBeamLength());
        } else {
            double halfGap = Math.sqrt(cameraClearance * cameraClearance - perpendicularSqr);
            renderBeamSection(eye, partialTick, poseStack, buffers, direction, 0, Math.min(eye.getBeamLength(), projection - halfGap));
            renderBeamSection(eye, partialTick, poseStack, buffers, direction, Math.max(0, projection + halfGap), eye.getBeamLength());
        }
    }

    /// 镜头附近留出可视空间，只裁剪光束外观，不改变服务端伤害范围。
    private void renderBeamSection(Retinazer eye, float partialTick, PoseStack poseStack, MultiBufferSource buffers, Vec3 direction, double from, double to) {
        if (to <= from) return;
        Vec3 middle = direction.scale((from + to) * 0.5).add(eye.getMuzzlePosition(partialTick).subtract(eye.getPosition(partialTick)));
        poseStack.pushPose();
        poseStack.translate(middle.x, middle.y, middle.z);
        LaserProjectileRenderer.renderBeam(poseStack, buffers, direction, (float) (to - from),
                eye.isBeamCharging() ? 0.025F : 0.4F, 0xF5DFFF, 0xC449FF, 0x6420A0, 1.0F);
        poseStack.popPose();
    }

    public static TwinEyeDissolveRenderer<Retinazer> retinazer(EntityRendererProvider.Context context) {
        return new TwinEyeDissolveRenderer<>(context, Confluence.asResource("boss/retinazer"), Set.of("bone26", "bone31"), 0xFF3030);
    }

    public static TwinEyeDissolveRenderer<Spazmatism> spazmatism(EntityRendererProvider.Context context) {
        return new TwinEyeDissolveRenderer<>(
                context,
                Confluence.asResource("boss/spazmatism"),
                Set.of("bone", "bone31"),
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
        // 子骨骼切换发光或溶解层后，调用方传入的共享缓冲区可能已经结束。
        buffer = bufferSource.getBuffer(renderType);
        poseStack.pushPose();
        RenderUtils.prepMatrixForBone(poseStack, bone);
        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        if (!isReRender && animatable instanceof Retinazer && !bone.isHidden()) {
            VertexConsumer glowBuffer = null;
            for (int i = 0; i < bone.getCubes().size(); i++) {
                GeoCube cube = bone.getCubes().get(i);
                var size = cube.size();
                boolean negative = size.x < 0 || size.y < 0 || size.z < 0;
                boolean emissiveSurface = switch (bone.getName()) {
                    case "bone36" -> i == 5;
                    case "bone37" -> i == 0 || i == 17;
                    default -> false;
                };
                if (!negative && !emissiveSurface) continue;
                if (glowBuffer == null) {
                    glowBuffer = bufferSource.getBuffer(RenderStateShardAccessor.entityUnshadedGlow(getTextureLocation(animatable)));
                }
                poseStack.pushPose();
                renderCube(poseStack, cube, glowBuffer, LightTexture.FULL_BRIGHT, packedOverlay, 1, 1, 1, alpha);
                poseStack.popPose();
            }
            if (glowBuffer != null) {
                buffer = bufferSource.getBuffer(renderType);
            }
        }


        if (!isReRender) {
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }

        boolean selected = selectedDepth > 0
                || dissolveRoots.contains(bone.getName());
        float dissolveProgress = dissolveProgress(animatable, partialTick);
        if (!isReRender && selected && dissolveProgress >= 0.0F) {
            ResourceLocation texture = getTextureLocation(animatable);
            RenderType dissolveType = hasNegativeCubes(animatable)
                    ? RenderStateShardAccessor.entityTranslucentCullOverlay(texture) : RenderType.entityTranslucent(texture);
            VertexConsumer dissolveBuffer = bufferSource.getBuffer(dissolveType);
            renderDissolveCubesOfBone(poseStack, bone, dissolveBuffer, packedOverlay, dissolveProgress, animatable.tickCount + partialTick);
            buffer = bufferSource.getBuffer(renderType);
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
