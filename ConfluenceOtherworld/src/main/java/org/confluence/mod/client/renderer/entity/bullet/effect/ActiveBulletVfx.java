package org.confluence.mod.client.renderer.entity.bullet.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public interface ActiveBulletVfx {
    boolean tick(ClientLevel level);

    void render(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPosition);
}
