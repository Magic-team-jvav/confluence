package org.confluence.mod.common.item.axe;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.StaffOfRegrowth;

import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class AxeOfRegrowth extends BaseAxeItem {
    public AxeOfRegrowth() {
        super(ModTiers.PLATINUM, 7, 1, unbreakable(), ModRarity.LIGHT_RED);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return StaffOfRegrowth.useOnAction(context);
    }
}
