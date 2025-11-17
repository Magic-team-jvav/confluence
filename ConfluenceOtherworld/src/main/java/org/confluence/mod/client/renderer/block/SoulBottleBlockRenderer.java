package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import org.confluence.mod.common.block.functional.BaseSoulInABottleBlock;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.Map;

import static org.confluence.lib.util.RenderUtils.drawCube;

public class SoulBottleBlockRenderer implements BlockEntityRenderer<BaseSoulInABottleBlock.BEntity> {

    private final RandomSource RANDOM = RandomSource.create();
    private double ROTATE00 = RANDOM.nextDouble() * Math.PI * 2;
    private double ROTATE01 = RANDOM.nextDouble() * Math.PI * 2;
    private double ROTATE10 = RANDOM.nextDouble() * Math.PI * 2;
    private double ROTATE11 = RANDOM.nextDouble() * Math.PI * 2;
    private double ROTATE1OFFSET = RANDOM.nextDouble() * 0.05 + 0.075;
    private double[] SIZE = new double[]{RANDOM.nextDouble(), RANDOM.nextDouble()};
    private double[] SIZE_OFFSET = new double[]{RANDOM.nextDouble() * 0.05 + 0.075, RANDOM.nextDouble() * 0.05 + 0.075};
    private boolean[] SIZE_UP = new boolean[]{true, true};
    private long TIME_BEFORE = 0;

    private static final Map<Block, Vector3i> SOUL_COLORS = Map.of(
            FunctionalBlocks.SOUL_OF_FLIGHT_IN_A_BOTTLE.get(), new Vector3i(0x0fa7d3, 0xcbfafc, 0x8bebef),
            FunctionalBlocks.SOUL_OF_LIGHT_IN_A_BOTTLE.get(), new Vector3i(0xff8dd3, 0xffffeb, 0xffd4ef),
            FunctionalBlocks.SOUL_OF_FRIGHT_IN_A_BOTTLE.get(), new Vector3i(0xff6639, 0xffe4d6, 0xffb37d),
            FunctionalBlocks.SOUL_OF_NIGHT_IN_A_BOTTLE.get(), new Vector3i(0xa25fea, 0xfdd4ff, 0xe08dff),
            FunctionalBlocks.SOUL_OF_MIGHT_IN_A_BOTTLE.get(), new Vector3i(0x4babff, 0xffffeb, 0xb4deff),
            FunctionalBlocks.SOUL_OF_SIGHT_IN_A_BOTTLE.get(), new Vector3i(0x26d17b, 0xffffeb, 0xc7ffcc),
            FunctionalBlocks.SOUL_OF_BRIGHT_IN_A_BOTTLE.get(), new Vector3i(0xffca49, 0xfdffdb, 0xfff594)
    );

    public SoulBottleBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BaseSoulInABottleBlock.BEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {

        final long timeVariable = (System.currentTimeMillis() / 20) % 10000;
        boolean updata = (TIME_BEFORE != timeVariable);
        TIME_BEFORE = timeVariable;

        if (updata) {
            ROTATE00 += 0.1;
            ROTATE01 += 0.1;
            ROTATE10 += 0.07;
            ROTATE11 += 0.07;
        }

        BlockPos blockPos = blockEntity.getBlockPos();
        Vector3d offsetPos = new Vector3d(0.5, (blockEntity.getBlockState() == blockEntity.getBlockState().setValue(LanternBlock.HANGING, true)) ? 0.425 : 0.3, 0.5);
        Vector3d entityMainPos = new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        Vector3i colors = SOUL_COLORS.getOrDefault(
                blockEntity.getBlockState().getBlock(),
                new Vector3i(0x0fa7d3, 0xcbfafc, 0x8bebef) // 默认颜色
        );

        int color0 = colors.x;
        int r0 = (color0 >> 16) & 0xFF;
        int g0 = (color0 >> 8) & 0xFF;
        int b0 = color0 & 0xFF;

        int color1 = colors.y;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int color2 = colors.z;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        for (int i = 0; i < 2; i++) {
            double sizeGet = SIZE[i];
            double sizeOffset = SIZE_OFFSET[i];
            boolean sizeUp = SIZE_UP[i];
            sizeGet += sizeUp ? sizeOffset : -sizeOffset;
            if (sizeGet > 1) {
                sizeGet = 1;
                sizeUp = false;
            } else if (sizeGet < 0) {
                sizeGet = 0;
                sizeUp = true;
            }
            if (updata) {
                SIZE[i] = sizeGet;
                SIZE_UP[i] = sizeUp;
            }

            double pi = 1 - 2 * i;
            double radius = 0.125;
            double cubeSize = 0.125;
            Vector3d offset = new Vector3d((Math.cos(ROTATE10) * Math.cos(ROTATE11) * radius) * pi, Math.sin(ROTATE10) * radius * pi, (-Math.cos(ROTATE10) * Math.sin(ROTATE11) * radius) * pi).add(offsetPos);

            drawCube(poseStack, bufferSource, sizeGet * cubeSize, r0, g0, b0, 255, entityMainPos, offset, true, ROTATE00 * pi * ROTATE1OFFSET, ROTATE01 * pi * ROTATE1OFFSET);
            drawCube(poseStack, bufferSource, sizeGet * cubeSize + 0.015625, r1, g1, b1, 255, entityMainPos, offset, false, ROTATE00 * pi * ROTATE1OFFSET, ROTATE01 * pi * ROTATE1OFFSET);
            drawCube(poseStack, bufferSource, sizeGet * cubeSize + 0.078125, r2, g2, b2, 255, entityMainPos, offset, false, ROTATE00 * pi * ROTATE1OFFSET, ROTATE01 * pi * ROTATE1OFFSET);
        }
    }
}
