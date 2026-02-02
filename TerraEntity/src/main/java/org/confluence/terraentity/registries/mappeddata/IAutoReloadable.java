package org.confluence.terraentity.registries.mappeddata;

import java.util.function.Supplier;

public interface IAutoReloadable<V> {

    void onReload(V value);

    Supplier<V> defaultValue();



}
