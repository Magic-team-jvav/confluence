package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.whip.WhipAppearance;
import org.confluence.mod.api.whip.WhipSegment;
import org.confluence.mod.api.whip.curve.WhipCurveSampler;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;
import org.confluence.mod.common.item.whip.BaseWhipItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 沿攻击实体的曲线绘制可组合鞭子外观。
 *
 * <p>玩家手中的普通物品 JSON 模型负责显示手柄，本渲染器只处理手柄之后的曲线。
 * 每个外观分段独立选择固定像素间距或固定数量，并按声明顺序叠加；鞭梢和颜色线均为
 * 可选项。所有模型仍通过原版烘焙模型渲染，因此模型自身的面与背面剔除规则不会丢失。</p>
 */
public final class WhipAttackRenderer extends EntityRenderer<WhipAttackEntity> {
    /**
     * 与 1.21 鞭子渲染器保持一致，物品模型按半格比例绘制。
     */
    private static final float MODEL_SCALE = 0.5F;
    private static final float ROLL_DEGREES_PER_SEGMENT = 10.0F;
    /*
     * 模型经过 0.5 缩放后，一个 JSON 像素只占世界中的 1/32 格。
     * 采样距离必须使用同一换算，否则四像素长的鞭节之间会留下半段空隙。
     */
    private static final double RENDERED_PIXELS_PER_BLOCK = 32.0;

