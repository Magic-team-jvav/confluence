package org.confluence.mod.client.model.block;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.BasePylonBlock;
import org.confluence.mod.common.init.block.PylonBlocks;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Function;

public class BasePylonModel extends GeoModel<BasePylonBlock.BEntity> {
    private final Function<BasePylonBlock.BEntity, ResourceLocation[]> function = new Function<>() {
        private final ResourceLocation[][] cache = Util.make(new ResourceLocation[PylonBlocks.BLOCKS.getEntries().size()][3], arr -> {
            for (DeferredHolder<Block, ? extends Block> entry : PylonBlocks.BLOCKS.getEntries()) {
                String path = entry.getId().getPath();
                arr[((BasePylonBlock) entry.get()).id] = new ResourceLocation[]{
                        Confluence.asResource("geo/block/" + path + ".geo.json"),
                        Confluence.asResource("textures/block/" + path + ".png"),
                        Confluence.asResource("animations/block/" + path + ".animation.json")
                };
            }
        });

        @Override
        public ResourceLocation[] apply(BasePylonBlock.BEntity entity) {
            ResourceLocation[] resources = cache[((BasePylonBlock) entity.getBlockState().getBlock()).id];
            return GeckoLibCache.getBakedModels().containsKey(resources[0]) ? resources : cache[0];
        }
    };

    @Override
    public ResourceLocation getModelResource(BasePylonBlock.BEntity animatable) {
        return function.apply(animatable)[0];
    }

    @Override
    public ResourceLocation getTextureResource(BasePylonBlock.BEntity animatable) {
        return function.apply(animatable)[1];
    }

    @Override
    public ResourceLocation getAnimationResource(BasePylonBlock.BEntity animatable) {
        return function.apply(animatable)[2];
    }
}
