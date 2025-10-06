package org.confluence.mod.client.renderer.entity.hook;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.hook.SkeletronHandModel;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.init.block.DecorativeBlocks;

public class SkeletronHandRenderer extends AbstractHookRenderer<AbstractHookEntity.Impl> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/hook/skeletron_hand.png");
    private final BlockState CHAIN;

    public SkeletronHandRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SkeletronHandModel(pContext.bakeLayer(SkeletronHandModel.LAYER_LOCATION)));
        this.CHAIN = DecorativeBlocks.BONE_CHAIN.get().defaultBlockState();
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
