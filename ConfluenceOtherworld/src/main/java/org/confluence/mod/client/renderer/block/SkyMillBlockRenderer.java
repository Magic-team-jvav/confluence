package org.confluence.mod.client.renderer.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.crafting.SkyMillBlock;
import org.confluence.mod.util.ClientUtils;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SkyMillBlockRenderer extends GeoBlockRenderer<SkyMillBlock.BEntity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/sky_mill.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/sky_mill.png");
    public static final ResourceLocation ANIMATION = Confluence.asResource("animations/block/sky_mill.animation.json");

    public SkyMillBlockRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(SkyMillBlock.BEntity animatable) {
                return MODEL;
            }

            @Override
            public ResourceLocation getTextureResource(SkyMillBlock.BEntity animatable) {
                return TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(SkyMillBlock.BEntity animatable) {
                return ANIMATION;
            }
        });
    }

    @Override
    public AABB getRenderBoundingBox(SkyMillBlock.BEntity blockEntity) {
        return ClientUtils.getRenderBoundingBox3x(blockEntity.getBlockPos());
    }
}
