package org.confluence.lib.mixed;

@SuppressWarnings("unchecked")
public interface SelfGetter<T> {
    default T confluence$self(){
        return (T) this;
    }
}
