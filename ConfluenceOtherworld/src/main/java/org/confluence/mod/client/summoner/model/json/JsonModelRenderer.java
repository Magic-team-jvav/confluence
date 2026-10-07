package org.confluence.mod.client.summoner.model.json;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.summoner.ColorBufferSource;
import org.confluence.mod.client.summoner.LyraRenderTypes;
import org.jetbrains.annotations.Nullable;

/// 使用原生独立模型登记与方块图集提交天顶剑组件。
public final class JsonModelRenderer {

    public static ResourceLocation resourcePath(ResourceLocation modelId) {
        return ResourceLocation.fromNamespaceAndPath(modelId.getNamespace(), "lyra_model/json/" + modelId.getPath() + "/" + fileName(modelId.getPath()));
    }

    public static ModelResourceLocation standaloneLocation(ResourceLocation modelId) {
        return ModelResourceLocation.standalone(resourcePath(modelId));
    }

    private static String fileName(String path) {
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    static boolean render(ModelResourceLocation modelLocation, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel model = resolve(modelManager, modelLocation);
        if (model != null) {
            ColorBufferSource colorBufferSource = new ColorBufferSource(bufferSource);
            colorBufferSource.setColor(color == -1 ? 0xFFFFFFFF : color);
            VertexConsumer consumer = colorBufferSource.getBuffer(LyraRenderTypes.getModel());
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, ItemStack.EMPTY, packedLight, OverlayTexture.NO_OVERLAY, poseStack, consumer);
            return true;
        }
        return false;
    }

    @Nullable
    private static BakedModel resolve(ModelManager modelManager, ModelResourceLocation modelLocation) {
        BakedModel missingModel = modelManager.getMissingModel();
        BakedModel model = modelManager.getModel(modelLocation);
        if (model != missingModel) {
            return model;
        }
        return null;
    }
}
