package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.TheHungry;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 绘制饿鬼本体及其连接 Boss 锚点的连续叶片。
public final class HungryRenderer<T extends TheHungry> extends GeoNormalRenderer<T> {
    private static final ModelResourceLocation SEGMENT_MODEL = new ModelResourceLocation(Confluence.asResource("entity/the_hungry_leaf"), "inventory");

    public HungryRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("the_hungry"), true, 1.0F, 0.0F);
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
        int count = Mth.clamp((int) (distance * 0.8) + 5, 5, 50);
        Vec3 step = difference.scale(1.0 / count);
        Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), step.toVector3f());
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(SEGMENT_MODEL);
        poseStack.pushPose();
        poseStack.translate(0.0, entity.getBbHeight() * 0.5, 0.0);
        for (int index = 0; index < count; index++) {
            poseStack.pushPose();
            poseStack.translate(step.x * index, step.y * index, step.z * index);
            poseStack.mulPose(rotation);
            poseStack.mulPose(com.mojang.math.Axis.YN.rotation(index * 0.5F));
            poseStack.translate(-0.5, 0.0, -0.5);
            poseStack.scale(1.0F, -1.0F, 1.0F);
            renderModel(model, poseStack, buffers, packedLight);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static void renderModel(BakedModel model, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        ItemStack stack = ItemStack.EMPTY;
        for (RenderType renderType : model.getRenderTypes(stack, false)) {
            VertexConsumer vertices = ItemRenderer.getFoilBuffer(buffers, renderType, false, false);
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, stack, packedLight, OverlayTexture.NO_OVERLAY, poseStack, vertices);
        }
    }
}
