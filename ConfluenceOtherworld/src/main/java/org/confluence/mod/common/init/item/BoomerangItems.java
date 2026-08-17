package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class BoomerangItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BoomerangItem> ENCHANTED_BOOMERANG = register("enchanted_boomerang", 6.6F, 1.55F, 1.55F, 15, 10, 1, 1, false, 300);
    public static final PortDeferredItem<BoomerangItem> FLAMARANG = register("flamarang", 20.3F, 1.85F, 1.85F, 18, 10, 1, 1, true, 1500);
    public static final PortDeferredItem<BoomerangItem> ICE_BOOMERANG = register("ice_boomerang", 8.3F, 1.6F, 1.6F, 16, 10, 1, 1, false, 500);
    public static final PortDeferredItem<BoomerangItem> SHROOMERANG = register("shroomerang", 8.6F, 1.55F, 1.55F, 15, 10, 1, 1, false, 500);
    public static final PortDeferredItem<BoomerangItem> TRIMARANG = register("trimarang", 8.3F, 1.85F, 1.85F, 17, 10, 3, 1, false, 1000);
    public static final PortDeferredItem<BoomerangItem> COMBAT_WRENCH = register("combat_wrench", 9.0F, 3.0F, 1.85F, 10, 10, 1, 1, false, 1500);
    public static final PortDeferredItem<BoomerangItem> WOOD_BOOMERANG = register("wood_boomerang", 3.8F, 1.52F, 1.52F, 15, 10, 1, 1, false, 100);

    public static final PortDeferredItem<BoomerangItem> BEIDOU_BOOMERANG = register("beidou_boomerang", 5.0F, 3.0F, 3.0F, 40, 5, 4, 7, false, 0);
    public static final PortDeferredItem<BoomerangItem> DEVELOPER_BOOMERANG = register("developer_boomerang", 9999.0F, 2.0F, 2.0F, 50, 0, 10, 8, false, 0);

    private static PortDeferredItem<BoomerangItem> register(String id, float damage, float flySpeed, float backSpeed, int forwardTicks, int cooldown, int maxCount, int penetration, boolean fire, int durability) {
        return ITEMS.register(id, () -> new BoomerangItem(new BoomerangItem.Settings(damage, flySpeed, backSpeed, forwardTicks, cooldown, maxCount, penetration, fire, durability > 0 ? new Item.Properties().durability(durability).stacksTo(1) : new Item.Properties().stacksTo(1))));
    }
}
