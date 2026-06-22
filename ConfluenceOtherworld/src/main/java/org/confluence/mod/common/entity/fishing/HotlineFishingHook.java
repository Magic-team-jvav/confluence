package org.confluence.mod.common.entity.fishing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.mixed.IFishingHook;

public class HotlineFishingHook extends AbstractFishingHook {
    public HotlineFishingHook(Player player, Level level, int luck, int lureSpeed) {
        super(ModEntities.HOTLINE_FISHING_HOOK.get(), level, luck, lureSpeed);
        IFishingHook.of(this).confluence$setIsLavaHook();
        setup(player);
    }

    public HotlineFishingHook(EntityType<HotlineFishingHook> entityType, Level level) {
        super(entityType, level);
    }
}
