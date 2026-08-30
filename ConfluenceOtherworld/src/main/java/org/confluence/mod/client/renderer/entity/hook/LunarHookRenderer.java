package org.confluence.mod.client.renderer.entity.hook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.hook.LunarHookNebulaModel;
import org.confluence.mod.client.model.entity.hook.LunarHookSolarModel;
import org.confluence.mod.client.model.entity.hook.LunarHookStardustModel;
import org.confluence.mod.client.model.entity.hook.LunarHookVortexModel;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.entity.hook.LunarHookEntity;
import org.confluence.mod.common.init.block.DecorativeBlocks;

public class LunarHookRenderer extends AbstractHookRenderer<LunarHookEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
        Confluence.asResource("textures/entity/hook/lunar_hook_nebula.png"),
        Confluence.asResource("textures/entity/hook/lunar_hook_solar.png"),
        Confluence.asResource("textures/entity/hook/lunar_hook_stardust.png"),
        Confluence.asResource("textures/entity/hook/lunar_hook_vortex.png")
    };
    private final BlockState[] CHAINS;
    private final EntityModel<? extends AbstractHookEntity>[] models;

    public LunarHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new LunarHookNebulaModel(pContext.bakeLayer(LunarHookNebulaModel.LAYER_LOCATION)));
        this.CHAINS = new BlockState[]{
            DecorativeBlocks.AMETHYST_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.AMBER_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.SAPPHIRE_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.JADE_CHAIN.get().defaultBlockState()
        };
        this.models = new EntityModel[]{
            new LunarHookNebulaModel(pContext.bakeLayer(LunarHookNebulaModel.LAYER_LOCATION)),
            new LunarHookSolarModel(pContext.bakeLayer(LunarHookSolarModel.LAYER_LOCATION)),
            new LunarHookStardustModel(pContext.bakeLayer(LunarHookStardustModel.LAYER_LOCATION)),
            new LunarHookVortexModel(pContext.bakeLayer(LunarHookVortexModel.LAYER_LOCATION))
        };
    }

    @Override
    public BlockState getChain(LunarHookEntity entity) {
        return CHAINS[entity.getVariant().getId()];
    }

    @Override
    public ResourceLocation getTextureLocation(LunarHookEntity pEntity) {
        return TEXTURES[pEntity.getVariant().getId()];
    }

    @Override
    protected void renderHook(LunarHookEntity entity, PoseStack poseStack, MultiBufferSource multiBufferSource, int skyLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));
        EntityModel<? extends AbstractHookEntity> selectedModel = models[entity.getVariant().getId()];
        selectedModel.renderToBuffer(poseStack, multiBufferSource.getBuffer(selectedModel.renderType(getTextureLocation(entity))), skyLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
