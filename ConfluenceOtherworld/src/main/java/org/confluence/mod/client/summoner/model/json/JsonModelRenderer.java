package org.confluence.mod.client.summoner.model.json;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.summoner.ColorBufferSource;
import org.confluence.mod.client.summoner.AfterimageManager;
import org.confluence.mod.client.summoner.LyraRenderTypes;

public final class JsonModelRenderer {

    static boolean render(ResourceLocation modelLocation, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel model = modelManager.getModel(modelLocation);
        if (model != modelManager.getMissingModel()) {
            AfterimageManager.renderModel(poseStack, bufferSource, ((color >>> 24) & 255) / 255f, source -> {
                ColorBufferSource tinted = new ColorBufferSource(source).setColor(color | 0xFF000000);
                VertexConsumer consumer = tinted.getBuffer(LyraRenderTypes.getModel());
                Minecraft.getInstance().getItemRenderer().renderModelLists(model, ItemStack.EMPTY, packedLight, OverlayTexture.NO_OVERLAY, poseStack, consumer);
            });
            return true;
        }
        return false;
    }
}
