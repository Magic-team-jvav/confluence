package org.confluence.mod.mixin.integration.sodium.iris;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import org.confluence.mod.integration.sodium.iris.IrisHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.shaderpack.IdMap", remap = false)
public abstract class IdMapMixin {
    @ModifyReturnValue(method = "parseBlockMap", at = @At("RETURN"))
    private static Int2ObjectLinkedOpenHashMap<List<BlockEntry>> modify(Int2ObjectLinkedOpenHashMap<List<BlockEntry>> original) {
        IrisHelper.modifyBlockProperties(original);
        return original;
    }
}
