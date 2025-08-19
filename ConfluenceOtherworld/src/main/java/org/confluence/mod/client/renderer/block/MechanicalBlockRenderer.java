package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.terra_curio.client.handler.InformationHandler;

import java.util.Set;

public class MechanicalBlockRenderer<E extends AbstractMechanicalBlock.BEntity> implements BlockEntityRenderer<E> {
    @Override
    public boolean shouldRenderOffScreen(E blockEntity) {
        return InformationHandler.hasMechanicalView();
    }

    @Override
    public int getViewDistance() {
        return InformationHandler.hasMechanicalView() ? 256 : BlockEntityRenderer.super.getViewDistance();
    }

    @Override
    public boolean shouldRender(AbstractMechanicalBlock.BEntity blockEntity, Vec3 cameraPos) {
        return InformationHandler.hasMechanicalView() && blockEntity.getBlockPos().getCenter().multiply(1.0, 0.0, 1.0).closerThan(cameraPos.multiply(1.0, 0.0, 1.0), getViewDistance());
    }

    @Override
    public AABB getRenderBoundingBox(E blockEntity) {
        return AABB.INFINITE;
    }

    @Override
    public void render(E blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        long gameTime = Minecraft.getInstance().level == null ? System.currentTimeMillis() / 50 : Minecraft.getInstance().level.getGameTime();
        Vec3 vec31 = blockEntity.getBlockPos().getCenter();
        for (Int2ObjectMap.Entry<Set<BlockPos>> entry : blockEntity.getConnectedPoses().int2ObjectEntrySet()) {
            int color = entry.getIntKey();
            for (BlockPos pos : entry.getValue()) {
                poseStack.pushPose();
                Vec3 subtract = pos.getCenter().subtract(vec31);
                Vec3 normalize = subtract.normalize();
                poseStack.translate(0.5F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI - (float) Math.atan2(normalize.z, normalize.x)));
                poseStack.mulPose(Axis.XP.rotation((float) Math.acos(normalize.y)));
                poseStack.translate(-0.5F, 0.0F, -0.5F);
                int height = (int) Math.round(subtract.length());
                BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION, partialTick, 1.0F, gameTime, 0, height, color, 0.2F, 0.25F);
                poseStack.popPose();
            }
        }
    }
}
