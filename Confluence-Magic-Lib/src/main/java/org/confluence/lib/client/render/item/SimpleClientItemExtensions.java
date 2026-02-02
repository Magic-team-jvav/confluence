package org.confluence.lib.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public class SimpleClientItemExtensions implements IClientItemExtensions {
    protected RenderByItemCallback renderByItemCallback = (minecraft, stack, displayContext, poseStack, buffer, packedLight, packedOverlay) -> renderSimpleItem(minecraft, stack, poseStack, buffer, packedLight, packedOverlay);
    protected BlockEntityWithoutLevelRenderer renderer;
    protected HandTransformPredicate handTransformPredicate = IClientItemExtensions.super::applyForgeHandTransform;
    protected ArmPoseGetter armPoseGetter = IClientItemExtensions.super::getArmPose;

    public SimpleClientItemExtensions noRenderer() {
        this.renderByItemCallback = (minecraft, stack, displayContext, poseStack, buffer, packedLight, packedOverlay) -> {};
        return this;
    }

    public SimpleClientItemExtensions customRenderer(RenderByItemCallback callback) {
        this.renderByItemCallback = callback;
        return this;
    }

    public SimpleClientItemExtensions handTransform(boolean force) {
        this.handTransformPredicate = (poseStack, player, arm, itemInHand, partialTick, equipProcess, swingProcess) -> force;
        return this;
    }

    public SimpleClientItemExtensions handTransform(HandTransformPredicate predicate) {
        this.handTransformPredicate = predicate;
        return this;
    }

    public SimpleClientItemExtensions armPose(HumanoidModel.ArmPose armPose) {
        this.armPoseGetter = (living, hand, itemStack) -> armPose;
        return this;
    }

    public SimpleClientItemExtensions armPose(ArmPoseGetter getter) {
        this.armPoseGetter = getter;
        return this;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) {
            Minecraft minecraft = Minecraft.getInstance();
            this.renderer = new BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels()) {
                @Override
                public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
                    renderByItemCallback.render(minecraft, stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
                }
            };
        }
        return renderer;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
        return handTransformPredicate.force(poseStack, player, arm, itemInHand, partialTick, equipProcess, swingProcess);
    }

    @Override
    public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        return armPoseGetter.get(entityLiving, hand, itemStack);
    }

    public static void renderSimpleItem(Minecraft minecraft, ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        minecraft.getItemRenderer().renderModelLists(
                minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 260109),
                stack, packedLight, packedOverlay, poseStack,
                ItemRenderer.getFoilBufferDirect(buffer, Sheets.translucentCullBlockSheet(), true, stack.hasFoil())
        );
    }

    @FunctionalInterface
    public interface RenderByItemCallback {
        void render(Minecraft minecraft, ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay);
    }

    @FunctionalInterface
    public interface HandTransformPredicate {
        boolean force(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess);
    }

    @FunctionalInterface
    public interface ArmPoseGetter {
        @Nullable HumanoidModel.ArmPose get(LivingEntity living, InteractionHand hand, ItemStack itemStack);
    }
}
