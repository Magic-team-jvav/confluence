package org.confluence.mod.client.renderer.entity.hook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.hook.IlluminantHookModel;
import org.confluence.mod.client.model.entity.hook.TendonHookModel;
import org.confluence.mod.client.model.entity.hook.WormHookModel;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.entity.hook.MimicHookEntity;

public class MimicHookRenderer extends AbstractHookRenderer<MimicHookEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
        Confluence.asResource("textures/entity/hook/illuminant_hook.png"),
        Confluence.asResource("textures/entity/hook/worm_hook.png"),
        Confluence.asResource("textures/entity/hook/tendon_hook.png")
    };
    private static final BlockState CHAIN = Blocks.CHAIN.defaultBlockState();
    private final EntityModel<? extends AbstractHookEntity>[] models;

    public MimicHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new IlluminantHookModel(pContext.bakeLayer(IlluminantHookModel.LAYER_LOCATION)));
        this.models = new EntityModel[]{
            new IlluminantHookModel(pContext.bakeLayer(IlluminantHookModel.LAYER_LOCATION)),
            new WormHookModel(pContext.bakeLayer(WormHookModel.LAYER_LOCATION)),
            new TendonHookModel(pContext.bakeLayer(TendonHookModel.LAYER_LOCATION))
        };
    }

    @Override
    public BlockState getChain(MimicHookEntity entity) {
        return CHAIN;
    }

    @Override
    public ResourceLocation getTextureLocation(MimicHookEntity pEntity) {
        return TEXTURES[pEntity.getVariant().ordinal()];
    }

    @Override
    protected void renderHook(MimicHookEntity entity, PoseStack poseStack, MultiBufferSource multiBufferSource, int skyLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));
        EntityModel<? extends AbstractHookEntity> selectedModel = models[entity.getVariant().ordinal()];
        selectedModel.renderToBuffer(poseStack, multiBufferSource.getBuffer(selectedModel.renderType(getTextureLocation(entity))), skyLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
