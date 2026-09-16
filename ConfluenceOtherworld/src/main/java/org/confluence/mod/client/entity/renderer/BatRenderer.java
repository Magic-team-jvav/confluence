package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.confluence.mod.common.entity.monster.CaveBat;

public class BatRenderer extends GeoNormalRenderer<CaveBat> {
    private final boolean luminous;

    public BatRenderer(EntityRendererProvider.Context context, ResourceLocation model, boolean luminous) {
        super(context, new GeoNormalModel<CaveBat>(model, false) {
            @Override
            public ResourceLocation getAnimationResource(CaveBat bat) {
                // Bat.bbmodel 的共用骨架已有完整飞行动画，不使用空的 idle 占位动画。
                return Confluence.asResource("animations/entity/cave_bat.animation.json");
            }
        }, true, 1.0F, 0.0625F);
        this.luminous = luminous;
    }

    @Override
    protected int getBlockLightLevel(CaveBat bat, BlockPos pos) {
        return luminous ? 15 : super.getBlockLightLevel(bat, pos);
    }
}
