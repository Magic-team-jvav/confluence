package org.confluence.mod.common.entity.npc.trade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.MoneyText;
import org.confluence.mod.util.PlayerMoneyTransaction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/// 一次 NPC 商店打开过程对应的独立原版商人会话。
///
/// <p>报价定义来自数据包，但 {@link MerchantOffer} 必须按玩家分别创建。这样使用次数、售罄状态和后续可能加入的
/// 补货信息不会在两名同时交易的玩家之间串联。原版报价的输入物品仅用于履行商人契约；泰拉钱币允许使用跨币种的
/// {@code long} 价格，因此实际扣款始终交给服务端钱币事务处理。</p>
public final class NPCTradeSession implements Merchant {
    private final ServerPlayer player;
    private final BaseNPC npc;
    private final List<NPCTradeOffer> definitions;
    private final List<Long> frozenPrices;
    private final MerchantOffers offers = new MerchantOffers();
    private final Map<MerchantOffer, Integer> offerIndices = new IdentityHashMap<>();
    private final @Nullable NPCTradeMenu expectedMenu;
    private boolean valid = true;
    private int villagerXp;

    public NPCTradeSession(ServerPlayer player, BaseNPC npc, List<NPCTradeOffer> definitions) {
        this(player, npc, definitions, null);
    }

    NPCTradeSession(
            ServerPlayer player,
            BaseNPC npc,
            List<NPCTradeOffer> definitions,
            @Nullable NPCTradeMenu expectedMenu) {
        this.player = player;
        this.npc = npc;
        this.definitions = List.copyOf(definitions);
        this.expectedMenu = expectedMenu;
        this.frozenPrices = new ArrayList<>(definitions.size());

        for (int index = 0; index < definitions.size(); index++) {
            NPCTradeOffer definition = definitions.get(index);
            frozenPrices.add(definition.priceFor(npc));

            MerchantOffer offer = new MerchantOffer(
                    new ItemStack(ModItems.COPPER_COIN.get()),
                    definition.stack().copy(),
                    definition.maxUses(),
                    0,
                    0.0F);
            offers.add(offer);
            offerIndices.put(offer, index);
        }
    }

    /// 按会话快照中的索引执行购买。
    ///
    /// <p>价格、商品和条件结果均在菜单打开时冻结；成交时只重新校验菜单身份、
    /// 玩家与 NPC 的生命周期、维度、距离、库存和资金。这样数据包重载或世界阶段
    /// 在菜单打开后发生变化时，不会悄悄改变玩家眼前的报价。</p>
    public boolean purchase(int index) {
        if (!valid || index < 0 || index >= offers.size() || !isTradingContextValid()) {
            return false;
        }

        NPCTradeOffer definition = definitions.get(index);
        MerchantOffer offer = offers.get(index);
        if (offer.isOutOfStock()) {
            return false;
        }
        if (!PlayerMoneyTransaction.purchase(
                player,
                frozenPrices.get(index),
                true,
                offer.getResult().copy())) {
            return false;
        }

        notifyTrade(offer);
        return true;
    }

    public int size() {
        return offers.size();
    }

    public ItemStack getDisplayResult(int index) {
        ItemStack display = offers.get(index).getResult().copy();
        ListTag lore = display.getOrCreateTagElement("display").getList("Lore", 8);
        Component price = Component.translatable("tooltip.price.buy")
                .withStyle(ChatFormatting.GRAY)
                .append(MoneyText.format(frozenPrices.get(index)));
        lore.add(StringTag.valueOf(Component.Serializer.toJson(price)));
        display.getOrCreateTagElement("display").put("Lore", lore);
        return display;
    }

    public long getFrozenPrice(int index) {
        return frozenPrices.get(index);
    }

    public NPCTradeOffer getDefinition(int index) {
        return definitions.get(index);
    }

    public void invalidate() {
        valid = false;
    }

    private boolean isTradingContextValid() {
        return player.isAlive()
                && npc.isAlive()
                && player.level() == npc.level()
                && player.distanceToSqr(npc) <= 64.0D
                && (expectedMenu == null
                || player.containerMenu == expectedMenu);
    }

    @Override
    public void setTradingPlayer(@Nullable Player tradingPlayer) {
        if (tradingPlayer != null && tradingPlayer != player) {
            throw new IllegalArgumentException("NPC trade session cannot be transferred to another player");
        }
        if (tradingPlayer == null) {
            invalidate();
        }
    }

    @Override
    public @Nullable Player getTradingPlayer() {
        return valid ? player : null;
    }

    @Override
    public MerchantOffers getOffers() {
        return offers;
    }

    @Override
    public void overrideOffers(MerchantOffers replacement) {
        throw new UnsupportedOperationException("NPC trade session offers are immutable");
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        if (!valid || !offerIndices.containsKey(offer)) {
            throw new IllegalArgumentException("Merchant offer does not belong to this NPC trade session");
        }
        offer.increaseUses();
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        // 箱子式商店没有原版商人输入槽，因此无需根据输入物品重新选择报价。
    }

    @Override
    public int getVillagerXp() {
        return villagerXp;
    }

    @Override
    public void overrideXp(int xp) {
        villagerXp = Math.max(0, xp);
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public boolean isClientSide() {
        return npc.level().isClientSide;
    }
}
