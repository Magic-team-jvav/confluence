package org.confluence.terra_curio.client.handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.terra_curio.client.TCKeyBindings;
import org.confluence.terra_curio.network.c2s.StepStoolSteppingPacketC2S;
import org.confluence.terra_curio.network.s2c.StepStoolSteppingPacketS2C;

import static org.confluence.terra_curio.network.c2s.StepStoolSteppingPacketC2S.INCREASE;

public final class StepStoolHandler {
    private static boolean upKeyDown = false;
    private static boolean shiftKeyDown = false;
    private static byte actualStep = 0;
    private static int maxStep = 0;
    private static int slot = StepStoolSteppingPacketS2C.NO_CURIO;

    public static void handle(LocalPlayer localPlayer) {
        if (slot == StepStoolSteppingPacketS2C.NO_CURIO || (actualStep == 0 && !localPlayer.onGround())) {
            actualStep = 0;
            return;
        }

        if (actualStep > 0) {
            if (localPlayer.input.jumping) {
                localPlayer.jumpFromGround();
                setStep((byte) 0, false);
                return;
            } else if (localPlayer.getVehicle() != null) {
                setStep((byte) 0, false);
                return;
            }
        }

        if (TCKeyBindings.STEP_STOOL.get().isDown()) {
            if (!upKeyDown && actualStep < maxStep) {
                setStep((byte) (actualStep + 1), true);
                upKeyDown = true;
            }
        } else {
            upKeyDown = false;
        }

        if (!upKeyDown && localPlayer.isShiftKeyDown()) {
            if (!shiftKeyDown && actualStep > 0) {
                setStep((byte) (actualStep - 1), false);
                shiftKeyDown = true;
            }
        } else {
            shiftKeyDown = false;
        }

        if (actualStep > 0) {
            localPlayer.setDeltaMovement(new Vec3(0.0, localPlayer.getDeltaMovement().y, 0.0));
        }
    }

    public static void reset() {
        actualStep = 0;
        maxStep = 0;
        slot = StepStoolSteppingPacketS2C.NO_CURIO;
    }

    public static void setStep(byte actualStep, boolean increase) {
        StepStoolHandler.actualStep = actualStep;
        byte step = actualStep;
        if (increase) step = (byte) (actualStep | INCREASE);
        PacketDistributor.sendToServer(new StepStoolSteppingPacketC2S(slot, step));
    }

    public static int getActualStep() {
        return actualStep;
    }

    public static boolean onStool() {
        return actualStep > 0;
    }

    public static void handlePacket(int slot, int maxStep) {
        if (slot == StepStoolSteppingPacketS2C.RESET_STEP) {
            StepStoolHandler.actualStep = 0;
            StepStoolHandler.maxStep = 0;
        } else {
            StepStoolHandler.maxStep = maxStep;
            StepStoolHandler.slot = maxStep == 0 ? StepStoolSteppingPacketS2C.NO_CURIO : slot;
        }
    }
}
