package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.yoyo.*;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class YoyoItems {
    public static void init() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    // 取一半数值-2
    /// 各特殊球的前四项依次是本体伤害、射程、持续时间和击退；末尾的射弹参数依次为
    /// 相对于本体命中伤害的倍率、初速度，以及需要索敌时的范围。
    public static final DeferredItem<YoyoItem> AMAZON = register("amazon", "亚马逊球", ModRarity.ORANGE, 7F, 13.4375F, 8, 3.75F);
    public static final DeferredItem<YoyoItem> ARTERY = register("artery", "血脉球", ModRarity.BLUE, 6.5F, 12.9375F, 6, 4F);
    public static final DeferredItem<YoyoItem> CASCADE = registerItem("cascade", "喷流球", () -> new CascadeYoyoItem(ModRarity.ORANGE, 11.5F, 14.6875F, 13 * 20, 4.3F, 2, 3, 1, 4, 2.0F, 0.8, 13));
    public static final DeferredItem<YoyoItem> CODE_1 = register("code_1", "代码1球", ModRarity.GREEN, 8F, 13.75F, 9, 3.25F);
    public static final DeferredItem<YoyoItem> HIVE_FIVE = registerItemWithEnglishName("hive_five", "蜂巢球", "Hive-Five", () -> new HiveFiveYoyoItem(ModRarity.ORANGE, 10F, 14.0625F, 11 * 20, 3.75F, 3, 0.5F));
    public static final DeferredItem<YoyoItem> MALAISE = register("malaise", "抑郁球", ModRarity.BLUE, 7F, 12.1875F, 7, 4.5F);
    public static final DeferredItem<YoyoItem> RALLY = register("rally", "对打球", ModRarity.BLUE, 5F, 10.625F, 5, 3.5F);
    public static final DeferredItem<YoyoItem> VALOR = register("valor", "英勇球", ModRarity.ORANGE, 12F, 14.0625F, 11, 3.85F);
    public static final DeferredItem<YoyoItem> WOODEN_YOYO = register("wooden_yoyo", "木悠悠球", ModRarity.WHITE, 2.3F, 8.125F, 3, 2.5F);
    public static final DeferredItem<YoyoItem> CHIK = registerItem("chik", "吉克球", () -> new ChikYoyoItem(ModRarity.LIGHT_RED, 19F, 17.1875F, 16 * 20, 3.3F, 3, 5, 2, 1.0F, 0.8, 7));
    public static final DeferredItem<YoyoItem> FORMAT_C = registerItemWithEnglishName("format_c", "好胜球", "Format:C", () -> new FormatCYoyoItem(ModRarity.PINK, 19, 20, 16 * 20, 3.25F, 0.2F, 2.5F));
    public static final DeferredItem<YoyoItem> HEL_FIRE = registerItemWithEnglishName("hel_fire", "狱火球", "Hel-Fire", () -> new HelFireYoyoItem(ModRarity.LIGHT_RED, 19F, 20.625F, 12 * 20, 4.5F, 60, 160));
    public static final DeferredItem<YoyoItem> AMAROK = registerItem("amarok", "冰雪悠悠球", () -> new AmarokYoyoItem(ModRarity.LIGHT_RED, 21F, 16.875F, 15 * 20, 2.8F, 3, 40, 100));
    public static final DeferredItem<YoyoItem> GRADIENT = register("gradient", "渐变球", ModRarity.LIGHT_RED, 25F, 15.625F, 30, 3.8F);
    public static final DeferredItem<YoyoItem> CODE_2 = register("code_2", "代码2球", ModRarity.PINK, 27F, 17.5F, 0, 3.8F);
    public static final DeferredItem<YoyoItem> YELETS = registerItem("yelets", "叶列茨球", () -> new YeletsYoyoItem(ModRarity.PINK, 28, 18.125F, 14 * 20, 3.1F, 40));
    public static final DeferredItem<YoyoItem> THE_EYE_OF_CTHULHU = registerItemWithEnglishName("the_eye_of_cthulhu", "克苏鲁之眼悠悠球", "The Eye of Cthulhu", () -> new EyeOfCthulhuYoyoItem(ModRarity.YELLOW, 55F, 22.5F, 0, 3.5F, 7, 2, 2.0F, 1.2, 5));
    public static final DeferredItem<YoyoItem> KRAKEN = registerItem("kraken", "克拉肯球", () -> new KrakenYoyoItem(ModRarity.YELLOW, 47, 23.75F, 0, 4.3F, 4, 3, 1.85, 0.75F, 0.1F, 0.5F, 0.8));
    public static final DeferredItem<YoyoItem> TERRARIAN = registerItem("terrarian", "泰拉悠悠球", () -> new TerrarianYoyoItem(ModRarity.RED, 95F, 25, 0, 6.5F, 2, 4, 0.1F, 1.0F, 0.8, 25));

    private static DeferredItem<YoyoItem> register(String registryId, String chineseName, ModRarity rarity, float damage, float range, int lifetime, float knockback) {
        DeferredItem<YoyoItem> entry = ITEMS.register(registryId, () -> new YoyoItem(ModItems.unbreakable(), rarity,
                damage, range, lifetime * 20, knockback));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

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
