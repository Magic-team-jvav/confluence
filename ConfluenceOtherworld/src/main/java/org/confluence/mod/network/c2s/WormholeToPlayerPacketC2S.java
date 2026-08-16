package org.confluence.mod.network.c2s;

import PortLib.extensions.java.util.List.PortListExtension;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;
import org.confluence.mod.common.init.item.PotionItems;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.UUID;

/// 请求消耗虫洞药水并传送到同队玩家。
///
/// <p>客户端只选择目标和入口来源；目标资格、药水消耗、跨维度位置以及玻璃瓶返还
/// 全部由服务端重新确认。白队表示尚未选择队伍，不能利用默认值互相传送。</p>
public record WormholeToPlayerPacketC2S(
        UUID targetPlayerId,
        ByMod byMod
) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("wormhole_to_player");
    public static final PortStreamCodec<FriendlyByteBuf, WormholeToPlayerPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            LibStreamCodecUtils.UUID,
            WormholeToPlayerPacketC2S::targetPlayerId,
            ByMod.STREAM_CODEC, WormholeToPlayerPacketC2S::byMod,
            WormholeToPlayerPacketC2S::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 虫洞请求会消耗物品并跨维度传送玩家，必须回到服务端主线程执行。
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        if (!byMod.enabled()) return;
        ServerPlayer target =
                player.server.getPlayerList().getPlayer(targetPlayerId);
        if (!isTrackable(player, target)) return;

        ItemStack potion = getWormholePotion(player);
        if (potion.isEmpty()) return;
        if (player.hasInfiniteMaterials()) {
            teleport(player, target);
            return;
        }

        potion.shrink(1);
        teleport(player, target);
        if (CommonConfigs.RETURN_POTION_GLASS_BOTTLE.get()) {
            ItemStack bottle = PotionItems.BOTTLE.toStack();
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
    }

    public static boolean isTrackable(ServerPlayer trackingPlayer, ServerPlayer trackedPlayer) {
        if (trackingPlayer == null
                || trackedPlayer == null
                || trackingPlayer == trackedPlayer
                || !trackingPlayer.isAlive()
                || !trackedPlayer.isAlive()
                || trackingPlayer.isSpectator()
                || trackedPlayer.isSpectator()) {
            return false;
        }
        Team trackingTeam = PlayerSpecialData.of(trackingPlayer).getTeam();
        Team trackedTeam = PlayerSpecialData.of(trackedPlayer).getTeam();
        return trackingTeam != Team.WHITE && trackingTeam == trackedTeam;
    }

    private static ItemStack getWormholePotion(ServerPlayer serverPlayer) {
        Inventory inventory = serverPlayer.getInventory();
        ItemStack stack = PortListExtension.getFirst(inventory.offhand);
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
        serverPlayer.teleportTo(
                target.serverLevel(),
                target.getX(),
                target.getY(),
                target.getZ(),
                serverPlayer.getYRot(),
                serverPlayer.getXRot());
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
        public static final PortStreamCodec<FriendlyByteBuf, ByMod> STREAM_CODEC =
                LibStreamCodecUtils.fromEnum(VALUES);

        public abstract boolean enabled();
    }
}
