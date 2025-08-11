package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terraentity.api.npc.trade.ITradeHealth;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 护士回血直接回满
 */
public record MoneyTradeHealthFull(@Nullable TradeProperties properties) implements IMoneyTrade, ITradeHealth {

    public static final MapCodec<MoneyTradeHealthFull> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TradeProperties.CODEC.optionalFieldOf("properties").forGetter(i-> Optional.ofNullable(i.properties))
    ).apply(instance, (properties)-> new MoneyTradeHealthFull(properties.orElse(null))));

    public static MoneyTradeHealthFull create() {
        return new MoneyTradeHealthFull(null);
    }

    public static MoneyTradeHealthFull create(TradeProperties properties) {
        return new MoneyTradeHealthFull(properties);
    }

    public long getCost(@Nullable Player player, ITradeHolder holder){
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
    public void onTradeSuccess(ServerPlayer player, ITradeHolder npc, int index, long cost) {
        player.setHealth(player.getMaxHealth());
        AchievementUtils.theFrequentFlyer(player, cost);
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MONEY_TRADE_HEALTH_FULL.get();
    }
}
