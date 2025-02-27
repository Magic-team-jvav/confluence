package org.confluence.mod.common.entity.fishing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEntities;

public class BloodyFishingHook extends AbstractFishingHook { // todo 使血月期间钓到敌怪的几率翻倍
    public BloodyFishingHook(EntityType<BloodyFishingHook> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BloodyFishingHook(Player player, Level pLevel, int pLuck, int pLureSpeed) {
        super(ModEntities.BLOODY_FISHING_HOOK.get(), pLevel, pLuck, pLureSpeed);
        setup(player);
    }
}
