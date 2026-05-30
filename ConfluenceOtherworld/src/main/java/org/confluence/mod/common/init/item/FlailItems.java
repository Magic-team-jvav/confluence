package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.item.flail.BaseFlailItem;

/**
 * 连枷物品注册
 */
public class FlailItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    /** 致伤球 */
    public static final DeferredItem<BaseFlailItem> BALL_O_HURT = ITEMS.register("ball_o_hurt", () ->
            new BaseFlailItem(FlailComponent.BALL_O_HURT.get(), ModRarity.ORANGE));

    /** 链球（MACE） */
    public static final DeferredItem<BaseFlailItem> MACE = ITEMS.register("mace", () ->
            new BaseFlailItem(FlailComponent.MACE.get(), ModRarity.WHITE));
}
