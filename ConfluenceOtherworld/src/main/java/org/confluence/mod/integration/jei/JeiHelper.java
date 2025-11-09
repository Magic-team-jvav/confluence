package org.confluence.mod.integration.jei;

import net.minecraft.client.KeyMapping;
import net.neoforged.fml.ModList;
import org.confluence.mod.mixin.accessor.KeyMappingAccessor;

public class JeiHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("jei");
    public static final KeyMapping showUses = KeyMappingAccessor.getALL().get("key.jei.showUses");
}
