package org.confluence.terraentity.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Zombie;
import org.confluence.terraentity.mixed.IZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieMixin implements IZombie {
    @Unique
    private boolean terra_entity$slimeZombie = false;

    @Override
    public void terra_entity$setSlimeZombie() {
        this.terra_entity$slimeZombie = true;
    }

    @Override
    public boolean terra_entity$isSlimeZombie() {
        return terra_entity$slimeZombie;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compound, CallbackInfo ci) {
        this.terra_entity$slimeZombie = compound.getBoolean("terra_entity:is_slime_zombie");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void add(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("terra_entity:is_slime_zombie", terra_entity$slimeZombie);
    }
}
