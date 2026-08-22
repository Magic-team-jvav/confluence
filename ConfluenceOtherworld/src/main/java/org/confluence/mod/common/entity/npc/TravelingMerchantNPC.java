package org.confluence.mod.common.entity.npc;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.npc.trade.NPCTradeOffer;

import java.util.ArrayList;
import java.util.List;

/// 旅商 —— 随机到访、黄昏后离开。
/// 每天黎明有概率生成，黄昏(dayTime 12000)后消失。
/// 商贩背包可使商品数 +1。
public class TravelingMerchantNPC extends BaseNPC {
    private static final String STOCK_INITIALIZED_TAG = "TradeStockInitialized";
    private static final String STOCK_TAG = "TradeStock";
    private static final Codec<List<NPCTradeOffer>> STOCK_CODEC = NPCTradeOffer.CODEC.listOf();
    private long spawnDayTime;
    private boolean departing;
    private boolean tradeStockInitialized;
    private final List<NPCTradeOffer> tradeStock = new ArrayList<>();

    public TravelingMerchantNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
        if (!level.isClientSide) {
            this.spawnDayTime = level.getDayTime();
        }
    }

    public int getTradeCount() {
        int base = level().getRandom().nextInt(4, 10);
        if (NPCSpawner.INSTANCE.isPeddlersSatchelUsed()) base += 1;
        return base;
    }

    /// 首次打开商店时从当前数据包报价中抽取本次到访库存，之后以完整报价固定到实体 NBT。
    @Override
    public List<NPCTradeOffer> selectTradeOffers(List<NPCTradeOffer> offers) {
        if (!tradeStockInitialized && !level().isClientSide) {
            List<NPCTradeOffer> shuffled = new ArrayList<>(offers);
            for (int index = shuffled.size() - 1; index > 0; index--) {
                int swapIndex = random.nextInt(index + 1);
                NPCTradeOffer previous = shuffled.set(index, shuffled.get(swapIndex));
                shuffled.set(swapIndex, previous);
            }
            int count = Math.min(getTradeCount(), shuffled.size());
            tradeStock.clear();
            tradeStock.addAll(shuffled.subList(0, count));
            tradeStockInitialized = true;
        }
        if (!tradeStockInitialized) return List.of();
        return List.copyOf(tradeStock);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        long dayTime = level().getDayTime();
        if (dayTime < spawnDayTime || dayTime % 24000 >= 12000) {
            if (!departing) {
                departing = true;
                NPCSpawner.INSTANCE.onNPCRemoved(this);
            }
            discard();
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.spawnDayTime = tag.getLong("SpawnDayTime");
        tradeStockInitialized = tag.getBoolean(STOCK_INITIALIZED_TAG);
        tradeStock.clear();
        if (tag.contains(STOCK_TAG)) {
            STOCK_CODEC.parse(NbtOps.INSTANCE, tag.get(STOCK_TAG)).result().ifPresent(tradeStock::addAll);
        }
        if (tradeStockInitialized && tradeStock.isEmpty()) tradeStockInitialized = false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("SpawnDayTime", spawnDayTime);
        tag.putBoolean(STOCK_INITIALIZED_TAG, tradeStockInitialized);
        STOCK_CODEC.encodeStart(NbtOps.INSTANCE, tradeStock).result().ifPresent(stock -> tag.put(STOCK_TAG, stock));
    }
}
