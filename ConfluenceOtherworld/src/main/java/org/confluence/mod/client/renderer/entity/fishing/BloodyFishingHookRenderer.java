package org.confluence.mod.client.renderer.entity.fishing;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.confluence.lib.color.IntegerRGB;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.fishing.BloodyFishingHookModel;
import org.confluence.mod.common.entity.fishing.BloodyFishingHook;

import static org.confluence.mod.client.renderer.entity.fishing.BaseFishingHookRenderer.renderString;

public class BloodyFishingHookRenderer extends EntityRenderer<BloodyFishingHook> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/fishing/bloody.png");
    private final BloodyFishingHookModel model;

    public BloodyFishingHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new BloodyFishingHookModel(pContext.bakeLayer(BloodyFishingHookModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BloodyFishingHook pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(BloodyFishingHook pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(model.renderType(TEXTURE)), pPackedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        renderString(entityRenderDispatcher, pEntity, pPartialTick, pPoseStack, pBuffer, IntegerRGB.LIGHT_RED.get());
    }
}
