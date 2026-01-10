package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;

import java.util.function.Supplier;

public class CrossbowItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 弩

    // 连弩

    public static final DeferredItem<BaseTerraRepeaterItem> ADAMANTITE_REPEATER = register("adamantite_repeater", 42,
            new BaseTerraBowItem.ModifyArrowBuilder().setDuration(1000).setRarity(ModRarity.LIGHT_RED),
            new BaseTerraRepeaterItem.Builder().capacity(10).arrowSpeed(10f).reloadTick(18).knockback(2.5f));
    public static final DeferredItem<BaseTerraRepeaterItem> CHLOROPHYTE_SHOTBOW = register("chlorophyte_shotbow", 34,
            new BaseTerraBowItem.ModifyArrowBuilder().setDuration(5000).setRarity(ModRarity.LIME),
            new BaseTerraRepeaterItem.Builder().capacity(32).arrowSpeed(11.5f).reloadTick(19).knockback(2.75f).concurrentCount(2, 3));

    public static <I extends BaseTerraRepeaterItem> DeferredItem<I> register(String name, Supplier<I> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static DeferredItem<BaseTerraRepeaterItem> register(String name, float damage, BaseTerraBowItem.ModifyArrowBuilder bowModifyArrowBuilder, BaseTerraRepeaterItem.Builder repeaterBuilder) {
        return register(name, () -> new BaseTerraRepeaterItem(damage, bowModifyArrowBuilder, repeaterBuilder));
    }
}
