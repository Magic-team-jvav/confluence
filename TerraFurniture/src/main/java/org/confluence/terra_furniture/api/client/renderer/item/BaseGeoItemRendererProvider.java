package org.confluence.terra_furniture.api.client.renderer.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * 简单Geo模型物品渲染提供器，提供一个{@link BaseGeoItemRenderer}
 * @apiNote 可以重写process()方法以对模型操作
 */
public class BaseGeoItemRendererProvider<T extends Item & GeoAnimatable> implements GeoRenderProvider {
    protected final GeoModel<T> model;
    protected GeoItemRenderer<T> renderer;
    private final boolean isNegative;

    public BaseGeoItemRendererProvider(GeoModel<T> model, boolean isNegative) {
        this.model = model;
        this.isNegative = isNegative;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        if (renderer == null) {
            this.renderer = new BaseGeoItemRenderer<>(model, this::process, isNegative);
        }
        return renderer;
    }
    public void process(BakedGeoModel model) {}
}
