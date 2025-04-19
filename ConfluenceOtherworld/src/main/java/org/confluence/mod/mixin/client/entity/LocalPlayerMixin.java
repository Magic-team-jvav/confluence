package org.confluence.mod.mixin.client.entity;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.client.gui.TombstoneEditScreen;
import org.confluence.mod.common.block.common.TombstoneBlock;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.item.bow.TerraBowItem;
import org.confluence.mod.mixed.ILocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements ILocalPlayer, SelfGetter<LocalPlayer> {
    @Shadow
    @Final
    protected Minecraft minecraft;
    @Shadow
    public abstract boolean isUsingItem();
    @Shadow
    public Input input;

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
            Block block = entity.getBlockState().getBlock();
            boolean isGold = ModBlocks.TOMBSTONES.object2BooleanEntrySet().stream()
                    .filter(entry -> entry.getKey().get() == block)
                    .findAny().map(Object2BooleanMap.Entry::getBooleanValue).orElse(false);
            minecraft.setScreen(new TombstoneEditScreen(entity, isFrontText, minecraft.isTextFilteringEnabled(), isGold));
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER))
    public void aiStep(CallbackInfo ci) {
        if (isUsingItem() && !confluence$self().isPassenger()) {
            if (confluence$self().getUseItem().getItem() instanceof TerraBowItem) {
                this.input.leftImpulse *= 5;
                this.input.forwardImpulse *= 5;
            }
        }
    }
}
