package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.yoyo.*;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class YoyoItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<YoyoItem> AMAZON = register("amazon", ModRarity.ORANGE, 4.5F, 13.4375F, 0xFFC896, 8, 3.75F);
    public static final PortDeferredItem<YoyoItem> ARTERY = register("artery", ModRarity.BLUE, 4.2F, 12.9375F, 0x9696FF, 6, 4F);
    public static final PortDeferredItem<YoyoItem> CASCADE = ITEMS.register("cascade", CascadeYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> CODE_1 = register("code_1", ModRarity.GREEN, 4.8F, 13.75F, 0x96FF96, 9, 3.25F);
    public static final PortDeferredItem<YoyoItem> HIVE_FIVE = ITEMS.register("hive_five", HiveFiveYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> MALAISE = register("malaise", ModRarity.BLUE, 3.8F, 12.1875F, 0x9696FF, 7, 4.5F);
    public static final PortDeferredItem<YoyoItem> RALLY = register("rally", ModRarity.BLUE, 3.5F, 10.625F, 0x9696FF, 5, 3.5F);
    public static final PortDeferredItem<YoyoItem> VALOR = register("valor", ModRarity.ORANGE, 5.7F, 14.0625F, 0xFFC896, 11, 3.85F);
    public static final PortDeferredItem<YoyoItem> WOODEN_YOYO = register("wooden_yoyo", ModRarity.WHITE, 1.5F, 8.125F, 0x00FF00, 3, 2.5F);
    public static final PortDeferredItem<YoyoItem> CHIK = ITEMS.register("chik", ChikYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> FORMAT_C = ITEMS.register("format_c", FormatCYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> HEL_FIRE = ITEMS.register("hel_fire", HelFireYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> AMAROK = ITEMS.register("amarok", AmarokYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> GRADIENT = register("gradient", ModRarity.LIGHT_RED, 13.25F, 15.625F, 30, 3.8F);
    public static final PortDeferredItem<YoyoItem> CODE_2 = register("code_2", ModRarity.PINK, 13.5F, 17.5F, 0, 3.8F);
    public static final PortDeferredItem<YoyoItem> YELETS = ITEMS.register("yelets", YeletsYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> THE_EYE_OF_CTHULHU = ITEMS.register("the_eye_of_cthulhu", EyeOfCthulhuYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> KRAKEN = ITEMS.register("kraken", KrakenYoyoItem::new);
    public static final PortDeferredItem<YoyoItem> TERRARIAN = ITEMS.register("terrarian", TerrarianYoyoItem::new);

    private static PortDeferredItem<YoyoItem> register(String name, ModRarity rarity, float damage, float range, int lifetime, float knockback) {
        return ITEMS.register(name, () -> new YoyoItem(new Item.Properties().unbreakable(), rarity,
                damage, range, 0xFFFFFFFF, lifetime * 20, knockback));
    }

    private static PortDeferredItem<YoyoItem> register(String name, ModRarity rarity, float damage, float range, int stringColor, int lifetime, float knockback) {
        return ITEMS.register(name, () -> new YoyoItem(new Item.Properties().unbreakable(), rarity, damage, range, stringColor, lifetime * 20, knockback));
    }
}
