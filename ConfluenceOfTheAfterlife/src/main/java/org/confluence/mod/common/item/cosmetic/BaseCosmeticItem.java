package org.confluence.mod.common.item.cosmetic;

import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class BaseCosmeticItem extends CustomRarityItem implements ICurioItem { // todo 如果有时装栏了就换
    public BaseCosmeticItem(ModRarity rarity) {
        super(new Properties().stacksTo(1), rarity);
    }

    public BaseCosmeticItem(Properties properties, ModRarity rarity) {
        super(properties.stacksTo(1), rarity);
    }


    @Override
    public boolean hasCurioCapability(ItemStack stack) {
        return false;
    }
}
