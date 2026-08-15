package org.confluence.mod.common.entity.npc.trade;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.Objects;

/**
 * 数据包声明的稳定 NPC 商品报价。
 *
 * <p>价格以铜币为最小单位并使用长整型；报价 ID 在网络请求、会话快照和附属扩展中
 * 保持稳定，不允许客户端用显示槽位或物品 NBT 代替服务端报价身份。</p>
 */
public record NPCTradeOffer(ResourceLocation id, ItemStack stack, long basePrice,
                            int maxUses, TradeCondition condition) {
    private static final Codec<Long> PRICE_CODEC = Codec.LONG.comapFlatMap(price -> {
        if (price < 1) {
            return DataResult.error(() -> "NPC trade price must be positive");
        }
        return DataResult.success(price);
    }, price -> price);

    public static final Codec<NPCTradeOffer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(NPCTradeOffer::id),
            PortItemStackExtension.codec().fieldOf("item").forGetter(NPCTradeOffer::stack),
            PRICE_CODEC.fieldOf("price").forGetter(NPCTradeOffer::basePrice),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("max_uses", Integer.MAX_VALUE)
                    .forGetter(NPCTradeOffer::maxUses),
            PortCodecExtension.lenientOptionalFieldOf(TradeCondition.CODEC, "condition", TradeCondition.alwaysTrue()).forGetter(NPCTradeOffer::condition)
    ).apply(instance, NPCTradeOffer::new));

    public NPCTradeOffer {
        Objects.requireNonNull(id, "NPC trade offer id must not be null");
        Objects.requireNonNull(stack, "NPC trade result must not be null");
        Objects.requireNonNull(
                condition, "NPC trade condition must not be null");
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("NPC trade result cannot be empty");
        }
        if (basePrice < 1) {
            throw new IllegalArgumentException("NPC trade price must be positive");
        }
        if (maxUses < 1) {
            throw new IllegalArgumentException(
                    "NPC trade maximum uses must be positive");
        }
        stack = stack.copy();
    }

    /**
     * 返回商品结果的独立副本。
     *
     * <p>报价定义会被多个玩家会话和附属事件共享，外部调用方不能通过修改返回的
     * {@link ItemStack} 间接污染已经加载的商店表。真正成交时仍会由会话再复制一次，
     * 让显示、扣款和发货都各自持有独立物品栈。</p>
     */
    @Override
    public ItemStack stack() {
        return stack.copy();
    }

    public boolean isAvailable(ServerPlayer player, BaseNPC npc) {
        return condition.test(player, npc);
    }

    public long priceFor(BaseNPC npc) {
        return Math.max(1L, Math.round(
                basePrice * (double) npc.getMood().getBuyPriceMultiplier()));
    }
}
