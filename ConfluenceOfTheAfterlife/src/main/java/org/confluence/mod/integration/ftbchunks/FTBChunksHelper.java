package org.confluence.mod.integration.ftbchunks;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.network.c2s.WormholeToPlayerPacketC2S;

public class FTBChunksHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("ftbchunks");

    @OnlyIn(Dist.CLIENT)
    public static void entityMapIcon$onMouseClicked(Object screen, Object button, Entity entity) {
        if (entity instanceof AbstractClientPlayer player && player != Minecraft.getInstance().player) {
            if (IS_LOADED && screen instanceof BaseScreen baseScreen && button instanceof MouseButton mouseButton && mouseButton.isLeft()) {
                PacketDistributor.sendToServer(new WormholeToPlayerPacketC2S(player.getGameProfile().getId()));
                baseScreen.closeGui(false);
            }
        }
    }
}
