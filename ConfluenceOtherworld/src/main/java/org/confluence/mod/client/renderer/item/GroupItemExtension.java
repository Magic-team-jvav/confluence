package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.GroupItem;

import java.util.List;

public class GroupItemExtension implements IClientItemExtensions {
    public static final GroupItemExtension INSTANCE = new GroupItemExtension();
    private BlockEntityWithoutLevelRenderer renderer;

    private GroupItemExtension() {}

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) {
            Minecraft minecraft = Minecraft.getInstance();
            this.renderer = new BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {
                @Override
                public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
                    GroupItem.Stacks stacks = stack.get(ModDataComponentTypes.GROUP_STACKS);
                    if (stacks == null) return;
                    List<ItemStack> values = stacks.getValues();
                    int size = values.size();
                    if (size == 0) return;
                    long time = System.currentTimeMillis();
                    if (time - stacks.lastRenderTime >= 1000) {
                        stacks.lastRenderTime = time;
                        if (++stacks.lastRenderIndex >= size) {
                            stacks.lastRenderIndex = 0;
                        }
                    }
                    ItemStack itemStack = values.get(stacks.lastRenderIndex);
                    BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, minecraft.level, minecraft.player, 251014);
                    poseStack.pushPose();
                    poseStack.translate(0.5F, 0.5F, 0.5F);
                    minecraft.getItemRenderer().render(itemStack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, bakedModel);
                    poseStack.popPose();
                }
            };
        }
        return renderer;
    }
}
