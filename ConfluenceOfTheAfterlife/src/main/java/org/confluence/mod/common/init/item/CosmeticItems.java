package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.cosmetic.BaseCosmeticItem;
import org.confluence.terra_curio.common.component.ModRarity;
import top.theillusivec4.curios.api.SlotContext;

import java.util.function.Supplier;

public class CosmeticItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<Item> GOLD_CROWN = ITEMS.register("gold_crown", () -> new BaseCosmeticItem(ModRarity.WHITE) {
        @Override
        public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
            return true;
        }
    });
    public static final Supplier<Item> PLATINUM_CROWN = ITEMS.register("platinum_crown", () -> new BaseCosmeticItem(ModRarity.WHITE));
}
