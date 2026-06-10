package org.confluence.mod.common.init.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.item.flail.BaseFlailItem;

/**
 * 连枷物品注册
 */
public class FlailItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Confluence.MODID);

    /**
     * 致伤球
     */
    public static final RegistryObject<BaseFlailItem> BALL_O_HURT = ITEMS.register("ball_o_hurt", () ->
            new BaseFlailItem(FlailComponent.BALL_O_HURT.get(), ModRarity.ORANGE));

    /**
     * 链球（MACE）
     */
    public static final RegistryObject<BaseFlailItem> MACE = ITEMS.register("mace", () ->
            new BaseFlailItem(FlailComponent.MACE.get(), ModRarity.WHITE));
}
