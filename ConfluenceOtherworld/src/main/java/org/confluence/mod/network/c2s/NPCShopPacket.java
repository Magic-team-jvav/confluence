package org.confluence.mod.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.npc_trade.MoneyTradeItem;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.registries.npc_trade.ITrade;
import org.jetbrains.annotations.NotNull;


public record NPCShopPacket(ITrade trade) implements CustomPacketPayload {
    public static final Type<NPCShopPacket> TYPE = new Type<>(Confluence.asResource("npc_trade_packet_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NPCShopPacket> STREAM_CODEC = StreamCodec.composite(
            ITrade.STREAM_CODEC,
            NPCShopPacket::trade,
            NPCShopPacket::new
    );

    @Override
    public @NotNull Type<NPCShopPacket> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {

            if(trade.canTrade(context.player()) &&  context.player() instanceof ServerPlayer sp){
                // todo
                if(trade instanceof MoneyTradeItem trade &&  PlayerUtils.tryCostMoney(sp, trade.cost())) {
                    ItemStack result = trade.result();
                    if(context.player().getInventory().getFreeSlot() ==-1){
                        context.player().drop(result.copy(),false);
                    }
                    else context.player().addItem(result.copy());
                }
            }else{
                context.player().sendSystemMessage(Component.translatable("confluence.trade.not_enough_items"));
            }

        }).exceptionally(e -> null);
    }
}
