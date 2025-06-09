package org.confluence.mod.mixin.integration.waystones;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "net.blay09.mods.waystones.block.entity.WaystoneBlockEntity", remap = false)
public abstract class WaystoneBlockEntityMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/blay09/mods/waystones/block/entity/WaystoneBlockEntityBase;<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"), index = 0)
    private static BlockEntityType<?> replace(BlockEntityType<?> blockEntityType, @Local(argsOnly = true) BlockState blockState) {
        if (BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).getNamespace().equals(Confluence.MODID)) {
            return WaystonesHelper.PYLON_ENTITY.get();
        }
        return blockEntityType;
    }
}
