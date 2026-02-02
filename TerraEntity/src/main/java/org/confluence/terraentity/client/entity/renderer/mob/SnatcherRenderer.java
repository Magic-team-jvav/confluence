package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.client.init.model.EntityBlockModelRegister;
import org.confluence.terraentity.entity.monster.Snatcher;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.utils.TEUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SnatcherRenderer<T extends Snatcher> extends GeoNormalRenderer<T> {

    public SnatcherRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path, true, 1, 0);

    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Vec3 init = entity.getInitPos();
        if(init == null){
            return;
        }

        poseStack.pushPose();
        if (Minecraft.getInstance().player != null) {
            // 错位渲染，防止正对的时候看到的是片面
            Vec3 p = Minecraft.getInstance().player.position().subtract(init);
            double a = p.cross(new Vec3(0, 1, 0)).dot(Minecraft.getInstance().player.position().subtract(entity.position()));
            poseStack.mulPose(Axis.YN.rotation(a>0?0.5f:-0.5f));
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();


        Vec3 lerpPos = new Vec3(
                Mth.lerp(partialTick, entity.xOld, entity.getX()),
                Mth.lerp(partialTick, entity.yOld, entity.getY()),
                Mth.lerp(partialTick, entity.zOld, entity.getZ())
        ).add(0, entity.getBbHeight() * 0.5F , 0); // 让渲染中心在实体的正中央

        Vec3 _diff = lerpPos.subtract(init); // initPos -> entity
        Vec3 diffNorm = _diff.normalize();

        // 渲染n段只需要偏移n-1次
        Vec3 offset = diffNorm.scale(-1);
        Vec3 diff = _diff.add(offset);
        int count = 10;
        double dx = diff.x / (count-1);
        double dy = diff.y / (count-1);
        double dz = diff.z / (count-1);
        Quaternionf rotate = TEUtils.rotateFromV1ToV2(
                new Vector3f(0, 1, 0),
                new Vector3f((float) diff.x, (float) diff.y + entity.getBbHeight() * 0.5F, (float) diff.z)); // 这里加一个高度偏移方向才正确，不知道为什么
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(EntityBlockModelRegister.getInstance().getModelResourceLocation(entity.getType()));;
        for (int i = 0; i < count; i++) {
            // 每一段的偏移量加上初始时包围盒y上的偏移
            Vec3 pos = new Vec3(
                    -i * dx ,
                    -i * dy + (1 - i * 1.0f / count) * entity.getBbHeight() * 0.5F ,
                    -i * dz );
            poseStack.pushPose();

            poseStack.translate(pos.x, pos.y, pos.z);

            poseStack.mulPose(rotate);
            poseStack.mulPose(Axis.YN.rotation(i * 0.5f));
            poseStack.translate(-0.5,0,-0.5);
            // 为了计算方便，需要从实体位置出发渲染，所以y轴取反
            poseStack.scale(1,-1,1);

            ItemStack stack = TEBoomerangItems.WOOD_BOOMERANG.toStack();
            for (RenderType rendertype : model.getRenderTypes(stack, false)) {
                VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, rendertype, false, stack.isEnchanted());
                Minecraft.getInstance().getItemRenderer().renderModelLists(
                        model, stack, packedLight, OverlayTexture.NO_OVERLAY,
                        poseStack, vertexconsumer);
            }

            poseStack.popPose();
        }

    }
}