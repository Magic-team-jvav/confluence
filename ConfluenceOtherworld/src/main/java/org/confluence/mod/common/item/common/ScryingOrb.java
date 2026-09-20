package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.client.handler.ScryingOrbHandler;

/// 占卜球
// TODO: lore
public class ScryingOrb extends Item {
    public ScryingOrb() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ScryingOrbHandler.changeTarget(level, player);
        return super.use(level, player, usedHand);
    }
}
