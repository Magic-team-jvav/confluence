package org.confluence.terra_curio.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.Tags;

public final class ScopeFovHandler {
    static boolean hasScope = false;
    private static boolean scoping = false;
    private static float fovModifier = 0.1F;
    private static double cameraMoveFactor = 0.125;

    public static void handle(Player player) {
        // confluence mixin here
        if (hasScope && player.isCrouching() && Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.getItemInHand(InteractionHand.MAIN_HAND).is(Tags.Items.RANGED_WEAPON_TOOLS)) {
            if (!scoping) {
                if (fovModifier != 1.0F) {
                    player.playSound(SoundEvents.SPYGLASS_USE);
                }
                scoping = true;
            }
        } else {
            scoping = false;
        }
    }

    public static void reset() {
        scoping = false;
        hasScope = false;
        fovModifier = 0.1F;
        cameraMoveFactor = 0.125F;
    }

    public static void handleScroll(Player player, double scrollDeltaY) {
        float cached = fovModifier;
        fovModifier = Mth.clamp(fovModifier - (float) scrollDeltaY * 0.1F, 0.1F, 1.0F);
        cameraMoveFactor = Mth.lerp((fovModifier - 0.1) / 0.9, 0.125, 1.0);
        if (cached != fovModifier) {
            player.playSound(SoundEvents.SPYGLASS_USE);
        }
    }

    public static boolean isScoping() {
        return scoping;
    }

    public static float getFovModifier() {
        return fovModifier;
    }

    public static double getCameraMoveFactor() {
        return cameraMoveFactor;
    }
}
