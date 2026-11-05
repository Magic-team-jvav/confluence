package org.confluence.mod.client.summoner.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.confluence.mod.common.summoner.projectile.Zenith;

public final class ZenithSwordModels {

    private ZenithSwordModels() {
    }

    public static ResourceLocation modelId(Zenith.RenderType type) {
        return type.getTexture();
    }

    public static void register(ModelEvent.RegisterAdditional event) {
        for (Zenith.RenderType type : Zenith.RenderType.values()) {
            event.register(modelId(type));
        }
    }
}
