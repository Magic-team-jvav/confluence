# XHN-Lib (小葫闹库)

XHN-Lib 是一个为 Minecraft NeoForge 模组开发设计的库，提供灵活的内容注册系统，支持静态和动态注册游戏内容。

## 主要特性

- **灵活的注册系统**：支持在游戏加载时（静态）和游戏运行时（动态）注册内容
- **与原版注册表集成**：无缝集成 Minecraft 原版注册表系统
- **资源重载支持**：通过 JSON 文件动态加载内容
- **简化模组开发**：减少模组开发中的重复代码和复杂性

## 安装

### Gradle

在你的 `build.gradle` 文件中添加以下依赖：

```gradle
dependencies {
    implementation 'com.xiaohunao:xhn_lib:版本号'
}
```

### Maven

```xml
<dependency>
    <groupId>com.xiaohunao</groupId>
    <artifactId>xhn_lib</artifactId>
    <version>版本号</version>
</dependency>
```

## 使用方法

### 基本用法

1. 创建一个 `FlexibleRegister` 实例：

```java
// 仅静态注册
FlexibleRegister<MyType> REGISTRY = new FlexibleRegister<>(
    MY_REGISTRY_KEY,
    "my_mod_id"
);

// 静态和动态注册
FlexibleRegister<MyType> REGISTRY = new FlexibleRegister<>(
    MY_REGISTRY_KEY,
    "my_mod_id",
    new MyDynamicContentManager()
);
```

2. 在模组初始化时注册到事件总线：

```java
public void onModConstruct(IEventBus modEventBus) {
    REGISTRY.register(modEventBus);
}
```

3. 静态注册内容：

```java
public static final DeferredHolder<MyType, MyType> MY_STATIC_ITEM = 
    REGISTRY.registerStatic("item_id", () -> new MyType());
```

4. 初始化动态注册系统：

```java
@SubscribeEvent
public void onCommonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
        REGISTRY.initializeDynamicRegistry(() -> getRegistry());
    });
}
```

5. 动态注册内容：

```java
FlexibleHolder<MyType, MyType> dynamicItem = 
    REGISTRY.registerDynamic("dynamic_item_id", new MyType());
```

### 完整示例

请查看 `src/main/java/com/xiaohunao/xhn_lib/common/init/examples/FlexibleRegistryExample.java` 获取完整的使用示例。

## API 文档

### FlexibleRegister

核心类，用于管理静态和动态注册。

```java
// 构造函数
FlexibleRegister(ResourceKey<? extends Registry<T>> registryKey, String modId)
FlexibleRegister(ResourceKey<? extends Registry<T>> registryKey, String modId, SimpleJsonResourceReloadListener dynamicManager)

// 主要方法
void register(IEventBus eventBus)
<I extends T> DeferredHolder<T, I> registerStatic(String name, Supplier<I> supplier)
<I extends T> FlexibleHolder<T, I> registerDynamic(String name, I object)
void initializeDynamicRegistry(Supplier<Registry<T>> registrySupplier)
Collection<FlexibleHolder<T, T>> getDynamicEntries()
Optional<FlexibleHolder<T, T>> getDynamicEntry(String name)
Collection<FlexibleHolder<T, T>> filterDynamicEntries(Predicate<FlexibleHolder<T, T>> predicate)
boolean isDynamicallyRegistered(String name)
```

### FlexibleHolder

扩展了 NeoForge 的 `DeferredHolder`，支持直接访问动态注册的对象。

```java
// 构造函数
FlexibleHolder(ResourceKey<R> key, T value)

// 主要方法
T value()
T get()
boolean isBound()
T getDirectValue()
```

## 许可证

[MIT 许可证](LICENSE)

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

- GitHub: [项目GitHub地址]
- Discord: [Discord服务器链接]
