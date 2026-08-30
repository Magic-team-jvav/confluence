package org.confluence.mod.mixin.world.level.levelgen.structure;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.confluence.mod.mixed.IStructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin implements IStructureStart {
    @Shadow
    public abstract BoundingBox getBoundingBox();

    @Override
    public BoundingBox confluence$cachedBoundingBox() {
        return getBoundingBox();
    }

    @Inject(method = "createTag", at = @At(value = "RETURN", ordinal = 0))
    private void saveBoundingBox(CallbackInfoReturnable<CompoundTag> cir) {
        cir.getReturnValue().put("confluence:cached_bounding_box", BoundingBox.CODEC.encodeStart(NbtOps.INSTANCE, getBoundingBox()).getOrThrow());
    }

    @Inject(method = "loadStaticStart", at = @At(value = "RETURN", ordinal = 2))
    private static void readBoundingBox(CallbackInfoReturnable<StructureStart> cir, @Local(argsOnly = true) CompoundTag tag) {
        StructureStart structureStart = cir.getReturnValue();
        BoundingBox.CODEC.parse(NbtOps.INSTANCE, tag.get("confluence:cached_bounding_box")).result()
                .ifPresentOrElse(boundingBox -> structureStart.cachedBoundingBox = boundingBox, structureStart::getBoundingBox);
    }
}
