package org.confluence.mod.common.entity.fishing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.ModEntities;

public class BloodyFishingHook extends AbstractFishingHook { // todo 使血月期间钓到敌怪的几率翻倍
    public BloodyFishingHook(EntityType<BloodyFishingHook> entityType, Level level) {
        super(entityType, level);
    }

    public BloodyFishingHook(Player player, Level level, int luck, int lureSpeed) {
        super(ModEntities.BLOODY_FISHING_HOOK.get(), level, luck, lureSpeed);
        setup(player);
    }
}
