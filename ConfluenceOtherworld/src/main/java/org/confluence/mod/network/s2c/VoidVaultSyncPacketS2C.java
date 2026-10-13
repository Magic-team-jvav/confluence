package org.confluence.mod.network.s2c;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerVoidVaultContainer;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.List;

public record VoidVaultSyncPacketS2C(List<ItemStack> stacks) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("void_vault_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, VoidVaultSyncPacketS2C> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.apply(PortByteBufCodecs.list()).map(VoidVaultSyncPacketS2C::new, VoidVaultSyncPacketS2C::stacks);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        if (stacks.size() != PlayerVoidVaultContainer.SIZE) return;
        PlayerVoidVaultContainer container = PlayerVoidVaultContainer.of(player);
        for (int i = 0; i < stacks.size(); i++) container.setItem(i, stacks.get(i));
    }

    public static void send(ServerPlayer player, PlayerVoidVaultContainer container) {
        PortPacketDistributor.sendToPlayer(player, new VoidVaultSyncPacketS2C(container.getItems().stream().map(ItemStack::copy).toList()));
    }
}
