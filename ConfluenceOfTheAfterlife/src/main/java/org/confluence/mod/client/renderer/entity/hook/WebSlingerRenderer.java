package org.confluence.mod.client.renderer.entity.hook;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.hook.WebSlingerModel;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.init.block.DecorativeBlocks;

public class WebSlingerRenderer extends AbstractHookRenderer<AbstractHookEntity.Impl> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/hook/web_slinger.png");
    private final BlockState CHAIN;

    public WebSlingerRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new WebSlingerModel(pContext.bakeLayer(WebSlingerModel.LAYER_LOCATION)));
        this.CHAIN = DecorativeBlocks.SILK_CHAIN.get().defaultBlockState();
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractHookEntity.Impl pEntity) {
        return TEXTURE;
    }

    @Override
    public BlockState getChain(AbstractHookEntity.Impl entity) {
        return CHAIN;
    }
}
