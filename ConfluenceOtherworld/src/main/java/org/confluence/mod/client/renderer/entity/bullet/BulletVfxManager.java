package org.confluence.mod.client.renderer.entity.bullet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.init.ModParticleTypes;
import org.confluence.mod.common.item.gun.definition.BulletImpactEffect;
import org.joml.Matrix4f;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

/// 管理普通粒子无法完整表达的短生命周期子弹命中特效。
public final class BulletVfxManager {
    private static final List<ActiveEffect> ACTIVE_EFFECTS = new ArrayList<>();
    private static final ResourceLocation SILVER_CROSS_TEXTURE =
            Confluence.asResource("textures/vfx/particles/star_06.png");
    private static final int[] CONFETTI_COLORS = {
            0xEBFF4BB4, 0xEBFFE042, 0xEB44D7FF,
            0xEB697DFF, 0xEB58DC6F, 0xEBFF8B3D
    };

    private BulletVfxManager() {}

    public static void play(BulletImpactEffect effect, Vec3 position) {
        switch (effect) {
            case SILVER_CROSS -> ACTIVE_EFFECTS.add(new SilverCrossEffect(position));
            case PARTY_CONFETTI -> ACTIVE_EFFECTS.add(new ConfettiEffect(position));
            case CRYSTAL_IMPACT -> playCrystalImpact(position);
            case LUMINITE_IMPACT -> playLuminiteImpact(position);
            case CHLOROPHYTE_IMPACT -> playChlorophyteImpact(position);
            case NONE -> {
            }
        }
    }

