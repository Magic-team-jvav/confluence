package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.item.PotionItems;

import java.util.UUID;

public record WormholeToPlayerPacketC2S(UUID playerId, ByMod byMod) implements IPacketC2S {
    public static final Type<WormholeToPlayerPacketC2S> TYPE = Confluence.createType("wormhole_to_player");
    public static final StreamCodec<FriendlyByteBuf, WormholeToPlayerPacketC2S> STREAM_CODEC = StreamCodec.composite(
            LibStreamCodecUtils.UUID, WormholeToPlayerPacketC2S::playerId,
            ByMod.STREAM_CODEC, WormholeToPlayerPacketC2S::byMod,
            WormholeToPlayerPacketC2S::new
    );

    @Override
    public Type<WormholeToPlayerPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        if (!byMod.enabled()) return;
        ServerPlayer target = player.server.getPlayerList().getPlayer(playerId);
        if (target != null && player.getTeam() == target.getTeam()) {
            ItemStack potion = getWormholePotion(player);
            if (potion.isEmpty()) return;
            if (!player.hasInfiniteMaterials()) potion.shrink(1);
            teleport(player, target);
        }
    }

    public static boolean isTrackable(ServerPlayer trackingPlayer, ServerPlayer trackedPlayer) {
        return trackingPlayer != trackedPlayer && trackingPlayer.getTeam() == trackedPlayer.getTeam();
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
