package org.confluence.terra_furniture.api.client.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import org.confluence.terra_furniture.TerraFurniture;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Function;

/**
 * 简化Geo物品渲染中提供方块实体模型的通用类，将会向{@link CacheBlockModel}缓存对应的模型、贴图与动画。
 */
public class CacheItemRefBlockModel<T extends BlockItem & GeoItem> extends GeoModel<T> {
    private final Function<String, ResourceLocation> pathApplier;
    /**
     * 创建一个缓存的Geo模型
     * @apiNote 使用该类的物品(一般逻辑上只能是方块物品)id必须对应一系列存在于由提供的位置处理器所处理的对应位置的文件:<p>
     * "geo/block/[registry_id].geo.json"<p>
     * "textures/block/[registry_id].png"<p>
     * "animations/block/[registry_id].animation.json"
     * @param pathApplier 位置处理器，负责处理目录字符串，并返回一个{@link ResourceLocation}
     * @implNote 警告：该类不建议用作独立物品的填充模型，因为将物品模型放到方块模型的位置是违背常理的
     */
    public CacheItemRefBlockModel(Function<String, ResourceLocation> pathApplier) {
        this.pathApplier = pathApplier;
    }
    public CacheItemRefBlockModel() {
        this.pathApplier = TerraFurniture::asResource;
    }
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return CacheBlockModel.MODEL.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(animatable.getBlock()).getPath(), block -> pathApplier.apply("geo/block/" + block + ".geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return CacheBlockModel.TEXTURE.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(animatable.getBlock()).getPath(), block -> pathApplier.apply("textures/block/" + block + ".png"));
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return CacheBlockModel.ANIMATION.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(animatable.getBlock()).getPath(), block -> pathApplier.apply("animations/block/" + block + ".animation.json"));
    }
}
