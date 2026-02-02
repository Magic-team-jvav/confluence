package org.confluence.terraentity.client.block.renderer;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.confluence.terraentity.block.FigureBlock.FigureBlockEntity;
import org.confluence.terraentity.client.util.ShaderUtil;
import org.jetbrains.annotations.NotNull;

public class FigureBlockRenderer <T extends FigureBlockEntity> implements BlockEntityRenderer<T> {
    public FigureBlockRenderer(BlockEntityRendererProvider.Context context) {

    }
    @Override
    public void render(T t, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {

//        poseStack.pushPose();

        poseStack.pushPose();
        poseStack.pushPose();
        poseStack.scale(0.95f,0.5f,0.95f);
        poseStack.translate(0.025,0,0.025);

//
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.STONE_SLAB.defaultBlockState(), poseStack, multiBufferSource, packedLight, packedOverlay);
//        poseStack.popPose();
//        poseStack.translate(0.075,0.25,0.075);
//        poseStack.scale(0.8f,0.3f,0.8f);
//        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.STONE_SLAB.defaultBlockState(), poseStack, multiBufferSource, packedLight, packedOverlay);


        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_ONE, GlConst.GL_ONE);
        ShaderUtil.renderLightBoundle(multiBufferSource, poseStack.last().pose(), 1F, (float) Math.sin(System.currentTimeMillis() % 10000000 * 0.001f) * 0.3f + 1, 0xFF12BF56);

        RenderSystem.disableBlend();


        poseStack.popPose();

//        var entity = t.entity;
//
//        float y = entity.tickCount + (t.turnOn?  partialTick : 0);
//        double fy = Math.sin(y * 0.05f) * 0.15;
//        poseStack.translate(0.5,   0.4f, 0.5);
//        poseStack.mulPose(Axis.YN.rotation(y * 0.01752f));
//        float scale = (float) (0.5 * t.scale);
//        poseStack.scale(scale, scale, scale);
//
//        Minecraft.getInstance().getEntityRenderDispatcher().render(
//                entity, 0,0,0,0, 0, poseStack, multiBufferSource, packedLight);



        poseStack.popPose();

    }
}
