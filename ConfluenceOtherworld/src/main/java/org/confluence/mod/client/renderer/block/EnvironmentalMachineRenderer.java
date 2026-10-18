package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.terraform.EnvironmentalMachineBlock;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public final class EnvironmentalMachineRenderer extends GeoBlockRenderer<EnvironmentalMachineBlock.BEntity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/clentamachine.geo.json");
    public static final ResourceLocation ANIMATION = Confluence.asResource("animations/block/clentamachine.animation.json");

    public static ResourceLocation texture(boolean world) {return Confluence.asResource("textures/block/" + (world ? "terraformer" : "clentaminator") + ".png");}

    public EnvironmentalMachineRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(EnvironmentalMachineBlock.BEntity entity) {return MODEL;}

            @Override
            public ResourceLocation getTextureResource(EnvironmentalMachineBlock.BEntity entity) {return texture(((EnvironmentalMachineBlock) entity.getBlockState().getBlock()).world);}

            @Override
            public ResourceLocation getAnimationResource(EnvironmentalMachineBlock.BEntity entity) {return ANIMATION;}
        });
    }

    @Override
    public void defaultRender(PoseStack pose, EnvironmentalMachineBlock.BEntity entity, MultiBufferSource source,
                              RenderType type, VertexConsumer buffer, float yaw, float partialTick, int light) {
        if (entity.isBase)
            super.defaultRender(pose, entity, source, type, buffer, yaw, partialTick, light);
    }

    // 贴图只有全透明和不透明像素；使用带背面剔除的裁切渲染，保留负体积且避免透明面排序穿插。
    @Override
    public RenderType getRenderType(EnvironmentalMachineBlock.BEntity entity, ResourceLocation texture, MultiBufferSource buffers, float partialTick) {
        return RenderType.entityCutout(texture);
    }

    // 只染色独立的溶液骨骼，机器金属外壳和罐体保持贴图原色；无燃料时不显示液体。
    @Override
    public void renderRecursively(PoseStack pose, EnvironmentalMachineBlock.BEntity entity, GeoBone bone, RenderType type,
                                  MultiBufferSource source, VertexConsumer buffer, boolean rerender, float partialTick, int light, int overlay, int color) {
        if (bone.getName().endsWith("solutions")) {
            if (entity.solution == null) return;
            int slot = bone.getName().startsWith("left_") ? 0 : bone.getName().startsWith("right_") ? 1 : bone.getName().startsWith("back_") ? 2 : -1;
            if (slot >= 0 && !entity.hasSolutionInSlot(slot)) return;
            color = 0xff000000 | entity.solution.color;
        }
        super.renderRecursively(pose, entity, bone, type, source, buffer, rerender, partialTick, light, overlay, color);
    }

    // 物品形态也使用负尺寸剔除；空机器不绘制储液罐中的液体。
    public static GeoRenderProvider itemRenderer(boolean world) {
        return new GeoRenderProvider() {
            private GeoItemRenderer<EnvironmentalMachineBlock.BItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null)
                    renderer = new GeoItemRenderer<>(new GeoModel<EnvironmentalMachineBlock.BItem>() {
                        @Override
                        public ResourceLocation getModelResource(EnvironmentalMachineBlock.BItem item) {return MODEL;}

                        @Override
                        public ResourceLocation getTextureResource(EnvironmentalMachineBlock.BItem item) {return texture(world);}

                        @Override
                        public ResourceLocation getAnimationResource(EnvironmentalMachineBlock.BItem item) {return ANIMATION;}
                    }) {
                        @Override
                        public RenderType getRenderType(EnvironmentalMachineBlock.BItem item, ResourceLocation texture, MultiBufferSource source, float partialTick) {
                            return RenderType.entityCutout(texture);
                        }

                        @Override
                        public void renderRecursively(PoseStack pose, EnvironmentalMachineBlock.BItem item, GeoBone bone,
                                                      RenderType type, MultiBufferSource source, VertexConsumer buffer, boolean rerender,
                                                      float partialTick, int light, int overlay, int color) {
                            if (bone.getName().endsWith("solutions")) return;
                            super.renderRecursively(pose, item, bone, type, source, buffer, rerender, partialTick, light, overlay, color);
                        }
                    };
                return renderer;
            }
        };
    }
}
