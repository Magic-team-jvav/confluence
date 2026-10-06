package org.confluence.mod.client.summoner.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.model.json.JsonModelRenderer;
import org.confluence.mod.common.summoner.projectile.Zenith;

public final class ZenithSwordModels {

    private ZenithSwordModels() {
    }

    public static ResourceLocation modelId(Zenith.RenderType type) {
        String path = type.getTexture().getPath();
        return Confluence.asResource("projectile/zenith/" + path.substring(path.lastIndexOf('/') + 1));
    }

    public static void register(ModelEvent.RegisterAdditional event) {
        for (Zenith.RenderType type : Zenith.RenderType.values()) {
            event.register(JsonModelRenderer.resourcePath(modelId(type)));
        }
    }
}
