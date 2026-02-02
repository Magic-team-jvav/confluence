package org.confluence.terra_furniture.api.client.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.confluence.terra_furniture.TerraFurniture;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 简化Geo方块实体模型提供的通用类，将会缓存对应的模型、贴图与动画。
 */
public class CacheBlockModel<T extends BlockEntity & GeoBlockEntity> extends GeoModel<T> {
    private final Function<String, ResourceLocation> pathApplier;
    protected static final ConcurrentHashMap<String, ResourceLocation> MODEL = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, ResourceLocation> TEXTURE = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, ResourceLocation> ANIMATION = new ConcurrentHashMap<>();
    /**
     * 创建一个缓存的Geo模型
     * @apiNote 使用该类的方块id必须对应一系列存在于由提供的位置处理器所处理的对应位置的文件:<p>
     * "geo/block/[registry_id].geo.json"<p>
     * "textures/block/[registry_id].png"<p>
     * "animations/block/[registry_id].animation.json"
     * @param pathApplier 位置处理器，负责处理目录字符串，并返回一个{@link ResourceLocation}<p>
     *     比如...
     * <pre>{@code
     *     new CacheBlockModel(TerraFurniture::asResource);
     * }</pre>
     */
    public CacheBlockModel(Function<String, ResourceLocation> pathApplier) {
        this.pathApplier = pathApplier;
    }

    public CacheBlockModel() {
        this.pathApplier = TerraFurniture::asResource;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return MODEL.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(animatable.getBlockState().getBlock()).getPath(), block -> pathApplier.apply("geo/block/" + block + ".geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return TEXTURE.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(animatable.getBlockState().getBlock()).getPath(), block -> pathApplier.apply("textures/block/" + block + ".png"));
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ANIMATION.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(animatable.getBlockState().getBlock()).getPath(), block -> pathApplier.apply("animations/block/" + block + ".animation.json"));
    }
}
