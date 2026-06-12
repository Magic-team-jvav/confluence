package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.VoidTreeRootBlock;

public class VoidTreeRootBlockRenderer implements BlockEntityRenderer<VoidTreeRootBlock.BEntity> {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final ResourceLocation VOID_PORTAL = Confluence.asResource("textures/block/void_portal.png");

    private static final long X_MIX = 1640531527L;
    private static final long Y_MIX = 3801415689L;
    private static final long Z_MIX = 2654435761L;

    @Override
    public int getViewDistance() {
        return MC.options.renderDistance().get() * 16;
    }

    @Override
    public void render(VoidTreeRootBlock.BEntity voidBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = voidBlockEntity.getBlockState();
        Level level = voidBlockEntity.getLevel();
        if ((level == null)) return;
        RandomSource random = level.random;
        BlockPos pos = voidBlockEntity.getBlockPos();
        long gameTime = level.getGameTime();

        long x = pos.getX();
        long y = pos.getY();
        long z = pos.getZ();

        long seed = (x * X_MIX) ^ (y * Y_MIX) ^ (z * Z_MIX);

        for (int i = 0; i < 6; i++) {
            Direction direction = Direction.from3DDataValue(i);
            EnumProperty<VoidTreeRootBlock.ConnectType> prop = VoidTreeRootBlock.CONNECTION_PROPERTIES.get(direction);
            if (state.getValue(prop) == VoidTreeRootBlock.ConnectType.CONNECT_BY_PORTAL)
                portal(poseStack, gameTime, direction, partialTick, bufferSource, packedOverlay, random, 60, 1.5F, 0.005F, seed);
            if (state.getValue(prop) == VoidTreeRootBlock.ConnectType.CONNECT_BY_PORTAL)
                portal(poseStack, gameTime, direction, partialTick, bufferSource, packedOverlay, random, 120, 1.5F, 0.0075F, seed);
            if (state.getValue(prop) == VoidTreeRootBlock.ConnectType.CONNECT_BY_PORTAL)
                portal(poseStack, gameTime, direction, partialTick, bufferSource, packedOverlay, random, 0, 2.0F, 0.01F, seed);
        }
    }

    private static void portal(PoseStack poseStack, long gameTime, Direction face, float partialTick, MultiBufferSource bufferSource, int packedOverlay, RandomSource random, float rotate, float scale, float offset, long seed) {

        RandomSource localRandom = RandomSource.create(seed);

        long lowTime = gameTime / 3 + localRandom.nextInt(10);
        float V = (lowTime % 10) * 0.1F;

        float size = scale + random.nextFloat() * 0.2F;
        float hSize = size / 2;


        poseStack.pushPose();
        {
            poseStack.translate(0.5D, 0.5D, 0.5D);

            if (face == Direction.DOWN) {
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
            } else if (face == Direction.NORTH) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            } else if (face == Direction.SOUTH) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            } else if (face == Direction.EAST) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            } else if (face == Direction.WEST) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            }
            poseStack.mulPose(Axis.YP.rotationDegrees(rotate + gameTime * 2 + partialTick + random.nextFloat() * 6 + localRandom.nextInt(361)));

            poseStack.translate(-0.5D, -0.5D, -0.5D);

            VertexConsumer quadBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(VOID_PORTAL));
            int alpha = 255;
            int r = 255, g = 255, b = 255;
            int light = 255;
            PoseStack.Pose pose = poseStack.last();
            quadBuffer.vertex(pose, 0.5F - hSize, 1.0F + offset, 0.5F - hSize)
                    .color(r, g, b, alpha)
                    .uv(0.0F, V)
                    .setOverlay(packedOverlay)
                    .uv2(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
            quadBuffer.vertex(pose, 0.5F - hSize, 1.0F + offset, 0.5F + hSize)
                    .color(r, g, b, alpha)
                    .uv(0.0F, V + 0.1F)
                    .setOverlay(packedOverlay)
                    .uv2(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
            quadBuffer.vertex(pose, 0.5F + hSize, 1.0F + offset, 0.5F + hSize)
                    .color(r, g, b, alpha)
                    .uv(1.0F, V + 0.1F)
                    .setOverlay(packedOverlay)
                    .uv2(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
            quadBuffer.vertex(pose, 0.5F + hSize, 1.0F + offset, 0.5F - hSize)
                    .color(r, g, b, alpha)
                    .uv(1.0F, V)
                    .setOverlay(packedOverlay)
                    .uv2(light)
                    .setNormal(0.0F, 1.0F, 0.0F);
        }
        poseStack.popPose();
    }
}
