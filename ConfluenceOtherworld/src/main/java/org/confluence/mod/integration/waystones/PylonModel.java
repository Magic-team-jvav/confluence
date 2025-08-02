package org.confluence.mod.integration.waystones;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Function;

public class PylonModel extends GeoModel<PylonBlock.BEntity> {
    private final Function<PylonBlock.BEntity, ResourceLocation[]> function = new Function<>() {
        private final ResourceLocation[][] cache = Util.make(new ResourceLocation[WaystonesHelper.BLOCKS.getEntries().size()][3], arr -> {
            for (DeferredHolder<Block, ? extends Block> entry : WaystonesHelper.BLOCKS.getEntries()) {
                String path = entry.getId().getPath();
                arr[((PylonBlock) entry.get()).id] = new ResourceLocation[]{
                        Confluence.asResource("geo/block/" + path + ".geo.json"),
                        Confluence.asResource("textures/block/" + path + ".png"),
                        Confluence.asResource("animations/block/" + path + ".animation.json")
                };
            }
        });

        @Override
        public ResourceLocation[] apply(PylonBlock.BEntity entity) {
            ResourceLocation[] resources = cache[((PylonBlock) entity.getBlockState().getBlock()).id];
            return GeckoLibCache.getBakedModels().containsKey(resources[0]) ? resources : cache[0];
        }
    };

    @Override
    public ResourceLocation getModelResource(PylonBlock.BEntity animatable) {
        return function.apply(animatable)[0];
    }

    @Override
    public ResourceLocation getTextureResource(PylonBlock.BEntity animatable) {
        return function.apply(animatable)[1];
    }

    @Override
    public ResourceLocation getAnimationResource(PylonBlock.BEntity animatable) {
        return function.apply(animatable)[2];
    }
}
