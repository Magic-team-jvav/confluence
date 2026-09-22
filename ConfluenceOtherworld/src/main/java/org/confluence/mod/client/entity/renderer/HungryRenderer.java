package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.TheHungry;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 绘制饿鬼本体及其连接 Boss 锚点的连续叶片。
public final class HungryRenderer<T extends TheHungry> extends GeoNormalRenderer<T> {
    private static final ModelResourceLocation SEGMENT_MODEL = new ModelResourceLocation(Confluence.asResource("entity/the_hungry_leaf"), "inventory");
    private static final double SEGMENT_SPACING = 0.75;
    private static final int MAX_SEGMENTS = 96;
    private static final Quaternionf[] SEGMENT_ROTATIONS = new Quaternionf[MAX_SEGMENTS];
    private Frustum frustum;

    static {
        for (int i = 0; i < MAX_SEGMENTS; i++) SEGMENT_ROTATIONS[i] = Axis.YN.rotation(i * 0.5F);
    }

    public HungryRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("the_hungry"), true, 1.0F, 0.0F);
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        this.frustum = frustum;
        if (super.shouldRender(entity, frustum, x, y, z)) return true;
        return !entity.isFree() && entity.shouldRender(x, y, z)
                && frustum.isVisible(entity.getBoundingBox().minmax(new AABB(entity.getAnchor(), entity.position())).inflate(2));
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
        if (entity.isFree()) return;
        double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTick, entity.yOld, entity.getY()) + entity.getBbHeight() * 0.5;
        double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
        Vec3 entityPosition = new Vec3(x, y, z);
        Vec3 difference = entity.getAnchor().subtract(entityPosition);
        double distance = difference.length();
        if (distance < 1.0E-5) return;
        // 单个叶节主干约长一格，按四分之三格布置可保留少量重叠；旧算法在长距离时
        // 被五十节上限截断，相邻距离会超过一格并直接露出断口。
        int count = Mth.clamp(Mth.ceil(distance / SEGMENT_SPACING), 5, MAX_SEGMENTS);
        Vec3 step = difference.scale(1.0 / count);
        Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), step.toVector3f());
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(SEGMENT_MODEL);
        int overlay = getPackedOverlay(entity, 0.0F, partialTick);
        poseStack.pushPose();
        poseStack.translate(0.0, entity.getBbHeight() * 0.5, 0.0);
        /// 渲染层和缓冲只查询一次，不在每个叶节重复运行物品层选择。
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        for (RenderType renderType : model.getRenderTypes(ItemStack.EMPTY, false)) {
            VertexConsumer vertices = ItemRenderer.getFoilBuffer(buffers, renderType, false, false);
            for (int index = 0; index < count; index++) {
                Vec3 center = entityPosition.add(step.scale(index));
                if (frustum != null && !frustum.isVisible(new AABB(center, center).inflate(2)))
                    continue;
                poseStack.pushPose();
                poseStack.translate(step.x * index, step.y * index, step.z * index);
                poseStack.mulPose(rotation);
                poseStack.mulPose(SEGMENT_ROTATIONS[index]);
                poseStack.translate(-0.5, 0.0, -0.5);
                poseStack.scale(1.0F, -1.0F, 1.0F);
                itemRenderer.renderModelLists(model, ItemStack.EMPTY, packedLight, overlay, poseStack, vertices);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }
}
