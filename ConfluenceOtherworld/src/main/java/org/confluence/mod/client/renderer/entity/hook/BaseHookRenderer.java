package org.confluence.mod.client.renderer.entity.hook;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.BaseHookEntity;
import org.confluence.mod.common.init.block.DecorativeBlocks;

public class BaseHookRenderer extends AbstractHookRenderer<BaseHookEntity> {
    private static final ResourceLocation[] TEXTURE = new ResourceLocation[]{
        Confluence.asResource("textures/entity/hook/grappling_hook.png"),
        Confluence.asResource("textures/entity/hook/amethyst_hook.png"),
        Confluence.asResource("textures/entity/hook/topaz_hook.png"),
        Confluence.asResource("textures/entity/hook/sapphire_hook.png"),
        Confluence.asResource("textures/entity/hook/jade_hook.png"),
        Confluence.asResource("textures/entity/hook/ruby_hook.png"),
        Confluence.asResource("textures/entity/hook/amber_hook.png"),
        Confluence.asResource("textures/entity/hook/diamond_hook.png")
    };
    private final BlockState[] CHAINS;

    public BaseHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.CHAINS = new BlockState[]{
            Blocks.CHAIN.defaultBlockState(),
            DecorativeBlocks.AMETHYST_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.TOPAZ_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.SAPPHIRE_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.JADE_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.RUBY_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.AMBER_CHAIN.get().defaultBlockState(),
            DecorativeBlocks.DIAMOND_CHAIN.get().defaultBlockState()
        };
    }

    @Override
    public ResourceLocation getTextureLocation(BaseHookEntity pEntity) {
        return TEXTURE[pEntity.getVariant().getId()];
    }

    @Override
    public BlockState getChain(BaseHookEntity entity) {
        return CHAINS[entity.getVariant().getId()];
    }
}
