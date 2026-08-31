package org.confluence.mod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public final class VoidSeaFilterRenderer {
    private static final ResourceLocation UNDERWATER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");

    private VoidSeaFilterRenderer() {}

    public static void render(Minecraft minecraft, PoseStack poseStack) {
//        LocalPlayer player = minecraft.player;
//        ClientLevel level = minecraft.level;
//        if (player == null
//                || level == null
//                || player.isSpectator()
//                || !VoidSeaHelper.isDimensionalOverlapEffect(player)) {
//            return;
//        }
//
//        GameRenderer gameRenderer = minecraft.gameRenderer;
//        if (gameRenderer.getMainCamera().getPosition().y >= VoidSeaHelper.getHeight(level, minecraft.getTimer().getGameTimeDeltaTicks())) {
//            return;
//        }
//
////        if (LibUtils.isModLoaded("iris") && VoidSeaIrisCompat.isRenderingShadowPass()) {
////            return;
////        }
//
//        ScreenEffectRenderer.renderFluid(minecraft, poseStack, UNDERWATER_TEXTURE);
    }
}
