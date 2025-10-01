package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.KingSlimeRelicBlock;
import software.bernie.geckolib.model.GeoModel;

public class KingSlimeRelicBlockModel extends GeoModel<KingSlimeRelicBlock.BEntity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/king_slime_relic_block.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/king_slime_relic_block.png");
    public static final ResourceLocation ANIMATIONS = Confluence.asResource("animations/block/king_slime_relic_block.animation.json");

    @Override
    public ResourceLocation getModelResource(KingSlimeRelicBlock.BEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(KingSlimeRelicBlock.BEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(KingSlimeRelicBlock.BEntity animatable) {
        return ANIMATIONS;
    }
}
