package org.confluence.terraentity.integration;

import net.neoforged.fml.ModList;

public class ModLoadPair {
    private final String id;
    private final boolean loaded;

    public ModLoadPair(String key) {
        this.id = key;
        this.loaded = ModList.get().isLoaded(key);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public String getId() {
        return id;
    }
}
