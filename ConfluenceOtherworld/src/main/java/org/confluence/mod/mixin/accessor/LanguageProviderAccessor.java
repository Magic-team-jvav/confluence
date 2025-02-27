package org.confluence.mod.mixin.accessor;

import net.neoforged.neoforge.common.data.LanguageProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = LanguageProvider.class, remap = false)
public interface LanguageProviderAccessor {
    @Accessor
    Map<String, String> getData();
}
