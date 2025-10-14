package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

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
                    minecraft.getItemRenderer().renderModelLists(
                            minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 250913),
                            stack, packedLight, packedOverlay, poseStack,
                            VertexMultiConsumer.create(buffer.getBuffer(getter.apply(stack)), buffer.getBuffer(Sheets.translucentCullBlockSheet()))
                    );
                }
            };
        }
        return renderer;
    }
}
