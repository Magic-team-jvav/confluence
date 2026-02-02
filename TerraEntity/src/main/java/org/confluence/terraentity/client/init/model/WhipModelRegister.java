package org.confluence.terraentity.client.init.model;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 鞭子模型注册管理类
 */
public class WhipModelRegister extends AbstractModelRegister<Item> {

    Map<Item, ResourceLocation> additionalModels;

    static Codec<Map<Item, ResourceLocation>> CODEC = Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), ResourceLocation.CODEC);

    private static WhipModelRegister instance;
    public static WhipModelRegister getInstance() {
        if(instance == null) {
            instance = new WhipModelRegister();
        }
        return instance;
    }

    public @Nullable ModelResourceLocation process(ResourceLocation location){
        String[] splits = location.getPath().split("[./]");
        int len = splits.length;
        String name = splits[len - 2];
        if(location.getPath().endsWith("config.json")){
            ResourceManager provider = Minecraft.getInstance().getResourceManager();

            try{
                Reader reader = provider.openAsReader(location);
                JsonObject jsonobject = GsonHelper.parse(reader);
                this.additionalModels = CODEC.decode(JsonOps.INSTANCE, jsonobject).result().get().getFirst();
            }catch (IOException e){
                TerraEntity.LOGGER.error("Can't open config file: {}", location);
            }catch (NoSuchElementException e){
                TerraEntity.LOGGER.error("Failed to load model config: {}", location);
            }
            return null;
        }

        for(DeferredHolder<Item, ? extends Item> item : TEWhipItems.ITEMS.getEntries()){
            String itemName = item.getId().toString();
            if(itemName.equals(location.getNamespace()+ ":" + name)){
                ResourceLocation modelLocation = TerraEntity.fromSpaceAndPath(location.getNamespace(), location.getPath().substring(7).replace(".json", ""));
                ModelResourceLocation modelResourceLocation = ModelResourceLocation.standalone(modelLocation);
//                event.register(modelResourceLocation);
                put(item.get(), modelResourceLocation);
                return modelResourceLocation;
            }
        }
        return null;
    }

    public void register(ModelEvent.RegisterAdditional event){
        super.register(event);
        this.additionalModels.forEach((item, location) -> {
            ResourceLocation modelLocation = TerraEntity.fromSpaceAndPath(location.getNamespace(), location.getPath().substring(7).replace(".json", ""));
            ModelResourceLocation modelResourceLocation = ModelResourceLocation.standalone(modelLocation);
//                event.register(modelResourceLocation);
            put(item, modelResourceLocation);
        });

    }

    @Override
    protected void outputLog(ResourceLocation location){
        if(!location.getPath().endsWith("config.json")) {
            super.outputLog(location);
        }
    }

    @Override
    protected String getFolder() {
        return "item/whip";
    }

}
