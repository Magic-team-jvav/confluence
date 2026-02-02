package org.confluence.terraentity.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.mixed.IPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayer , SelfGetter<Player> {

    @Unique
    private ITradeHolder terra_entity$tradeHolder;

    @Unique
    private boolean terra_entity$isInfiniteInteractBlock;

    @Override
    public ITradeHolder terra_entity$getTradeHolder() {
        return terra_entity$tradeHolder;
    }

    @Override
    public void terra_entity$setTradeHolder(ITradeHolder  entity) {
        this.terra_entity$tradeHolder = entity;
    }

    @Override
    public boolean terra_entity$isInfiniteInteractBlock(){
        return terra_entity$isInfiniteInteractBlock;
    }

    @Override
    public void terra_entity$setInfiniteInteractBlock(boolean flag){
        this.terra_entity$isInfiniteInteractBlock = flag;
    }


    @Inject(method = "canInteractWithBlock", at = @At("HEAD"), cancellable = true)
    private void canInteractWithBlock(BlockPos pos, double distance, CallbackInfoReturnable<Boolean> cir) {
        if(this.terra_entity$isInfiniteInteractBlock){
            cir.setReturnValue(true);
        }
    }
}
