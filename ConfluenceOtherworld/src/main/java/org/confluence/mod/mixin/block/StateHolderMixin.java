package org.confluence.mod.mixin.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StateHolder.class)
public abstract class StateHolderMixin<O, S> implements SelfGetter<StateHolder<O, S>> {
    @Inject(method = "getValue", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalArgumentException;<init>(Ljava/lang/String;)V"), cancellable = true)
    private <T extends Comparable<T>> void test(Property<T> property, CallbackInfoReturnable<T> cir) {
        if (confluence$self() instanceof BlockState source) {
            BlockState target = GlobalCloakData.INSTANCE.getTarget(source);
            if (target != source) {
                cir.setReturnValue(target.getValue(property));
            }
        }
    }
}
