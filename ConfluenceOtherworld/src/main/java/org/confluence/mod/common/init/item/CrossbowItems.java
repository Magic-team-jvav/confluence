package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.crossbow.IRandomCount;

import java.util.function.Supplier;

public class CrossbowItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    // 弩

    // 连弩
    // 攻击间隔逐级减小，面板取自泰拉面板一半-2 ，击退十分之一，

    public static final RegistryObject<BaseTerraRepeaterItem> COBALT_REPEATER = register("cobalt_repeater", 15, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(60).arrowSpeed(2.6f).shootInterval(8).reloadTick(30).knockback(0.25f));
    public static final RegistryObject<BaseTerraRepeaterItem> PALLADIUM_REPEATER = register("palladium_repeater", 16, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(80).arrowSpeed(2.6f).shootInterval(8).reloadTick(30).knockback(0.25f));
    public static final RegistryObject<BaseTerraRepeaterItem> MYTHRIL_REPEATER = register("mythril_repeater", 17, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(100).arrowSpeed(2.7f).shootInterval(6).reloadTick(28).knockback(0.25f));
    public static final RegistryObject<BaseTerraRepeaterItem> ORICHALCUM_REPEATER = register("orichalcum_repeater", 18, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(120).arrowSpeed(2.7f).shootInterval(6).reloadTick(28).knockback(0.25f));
    public static final RegistryObject<BaseTerraRepeaterItem> ADAMANTITE_REPEATER = register("adamantite_repeater", 19, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(140).arrowSpeed(2.8f).shootInterval(5).reloadTick(26).knockback(0.25f));
    public static final RegistryObject<BaseTerraRepeaterItem> TITANIUM_REPEATER = register("titanium_repeater", 20, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(160).arrowSpeed(2.8f).shootInterval(5).reloadTick(26).knockback(0.25f));
    public static final RegistryObject<BaseTerraRepeaterItem> HALLOWED_REPEATER = register("hallowed_repeater", 21, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(180).arrowSpeed(2.9f).shootInterval(4).reloadTick(24).knockback(0.25f));
    public static final RegistryObject<BaseTerraRepeaterItem> CHLOROPHYTE_REPEATER = register("chlorophyte_repeater", 15,
            new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIME),
            new BaseTerraRepeaterItem.Builder()
                    .capacity(200)
                    .shootInterval(4)
                    .arrowSpeed(2.9f)
                    .reloadTick(24)
                    .knockback(0.27f)
                    .concurrentCount(IRandomCount.create(2, 3))
                    .concurrentAngle(1)
                    .concurrentInterval(IRandomCount.create(0.01f, 0.03f)));
    public static final RegistryObject<BaseTerraRepeaterItem> STAKE_LAUNCHER = register("stake_launcher", 33, new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable().setRarity(ModRarity.LIGHT_RED), new BaseTerraRepeaterItem.Builder().capacity(220).arrowSpeed(3.0f).shootInterval(4).reloadTick(20).knockback(0.25f));

    public static <I extends BaseTerraRepeaterItem> RegistryObject<I> register(String name, Supplier<I> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static RegistryObject<BaseTerraRepeaterItem> register(String name, float damage, BaseTerraArrowItem.ModifyArrowBuilder bowModifyArrowBuilder, BaseTerraRepeaterItem.Builder repeaterBuilder) {
        return register(name, () -> new BaseTerraRepeaterItem(damage, bowModifyArrowBuilder, repeaterBuilder));
    }
}
