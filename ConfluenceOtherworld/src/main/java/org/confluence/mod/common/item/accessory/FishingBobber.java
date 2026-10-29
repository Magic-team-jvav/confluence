package org.confluence.mod.common.item.accessory;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;

public class FishingBobber extends BaseCurioItem {
    public final CurioFishingHook.Variant variant;

    public FishingBobber(CurioFishingHook.Variant variant) {
        super(builder("fishing_bobber", new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE))
                .attribute(ConfluenceMagicLib.FISHING_POWER, 10, AttributeModifier.Operation.ADD_VALUE)
                .noTooltip().infos(0));
        this.variant = variant;
    }
}
