package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.mod.network.s2c.GamePhasePacketS2C;
import org.confluence.mod.network.s2c.ManaPacketS2C;
import org.confluence.phase_journey.mixed.ILevelRenderer;

@OnlyIn(Dist.CLIENT)
public final class ClientPacketHandler {
    private static int maxMana = 20;
    private static int currentMana = 20;
    private static GamePhase gamePhase = GamePhase.BEFORE_SKELETRON;
    private static float fishingPower = 0.0F;
    private static boolean echoVisible = false;

    public static int getCurrentMana() {
        return currentMana;
    }

    public static int getMaxMana() {
        return maxMana;
    }

    public static GamePhase getGamePhase() {
        return gamePhase;
    }

    public static boolean isHardcore() {
        return gamePhase.ordinal() > 1;
    }

    public boolean isGraduated() {
        return gamePhase.ordinal() == 6;
    }

    public static float getFishingPower() {
        return fishingPower;
    }

    public static boolean hasEchoVisible() {
        return echoVisible;
    }

    public static void handleMana(ManaPacketS2C packet, Player player) {
        maxMana = packet.maxMana();
        currentMana = packet.currentMana();
        if (currentMana == maxMana) {
            player.playSound(ModSoundEvents.COOLDOWN_RECOVERY.get());
        }
    }

    public static void handleGamePhase(GamePhasePacketS2C packet) {
        gamePhase = packet.gamePhase();
    }

    public static void handleFishingPower(FishingPowerInfoPacketS2C packet) {
        fishingPower = packet.value();
    }

    public static void handleEcho(boolean visible) {
        if (echoVisible != visible) {
            ((ILevelRenderer) Minecraft.getInstance().levelRenderer).phase_journey$rebuildAllChunks();
            echoVisible = visible;
        }
    }
}
