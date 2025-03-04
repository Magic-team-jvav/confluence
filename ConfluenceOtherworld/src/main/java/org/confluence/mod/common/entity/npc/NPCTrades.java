package org.confluence.mod.common.entity.npc;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.util.PlayerUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record NPCTrades(List<Trade> trades) {
    public static final String KEY = "npc_shop";
    public static final Codec<NPCTrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Trade.CODEC.listOf().fieldOf("trades").forGetter(NPCTrades::trades)
    ).apply(instance, NPCTrades::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, NPCTrades> STREAM_CODEC = StreamCodec.composite(
            Trade.LIST_STREAM_CODEC, NPCTrades::trades,
            NPCTrades::new
    );
    private static final Map<ResourceLocation, NPCTrades> TRADE_MAP = new HashMap<>();

    /**
     * 获取NPC商店的交易列表
     * @param id 注册交易表的实体type
     */
    public static NPCTrades getTrade(ResourceLocation id) {
        return TRADE_MAP.get(id);
    }

    public static void readTradesFromJson(ResourceManager manager) {
        Map<ResourceLocation, Resource> jsons = manager.listResources(KEY, r -> r.getPath().endsWith(".json"));
        jsons.forEach((k, v) -> {
            try {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(k.getNamespace(),
                        k.getPath().replace(".json", "").replace(KEY + "/", ""));
                Reader reader = manager.openAsReader(k);
                JsonObject jsonobject = GsonHelper.parse(reader);
                TRADE_MAP.put(id, NPCTrades.CODEC.decode(JsonOps.INSTANCE, jsonobject).result().get().getFirst());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public record Trade(ItemStack result, long cost) {
        public boolean canTrade(Player player) {
            return PlayerUtils.getMoney(player) >= cost;
        }

        public static final Codec<Trade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(Trade::result),
                Codec.LONG.fieldOf("cost").forGetter(Trade::cost)
        ).apply(instance, Trade::new));

        public static StreamCodec<RegistryFriendlyByteBuf, Trade> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Trade::result,
                ByteBufCodecs.VAR_LONG, Trade::cost,
                Trade::new
        );
        public static StreamCodec<RegistryFriendlyByteBuf, List<Trade>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity));
    }
}
