package org.confluence.terraentity.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.entity.TESummonEntities;
import org.confluence.terraentity.item.ChesterSummonItem;

public class TEPetItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TerraEntity.MODID);

    public static final DeferredItem<Item> CHESTER_STAFF = ITEMS.register("chester_staff", () -> new ChesterSummonItem<>(new Item.Properties(), TESummonEntities.CHESTER));
    public static final DeferredItem<Item> WALLET = ITEMS.register("wallet", () -> new ChesterSummonItem<>(new Item.Properties(), TESummonEntities.PIGGY_BANK));


}
