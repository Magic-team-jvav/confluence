package org.confluence.mod.integration.ponder;

import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ModPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return Confluence.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        Confluence.LOGGER.info("Hi From Confluence Team");
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
    }
}
