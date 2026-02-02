package org.confluence.terraentity.client.init.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.confluence.terraentity.TerraEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽象模型注册管理类
 */
public abstract class AbstractModelRegister<T>{


    protected final Map<T, ModelResourceLocation> whipModelMap = new HashMap<>();

    public ModelResourceLocation getModelResourceLocation(T item) {
        return whipModelMap.get(item);
    }

    protected void put(T whip, ModelResourceLocation model){
        whipModelMap.put(whip, model);
    }

    protected abstract @Nullable ModelResourceLocation process(ResourceLocation location);

    protected abstract String getFolder();

    public void register(ModelEvent.RegisterAdditional event){
        ResourceManager provider = Minecraft.getInstance().getResourceManager();
        provider.listResources("models/" + getFolder(), s -> s.getPath().endsWith(".json")).forEach((location, resource) -> {
            var f = process(location);
            if(f!= null){
                event.register(f);
            }else{
                outputLog(location);
            }
        });
    }

    protected void outputLog(ResourceLocation location){
        TerraEntity.LOGGER.warn("Failed to load model: {}", location);
    }
}
