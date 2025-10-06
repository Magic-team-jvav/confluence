package org.confluence.mod.client.renderer.entity.fishing;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.confluence.lib.client.animate.ExpertColorAnimation;
import org.confluence.lib.color.IntegerRGB;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.fishing.GlowingFishingHookModel;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;

import static org.confluence.mod.client.renderer.entity.fishing.BaseFishingHookRenderer.renderString;

public class GlowingFishingHookRenderer extends EntityRenderer<CurioFishingHook> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            Confluence.asResource("textures/entity/fishing/common.png"),
            Confluence.asResource("textures/entity/fishing/glowing.png"),
            Confluence.asResource("textures/entity/fishing/lava_moss.png"),
            Confluence.asResource("textures/entity/fishing/helium_moss.png"),
            Confluence.asResource("textures/entity/fishing/neon_moss.png"),
            Confluence.asResource("textures/entity/fishing/argon_moss.png"),
            Confluence.asResource("textures/entity/fishing/krypton_moss.png"),
            Confluence.asResource("textures/entity/fishing/xenon_moss.png")
    };
    private static final RenderType[] GLOWS = new RenderType[]{
            null,
            RenderType.entityCutoutNoCull(Confluence.asResource("textures/entity/fishing/glowing_glow.png")),
            RenderType.entityCutoutNoCull(Confluence.asResource("textures/entity/fishing/lava_moss_glow.png")),
            RenderType.entityCutoutNoCull(Confluence.asResource("textures/entity/fishing/helium_moss_glow.png")),
            RenderType.entityCutoutNoCull(Confluence.asResource("textures/entity/fishing/neon_moss_glow.png")),
            RenderType.entityCutoutNoCull(Confluence.asResource("textures/entity/fishing/argon_moss_glow.png")),
            RenderType.entityCutoutNoCull(Confluence.asResource("textures/entity/fishing/krypton_moss_glow.png")),
            RenderType.entityCutoutNoCull(Confluence.asResource("textures/entity/fishing/xenon_moss_glow.png"))
    };
    private final GlowingFishingHookModel mossModel;
    private final GlowingFishingHookModel commonModel;
    private final GlowingFishingHookModel glowingModel;

    public GlowingFishingHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.mossModel = new GlowingFishingHookModel(pContext.bakeLayer(GlowingFishingHookModel.MOSS));
        this.commonModel = new GlowingFishingHookModel(pContext.bakeLayer(GlowingFishingHookModel.COMMON));
        this.glowingModel = new GlowingFishingHookModel(pContext.bakeLayer(GlowingFishingHookModel.GLOWING));
    }

    @Override
    public ResourceLocation getTextureLocation(CurioFishingHook pEntity) {
        return TEXTURES[pEntity.getVariant().getId()];
    }

    public GlowingFishingHookModel getModel(CurioFishingHook fishingHook) {
        CurioFishingHook.Variant variant = fishingHook.getVariant();
        if (variant == CurioFishingHook.Variant.COMMON) {
            return commonModel;
        } else if (variant == CurioFishingHook.Variant.GLOWING) {
            return glowingModel;
        } else {
            return mossModel;
        }
    }

    @Override
    public void render(CurioFishingHook pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        int id = pEntity.getVariant().getId();
        GlowingFishingHookModel model = getModel(pEntity);
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(model.renderType(TEXTURES[id])), pPackedLight, OverlayTexture.NO_OVERLAY);
        if (id != 0) {
            model.renderToBuffer(pPoseStack, pBuffer.getBuffer(GLOWS[id]), 0xF000F0, OverlayTexture.NO_OVERLAY, id == 3 ? ExpertColorAnimation.INSTANCE.getColor() : -1);
        }
        renderString(entityRenderDispatcher, pEntity, pPartialTick, pPoseStack, pBuffer, IntegerRGB.BLACK.get());
    }
}
