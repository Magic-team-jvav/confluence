package org.confluence.mod.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.client.model.entity.TargetDummyModel;
import org.confluence.mod.common.entity.TargetDummyEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TargetDummyRenderer extends GeoEntityRenderer<TargetDummyEntity> {
    public TargetDummyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TargetDummyModel());
    }
}
