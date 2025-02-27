package org.confluence.mod.integration.ftbchunks;

import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.network.c2s.WormholeToPlayerPacketC2S;

import java.util.UUID;

public class FTBChunksHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("ftbchunks");

    @OnlyIn(Dist.CLIENT)
    public static void onMouseClicked(Object screen, Object button, Entity entity) {
        if (entity instanceof AbstractClientPlayer player) {
            onMouseClicked(screen, button, player.getGameProfile().getId());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMouseClicked(Object screen, Object button, UUID playerId) {
        if (!IS_LOADED || Minecraft.getInstance().player == null) return;
        if (!playerId.equals(Minecraft.getInstance().player.getGameProfile().getId())) {
            if (screen instanceof BaseScreen baseScreen && button instanceof MouseButton mouseButton && mouseButton.isLeft()) {
                PacketDistributor.sendToServer(new WormholeToPlayerPacketC2S(playerId));
                baseScreen.closeGui(false);
            }
        }
    }

    public static boolean shouldTrack(ServerPlayer trackingPlayer, ServerPlayer trackedPlayer) {
        if (!IS_LOADED) return false;
        if (FTBChunksWorldConfig.LOCATION_MODE_OVERRIDE.get()) return true;
        return WormholeToPlayerPacketC2S.isTrackable(trackingPlayer, trackedPlayer);
    }
}
