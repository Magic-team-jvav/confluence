package org.confluence.mod.common.item.hook;

import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terra_curio.common.component.ModRarity;

public class WebSlingerItem extends BaseHookItem implements IHookFastThrow {
    public WebSlingerItem() {
        super(ModRarity.GREEN, 8, 15.08F, 1.0F, HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.WEB_SLINGER.get(), item, player, level));
    }
}
