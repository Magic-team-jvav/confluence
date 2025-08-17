package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCures;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terraentity.api.npc.trade.ITradeHealth;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 护士回血直接回满
 */
public record MoneyTradeHealthFull(@Nullable TradeProperties properties) implements IMoneyTrade, ITradeHealth {
    public static final MapCodec<MoneyTradeHealthFull> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TradeProperties.CODEC.optionalFieldOf("properties").forGetter(i -> Optional.ofNullable(i.properties))
    ).apply(instance, (properties) -> new MoneyTradeHealthFull(properties.orElse(null))));

    public static MoneyTradeHealthFull create() {
        return new MoneyTradeHealthFull(null);
    }

    public static MoneyTradeHealthFull create(TradeProperties properties) {
        return new MoneyTradeHealthFull(properties);
    }

    public long getCost(@Nullable Player player, ITradeHolder holder) {
        if (player == null) return 0;

        // 每一滴血5铜
        long cost = (long) (player.getMaxHealth() - player.getHealth() + 1) * 5;
        // 每个减益5银
        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (instance.getCures().contains(EffectCures.HONEY)) {
                cost += 500;
            }
        }
        // 专家模式再×2
        if (LibUtils.isAtLeastExpert(player.level(), player.blockPosition())) {
            cost *= 2;
        }
        // todo 随着游戏推进，护士会收取更多治疗费用
        KillBoard instance = KillBoard.INSTANCE;
        if (instance.getGamePhase().isHardmode()) {
            cost *= 60;
        } else if (instance.isAnyDefeated(TEBossEntities.SKELETRON.get(), TEBossEntities.QUEEN_BEE.get())) {
            cost *= 25;
        } else if (instance.isAnyDefeated(TEBossEntities.EATER_OF_WORLDS.get(), TEBossEntities.BRAIN_OF_CTHULHU.get())) {
            cost *= 10;
        } else if (instance.isDefeated(TEBossEntities.EYE_OF_CTHULHU.get())) {
            cost *= 3;
        }
        return cost;
    }

    public int getHealth(@Nullable Player player) {
        if (player == null) {
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
        player.removeEffectsCuredBy(EffectCures.HONEY);
        AchievementUtils.theFrequentFlyer(player, cost);
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MONEY_TRADE_HEALTH_FULL.get();
    }
}
