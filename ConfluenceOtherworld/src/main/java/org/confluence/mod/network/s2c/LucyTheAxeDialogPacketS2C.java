package org.confluence.mod.network.s2c;

import PortLib.extensions.net.minecraft.resources.ResourceLocation.PortResourceLocationExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.LucyTheAxeHandler;
import org.confluence.mod.common.data.LucyTheAxeDialogCategory;
import org.confluence.mod.common.init.item.AxeItems;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record LucyTheAxeDialogPacketS2C(
        ResourceLocation categoryKey,
        LucyTheAxeDialogCategory category,
        int senderId
) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("lucy_the_axe_dialog");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, LucyTheAxeDialogPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortResourceLocationExtension.streamCodec(), LucyTheAxeDialogPacketS2C::categoryKey,
            LucyTheAxeDialogCategory.STREAM_CODEC, LucyTheAxeDialogPacketS2C::category,
            PortByteBufCodecs.VAR_INT, LucyTheAxeDialogPacketS2C::senderId,
            LucyTheAxeDialogPacketS2C::new
    );

    @Override
    public void work(Player player) {
        LucyTheAxeHandler.handlePacket(this, player);
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public static boolean checkAndBroadcast(ServerPlayer source, ItemStack stack, ResourceLocation category) {
        if (stack.is(AxeItems.LUCY_THE_AXE.get())) {
            broadcast(source, category);
            return true;
        }
        return false;
    }

    public static boolean checkAndBroadcast(ServerPlayer source, ResourceLocation category) {
        return checkAndBroadcast(source, source.getMainHandItem(), category);
    }

    public static void broadcast(ServerPlayer source, ResourceLocation category) {
        LucyTheAxeDialogCategory dialogCategory = LucyTheAxeDialogCategory.Loader.getInstance().getCategories().get(category);
        if (dialogCategory == null) return;
        Confluence.NETWORK_HANDLER.sendToPlayersTrackingEntityAndSelf(source, new LucyTheAxeDialogPacketS2C(category, dialogCategory, source.getId()));
    }
}
