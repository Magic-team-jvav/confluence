package com.xiaohunao.xhn_lib.common.events;

import com.xiaohunao.xhn_lib.common.init.FlexibleRegister;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * 灵活注册事件
 * 当FlexibleRegister注册内容时触发此事件
 * 可以用于监听和修改注册过程
 * @param <T> 注册内容的类型
 */
public class FlexibleRegisteredEvent<T> extends Event implements ICancellableEvent {
    private final FlexibleRegister<T> register;
    private final ResourceLocation id;
    private final T value;
    private final boolean isDynamic;
    private boolean cancelled = false;
    
    /**
     * 创建一个新的灵活注册事件
     * @param register 触发事件的注册表
     * @param id 注册的ID
     * @param value 注册的值
     * @param isDynamic 是否是动态注册
     */
    public FlexibleRegisteredEvent(FlexibleRegister<T> register, ResourceLocation id, T value, boolean isDynamic) {
        this.register = register;
        this.id = id;
        this.value = value;
        this.isDynamic = isDynamic;
    }
    
    /**
     * 获取触发事件的注册表
     * @return 注册表
     */
    public FlexibleRegister<T> getRegister() {
        return register;
    }
    
    /**
     * 获取注册的ID
     * @return 注册ID
     */
    public ResourceLocation getId() {
        return id;
    }
    
    /**
     * 获取注册的值
     * @return 注册值
     */
    public T getValue() {
        return value;
    }
    
    /**
     * 检查是否是动态注册
     * @return 如果是动态注册则返回true
     */
    public boolean isDynamic() {
        return isDynamic;
    }
    
    /**
     * 检查事件是否已取消
     * @return 如果事件已取消则返回true
     */
    @Override
    public boolean isCanceled() {
        return cancelled;
    }
    
    /**
     * 设置事件的取消状态
     * @param cancel 是否取消事件
     */
    @Override
    public void setCanceled(boolean cancel) {
        this.cancelled = cancel;
    }
}
