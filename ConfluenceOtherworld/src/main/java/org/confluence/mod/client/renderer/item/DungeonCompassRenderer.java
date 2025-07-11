package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.phys.Vec3;

public class DungeonCompassRenderer {
    private static DungeonCompassRenderer INSTANCE;
    private final SkullModel model;

    private DungeonCompassRenderer() {
        this.model = new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SKELETON_SKULL));
    }

    public static DungeonCompassRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DungeonCompassRenderer();
        }
        return INSTANCE;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, AbstractClientPlayer player, int x, int y, int z) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        RenderSystem.enableDepthTest();
        poseStack.pushPose();
        poseStack.scale(-1, -1, 1);
        float ry = Mth.PI - player.getYRot() * Mth.DEG_TO_RAD;
        float cos = Mth.cos(ry);
        float sin = Mth.sin(ry);
        double mx = -0.75;
        double mz = -1;
        double vx = mx * cos + mz * -sin;
        double vz = mx * sin + mz * cos;
        poseStack.translate(vx, 0.25, vz);
        Vec3 delta = new Vec3(x - player.getX(), 0, z - player.getZ());
        float yaw = (float) Mth.atan2(-delta.x, delta.z) * Mth.RAD_TO_DEG;
        model.setupAnim(0, 180 + yaw, 0);
        model.renderToBuffer(poseStack, bufferSource.getBuffer(SkullBlockRenderer.getRenderType(SkullBlock.Types.SKELETON, null)), 0xF000F0, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        RenderSystem.disableDepthTest();
    }
}
