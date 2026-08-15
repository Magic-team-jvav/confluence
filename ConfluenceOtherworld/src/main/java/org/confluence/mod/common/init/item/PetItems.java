package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.storage.ChesterEntity;
import org.confluence.mod.common.entity.storage.FlyingPiggyBankEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.storage.StorageCompanionItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class PetItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<StorageCompanionItem<ChesterEntity>>
            CHESTER_STAFF = ITEMS.register("chester_staff",
            () -> new StorageCompanionItem<>(
                    new Item.Properties(), ModEntities.CHESTER));
    public static final PortDeferredItem<StorageCompanionItem<FlyingPiggyBankEntity>>
            WALLET = ITEMS.register("wallet",
            () -> new StorageCompanionItem<>(
                    new Item.Properties(), ModEntities.FLYING_PIGGY_BANK));
}
