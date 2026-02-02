package org.confluence.terra_curio.common.item.curio.health;

import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import top.theillusivec4.curios.api.SlotContext;

@javax.annotation.ParametersAreNonnullByDefault
@net.minecraft.MethodsReturnNonnullByDefault
public class BandOfRegeneration extends BaseCurioItem {
    public BandOfRegeneration(Builder builder) {
        super(builder);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        TCEffects.healPerSecond(slotContext.entity(), 0.2F);
    }
}
