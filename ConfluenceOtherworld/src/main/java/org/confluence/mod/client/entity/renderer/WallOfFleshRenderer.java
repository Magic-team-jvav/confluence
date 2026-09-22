package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.entity.boss.WallOfFleshEye;
import org.confluence.mod.common.entity.boss.WallOfFleshPart;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.io.IOException;

/// 肉墙与眼嘴共享本体的插值变换；世界事件绘制绕过管理原点区段未编译的限制。
public final class WallOfFleshRenderer extends GeoEntityRenderer<WallOfFlesh> {
    private static final int GRID_WIDTH = 40;
    private static final int GRID_HEIGHT = 30;
    private static final float GRID_SPACING = 15;
    private static final float WALL_THICKNESS = 2;
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/entity/boss/wall_of_flesh_background");
    private static final ResourceLocation MODEL_TEXTURE = Confluence.asResource("textures/entity/boss/wall_of_flesh.png");
    private BakedGeoModel cachedModel;
    private GeoBone eye;
    private GeoBone eyeHead;
    private GeoBone mouth;

    public WallOfFleshRenderer(EntityRendererProvider.Context context) {
        super(context, new GeoNormalModel<>(Confluence.asResource("boss/wall_of_flesh"), false));
        shadowRadius = 0;
        Minecraft.getInstance().getTextureManager().register(BACKGROUND, new SimpleTexture(MODEL_TEXTURE) {
            @Override
            protected TextureImage getTextureImage(ResourceManager resources) {
                try (NativeImage atlas = super.getTextureImage(resources).getImage()) {
                    int width = atlas.getWidth() * 80 / 512;
                    int height = atlas.getHeight() * 80 / 512;
                    NativeImage tile = new NativeImage(width * 2, height, false);
                    atlas.copyRect(tile, 0, 0, 0, 0, width, height, false, false);
                    atlas.copyRect(tile, atlas.getWidth() * 81 / 512, 0, width, 0, width, height, false, false);
                    return new TextureImage(new TextureMetadataSection(false, false), tile);
                } catch (IOException exception) {
                    return new TextureImage(exception);
                }
            }
        });
    }

