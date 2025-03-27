package org.confluence.mod.integration.terra_entity;

import net.minecraft.core.component.DataComponentPatch;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terraentity.init.TEItems;

import java.util.function.Consumer;

public class TEItemComponentModify {

    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        Consumer<DataComponentPatch.Builder> orange = builder -> builder.set(TCDataComponentTypes.MOD_RARITY.get(), ModRarity.ORANGE);
        event.modify(TEItems.SWAMP_WHIP.get(), orange);

    }
}