package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.confluence.mod.integration.terra_curio.WorkshopLevelAccess;
import org.confluence.terra_curio.common.menu.WorkshopMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WorkshopMenu.class, remap = false)
public abstract class WorkshopMenuMixin {
    @Shadow
    @Final
    private Player player;

    @ModifyExpressionValue(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/util/TCUtils;forConfluence$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object modify(Object original) {
        if (original == ContainerLevelAccess.NULL) {
            WorkshopLevelAccess access = new WorkshopLevelAccess(null, null);
            access.initializeIfNeeded(player);
            return access;
        }
        return original;
    }
}
