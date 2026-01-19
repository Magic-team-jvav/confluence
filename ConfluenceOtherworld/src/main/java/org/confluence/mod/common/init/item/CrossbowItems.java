package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.crossbow.IRandomCount;

import java.util.function.Supplier;

public class CrossbowItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 弩

    // 连弩

    public static final DeferredItem<BaseTerraRepeaterItem> COBALT_REPEATER = register("cobalt_repeater", 5, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(60).arrowSpeed(2.6f).reloadTick(30).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> PALLADIUM_REPEATER = register("palladium_repeater", 6, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(80).arrowSpeed(2.6f).reloadTick(30).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> MYTHRIL_REPEATER = register("mythril_repeater", 7, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(100).arrowSpeed(2.7f).reloadTick(28).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> ORICHALCUM_REPEATER = register("orichalcum_repeater", 8, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(120).arrowSpeed(2.7f).reloadTick(28).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> ADAMANTITE_REPEATER = register("adamantite_repeater", 9, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(140).arrowSpeed(2.8f).reloadTick(26).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> TITANIUM_REPEATER = register("titanium_repeater", 10, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(160).arrowSpeed(2.8f).reloadTick(26).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> HALLOWED_REPEATER = register("hallowed_repeater", 13, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(180).arrowSpeed(2.9f).reloadTick(24).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> CHLOROPHYTE_REPEATER = register("chlorophyte_repeater", 5,
            new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIME),
            new BaseTerraRepeaterItem.Builder()
                    .capacity(200)
                    .arrowSpeed(2.9f)
                    .reloadTick(24)
                    .knockback(2.75f)
                    .concurrentCount(IRandomCount.create(2, 3))
                    .concurrentAngle(1)
                    .concurrentInterval(IRandomCount.create(0.01f, 0.03f)));
    public static final DeferredItem<BaseTerraRepeaterItem> STAKE_LAUNCHER = register("stake_launcher", 16, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(220).arrowSpeed(3.0f).reloadTick(20).knockback(2.5f));

    public static <I extends BaseTerraRepeaterItem> DeferredItem<I> register(String name, Supplier<I> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static DeferredItem<BaseTerraRepeaterItem> register(String name, float damage, BaseTerraArrowItem.ModifyArrowBuilder bowModifyArrowBuilder, BaseTerraRepeaterItem.Builder repeaterBuilder) {
        return register(name, () -> new BaseTerraRepeaterItem(damage, bowModifyArrowBuilder, repeaterBuilder));
    }
}
