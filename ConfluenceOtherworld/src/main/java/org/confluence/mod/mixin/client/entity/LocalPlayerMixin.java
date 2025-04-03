package org.confluence.mod.mixin.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.confluence.mod.client.gui.TombstoneEditScreen;
import org.confluence.mod.common.block.common.TombstoneBlock;
import org.confluence.mod.mixed.ILocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements ILocalPlayer {
    @Shadow
    @Final
    protected Minecraft minecraft;
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

    @Inject(method = "openTextEdit", at = @At("HEAD"), cancellable = true)
    private void openTombstoneEdit(SignBlockEntity signEntity, boolean isFrontText, CallbackInfo ci) {
        if (signEntity instanceof TombstoneBlock.Entity entity) {
            minecraft.setScreen(new TombstoneEditScreen(entity, isFrontText, minecraft.isTextFilteringEnabled()));
            ci.cancel();
        }
    }
}
