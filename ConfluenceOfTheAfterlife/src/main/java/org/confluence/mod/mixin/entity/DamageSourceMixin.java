package org.confluence.mod.mixin.entity;

import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.mixed.IDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements IDamageSource {
    @Unique
    private boolean confluence$critical;

    @Override
    public void confluence$setCritical(boolean critical){
        confluence$critical = critical;
    }

    @Override
    public boolean confluence$isCritical(){
        return confluence$critical;
    }
}
