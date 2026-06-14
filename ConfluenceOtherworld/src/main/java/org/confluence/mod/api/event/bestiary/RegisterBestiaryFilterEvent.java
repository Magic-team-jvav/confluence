package org.confluence.mod.api.event.bestiary;

import com.mojang.datafixers.util.Function6;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.confluence.mod.client.handler.bestiary.FilterEntry;

public class RegisterBestiaryFilterEvent extends Event implements IModBusEvent {
    private final Function6<String, Integer, Integer, Integer, Integer, Integer, FilterEntry> register;

    public RegisterBestiaryFilterEvent(Function6<String, Integer, Integer, Integer, Integer, Integer, FilterEntry> register) {
        this.register = register;
    }

    public void register(String name, int order, int u, int v, int w, int h) {
        register.apply(name, order, u, v, w, h);
    }
}
