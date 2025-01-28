package org.confluence.mod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @Inject(method = "fillFromWorld",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;addToLists(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V"))
    private void fillBrushData(Level level, BlockPos pos, Vec3i size, boolean withEntities, Block toIgnore, CallbackInfo ci, @Local(ordinal = 5) BlockPos blockPos) {

    }
}
