package org.confluence.terraentity.config;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import net.minecraft.util.GsonHelper;
import net.neoforged.fml.loading.FMLPaths;
import org.confluence.terraentity.TerraEntity;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;


public abstract class AbstractJsonConfig {
    private String path;
    private JsonObject json;

    /**
     * Server
     */
    protected AbstractJsonConfig(String name){
        this.path = name;
        this.json = new JsonObject();
    }

    /**
     * Client
     */
    public AbstractJsonConfig(JsonObject config){
        this.json = config;
    }

    protected abstract JsonObject defaultConfig();

    protected abstract void initConfig(JsonObject config);

    public JsonObject rawConfig(){
        return json;
    }
    public String getPath() {
        return path;
    }

    /**
     * Server
     */
    public void loadConfig(){
        if(json == null || path == null) return;
        Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve(TerraEntity.MODID);
        Path configFile = CONFIG_PATH.resolve(path +".json");
        File file = configFile.toFile();
        try {
            Reader reader;
            if (!file.exists()) {
                CONFIG_PATH.toFile().mkdirs();
                file.createNewFile();
                reader = new java.io.FileReader(file);

                json = defaultConfig();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Writer writer = new java.io.FileWriter(file);
                writer.write(gson.toJson(json));
                writer.close();
            }else{
                reader = new java.io.FileReader(file);
                json = GsonHelper.parse(reader);
            }

            initConfig(json);

            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static JsonElement parseCodec(DataResult<?> result){
        return JsonParser.parseString(new Gson().toJson(result.result().get()));
    }

}
