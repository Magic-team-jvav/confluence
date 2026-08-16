package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.boss.AbstractTwinEye;
import org.confluence.mod.common.entity.boss.Retinazer;
import org.confluence.mod.common.entity.boss.Spazmatism;
import org.confluence.mod.common.entity.boss.TheTwins;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.UUID;

/// 双子魔眼主控实体的客户端视觉层。
///
/// <p>主控实体本身只负责生命、归属和结算，两只眼睛依然分别用自己的 Boss 模型渲染。
/// 这里补上 1.21 侧已有的“两眼连接线”表现，避免主控完全空渲染导致双子魔眼少一层视觉反馈。
/// 客户端不会直接持有服务端字段，因此优先使用已同步的所属 UUID 从附近实体中恢复两只眼的位置。</p>
public final class TheTwinsRenderer extends EntityRenderer<TheTwins> {
    private static final double SEARCH_RANGE = 128.0;
    private static final float MAIN_ALPHA = 0.95F;
    private static final float GLOW_ALPHA = 0.45F;

    public TheTwinsRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            @NotNull TheTwins entity,
            float entityYaw,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight) {
        AbstractTwinEye retinazer = findEye(entity, true);
        AbstractTwinEye spazmatism = findEye(entity, false);
        if (retinazer == null || spazmatism == null) {
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            return;
        }

        Vec3 origin = interpolatedPosition(entity, partialTick);
        Vec3 from = interpolatedPosition(retinazer, partialTick)
                .add(0.0, retinazer.getBbHeight() * 0.5F, 0.0)
                .subtract(origin);
        Vec3 to = interpolatedPosition(spazmatism, partialTick)
                .add(0.0, spazmatism.getBbHeight() * 0.5F, 0.0)
                .subtract(origin);

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
        drawLine(lines, pose.pose(), pose.normal(), from, to, 255, 216, 104, MAIN_ALPHA);
        drawOffsetLine(lines, pose.pose(), pose.normal(), from, to, 0.05F, 255, 138, 86, GLOW_ALPHA);
        drawOffsetLine(lines, pose.pose(), pose.normal(), from, to, -0.05F, 255, 138, 86, GLOW_ALPHA);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private static AbstractTwinEye findEye(TheTwins entity, boolean retinazer) {
        AbstractTwinEye direct = retinazer ? entity.getRetinazer() : entity.getSpazmatism();
        if (direct != null && direct.isAlive()) {
            return direct;
        }
        UUID owner = entity.getUUID();
        Class<? extends AbstractTwinEye> type = retinazer ? Retinazer.class : Spazmatism.class;
        return entity.level()
                .getEntitiesOfClass(type, entity.getBoundingBox().inflate(SEARCH_RANGE),
                        eye -> eye.isAlive() && owner.equals(eye.getMasterUUID()))
                .stream()
                .findFirst()
                .orElse(null);
    }

    private static Vec3 interpolatedPosition(AbstractTwinEye entity, float partialTick) {
        return new Vec3(
                Mth.lerp(partialTick, entity.xOld, entity.getX()),
                Mth.lerp(partialTick, entity.yOld, entity.getY()),
                Mth.lerp(partialTick, entity.zOld, entity.getZ()));
    }

    private static Vec3 interpolatedPosition(TheTwins entity, float partialTick) {
        return new Vec3(
                Mth.lerp(partialTick, entity.xOld, entity.getX()),
                Mth.lerp(partialTick, entity.yOld, entity.getY()),
                Mth.lerp(partialTick, entity.zOld, entity.getZ()));
    }

    private static void drawOffsetLine(
            VertexConsumer consumer,
            Matrix4f pose,
            Matrix3f normal,
            Vec3 from,
            Vec3 to,
            float offset,
            int red,
            int green,
            int blue,
            float alpha) {
        Vec3 direction = to.subtract(from);
        Vec3 side = direction.cross(new Vec3(0.0, 1.0, 0.0));
        if (side.lengthSqr() < 1.0E-4) {
            side = direction.cross(new Vec3(1.0, 0.0, 0.0));
        }
        side = side.normalize().scale(offset);
        drawLine(consumer, pose, normal, from.add(side), to.add(side), red, green, blue, alpha);
    }

    private static void drawLine(
            VertexConsumer consumer,
            Matrix4f pose,
            Matrix3f normal,
            Vec3 from,
            Vec3 to,
            int red,
            int green,
            int blue,
            float alpha) {
        Vec3 direction = to.subtract(from).normalize();
        addLineVertex(consumer, pose, normal, from, direction, red, green, blue, alpha);
        addLineVertex(consumer, pose, normal, to, direction, red, green, blue, alpha);
    }

    private static void addLineVertex(
            VertexConsumer consumer,
            Matrix4f pose,
            Matrix3f normal,
            Vec3 position,
            Vec3 lineNormal,
            int red,
            int green,
            int blue,
            float alpha) {
        consumer.vertex(pose, (float) position.x, (float) position.y, (float) position.z)
                .color(red, green, blue, (int) (alpha * 255.0F))
                .normal(normal, (float) lineNormal.x, (float) lineNormal.y, (float) lineNormal.z)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TheTwins entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
