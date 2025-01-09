package org.confluence.mod.mixin.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;
import org.confluence.mod.common.entity.npc.NPCTrades;
import org.confluence.mod.mixed.IDamageSource;
import org.confluence.mod.mixed.IPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public class PlayerMixin implements IPlayer {
    @Inject(method = "attack",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void attack(Entity target, CallbackInfo ci, float f, ItemStack itemstack, DamageSource damagesource, float f1, float f2, boolean flag4, boolean flag, boolean flag1, CriticalHitEvent critEvent, float f3, boolean flag2, double d0, boolean critBlocksSweep, SweepAttackEvent sweepEvent, float f6, Vec3 vec3){
        ((IDamageSource) damagesource).confluence$setCritical(flag1);
    }


    @Unique
    private NPCTrades rhyme$NPCTrades;
    @Unique
    private Entity rhyme$interactingEntity;

    public NPCTrades rhyme$getDaveTrades() {
        return rhyme$NPCTrades;
    }
    public void rhyme$setDaveTrades(NPCTrades NPCTrades) {
        rhyme$NPCTrades = NPCTrades;
    }

    public Entity rhyme$getInteractingEntity(){
        return rhyme$interactingEntity;
    } // getRhyme$dave
    public void rhyme$setInteractingEntity(Entity entity){
        this.rhyme$interactingEntity = entity;
    } // setRhyme$dave

}
