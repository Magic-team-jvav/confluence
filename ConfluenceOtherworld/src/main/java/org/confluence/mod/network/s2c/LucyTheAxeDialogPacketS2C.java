package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.LucyTheAxeHandler;
import org.confluence.mod.common.data.LucyTheAxeDialogCategory;
import org.confluence.mod.common.init.item.AxeItems;

public record LucyTheAxeDialogPacketS2C(
        ResourceLocation categoryKey,
        LucyTheAxeDialogCategory category,
        int senderId
) implements IPacketS2C {
    public static final Type<LucyTheAxeDialogPacketS2C> TYPE = Confluence.createType("lucy_the_axe_dialog");
    public static final StreamCodec<RegistryFriendlyByteBuf, LucyTheAxeDialogPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, LucyTheAxeDialogPacketS2C::categoryKey,
            LucyTheAxeDialogCategory.STREAM_CODEC, LucyTheAxeDialogPacketS2C::category,
            ByteBufCodecs.VAR_INT, LucyTheAxeDialogPacketS2C::senderId,
            LucyTheAxeDialogPacketS2C::new
    );

    @Override
    public void work(Player player) {
        LucyTheAxeHandler.handlePacket(this, player);
    }

    @Override
    public Type<LucyTheAxeDialogPacketS2C> type() {
        return TYPE;
    }

    public static boolean checkAndBroadcast(ServerPlayer source, ItemStack stack, ResourceLocation category) {
        if (stack.is(AxeItems.LUCY_THE_AXE)) {
            broadcast(source, category);
            return true;
        }
        return false;
    }

    public static boolean checkAndBroadcast(ServerPlayer source, ResourceLocation category) {
        return checkAndBroadcast(source, source.getInventory().getSelected(), category);
    }

    public static void broadcast(ServerPlayer source, ResourceLocation category) {
        LucyTheAxeDialogCategory dialogCategory = LucyTheAxeDialogCategory.Loader.getInstance().getCategories().get(category);
        if (dialogCategory == null) return;
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(source, new LucyTheAxeDialogPacketS2C(category, dialogCategory, source.getId()));
    }
}
