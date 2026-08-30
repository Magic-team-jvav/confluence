package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.VoidBlock;

public class VoidBlockRenderer implements BlockEntityRenderer<VoidBlock.VoidBlockEntity> {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final NormalNoise normalNoise = NormalNoise.create(RandomSource.create(0), new NormalNoise.NoiseParameters(-5, 1.0, 1.0, 1.0, 1.0, 1.0));
    private static final ResourceLocation VOID_SIDE = Confluence.asResource("textures/block/fluid/void_side.png");
    private static final ResourceLocation VOID_TOP = Confluence.asResource("textures/block/fluid/void_top.png");

    @Override
    public int getViewDistance() {
        return MC.options.renderDistance().get() * 16;
    }

    @Override
    public void render(VoidBlock.VoidBlockEntity voidBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Player player = MC.player;
        BlockState state = voidBlockEntity.getBlockState();
        Level level = voidBlockEntity.getLevel();
        BlockPos pos = voidBlockEntity.getBlockPos();
        if ((level == null)) return;
        if (player == null || voidBlockEntity.getLevel() == null) return;

        FluidState fluidState = state.getFluidState();
        if (fluidState.isEmpty()) {
            return;
        }
        float[] heights = getFluidCornerHeights(level, pos, fluidState);

        boolean northSide = !level.getBlockState(pos.north()).is(state.getBlock());
        boolean southSide = !level.getBlockState(pos.south()).is(state.getBlock());
        boolean westSide = !level.getBlockState(pos.west()).is(state.getBlock());
        boolean eastSide = !level.getBlockState(pos.east()).is(state.getBlock());

        boolean shouldUseNWSEDiagonal = (heights[0] + heights[2]) >= (heights[1] + heights[3]);

        long time = level.getGameTime();
        double scale = 3;
        double timeScale = 0.2;
        double noiseYOffset = normalNoise.getValue(
                pos.getX() * scale,
                time * timeScale,
                pos.getZ() * scale
        );
        double noiseVal = normalNoise.getValue(
                pos.getX() * scale,
                (pos.getY() + noiseYOffset * 10) * scale,
                pos.getZ() * scale
        );

        if (!level.getBlockState(pos.above()).is(state.getBlock())) {
            poseStack.pushPose();
            {
                VertexConsumer quadBuffer = bufferSource.getBuffer(RenderType.entityTranslucentCull(VOID_TOP));

                int alpha = Mth.clamp((int) ((noiseVal + 2) * 64), 10, 200) + 1; // 透明度 (0-255)
                int r = 255, g = 255, b = 255;
                int light = 255;
                float uvNorth = 0.0F;
                float uvSouth = 1.0F;
                float uvWest = 0.0F;
                float uvEast = 1.0F;

                PoseStack.Pose pose = poseStack.last();
                if (shouldUseNWSEDiagonal) {
                    quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                            .setColor(r, g, b, alpha)
                            .setUv(uvWest, uvNorth)
                            .setOverlay(packedOverlay)
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                            .setColor(r, g, b, alpha)
                            .setUv(uvWest, uvSouth)
                            .setOverlay(packedOverlay)
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                            .setColor(r, g, b, alpha)
                            .setUv(uvEast, uvSouth)
                            .setOverlay(packedOverlay)
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                            .setColor(r, g, b, alpha)
                            .setUv(uvEast, uvNorth)
                            .setOverlay(packedOverlay)
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                } else {
                    quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                            .setColor(r, g, b, alpha)
                            .setUv(uvEast, uvNorth)
                            .setOverlay(packedOverlay)
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                            .setColor(r, g, b, alpha)
                            .setUv(uvWest, uvNorth)
                            .setOverlay(packedOverlay)
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                            .setColor(r, g, b, alpha)
                            .setUv(uvWest, uvSouth)
                            .setOverlay(packedOverlay)
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                            .setColor(r, g, b, alpha)
                            .setUv(uvEast, uvSouth)
                            .setOverlay(packedOverlay)
                            .setLight(light)
                            .setNormal(pose, 0.0F, 1.0F, 0.0F);
                }
            }
            poseStack.popPose();

            poseStack.pushPose();
            {
                VertexConsumer quadBuffer = bufferSource.getBuffer(RenderType.entityTranslucentCull(VOID_SIDE));

                int alpha = Mth.clamp((int) ((noiseVal + 2) * 128), 0, 255); // 透明度 (0-255)
                int r = 255, g = 255, b = 255;
                int light = 255;
                float uvNorth = 0.0F;
                float uvSouth = 1.0F;
                float uvWest = 0.0F;
                float uvEast = 1.0F;

                PoseStack.Pose pose = poseStack.last();

                if (northSide) {
                    if (shouldUseNWSEDiagonal) {
                        quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    } else {
                        quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    }
                }
                if (southSide) {
                    if (shouldUseNWSEDiagonal) {
                        quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    } else {
                        quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    }
                }
                if (eastSide) {
                    if (shouldUseNWSEDiagonal) {
                        quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    } else {
                        quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    }
                }
                if (westSide) {
                    if (shouldUseNWSEDiagonal) {
                        quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    } else {
                        quadBuffer.addVertex(pose, 1.0F, heights[3], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[0], 0.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvEast, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 0.0F, heights[1], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvNorth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                        quadBuffer.addVertex(pose, 1.0F, heights[2], 1.0F)
                                .setColor(r, g, b, alpha)
                                .setUv(uvWest, uvSouth)
                                .setOverlay(packedOverlay)
                                .setLight(light)
                                .setNormal(pose, 0.0F, 1.0F, 0.0F);
                    }
                }
            }
            poseStack.popPose();
        }

    }

    public static float[] getFluidCornerHeights(BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        Fluid fluid = fluidState.getType();
        BlockState blockState = level.getBlockState(pos);

        float selfHeight = getHeight(level, fluid, pos, blockState, fluidState);

        if (selfHeight >= 1.0F) {
            return new float[]{1.0F, 1.0F, 1.0F, 1.0F};
        }

        BlockState northBlock = level.getBlockState(pos.north());
        FluidState northFluid = northBlock.getFluidState();

        BlockState southBlock = level.getBlockState(pos.south());
        FluidState southFluid = southBlock.getFluidState();

        BlockState eastBlock = level.getBlockState(pos.east());
        FluidState eastFluid = eastBlock.getFluidState();

        BlockState westBlock = level.getBlockState(pos.west());
        FluidState westFluid = westBlock.getFluidState();

        float heightNorth = getHeight(level, fluid, pos.north(), northBlock, northFluid);
        float heightSouth = getHeight(level, fluid, pos.south(), southBlock, southFluid);
        float heightEast = getHeight(level, fluid, pos.east(), eastBlock, eastFluid);
        float heightWest = getHeight(level, fluid, pos.west(), westBlock, westFluid);

        float[] corners = new float[4];

        corners[0] = calculateAverageHeight(level, fluid, selfHeight, heightNorth, heightWest,
                pos.relative(Direction.NORTH).relative(Direction.WEST));

        corners[1] = calculateAverageHeight(level, fluid, selfHeight, heightSouth, heightWest,
                pos.relative(Direction.SOUTH).relative(Direction.WEST));

        corners[2] = calculateAverageHeight(level, fluid, selfHeight, heightSouth, heightEast,
                pos.relative(Direction.SOUTH).relative(Direction.EAST));

        corners[3] = calculateAverageHeight(level, fluid, selfHeight, heightNorth, heightEast,
                pos.relative(Direction.NORTH).relative(Direction.EAST));

        return corners;
    }
    private static float calculateAverageHeight(BlockAndTintGetter level, Fluid fluid,
                                                float currentHeight, float height1, float height2, BlockPos pos) {

        if (height2 >= 1.0F || height1 >= 1.0F) {
            return 1.0F;
        }

        float[] weightedSum = new float[2];

        if (height2 > 0.0F || height1 > 0.0F) {
            float diagHeight = getHeight(level, fluid, pos);
            if (diagHeight >= 1.0F) {
                return 1.0F;
            }
            addWeightedHeight(weightedSum, diagHeight);
        }

        addWeightedHeight(weightedSum, currentHeight);
        addWeightedHeight(weightedSum, height2);
        addWeightedHeight(weightedSum, height1);

        return weightedSum[0] / weightedSum[1];
    }

    private static void addWeightedHeight(float[] output, float height) {
        if (height >= 0.8F) {
            output[0] += height * 10.0F;
            output[1] += 10.0F;
        } else if (height >= 0.0F) {
            output[0] += height;
            output[1] += 1.0F;
        }
    }

    private static float getHeight(BlockAndTintGetter level, Fluid fluid,
                                   BlockPos pos, BlockState blockState, FluidState fluidState) {

        if (fluid.isSame(fluidState.getType())) {
            BlockState aboveBlock = level.getBlockState(pos.above());
            FluidState aboveFluid = aboveBlock.getFluidState();

            if (fluid.isSame(aboveFluid.getType())) {
                return 1.0F;
            } else {
                return fluidState.getOwnHeight();
            }
        }
        else {
            if (blockState.isSolid()) {
                return -1.0F;
            } else {
                return 0.0F;
            }
        }
    }

    private static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        FluidState fluidState = blockState.getFluidState();
        return getHeight(level, fluid, pos, blockState, fluidState);
    }
}
