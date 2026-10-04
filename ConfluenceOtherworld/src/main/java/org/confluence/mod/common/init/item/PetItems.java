package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.storage.ChesterEntity;
import org.confluence.mod.common.entity.storage.FlyingPiggyBankEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.storage.StorageCompanionItem;

public class PetItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<StorageCompanionItem<ChesterEntity>> CHESTER_STAFF = ITEMS.register("chester_staff", () -> new StorageCompanionItem<>(new Item.Properties(), ModEntities.CHESTER));
    public static final DeferredItem<StorageCompanionItem<FlyingPiggyBankEntity>> MONEY_TROUGH = ITEMS.register("money_trough", () -> new StorageCompanionItem<>(new Item.Properties(), ModEntities.FLYING_PIGGY_BANK));
}
