package org.confluence.mod.integration.jade;

import net.createmod.ponder.foundation.PonderTooltipHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayList;
import java.util.List;

public class PonderComponentProvider implements IBlockComponentProvider {
    public static final PonderComponentProvider INSTANCE = new PonderComponentProvider();
    public static final ResourceLocation UID = Confluence.asResource("jade_ponder_component");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        List<Component> toAdd = new ArrayList<>();
        PonderTooltipHandler.addToTooltip(toAdd, blockAccessor.getPickedResult());
        iTooltip.addAll(toAdd);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
