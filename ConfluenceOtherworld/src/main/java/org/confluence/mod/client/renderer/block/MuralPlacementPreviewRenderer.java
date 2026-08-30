package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.common.block.common.MuralBlock;
import org.confluence.mod.common.init.block.DecorativeBlocks;

public final class MuralPlacementPreviewRenderer {
    private static final double EPSILON = 0.002;

    private MuralPlacementPreviewRenderer() {}

    public static void render(Minecraft minecraft, LocalPlayer player, RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if (!(minecraft.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK)
            return;

        InteractionHand hand = getMuralHand(player);
        if (hand == null) return;

        ItemStack stack = player.getItemInHand(hand);
        int width = MuralBlock.getMuralWidth(stack);
        int height = MuralBlock.getMuralHeight(stack);
        if (width < 1 || height < 1) return;

        BlockPlaceContext context = new BlockPlaceContext(player, hand, stack, hit);
        BlockPos pos = context.getClickedPos();
        BlockState placementState = DecorativeBlocks.MURAL_BLOCK.get().getStateForPlacement(context);
        Direction facing = placementState == null ? context.getHorizontalDirection() : placementState.getValue(MuralBlock.FACING);

        renderBox(
                event.getPoseStack(),
                minecraft.renderBuffers().bufferSource(),
                minecraft.gameRenderer.getMainCamera(),
                pos,
                facing,
                width,
                height,
                placementState != null
        );
    }

    private static InteractionHand getMuralHand(LocalPlayer player) {
        if (player.getMainHandItem().is(DecorativeBlocks.MURAL_BLOCK.get().asItem())) {
            return InteractionHand.MAIN_HAND;
        }
        if (player.getOffhandItem().is(DecorativeBlocks.MURAL_BLOCK.get().asItem())) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    private static void renderBox(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Camera camera, BlockPos pos, Direction facing, int width, int height, boolean canPlace) {
        int xOffset = -facing.getStepZ();
        int zOffset = facing.getStepX();
        double xEnd = xOffset * (width - 1);
        double zEnd = zOffset * (width - 1);
        double minX = Math.min(0.0, xEnd) - EPSILON;
        double maxX = Math.max(1.0, xEnd + 1.0) + EPSILON;
        double minZ = Math.min(0.0, zEnd) - EPSILON;
        double maxZ = Math.max(1.0, zEnd + 1.0) + EPSILON;

        Vec3 cameraPos = camera.getPosition();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        poseStack.pushPose();
        poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
        if (canPlace) {
            LevelRenderer.renderLineBox(poseStack, consumer, minX, -EPSILON, minZ, maxX, height + EPSILON, maxZ, 1.0F, 1.0F, 1.0F, 0.75F);
        } else {
            LevelRenderer.renderLineBox(poseStack, consumer, minX, -EPSILON, minZ, maxX, height + EPSILON, maxZ, 1.0F, 0.2F, 0.2F, 0.8F);
        }
        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }
}
