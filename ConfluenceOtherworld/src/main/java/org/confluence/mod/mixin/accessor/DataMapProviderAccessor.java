package org.confluence.mod.mixin.accessor;

import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Pseudo
@Mixin(targets = "net.neoforged.neoforge.common.data.DataMapProvider", remap = false)
public interface DataMapProviderAccessor {
    @Accessor
    Map<DataMapType<?, ?>, DataMapProvider.Builder<?, ?>> getBuilders();
}
