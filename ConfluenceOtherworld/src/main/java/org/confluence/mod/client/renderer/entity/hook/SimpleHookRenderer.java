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
import org.confluence.mod.common.entity.hook.AbstractHookEntity;

/**
 * <h1>通用钩爪渲染器</h1>
 * 替代所有单模型/单贴图的独立 Renderer 类。
 * 通过构造参数指定模型和贴图，chain 默认使用原版锁链方块。
 */
public class SimpleHookRenderer<T extends AbstractHookEntity> extends AbstractHookRenderer<T> {
    private static final BlockState CHAIN = Blocks.CHAIN.defaultBlockState();
    private final ResourceLocation texture;

    public SimpleHookRenderer(EntityRendererProvider.Context context,
                              EntityModel<? extends AbstractHookEntity> model,
                              ResourceLocation texture) {
        super(context, model);
        this.texture = texture;
    }

    @Override
    public BlockState getChain(T entity) {
        return CHAIN;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
