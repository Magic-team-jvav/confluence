package org.confluence.terraentity.entity.npc.trade;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeGenerator;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade_list.variant.SimpleGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 交易清单
 */
public class NPCTradeManager {
    public static final Codec<NPCTradeManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ITrade.TYPED_CODEC.listOf().optionalFieldOf("trades").forGetter(i -> Optional.ofNullable(i.trades)),
            ITradeGenerator.TYPED_CODEC.optionalFieldOf("trades_generator").forGetter(i -> Optional.ofNullable(i.tradeList))
    ).apply(instance, (trades, tradeList) -> {
        return trades.map(NPCTradeManager::new).orElseGet(() -> tradeList.map(NPCTradeManager::new).orElseGet(() -> new NPCTradeManager(List.of())));
    }));
    public static final StreamCodec<RegistryFriendlyByteBuf, NPCTradeManager> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private List<ITrade> trades;
    private List<ITrade> availableTrades;
    private ITradeHolder owner;
    private final List<Integer> toBeSync = new ArrayList<>();
    private ITradeGenerator tradeList;

    /**
     * 记录需要更新的交易表
     *
     * @param index 交易表索引
     */
    public void addToBeSync(int index) {
        toBeSync.add(index);
    }

    /**
     * 局部更新所有交易表
     */
    public void syncDirtyTrade() {
        for (int index : toBeSync) {
            syncTradeTasks(index);
        }
        toBeSync.clear();
    }

    private void syncTradeTasks(int index) {
        if (owner != null) {
            owner.syncNpcTrade(index);
        }
    }

    /**
     * 用于传输数据
     *
     * @param trade 当为空时，表示未初始化
     */
    public NPCTradeManager(List<ITrade> trade) {
        this.trades = new ArrayList<>(trade);
    }

    /**
     * 用于初始化
     *
     * @param tradeList 交易列表的生成方式
     */
    public NPCTradeManager(ITradeGenerator tradeList) {
        this.tradeList = tradeList;
    }

    /**
     * 初始化交易列表，将未生成的表生成子表，同时设置owner
     */
    public void initTrades(ITradeHolder holder, ResourceLocation id) {
        if (tradeList != null) {
            this.trades = new ArrayList<>(tradeList.generateTrades(holder));
            this.tradeList = null;
        }
        if (id != null) { // 正常情况只会在第一次生成时不为null
            TradeModifiers.getInstance().applyModifiers(this, id);
        }
        this.setOwner(holder);
    }


    private void setOwner(ITradeHolder npc) {
        this.owner = npc;
    }

    /**
     * 获取所有的交易列表
     */
    public List<ITrade> trades() {
        return this.trades;
    }

    /**
     * 获取可用的交易列表
     */
    public List<ITrade> availableTrades() {
        if (this.availableTrades == null) {
            return this.trades;
        }
        return this.availableTrades;
    }

    /**
     * 获取可用的交易表。玩家实际交易时调用，防止客户端数据不同步
     */
    public ITrade targetTrade(TradeParams params, int index) {

        if (params == null) {
            throw new IllegalArgumentException("Trade params cannot be null");
        }
        BitMask bitMask = params.bitMask();
        int target = 0;
        for (ITrade trade : this.trades) {
            if (bitMask.contains(target)) {
                target++;
                continue;
            }

            if (index == 0) {
                return trade;
            }
            index--;
            target++;
        }
        throw new IllegalArgumentException("Trade index out of range");
    }

    public ITradeGenerator getRawTrades() {
        if (tradeList == null) {
            return new SimpleGenerator(trades);
        }
        return tradeList;
    }

    /**
     * 设置可用的交易列表，在玩家打开商店时，服务端调用
     *
     * @param player 玩家
     */
    public void reCheckAvailableTrades(Player player) {
        int index = 0;
        TradeParams params = this.owner.getTradeParams();
        if (params == null) {
            this.availableTrades = this.trades;
            return;
        }
        BitMask bitMask = params.bitMask();
        boolean dirty = false;
        this.availableTrades = new ArrayList<>();
        for (ITrade trade : this.trades) {
            if (trade.lock().canTrade(player, owner, index)) {
                this.availableTrades.add(trade);
                if (bitMask.remove(index)) {
                    dirty = true;
                }
            } else {
                if (bitMask.add(index)) {
                    dirty = true;
                }
            }
            index++;
        }
        if (dirty) { // 不能因为数据为脏才同步，这样会出现数据不同步
            this.owner.syncTradeTasksParams();
        }
    }

    /**
     * 刷新可用的交易列表，在同步参数时，客户端调用
     */
    public void refreshAvailableTrades() {
        this.availableTrades = new ArrayList<>();
        TradeParams params = this.owner.getTradeParams();
        if (params == null) {
            this.availableTrades = this.trades;
            return;
        }
        BitMask bitMask = params.bitMask();
        int index = 0;
        for (ITrade trade : this.trades) {
            if (!bitMask.contains(index)) {
                this.availableTrades.add(trade);
            }
            index++;
        }
    }

    public boolean isEmpty() {
        return this.trades.isEmpty();
    }

    /**
     * 获取NPC商店的交易列表
     *
     * @param id 交易表id
     */
    @Nullable
    public static NPCTradeManager getTradeById(ResourceLocation id) {
        return Loader.getInstance().tradeMap.getOrDefault(id, null);
    }

    /**
     * 用于初始化npc时给出不同的交易列表防止影响全局
     *
     * @param id 交易表id
     * @return 交易列表的拷贝
     */
    @Nullable
    public static NPCTradeManager getCopy(ResourceLocation id, DynamicOps<Tag> ops) {
        Tag tag = Loader.getInstance().tagMap.get(id);
        if (tag == null) return null;
        return CODEC.parse(ops, tag).result().orElse(null);
    }

    public static class Loader extends SimpleJsonResourceReloadListener {
        public static final String KEY = "npc/shop";
        private static Loader INSTANCE;
        private Map<ResourceLocation, NPCTradeManager> tradeMap = ImmutableMap.of();
        private Map<ResourceLocation, Tag> tagMap = ImmutableMap.of();

        private Loader() {
            super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(), KEY);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            ImmutableMap.Builder<ResourceLocation, NPCTradeManager> map1 = ImmutableMap.builder();
            ImmutableMap.Builder<ResourceLocation, Tag> map2 = ImmutableMap.builder();

            ConditionalOps<JsonElement> ops = makeConditionalOps();
            map.forEach((key, value) -> NPCTradeManager.CODEC.parse(ops, value).ifSuccess(r -> {
                map1.put(key, r);
                map2.put(key, JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, value));
            }).ifError(err -> TerraEntity.LOGGER.error("Failed to read trade list {} :{}", key, err)));

            this.tradeMap = map1.build();
            this.tagMap = map2.build();
        }

        public void syncFromServer(RegistryAccess registryAccess, Map<ResourceLocation, Tag> map) {
            ImmutableMap.Builder<ResourceLocation, NPCTradeManager> map1 = ImmutableMap.builder();

            RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);
            map.forEach((key, value) -> NPCTradeManager.CODEC.parse(ops, value)
                    .ifSuccess(r -> map1.put(key, r))
                    .ifError(err -> TerraEntity.LOGGER.error("Failed to sync trade list {} :{}", key, err)));

            this.tradeMap = map1.build();
        }

        public Map<ResourceLocation, NPCTradeManager> getTradeMap() {
            return tradeMap;
        }

        public Map<ResourceLocation, Tag> getTagMap() {
            return tagMap;
        }

        public static Loader getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new Loader();
            }
            return INSTANCE;
        }
    }
}
