package org.confluence.terra_curio.common.item.curio.expert;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.terra_curio.client.TCKeyBindings;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;

import java.util.List;

public class ShieldOfCthulhu extends BaseCurioItem {
    public ShieldOfCthulhu(Builder builder) {
        super(builder);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.terra_curio.shield_of_cthulhu.0",
                MutableComponent.create(TCKeyBindings.CTHULHU_SPRINTING.get().getTranslatedKeyMessage().getContents()).withStyle(ChatFormatting.GRAY))
        );
    }
}
