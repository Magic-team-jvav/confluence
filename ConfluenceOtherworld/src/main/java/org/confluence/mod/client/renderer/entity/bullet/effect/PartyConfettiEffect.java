package org.confluence.mod.client.renderer.entity.bullet.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class PartyConfettiEffect implements ActiveBulletVfx {
    private static final int[] COLORS = {0xEBFF4BB4, 0xEBFFE042, 0xEB44D7FF, 0xEB697DFF, 0xEB58DC6F, 0xEBFF8B3D};
    private static final int COUNT = 56;
    private final List<Piece> pieces = new ArrayList<>(COUNT);

    public PartyConfettiEffect(Vec3 position) {
        double phase = (position.x * 0.37D + position.y * 0.13D + position.z * 0.71D) * 0.25D;
        for (int index = 0; index < COUNT; index++) {
            double angle = Math.PI * 2.0D * index / COUNT + phase;
            double horizontalSpeed = 0.14D + index % 5 * 0.035D;
            Vec3 velocity = new Vec3(Math.cos(angle) * horizontalSpeed, 0.10D + (index * 7) % 5 * 0.035D, Math.sin(angle) * horizontalSpeed);
            pieces.add(new Piece(position.add(velocity.scale(0.15D)), velocity, COLORS[index % COLORS.length],
                    0.075F + index % 9 * 0.012F, 0.012F + index % 4 * 0.004F,
                    (float) (angle * 1.7D + index * 0.43D),
                    (index % 2 == 0 ? 1.0F : -1.0F) * (0.08F + index % 4 * 0.025F), 120 + index % 25));
        }
    }

    @Override
    public boolean tick(ClientLevel level) {
        pieces.removeIf(piece -> !piece.tick());
        return !pieces.isEmpty();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPosition) {
        for (Piece piece : pieces) piece.render(poseStack, bufferSource, cameraPosition);
    }

    private static final class Piece {
        private Vec3 position;
        private Vec3 velocity;
        private final int color;
        private final float length;
        private final float width;
        private float rotation;
        private final float rotationSpeed;
        private final int lifetime;
        private int age;

        private Piece(Vec3 position, Vec3 velocity, int color, float length, float width, float rotation, float rotationSpeed, int lifetime) {
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
            int faded = BulletVfxRenderUtil.fadeColor(color, 1.0F - age / (float) lifetime);
            BulletVfxRenderUtil.rectangle(poseStack, bufferSource, cameraPosition, position, faded, length, width, rotation);
        }
    }
}
