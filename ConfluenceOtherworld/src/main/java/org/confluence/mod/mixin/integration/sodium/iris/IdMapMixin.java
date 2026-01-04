package org.confluence.mod.mixin.integration.sodium.iris;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
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

    @ModifyReturnValue(method = "parseItemIdMap", at = @At("RETURN"))
    private static Object2IntMap<NamespacedId> modify(Object2IntMap<NamespacedId> original) {
        Object2IntMap<NamespacedId> map = new Object2IntOpenHashMap<>(original);
        IrisHelper.modifyItemProperties(map);
        return Object2IntMaps.unmodifiable(map);
    }
}
