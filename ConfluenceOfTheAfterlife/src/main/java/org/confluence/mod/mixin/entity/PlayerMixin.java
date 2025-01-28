package org.confluence.mod.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.entity.npc.NPCTrades;
import org.confluence.mod.mixed.IDamageSource;
import org.confluence.mod.mixed.IPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayer {
    @Unique
    private NPCTrades rhyme$NPCTrades;
    @Unique
    private Entity rhyme$interactingEntity;

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void attack(Entity target, CallbackInfo ci, @Local DamageSource damagesource, @Local(ordinal = 2) boolean flag1) {
        ((IDamageSource) damagesource).confluence$setCritical(flag1);
    }

    public NPCTrades rhyme$getDaveTrades() {
        return rhyme$NPCTrades;
    }

    public void rhyme$setDaveTrades(NPCTrades NPCTrades) {
        rhyme$NPCTrades = NPCTrades;
    }

    public Entity rhyme$getInteractingEntity() {
        return rhyme$interactingEntity;
    } // getRhyme$dave

    public void rhyme$setInteractingEntity(Entity entity) {
        this.rhyme$interactingEntity = entity;
    } // setRhyme$dave
}
