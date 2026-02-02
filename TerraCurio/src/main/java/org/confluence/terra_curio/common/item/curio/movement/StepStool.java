package org.confluence.terra_curio.common.item.curio.movement;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.common.entity.StepStoolEntity;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terra_curio.network.s2c.StepStoolSteppingPacketS2C;
import org.confluence.terra_curio.util.CuriosUtils;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class StepStool extends BaseCurioItem {
    public StepStool(Builder builder) {
        super(builder);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (prevStack.getItem() == stack.getItem()) return;
        super.onEquip(slotContext, prevStack, stack);
        if (!slotContext.entity().level().isClientSide) {
            StepStoolSteppingPacketS2C.sendToClient(slotContext, LibUtils.getItemStackNbtNoCopy(stack).getInt("extraStep") + 1);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack.getItem() == stack.getItem()) return;
        super.onUnequip(slotContext, newStack, stack);
        Level level = slotContext.entity().level();
        if (!level.isClientSide) {
            StepStoolSteppingPacketS2C.resetStep(slotContext.entity());
            if (level.getEntity(LibUtils.getItemStackNbtNoCopy(stack).getInt("id")) instanceof StepStoolEntity stepStool) {
                stepStool.setOwner(null);
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosUtils.noSameCurio(slotContext.entity(), StepStool.class);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
                "tooltip.item.terra_curio.step_stool.1", LibUtils.getItemStackNbtNoCopy(stack).getInt("extraStep")
        ).withStyle(style -> style.withColor(ChatFormatting.BLUE)));
    }
}
