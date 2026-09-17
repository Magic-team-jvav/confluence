package org.confluence.mod.client.summon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

/// 使用原有模型资源绘制非实体召唤物。
final class ClientSummonGeoRenderer extends GeoObjectRenderer<ClientSummonVisual> {
    private final ClientSummonModels.Binding binding;
    private final float scale;
    private final float offsetY;
    private final float yawOffset;
    private boolean shellPass;

    ClientSummonGeoRenderer(ClientSummonModels.Binding binding) {
        super(new Model(binding));
        this.binding = binding;
        scale = binding.scale();
        offsetY = binding.offsetY();
        yawOffset = binding.yawOffset();
    }

    @Override
    public long getInstanceId(ClientSummonVisual visual) {
        return visual.id().getMostSignificantBits() ^ visual.id().getLeastSignificantBits();
    }

    @Override
    public void preRender(PoseStack poseStack, ClientSummonVisual visual, BakedGeoModel model, MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, visual, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.translate(-0.5F, -0.51F, -0.5F);
        scaleModelForRender(scale, scale, poseStack, visual, model, isReRender, partialTick, packedLight, packedOverlay);
        poseStack.translate(0.0F, offsetY, 0.0F);
        if (yawOffset != 0.0F) poseStack.mulPose(Axis.YP.rotationDegrees(yawOffset));
    }

    @Override
    public void actuallyRender(PoseStack poses, ClientSummonVisual visual, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource buffers, VertexConsumer buffer, boolean reRender, float partialTick,
                               int light, int overlay, float red, float green, float blue, float alpha) {
        shellPass = false;
        super.actuallyRender(poses, visual, model, renderType, buffers, buffer, reRender,
                partialTick, light, overlay, red, green, blue, alpha);
        if (binding.material() != ClientSummonModels.Material.SLIME) return;
        if (buffers instanceof MultiBufferSource.BufferSource source) source.endBatch(renderType);
        RenderType shell = RenderType.entityTranslucentCull(getTextureLocation(visual));
        shellPass = true;
        try {
            super.actuallyRender(poses, visual, model, shell, buffers, buffers.getBuffer(shell), true, partialTick, light, overlay, red, green, blue, alpha);
        } finally {
            shellPass = false;
        }
    }

    @Override
    public void renderCubesOfBone(PoseStack poses, GeoBone bone, VertexConsumer buffer, int light,
                                  int overlay, float red, float green, float blue, float alpha) {
        if (binding.material() != ClientSummonModels.Material.SLIME) {
            super.renderCubesOfBone(poses, bone, buffer, light, overlay, red, green, blue, alpha);
            return;
        }
        if (bone.isHidden()) return;
        for (int index = 0; index < bone.getCubes().size(); index++) {
            boolean shell = bone.getName().equals("outer") && index == 0;
            if (shell != shellPass) continue;
            poses.pushPose();
            renderCube(poses, bone.getCubes().get(index), buffer, light, overlay, red, green, blue, alpha);
            poses.popPose();
        }
    }

    private static final class Model extends GeoModel<ClientSummonVisual> {
        private final ClientSummonModels.Binding binding;

        private Model(ClientSummonModels.Binding binding) {
            this.binding = binding;
        }

        @Override
        public ResourceLocation getModelResource(ClientSummonVisual visual) {
            return binding.model();
        }

        @Override
        public ResourceLocation getTextureResource(ClientSummonVisual visual) {
            return binding.texture();
        }

        @Override
        public ResourceLocation getAnimationResource(ClientSummonVisual visual) {
            return binding.animation();
        }

        @Override
        public RenderType getRenderType(ClientSummonVisual visual, ResourceLocation texture) {
            return binding.material() == ClientSummonModels.Material.CUTOUT ? RenderType.entityCutout(texture) : RenderType.entityCutoutNoCull(texture);
        }
    }
}
