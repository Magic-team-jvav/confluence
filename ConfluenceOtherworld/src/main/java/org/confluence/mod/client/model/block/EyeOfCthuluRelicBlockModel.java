package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.EyeOfCthulhuRelicBlock;
import software.bernie.geckolib.model.GeoModel;

public class EyeOfCthuluRelicBlockModel extends GeoModel<EyeOfCthulhuRelicBlock.BEntity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/eye_of_cthulhu_relic_block.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/eye_of_cthulhu_relic_block.png");
    public static final ResourceLocation ANIMATIONS = Confluence.asResource("animations/block/eye_of_cthulhu_relic_block.animation.json");

    @Override
    public ResourceLocation getModelResource(EyeOfCthulhuRelicBlock.BEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EyeOfCthulhuRelicBlock.BEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(EyeOfCthulhuRelicBlock.BEntity animatable) {
        return ANIMATIONS;
    }
}
