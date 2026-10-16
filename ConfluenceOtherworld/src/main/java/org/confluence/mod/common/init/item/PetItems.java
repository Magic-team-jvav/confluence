package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.storage.ChesterEntity;
import org.confluence.mod.common.entity.storage.FlyingPiggyBankEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.storage.StorageCompanionItem;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class PetItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<StorageCompanionItem<ChesterEntity>> CHESTER_STAFF = registerItemWithEnglishName("chester_staff", "眼骨", "Eye Bone", () -> new StorageCompanionItem<>(new Item.Properties(), ModEntities.CHESTER));
    public static final DeferredItem<StorageCompanionItem<FlyingPiggyBankEntity>> MONEY_TROUGH = registerItem("money_trough", "钱币槽", () -> new StorageCompanionItem<>(new Item.Properties(), ModEntities.FLYING_PIGGY_BANK));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
