package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.BewitchingTableBlock;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BewitchingTableBlockRenderer extends GeoBlockRenderer<BewitchingTableBlock.Entity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/bewitching_table.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/bewitching_table.png");
    public static final ResourceLocation ANIMATION = Confluence.asResource("animations/block/bewitching_table.animation.json");

    public BewitchingTableBlockRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(BewitchingTableBlock.Entity animatable) {
                return MODEL;
            }

            @Override
            public ResourceLocation getTextureResource(BewitchingTableBlock.Entity animatable) {
                return TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(BewitchingTableBlock.Entity animatable) {
                return ANIMATION;
            }
        });
    }

    @Override
    public void defaultRender(PoseStack poseStack, BewitchingTableBlock.Entity animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        if (animatable.isBase) {
            super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        }
    }
}
