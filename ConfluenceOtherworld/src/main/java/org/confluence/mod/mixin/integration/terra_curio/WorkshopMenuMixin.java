package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.confluence.mod.common.recipe.EnvironmentRecipeInput;
import org.confluence.mod.integration.terra_curio.WorkshopLevelAccess;
import org.confluence.terra_curio.common.menu.WorkshopMenu;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WorkshopMenu.class, remap = false)
public abstract class WorkshopMenuMixin implements SelfGetter<WorkshopMenu> {
    @Shadow
    @Final
    private Player player;

    @Shadow
    @Final
    private ContainerLevelAccess access;

    @ModifyExpressionValue(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/util/TCUtils;forConfluence$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private Object modifyAccess(Object original) {
        if (original == ContainerLevelAccess.NULL) {
            WorkshopLevelAccess access = new WorkshopLevelAccess(null, null);
            access.initializeIfNeeded(player);
            return access;
        }
        return original;
    }

    @ModifyExpressionValue(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At(value = "INVOKE", target = "Lorg/confluence/terra_curio/util/TCUtils;forConfluence$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    private Object modifyRecipeInput(Object original) {
        return new EnvironmentRecipeInput(self(), 12, (WorkshopLevelAccess) access);
    }
}
