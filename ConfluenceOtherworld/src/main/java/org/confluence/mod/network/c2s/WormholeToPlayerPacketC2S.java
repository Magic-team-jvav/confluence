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
import org.confluence.mod.client.handler.WormholeHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.item.PotionItems;

import java.util.UUID;

public record WormholeToPlayerPacketC2S(UUID targetPlayerId, ByMod byMod) implements IPacketC2S {
    public static final Type<WormholeToPlayerPacketC2S> TYPE = Confluence.createType("wormhole_to_player");
    public static final StreamCodec<FriendlyByteBuf, WormholeToPlayerPacketC2S> STREAM_CODEC = StreamCodec.composite(
            LibStreamCodecUtils.UUID, WormholeToPlayerPacketC2S::targetPlayerId,
            ByMod.STREAM_CODEC, WormholeToPlayerPacketC2S::byMod,
            WormholeToPlayerPacketC2S::new
    );

    @Override
    public Type<WormholeToPlayerPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer sourcePlayer) {
        if (!byMod.enabled()) return;
        ServerPlayer target = sourcePlayer.server.getPlayerList().getPlayer(targetPlayerId);
        if (target == null) {
            return;
        }

        if (!WormholeHandler.judgment(sourcePlayer, target)) {
            return;
        }

        ItemStack potion = getWormholePotion(sourcePlayer);
        if (potion.isEmpty()) {
            return;
        }

        if (sourcePlayer.hasInfiniteMaterials()) {
            teleport(sourcePlayer, target);
            return;
        }

        potion.shrink(1);
        teleport(sourcePlayer, target);

        if (!CommonConfigs.RETURN_POTION_GLASS_BOTTLE.get()) {
            return;
        }

        ItemStack itemstack = PotionItems.BOTTLE.toStack();
        if (sourcePlayer.getInventory().add(itemstack)) {
            return;
        }

        sourcePlayer.drop(itemstack, false);
    }

    public static boolean isTrackable(ServerPlayer trackingPlayer, ServerPlayer trackedPlayer) {
        return trackingPlayer != trackedPlayer && PlayerSpecialData.of(trackingPlayer).getTeam() == PlayerSpecialData.of(trackedPlayer).getTeam();
    }

    private static ItemStack getWormholePotion(ServerPlayer serverPlayer) {
        Inventory inventory = serverPlayer.getInventory();
        ItemStack stack = inventory.offhand.getFirst();

        if (stack.is(PotionItems.WORMHOLE_POTION)) {
            return stack;
        }

        for (ItemStack itemStack : inventory.items) {
            if (itemStack.isEmpty()) {
                continue;
            }
            if (itemStack.is(PotionItems.WORMHOLE_POTION)) {
                return itemStack;
            }
        }

        return ItemStack.EMPTY;
    }

    private void teleport(ServerPlayer serverPlayer, ServerPlayer target) {
        serverPlayer.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(), serverPlayer.getViewYRot(1), serverPlayer.getViewXRot(1));
    }

    public enum ByMod {
        DEFAULT {
            @Override
            public boolean enabled() {
                return true;
            }
        },
        FTB_CHUNKS {
            @Override
            public boolean enabled() {
                return CommonConfigs.FTB_CHUNKS_WORMHOLE_POTION.get();
            }
        };

        public static final ByMod[] VALUES = values();
        public static final StreamCodec<ByteBuf, ByMod> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(i -> VALUES[i], Enum::ordinal);

        public abstract boolean enabled();
    }
}
