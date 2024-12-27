package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.confluence.mod.integration.terra_curio.WorkshopLevelAccess;
import org.confluence.terra_curio.common.block.WorkshopBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WorkshopBlock.class, remap = false)
public abstract class WorkshopBlockMixin {
    @WrapOperation(method = "lambda$getMenuProvider$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;create(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/inventory/ContainerLevelAccess;"))
    private static ContainerLevelAccess modify(Level level, BlockPos pos, Operation<ContainerLevelAccess> original) {
        return new WorkshopLevelAccess(level, pos);
    }
}
