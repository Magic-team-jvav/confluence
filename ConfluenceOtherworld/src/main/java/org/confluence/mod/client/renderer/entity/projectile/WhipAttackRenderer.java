package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.whip.WhipAppearance;
import org.confluence.mod.api.whip.WhipSegment;
import org.confluence.mod.client.renderer.entity.TetherRenderHelper;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;
import org.confluence.mod.common.item.whip.BaseWhipItem;
import org.confluence.mod.network.c2s.WhipPlaybackCompletePacketC2S;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

import java.util.List;

// 沿攻击曲线按声明顺序绘制多个分段层；手柄仍由普通物品 JSON 渲染。
// 分段可选择固定像素间距或固定数量，并可附加鞭梢与颜色曲线。
public final class WhipAttackRenderer extends EntityRenderer<WhipAttackEntity> {
    // 与 1.21 保持一致：十六像素长的标准分段显示为半格。
    private static final float MODEL_SCALE = 0.5F;
    private static final double MODEL_PIXEL_SIZE = MODEL_SCALE / 16.0;

    public WhipAttackRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WhipAttackEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        ItemStack weapon = entity.weapon();
        if (entity.isRemoved()) return;
        if (entity.swingProgress(partialTick) >= 1.0F) {
            if (entity.getOwner() == Minecraft.getInstance().player && entity.reportPlaybackComplete()) {
                Confluence.NETWORK_HANDLER.sendToServer(new WhipPlaybackCompletePacketC2S(entity.getId()));
            }
            return;
        }
        List<Vec3> points = entity.sampleWorldPoints(partialTick);
        if (weapon.isEmpty() || points.size() < 2) {
            return;
        }
        if (!(weapon.getItem() instanceof BaseWhipItem whip)) {
            return;
        }
        if (points.get(0).distanceToSqr(points.get(points.size() - 1)) < 1.0E-6) return;
        Vec3 origin = entity.getPosition(partialTick);
        if (entity.getOwner() instanceof Player player) {
            Vec3 offset = TetherRenderHelper.handPosition(entityRenderDispatcher, player, entity.attackArm(), partialTick).subtract(points.get(0));
            // 曲线整体平移等价于反向移动渲染原点，避免每帧复制所有采样点。
            origin = origin.subtract(offset);
        }
        Vec3 widthAxis = entity.swingRight();
        WhipAppearance appearance = whip.appearance();
        if (!appearance.segments().isEmpty()) {
            WhipPolylineSamples samples = WhipPolylineSamples.of(points);
            for (WhipSegment segment : appearance.segments()) {
                renderLayer(weapon, segment, samples, origin, widthAxis, poseStack, buffers, packedLight);
            }
        }
        if (appearance.lineColor() != null) {
            renderCurveLine(points, origin, appearance.lineColor(), poseStack, buffers);
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }


    private static void renderLayer(ItemStack weapon, WhipSegment segment, WhipPolylineSamples curve, Vec3 origin, Vec3 widthAxis, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        List<WhipPolylineSamples.Sample> positions =
                segment.mode() == WhipSegment.Mode.FIXED_SPACING
                        ? curve.fixedSpacing(segment.value() * MODEL_PIXEL_SIZE)
                        : curve.fixedCount(segment.value());
        BakedModel body = Minecraft.getInstance().getModelManager().getModel(segment.model());
        boolean hasTip = segment.tipModel() != null;
        WhipPolylineSamples.Sample tip = hasTip ? curve.tip() : null;
        int bodyCount = positions.size();
        if (hasTip && bodyCount > 0 && positions.get(bodyCount - 1).position().distanceToSqr(tip.position()) <= 1.0E-10) {
            bodyCount--;
        }
        Quaternionf orientation = null;
        Vec3 previousDirection = null;
        Vec3 previousPosition = curve.start();
        // 沿曲线传递上一节的朝向，不再为每节叠加固定扭转。
        for (int index = 0; index < bodyCount; index++) {
            WhipPolylineSamples.Sample sample = positions.get(index);
            Vec3 edge = previousPosition.subtract(sample.position());
            double length = edge.length();
            if (length < 1.0E-8) continue;
            Vec3 direction = edge.scale(1.0 / length);
            orientation = segmentOrientation(direction, previousDirection, orientation, widthAxis);
            renderSegment(weapon, body, sample.position().subtract(origin), orientation, length, poseStack, buffers, packedLight);
            previousDirection = direction;
            previousPosition = sample.position();
        }
        if (hasTip) {
            Vec3 edge = previousPosition.subtract(tip.position());
            double length = edge.length();
            if (length > 1.0E-8) {
                orientation = segmentOrientation(edge.scale(1.0 / length), previousDirection, orientation, widthAxis);
                BakedModel tipModel = Minecraft.getInstance().getModelManager().getModel(segment.tipModel());
                renderSegment(weapon, tipModel, tip.position().subtract(origin), orientation, length, poseStack, buffers, packedLight);
            }
        }
    }

    private static Quaternionf segmentOrientation(Vec3 direction, Vec3 previousDirection, Quaternionf previous, Vec3 widthAxis) {
        if (previous != null && previousDirection.dot(direction) > -0.9999) {
            return new Quaternionf().rotationTo((float) previousDirection.x, (float) previousDirection.y, (float) previousDirection.z, (float) direction.x, (float) direction.y, (float) direction.z).mul(previous).normalize();
        }
        Vec3 width = widthAxis.subtract(direction.scale(widthAxis.dot(direction)));
        if (width.lengthSqr() < 1.0E-6) {
            Vec3 fallback = Math.abs(direction.y) < 0.9 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(0.0, 0.0, 1.0);
            width = fallback.subtract(direction.scale(fallback.dot(direction)));
        }
        width = width.normalize();
        Vec3 normal = width.cross(direction).normalize();
        return new Quaternionf().setFromNormalized(new Matrix3f((float) width.x, (float) width.y, (float) width.z, (float) direction.x, (float) direction.y, (float) direction.z, (float) normal.x, (float) normal.y, (float) normal.z));
    }

    private static void renderSegment(ItemStack weapon, BakedModel model, Vec3 offset, Quaternionf orientation, double length, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(orientation);
        // 分段模型统一以 Y=0..16、X/Z 围绕方块中心建模。
        poseStack.scale(MODEL_SCALE, (float) length, MODEL_SCALE);
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        for (RenderType renderType : model.getRenderTypes(weapon, false)) {
            VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffers, renderType, false, weapon.hasFoil());
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, weapon, packedLight, OverlayTexture.NO_OVERLAY, poseStack, consumer);
        }
        poseStack.popPose();
    }

    private static void renderCurveLine(List<Vec3> points, Vec3 origin, int color, PoseStack poseStack, MultiBufferSource buffers) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.lineStrip());
        PoseStack.Pose pose = poseStack.last();
        for (int index = 0; index < points.size(); index++) {
            Vec3 point = points.get(index).subtract(origin);
            Vec3 tangent;
            if (index + 1 < points.size()) {
                tangent = points.get(index + 1).subtract(points.get(index));
            } else {
                tangent = points.get(index).subtract(points.get(index - 1));
            }
            if (tangent.lengthSqr() <= 1.0E-10) {
                continue;
            }
            tangent = tangent.normalize();
            consumer.vertex(pose.pose(), (float) point.x, (float) point.y, (float) point.z)
                    .color(color)
                    .normal(pose.normal(), (float) tangent.x, (float) tangent.y, (float) tangent.z)
                    .endVertex();
        }
    }

    @Override
    public boolean shouldRender(WhipAttackEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(WhipAttackEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
