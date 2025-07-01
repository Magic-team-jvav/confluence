package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.crafting.ExtractinatorBlock;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ExtractinatorBlockRenderer extends GeoBlockRenderer<ExtractinatorBlock.Entity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/extractinator.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/extractinator.png");
    public static final ResourceLocation ANIMATION = Confluence.asResource("animations/block/extractinator.animation.json");

    public ExtractinatorBlockRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(ExtractinatorBlock.Entity animatable) {
                return MODEL;
            }

            @Override
            public ResourceLocation getTextureResource(ExtractinatorBlock.Entity animatable) {
                return TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(ExtractinatorBlock.Entity animatable) {
                return ANIMATION;
            }
        });
    }

    @Override
    public void defaultRender(PoseStack poseStack, ExtractinatorBlock.Entity animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        if (animatable.isBase) {
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
