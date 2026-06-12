package org.confluence.mod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.AskForSoftcoreLayer;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record AskForSoftcorePacket(boolean accept) implements IPortPacket {
    public static final ResourceLocation ID = Confluence.asResource("ask_for_softcore");
    public static final PortStreamCodec<ByteBuf, AskForSoftcorePacket> STREAM_CODEC = PortByteBufCodecs.BOOL
            .map(AskForSoftcorePacket::new, AskForSoftcorePacket::accept);

    @Override
    public void handle(Context context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            c2s(serverPlayer);
        } else if (context.player() != null) {
            s2c(context.player());
        }
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public void s2c(Player player) {
        AskForSoftcoreLayer.setAskForSoftcoreLayer(true);
    }

    public void c2s(ServerPlayer player) {
        ServerLevel overworld = player.server.overworld();
        if (accept) {
            overworld.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, player.server);
            player.sendSystemMessage(Component.translatable("confluence.difficulty_notice.sure.done"));
        } else {
            ConfluenceData.get(overworld).setStopAskForSoftcore(true);
            player.sendSystemMessage(Component.translatable("confluence.difficulty_notice.never.done"));
        }
    }

}
