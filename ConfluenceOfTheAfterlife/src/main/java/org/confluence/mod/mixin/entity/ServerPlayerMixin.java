package org.confluence.mod.mixin.entity;

import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.mixed.IServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements IServerPlayer {
    @Unique
    private boolean confluence$couldPickupItem = true;

    @Override
    public void confluence$setCouldPickupItem(boolean enable) {
        this.confluence$couldPickupItem = enable;
    }

    @Override
    public boolean confluence$isCouldPickupItem() {
        return confluence$couldPickupItem;
    }
}
