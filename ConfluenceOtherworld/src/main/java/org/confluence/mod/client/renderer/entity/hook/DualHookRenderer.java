package org.confluence.mod.client.renderer.entity.hook;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.DualHookEntity;

public class DualHookRenderer extends AbstractHookRenderer<DualHookEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
        Confluence.asResource("textures/entity/hook/ruby_hook.png"),
        Confluence.asResource("textures/entity/hook/sapphire_hook.png")
    };
    private static final BlockState CHAIN = Blocks.CHAIN.defaultBlockState();

    public DualHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public BlockState getChain(DualHookEntity entity) {
        return CHAIN;
    }

    @Override
    public ResourceLocation getTextureLocation(DualHookEntity pEntity) {
        return TEXTURES[pEntity.getVariant().getId()];
    }
}
