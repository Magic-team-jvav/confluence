package org.confluence.mod.common.block.common;

import net.minecraftforge.registries.RegistryObject;

public class StoneBlockSet {
    private final Builder builder;

    public StoneBlockSet(Builder builder) {
        this.builder = builder;
    }

    public static class Builder {
        private RegistryObject bricks;
        private RegistryObject stair;
        private RegistryObject slab;
    }
}
