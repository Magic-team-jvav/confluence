package org.confluence.terraentity.entity.npc.trade;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITradeModifier;

import java.util.*;

/**
 * 交易表修饰器：修改单个表项或者修改整个交易表{@link ITradeModifier}
 */
public class TradeModifiers extends SimpleJsonResourceReloadListener {
    public static final String KEY = "npc/trade_modifiers";
    public static final Codec<List<ITradeModifier>> CODEC = ITradeModifier.TYPED_CODEC.listOf();
    private static TradeModifiers INSTANCE;
    private Map<ResourceLocation, List<ITradeModifier>> modifiersMap = ImmutableMap.of();

    private TradeModifiers() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(), KEY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, List<ITradeModifier>> map1 = new HashMap<>();

        ConditionalOps<JsonElement> ops = makeConditionalOps();
        map.forEach((k, v) -> CODEC.parse(ops, v).ifSuccess(rl -> {
            for (ITradeModifier modifier : rl) {
                map1.computeIfAbsent(modifier.id(), map2 -> new ArrayList<>()).add(modifier);
            }
        }).ifError(err -> TerraEntity.LOGGER.error("Failed to read modifier map {} :{}", k, err)));

        for (List<ITradeModifier> rl : map1.values()) {
            rl.sort(Comparator.comparing(ITradeModifier::priority)); // 加载时就排序
        }

        this.modifiersMap = ImmutableMap.copyOf(map1);
    }

    public Map<ResourceLocation, List<ITradeModifier>> getModifiers() {
        return modifiersMap;
    }

    public static TradeModifiers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TradeModifiers();
        }
        return INSTANCE;
    }

    public void applyModifiers(NPCTradeManager trade, ResourceLocation location) {
        List<ITradeModifier> modifiers = modifiersMap.get(location);
        if (modifiers != null) {
            for (ITradeModifier modifier : modifiers) {
                modifier.accept(trade, location);
            }
        }
    }
}
