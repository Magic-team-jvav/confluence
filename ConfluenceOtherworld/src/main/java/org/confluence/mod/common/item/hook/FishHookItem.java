package org.confluence.mod.common.item.hook;

import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terra_curio.common.component.ModRarity;

public class FishHookItem extends BaseHookItem implements IHookFastThrow {
    public FishHookItem() {
        super(ModRarity.ORANGE, 2, 16.67F, 1.3F, HookType.SIMULTANEOUS, (itemStack, item, player, level) -> new AbstractHookEntity.Impl(ModEntities.FISH_HOOK.get(), item, player, level));
    }
}
