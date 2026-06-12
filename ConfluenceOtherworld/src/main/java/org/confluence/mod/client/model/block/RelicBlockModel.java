package org.confluence.mod.client.model.block;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.RelicBlock;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import software.bernie.geckolib.model.GeoModel;

import java.util.IdentityHashMap;
import java.util.Map;

public class RelicBlockModel extends GeoModel<RelicBlock.BEntity> {
    public static final Map<Block, ResourceLocation[]> CACHE = Util.make(new IdentityHashMap<>(), map -> {
        for (RegistryObject block : DecorativeBlocks.RELIC_BLOCKS) {
            String path = block.getId().getPath();
            map.put(block.get(), new ResourceLocation[]{
                    Confluence.asResource("geo/block/" + path + ".geo.json"),
                    Confluence.asResource("textures/block/" + path + ".png"),
                    Confluence.asResource("animations/block/" + path + ".animation.json")
            });
        }
    });

    private static ResourceLocation[] getLocations(RelicBlock.BEntity animatable) {
        return CACHE.get(animatable.getBlockState().getBlock());
    }

    @Override
    public ResourceLocation getModelResource(RelicBlock.BEntity animatable) {
        return getLocations(animatable)[0];
    }

    @Override
    public ResourceLocation getTextureResource(RelicBlock.BEntity animatable) {
        return getLocations(animatable)[1];
    }

    @Override
    public ResourceLocation getAnimationResource(RelicBlock.BEntity animatable) {
        return getLocations(animatable)[2];
    }
}
