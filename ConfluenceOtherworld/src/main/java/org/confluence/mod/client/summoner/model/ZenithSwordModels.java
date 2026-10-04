package org.confluence.mod.client.summoner.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderer;
import org.confluence.mod.common.summoner.projectile.Zenith;

public final class ZenithSwordModels {

    private ZenithSwordModels() {
    }

    private static ResourceLocation modelId(Zenith.RenderType type) {
        return Confluence.asResource("projectile/zenith/" + type.textureName());
    }

    public static void register(ModelEvent.RegisterAdditional event) {
        for (Zenith.RenderType type : Zenith.RenderType.values()) {
            event.register(JsonModelRenderer.resourcePath(modelId(type)));
        }
    }
}
