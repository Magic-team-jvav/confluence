package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.crossbow.RandomCount;

import java.util.function.Supplier;

public class CrossbowItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 弩

    // 连弩

    public static final DeferredItem<BaseTerraRepeaterItem> ADAMANTITE_REPEATER = register("adamantite_repeater", 42, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> COBALT_REPEATER = register("cobalt_repeater", 42, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> MYTHRIL_REPEATER = register("mythril_repeater", 42, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> ORICHALCUM_REPEATER = register("orichalcum_repeater", 42, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> PALLADIUM_REPEATER = register("palladium_repeater", 42, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> TITANIUM_REPEATER = register("titanium_repeater", 42, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> STAKE_LAUNCHER = register("stake_launcher", 42, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> Hallowed_REPEATER = register("hallowed_repeater", 42, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> CHLOROPHYTE_REPEATER = register("chlorophyte_repeater", 34,
            new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(5000).setRarity(ModRarity.LIME),
            new BaseTerraRepeaterItem.Builder()
                    .capacity(32)
                    .arrowSpeed(11.5f)
                    .reloadTick(19)
                    .knockback(2.75f)
                    .concurrentCount(RandomCount.create(2, 3))
                    .concurrentAngle(1)
                    .concurrentInterval(RandomCount.create(0.01f, 0.03f)));

    public static <I extends BaseTerraRepeaterItem> DeferredItem<I> register(String name, Supplier<I> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static DeferredItem<BaseTerraRepeaterItem> register(String name, float damage, BaseTerraArrowItem.ModifyArrowBuilder bowModifyArrowBuilder, BaseTerraRepeaterItem.Builder repeaterBuilder) {
        return register(name, () -> new BaseTerraRepeaterItem(damage, bowModifyArrowBuilder, repeaterBuilder));
    }
}
