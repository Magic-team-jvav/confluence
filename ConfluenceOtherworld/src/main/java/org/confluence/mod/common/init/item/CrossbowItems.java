package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.crossbow.IRandomCount;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class CrossbowItems {
    public static void init() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 弩

    // 连弩
    // 攻击间隔逐级减小，面板取自泰拉面板一半-2 ，击退十分之一，
    public static final DeferredItem<BaseTerraRepeaterItem> COBALT_REPEATER = register("cobalt_repeater", "钴连弩", 11, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(60).arrowSpeed(2.6f).shootInterval(14).reloadTick(30).knockback(0.25f));
    public static final DeferredItem<BaseTerraRepeaterItem> PALLADIUM_REPEATER = register("palladium_repeater", "钯金连弩", 12, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(80).arrowSpeed(2.6f).shootInterval(13).reloadTick(30).knockback(0.25f));
    public static final DeferredItem<BaseTerraRepeaterItem> MYTHRIL_REPEATER = register("mythril_repeater", "秘银连弩", 13, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(100).arrowSpeed(2.7f).shootInterval(11).reloadTick(28).knockback(0.25f));
    public static final DeferredItem<BaseTerraRepeaterItem> ORICHALCUM_REPEATER = register("orichalcum_repeater", "山铜连弩", 13.5f, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(120).arrowSpeed(2.7f).shootInterval(10).reloadTick(28).knockback(0.25f));
    public static final DeferredItem<BaseTerraRepeaterItem> ADAMANTITE_REPEATER = register("adamantite_repeater", "精金连弩", 14, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(140).arrowSpeed(2.8f).shootInterval(8).reloadTick(26).knockback(0.25f));
    public static final DeferredItem<BaseTerraRepeaterItem> TITANIUM_REPEATER = register("titanium_repeater", "钛金连弩", 15, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(160).arrowSpeed(2.8f).shootInterval(7).reloadTick(26).knockback(0.25f));
    public static final DeferredItem<BaseTerraRepeaterItem> HALLOWED_REPEATER = register("hallowed_repeater", "神圣连弩", 16, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(180).arrowSpeed(2.9f).shootInterval(6).reloadTick(24).knockback(0.25f));
    public static final DeferredItem<BaseTerraRepeaterItem> CHLOROPHYTE_REPEATER = register("chlorophyte_repeater", "叶绿连弩", 13, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIME), new BaseTerraRepeaterItem.Builder()
                    .capacity(200)
                    .shootInterval(7)
                    .arrowSpeed(2.9f)
                    .reloadTick(24)
                    .knockback(0.27f)
                    .concurrentCount(IRandomCount.create(2, 3))
                    .concurrentAngle(1)
                    .concurrentInterval(IRandomCount.create(0.01f, 0.03f)));
    public static final DeferredItem<BaseTerraRepeaterItem> STAKE_LAUNCHER = register("stake_launcher", "尖桩发射器", 21, new BaseTerraRepeaterItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(220).arrowSpeed(3.0f).shootInterval(7).reloadTick(20).knockback(0.25f));

    public static <I extends BaseTerraRepeaterItem> DeferredItem<I> register(String name, Supplier<I> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static DeferredItem<BaseTerraRepeaterItem> register(String name, float damage, BaseTerraRepeaterItem.ModifyArrowBuilder bowModifyArrowBuilder, BaseTerraRepeaterItem.Builder repeaterBuilder) {
        return register(name, () -> new BaseTerraRepeaterItem(damage, bowModifyArrowBuilder, repeaterBuilder));
    }

    private static <I extends BaseTerraRepeaterItem> DeferredItem<I> register(String registryId, String chineseName, Supplier<I> supplier) {
        DeferredItem<I> entry = ITEMS.register(registryId, supplier);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<BaseTerraRepeaterItem> register(String registryId, String chineseName, float damage, BaseTerraRepeaterItem.ModifyArrowBuilder bowModifyArrowBuilder, BaseTerraRepeaterItem.Builder repeaterBuilder) {
        DeferredItem<BaseTerraRepeaterItem> entry = register(registryId, () -> new BaseTerraRepeaterItem(damage, bowModifyArrowBuilder, repeaterBuilder));
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
