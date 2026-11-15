package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.block.functional.BossConjuringBlock;
import software.bernie.geckolib.model.GeoModel;

/// 三个召唤装置共用一个方块实体类型，模型/贴图/动画按方块所属的变体解析。
public class BossConjuringBlockModel extends GeoModel<BossConjuringBlock.BEntity> {
    private static BossConjuringBlock.Variant variant(BossConjuringBlock.BEntity entity) {
        return ((BossConjuringBlock) entity.getBlockState().getBlock()).getVariant();
    }

    @Override
    public ResourceLocation getModelResource(BossConjuringBlock.BEntity entity) {
        return variant(entity).geoModel();
    }

    @Override
    public ResourceLocation getTextureResource(BossConjuringBlock.BEntity entity) {
        return variant(entity).geoTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(BossConjuringBlock.BEntity entity) {
        return variant(entity).geoAnimation();
    }
}
