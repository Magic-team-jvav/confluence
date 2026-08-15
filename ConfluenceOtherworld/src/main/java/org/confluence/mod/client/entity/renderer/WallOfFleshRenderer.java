package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.entity.boss.WallOfFleshEye;
import org.confluence.mod.common.entity.boss.WallOfFleshMouth;
import org.confluence.mod.common.entity.boss.WallOfFleshPart;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;

/**
 * 根据血肉墙的运行时部件建立完整墙面模型。
 *
 * <p>资源中的五种血肉格、眼睛和嘴只是模板，本渲染器把模板复制到墙面网格与服务端
 * 已生成的部件位置。碰撞与攻击仍由对应部件实体负责，渲染骨骼不参与任何服务端判定。</p>
 */
public final class WallOfFleshRenderer
        extends BossGeoRenderer<WallOfFlesh> {
    private static final String[] WALL_VARIANTS = {
            "bone0", "bone1", "bone2", "bone3", "bone4"
    };
    private static final int GRID_WIDTH = 60;
    private static final int GRID_HEIGHT = 40;
    private static final float GRID_SPACING = 15.0F;
    private static final float GECKO_SCALE = 16.0F;

    private int cachedWallId = Integer.MIN_VALUE;
    private int cachedPartSignature = Integer.MIN_VALUE;

    public WallOfFleshRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/wall_of_flesh"));
        setShadowRadius(0.0F);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            WallOfFlesh wall,
            BakedGeoModel bakedModel,
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
        rebuildModelIfNeeded(wall, bakedModel);
        super.preRender(
                poseStack,
                wall,
                bakedModel,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha);
    }

    @Override
    public void renderRecursively(
            PoseStack poseStack,
            WallOfFlesh wall,
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
        if ("Head_eye".equals(bone.getName())) {
            updateEyeRotation(wall, bone, partialTick);
        }
        super.renderRecursively(
                poseStack,
                wall,
                bone,
                renderType,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha);
    }

    private void rebuildModelIfNeeded(
            WallOfFlesh wall, BakedGeoModel bakedModel) {
        List<WallOfFleshPart> parts = wall.getSubEntities().stream()
                .filter(WallOfFleshPart.class::isInstance)
                .map(WallOfFleshPart.class::cast)
                .filter(WallOfFleshPart::isAlive)
                .toList();
        int signature = parts.stream()
                .mapToInt(part -> 31 * part.getId()
                        + part.getType().hashCode())
                .reduce(1, (left, right) -> 31 * left + right);
        if (cachedWallId == wall.getId()
                && cachedPartSignature == signature) {
            return;
        }

        GeoModel<WallOfFlesh> model = getGeoModel();
        GeoBone root = model.getBone("All").orElse(null);
        if (root == null) {
            return;
        }
        GeoBone[] variants = new GeoBone[WALL_VARIANTS.length];
        for (int index = 0; index < WALL_VARIANTS.length; index++) {
            variants[index] = model.getBone(
                    WALL_VARIANTS[index]).orElse(null);
        }
        GeoBone eyeTemplate = model.getBone("bone_eye").orElse(null);
        GeoBone mouthTemplate = model.getBone("bone_mouth").orElse(null);

        root.getChildBones().clear();
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                GeoBone template = variants[pickWallVariant(x, y)];
                if (template == null) {
                    continue;
                }
                Vec3 offset = new Vec3(
                        -(x - GRID_WIDTH * 0.5)
                                * GRID_SPACING * GECKO_SCALE,
                        (y - GRID_HEIGHT * 0.5)
                                * GRID_SPACING * GECKO_SCALE,
                        0.0);
                root.getChildBones().add(copyBone(
                        template,
                        offset,
                        "wall_grid_" + x + '_' + y,
                        root));
            }
        }

        for (WallOfFleshPart part : parts) {
            GeoBone template = part instanceof WallOfFleshEye
                    ? eyeTemplate
                    : part instanceof WallOfFleshMouth
                    ? mouthTemplate : null;
            if (template == null) {
                continue;
            }
            Vec3 local = wall.getLocalOffset(part);
            Vec3 renderOffset = new Vec3(
                    -local.x * GECKO_SCALE,
                    local.y * GECKO_SCALE,
                    local.z * GECKO_SCALE);
            String name = part instanceof WallOfFleshEye
                    ? "wall_eye_" + part.getId()
                    : "wall_mouth_" + part.getId();
            root.getChildBones().add(copyBone(
                    template, renderOffset, name, root));
        }

        if (!bakedModel.topLevelBones().contains(root)) {
            bakedModel.topLevelBones().clear();
            bakedModel.topLevelBones().add(root);
        }
        cachedWallId = wall.getId();
        cachedPartSignature = signature;
    }

    private static int pickWallVariant(int x, int y) {
        int value = Math.floorMod(x * 73428767 ^ y * 912931, 9);
        return value < 4 ? value : 4;
    }

    private static GeoBone copyBone(
            GeoBone template,
            Vec3 offset,
            String name,
            GeoBone parent) {
        GeoBone copy = new GeoBone(
                parent,
                name,
                template.getMirror(),
                template.getInflate(),
                template.shouldNeverRender(),
                template.getReset());
        copy.setPosX((float) offset.x);
        copy.setPosY((float) offset.y);
        copy.setPosZ((float) offset.z);
        copy.setPivotX((float) offset.x);
        copy.setPivotY((float) offset.y);
        copy.setPivotZ((float) offset.z);
        copy.setRotX(template.getRotX());
        copy.setRotY(template.getRotY());
        copy.setRotZ(template.getRotZ());
        copy.updateScale(
                template.getScaleX(),
                template.getScaleY(),
                template.getScaleZ());
        copy.getCubes().addAll(template.getCubes());
        for (GeoBone child : template.getChildBones()) {
            copy.getChildBones().add(copyChildBone(child, copy));
        }
        return copy;
    }

    private static GeoBone copyChildBone(
            GeoBone template, GeoBone parent) {
        GeoBone copy = new GeoBone(
                parent,
                template.getName(),
                template.getMirror(),
                template.getInflate(),
                template.shouldNeverRender(),
                template.getReset());
        copy.setPosX(template.getPosX());
        copy.setPosY(template.getPosY());
        copy.setPosZ(template.getPosZ());
        copy.setPivotX(template.getPivotX());
        copy.setPivotY(template.getPivotY());
        copy.setPivotZ(template.getPivotZ());
        copy.setRotX(template.getRotX());
        copy.setRotY(template.getRotY());
        copy.setRotZ(template.getRotZ());
        copy.updateScale(
                template.getScaleX(),
                template.getScaleY(),
                template.getScaleZ());
        copy.getCubes().addAll(template.getCubes());
        for (GeoBone child : template.getChildBones()) {
            copy.getChildBones().add(copyChildBone(child, copy));
        }
        return copy;
    }

    private static void updateEyeRotation(
            WallOfFlesh wall, GeoBone eyeBone, float partialTick) {
        GeoBone root = eyeBone.getParent();
        if (root == null || !root.getName().startsWith("wall_eye_")) {
            return;
        }
        int entityId;
        try {
            entityId = Integer.parseInt(
                    root.getName().substring("wall_eye_".length()));
        } catch (NumberFormatException ignored) {
            return;
        }
        WallOfFleshEye eye = wall.getSubEntities().stream()
                .filter(WallOfFleshEye.class::isInstance)
                .map(WallOfFleshEye.class::cast)
                .filter(part -> part.getId() == entityId)
                .findFirst()
                .orElse(null);
        LivingEntity target = eye == null ? null : eye.getPartTarget();
        if (target == null) {
            eyeBone.setRotX(Mth.lerp(0.15F, eyeBone.getRotX(), 0.0F));
            eyeBone.setRotY(Mth.lerp(0.15F, eyeBone.getRotY(), 0.0F));
            return;
        }

        Vec3 eyePosition = new Vec3(eye.xo, eye.yo, eye.zo)
                .lerp(eye.position(), partialTick)
                .add(0.0, eye.getBbHeight() * 0.5, 0.0);
        Vec3 targetPosition = new Vec3(target.xo, target.yo, target.zo)
                .lerp(target.position(), partialTick)
                .add(0.0, target.getEyeHeight(), 0.0);
        Vec3 difference = targetPosition.subtract(eyePosition);
        Vec3 horizontal = new Vec3(difference.x, 0.0, difference.z);
        if (horizontal.lengthSqr() < 1.0E-6
                || wall.getForwardVector().dot(horizontal.normalize()) < 0.0) {
            return;
        }
        float yaw = (float) Math.atan2(
                wall.getForwardVector().x * difference.z
                        - wall.getForwardVector().z * difference.x,
                wall.getForwardVector().x * difference.x
                        + wall.getForwardVector().z * difference.z);
        float pitch = (float) Math.atan2(
                difference.y, horizontal.length());
        eyeBone.setRotY(Mth.clamp(
                -yaw,
                (float) Math.toRadians(-60.0),
                (float) Math.toRadians(60.0)));
        eyeBone.setRotX(Mth.clamp(
                pitch,
                (float) Math.toRadians(-45.0),
                (float) Math.toRadians(45.0)));
    }
}
