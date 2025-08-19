package org.confluence.mod.mixin.client.entity;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.confluence.mod.client.gui.TombstoneEditScreen;
import org.confluence.mod.common.block.common.TombstoneBlock;
import org.confluence.mod.common.init.block.ModBlocks;
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

    @Shadow
    public Input input;

//    @Shadow
//    public abstract boolean isUnderWater();

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
        if (signEntity instanceof TombstoneBlock.BEntity entity) {
            Block block = entity.getBlockState().getBlock();
            boolean isGold = ModBlocks.TOMBSTONES.object2BooleanEntrySet().stream()
                    .filter(entry -> entry.getKey().get() == block)
                    .findAny().map(Object2BooleanMap.Entry::getBooleanValue).orElse(false);
            minecraft.setScreen(new TombstoneEditScreen(entity, isFrontText, minecraft.isTextFilteringEnabled(), isGold));
            ci.cancel();
        }
    }
// 全向冲刺核心代码
//    @ModifyReturnValue(method = "hasEnoughImpulseToStartSprinting", at = @At("RETURN"))
//    private boolean omni(boolean original, @Local double d0) {
//        if (original) return true;
//        if (isUnderWater()) return input.forwardImpulse < -Mth.EPSILON || input.leftImpulse > Mth.EPSILON || input.leftImpulse < -Mth.EPSILON;
//        return input.forwardImpulse < -d0 || input.leftImpulse > d0 || input.leftImpulse < -d0;
//    }
//
//    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;hasForwardImpulse()Z"))
//    private boolean omni(boolean original) {
//        return original || input.forwardImpulse < -Mth.EPSILON || input.leftImpulse > Mth.EPSILON || input.leftImpulse < -Mth.EPSILON;
//    }
}
