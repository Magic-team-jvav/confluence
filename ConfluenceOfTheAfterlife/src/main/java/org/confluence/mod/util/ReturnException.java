package org.confluence.mod.util;

/** 用来在lambda循环中跳出循环 */
public class ReturnException extends RuntimeException{
    private final Object value;

    public ReturnException(Object value){
        this.value = value;
    }

    public Object getValue(){
        return value;
    }
}
