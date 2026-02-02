package org.confluence.terra_curio.common.item.curio.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.item.curio.HealPerSecondCurioItem;
import org.confluence.terra_curio.util.CuriosUtils;
import top.theillusivec4.curios.api.SlotContext;

public class CelestialStone extends HealPerSecondCurioItem {
    public CelestialStone(Builder builder) {
        super(0.2F, builder);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        return CuriosUtils.noSameCurio(living, this) && CuriosUtils.noSameCurio(living, TCItems.CELESTIAL_SHELL.get());
    }
}
