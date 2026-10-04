package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ScryingOrbHandler {
    public static Player spectatingPlayer;

    public static void handle(Minecraft minecraft, LocalPlayer player) {
        if (spectatingPlayer != null && !spectatingPlayer.isAlive()) {
            changeTarget(minecraft.level, player);
        }
        if (player.isShiftKeyDown()) {
            stopSpectating();
        }
    }

    public static void changeTarget(@Nullable Level level, @Nullable Player player) {
        if (level == null) return;
        if (!level.isClientSide || !(level instanceof ClientLevel clientLevel)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.isSingleplayer()) {
            mc.getChatListener().handleSystemMessage(Component.translatable("message.confluence.scrying_orb.singleplayer"), false);
            return;
        }
        List<? extends Player> players = clientLevel.players().stream().filter(p -> p != player && p.isAlive()).toList();
        if (players.isEmpty()) {
            stopSpectating();
            mc.getChatListener().handleSystemMessage(Component.translatable("message.confluence.scrying_orb.alone"), false);
            return;
        }
        int index = players.indexOf(spectatingPlayer);
        if (index == -1 || index == players.size() - 1) {
            index = 0;
        } else {
            index++;
        }
        spectatingPlayer = players.get(index);
        mc.setCameraEntity(spectatingPlayer);
    }

    public static void stopSpectating() {
        Minecraft mc = Minecraft.getInstance();
        spectatingPlayer = null;
        mc.setCameraEntity(mc.player);
    }
}
