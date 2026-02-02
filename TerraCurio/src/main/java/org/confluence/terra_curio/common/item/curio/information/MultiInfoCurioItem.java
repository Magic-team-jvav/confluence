package org.confluence.terra_curio.common.item.curio.information;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.terra_curio.common.item.IMultiFunctionCouldEnable;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;

import java.util.List;
import java.util.Optional;

@javax.annotation.ParametersAreNonnullByDefault
@net.minecraft.MethodsReturnNonnullByDefault
public class MultiInfoCurioItem extends BaseCurioItem implements IMultiFunctionCouldEnable {
    public MultiInfoCurioItem(Builder builder) {
        super(builder);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.ofNullable(getTooltipComponent(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!tooltipFlag.hasShiftDown()) {
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }
}
