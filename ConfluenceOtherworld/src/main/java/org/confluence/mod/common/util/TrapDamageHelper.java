package org.confluence.mod.common.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.item.VanityArmorItems;

public final class TrapDamageHelper {
    private static final float MAX_REDUCTION = 78.0F;

    public static float applyDeadMansSweaterReduction(LivingEntity living, float original) {
        if (living.getItemBySlot(EquipmentSlot.CHEST).is(VanityArmorItems.DEAD_MANS_SWEATER.get())) {
            if (original <= MAX_REDUCTION) {
                return original * 0.5F;
            }
            return original - MAX_REDUCTION;
        }
        return original;
    }

    private TrapDamageHelper() {}
}