package org.confluence.mod.client.summon;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

/// 复用巨型蠕虫美术资源绘制星尘龙，不创建头部或体节实体。
final class ClientStardustDragonRenderer extends GeoObjectRenderer<ClientStardustDragonVisual> {
    ClientStardustDragonRenderer() {
        super(new Model());
    }

    @Override
    public long getInstanceId(ClientStardustDragonVisual visual) {
        return visual.id().getMostSignificantBits() ^ visual.id().getLeastSignificantBits();
    }

    private static final class Model extends GeoModel<ClientStardustDragonVisual> {
        @Override
        public ResourceLocation getModelResource(ClientStardustDragonVisual visual) {
            return resource(visual, "geo/entity/", ".geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(ClientStardustDragonVisual visual) {
            return resource(visual, "textures/entity/", ".png");
        }

        @Override
        public @Nullable ResourceLocation getAnimationResource(ClientStardustDragonVisual visual) {
            return null;
        }

        private static ResourceLocation resource(ClientStardustDragonVisual visual, String directory, String suffix) {
            String partSuffix = switch (visual.part()) {
                case HEAD -> "";
                case BODY -> "_segment";
                case TAIL -> "_tail";
            };
            return Confluence.asResource(directory + "giant_worm" + partSuffix + suffix);
        }
    }
}