    public WhipAttackRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            WhipAttackEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight
    ) {
        ItemStack weapon = entity.weapon();
        List<Vec3> points = alignCurveToHand(
                entity,
                entity.sampleRenderControlPoints(partialTick),
                partialTick);
        if (weapon.isEmpty() || points.size() < 2) {
            return;
        }
        if (!(weapon.getItem() instanceof BaseWhipItem whip)) {
            return;
        }
        WhipAppearance appearance = whip.appearance();
        Vec3 origin = entity.getPosition(partialTick);

        for (WhipSegment segment : appearance.segments()) {
            renderLayer(
                    weapon, segment, points, origin,
                    poseStack, buffers, packedLight);
        }
        if (appearance.optionalLineColor().isPresent()) {
            renderCurveLine(
                    points,
                    origin,
                    appearance.optionalLineColor().getAsInt(),
                    poseStack,
                    buffers);
        }
        super.render(
                entity, entityYaw, partialTick,
                poseStack, buffers, packedLight);
    }

    /**
     * 把玩家当前持手作为客户端样条根部。
     *
     * <p>服务端轨迹仍使用实体生成时冻结的世界坐标，本方法只修正客户端显示。1.21
     * 是把玩家当前手部作为样条控制点参与插值，而不是对已经采样完的折线硬改第一个点。
     * 这里保持同样的时机，避免第三人称看起来从错误手侧甩出。</p>
     */
    private List<Vec3> alignCurveToHand(
            WhipAttackEntity entity,
            List<Vec3> controlPoints,
            float partialTick
    ) {
        if (controlPoints.isEmpty()
                || !(entity.getOwner() instanceof Player player)) {
            return controlPoints;
        }
        float attack = player.getAttackAnim(partialTick);
        float swing = Mth.sin(Mth.sqrt(attack) * Mth.PI);
        Vec3 hand = player == Minecraft.getInstance().player
                && entityRenderDispatcher.options.getCameraType().isFirstPerson()
                ? getFirstPersonHandPosition(
                player, entity.attackArm(), swing, partialTick)
                : getThirdPersonHandPosition(
                player, entity.attackArm(), partialTick);
        if (controlPoints.size() == 1) {
            return List.of(hand);
        }
        ArrayList<Vec3> result = new ArrayList<>(controlPoints.size());
        result.add(hand);
        for (int index = 1; index < controlPoints.size(); index++) {
            result.add(controlPoints.get(index));
        }
        return WhipCurveSampler.sampleControlPoints(result, WhipAttackEntity.RENDER_SEGMENT_SPACING);
    }

    /**
     * 按 1.21 的视场角和近裁剪面换算第一人称持鞭手位置。
     */
    private Vec3 getFirstPersonHandPosition(
            Player player,
            HumanoidArm arm,
            float swing,
            float partialTick
    ) {
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        double fovScale = 960.0
                / entityRenderDispatcher.options.fov().get();
        Vec3 offset = entityRenderDispatcher.camera.getNearPlane()
                .getPointOnPlane(side * 0.525F, -0.1F)
                .scale(fovScale)
                .yRot(swing * 0.5F - 1.0F)
                .xRot(-swing * 0.7F);
        return player.getPosition(partialTick)
                .add(offset)
                .add(0.0, player.getEyeHeight() * 0.8F, 0.0);
    }

    /**
     * 取得第三人称挥鞭手位置，并和服务端生成锚点保持相同的左右手约定。
     */
    private static Vec3 getThirdPersonHandPosition(
            Player player,
            HumanoidArm arm,
            float partialTick
    ) {
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float bodyYaw = Mth.lerp(
                partialTick, player.yBodyRotO, player.yBodyRot)
                * Mth.DEG_TO_RAD;
        double sin = Mth.sin(bodyYaw);
        double cos = Mth.cos(bodyYaw);
        float scale = player.getScale();
        double sideOffset = side * 0.5 * scale;
        double crouchOffset = player.isCrouching() ? -0.1875 : 0.0;
        return player.getEyePosition(partialTick).add(
                -cos * sideOffset,
                crouchOffset - scale,
                -sin * sideOffset);
    }

    private static void renderLayer(
            ItemStack weapon,
            WhipSegment segment,
            List<Vec3> curve,
            Vec3 origin,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight
    ) {
        List<WhipPolylineSamples.Sample> samples =
                segment.mode() == WhipSegment.Mode.FIXED_SPACING
                        ? WhipPolylineSamples.fixedSpacing(
                        curve,
                        segment.value() / RENDERED_PIXELS_PER_BLOCK)
                        : WhipPolylineSamples.fixedCount(
                        curve, segment.value());
        BakedModel body = WhipSegmentModels.model(segment.model());
        boolean hasTip = segment.tipModel() != null;
        WhipPolylineSamples.Sample tip = hasTip
                ? WhipPolylineSamples.tip(curve)
                : null;
        int bodyCount = samples.size();
        if (hasTip
                && bodyCount > 0
                && samples.get(bodyCount - 1).position()
                .distanceToSqr(tip.position()) <= 1.0E-10) {
            bodyCount--;
        }
        /* fixedSpacing/fixedCount 已从第一个有效间隔开始取样，不包含曲线根点。 */
        for (int index = 0; index < bodyCount; index++) {
            WhipPolylineSamples.Sample sample = samples.get(index);
            renderSegment(
                    weapon,
                    body,
                    sample.position().subtract(origin),
                    sample.tangent(),
                    index + 1,
                    poseStack,
                    buffers,
                    packedLight);
        }
        if (hasTip) {
            renderSegment(
                    weapon,
                    WhipSegmentModels.model(segment.tipModel()),
                    tip.position().subtract(origin),
                    tip.tangent(),
                    bodyCount + 1,
                    poseStack,
                    buffers,
                    packedLight);
        }
    }

    private static void renderSegment(
            ItemStack weapon,
            BakedModel model,
            Vec3 offset,
            Vec3 tangent,
            int index,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight
    ) {
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);

        float yaw = (float) (Math.PI - Math.atan2(tangent.z, tangent.x));
        float pitch = (float) -Math.atan2(
                tangent.y, tangent.horizontalDistance());
        poseStack.mulPose(Axis.YP.rotation(yaw));
        poseStack.mulPose(Axis.ZP.rotation(
                pitch + (float) Math.PI * 0.5F));
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                index * ROLL_DEGREES_PER_SEGMENT));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        for (RenderType renderType : model.getRenderTypes(weapon, false)) {
            VertexConsumer consumer = ItemRenderer.getFoilBuffer(
                    buffers,
                    renderType,
                    false,
                    weapon.hasFoil());
            Minecraft.getInstance().getItemRenderer().renderModelLists(
                    model,
                    weapon,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    consumer
            );
        }
        poseStack.popPose();
    }

    private static void renderCurveLine(
            List<Vec3> points,
            Vec3 origin,
            int color,
            PoseStack poseStack,
            MultiBufferSource buffers
    ) {
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
            consumer.vertex(
                            pose.pose(),
                            (float) point.x,
                            (float) point.y,
                            (float) point.z)
                    .color(color)
                    .normal(
                            pose.normal(),
                            (float) tangent.x,
                            (float) tangent.y,
                            (float) tangent.z)
                    .endVertex();
        }
    }

    @Override
    public boolean shouldRender(
            WhipAttackEntity entity,
            Frustum frustum,
            double cameraX,
            double cameraY,
            double cameraZ
    ) {
        // 实体锚点在玩家手边，但鞭梢可能进入视锥，因此不能只按实体本身的小包围盒裁剪。
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(WhipAttackEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
