package org.confluence.mod.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.PotionItems;

import java.util.UUID;

public record WormholeToPlayerPacketC2S(UUID playerId) implements CustomPacketPayload {
    public static final Type<WormholeToPlayerPacketC2S> TYPE = new Type<>(Confluence.asResource("wormhole_to_player"));
    public static final StreamCodec<FriendlyByteBuf, WormholeToPlayerPacketC2S> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public WormholeToPlayerPacketC2S decode(FriendlyByteBuf buffer) {
            return new WormholeToPlayerPacketC2S(buffer.readUUID());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, WormholeToPlayerPacketC2S value) {
            buffer.writeUUID(value.playerId);
        }
    };

    @Override
    public Type<WormholeToPlayerPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                ServerPlayer target = serverPlayer.server.getPlayerList().getPlayer(playerId);
                if (target != null && serverPlayer.getTeam() == target.getTeam()) {
                    ItemStack potion = getWormholePotion(serverPlayer);
                    if (!potion.isEmpty()) {
                        potion.shrink(1);
                        teleport(serverPlayer, target);
                    }
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static boolean isTrackable(ServerPlayer trackingPlayer, ServerPlayer trackedPlayer) {
        if (trackingPlayer == trackedPlayer || trackingPlayer.getTeam() != trackedPlayer.getTeam()) return false;
        return !getWormholePotion(trackingPlayer).isEmpty();
    }

    private static ItemStack getWormholePotion(ServerPlayer serverPlayer) {
        Inventory inventory = serverPlayer.getInventory();
        ItemStack stack = inventory.offhand.getFirst();
        if (!stack.isEmpty() && stack.is(PotionItems.WORMHOLE_POTION)) {
            return stack;
        } else {
            for (ItemStack itemStack : inventory.items) {
                if (!itemStack.isEmpty() && itemStack.is(PotionItems.WORMHOLE_POTION)) {
                    return itemStack;
                }
            }
            return ItemStack.EMPTY;
        }
    }

    private void teleport(ServerPlayer serverPlayer, ServerPlayer target) {
        serverPlayer.teleportTo(serverPlayer.serverLevel(), target.getX(), target.getY(), target.getZ(), serverPlayer.getXRot(), serverPlayer.getYRot());
    }
}
