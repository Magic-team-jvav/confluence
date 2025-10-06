package org.confluence.mod.common.entity.fishing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.IFishingHook;

public class HotlineFishingHook extends AbstractFishingHook {
    public HotlineFishingHook(Player pPlayer, Level pLevel, int pLuck, int pLureSpeed) {
        super(ModEntities.HOTLINE_FISHING_HOOK.get(), pLevel, pLuck, pLureSpeed);
        ((IFishingHook) this).confluence$setIsLavaHook();
        setup(pPlayer);
    }

    public HotlineFishingHook(EntityType<HotlineFishingHook> entityType, Level level) {
        super(entityType, level);
    }
}
