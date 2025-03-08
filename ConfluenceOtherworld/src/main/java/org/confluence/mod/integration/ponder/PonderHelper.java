package org.confluence.mod.integration.ponder;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.fml.ModList;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.integration.jade.PonderComponentProvider;
import snownee.jade.api.IWailaClientRegistration;

public class PonderHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("ponder");

    public static void registerPlugin() {
        if (IS_LOADED) {
            try {
                PonderIndex.addPlugin(ModPonderPlugin.class.getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {}
        }
    }

    public static void registerComponent(IWailaClientRegistration registration) {
        if (IS_LOADED) {
            registration.registerBlockComponent(PonderComponentProvider.INSTANCE, AltarBlock.class);
        }
    }
}
