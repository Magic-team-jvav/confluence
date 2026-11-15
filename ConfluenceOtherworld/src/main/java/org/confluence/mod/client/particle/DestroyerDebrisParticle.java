package org.confluence.mod.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/// 纯客户端出土碎块：绘制来源方块的完整模型，不创建下落方块实体或改动地形。
public final class DestroyerDebrisParticle extends Particle {
    /// 每次出土飞出的方块数量与存在时间，单位个/tick。
    private static final int COUNT = 40, LIFE = 40;
    /// 方块模型的尺寸范围，相对一个完整方块。
    private static final float MIN_SCALE = 0.8F, MAX_SCALE = 1.5F;
    /// 水平散射速度、初始上抛速度与重力，单位格/tick、格/tick²。
    private static final double SIDE_SPEED = 0.5, MIN_RISE = 0.3, MAX_RISE = 0.55, GRAVITY = 0.035;
    /// 消失前缩小的时间，单位 tick。
    private static final int SHRINK_TICKS = 8;
    /// 方块翻滚角速度，单位度/tick。
    private static final float SPIN = 9.0F;
    private final BlockState state;
    private final Vec3[] velocities = new Vec3[COUNT];
    private final float[] sizes = new float[COUNT];

    private DestroyerDebrisParticle(ClientLevel level, BlockPos origin, BlockState state) {
        super(level, origin.getX() + 0.5, origin.getY() + 1.0, origin.getZ() + 0.5);
        this.state = state;
        lifetime = LIFE;
        hasPhysics = false;
        for (int i = 0; i < COUNT; i++) {
            double angle = Math.PI * 2 * i / COUNT;
            double speed = SIDE_SPEED * (0.5 + random.nextDouble());
            velocities[i] = new Vec3(Math.cos(angle) * speed, Mth.lerp(random.nextDouble(), MIN_RISE, MAX_RISE), Math.sin(angle) * speed);
            sizes[i] = Mth.lerp(random.nextFloat(), MIN_SCALE, MAX_SCALE);
        }
    }

    public static void spawn(BlockPos origin, BlockState state) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || state.getRenderShape() != RenderShape.MODEL) return;
        client.particleEngine.add(new DestroyerDebrisParticle(client.level, origin, state));
    }

    @Override
    public ParticleRenderType getRenderType() {return ParticleRenderType.CUSTOM;}

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTick) {
        Minecraft client = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = client.renderBuffers().bufferSource();
        Vec3 cameraPos = camera.getPosition();
        float time = age + partialTick;
        float shrink = Math.min(1, (LIFE - time) / SHRINK_TICKS);
        PoseStack pose = new PoseStack();
        for (int i = 0; i < COUNT; i++) {
            Vec3 velocity = velocities[i];
            pose.pushPose();
            pose.translate(x + velocity.x * time - cameraPos.x,
                    y + velocity.y * time - GRAVITY * time * time / 2 - cameraPos.y,
                    z + velocity.z * time - cameraPos.z);
            pose.mulPose(Axis.YP.rotationDegrees(time * SPIN + i * 360.0F / COUNT));
            pose.mulPose(Axis.XP.rotationDegrees(time * SPIN));
            float scale = sizes[i] * shrink;
            pose.scale(scale, scale, scale);
            pose.translate(-0.5, -0.5, -0.5);
            client.getBlockRenderer().renderSingleBlock(state, pose, buffers, getLightColor(partialTick), OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }
        buffers.endBatch();
    }
}
