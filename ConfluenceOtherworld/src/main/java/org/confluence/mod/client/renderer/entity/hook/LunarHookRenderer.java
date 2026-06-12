package org.confluence.mod.client.renderer.entity.hook;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.LunarHookEntity;
import org.confluence.mod.common.init.block.DecorativeBlocks;

public class LunarHookRenderer extends AbstractHookRenderer<LunarHookEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            Confluence.asResource("textures/entity/hook/amethyst_hook.png"),
            Confluence.asResource("textures/entity/hook/amber_hook.png"),
            Confluence.asResource("textures/entity/hook/sapphire_hook.png"),
            Confluence.asResource("textures/entity/hook/jade_hook.png")
    };
    private final BlockState[] CHAINS;

    public LunarHookRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.CHAINS = new BlockState[]{
                DecorativeBlocks.AMETHYST_CHAIN.get().defaultBlockState(),
                DecorativeBlocks.AMBER_CHAIN.get().defaultBlockState(),
                DecorativeBlocks.SAPPHIRE_CHAIN.get().defaultBlockState(),
                DecorativeBlocks.JADE_CHAIN.get().defaultBlockState()
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
}
