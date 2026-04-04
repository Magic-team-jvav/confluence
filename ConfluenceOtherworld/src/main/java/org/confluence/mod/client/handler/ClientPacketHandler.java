package org.confluence.mod.client.handler;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.mixed.IDeathScreen;
import org.confluence.mod.mixed.ILevelRenderer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.*;
import org.confluence.mod.util.ClientUtils;
import org.confluence.terra_curio.common.init.TCItems;

public final class ClientPacketHandler {
    private static int maxMana = 20;
    private static float currentMana = 20;
//    private static int maxSoul = 10;
//    private static float currentSoul = 10;
    private static float fishingPower = 0.0F;
    private static boolean echoVisible = false;
    private static long secretFlag = 0L;
    private static boolean showSignal = false;
//    private static boolean fallenSoulCoreActive = false;

    private static int luminance = 0;
    private static final Int2IntMap remoteLuminance = new Int2IntArrayMap();

    public static float getCurrentMana() {
        return currentMana;
    }

    public static int getMaxMana() {
        return maxMana;
    }

//    public static float getCurrentSoul() {
//        return currentSoul;
//    }
//
//    public static int getMaxSoul() {
//        return maxSoul;
//    }
//
//    public static boolean isFallenSoulCoreActive() {return fallenSoulCoreActive;}

    public static float getFishingPower() {
        return fishingPower;
    }

    public static boolean hasEchoVisible() {
        return echoVisible;
    }

    public static long getSecretFlag() {
        return secretFlag;
    }

    public static boolean isShowSignal() {
        return showSignal;
    }

    public static int getLuminance(Player player) {
        return player == Minecraft.getInstance().player ? luminance : remoteLuminance.getOrDefault(player.getId(), 0);
    }

    public static void setLuminance(Player player, PlayerSpecialData data) {
        if (player == Minecraft.getInstance().player) {
            luminance = data.getValue(TCItems.LUMINANCE);
        } else {
            remoteLuminance.put(player.getId(), (int) data.getValue(TCItems.LUMINANCE));
        }
    }

    public static void reset() {
        maxMana = 20;
        currentMana = 20;
//        maxSoul = 10;
//        currentSoul = 10;
        fishingPower = 0.0F;
        echoVisible = false;
        secretFlag = 0L;
        showSignal = false;
        luminance = 0;
        remoteLuminance.clear();
//        fallenSoulCoreActive = false;
    }

    public static void handleMana(ManaPacketS2C packet, Player player) {
        maxMana = packet.maxMana();
        currentMana = packet.currentMana();
        if (currentMana >= maxMana) {
            player.playSound(ModSoundEvents.COOLDOWN_RECOVERY.get());
        }
    }

    public static void handleSoul(SoulPacketS2C packet, Player player) {
//        maxSoul = packet.maxSoul();
//        currentSoul = packet.currentSoul();
//        fallenSoulCoreActive = packet.fallenSoulCoreActive();
//        EverBeneficial beneficial = EverBeneficial.of(player);
//        beneficial.setFallenSoulCore(fallenSoulCoreActive);
//        PlayerSpecialData data = PlayerSpecialData.of(player);
//        data.setFallenSoulCoreActive(beneficial.getFallenSoulCore());
    }

    public static void handleFishingPower(FishingPowerInfoPacketS2C packet) {
        fishingPower = packet.value();
    }

    public static void handleVisibility(byte mask, boolean visible) {
        if ((mask & VisibilityPacketS2C.ECHO) != 0) {
            if (echoVisible != visible) {
                ILevelRenderer.rebuildAllChunks();
                echoVisible = visible;
            }
        }
        if ((mask & VisibilityPacketS2C.THE_CONSTANT_POST_EFFECT) != 0) {
            ClientUtils.postTheConstantEffect(visible);
        }
        if ((mask & VisibilityPacketS2C.SIGNAL) != 0) {
            showSignal = visible;
        }
    }

    public static void handleSecretFlag(SecretFlagSyncPacketS2C packet) {
        secretFlag = packet.flag();
        if ((secretFlag & IWorldOptions.HARDMODE) != 0) {
            ILevelRenderer.rebuildAllChunks();
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

    public static void handleFlushArmorSetBonus(Player localPlayer, int playerId) {
        if (localPlayer.level().getEntity(playerId) instanceof AbstractClientPlayer clientPlayer) {
            if (localPlayer == clientPlayer) {
                PlayerSpecialData.of(localPlayer).flushArmorSetBonus(localPlayer);
            } else {
                PlayerSpecialData.of(clientPlayer).flushArmorSetBonus(clientPlayer);
            }
        }
    }

    // 客户端同步过来的只有隐藏的
    public static void handleCloak() {
        GlobalCloakData.INSTANCE.rollbackAllProperties();
        ILevelRenderer.rebuildAllChunks();
    }

    private static boolean askForSoftcoreLayer = false;

    public static void setAskForSoftcoreLayer(boolean b) {
        askForSoftcoreLayer = b;
    }

    public static boolean isAskForSoftcoreLayer() {
        return askForSoftcoreLayer;
    }
}
