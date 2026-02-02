package org.confluence.terra_curio.common.item.curio;

import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.init.TCEffects;
import top.theillusivec4.curios.api.SlotContext;

public class HealPerSecondCurioItem extends BaseCurioItem {
    private final float amount;

    public HealPerSecondCurioItem(float amount, Builder builder) {
        super(builder);
        this.amount = amount;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        TCEffects.healPerSecond(slotContext.entity(), amount);
    }
}
