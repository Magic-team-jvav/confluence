package org.confluence.mod.common.entity.npc.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.AlwaysCondition;

import java.util.Optional;

/**
 * 一条对话规则：聊天内容 + 触发条件 + 冷却时间。
 */
public record ChatLine(NPCChat chat, TradeCondition condition, int cooldownTicks) {
    public static final Codec<ChatLine> CODEC = RecordCodecBuilder.create(b -> b.group(
            NPCChat.CODEC.fieldOf("chat").forGetter(ChatLine::chat),
            TradeCondition.CODEC.optionalFieldOf("condition")
                    .forGetter(l -> l.condition instanceof AlwaysCondition ? Optional.empty() : Optional.of(l.condition)),
            Codec.INT.optionalFieldOf("cooldown_ticks", 6000)
                    .forGetter(ChatLine::cooldownTicks)
    ).apply(b, (chat, cond, cd) -> new ChatLine(chat, cond.orElse(AlwaysCondition.INSTANCE), cd)));

    public boolean canTrigger(ServerLevel level, BaseNPC npc) {
        return condition.test(level, npc);
    }
}
