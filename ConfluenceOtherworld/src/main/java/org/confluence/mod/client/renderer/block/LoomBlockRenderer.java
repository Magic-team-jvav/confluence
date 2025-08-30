package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.crafting.LoomBlock;
import org.confluence.mod.util.ClientUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LoomBlockRenderer extends GeoBlockRenderer<LoomBlock.BEntity> {
    public LoomBlockRenderer() {
        super(new DefaultedBlockGeoModel<>(Confluence.asResource("loom")));
    }

    @Override
    public void defaultRender(PoseStack poseStack, LoomBlock.BEntity animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        if (animatable.isBase) {
            super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(LoomBlock.BEntity pBlockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(LoomBlock.BEntity blockEntity) {
        return ClientUtils.getRenderBoundingBox3x(blockEntity.getBlockPos());
    }
}
