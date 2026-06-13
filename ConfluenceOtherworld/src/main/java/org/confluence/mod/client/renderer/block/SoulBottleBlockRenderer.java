package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import org.confluence.mod.common.block.functional.BaseSoulInABottleBlock;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Map;

import static org.confluence.lib.util.LibRenderUtils.drawCube;

public class SoulBottleBlockRenderer implements BlockEntityRenderer<BaseSoulInABottleBlock.BEntity> {

    private static final Map<Block, Vector3i> SOUL_COLORS = Map.of(
            FunctionalBlocks.SOUL_OF_FLIGHT_IN_A_BOTTLE.get(), new Vector3i(0x0fa7d3, 0xcbfafc, 0x8bebef),
            FunctionalBlocks.SOUL_OF_LIGHT_IN_A_BOTTLE.get(), new Vector3i(0xff8dd3, 0xffffeb, 0xffd4ef),
            FunctionalBlocks.SOUL_OF_FRIGHT_IN_A_BOTTLE.get(), new Vector3i(0xff6639, 0xffe4d6, 0xffb37d),
            FunctionalBlocks.SOUL_OF_NIGHT_IN_A_BOTTLE.get(), new Vector3i(0xa25fea, 0xfdd4ff, 0xe08dff),
            FunctionalBlocks.SOUL_OF_MIGHT_IN_A_BOTTLE.get(), new Vector3i(0x4babff, 0xffffeb, 0xb4deff),
            FunctionalBlocks.SOUL_OF_SIGHT_IN_A_BOTTLE.get(), new Vector3i(0x26d17b, 0xffffeb, 0xc7ffcc),
            FunctionalBlocks.SOUL_OF_BRIGHT_IN_A_BOTTLE.get(), new Vector3i(0xffca49, 0xfdffdb, 0xfff594),
            FunctionalBlocks.SOUL_OF_VOIGHT_IN_A_BOTTLE.get(), new Vector3i(0x1e0034, 0xe3bbff, 0x9f1bff)
    );
    private final RandomSource RANDOM = RandomSource.create();
    private float ROTATE00 = RANDOM.nextFloat() * Mth.TWO_PI;
    private float ROTATE01 = RANDOM.nextFloat() * Mth.TWO_PI;
    private float ROTATE10 = RANDOM.nextFloat() * Mth.TWO_PI;
    private float ROTATE11 = RANDOM.nextFloat() * Mth.TWO_PI;
    private final float ROTATE1OFFSET = RANDOM.nextFloat() * 0.05F + 0.075F;
    private final float[] SIZE = new float[]{RANDOM.nextFloat(), RANDOM.nextFloat()};
    private final float[] SIZE_OFFSET = new float[]{RANDOM.nextFloat() * 0.05F + 0.075F, RANDOM.nextFloat() * 0.05F + 0.075F};
    private final boolean[] SIZE_UP = new boolean[]{true, true};
    private long TIME_BEFORE = 0;

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
        Vector3f offsetPos = new Vector3f(0.5F, (blockEntity.getBlockState() == blockEntity.getBlockState().setValue(LanternBlock.HANGING, true)) ? 0.425F : 0.3F, 0.5F);
        Vector3f entityMainPos = new Vector3f(blockPos.getX(), blockPos.getY(), blockPos.getZ());

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
            float sizeGet = SIZE[i];
            float sizeOffset = SIZE_OFFSET[i];
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

            RANDOM.setSeed(blockPos.getX());
            RANDOM.setSeed(blockPos.getY() + RANDOM.nextInt(100));
            RANDOM.setSeed(blockPos.getZ() + RANDOM.nextInt(100));

            int step = RANDOM.nextInt(20);
            for (int j = 0; j < step; j++) {
                sizeGet += sizeUp ? sizeOffset : -sizeOffset;
                if (sizeGet > 1) {
                    sizeGet = 1;
                    sizeUp = false;
                } else if (sizeGet < 0) {
                    sizeGet = 0;
                    sizeUp = true;
                }
            }

            float pi = 1 - 2 * i;
            float radius = 0.125F;
            float cubeSize = 0.125F;
            Vector3f offset = new Vector3f(
                    (Mth.cos(ROTATE10 + RANDOM.nextFloat() * 10) * Mth.cos(ROTATE11 + RANDOM.nextFloat() * 10) * radius) * pi,
                    Mth.sin(ROTATE10 + RANDOM.nextFloat() * 10) * radius * pi,
                    (-Mth.cos(ROTATE10 + RANDOM.nextFloat() * 10) * Mth.sin(ROTATE11 + RANDOM.nextFloat() * 10) * radius) * pi
            ).add(offsetPos);

            float rotate0 = RANDOM.nextFloat() * 10;
            float rotate1 = RANDOM.nextFloat() * 10;
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());
            drawCube(poseStack, sizeGet * cubeSize, r0, g0, b0, 255, entityMainPos, offset, true, ROTATE00 * pi * ROTATE1OFFSET + rotate0, ROTATE01 * pi * ROTATE1OFFSET + rotate1, consumer);
            drawCube(poseStack, sizeGet * cubeSize + 0.015625F, r1, g1, b1, 255, entityMainPos, offset, false, ROTATE00 * pi * ROTATE1OFFSET + rotate0, ROTATE01 * pi * ROTATE1OFFSET + rotate1, consumer);
            drawCube(poseStack, sizeGet * cubeSize + 0.078125F, r2, g2, b2, 255, entityMainPos, offset, false, ROTATE00 * pi * ROTATE1OFFSET + rotate0, ROTATE01 * pi * ROTATE1OFFSET + rotate1, consumer);
        }
    }
}
