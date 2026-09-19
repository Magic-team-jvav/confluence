package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.Snatcher;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 头部和藤蔓共用模型中心、茎部接点及旋转，模型坐标以像素声明。
public abstract class PlantHeadRenderer extends GeoNormalRenderer<Snatcher> {
    private final Vec3 center;
    private final Vec3 stem;

    protected PlantHeadRenderer(EntityRendererProvider.Context context, ResourceLocation resource, Vec3 centerPixels, Vec3 stemPixels) {
        super(context, resource, false, 1.0F, 0.0F);
        center = centerPixels.scale(1.0 / 16.0);
        stem = stemPixels.scale(1.0 / 16.0);
    }

    private Quaternionf headRotation(Snatcher entity, float partialTick) {
        float yaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
        return new Quaternionf().rotationY((180.0F - yaw) * Mth.DEG_TO_RAD)
                .rotateX(-pitch * Mth.DEG_TO_RAD);
    }

    @Override
    protected void applyRotations(Snatcher entity, PoseStack poses, float age, float yaw, float partialTick) {
        float scale = entity.getScale();
        poses.translate(0.0, entity.getBbHeight() * 0.5 / scale, 0.0);
        poses.mulPose(headRotation(entity, partialTick));
        poses.translate(-center.x, -center.y - 0.01, -center.z);
    }

    protected final Vec3 stemPosition(Snatcher entity, float partialTick) {
        Vector3f offset = stem.subtract(center).toVector3f();
        headRotation(entity, partialTick).transform(offset);
        return new Vec3(offset).scale(entity.getScale()).add(0.0, entity.getBbHeight() * 0.5, 0.0);
    }
}
