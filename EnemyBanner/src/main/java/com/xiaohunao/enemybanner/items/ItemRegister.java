package com.xiaohunao.enemybanner.items;

import com.xiaohunao.enemybanner.BannerParameters;
import com.xiaohunao.enemybanner.EnemyBanner;
import com.xiaohunao.enemybanner.blocks.BlockRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public class ItemRegister {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnemyBanner.MODID);

    public static final DeferredItem<SilksItem> BASIC_SILKS = ITEMS.registerItem("basic_silks", SilksItem::new);
    public static final DeferredItem<SilksItem> DAMAGE_SILKS = ITEMS.registerItem("damage_silks", SilksItem::new);
    public static final DeferredItem<SilksItem> INHIBIT_SILKS = ITEMS.registerItem("inhibit_silks", SilksItem::new);
    public static final DeferredItem<SilksItem> LOOT_SILKS = ITEMS.registerItem("loot_silks", SilksItem::new);
    public static final DeferredItem<SilksItem> PULL_SILKS = ITEMS.registerItem("pull_silks", SilksItem::new);
    public static final DeferredItem<SilksItem> PUSH_SILKS = ITEMS.registerItem("push_silks", SilksItem::new);
    public static final DeferredItem<SilksItem> RANGE_SILKS = ITEMS.registerItem("range_silks", SilksItem::new);
    public static final DeferredItem<SilksItem> RESIST_SILKS = ITEMS.registerItem("resist_silks", SilksItem::new);

    public static final DeferredItem<EnemyBannerItem> ENEMY_BANNER = ITEMS.register("enemy_banner",
        () -> new EnemyBannerItem(
                BlockRegister.ENEMY_BANNER.get(),
                new Item.Properties()
                        .stacksTo(1)
                        .component(
                                BannerParameters.BANNER_DATA_COMPONENT,
                                new BannerParameters("minecraft:zombie", DyeColor.WHITE.getId(), BASIC_SILKS.getId().getPath())
                )
        )
    );

    public static final DeferredItem<EnemyBannerItem> ENEMY_BANNER_PLANE = ITEMS.register("enemy_banner_plane",
            () -> new EnemyBannerItem(
                    BlockRegister.ENEMY_BANNER.get(),
                    new Item.Properties()
                            .stacksTo(1)
                            .component(
                                    BannerParameters.BANNER_DATA_COMPONENT,
                                    new BannerParameters("minecraft:zombie", DyeColor.WHITE.getId(), BASIC_SILKS.getId().getPath())
                            )
            )
    );

    public static final DeferredItem<BlockItem> BANNER_BOX = ITEMS.registerSimpleBlockItem(BlockRegister.BANNER_BOX);


    public static final Map<String, DeferredItem<SilksItem>> SILKS_MAP = Map.of(
            BASIC_SILKS.getId().getPath(), BASIC_SILKS,
            DAMAGE_SILKS.getId().getPath(), DAMAGE_SILKS,
            INHIBIT_SILKS.getId().getPath(), INHIBIT_SILKS,
            LOOT_SILKS.getId().getPath(), LOOT_SILKS,
            PULL_SILKS.getId().getPath(), PULL_SILKS,
            PUSH_SILKS.getId().getPath(), PUSH_SILKS,
            RANGE_SILKS.getId().getPath(), RANGE_SILKS,
            RESIST_SILKS.getId().getPath(), RESIST_SILKS
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
