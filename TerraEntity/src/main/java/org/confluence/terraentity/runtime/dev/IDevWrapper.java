package org.confluence.terraentity.runtime.dev;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * 使用这些方法包装的Supplier和Runnable，只有在开发环境下才会执行，否则直接返回null或不执行。
 */
public interface IDevWrapper {

    @DevOnly
    default <T> @Nullable T supplyDevProxy(Supplier<T> supplier){
        return supplier.get();
    }

    @DevOnly
    default void runDevProxy(Runnable runnable){
        runnable.run();
    }

    default boolean isDevMode(){
        return DevOnlyInterceptor.isDevMode;
    }

}
