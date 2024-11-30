package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.confluence.mod.common.block.functional.WeatherVaneBlock;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class WeatherVaneBlockRenderer implements BlockEntityRenderer<WeatherVaneBlock.Entity> {
    private static final BlockState BLOCK_STATE = Blocks.END_ROD.defaultBlockState();
    private final BlockRenderDispatcher blockRenderer;

    public WeatherVaneBlockRenderer() {
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(WeatherVaneBlock.Entity entity, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0F, 0.5F);
        Quaternionf rotation = Axis.YP.rotation(Mth.lerp(partialTick, entity.rotationO, entity.rotation)).rotateX(Mth.HALF_PI);
        poseStack.mulPose(rotation.rotateZ(Mth.lerp(partialTick, entity.shakeO, entity.shake)));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        blockRenderer.renderSingleBlock(BLOCK_STATE, poseStack, bufferSource, packedLight, packedOverlay, ModelData.EMPTY, RenderType.solid());
        poseStack.popPose();
    }
}
