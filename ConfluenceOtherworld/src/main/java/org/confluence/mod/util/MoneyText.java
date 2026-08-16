package org.confluence.mod.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/// 把以铜币为最小单位的价格转换为可本地化文本。
///
/// <p>该工具位于通用代码中，服务端可以把带翻译键的价格写入商品展示信息，客户端也可以直接复用。
/// 使用 {@code long} 是为了避免高价商品在格式化阶段退回旧的整型上限。</p>
public final class MoneyText {
    private static final long PLATINUM = 1_000_000L;
    private static final long GOLD = 10_000L;
    private static final long SILVER = 100L;

    private MoneyText() {}

    public static Component format(long price) {
        if (price < 0L) {
            throw new IllegalArgumentException("Money text price cannot be negative");
        }

        long platinum = price / PLATINUM;
        price %= PLATINUM;
        long gold = price / GOLD;
        price %= GOLD;
        long silver = price / SILVER;
        long copper = price % SILVER;

        MutableComponent result = Component.empty();
        append(result, platinum, "tooltip.price.platinum", -4996668);
        append(result, gold, "tooltip.price.gold", -3891380);
        append(result, silver, "tooltip.price.silver", -4532777);
        append(result, copper, "tooltip.price.copper", -3837899);
        return result;
    }

    private static void append(
            MutableComponent target,
            long amount,
            String translationKey,
            int color
    ) {
        if (amount <= 0L) {
            return;
        }
        MutableComponent part = Component.literal(amount + " ")
                .append(Component.translatable(translationKey));
        target.append(part.withColor(color));
    }
}
