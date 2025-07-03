package org.confluence.mod.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.particle.WholeItemParticleOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WholeItemParticle extends TextureSheetParticle {
    private final ItemStack item;
    private final float roll;
    private final float yaw;
    private final float pitch;

    public WholeItemParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ItemStack item, float gravity, int life) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.item = item;
        this.gravity = gravity;
        this.lifetime = life;

        this.roll = random.nextFloat() * Mth.TWO_PI;
        this.yaw = random.nextFloat() * Mth.TWO_PI;
        this.pitch = random.nextFloat() * Mth.TWO_PI;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, Camera camera, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = new PoseStack();

        double x = Mth.lerp(partialTicks, this.xo, this.x);
        double y = Mth.lerp(partialTicks, this.yo, this.y);
        double z = Mth.lerp(partialTicks, this.zo, this.z);

        poseStack.pushPose();
        poseStack.translate(x - camera.getPosition().x, y - camera.getPosition().y, z - camera.getPosition().z);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotation(this.roll).rotateX(this.pitch).rotateY(this.yaw));

        mc.getItemRenderer().renderStatic(
                item,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                mc.renderBuffers().bufferSource(),
                null,
                0
        );
        mc.renderBuffers().bufferSource().endBatch();

        poseStack.popPose();
    }

    public static class Provider implements ParticleProvider<WholeItemParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(WholeItemParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WholeItemParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options.item(), options.gravity(), options.life());
        }
    }
}
