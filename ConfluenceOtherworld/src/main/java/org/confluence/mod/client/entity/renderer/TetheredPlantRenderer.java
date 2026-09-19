package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.Snatcher;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TetheredPlantRenderer extends PlantHeadRenderer {
    private final ResourceLocation segmentModel;
    private final ResourceLocation segmentTexture;
    private final double segmentLength;
    private final double segmentCenterY;

    public TetheredPlantRenderer(EntityRendererProvider.Context context, EntityType<? extends Snatcher> type) {
        super(context, BuiltInRegistries.ENTITY_TYPE.getKey(type), headCenter(type), stemPoint(type));
        ResourceLocation path = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        segmentModel = Confluence.asResource("geo/entity/" + path.getPath() + "_segment.geo.json");
        segmentTexture = Confluence.asResource("textures/entity/" + path.getPath() + "_segment.png");
        boolean clinger = type == MonsterEntities.CLINGER.get();
        segmentLength = (clinger ? 15.0 : 17.0) / 16.0;
        segmentCenterY = (clinger ? 9.5 : 8.0) / 16.0;
    }

    private static Vec3 headCenter(EntityType<? extends Snatcher> type) {
        if (type == MonsterEntities.CLINGER.get()) return new Vec3(0, 9.5, 4);
        if (type == MonsterEntities.GIANT_FUNGI_BULB.get()) return new Vec3(0, 10, 3);
        return new Vec3(0, 8, 1);
    }

    private static Vec3 stemPoint(EntityType<? extends Snatcher> type) {
        if (type == MonsterEntities.CLINGER.get()) return new Vec3(0, 9.5, 8);
        if (type == MonsterEntities.GIANT_FUNGI_BULB.get()) return new Vec3(0, 10, 10);
        return new Vec3(0, 8, 6);
    }

    @Override
    public void render(Snatcher entity, float yaw, float partialTick, PoseStack poses, MultiBufferSource buffers, int light) {
        super.render(entity, yaw, partialTick, poses, buffers, light);
        if (!entity.isAnchored()) return;
        Vec3 origin = new Vec3(Mth.lerp(partialTick, entity.xOld, entity.getX()), Mth.lerp(partialTick, entity.yOld, entity.getY()), Mth.lerp(partialTick, entity.zOld, entity.getZ()));
        Vec3 start = stemPosition(entity, partialTick);
        Vec3 offset = entity.getAnchor().subtract(origin).subtract(start);
        if (offset.lengthSqr() < 1.0E-8) return;
        int count = Math.max(1, Mth.ceil(offset.length() / segmentLength));
        float lengthScale = (float) (offset.length() / count / segmentLength);
        Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0, 0, 1), offset.normalize().toVector3f());
        var model = getGeoModel().getBakedModel(segmentModel);
        RenderType type = RenderType.entityCutoutNoCull(segmentTexture);
        var vertices = buffers.getBuffer(type);
        int overlay = getPackedOverlay(entity, 0.0F, partialTick);
        for (int index = 0; index < count; index++) {
            Vec3 position = start.add(offset.scale((index + 0.5) / count));
            poses.pushPose();
            poses.translate(position.x, position.y, position.z);
            poses.mulPose(rotation);
            // 连接模型沿 Z 轴延伸，主干中心的 Z 坐标均为半像素。
            poses.scale(1.0F, 1.0F, lengthScale);
            poses.translate(0.0, -segmentCenterY, -0.5 / 16.0);
            for (var bone : model.topLevelBones())
                renderRecursively(poses, entity, bone, type, buffers, vertices, true, partialTick, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            poses.popPose();
        }
    }
}
