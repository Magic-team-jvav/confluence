package com.xiaohunao.xhn_lib.common.register;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 扩展的Holder实现，支持静态和动态注册的内容
 * 基于DeferredHolder但增加了对动态内容的支持
 */
public class FlexibleHolder<R, T extends R> implements Holder<R>, Supplier<T> {
    protected final ResourceKey<R> key;
    private @Nullable Holder<R> holder = null;
    private @Nullable T directValue = null;
    private final boolean isDynamic;
    private final Supplier<T> valueSupplier;

    /**
     * 创建静态注册的FlexibleHolder
     */
    public static <R, T extends R> FlexibleHolder<R, T> createStatic(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName, Supplier<T> supplier) {
        return new FlexibleHolder<>(ResourceKey.create(registryKey, valueName), supplier, false);
    }

    /**
     * 创建动态注册的FlexibleHolder
     */
    public static <R, T extends R> FlexibleHolder<R, T> createDynamic(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName) {
        // 使用空的Supplier和null值，值将在注册表绑定时获取
        return new FlexibleHolder<>(ResourceKey.create(registryKey, valueName), () -> null, true, null);
    }

    /**
     * 静态注册的构造函数
     */
    protected FlexibleHolder(ResourceKey<R> key, Supplier<T> supplier, boolean isDynamic) {
        this(key, supplier, isDynamic, null);
    }

    /**
     * 完整构造函数，支持静态和动态注册
     */
    protected FlexibleHolder(ResourceKey<R> key, Supplier<T> supplier, boolean isDynamic, @Nullable T directValue) {
        this.key = Objects.requireNonNull(key);
        this.valueSupplier = Objects.requireNonNull(supplier);
        this.isDynamic = isDynamic;
        this.directValue = directValue;
        this.bind(false);
    }

    /**
     * 获取持有的值
     */
    @Override
    public R value() {
        if (isDynamic && directValue != null) {
            return directValue;
        }
        
        this.bind(true);
        if (this.holder == null) {
            if (directValue != null) {
                return directValue;
            }
            return valueSupplier.get();
        } else {
            return Objects.requireNonNull(this.holder).value();
        }
    }

    @Override
    public T get() {
        @SuppressWarnings("unchecked")
        T result = (T) value();
        return result;
    }

    /**
     * 获取可选的值
     */
    public Optional<T> asOptional() {
        return this.isBound() || isDynamic ? Optional.of(this.get()) : Optional.empty();
    }

    /**
     * 获取关联的注册表
     */
    @SuppressWarnings("unchecked")
    protected @Nullable Registry<R> getRegistry() {
        // 根据DeferredHolder实现，这里应该使用BuiltInRegistries.REGISTRY
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(this.key.registry());
    }

    /**
     * 绑定到注册表中的实际值
     */
    protected final void bind(boolean throwOnMissingRegistry) {
        if (this.holder == null) {
            Registry<R> registry = this.getRegistry();
            if (registry != null) {
                this.holder = registry.getHolder(this.key).orElse(null);
            } else if (throwOnMissingRegistry && !isDynamic) {
                throw new IllegalStateException("Registry not present for " + this + ": " + this.key.registry());
            }
        }
    }

    /**
     * 判断是否是动态注册的
     */
    public boolean isDynamic() {
        return isDynamic;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof Holder<?> h) {
            if (h.kind() == Kind.REFERENCE && Objects.equals(h.getKey(), this.key)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    @Nonnull
    public String toString() {
        return String.format(Locale.ENGLISH, "FlexibleHolder{%s, dynamic=%s}", this.key, this.isDynamic);
    }

    /**
     * 检查是否已绑定
     */
    @Override
    public boolean isBound() {
        if (isDynamic && directValue != null) {
            return true;
        }
        
        this.bind(false);
        return this.holder != null && this.holder.isBound();
    }

    @Override
    public boolean is(@Nonnull ResourceLocation id) {
        return id.equals(this.key.location());
    }

    @Override
    public boolean is(@Nonnull ResourceKey<R> key) {
        return key.equals(this.key);
    }

    @Override
    public boolean is(@Nonnull Predicate<ResourceKey<R>> filter) {
        return filter.test(this.key);
    }

    @Override
    public boolean is(@Nonnull TagKey<R> tag) {
        this.bind(false);
        return this.holder != null && this.holder.is(tag);
    }

    @Override
    @Deprecated
    public boolean is(@Nonnull Holder<R> holder) {
        this.bind(false);
        return this.holder != null && this.holder.is(holder);
    }

    @Override
    public <Z> @Nullable Z getData(@Nonnull DataMapType<R, Z> type) {
        this.bind(false);
        return this.holder == null ? null : this.holder.getData(type);
    }

    @Override
    @Nonnull
    public Stream<TagKey<R>> tags() {
        this.bind(false);
        return this.holder != null ? Objects.requireNonNull(this.holder).tags() : Stream.empty();
    }

    @Nonnull
    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return Either.left(this.key);
    }

    @Nonnull
    @Override
    public Optional<ResourceKey<R>> unwrapKey() {
        return Optional.of(this.key);
    }

    @Nonnull
    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(@Nonnull HolderOwner<R> owner) {
        this.bind(false);
        return this.holder != null && this.holder.canSerializeIn(owner);
    }

    @Nonnull
    @Override
    public Holder<R> getDelegate() {
        this.bind(false);
        return this.holder != null ? this.holder.getDelegate() : this;
    }

    /**
     * 更新动态值
     * 仅适用于动态注册的持有者
     */
    public void updateDynamicValue(T newValue) {
        if (!isDynamic) {
            throw new IllegalStateException("Cannot update value for non-dynamic FlexibleHolder");
        }
        this.directValue = newValue;
    }
}
