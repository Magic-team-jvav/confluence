package org.confluence.mod.client.renderer.entity.bullet.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;

public final class SilverCrossEffect implements ActiveBulletVfx {
    private static final int LIFETIME = 5;
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/vfx/particles/star_06.png");
    private final Vec3 position;
    private int age;

    public SilverCrossEffect(Vec3 position) {
        this.position = position;
    }

    @Override
    public boolean tick(ClientLevel level) {
        return ++age < LIFETIME;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPosition) {
        int color = BulletVfxRenderUtil.fadeColor(0xFFFFFFFF, 1.0F - age / (float) LIFETIME);
        BulletVfxRenderUtil.sprite(poseStack, bufferSource, cameraPosition, position, TEXTURE, color, 0.36F + age * 0.004F);
    }
}
