package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.mixed.IDeathScreen;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.*;
import org.confluence.phase_journey.mixed.ILevelRenderer;

@OnlyIn(Dist.CLIENT)
public final class ClientPacketHandler {
    private static int maxMana = 20;
    private static float currentMana = 20;
    private static float fishingPower = 0.0F;
    private static boolean echoVisible = false;
    private static long secretFlag = 0L;
    private static boolean sprintable = false;
    private static boolean showSignal = false;

    public static float getCurrentMana() {
        return currentMana;
    }

    public static int getMaxMana() {
        return maxMana;
    }

    public static float getFishingPower() {
        return fishingPower;
    }

    public static boolean hasEchoVisible() {
        return echoVisible;
    }

    public static long getSecretFlag() {
        return secretFlag;
    }

    public static boolean isSprintable() {
        return sprintable;
    }

    public static boolean isShowSignal() {
        return showSignal;
    }

    public static void reset() {
        maxMana = 20;
        currentMana = 20;
        fishingPower = 0.0F;
        echoVisible = false;
        secretFlag = 0L;
        sprintable = false;
        showSignal = false;
    }

    public static void handleMana(ManaPacketS2C packet, Player player) {
        maxMana = packet.maxMana();
        currentMana = packet.currentMana();
        if (currentMana >= maxMana) {
            player.playSound(ModSoundEvents.COOLDOWN_RECOVERY.get());
        }
    }

    public static void handleFishingPower(FishingPowerInfoPacketS2C packet) {
        fishingPower = packet.value();
    }

    public static void handleVisibility(byte mask, boolean visible) {
        if ((mask & VisibilityPacketS2C.ECHO) != 0) {
            if (echoVisible != visible) {
                ((ILevelRenderer) Minecraft.getInstance().levelRenderer).phase_journey$rebuildAllChunks();
                echoVisible = visible;
            }
        }
        if ((mask & VisibilityPacketS2C.THE_CONSTANT_POST_EFFECT) != 0) {
            TheConstant.postEffect(visible);
        }
        if ((mask & VisibilityPacketS2C.SIGNAL) != 0) {
            showSignal = visible;
        }
    }

    public static void handleSecretFlag(SecretFlagSyncPacketS2C packet) {
        secretFlag = packet.flag();
        if ((secretFlag & IWorldOptions.HARDMODE) != 0) {
            ((ILevelRenderer) Minecraft.getInstance().levelRenderer).phase_journey$rebuildAllChunks();
        }
    }

    public static void handleDeathInfo(PlayerDeathInfoPacketS2C packet, Player player) {
        ((LocalPlayer) player).connection.handlePlayerCombatKill(new ClientboundPlayerCombatKillPacket(player.getId(), packet.deathMessage()));
        if (!player.isCreative() && Minecraft.getInstance().screen instanceof IDeathScreen deathScreen) {
            deathScreen.confluence$setDelayTicker(0);
            deathScreen.confluence$setRespawnWaitTime(packet.respawnTime() * 20);
            if (packet.platinum() != 0 || packet.gold() != 0 || packet.silver() != 0 || packet.copper() != 0) {
                MutableComponent component = Component.translatable("info.confluence.drops_money");
                if (packet.platinum() > 0) {
                    component.append(Component.translatable("info.confluence.drops_money.platinum", packet.platinum()));
                }
                if (packet.gold() > 0) {
                    component.append(Component.translatable("info.confluence.drops_money.gold", packet.gold()));
                }
                if (packet.silver() > 0) {
                    component.append(Component.translatable("info.confluence.drops_money.silver", packet.silver()));
                }
                if (packet.copper() > 0) {
                    component.append(Component.translatable("info.confluence.drops_money.copper", packet.copper()));
                }
                deathScreen.confluence$setDropsMoney(component);
            }
        }
    }

    public static void handleSprintable(boolean able) {
        sprintable = able;
    }
}
