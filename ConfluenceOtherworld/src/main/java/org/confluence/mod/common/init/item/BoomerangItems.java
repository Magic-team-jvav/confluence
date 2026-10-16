package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.boomerang.BeiDouBoomerangItem;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class BoomerangItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BoomerangItem> ENCHANTED_BOOMERANG = register("enchanted_boomerang", "附魔回旋镖", 6.6F, 1.55F, 1.55F, 15, 10, 1, 1, false, 300);
    public static final PortDeferredItem<BoomerangItem> FLAMARANG = register("flamarang", "烈焰回旋镖", 20.3F, 1.85F, 1.85F, 18, 10, 1, 1, true, 1500);
    public static final PortDeferredItem<BoomerangItem> ICE_BOOMERANG = register("ice_boomerang", "冰雪回旋镖", 8.3F, 1.6F, 1.6F, 16, 10, 1, 1, false, 500);
    public static final PortDeferredItem<BoomerangItem> SHROOMERANG = register("shroomerang", "蘑菇回旋镖", 8.6F, 1.55F, 1.55F, 15, 10, 1, 1, false, 500);
    public static final PortDeferredItem<BoomerangItem> TRIMARANG = register("trimarang", "三尖回旋镖", 8.3F, 1.85F, 1.85F, 17, 10, 3, 1, false, 1000);
    public static final PortDeferredItem<BoomerangItem> COMBAT_WRENCH = register("combat_wrench", "战斗扳手", 9.0F, 3.0F, 1.85F, 10, 10, 1, 1, false, 1500);
    public static final PortDeferredItem<BoomerangItem> WOOD_BOOMERANG = register("wood_boomerang", "木回旋镖", 3.8F, 1.52F, 1.52F, 15, 10, 1, 1, false, 100);

    public static final PortDeferredItem<BoomerangItem> BEIDOU_BOOMERANG = registerItem("bei_dou_boomerang", "北斗飞镖", BeiDouBoomerangItem::new);
    public static final PortDeferredItem<BoomerangItem> DEVELOPER_BOOMERANG = register("developer_boomerang", "开发者回旋镖", 9999.0F, 2.0F, 2.0F, 50, 0, 10, 8, false, 0);

    private static PortDeferredItem<BoomerangItem> register(String registryId, String chineseName, float damage, float flySpeed, float backSpeed, int forwardTicks, int cooldown, int maxCount, int penetration, boolean fire, int durability) {
        PortDeferredItem<BoomerangItem> entry = ITEMS.register(registryId, () -> new BoomerangItem(new BoomerangItem.Settings(damage, flySpeed, backSpeed, forwardTicks, cooldown, maxCount, penetration, fire, durability > 0 ? new Item.Properties().durability(durability).stacksTo(1) : new Item.Properties().stacksTo(1))));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
