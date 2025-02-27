package org.confluence.mod.common.item.accessory;

import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.api.primitive.FloatValue;
import org.confluence.terra_curio.common.component.AccessoriesComponent;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;

public class FishingBobber extends BaseCurioItem {
    public final CurioFishingHook.Variant variant;

    public FishingBobber(CurioFishingHook.Variant variant) {
        super(new Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.BLUE).component(TCDataComponentTypes.ACCESSORIES, AccessoriesComponent.entry(AccessoryItems.FISHING$POWER, new FloatValue(10.0F))));
        this.variant = variant;
    }
}
