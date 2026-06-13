package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.client.model.block.AltarBlockModel;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.ArrayList;
import java.util.List;

public class AltarBlockRenderer extends GeoBlockRenderer<AltarBlock.BEntity> {
    public AltarBlockRenderer() {
        super(new AltarBlockModel());
    }

    @Override
    public void defaultRender(PoseStack poseStack, AltarBlock.BEntity animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);

        Level level = animatable.getLevel();
        long time = level == null ? 0 : level.getGameTime() % 10000;

        final List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ItemStack itemStack = animatable.getItem(i);
            if (!itemStack.isEmpty()) items.add(itemStack);
        }

        int itemCount = items.size();

        float radius = 0.4F + 0.25F * Mth.sqrt(itemCount);
        float rotate = Mth.TWO_PI / itemCount;
        float scale = 1;
        float v = Mth.lerp(partialTick, time - 1F, (float) time) * 0.125F * Mth.sqrt(itemCount);

        int i = 0;
        for (ItemStack itemStack : items) {
            poseStack.pushPose();

            float rotateToDraw = v + rotate * i;
            float offsetX = Mth.cos(rotateToDraw) * radius;
            float offsetZ = Mth.sin(rotateToDraw) * radius;

            poseStack.translate(0.5 + offsetX, 1.5, 0.5 + offsetZ);
            poseStack.scale(scale, scale, scale);

            float distance = Mth.invSqrt(offsetX * offsetX + offsetZ * offsetZ);
            if (distance > 0) {
                float dirX = -offsetX * distance;
                float dirZ = -offsetZ * distance;

                float angle = (float) Mth.atan2(dirZ, dirX);

                poseStack.mulPose(Axis.YP.rotation(angle));
            }

            Minecraft.getInstance().getItemRenderer().render(
                    itemStack,
                    ItemDisplayContext.GROUND,
                    false,
                    poseStack,
                    bufferSource,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    Minecraft.getInstance().getItemRenderer().getModel(itemStack, level, null, 0)
            );
            poseStack.popPose();
            i++;
        }
    }
}
