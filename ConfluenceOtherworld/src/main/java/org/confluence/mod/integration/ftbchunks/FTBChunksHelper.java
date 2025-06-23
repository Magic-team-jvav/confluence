package org.confluence.mod.integration.ftbchunks;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.network.c2s.WormholeToPlayerPacketC2S;

import java.util.UUID;

public class FTBChunksHelper {
    @OnlyIn(Dist.CLIENT)
    public static void onMouseClicked(Object screen, Object button, UUID playerId) {
        if (Minecraft.getInstance().player == null) return;
        if (!playerId.equals(Minecraft.getInstance().player.getGameProfile().getId())) {
            if (screen instanceof BaseScreen baseScreen && button instanceof MouseButton mouseButton && mouseButton.isLeft()) {
                PacketDistributor.sendToServer(new WormholeToPlayerPacketC2S(playerId));
                baseScreen.closeGui(false);
            }
        }
    }
}