    public static void tick() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            ACTIVE_EFFECTS.clear();
            return;
        }
        for (int index = ACTIVE_EFFECTS.size() - 1; index >= 0; index--) {
            if (!ACTIVE_EFFECTS.get(index).tick()) {
                ACTIVE_EFFECTS.remove(index);
            }
        }
    }

    public static void render(PortRenderLevelStageEvent event) {
        if (event.getStage() != PortRenderLevelStageEvent.Stage.AFTER_PARTICLES
                || ACTIVE_EFFECTS.isEmpty()) {
            return;
        }
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 cameraPosition = event.getCamera().getPosition();
        for (ActiveEffect effect : ACTIVE_EFFECTS) {
            effect.render(event.getPoseStack(), buffers, cameraPosition);
        }
        buffers.endBatch(RenderStateShardAccessor.TRAIL_RENDER_TYPE);
        buffers.endBatch(BulletRenderTypes.trail(SILVER_CROSS_TEXTURE, true));
    }

    /// 保留 1.21 水晶弹命中时的数量、出生范围和速度分布。
    private static void playCrystalImpact(Vec3 position) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        int count = 14;
        for (int index = 0; index < count; index++) {
            double angle = Math.PI * 2.0D * index / count;
            double speed = 0.025D + level.random.nextDouble() * 0.035D;
            level.addParticle(
                    ParticleTypes.DOLPHIN,
                    position.x + (level.random.nextDouble() - 0.5D) * 0.18D,
                    position.y + (level.random.nextDouble() - 0.5D) * 0.18D,
                    position.z + (level.random.nextDouble() - 0.5D) * 0.18D,
                    Math.cos(angle) * speed,
                    0.015D + level.random.nextDouble() * 0.035D,
                    Math.sin(angle) * speed);
        }
    }

    /// 保留 1.21 夜明弹命中时的粒子、数量和运动参数。
    private static void playLuminiteImpact(Vec3 position) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        int count = 16;
        for (int index = 0; index < count; index++) {
            double angle = Math.PI * 2.0D * index / count;
            double speed = 0.025D + level.random.nextDouble() * 0.05D;
            level.addParticle(
                    ModParticleTypes.LUMINITE_IMPACT.get(),
                    position.x + (level.random.nextDouble() - 0.5D) * 0.20D,
                    position.y + (level.random.nextDouble() - 0.5D) * 0.20D,
                    position.z + (level.random.nextDouble() - 0.5D) * 0.20D,
                    Math.cos(angle) * speed,
                    0.025D + level.random.nextDouble() * 0.045D,
                    Math.sin(angle) * speed);
        }
    }

    /// 保留 1.21 叶绿弹命中时的数量、出生范围和速度分布。
    private static void playChlorophyteImpact(Vec3 position) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        int count = 9;
        for (int index = 0; index < count; index++) {
            double angle = Math.PI * 2.0D * index / count;
            double speed = 0.05D + level.random.nextDouble() * 0.045D;
            level.addParticle(
                    ParticleTypes.ENCHANTED_HIT,
                    position.x + (level.random.nextDouble() - 0.5D) * 0.32D,
                    position.y + (level.random.nextDouble() - 0.5D) * 0.32D,
                    position.z + (level.random.nextDouble() - 0.5D) * 0.32D,
                    Math.cos(angle) * speed,
                    0.02D + level.random.nextDouble() * 0.08D,
                    Math.sin(angle) * speed);
        }
    }

    private interface ActiveEffect {
        boolean tick();

        void render(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPosition);
    }

    private static final class SilverCrossEffect implements ActiveEffect {
        private static final int LIFETIME = 5;
        private final Vec3 position;
        private int age;

        private SilverCrossEffect(Vec3 position) {
            this.position = position;
        }

        @Override
        public boolean tick() {
            return ++age < LIFETIME;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPosition) {
            float fade = 1.0F - age / (float) LIFETIME;
            int color = FastColor.ARGB32.color(Math.round(255.0F * fade), 255, 255, 255);
            float size = 0.36F + age * 0.004F;
            renderSprite(poseStack, bufferSource, cameraPosition, position, color, size);
        }
    }

    private static final class ConfettiEffect implements ActiveEffect {
        private static final int COUNT = 56;
        private final List<ConfettiPiece> pieces = new ArrayList<>(COUNT);

        private ConfettiEffect(Vec3 position) {
            double phase = (position.x * 0.37D + position.y * 0.13D + position.z * 0.71D) * 0.25D;
            for (int index = 0; index < COUNT; index++) {
                double angle = Math.PI * 2.0D * index / COUNT + phase;
                double horizontalSpeed = 0.14D + index % 5 * 0.035D;
                Vec3 velocity = new Vec3(
                        Math.cos(angle) * horizontalSpeed,
                        0.10D + (index * 7) % 5 * 0.035D,
                        Math.sin(angle) * horizontalSpeed);
                pieces.add(new ConfettiPiece(
                        position.add(velocity.scale(0.15D)), velocity,
                        CONFETTI_COLORS[index % CONFETTI_COLORS.length],
                        0.075F + index % 9 * 0.012F,
                        0.012F + index % 4 * 0.004F,
                        (float) (angle * 1.7D + index * 0.43D),
                        (index % 2 == 0 ? 1.0F : -1.0F) * (0.08F + index % 4 * 0.025F),
                        120 + index % 25));
            }
        }

        @Override
        public boolean tick() {
            pieces.removeIf(piece -> !piece.tick());
            return !pieces.isEmpty();
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPosition) {
            for (ConfettiPiece piece : pieces) {
                piece.render(poseStack, bufferSource, cameraPosition);
            }
        }
    }

    private static final class ConfettiPiece {
        private Vec3 position;
        private Vec3 velocity;
        private final int color;
        private final float length;
        private final float width;
        private float rotation;
        private final float rotationSpeed;
        private final int lifetime;
        private int age;

        private ConfettiPiece(Vec3 position, Vec3 velocity, int color, float length, float width,
                              float rotation, float rotationSpeed, int lifetime) {
            this.position = position;
            this.velocity = velocity;
            this.color = color;
            this.length = length;
            this.width = width;
            this.rotation = rotation;
            this.rotationSpeed = rotationSpeed;
            this.lifetime = lifetime;
        }

        private boolean tick() {
            position = position.add(velocity);
            velocity = velocity.add(0.0D, -0.007D, 0.0D).scale(0.985D);
            rotation += rotationSpeed;
            return ++age < lifetime;
        }

        private void render(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPosition) {
            float fade = 1.0F - age / (float) lifetime;
            int faded = FastColor.ARGB32.color(
                    Math.round(FastColor.ARGB32.alpha(color) * fade),
                    FastColor.ARGB32.red(color),
                    FastColor.ARGB32.green(color),
                    FastColor.ARGB32.blue(color));
            renderRectangle(poseStack, bufferSource, cameraPosition, position,
                    faded, length, width, rotation);
        }
    }

    private static void renderRectangle(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Vec3 cameraPosition,
            Vec3 worldPosition,
            int color,
            float length,
            float width,
            float rotation
    ) {
        Vec3 facing = cameraPosition.subtract(worldPosition);
        if (facing.lengthSqr() <= 1.0E-7D) {
            return;
        }
        facing = facing.normalize();
        Vec3 up = Math.abs(facing.y) > 0.98D
                ? new Vec3(1.0D, 0.0D, 0.0D)
                : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = facing.cross(up).normalize();
        up = right.cross(facing).normalize();
        float cosine = (float) Math.cos(rotation);
        float sine = (float) Math.sin(rotation);
        Vec3 lengthOffset = right.scale(cosine).add(up.scale(sine)).scale(length * 0.5D);
        Vec3 widthOffset = right.scale(-sine).add(up.scale(cosine)).scale(width * 0.5D);
        Vec3 center = worldPosition.subtract(cameraPosition);
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderStateShardAccessor.TRAIL_RENDER_TYPE);
        vertex(buffer, matrix, center.subtract(lengthOffset).subtract(widthOffset), color);
        vertex(buffer, matrix, center.add(lengthOffset).subtract(widthOffset), color);
        vertex(buffer, matrix, center.add(lengthOffset).add(widthOffset), color);
        vertex(buffer, matrix, center.subtract(lengthOffset).add(widthOffset), color);
    }

    private static void renderSprite(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Vec3 cameraPosition,
            Vec3 worldPosition,
            int color,
            float size
    ) {
        Vec3 facing = cameraPosition.subtract(worldPosition);
        if (facing.lengthSqr() <= 1.0E-7D) return;
        facing = facing.normalize();
        Vec3 up = Math.abs(facing.y) > 0.98D
                ? new Vec3(1.0D, 0.0D, 0.0D)
                : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = facing.cross(up).normalize();
        up = right.cross(facing).normalize();
        Vec3 center = worldPosition.subtract(cameraPosition);
        Vec3 rightOffset = right.scale(size * 0.5D);
        Vec3 upOffset = up.scale(size * 0.5D);
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(
                BulletRenderTypes.trail(SILVER_CROSS_TEXTURE, true));
        spriteVertex(buffer, matrix, center.subtract(rightOffset).subtract(upOffset), color, 0.0F, 1.0F);
        spriteVertex(buffer, matrix, center.add(rightOffset).subtract(upOffset), color, 1.0F, 1.0F);
        spriteVertex(buffer, matrix, center.add(rightOffset).add(upOffset), color, 1.0F, 0.0F);
        spriteVertex(buffer, matrix, center.subtract(rightOffset).add(upOffset), color, 0.0F, 0.0F);
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 position, int color) {
        buffer.vertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .color(color)
                .endVertex();
    }

    private static void spriteVertex(VertexConsumer buffer, Matrix4f matrix, Vec3 position,
                                     int color, float u, float v) {
        buffer.vertex(matrix, (float) position.x, (float) position.y, (float) position.z)
                .color(color)
                .uv(u, v)
                .uv2(0xF000F0)
                .endVertex();
    }
}
