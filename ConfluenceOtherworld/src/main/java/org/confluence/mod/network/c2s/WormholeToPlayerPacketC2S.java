package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.network.ExtraByteBufCodecs;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.item.PotionItems;

import java.util.UUID;

public record WormholeToPlayerPacketC2S(UUID playerId, ByMod byMod) implements CustomPacketPayload {
    public static final Type<WormholeToPlayerPacketC2S> TYPE = new Type<>(Confluence.asResource("wormhole_to_player"));
    public static final StreamCodec<FriendlyByteBuf, WormholeToPlayerPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.UUID, WormholeToPlayerPacketC2S::playerId,
            ByMod.STREAM_CODEC, WormholeToPlayerPacketC2S::byMod,
            WormholeToPlayerPacketC2S::new
    );

    @Override
    public Type<WormholeToPlayerPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer && byMod.enabled()) {
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

    public enum ByMod {
        FTB_CHUNKS {
            @Override
            public boolean enabled() {
                return CommonConfigs.FTB_CHUNKS_WORMHOLE_POTION.get();
            }
        },
        XAEROS_MAP {
            @Override
            public boolean enabled() {
                return CommonConfigs.XAEROS_MAP_WORMHOLE_POTION.get();
            }
        };

        public static final ByMod[] VALUES = values();
        public static final StreamCodec<ByteBuf, ByMod> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(i -> VALUES[i], Enum::ordinal);

        public abstract boolean enabled();
    }
}
