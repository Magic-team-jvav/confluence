package org.confluence.mod.integration.terra_entity;

import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.world.BiomeModifier;
import org.confluence.mod.Confluence;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TERemoval {

    // 替换配方
    public static void processRecipes(Map<ResourceLocation, JsonElement> objectMap, ResourceManager resourceManager){
        List<ResourceLocation> keysToRemove = new ArrayList<>();
        objectMap.forEach((r, j) -> {
            if (r.getNamespace().equals("terra_entity")) {
                keysToRemove.add(r);
            }
        });
        keysToRemove.forEach(objectMap::remove);
    }

    // 禁用生物生成
    public static List<BiomeModifier> processSpawners(List<BiomeModifier> original, Registry<BiomeModifier> registry) {
        try{
            var new1 = registry.entrySet().stream().filter(e->!e.getKey().location().toString().matches("^terra_entity:mob_spawner/.*")).map(Map.Entry::getValue).toList();
            return new1;
        }catch (Exception e){
            return original;
        }
    }

    // 替换战利品表
    public static Reader processLootTables(Reader reader, ResourceManager resourceManager, Map.Entry<ResourceLocation, Resource> entry) {
        if(entry.getKey().getNamespace().equals("terra_entity")){
            if(entry.getKey().getPath().startsWith("loot_table/entities/")){
                var sp = entry.getKey().getPath().split("/",3);
                try {
                    var rl = Confluence.asResource("loot_table/with/terra_entity/" + sp[2]);
                    var op = resourceManager.getResource(rl).get().openAsReader();
                    return op;
                }catch (Exception e){
                    return reader;
                }
            }
        }

        return reader;
    }
}
