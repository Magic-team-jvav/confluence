package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.entity.npc.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade.ITradeHealth;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.jetbrains.annotations.Nullable;

/**
 * 护士回血直接回满
 */
public class MoneyTradeHealthFull implements IMoneyTrade, ITradeHealth {

    public static final MapCodec<MoneyTradeHealthFull> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.EMPTY.fieldOf("void").forGetter(trade -> Unit.INSTANCE)
    ).apply(instance, (type)-> new MoneyTradeHealthFull()));

    public static MoneyTradeHealthFull create() {
        return new MoneyTradeHealthFull();
    }

    public long getCost(@Nullable Player player){
        if(player == null){
            return cost();
        }
        // 每一滴血98铜币
        return (long) (player.getMaxHealth() - player.getHealth() + 1) * 98;
    }

    public int getHealth(@Nullable Player player){
        if(player == null){
            return health();
        }
        return (int) (player.getMaxHealth() - player.getHealth() + 1);
    }


    @Override
    public long cost() {
        return 0;
    }

    @Override
    public int health() {
        return 0;
    }

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return IMoneyTrade.super.canTrade(player, npc, index) && ITradeHealth.super.canTrade(player, npc, index);
    }

    @Override
    public void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        IMoneyTrade.super.onTrade(player, npc, 0);
    }

    @Override
    public void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {

    }


    @Override
    public void onTradeSuccess(ServerPlayer player, ITradeHolder npc, int index) {
        player.setHealth(player.getMaxHealth());
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MONEY_TRADE_HEALTH_FULL.get();
    }
}
