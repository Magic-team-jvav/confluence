package org.confluence.mod.integration.jei;

import net.minecraft.client.KeyMapping;
import org.confluence.lib.mixin.accessor.KeyMappingAccessor;
import org.confluence.lib.util.LibUtils;

public class JeiHelper {
    public static final boolean IS_LOADED = LibUtils.isModLoaded("jei");
    public static final KeyMapping showUses = KeyMappingAccessor.getALL().get("key.jei.showUses");
}
