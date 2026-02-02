package org.confluence.terra_furniture.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.confluence.terra_furniture.api.client.model.CacheItemRefBlockModel;
import org.confluence.terra_furniture.api.client.renderer.item.BaseGeoItemRendererProvider;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class SimpleGeoRenderedItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final boolean isNegative;
    public SimpleGeoRenderedItem(Block block, Properties properties, boolean isNegative) {
        super(block, properties);
        this.isNegative = isNegative;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new BaseGeoItemRendererProvider<>(new CacheItemRefBlockModel<>(), isNegative));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, state -> state.setAndContinue(RawAnimation.begin().thenLoop("default"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
