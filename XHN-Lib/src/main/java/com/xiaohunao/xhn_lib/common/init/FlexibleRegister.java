package com.xiaohunao.xhn_lib.common.init;

import com.google.gson.JsonElement;
import com.xiaohunao.xhn_lib.common.events.FlexibleRegisteredEvent;
import com.xiaohunao.xhn_lib.common.serialization.AbstractDynamicLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class FlexibleRegister<T> {
    /**
     * 灵活的持有者类，类似于DeferredHolder但支持动态注册
     * @param <R> 注册表类型
     * @param <V> 值类型
     */
    public static class FlexibleHolder<R, V> implements Supplier<V> {
        private final ResourceKey<R> key;
        private final Supplier<V> valueSupplier;
        
        /**
         * 创建一个新的FlexibleHolder
         * @param key 资源键
         * @param valueSupplier 值提供者
         */
        public FlexibleHolder(ResourceKey<R> key, Supplier<V> valueSupplier) {
            this.key = key;
            this.valueSupplier = valueSupplier;
        }
        
        /**
         * 创建一个新的FlexibleHolder
         * @param key 资源键
         * @param value 固定值
         */
        public FlexibleHolder(ResourceKey<R> key, V value) {
            this.key = key;
            this.valueSupplier = () -> value;
        }
        
        /**
         * 获取此持有者的资源键
         * @return 资源键
         */
        public ResourceKey<R> getKey() {
            return key;
        }
        
        /**
         * 获取此持有者的ID
         * @return ID
         */
        public ResourceLocation getId() {
            return key.location();
        }
        
        /**
         * 获取此持有者的值
         * @return 值
         */
        @Override
        public V get() {
            return valueSupplier.get();
        }
        
        @Override
        public String toString() {
            return "FlexibleHolder[" + key.location() + "]";
        }
    }
    // 内部使用原版DeferredRegister处理静态注册
    private final DeferredRegister<T> staticRegister;

    // 存储动态注册的对象
    private final Map<String, FlexibleHolder<T, T>> dynamicEntries = new HashMap<>();
    private final Map<String, FlexibleHolder<T, T>> staticEntries = new HashMap<>();

    // 模组ID
    private final String modId;

    // 注册表实例 - 用于动态注册
    private Registry<T> registry;

    // 动态加载管理器 - 如果支持动态注册则不为null
    private AbstractDynamicLoader dynamicManager = null;

    // 是否支持动态注册
    private boolean supportsDynamic = false;


    public FlexibleRegister(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
        this.staticRegister = DeferredRegister.create(registry, modId);
    }


    /**
     * 将静态注册部分绑定到事件总线 - 在模组初始化期间调用
     * @param eventBus 要绑定的NeoForge事件总线
     */
    public void register(IEventBus eventBus) {
        staticRegister.register(eventBus);
    }



    /**
     * 获取动态内容管理器，用于注册到资源重载监听器中
     * @return 动态内容管理器或null(如果不支持动态注册)
     */
    public SimpleJsonResourceReloadListener getDynamicManager() {
        return dynamicManager;
    }
    
    /**
     * 获取类型安全的动态内容管理器
     * @return 动态内容管理器或null(如果不支持动态注册)
     */
    public AbstractDynamicLoader getTypedDynamicManager() {
        return dynamicManager;
    }
    


    /**
     * 静态注册一个对象 - 必须在模组加载阶段完成
     * @param name 对象的注册名称
     * @param supplier 提供对象实例的Supplier
     * @return 注册对象的引用
     */
    @SuppressWarnings("unchecked")
    public <I extends T> DeferredHolder<T, I> registerStatic(String name, Supplier<I> supplier) {
        // 触发注册事件 - 使用supplier.get()获取实例可能会过早
        // 所以这里我们传入null，事件处理器需要注意这一点
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        FlexibleRegisteredEvent<T> event = new FlexibleRegisteredEvent<>(this, id, null, false);
        NeoForge.EVENT_BUS.post(event);
        
        // 如果事件被取消，则不进行注册
        if (event.isCanceled()) {
            return null;
        }
        
        DeferredHolder<T, I> holder = staticRegister.register(name, supplier);
        
        // 创建并存储FlexibleHolder
        ResourceKey<T> resourceKey = ResourceKey.create(registry.key(), id);
        FlexibleHolder<T, I> flexibleHolder = new FlexibleHolder<>(resourceKey, holder::get);
        staticEntries.put(name, (FlexibleHolder<T, T>) flexibleHolder);
        
        return holder;
    }

    /**
     * 动态注册一个对象 - 可以在游戏运行的任何阶段完成
     * @param name 对象的注册名称
     * @param object 要注册的对象实例
     * @return 注册对象的引用
     * @throws IllegalStateException 如果注册表未准备好或不支持动态注册
     */
    @SuppressWarnings("unchecked")
    public <I extends T> FlexibleHolder<T, I> registerDynamic(String name, I object) {
        if (!supportsDynamic) {
            throw new IllegalStateException("此注册表未配置为支持动态注册");
        }

        if (registry == null) {
            throw new IllegalStateException("尝试在注册表准备完成前进行动态注册");
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        
        // 触发注册事件
        FlexibleRegisteredEvent<T> event = new FlexibleRegisteredEvent<>(this, id, object, true);
        NeoForge.EVENT_BUS.post(event);
        
        // 如果事件被取消，则不进行注册
        if (event.isCanceled()) {
            return null;
        }
        
        Registry.register(registry, id, object);

        // 创建并存储FlexibleHolder
        ResourceKey<T> resourceKey = ResourceKey.create(registry.key(), id);
        FlexibleHolder<T, I> holder = new FlexibleHolder<>(resourceKey, object);
        dynamicEntries.put(name, (FlexibleHolder<T, T>) holder);

        return holder;
    }



    /**
     * 获取所有动态注册的条目
     * @return 动态注册的所有条目的集合
     */
    public Collection<FlexibleHolder<T, T>> getDynamicEntries() {
        return dynamicEntries.values();
    }
    
    /**
     * 根据名称获取动态注册的条目
     * @param name 条目的注册名称
     * @return 包含找到的条目的Optional，如果不存在则为空
     */
    public Optional<FlexibleHolder<T, T>> getDynamicEntry(String name) {
        return Optional.ofNullable(dynamicEntries.get(name));
    }
    
    /**
     * 根据条件过滤动态注册的条目
     * @param predicate 过滤条件
     * @return 符合条件的动态注册条目集合
     */
    public Collection<FlexibleHolder<T, T>> filterDynamicEntries(Predicate<FlexibleHolder<T, T>> predicate) {
        return dynamicEntries.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有静态注册的条目
     * @return 静态注册的所有条目的集合
     */
    public Collection<FlexibleHolder<T, T>> getStaticEntries() {
        return staticEntries.values();
    }
    
    /**
     * 根据名称获取静态注册的条目
     * @param name 条目的注册名称
     * @return 包含找到的条目的Optional，如果不存在则为空
     */
    public Optional<FlexibleHolder<T, T>> getStaticEntry(String name) {
        return Optional.ofNullable(staticEntries.get(name));
    }


    /**
     * 检查此注册表是否支持动态注册
     * @return 如果支持动态注册则返回true
     */
    public boolean supportsDynamicRegistration() {
        return supportsDynamic;
    }

    /**
     * 设置动态内容管理器
     * @param manager 动态内容管理器
     */
    public void setDynamicManager(AbstractDynamicLoader manager) {
        this.dynamicManager = manager;
        this.supportsDynamic = true;
    }

    
    /**
     * 检查指定名称的条目是否已经动态注册
     * @param name 要检查的条目名称
     * @return 如果条目已注册则返回true
     */
    public boolean isDynamicallyRegistered(String name) {
        return dynamicEntries.containsKey(name);
    }
    
    /**
     * 获取此注册表的模组ID
     * @return 模组ID
     */
    public String getModId() {
        return modId;
    }
    
    /**
     * 获取静态注册的条目数量
     * @return 静态条目数量
     */
    public int getStaticEntryCount() {
        return staticEntries.size();
    }
    
    /**
     * 获取动态注册的条目数量
     * @return 动态条目数量
     */
    public int getDynamicEntryCount() {
        return dynamicEntries.size();
    }
    
    /**
     * 获取所有注册条目的总数量
     * @return 条目总数量
     */
    public int getTotalEntryCount() {
        return getStaticEntryCount() + getDynamicEntryCount();
    }
}
