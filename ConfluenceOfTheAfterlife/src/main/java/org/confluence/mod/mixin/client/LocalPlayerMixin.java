package org.confluence.mod.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import org.confluence.mod.mixed.ILocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements ILocalPlayer {
    @Unique
    private boolean confluence$canMove = true;

    @Override
    public void confluence$setCanMove(boolean b) {
        this.confluence$canMove = b;
    }

    @Override
    public boolean confluence$isCanMove() {
        return confluence$canMove;
    }
}
