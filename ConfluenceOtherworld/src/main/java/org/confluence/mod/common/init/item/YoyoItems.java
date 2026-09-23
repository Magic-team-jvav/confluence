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
    // 取一半数值-2
    /// 各特殊球的前四项依次是本体伤害、射程、持续时间和击退；末尾的射弹参数依次为
    /// 相对于本体命中伤害的倍率、初速度，以及需要索敌时的范围。
    public static final PortDeferredItem<YoyoItem> AMAZON = register("amazon", ModRarity.ORANGE, 7F, 13.4375F, 8, 3.75F);
    public static final PortDeferredItem<YoyoItem> ARTERY = register("artery", ModRarity.BLUE, 6.5F, 12.9375F, 6, 4F);
    public static final PortDeferredItem<YoyoItem> CASCADE = ITEMS.register("cascade", () -> new CascadeYoyoItem(ModRarity.ORANGE, 11.5F, 14.6875F, 13 * 20, 4.3F, 2, 3, 1, 4, 2.0F, 0.8, 13));
    public static final PortDeferredItem<YoyoItem> CODE_1 = register("code_1", ModRarity.GREEN, 8F, 13.75F, 9, 3.25F);
    public static final PortDeferredItem<YoyoItem> HIVE_FIVE = ITEMS.register("hive_five", () -> new HiveFiveYoyoItem(ModRarity.ORANGE, 10F, 14.0625F, 11 * 20, 3.75F, 3, 0.5F));
    public static final PortDeferredItem<YoyoItem> MALAISE = register("malaise", ModRarity.BLUE, 7F, 12.1875F, 7, 4.5F);
    public static final PortDeferredItem<YoyoItem> RALLY = register("rally", ModRarity.BLUE, 5F, 10.625F, 5, 3.5F);
    public static final PortDeferredItem<YoyoItem> VALOR = register("valor", ModRarity.ORANGE, 12F, 14.0625F, 11, 3.85F);
    public static final PortDeferredItem<YoyoItem> WOODEN_YOYO = register("wooden_yoyo", ModRarity.WHITE, 2.3F, 8.125F, 3, 2.5F);
    public static final PortDeferredItem<YoyoItem> CHIK = ITEMS.register("chik", () -> new ChikYoyoItem(ModRarity.LIGHT_RED, 19F, 17.1875F, 16 * 20, 3.3F, 3, 5, 2, 1.0F, 0.8, 7));
    public static final PortDeferredItem<YoyoItem> FORMAT_C = ITEMS.register("format_c", () -> new FormatCYoyoItem(ModRarity.PINK, 19, 20, 16 * 20, 3.25F, 0.2F, 2.5F));
    public static final PortDeferredItem<YoyoItem> HEL_FIRE = ITEMS.register("hel_fire", () -> new HelFireYoyoItem(ModRarity.LIGHT_RED, 19F, 20.625F, 12 * 20, 4.5F, 60, 160));
    public static final PortDeferredItem<YoyoItem> AMAROK = ITEMS.register("amarok", () -> new AmarokYoyoItem(ModRarity.LIGHT_RED, 21F, 16.875F, 15 * 20, 2.8F, 3, 40, 100));
    public static final PortDeferredItem<YoyoItem> GRADIENT = register("gradient", ModRarity.LIGHT_RED, 25F, 15.625F, 30, 3.8F);
    public static final PortDeferredItem<YoyoItem> CODE_2 = register("code_2", ModRarity.PINK, 27F, 17.5F, 0, 3.8F);
    public static final PortDeferredItem<YoyoItem> YELETS = ITEMS.register("yelets", () -> new YeletsYoyoItem(ModRarity.PINK, 28, 18.125F, 14 * 20, 3.1F, 40));
    public static final PortDeferredItem<YoyoItem> THE_EYE_OF_CTHULHU = ITEMS.register("the_eye_of_cthulhu", () -> new EyeOfCthulhuYoyoItem(ModRarity.YELLOW, 55F, 22.5F, 0, 3.5F, 7, 2, 2.0F, 1.2, 5));
    public static final PortDeferredItem<YoyoItem> KRAKEN = ITEMS.register("kraken", () -> new KrakenYoyoItem(ModRarity.YELLOW, 47, 23.75F, 0, 4.3F, 4, 3, 1.85, 0.75F, 0.1F, 0.5F, 0.8));
    public static final PortDeferredItem<YoyoItem> TERRARIAN = ITEMS.register("terrarian", () -> new TerrarianYoyoItem(ModRarity.RED, 95F, 25, 0, 6.5F, 2, 4, 0.1F, 1.0F, 0.8, 25));

    private static PortDeferredItem<YoyoItem> register(String name, ModRarity rarity, float damage, float range, int lifetime, float knockback) {
        return ITEMS.register(name, () -> new YoyoItem(new Item.Properties().unbreakable(), rarity,
                damage, range, lifetime * 20, knockback));
    }

}
