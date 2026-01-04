package org.confluence.mod.integration.sodium.iris;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.iris.shaderpack.materialmap.BlockEntry;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.OreBlocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class IrisHelper {
    public static <T> void modifyBlockProperties(Int2ObjectMap<List<T>> blockEntriesById) {
        // todo 继续补充
        injectBlockProperties(10272, blockEntriesById, list -> {
            list.add(blockEntry(OreBlocks.CORRUPTION_IRON_ORE.getId()));
            list.add(blockEntry(OreBlocks.SANCTIFICATION_IRON_ORE.getId()));
        });
        injectBlockProperties(10368, blockEntriesById, list -> {
            list.add(blockEntry(OreBlocks.FLESHIFICATION_IRON_ORE.getId()));
        });
    }

    private static <T> void injectBlockProperties(int intId, Int2ObjectMap<List<T>> blockEntriesById, Consumer<List<T>> consumer) {
        List<T> list = Lists.newArrayList(blockEntriesById.getOrDefault(intId, List.of()));
        consumer.accept(list);
        blockEntriesById.put(intId, list);
    }

    @SuppressWarnings("unchecked")
    private static <T> T blockEntry(ResourceLocation id, String... properties) {
        Map<String, String> map;
        if (properties.length > 0) {
            map = new HashMap<>();
            for (String property : properties) {
                String[] kv = property.split("=");
                if (kv.length != 2) continue;
                map.put(kv[0], kv[1]);
            }
        } else {
            map = Collections.emptyMap();
        }
        return (T) new BlockEntry(new NamespacedId(id.getNamespace(), id.getPath()), map);
    }

    public static void register(IEventBus eventBus) {
        DeferredRegister.Items items = DeferredRegister.createItems(Confluence.MODID);
        items.registerItem("test_iris", TestItem::new);
        items.register(eventBus);
    }

    public static void modifyItemProperties(Object2IntMap<NamespacedId> original) {
        original.put(new NamespacedId("confluence", "test_iris"), 44002);
    }
}
