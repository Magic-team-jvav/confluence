package org.confluence.terraentity.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.terraentity.mixed.IAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;

@Mixin(AttributeInstance.class)
public abstract class AttributeInstanceMixin implements IAttributeInstance {

    @Shadow protected abstract Collection<AttributeModifier> getModifiersOrEmpty(AttributeModifier.Operation operation);

    @Unique
    private double terraentity$percentage;
    @Unique
    private boolean terraentity$percentage_dirty;

    @Override
    public double terraentity$getPercentage() {
        if(this.terraentity$percentage_dirty){
            this.terraentity$percentage = 1;

            AttributeModifier modifier;
            Iterator<AttributeModifier> iterator;
            for(iterator = this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_MULTIPLIED_BASE).iterator(); iterator.hasNext(); this.terraentity$percentage += modifier.amount()) {
                modifier = iterator.next();
            }

            for(iterator = this.getModifiersOrEmpty(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).iterator(); iterator.hasNext(); this.terraentity$percentage *= (1 + modifier.amount())) {
                modifier = iterator.next();
            }

            this.terraentity$percentage_dirty = false;
        }
        return this.terraentity$percentage;
    }

    @Inject(method = "setDirty", at = @At("RETURN"))
    protected void setDirtyMixin(CallbackInfo ci) {
        this.terraentity$percentage_dirty = true;
    }
}
