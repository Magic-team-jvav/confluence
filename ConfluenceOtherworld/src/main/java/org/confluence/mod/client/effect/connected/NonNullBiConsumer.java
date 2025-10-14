package org.confluence.mod.client.effect.connected;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface NonNullBiConsumer<T, U> extends BiConsumer<T, U> {
    
    @Override
    void accept(T t, U u);

    static <T, U> NonNullBiConsumer<T, U> noop() {
        return (t, u) -> {};
    }
}
