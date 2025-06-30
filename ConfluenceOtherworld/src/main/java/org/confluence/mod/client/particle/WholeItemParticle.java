package org.confluence.mod.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
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

    public WholeItemParticle(ClientLevel level, double x, double y, double z,
                             double xSpeed, double ySpeed, double zSpeed,
                             ItemStack item) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.item = item;
        this.gravity = 1;
        this.lifetime = 60 + this.random.nextInt(10);

        this.roll = (float) (random.nextFloat() * (Math.PI * 2));
        this.yaw = (float) (random.nextFloat() * (Math.PI * 2));
        this.pitch = (float) (random.nextFloat() * (Math.PI * 2));
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
        poseStack.mulPose(Axis.ZP.rotation(this.roll));
        poseStack.mulPose(Axis.XP.rotation(this.pitch));
        poseStack.mulPose(Axis.YP.rotation(this.yaw));

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
            WholeItemParticle particle = new WholeItemParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options.getItem());
            return particle;
        }
    }
}
