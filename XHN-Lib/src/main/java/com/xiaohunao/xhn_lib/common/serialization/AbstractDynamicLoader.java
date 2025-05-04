package com.xiaohunao.xhn_lib.common.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractDynamicLoader extends SimpleJsonResourceReloadListener {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDynamicLoader.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final String folderName;

    public AbstractDynamicLoader(String folderName) {
        super(GSON, folderName);
        this.folderName = folderName;
    }
    
    @Override
    protected abstract void apply(@NotNull Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler);
    

    public boolean shouldRegisterOnClient() {
        return false;
    }

}
