package org.confluence.mod.mixin.world.entity.vehicle;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import org.confluence.mod.mixed.IAbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin implements IAbstractMinecart {
    @Shadow
    protected abstract Item getDropItem();

    @Override
    public Item confluence$getDropItem() {
        return getDropItem();
    }

//    @Inject(method = "createMinecart", at = @At("HEAD"), cancellable = true)
//    private static void replaceMinecart(Level level, double x, double y, double z, AbstractMinecart.Type type, CallbackInfoReturnable<AbstractMinecart> cir) {
//        if (stack.getItem() instanceof BaseMinecartItem baseMinecartItem) {
//            AbstractMinecart minecart = baseMinecartItem.createMinecart(level, x, y, z, type, stack, player);
//            if (minecart != null) cir.setReturnValue(minecart);
//        }
//    }
}
