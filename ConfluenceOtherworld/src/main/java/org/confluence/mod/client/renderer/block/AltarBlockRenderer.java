package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.model.block.AltarBlockModel;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.ArrayList;
import java.util.List;

public class AltarBlockRenderer extends GeoBlockRenderer<AltarBlock.BEntity> {

    private long TIME_BEFORE = 0;

    public AltarBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new AltarBlockModel());
    }

    @Override
    public void render(AltarBlock.BEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {

        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);


        final long timeVariable = (System.currentTimeMillis() / 20) % 10000;
        TIME_BEFORE = timeVariable;

        final List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ItemStack itemStack = blockEntity.getItem(i);
            if (!itemStack.isEmpty()) items.add(itemStack);
        }

        int itemCount = items.size();

        double radius = 0.4 + 0.25 * Math.sqrt(itemCount);
        double rotate = Math.TAU / itemCount;
        float scale = 1;

        int i = 0;
        for (ItemStack itemStack : items) {
            poseStack.pushPose();

            double rotateToDraw = TIME_BEFORE * 0.05 * Math.sqrt(itemCount) + rotate * i;
            double offsetX = Math.cos(rotateToDraw) * radius;
            double offsetZ = Math.sin(rotateToDraw) * radius;

            poseStack.translate(0.5 + offsetX, 1.5, 0.5 + offsetZ);
            poseStack.scale(scale, scale, scale);

            double distance = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
            if (distance > 0) {
                double dirX = -offsetX / distance;
                double dirZ = -offsetZ / distance;

                float angle = (float) Math.toDegrees(Math.atan2(dirZ, dirX));

                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle));
            }

            Minecraft.getInstance().getItemRenderer().render(
                    itemStack,
                    ItemDisplayContext.GROUND,
                    false,
                    poseStack,
                    bufferSource,
                    255,
                    packedOverlay,
                    Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0));

            poseStack.popPose();
            i ++;
        }
    }
}
