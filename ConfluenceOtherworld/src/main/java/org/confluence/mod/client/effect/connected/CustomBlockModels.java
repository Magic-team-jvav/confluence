package org.confluence.mod.client.effect.connected;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.confluence.lib.util.consumer.NonNullBiConsumer;
import org.confluence.lib.util.function.NonNullFunction;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class CustomBlockModels {
    private final Multimap<ResourceLocation, NonNullFunction<BakedModel, ? extends BakedModel>> modelFuncs = MultimapBuilder.hashKeys().arrayListValues().build();
    private final Set<Block> registered = new HashSet<>();
    private final Map<Block, NonNullFunction<BakedModel, ? extends BakedModel>> finalModelFuncs = new IdentityHashMap<>();
    private boolean funcsLoaded = false;

    public void register(Block block, NonNullFunction<BakedModel, ? extends BakedModel> func) {
        modelFuncs.put(BuiltInRegistries.BLOCK.getKey(block), func);
        registered.add(block);
    }

    public void forEach(NonNullBiConsumer<Block, NonNullFunction<BakedModel, ? extends BakedModel>> consumer) {
        loadEntriesIfMissing();
        finalModelFuncs.forEach(consumer);
    }

    private void loadEntriesIfMissing() {
        if (!funcsLoaded) {
            ModConnectives.register();
            loadEntries();
            funcsLoaded = true;
        }
    }

    private void loadEntries() {
        finalModelFuncs.clear();
        modelFuncs.asMap().forEach((location, funcList) -> {
            Block block = BuiltInRegistries.BLOCK.get(location);

            NonNullFunction<BakedModel, ? extends BakedModel> finalFunc = null;
            for (NonNullFunction<BakedModel, ? extends BakedModel> func : funcList) {
                if (finalFunc == null) {
                    finalFunc = func;
                } else {
                    finalFunc = finalFunc.andThen(func);
                }
            }

            finalModelFuncs.put(block, finalFunc);
        });
    }

    public boolean containsBlock(Block block) {
        return registered.contains(block);
    }
}
