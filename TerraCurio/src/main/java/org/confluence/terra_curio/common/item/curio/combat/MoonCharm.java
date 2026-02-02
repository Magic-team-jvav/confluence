package org.confluence.terra_curio.common.item.curio.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.item.curio.NightBonusCurioItem;
import org.confluence.terra_curio.util.CuriosUtils;
import top.theillusivec4.curios.api.SlotContext;

public class MoonCharm extends NightBonusCurioItem implements ICosmetic {
    public MoonCharm(Builder builder) {
        super(0.1F, builder);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        return CuriosUtils.noSameCurio(living, this) && CuriosUtils.noSameCurio(living, ICosmetic.class);
    }
}
