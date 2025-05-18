package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.client.model.block.ExtractinatorBlockModel;
import org.confluence.mod.common.block.functional.crafting.ExtractinatorBlock;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ExtractinatorBlockRenderer extends GeoBlockRenderer<ExtractinatorBlock.Entity> {
    public ExtractinatorBlockRenderer() {
        super(new ExtractinatorBlockModel());
    }

    @Override
    public void defaultRender(PoseStack poseStack, ExtractinatorBlock.Entity animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        if (animatable.getBlockState().getValue(StateProperties.HORIZONTAL_TWO_PART).isBase()) {
            super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ExtractinatorBlock.Entity pBlockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(ExtractinatorBlock.Entity blockEntity) {
        return AABB.INFINITE;
    }
}
