package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.jetbrains.annotations.Nullable;

// TODO 请给予更好的类名称
public final class SpecialItemRenderingUtil {
    public static void bowArrowRenderer(ItemRenderer itemRenderer, LivingEntity entity, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, @Nullable Level level, int combinedLight, int combinedOverlay, int seed, Player player, ItemStack stack) {
        float charge = player.getTicksUsingItem() / 20.0f;
        if (charge < 0.1f) {
            return;
        }

        ItemStack arrowItem = player.getProjectile(stack);
        ArrowInBowRenderer.bowTransform(stack, poseStack, charge, displayContext);
        BakedModel bakedmodel = itemRenderer.getModel(arrowItem, level, entity, seed);
        itemRenderer.render(arrowItem, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, bakedmodel);
    }

    public static void repeaterArrowRenderer(ItemRenderer itemRenderer, LivingEntity entity, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, @Nullable Level level, int combinedLight, int combinedOverlay, int seed, Player player, ItemStack stack) {
        RepeaterContents repeaterContents = stack.getDataOrDefault(ModDataComponentTypes.REPEATER_CONTENTS, RepeaterContents.EMPTY);
        if (repeaterContents.isEmpty()) {
            return;
        }
        ItemStack arrowItem = repeaterContents.getStackInSlot(0);
        ArrowInBowRenderer.repeaterTransform(arrowItem, stack, poseStack, displayContext);
        BakedModel bakedmodel = itemRenderer.getModel(arrowItem, level, entity, seed);
        itemRenderer.render(arrowItem, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, bakedmodel);
    }
}
