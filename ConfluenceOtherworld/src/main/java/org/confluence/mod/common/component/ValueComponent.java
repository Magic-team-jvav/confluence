package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.util.ClientUtils;
import org.mesdag.portlib.component.PortDataComponentType;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.List;

public record ValueComponent(int value) {
    public static final Codec<ValueComponent> CODEC = Codec.INT.xmap(ValueComponent::new, ValueComponent::value);
    public static final PortStreamCodec<ByteBuf, ValueComponent> STREAM_CODEC = PortByteBufCodecs.INT.map(ValueComponent::new, ValueComponent::value);

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ValueComponent that && that.value == value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    public static int getValue(ItemStack stack, int defaultValue, boolean prototype) {
        long value = getValueLong(stack, defaultValue, prototype);
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, value));
    }

    /// 读取整组物品的售卖价值。
    ///
    /// <p>交易结算需要按长整型计算完整堆叠，避免高价值物品在真正扣除前发生整型回绕。1.20 侧的数据组件
    /// 由 PortLib 的显式桥接层持久化，所以运行时读取必须走同一入口。</p>
    public static long getValueLong(ItemStack stack, int defaultValue, boolean prototype) {
        PortDataComponentType<ValueComponent> type = ModDataComponentTypes.VALUE.get();
        ValueComponent value = prototype ? stack.getPrototype().get(type) : stack.get(type);
        if (value == null) {
            value = stack.getItemHolder().getData(ModDataMaps.VALUE);
            return Math.multiplyExact(
                    (long) (value == null ? defaultValue : value.value()),
                    stack.getCount());
        }
        return Math.multiplyExact((long) value.value(), stack.getCount());
    }

    public static int getValue(ItemStack itemStack, int defaultValue) {
        return getValue(itemStack, defaultValue, false);
    }

    public static long getValueLong(ItemStack itemStack, int defaultValue) {
        return getValueLong(itemStack, defaultValue, false);
    }

    public static void addTooltip(ItemStack stack, List<Component> toolTip) {
        int price = getValue(stack, 0);
        if (price > 0) {
            toolTip.add(Component.translatable("tooltip.price.sell").withStyle(ChatFormatting.GRAY).append(ClientUtils.formatPrice(price)));
        }
    }
}
