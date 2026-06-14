package org.confluence.mod.common.init.item;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

/// 连枷物品注册
public class FlailItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    /// 致伤球
    public static final PortDeferredItem<BaseFlailItem> BALL_O_HURT = ITEMS.register("ball_o_hurt", () -> new BaseFlailItem(FlailComponent.BALL_O_HURT.get(), ModRarity.ORANGE));

    /// 链球（MACE）
    public static final PortDeferredItem<BaseFlailItem> MACE = ITEMS.register("mace", () -> new BaseFlailItem(FlailComponent.MACE.get(), ModRarity.WHITE));
}
