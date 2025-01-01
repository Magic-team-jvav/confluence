package org.confluence.mod.common.init.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.vanity_armor.BaseVanityArmorItem;
import org.confluence.mod.common.item.vanity_armor.BaseVanityDyeItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.function.Supplier;

public class VanityArmorItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Supplier<BaseVanityArmorItem> GOLD_CROWN = ITEMS.register("gold_crown", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE) {
        @Override
        public boolean makesPiglinsNeutral(Player player, ItemStack stack) {
            return true;
        }
    });
    public static final Supplier<BaseVanityArmorItem> PLATINUM_CROWN = ITEMS.register("platinum_crown", () -> new BaseVanityArmorItem(ArmorItem.Type.HELMET, ModRarity.WHITE));

    public static final Supplier<BaseVanityDyeItem> WHITE_DYE = ITEMS.register("white_dye", () -> new BaseVanityDyeItem(0xFFFFFFFF, ModRarity.ORANGE));
}
