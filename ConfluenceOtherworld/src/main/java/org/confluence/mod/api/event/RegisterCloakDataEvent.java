package org.confluence.mod.api.event;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.BiConsumer;

public class RegisterCloakDataEvent extends Event implements IModBusEvent {
    private final BiConsumer<BlockState, BlockState> consumer;

    public RegisterCloakDataEvent(BiConsumer<BlockState, BlockState> consumer) {
        this.consumer = consumer;
    }

    public void register(BlockState source, BlockState target) {
        consumer.accept(source, target);
    }
}
