package com.xiaohunao.enemybanner.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.xiaohunao.enemybanner.BannerParameters;
import com.xiaohunao.enemybanner.BannerRenderUtils;
import com.xiaohunao.enemybanner.items.ItemRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BannerWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {

    public BannerWithoutLevelRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        ModelPart modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(EnemyBannerRenderer.LAYER_LOCATION);
        ModelPart flag = modelPart.getChild(EnemyBannerRenderer.FLAG);
        ModelPart pole = modelPart.getChild(EnemyBannerRenderer.POLE);
        ModelPart bar = modelPart.getChild(EnemyBannerRenderer.BAR);

        VertexConsumer vertexConsumer = ModelBakery.BANNER_BASE.buffer(buffer, RenderType::entitySolid);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        if (stack.is(ItemRegister.ENEMY_BANNER_PLANE)){
            pole.visible = false;
            bar.visible = false;
        }

        pole.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        bar.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.translate(0, -2, 0);

        BannerParameters bannerParameters = stack.get(BannerParameters.BANNER_DATA_COMPONENT);
        if (bannerParameters != null) {
            BannerRenderUtils.renderBannerFlag(bannerParameters, flag, poseStack, buffer, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }
}
