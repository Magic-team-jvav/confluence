package com.xiaohunao.xhn_lib.common.register;

import com.xiaohunao.xhn_lib.api.AbstractDynamicLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 灵活的注册工具，支持静态注册和动态注册
 * <p>
 * 静态注册：在游戏启动时通过代码注册，类似于普通的内容注册
 * 动态注册：在游戏运行时通过资源包或代码动态注册，可以在不重启游戏的情况下更新内容
 * </p>
 *
 * @param <T> 要注册的内容类型
 */
public class FlexibleRegister<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlexibleRegister.class);

    // 存储动态注册的条目，键为名称，值为Holder
    private final Map<String, FlexibleHolder<T, ? extends T>> dynamicEntries = new HashMap<>();
    // 存储静态注册的条目，键为名称，值为Holder
    private final Map<String, FlexibleHolder<T, ? extends T>> staticEntries = new HashMap<>();

    // 模组ID
    private final String modId;
    // 注册表键
    private final ResourceKey<? extends Registry<T>> registryKey;
    // 用于动态内容加载的管理器
    private AbstractDynamicLoader<T> dynamicManager;
    // 是否支持动态注册
    private boolean supportsDynamic;


    // 状态跟踪
    private boolean registeredEventBus = false;
    private boolean seenRegisterEvent = false;
    private boolean seenNewRegistryEvent = false;



    public FlexibleRegister(Registry<T> registry, String modId, @Nullable AbstractDynamicLoader<T> dynamicManager ) {
        this.registryKey = Objects.requireNonNull(registry.key());
        this.modId = Objects.requireNonNull(modId);
        this.dynamicManager = dynamicManager;
        this.supportsDynamic = dynamicManager != null;

    }

    public FlexibleRegister(Registry<T> registry, String modId) {
        this(registry, modId, null);
    }



    public void register(IEventBus eventBus) {
        if (this.registeredEventBus)
            throw new IllegalStateException("Cannot register FlexibleRegister to more than one event bus.");
        this.registeredEventBus = true;

        eventBus.addListener(this::addEntries);
    }

    /**
     * 处理RegisterEvent事件，注册静态条目
     */
    private void addEntries(RegisterEvent event) {
        if (!event.getRegistryKey().equals(this.registryKey)) {
            return;
        }
        
        LOGGER.debug("Processing RegisterEvent for {} entries from mod {}", this.registryKey, this.modId);
        this.seenRegisterEvent = true;
        

        // 遍历静态条目，进行最终注册
        for (Map.Entry<String, FlexibleHolder<T, ? extends T>> entry : staticEntries.entrySet()) {
            FlexibleHolder<T, ? extends T> holder = entry.getValue();
            ResourceLocation entryId = holder.getKey().location();
            
            event.register(this.registryKey, entryId, holder::get);
            LOGGER.debug("Registered static entry: {}", entryId);
        }
    }

    /**
     * 静态注册一个条目
     *
     * @param name 条目名称
     * @param supplier 条目供应器
     * @return 注册的DeferredHolder
     */
    public <I extends T> FlexibleHolder<T, I> registerStatic(String name, Supplier<I> supplier) {
        if (seenRegisterEvent) {
            throw new IllegalStateException("Cannot register new entries to FlexibleRegister after RegisterEvent has been fired.");
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        FlexibleHolder<T, I> flexibleHolder = FlexibleHolder.createStatic(this.registryKey, id, supplier);
        staticEntries.put(name, flexibleHolder);
        return flexibleHolder;
    }

    public <I extends T> FlexibleHolder<T, I> registerDynamic(String name) {
        if (!supportsDynamic) {
            throw new IllegalStateException("This registry is not configured to support dynamic registration: " + this.registryKey);
        }

        if (dynamicManager == null) {
            throw new IllegalStateException("Attempted dynamic registration without a dynamic loader: " + this.registryKey);
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);

        FlexibleHolder<T, I> holder = FlexibleHolder.createDynamic(this.registryKey, id);
        dynamicEntries.put(name, holder);
        
        LOGGER.info("Prepared dynamic content placeholder: {}", id);
        
        return holder;
    }

    /**
     * 获取类型化的动态内容加载管理器
     */
    public AbstractDynamicLoader<T> getTypedDynamicManager() {
        return dynamicManager;
    }



    /**
     * 获取所有动态注册的条目
     */
    public Collection<FlexibleHolder<T, ? extends T>> getDynamicEntries() {
        return Collections.unmodifiableCollection(dynamicEntries.values());
    }

    /**
     * 根据名称获取动态注册的条目
     */
    public Optional<FlexibleHolder<T, ? extends T>> getDynamicEntry(String name) {
        return Optional.ofNullable(dynamicEntries.get(name));
    }

    /**
     * 根据谓词过滤动态条目
     */
    public Collection<FlexibleHolder<T, ? extends T>> filterDynamicEntries(Predicate<FlexibleHolder<T, ? extends T>> predicate) {
        return dynamicEntries.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有静态注册的条目
     */
    public Collection<FlexibleHolder<T, ? extends T>> getStaticEntries() {
        return Collections.unmodifiableCollection(staticEntries.values());
    }

    /**
     * 根据名称获取静态注册的条目
     */
    public Optional<FlexibleHolder<T, ? extends T>> getStaticEntry(String name) {
        return Optional.ofNullable(staticEntries.get(name));
    }

    /**
     * 获取所有注册的条目（静态+动态）
     */
    public Collection<FlexibleHolder<T, ? extends T>> getAllEntries() {
        List<FlexibleHolder<T, ? extends T>> allEntries = new ArrayList<>(staticEntries.size() + dynamicEntries.size());
        allEntries.addAll(staticEntries.values());
        allEntries.addAll(dynamicEntries.values());
        return Collections.unmodifiableCollection(allEntries);
    }

    /**
     * 检查是否支持动态注册
     */
    public boolean supportsDynamicRegistration() {
        return supportsDynamic;
    }

    /**
     * 检查指定名称的条目是否为动态注册
     */
    public boolean isDynamicallyRegistered(String name) {
        return dynamicEntries.containsKey(name);
    }

    /**
     * 获取模组ID
     */
    public String getModId() {
        return modId;
    }

    /**
     * 获取注册表键
     */
    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return registryKey;
    }
}
