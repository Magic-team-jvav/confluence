package org.confluence.mod.client.summoner.renderer.minion;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.SpiderMinion;

public class SpiderRenderer extends AbstractAttachmentEntityGeoRenderer<SpiderMinion> {

    public SpiderRenderer() {
        super(Confluence.asResource("entity/summon/spider"));
    }

    @Override
    public ResourceLocation getTextureLocation(SpiderMinion spider) {
        int variant = spider.variant < 0 ? 0 : Math.floorMod(spider.variant, 3);
        return variant == 1
                ? Confluence.asResource("textures/entity/summon/spider/jumper.png")
                : variant == 2
                ? Confluence.asResource("textures/entity/summon/spider/dangerous.png")
                : Confluence.asResource("textures/entity/summon/spider.png");
    }

    @Override
    protected RenderContext<SpiderMinion> createContext(SpiderMinion spider, float partialTick) {
        return RenderContext.<SpiderMinion>builder()
                .model(new ModelConfig<SpiderMinion>()
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
