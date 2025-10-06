package org.confluence.mod.mixin.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.item.common.BaseMinecartItem;
import org.confluence.mod.mixed.IAbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends VehicleEntity implements IAbstractMinecart {
    public AbstractMinecartMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Item confluence$getDropItem() {
        return getDropItem();
    }

    @Inject(method = "createMinecart", at = @At("HEAD"), cancellable = true)
    private static void replaceMinecart(ServerLevel level, double x, double y, double z, AbstractMinecart.Type type, ItemStack stack, Player player, CallbackInfoReturnable<AbstractMinecart> cir) {
        if (stack.getItem() instanceof BaseMinecartItem baseMinecartItem) {
            AbstractMinecart minecart = baseMinecartItem.createMinecart(level, x, y, z, type, stack, player);
            if (minecart != null) cir.setReturnValue(minecart);
        }
    }
}
