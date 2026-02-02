package org.confluence.lib.event;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public abstract class NameFixRegisterEvent extends Event implements IModBusEvent {
    private final ImmutableMap.Builder<String, String> builder;

    public NameFixRegisterEvent(ImmutableMap.Builder<String, String> builder) {
        this.builder = builder;
    }

    public NameFixRegisterEvent register(String source, String target) {
        builder.put(source, target);
        return this;
    }

    public NameFixRegisterEvent register(ResourceLocation source, ResourceLocation target) {
        builder.put(source.toString(), target.toString());
        return this;
    }

    public NameFixRegisterEvent register(String source, ResourceLocation target) {
        builder.put(source, target.toString());
        return this;
    }

    public NameFixRegisterEvent register(ResourceLocation source, String target) {
        builder.put(source.toString(), target);
        return this;
    }

    public static class BlockWithItem extends NameFixRegisterEvent {
        public BlockWithItem(ImmutableMap.Builder<String, String> builder) {
            super(builder);
        }
    }

    public static class Block extends NameFixRegisterEvent {
        public Block(ImmutableMap.Builder<String, String> builder) {
            super(builder);
        }
    }

    public static class Item extends NameFixRegisterEvent {
        public Item(ImmutableMap.Builder<String, String> builder) {
            super(builder);
        }
    }

    public static class Biome extends NameFixRegisterEvent {
        public Biome(ImmutableMap.Builder<String, String> builder) {
            super(builder);
        }
    }
}
