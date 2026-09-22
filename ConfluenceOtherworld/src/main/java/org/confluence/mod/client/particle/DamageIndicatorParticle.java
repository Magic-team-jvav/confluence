package org.confluence.mod.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.particle.DamageIndicatorOptions;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class DamageIndicatorParticle extends TextureSheetParticle {
    private final Component text;
    private float factor = 0;
    private float factorOld = 0;
    private int transparency = 0x11;
    private int transparencyOld = 0x11;
    private final boolean big;

    public DamageIndicatorParticle(ClientLevel pLevel, double pX, double pY, double pZ, Component text, boolean big) {
        super(pLevel, pX, pY, pZ);
        this.text = text;
        lifetime = 30;
        this.big = big;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera camera, float pPartialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        Vec3 camPos = camera.getPosition();
        double dx = this.x - camPos.x;
        double dy = Mth.lerp(pPartialTicks, yo, y) - camPos.y;
        double dz = this.z - camPos.z;

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(dx, dy, dz);

        poseStack.mulPose(camera.rotation());
        float f = Mth.lerp(pPartialTicks, factorOld, factor);
        poseStack.scale(-f, -f, f);
        int width = minecraft.font.width(text);
        Matrix4f matrix = new Matrix4f(poseStack.last().pose());
        int color = Mth.lerpInt(pPartialTicks, transparencyOld, transparency) << 24 | 0xFFFFFF;
        minecraft.font.drawInBatch(text.getVisualOrderText(), -width / 2.0F, 0.0F, color, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, getLightColor(pPartialTicks));
        matrix.translate(0.0F, 0.0F, 0.03F);
        minecraft.font.drawInBatch(text.getVisualOrderText(), -width / 2.0F, 0.0F, color, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, getLightColor(pPartialTicks));
        bufferSource.endBatch();
        poseStack.popPose();
    }

    @Override
    public void tick() {
        transparencyOld = transparency;
        factorOld = factor;
        yo = y;
        if (age <= 4) {
            transparency = Math.min(transparency + 80, 255);
            factor = LibMathUtils.cubicBezier(age / 5f, 0, 1, 1, 1) * (big ? 0.06f : 0.04f);
        }
        float add = LibMathUtils.cubicBezier(age / (float) lifetime, 0, 0.8f, 1f, 1) * 0.1f;
        float yOffset = 0.1f - add;
        y += yOffset;
        if (age >= lifetime - 3) {
            transparency = Math.max(transparency - 60, 0);
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        return 15 << 20 | 15 << 4;
    }

    public static class Provider implements ParticleProvider<DamageIndicatorOptions> {
        @Override
        public @Nullable Particle createParticle(DamageIndicatorOptions options, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            // 虚拟信息模式下改由信息渲染调度器显示，这里不再生成粒子，避免同一个数字显示两遍
            if (ClientConfigs.damageIndicator.isVirtual() && options.type() == DamageIndicatorOptions.Type.DAMAGE || ClientConfigs.healIndicator.isVirtual() && options.type() == DamageIndicatorOptions.Type.HEAL) {
                return null;
            }
            return new DamageIndicatorParticle(pLevel, pX, pY, pZ, options.text(), options.big());
        }
    }
}
