package org.confluence.terraentity.entity.ai;

/**
 * All bosses should implement this interface
 * <p>
 * 所有boss都应该实现这个接口
 */
public interface Boss {
    default boolean shouldShowMessage(){
        return isMainBody();
    }

    default boolean isMainBody(){
        return true;
    }
}
