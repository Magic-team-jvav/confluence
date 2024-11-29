package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.confluence.mod.common.block.functional.WeatherVaneBlock;
import org.joml.Quaternionf;

public class WeatherVaneBlockRenderer implements BlockEntityRenderer<WeatherVaneBlock.Entity> {
    private static final BlockState BLOCK_STATE = Blocks.END_ROD.defaultBlockState();
    private static final Quaternionf X_ROT = Axis.XP.rotation(Mth.HALF_PI);
    private final BlockRenderDispatcher blockRenderer;

    public WeatherVaneBlockRenderer(BlockEntityRendererProvider.Context pContext) {
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(WeatherVaneBlock.Entity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0F, 0.5F);
        Quaternionf rotation = Axis.YP.rotation(Mth.lerp(partialTick, entity.rotationO, entity.rotation)).rotateX(Mth.HALF_PI);
        poseStack.mulPose(rotation.rotateZ(Mth.lerp(partialTick, entity.shakeO, entity.shake)));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        blockRenderer.renderSingleBlock(BLOCK_STATE, poseStack, bufferSource, packedLight, packedOverlay, ModelData.EMPTY, null);
        poseStack.popPose();
    }
}