    private void prepareModels() {
        BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));
        if (model == cachedModel) return;
        cachedModel = model;
        eye = copyBone(getGeoModel().getBone("bone_eye").orElseThrow(), null);
        mouth = copyBone(getGeoModel().getBone("bone_mouth").orElseThrow(), null);
        eyeHead = eye.getChildBones().stream().filter(bone -> "Head_eye".equals(bone.getName())).findFirst().orElse(null);
    }

    /// 标准实体入口不重复绘制；墙面可能可见而管理原点仍在未编译区段中。
    @Override
    public void render(WallOfFlesh wall, float yaw, float partialTick, PoseStack poses, MultiBufferSource buffers, int light) {}

    public static void renderWalls(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        Vec3 camera = event.getCamera().getPosition();
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (!(entity instanceof WallOfFlesh wall) || wall.isRemoved()) continue;
            if (!(minecraft.getEntityRenderDispatcher().getRenderer(wall) instanceof WallOfFleshRenderer renderer))
                continue;
            renderer.renderWall(wall, event.getPoseStack(), buffers, event.getFrustum(), camera, event.getPartialTick());
        }
    }

    private void renderWall(WallOfFlesh wall, PoseStack poses, MultiBufferSource buffers,
                            Frustum frustum, Vec3 camera, float partialTick) {
        animatable = wall;
        prepareModels();
        Vec3 origin = wall.getPosition(partialTick);
        Vec3 forward = Vec3.atLowerCornerOf(wall.getDirection().getNormal());
        Vec3 lateral = new Vec3(-forward.z, 0, forward.x);
        float scale = wall.getScale();
        RenderType type = getRenderType(wall, getTextureLocation(wall), buffers, partialTick);
        int light = Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(wall, partialTick);
        poses.pushPose();
        poses.translate(origin.x - camera.x, origin.y - camera.y, origin.z - camera.z);
        poses.scale(scale, scale, scale);
        poses.mulPose(Axis.YP.rotationDegrees(180 - wall.getDirection().toYRot()));
        VertexConsumer background = buffers.getBuffer(RenderType.entityCutout(BACKGROUND));
        float halfWidth = GRID_WIDTH * GRID_SPACING * 0.5F;
        float halfHeight = GRID_HEIGHT * GRID_SPACING * 0.5F;
        int overlay = getPackedOverlay(wall, 0, partialTick);
        for (int x = 0; x < GRID_WIDTH; x++) {
            float left = x * GRID_SPACING - halfWidth;
            float right = left + GRID_SPACING;
            for (int y = 0; y < GRID_HEIGHT; y++) {
                float bottom = y * GRID_SPACING - halfHeight;
                float top = bottom + GRID_SPACING;
                Vec3 center = origin.add(lateral.scale((left + GRID_SPACING * 0.5F) * scale)).add(0, (bottom + GRID_SPACING * 0.5F) * scale, 0);
                if (!frustum.isVisible(AABB.ofSize(center, GRID_SPACING * scale, GRID_SPACING * scale, GRID_SPACING * scale)))
                    continue;
                /// 同一布局种子与网格坐标决定纹理，视角、帧数和多人加入顺序均不影响结果。
                long variation = Mth.getSeed(x, (int) wall.getLayoutSeed(), y) ^ (wall.getLayoutSeed() >>> 32);
                /// 普通墙面与脓包纹理权重为 6:1，按布局种子固定每格的选择。
                int variant = (variation >>> 16) % 7 == 0 ? 1 : 0;
                backgroundFace(poses, background, left, bottom, right, top, 0, 0, false, variant, light, overlay);
                backgroundFace(poses, background, left, bottom, right, top, 0, WALL_THICKNESS, true, variant, light, overlay);
                /// 仅封整个墙体的外边缘，不生成网格之间不可见的内部面。
                if (x == 0)
                    backgroundFace(poses, background, 0, bottom, WALL_THICKNESS, top, 1, left, true, variant, light, overlay);
                if (x == GRID_WIDTH - 1)
                    backgroundFace(poses, background, 0, bottom, WALL_THICKNESS, top, 1, right, false, variant, light, overlay);
                if (y == 0)
                    backgroundFace(poses, background, left, 0, right, WALL_THICKNESS, 2, bottom, true, variant, light, overlay);
                if (y == GRID_HEIGHT - 1)
                    backgroundFace(poses, background, left, 0, right, WALL_THICKNESS, 2, top, false, variant, light, overlay);
            }
        }
        VertexConsumer vertices = buffers.getBuffer(type);
        for (WallOfFleshPart part : wall.getParts()) {
            Vec3 offset = wall.getLocalOffset(part);
            Vec3 center = origin.add(lateral.scale(offset.x)).add(0, offset.y, 0).add(forward.scale(offset.z));
            if (!frustum.isVisible(AABB.ofSize(center, 12 * scale, 12 * scale, 12 * scale)))
                continue;
            if (part instanceof WallOfFleshEye && eyeHead != null) updateEye(part, partialTick);
            poses.pushPose();
            poses.translate(offset.x / scale, offset.y / scale, -offset.z / scale);
            double pivotY = part.getBbHeight() / scale * 0.5;
            poses.translate(0, pivotY, 0);
            poses.mulPose(Axis.ZP.rotationDegrees(part.getVisualRoll()));
            poses.translate(0, -pivotY, 0);
            super.renderRecursively(poses, wall, part instanceof WallOfFleshEye ? eye : mouth,
                    type, buffers, vertices, false, partialTick, light,
                    getPackedOverlay(wall, 0, partialTick), 1, 1, 1, 1);
            poses.popPose();
        }
        poses.popPose();
    }

    /// axis=0 为前后面，1 为左右封边，2 为上下封边；反向顶点顺序保证外表面朝外。
    private static void backgroundFace(PoseStack poses, VertexConsumer buffer, float left, float bottom,
                                       float right, float top, int axis, float plane, boolean reverse,
                                       int variant, int light, int overlay) {
        float u0 = variant * 0.5F;
        float u1 = u0 + 0.5F;
        backgroundVertex(poses, buffer, left, bottom, axis, plane, u0, 1, reverse, light, overlay);
        if (reverse) {
            backgroundVertex(poses, buffer, right, bottom, axis, plane, u1, 1, true, light, overlay);
            backgroundVertex(poses, buffer, right, top, axis, plane, u1, 0, true, light, overlay);
            backgroundVertex(poses, buffer, left, top, axis, plane, u0, 0, true, light, overlay);
        } else {
            backgroundVertex(poses, buffer, left, top, axis, plane, u0, 0, false, light, overlay);
            backgroundVertex(poses, buffer, right, top, axis, plane, u1, 0, false, light, overlay);
            backgroundVertex(poses, buffer, right, bottom, axis, plane, u1, 1, false, light, overlay);
        }
    }

    private static void backgroundVertex(PoseStack poses, VertexConsumer buffer, float a, float b,
                                         int axis, float plane, float u, float v, boolean reverse,
                                         int light, int overlay) {
        float x = axis == 1 ? plane : a;
        float y = axis == 2 ? plane : b;
        float z = axis == 0 ? plane : axis == 1 ? a : b;
        float sign = reverse ? -1 : 1;
        buffer.vertex(poses.last().pose(), x, y, z).color(255, 255, 255, 255)
                .uv(u, v).overlayCoords(overlay).uv2(light)
                .normal(poses.last().normal(), axis == 1 ? sign : 0, axis == 2 ? sign : 0, axis == 0 ? -sign : 0).endVertex();
    }

    /// 保留模板子骨骼局部变换，只替换顶层模板的放置原点；不修改 GeckoLib 的共享资源。
    private static GeoBone copyBone(GeoBone template, GeoBone parent) {
        GeoBone copy = new GeoBone(parent, template.getName(), template.getMirror(), template.getInflate(), template.shouldNeverRender(), template.getReset());
        if (parent != null) {
            copy.setPosX(template.getPosX());
            copy.setPosY(template.getPosY());
            copy.setPosZ(template.getPosZ());
            copy.setPivotX(template.getPivotX());
            copy.setPivotY(template.getPivotY());
            copy.setPivotZ(template.getPivotZ());
        }
        copy.setRotX(template.getRotX());
        copy.setRotY(template.getRotY());
        copy.setRotZ(template.getRotZ());
        copy.updateScale(template.getScaleX(), template.getScaleY(), template.getScaleZ());
        copy.getCubes().addAll(template.getCubes());
        for (GeoBone child : template.getChildBones())
            copy.getChildBones().add(copyBone(child, copy));
        return copy;
    }

    private void updateEye(WallOfFleshPart part, float partialTick) {
        LivingEntity target = part.getPartTarget();
        eyeHead.setRotX(0);
        eyeHead.setRotY(0);
        if (target == null) return;
        Vec3 difference = target.getPosition(partialTick).add(0, target.getEyeHeight(), 0)
                .subtract(part.getPosition(partialTick).add(0, part.getBbHeight() * 0.5, 0));
        Vec3 forward = Vec3.atLowerCornerOf(part.getParent().getDirection().getNormal());
        double dot = forward.x * difference.x + forward.z * difference.z;
        if (dot <= 0) return;
        float yaw = (float) Math.atan2(forward.x * difference.z - forward.z * difference.x, dot);
        float pitch = (float) Math.atan2(difference.y, difference.horizontalDistance());
        eyeHead.setRotY(Mth.clamp(-yaw, -Mth.PI / 3, Mth.PI / 3));
        eyeHead.setRotX(Mth.clamp(pitch, -Mth.PI / 4, Mth.PI / 4));
    }
}
