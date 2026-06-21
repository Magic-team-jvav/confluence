package org.confluence.mod.common.component;

import PortLib.extensions.net.minecraft.core.Holder.PortHolderExtension;
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
        PortDataComponentType<ValueComponent> type = ModDataComponentTypes.VALUE.get();
        ValueComponent value = prototype ? stack.getPrototypeData().get(type) : stack.getData(type);
        if (value == null) {
            value = PortHolderExtension.getData(stack.getItemHolder(), ModDataMaps.VALUE);
            return (value == null ? defaultValue : value.value()) * stack.getCount();
        }
        return value.value() * stack.getCount();
    }

    public static int getValue(ItemStack itemStack, int defaultValue) {
        return getValue(itemStack, defaultValue, false);
    }

    public static void addTooltip(ItemStack stack, List<Component> toolTip) {
        int price = getValue(stack, 0);
        if (price > 0) {
            toolTip.add(Component.translatable("tooltip.price.sell").withStyle(ChatFormatting.GRAY).append(ClientUtils.formatPrice(price)));
        }
    }
}
