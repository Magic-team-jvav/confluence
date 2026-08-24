package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Function;

public class MutableRenderTypeItemExtension implements IClientItemExtensions {
    private final Function<ItemStack, RenderType> getter;
    private BlockEntityWithoutLevelRenderer renderer;

    public MutableRenderTypeItemExtension(Function<ItemStack, RenderType> getter) {
        this.getter = getter;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) {
            Minecraft minecraft = Minecraft.getInstance();
            this.renderer = new BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {
                @Override
                public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
                    BakedModel model = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 250913);
                    for (RenderType renderType : model.getRenderTypes(stack, false)) {
                        minecraft.getItemRenderer().renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer.getBuffer(renderType));
                    }
                    minecraft.getItemRenderer().renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer.getBuffer(getter.apply(stack)));
                }
            };
        }
        return renderer;
    }
}
