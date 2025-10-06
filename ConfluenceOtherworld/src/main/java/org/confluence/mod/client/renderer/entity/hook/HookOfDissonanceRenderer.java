package org.confluence.mod.client.renderer.entity.hook;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.HookOfDissonanceEntity;

public class HookOfDissonanceRenderer extends AbstractHookRenderer<HookOfDissonanceEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/hook/grappling_hook.png");
    private static final BlockState CHAIN = Blocks.CHAIN.defaultBlockState();

    public HookOfDissonanceRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public BlockState getChain(HookOfDissonanceEntity entity) {
        return CHAIN;
    }

    @Override
    public ResourceLocation getTextureLocation(HookOfDissonanceEntity pEntity) {
        return TEXTURE;
    }
}
