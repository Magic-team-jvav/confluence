package org.confluence.mod.client.summoner.model.bbmodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.confluence.mod.client.summoner.ColorBufferSource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

final class BBModelRenderer {

    private BBModelRenderer() {
    }

    static boolean render(
            BBModelRenderOptions options,
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        BBModelManager.BBModelAsset asset = BBModelManager.INSTANCE.getModel(options.modelId());
        if (asset == null) {
            return false;
        }
        BBModelModel model = asset.model();
        model.reset();
        try {
            String animationName = options.animationName();
            if (animationName != null) {
                BBModelClip clip = asset.clip(animationName);
                if (clip != null) {
                    clip.sample(options.ageTicks(), model);
                }
            }
            for (String boneName : options.hiddenBones()) {
                BBModelBone bone = model.getBone(boneName);
                if (bone != null) {
                    bone.setHidden(true);
                }
            }

            MultiBufferSource source = bufferSource;
            if (options.color() != -1 || options.alpha() < 1) {
                source = new ColorBufferSource(bufferSource)
                        .setColor(options.color())
                        .setAlpha(options.alpha());
            }

            VertexConsumer[] consumers = new VertexConsumer[model.getTextures().size()];
            for (int i = 0; i < consumers.length; i++) {
                ResourceLocation texture = options.texture() != null ? options.texture() : model.getTexture(i);
                texture = BBModelManager.INSTANCE.resolveTexture(texture);
                RenderType renderType = options.renderType() != null
                        ? options.renderType()
                        : RenderType.entityTranslucent(texture);
                consumers[i] = source.getBuffer(renderType);
            }

            poseStack.pushPose();
            try {
                poseStack.translate(options.translateX(), options.translateY(), options.translateZ());
                if (options.rotX() != 0) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(options.rotX()));
                }
                if (options.rotY() != 0) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(options.rotY()));
                }
                if (options.rotZ() != 0) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(options.rotZ()));
                }
                if (options.scale() != 1) {
                    poseStack.scale(options.scale(), options.scale(), options.scale());
                }

                model.render(
                        poseStack,
                        index -> consumers[Math.max(0, Math.min(consumers.length - 1, index))],
                        options.packedLight(),
                        options.packedOverlay(),
                        options.color() == -1 ? -1 : options.color()
                );
                return true;
            } finally {
                poseStack.popPose();
            }
        } finally {
            model.reset();
        }
    }
}
