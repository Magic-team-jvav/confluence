package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class CustomLightItemExtension implements IClientItemExtensions {
    private final int light;
    private BlockEntityWithoutLevelRenderer renderer;

    /**
     * @param light [0, 15]
     */
    public CustomLightItemExtension(int light) {
        this.light = LightTexture.pack(light, light);
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) {
            Minecraft minecraft = Minecraft.getInstance();
            this.renderer = new BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {
                @Override
                public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
                    minecraft.getItemRenderer().renderModelLists(
                            minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 250914),
                            stack, light, OverlayTexture.NO_OVERLAY, poseStack,
                            buffer.getBuffer(Sheets.translucentCullBlockSheet())
                    );
                }
            };
        }
        return renderer;
    }
}
