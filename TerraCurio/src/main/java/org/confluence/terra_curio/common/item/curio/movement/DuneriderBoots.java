package org.confluence.terra_curio.common.item.curio.movement;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class DuneriderBoots extends BaseSpeedBoots {
    public DuneriderBoots(String name) {
        super(1, 40, builder(name).attribute(Attributes.STEP_HEIGHT, 0.5, AttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living.getBlockStateOn().is(BlockTags.SAND)) {
            speedUp(slotContext, stack, 2, 70);
        } else {
            speedUp(slotContext, stack, 1, 40);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.terra_curio.dunerider_boots.0"));
        tooltipComponents.add(Component.translatable("tooltip.item.terra_curio.dunerider_boots.1"));
    }
}
