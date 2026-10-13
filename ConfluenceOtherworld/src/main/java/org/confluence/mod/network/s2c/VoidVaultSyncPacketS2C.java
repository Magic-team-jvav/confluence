package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerVoidVaultContainer;

import java.util.List;

public record VoidVaultSyncPacketS2C(List<ItemStack> stacks) implements IPacketS2C {
    public static final Type<VoidVaultSyncPacketS2C> TYPE = Confluence.createType("void_vault_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, VoidVaultSyncPacketS2C> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()).map(VoidVaultSyncPacketS2C::new, VoidVaultSyncPacketS2C::stacks);

    @Override
    public Type<VoidVaultSyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        if (stacks.size() != PlayerVoidVaultContainer.SIZE) return;
        PlayerVoidVaultContainer container = PlayerVoidVaultContainer.of(player);
        for (int i = 0; i < stacks.size(); i++) container.setItem(i, stacks.get(i));
    }

    public static void send(ServerPlayer player, PlayerVoidVaultContainer container) {
        PacketDistributor.sendToPlayer(player, new VoidVaultSyncPacketS2C(container.getItems().stream().map(ItemStack::copy).toList()));
    }
}
